/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.entityhub.yard.solr.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.solr.analysis.ICUTokenizerFactory;
import org.apache.solr.analysis.TokenizerFactory;
import org.apache.stanbol.commons.solr.utils.SolrUtil;
import org.apache.stanbol.entityhub.yard.solr.defaults.IndexDataTypeEnum;
import org.apache.stanbol.entityhub.yard.solr.model.IndexValue;
import org.apache.stanbol.entityhub.yard.solr.model.IndexValueFactory;

public final class QueryUtils {
    private QueryUtils() {}
    /**
     * The {@link TokenizerFactory} used to create Tokens for parsed 
     * {@link IndexValue#getValue()} in case <code>false</code> is parsed for
     * the tokenize property of {@link #encodeQueryValue(IndexValue, boolean)}.
     * <p>
     * Currently the {@link ICUTokenizerFactory} is used for Tokenizing.
     */
    private final static TokenizerFactory tokenizerFactory = new ICUTokenizerFactory();
    /**
     * Regex patter that searches for Wildcard chars '*' and '?' excluding
     * escaped versions '\*' and '\?'
     */
    private final static Pattern wILDCARD_QUERY_CHAR_PATTERN = Pattern.compile("[^\\\\][\\*\\?]");
    
    /**
     * This method encodes a parsed index value as needed for queries.
     * <p>
     * In case of TXT it is assumed that a whitespace tokenizer is used by the index. Therefore values with
     * multiple words need to be treated and connected with AND to find only values that contain all. In case
     * of STR no whitespace is assumed. Therefore spaces need to be replaced with '+' to search for tokens
     * with the exact name. In all other cases the string need not to be converted.
     * 
     * <del>Note also that text queries are converted to lower case</del>
     * Note: since 2012-03-14 parsed values are only converted to lower case.
     * <p>
     * <b>TODO:</b> Until Solr 3.6 is released and the implementation of
     * <a href="https://issues.apache.org/jira/browse/">SOLR-2438</a> is
     * released this needs to still convert wildcard queries to lower case.<br>
     * Because of that:<ul>
     * <li> in case <code>escape=true</code>. Non-wildcard queries should support
     * case sensitive searches. If the searched solr field uses a lowerCase
     * filter than this will be done by Solr anyway and if not that case
     * sensitivity might be important!
     * <li> for <code>escape=false</code> - wild card searches the values are
     * still converted to lower case to keep compatible with previous versions.
     * TODO: the caseSensitive parameter of TextConstraints should be used
     * instead
     * </ul>
     * 
     * @param value
     *            the index value
     * @param escape if <code>true</code> all Solr special chars are escaped if
     * <code>false</code> than '*' and '?' as used for wildcard searches are
     * not escaped.
     * @return the (possible multiple) values that need to be connected with AND
     */
    public static String[] encodeQueryValue(IndexValue indexValue, boolean escape) {
        if (indexValue == null) {
            return null;
        }
        String[] queryConstraints;
        String value = indexValue.getValue(); 
        if (escape) {
            value = SolrUtil.escapeSolrSpecialChars(value);
        } else {
            value = SolrUtil.escapeWildCardString(value);
        }
        if (IndexDataTypeEnum.TXT.getIndexType().equals(indexValue.getType())) {
            if(escape) { 
                //value does not contain '*' and '?' as they would be escaped.
                queryConstraints = new String[] {
                    new StringBuilder(value).insert(0, '"').append('"').toString()
                };
            } else { //non escaped strings might contain wildcard chars '*', '?'
                //those need to be treated specially (STANBOL-607)
                //Change to 2nd param to false after switching to Solr 3.6+ (see SOLR-2438)
                queryConstraints = parseWildcardQueryTerms(value, true);
            }
        } else if (IndexDataTypeEnum.STR.getIndexType().equals(indexValue.getType())) {
            if(escape){ 
                 //rw: 20120314: respect case sensitivity for escaped (non wildcard)
                queryConstraints = new String[] { value.indexOf(' ')>=0 ?
                        '"'+value+'"' : value
                };
            } else { //encode non
                //rw: 20120314: respect case sensitivity for escaped (non wildcard)
                //Change to 2nd param to false after switching to Solr 3.6+ (see SOLR-2438)
                queryConstraints = parseWildcardQueryTerms(value, true);
            }
        } else {
            queryConstraints = new String[] {value};
        }
        return queryConstraints;
    }
    
