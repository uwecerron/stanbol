package org.apache.stanbol.entityhub.ldpath.backend;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.stanbol.entityhub.core.mapping.ValueConverterFactory;
import org.apache.stanbol.entityhub.servicesapi.EntityhubException;
import org.apache.stanbol.entityhub.servicesapi.defaults.DataTypeEnum;
import org.apache.stanbol.entityhub.servicesapi.model.Reference;
import org.apache.stanbol.entityhub.servicesapi.model.Representation;
import org.apache.stanbol.entityhub.servicesapi.model.Text;
import org.apache.stanbol.entityhub.servicesapi.model.ValueFactory;
import org.apache.stanbol.entityhub.servicesapi.query.FieldQuery;
import org.apache.stanbol.entityhub.servicesapi.query.QueryResultList;
import org.apache.stanbol.entityhub.servicesapi.query.ReferenceConstraint;
import org.apache.stanbol.entityhub.servicesapi.query.TextConstraint;
import org.apache.stanbol.entityhub.servicesapi.query.ValueConstraint;
import org.apache.stanbol.entityhub.servicesapi.query.TextConstraint.PatternType;
import org.apache.stanbol.entityhub.servicesapi.util.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.newmedialab.ldpath.api.backend.RDFBackend;
/**
 * Abstract super class for all Entityhub related {@link RDFBackend}
 * implementations. This implements the whole {@link RDFBackend} interface by
 * forwarding requests to the abstract methods<ul>
 * <li> {@link #getRepresentation(String)}: Lookup of Representations by ID
 * <li> {@link #query(FieldQuery)}: Used to query entities for property value
 * pairs.
 * </ul>
 * In addition two further methods are defined to create {@link FieldQuery field queries} and
 * o lookup the {@link ValueFactory} instance needed to create URIs and
 * Literals.
 * @author Rupert Westenthaler
 *
 */
abstract class AbstractBackend implements RDFBackend<Object> {
    
    public static final int DEFAULT_MAX_SELECT = 1000; //select a maximum of 1000 values per query
    public static final int DEFAULT_MAX_RESULTS = 100000; //select a maximum of 100k entities

    private static final int LRU_CACHE_SIZE = 100;
    