    /**
     * Utility Method that extracts IndexValues form an parsed {@link Object}.
     * This checks for {@link IndexValue}, {@link Iterable}s and values
     * @param indexValueFactory The indexValueFactory used to create indexValues if necessary
     * @param value the value to parse
     * @return A set with the parsed values. The returned Set is guaranteed 
     * not to be <code>null</code> and contains at least a single element. 
     * If no IndexValue could be parsed from the parsed value than a set containing
     * the <code>null</code> value is returned.
     */
    public static Set<IndexValue> parseIndexValues(IndexValueFactory indexValueFactory,Object value) {
        Set<IndexValue> indexValues;
        if (value == null) {
            indexValues = Collections.singleton(null);
        } else if (value instanceof IndexValue) {
            indexValues = Collections.singleton((IndexValue) value);
        } else if (value instanceof Iterable<?>){
            indexValues = new HashSet<IndexValue>();
            for(Object o : (Iterable<?>) value){
                if(o instanceof IndexValue){
                    indexValues.add((IndexValue)o);
                } else if (o != null){
                    indexValues.add(indexValueFactory.createIndexValue(o));
                }
            }
            if(indexValues.isEmpty()){
                indexValues.add(null); //add null element instead of an empty set
            }
        } else {
            indexValues = Collections.singleton(indexValueFactory.createIndexValue(value));
        }
        return indexValues;
    }
    
    public static void main(String[] args) throws IOException {
        String value = "This is a te?t for multi* Toke? Wildc\\*adrd Se?rche*";
        System.out.println(Arrays.toString(parseWildcardQueryTerms(value,true)));
    }

    /**
     * Parses query terms for Wildcard queries as described in the first
     * comment of STANBOL-607. <p>
     * As an example the String:
     * <code><pre>
     *     "This is a te?t for multi* Toke? Wildc\*adrd Se?rche*
     * </pre></code>
     * is converted in the query terms
     * <code><pre>
     *     ["This is a","te?t","multi*","toke?","Wildc\*adrd","se?rche*"]
     * </pre></code>
     * NOTE: that tokens that include are converted to lower case
     * @param value the value
     * @param loewercaseWildcardTokens if query elements that include a wildcard
     * should be converted to lower case.
     * @return the query terms
     * @throws IOException
     */
    private static String[] parseWildcardQueryTerms(String value,boolean loewercaseWildcardTokens) {
        //This assumes that the Tokenizer does tokenize '*' and '?',
        //what makes it a little bit tricky. 
        Tokenizer tokenizer = tokenizerFactory.create(new StringReader(value));
        Matcher m = wILDCARD_QUERY_CHAR_PATTERN.matcher(value);
        int next = m.find()?m.start()+1:-1;
        if(next < 0){ //No wildcard
            return new String[]{'"'+value+'"'};
        } 
        ArrayList<String> queryElements = new ArrayList<String>(5);
        int lastAdded = -1;
        int lastOffset = 0;
        boolean foundWildcard = false;
        //Lucene tokenizer are really low level ...
        try {
            while(tokenizer.incrementToken()){
                //only interested in the start/end indexes of tokens
                OffsetAttribute offset = tokenizer.addAttribute(OffsetAttribute.class);
                if(lastAdded < 0){ //rest with this token
                    lastAdded = offset.startOffset();
                }
                if(foundWildcard){ //wildcard present in the current token
                    //two cases: "wildcar? at the end", "wild?ard within the word"
                    // (1) [wildcar,at,the,end] : In this case this is called with
                    //      'at' as active Token and we need write "wildcar?" as
                    //      query term
                    // (2) [wild,ard,within,the,word]: In this case this is called with
                    //      'ard' as active Token and we need write "wild?ard" as
                    //      query term.
                    if(offset.startOffset() > lastOffset+1) {//(1)
                        String queryElement = value.substring(lastAdded,lastOffset+1);
                        if(loewercaseWildcardTokens){
                            queryElement = queryElement.toLowerCase();
                        }
                        
                        queryElements.add(queryElement);
                        lastAdded = offset.startOffset(); //previous token consumed
                        //set to the start of the current token
                        foundWildcard = false;
                    } else if(next != offset.endOffset()){ //(2)
                        String queryElement = value.substring(lastAdded,offset.endOffset());
                        if(loewercaseWildcardTokens){
                            queryElement = queryElement.toLowerCase();
                        }
                        queryElements.add(queryElement);
                        lastAdded = -1; //consume the current token
                        foundWildcard = false;
                    }
                }
                if(next == offset.endOffset()){ //end of current token is '*' or '?'
                    next = m.find()?m.start()+1:-1; //search next '*', '?' in value
                    //we need to write all tokens previous to the current (if any)
                    //NOTE: ignore if foundWildcard is TRUE (multiple wildcards in
                    //      a single word
                    if(!foundWildcard && lastAdded<lastOffset){
                        String queryElement = value.substring(lastAdded,lastOffset);
                        queryElements.add('"'+queryElement+'"');
                        lastAdded = offset.startOffset();
                    }//else multiple wildcards in a single token
                    foundWildcard = true;
                }
                lastOffset = offset.endOffset();
            }
        } catch (IOException e) {
            //StringReader can not throw IOExceptions
            throw new IllegalStateException(e);
        }
        if(lastAdded >= 0 && lastAdded < value.length()){
            String queryElement = value.substring(lastAdded,value.length());
            if(foundWildcard && loewercaseWildcardTokens){
                queryElement = queryElement.toLowerCase();
            }
            if(foundWildcard){
                queryElements.add(queryElement);
            } else {
                queryElements.add('"'+queryElement+'"');
            }
        }
        return queryElements.toArray(new String[queryElements.size()]);
    }


}