    @SuppressWarnings("serial")
    private final java.util.LinkedHashMap<String,Representation> lru = 
        new LinkedHashMap<String,Representation>(LRU_CACHE_SIZE+1, 0.75f, true){
        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<String,Representation> eldest) {
            return size() > LRU_CACHE_SIZE;
        }
    };
    /**
     * EnumMap to avoid instantiations of URIs for the limited set of
     * DataTypes
     */
    private static final Map<DataTypeEnum,URI> dataTypeURIs;
    static {
        Map<DataTypeEnum,URI> uris = new EnumMap<DataTypeEnum,URI>(DataTypeEnum.class);
        for(DataTypeEnum type : DataTypeEnum.values()){
            uris.put(type, URI.create(type.getUri()));
        }
        dataTypeURIs = Collections.unmodifiableMap(uris);
    }
    
    private final Logger log = LoggerFactory.getLogger(YardBackend.class);
    
    protected final ValueConverterFactory valueConverter;
    
    public AbstractBackend() {
        this(null);
    }
    public AbstractBackend(ValueConverterFactory valueConverter) {
        if(valueConverter == null){
            this.valueConverter = ValueConverterFactory.getDefaultInstance();
        } else {
            this.valueConverter = valueConverter;
        }
    }    protected abstract ValueFactory getValueFactory();
    
    protected abstract Representation getRepresentation(String id) throws EntityhubException;
    
    protected abstract QueryResultList<String> query(FieldQuery query) throws EntityhubException;

    protected abstract FieldQuery createQuery();

    @Override
    public String createLiteral(String content) {
        return content;
    }

    @Override
    public Object createLiteral(String content, Locale language, URI type) {
        DataTypeEnum dataType = type == null ? null : DataTypeEnum.getDataType(type.toString());
        if(language == null && type == null){
            return content;
        }
        if(language != null){
            if(type != null && !(DataTypeEnum.String == dataType || DataTypeEnum.Text == dataType)){
                throw new IllegalArgumentException("Literals with a Lanugage MUST not have NULL,"+
                    DataTypeEnum.String.getShortName()+" or "+
                    DataTypeEnum.Text.getShortName()+" assigned as type!");
            } else {
                return getValueFactory().createText(content, language.getLanguage());
            }
        } else if(type != null){ //create a typed literal
            if(dataType == null){ //the parsed type is an unknown data type
                return content; //return an string
            } else {
                Object converted = valueConverter.convert(content, dataType.getUri(), getValueFactory());
                if(converted == null){
                    log.debug("Unable to convert content '{}' to dataType '{}'",converted,dataType);
                    return content;
                } else {
                    return converted;
                }
            }
        } else { //language is null and type is null
            return content;
        }
    }

    @Override
    public Object createURI(String uri) {
        return getValueFactory().createReference(uri);
    }

    @Override
    public Locale getLiteralLanguage(Object n) {
        String language;
        try {
            language = ((Text)n).getLanguage();
        } catch (ClassCastException e) {
           return null;
        }
        if(language == null){
            return null;
        } else {
            //TODO check if Locales should be create created like that
            return new Locale(language);
        }
    }

    @Override
    public URI getLiteralType(Object n) {
        if(n == null){
            return null;
        } else {
            Set<DataTypeEnum> types = DataTypeEnum.getPrimaryDataTypes(n.getClass());
            if(types == null || types.isEmpty()){
                return dataTypeURIs.get(DataTypeEnum.String);
            } else {
                return dataTypeURIs.get(types.iterator().next());
            }
        }
    }

    @Override
    public boolean isBlank(Object n) {
        //The entityhub does not use blank nodes
        return false;
    }

    @Override
    public boolean isLiteral(Object n) {
        return !(n instanceof Reference);
    }

    @Override
    public boolean isURI(Object n) {
        return n instanceof Reference;
    }

    @Override
    public Collection<Object> listObjects(Object subject, Object property) {
        Collection<Object> results;
        if(subject == null){
            results =  Collections.emptySet();
        } else {
            //Here the assumption is the the LD Path program will request
            //a lot of properties for a very low numbers of Entities
            // .. there fore we keep here representations within an LRU cache 
            Representation r = lru.get(subject.toString());
            if(r == null){
                try {
                    r = getRepresentation(subject.toString());
                } catch (EntityhubException e) {
                    throw new IllegalStateException(e.getMessage(),e);
                }
                if(r != null){
                    lru.put(subject.toString(), r);
                }
            }
            if(r != null){
                if(property != null){
                    results = ModelUtils.asCollection(r.get(property.toString()));
                } else {
                    results = new LinkedHashSet<Object>();
                    for(Iterator<String> properties = r.getFieldNames();properties.hasNext();){
                        results.addAll(ModelUtils.addToCollection(r.get(properties.next()), results));
                    }
                }
            } else {
                results = Collections.emptyList();
            }
        }
        return results;
    }

    @Override
    public Collection<Object> listSubjects(Object property, Object object) {
        FieldQuery query = createQuery();
        if(this.isURI(object)){
            query.setConstraint(property.toString(), new ReferenceConstraint(object.toString()));
        } else if(object instanceof Text){
            Text text = (Text)object;
            TextConstraint constraint;
            if(text.getLanguage() == null){
                constraint = new TextConstraint(text.getText(), PatternType.none, true);
            } else {
                constraint = new TextConstraint(text.getText(), PatternType.none, true,text.getLanguage());
            }
            query.setConstraint(property.toString(), constraint);
        } else {
            Set<DataTypeEnum> dataTypes = DataTypeEnum.getPrimaryDataTypes(object.getClass());
            if(dataTypes == null || dataTypes.isEmpty()){
                query.setConstraint(property.toString(), 
                    new ValueConstraint(object));
            } else {
                Collection<String> types = new ArrayList<String>(dataTypes.size());
                for(DataTypeEnum type : dataTypes){
                    types.add(type.getUri());
                }
                query.setConstraint(property.toString(), 
                    new ValueConstraint(object,types));
            }
        }
        query.setLimit(Integer.valueOf(DEFAULT_MAX_SELECT)); 
        QueryResultList<String> results;
        try {
            results = query(query);
            
        } catch (EntityhubException  e) {
            throw new IllegalStateException("Unable to query for resources with value '"+
                object+"' on property '"+property+"'!",e);
        }
        Collection<Object> references;
        if(results.isEmpty()){
            references = Collections.emptySet();
        } else if(results.size() == 1){ //assuming that a single result is a likely case
            references = Collections.singleton(
                (Object)getValueFactory().createReference(results.iterator().next()));
        } else {
            int offset = 0;
            references = new HashSet<Object>(results.size());
            for(String result : results){
                references.add(getValueFactory().createReference(result));
            }
            while(results.size() >= DEFAULT_MAX_SELECT && references.size() <= DEFAULT_MAX_RESULTS-DEFAULT_MAX_SELECT){
                offset = offset + results.size();
                query.setOffset(offset);
                try {
                    results = query(query);
                } catch (EntityhubException e) {
                    throw new IllegalStateException("Unable to query for resources with value '"+
                        object+"' on property '"+property+"'!",e);
                }
                for(String result : results){
                    references.add(getValueFactory().createReference(result));
                }
            }
        }
        return references;
    }
    @Override
    public String stringValue(Object node) {
        //The Entityhub requires that the toString method returns the lexical form
        return node == null ? null : 
            node instanceof Text ? ((Text)node).getText() : node.toString();
    }
    @Override
    public Boolean booleanValue(Object node) {
        return convert(DataTypeEnum.Boolean, node);
    }
    @Override
    public BigInteger integerValue(Object node) {
        return convert(DataTypeEnum.Integer, node);
    }
    @Override
    public Integer intValue(Object node) {
        return convert(DataTypeEnum.Int, node);
    }
    @Override
    public Long longValue(Object node) {
        return convert(DataTypeEnum.Long, node);
    }
    @Override
    public Float floatValue(Object node) {
        return convert(DataTypeEnum.Float, node);
    }
    @Override
    public Double doubleValue(Object node) {
        return convert(DataTypeEnum.Double, node);
    }
    @Override
    public BigDecimal decimalValue(Object node) {
        return convert(DataTypeEnum.Decimal, node);
    }
    @Override
    public Date dateTimeValue(Object node) {
        return convert(DataTypeEnum.DateTime, node);
    }
    @Override
    public Date dateValue(Object node) {
        return convert(DataTypeEnum.Date, node);
    }
    @Override
    public Date timeValue(Object node) {
        return convert(DataTypeEnum.Time, node);
    }
    /**
     * Internal utility to save a lot of code lines for checking on <code>null</code>
     * for conversions and throwing an {@link IllegalArgumentException} is so.
     * @param <T> the generic return type
     * @param type the dataType
     * @param vf the valueFactory
     * @param value the value to convert
     * @return the converted value
     */
    @SuppressWarnings("unchecked")
    private <T> T convert(DataTypeEnum type, Object value){
        T converted;
        Object convertedObject = valueConverter.convert(value, type.getUri(), getValueFactory());
        try {
            converted = (T)convertedObject;
        } catch (ClassCastException e) {
            //this is something unexpected ... fail cleanly
            throw new IllegalStateException("Convert value for Node'"+
                value+"' has not the expected java type "+type.getJavaType()+
                "(for type '"+type.getShortName()+"') but '"+convertedObject.getClass()+"'!");
        }
        if(converted == null){
            throw new IllegalArgumentException("Unable to convert value '"+
                value+"' to dataType '"+type.getShortName()+"' (java: "+
                type.getJavaType()+")!");
        }
        return converted;
    }
}