/* Generated By:JavaCC: Do not edit this line. KReSRuleParser.java */
package eu.iksproject.kres.rules.parser;

import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;


import eu.iksproject.kres.api.rules.KReSRule;
import eu.iksproject.kres.api.rules.KReSRuleAtom;
import eu.iksproject.kres.api.rules.URIResource;
import eu.iksproject.kres.api.rules.util.AtomList;
import eu.iksproject.kres.api.rules.util.KReSRuleList;
import eu.iksproject.kres.rules.KReSKB;
import eu.iksproject.kres.rules.KReSRuleImpl;
import eu.iksproject.kres.rules.atoms.ClassAtom;
import eu.iksproject.kres.rules.atoms.DatavaluedPropertyAtom;
import eu.iksproject.kres.rules.atoms.IndividualPropertyAtom;
import eu.iksproject.kres.rules.atoms.KReSResource;
import eu.iksproject.kres.rules.atoms.KReSVariable;

/**
 * 
 * @author andrea.nuzzolese
 *
 */

public class KReSRuleParser implements KReSRuleParserConstants {

  static KReSKB kReSKB;

  /**
   * Parses the {@link String} containing the rule(s) in KReSRule syntax.
   * @param inString {@link String}
   * @return the KReS Knowledge Base - {@link KReSKB}
   */
  public static KReSKB parse( String inString ) {
  {
        kReSKB = new KReSKB();
        Reader reader = new StringReader( inString ) ;
    KReSRuleParser parser = new KReSRuleParser(reader);
    StringBuffer buffer = new StringBuffer() ;
        try {
                parser.start( ) ;
        } catch( TokenMgrError e ) {
                throw new IllegalStateException(e) ;
        } catch( ParseException e ) {
                throw new IllegalStateException(e) ;
        }
        return kReSKB ; }
  }


        private static URI getSWRLArgument(String argument){
                Resource rdfNode = null;
                String[] argumentComposition = argument.split(":");
                if(argumentComposition.length == 2){
                        String prefix = argumentComposition[0];
                        String resourceName = argumentComposition[1];

                        String namespaceURI = kReSKB.getPrefixURI(prefix);
                        rdfNode = ModelFactory.createDefaultModel().createResource(namespaceURI+resourceName);
                        try {
                                                        return new URI(rdfNode.getURI());
                                                } catch (URISyntaxException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                }

                }

                return null;
        }

        private static URI getSWRLVariable(String argument){
                Resource variableResource = null;
                String variableString = argument.substring(1);


                variableResource = ModelFactory.createDefaultModel().createResource(kReSKB.getPrefixURI("var")+variableString);



                try {
                                        return new URI(variableResource.getURI());
                                } catch (URISyntaxException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        return null;
                                }
        }

  final public void start() throws ParseException {
    expression();
    expressionCont();
  }

  final public void expressionCont() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AND:
      jj_consume_token(AND);
      expression();
      break;
    default:
      jj_la1[0] = jj_gen;

    }
  }

  final public void expression() throws ParseException {
 KReSRule kReSRule;
    prefix();
    expressionCont();
  }

  final public void prefix() throws ParseException {
 String nsPrefix; Object obj;
    nsPrefix = getVariable();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EQUAL:
      obj = equality();
                                           String prefixURI = (String)obj;
                                                                                   prefixURI = prefixURI.substring(1, prefixURI.length()-1);
                                                                                   kReSKB.addPrefix(nsPrefix, prefixURI);
      break;
    case LQUAD:
      obj = rule();
                                                                         AtomList[] atoms = (AtomList[]) obj;
                                                                                         String varPrefix = kReSKB.getPrefixURI("var");
                                                                         				 varPrefix = varPrefix.substring(0, varPrefix.length());
                                                                                         KReSRule kReSRule = new KReSRuleImpl(varPrefix+nsPrefix, atoms[0], atoms[1]);
                                                                                         kReSKB.addRule(kReSRule);
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public String equality() throws ParseException {
 String nsURI;
    jj_consume_token(EQUAL);
    nsURI = getURI();
          {if (true) return nsURI;}
    throw new Error("Missing return statement in function");
  }

  final public AtomList[] rule() throws ParseException {
 AtomList[] ruleAtoms;
    jj_consume_token(LQUAD);
    ruleAtoms = ruleDefinition();
    jj_consume_token(RQUAD);
     {if (true) return ruleAtoms;}
    throw new Error("Missing return statement in function");
  }

  final public AtomList[] ruleDefinition() throws ParseException {
 AtomList body; AtomList head;
    body = atomList();
    jj_consume_token(LARROW);
    head = atomList();
          {if (true) return new AtomList[]{body, head};}
    throw new Error("Missing return statement in function");
  }

  final public AtomList atomList() throws ParseException {
 AtomList atomList = new AtomList(); KReSRuleAtom kReSAtom;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SAME:
    case DIFFERENT:
    case IS:
    case HAS:
    case RANGE:
    case VALUES:
      kReSAtom = atom();
      atomList = atomListRest();
          atomList.add(kReSAtom); {if (true) return atomList;}
      break;
    default:
      jj_la1[2] = jj_gen;

         {if (true) return atomList;}
    }
    throw new Error("Missing return statement in function");
  }

  final public AtomList atomListRest() throws ParseException {
 AtomList atomList = new AtomList(); KReSRuleAtom kReSAtom;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AND:
      jj_consume_token(AND);
      atomList = atomList();
          {if (true) return atomList;}
      break;
    default:
      jj_la1[3] = jj_gen;

         {if (true) return atomList;}
    }
    throw new Error("Missing return statement in function");
  }

  final public KReSRuleAtom atom() throws ParseException {
 KReSRuleAtom kReSRuleAtom;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IS:
      kReSRuleAtom = classAtom();
                             {if (true) return kReSRuleAtom;}
      break;
    case RANGE:
      dataRangeAtom();
      break;
    case HAS:
      kReSRuleAtom = individualPropertyAtom();
                                          {if (true) return kReSRuleAtom;}
      break;
    case VALUES:
      kReSRuleAtom = datavaluedPropertyAtom();
                                          {if (true) return kReSRuleAtom;}
      break;
    case SAME:
      sameAsAtom();
      break;
    case DIFFERENT:
      differentFromAtom();
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public ClassAtom classAtom() throws ParseException {
 URIResource uri1; URIResource uri2;
    jj_consume_token(IS);
    jj_consume_token(LPAR);
    uri1 = iObject();
    jj_consume_token(COMMA);
    uri2 = iObject();
    jj_consume_token(RPAR);
          {if (true) return new ClassAtom(uri1, uri2);}
    throw new Error("Missing return statement in function");
  }

  final public void dataRangeAtom() throws ParseException {
    jj_consume_token(RANGE);
    jj_consume_token(LPAR);
    iObject();
    jj_consume_token(COMMA);
    dObject();
    jj_consume_token(RPAR);
  }

  final public IndividualPropertyAtom individualPropertyAtom() throws ParseException {
 URIResource uri1; URIResource uri2; URIResource uri3;
    jj_consume_token(HAS);
    jj_consume_token(LPAR);
    uri1 = iObject();
    jj_consume_token(COMMA);
    uri2 = iObject();
    jj_consume_token(COMMA);
    uri3 = iObject();
    jj_consume_token(RPAR);
          {if (true) return new IndividualPropertyAtom(uri1, uri2, uri3);}
    throw new Error("Missing return statement in function");
  }

  final public DatavaluedPropertyAtom datavaluedPropertyAtom() throws ParseException {
 URIResource uri1; URIResource uri2; Object obj;
    jj_consume_token(VALUES);
    jj_consume_token(LPAR);
    uri1 = iObject();
    jj_consume_token(COMMA);
    uri2 = iObject();
    jj_consume_token(COMMA);
    obj = dObject();
    jj_consume_token(RPAR);
           {if (true) return new DatavaluedPropertyAtom(uri1, uri2, obj);}
    throw new Error("Missing return statement in function");
  }

  final public void sameAsAtom() throws ParseException {
    jj_consume_token(SAME);
    jj_consume_token(LPAR);
    iObject();
    jj_consume_token(COMMA);
    iObject();
    jj_consume_token(RPAR);
  }

  final public void differentFromAtom() throws ParseException {
    jj_consume_token(DIFFERENT);
    jj_consume_token(LPAR);
    iObject();
    jj_consume_token(COMMA);
    iObject();
    jj_consume_token(RPAR);
  }

  final public URIResource reference() throws ParseException {
  String uri1;
  Token colon;
  String uri3;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case URI:
      uri1 = getURI();
                         uri1 = uri1.substring(1, uri1.length()-1);
                                                try {
                                                  {if (true) return new KReSResource(new URI(uri1));}
                                                        } catch (URISyntaxException e) {
                                                                e.printStackTrace();
                                                        }
      break;
    case VAR:
      uri1 = getVariable();
      colon = jj_consume_token(COLON);
      uri3 = getVariable();
                                                                  {if (true) return new KReSResource(getSWRLArgument(uri1+colon.image+uri3));}
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public URIResource varReference() throws ParseException {
  String uri1;
  Token colon;
  String uri3;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case URI:
      uri1 = getURI();
                         try {
                                                                                                                        {if (true) return new KReSResource(new URI(uri1));}
                                                                                                                } catch (URISyntaxException e) {
                                                                                                                        e.printStackTrace();
                                                                                                                }
      break;
    case VAR:
      uri1 = getVariable();
      colon = jj_consume_token(COLON);
      uri3 = getVariable();
                                                                  {if (true) return new KReSResource(getSWRLArgument(uri1+colon.image+uri3));}
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String getURI() throws ParseException {
        Token t;
    t = jj_consume_token(URI);
                      {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  final public String getVariable() throws ParseException {
        Token t;
    t = jj_consume_token(VAR);
                      {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  final public String getString() throws ParseException {
        String t;
    jj_consume_token(DQUOT);
    t = getVariable();
    jj_consume_token(DQUOT);
                                               {if (true) return t;}
    throw new Error("Missing return statement in function");
  }

  final public Integer getInt() throws ParseException {
        Token t;
    t = jj_consume_token(NUM);
                    {if (true) return Integer.valueOf(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public URIResource iObject() throws ParseException {
  URIResource uri;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case VARIABLE:
      uri = variable();
                          {if (true) return uri;}
      break;
    case VAR:
    case URI:
      uri = reference();
                                                            {if (true) return uri;}
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Object dObject() throws ParseException {
  Object variable;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DQUOT:
      variable = getString();
      break;
    case NUM:
      variable = getInt();
      break;
    case VARIABLE:
    	variable = variable();
                            {if (true) return variable;}
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                                                    {if (true) return variable;}
    throw new Error("Missing return statement in function");
  }

  final public URIResource variable() throws ParseException {
  Token t;
    t = jj_consume_token(VARIABLE);
          String var=t.image; var=kReSKB.getPrefixURI("var") + var.substring(1);
                                                                                                                try{
                                                                                                                        {if (true) return new KReSVariable(new URI(var));}
                                                                                                                } catch (URISyntaxException e) {
                                                                                                                        e.printStackTrace();
                                                                                                                        {if (true) return null;}
                                                                                                                }
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public KReSRuleParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[9];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x100,0x80080,0xfc00,0x100,0xfc00,0x1400000,0x1400000,0x1c00000,0x240000,};
   }

  /** Constructor with InputStream. */
  public KReSRuleParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public KReSRuleParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new KReSRuleParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public KReSRuleParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new KReSRuleParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public KReSRuleParser(KReSRuleParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(KReSRuleParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[25];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 9; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 25; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  public static void main(String[] args){
	  String rule = "values(<http://dbpedia.org/ontology/role>, ?x, ?y) -> values(<http://rdf.data-vocabulary.org/#role>, ?x, ?y) . " +
	  		"values(<http://xmlns.com/foaf/0.1/homepagenick>, ?x, ?y) -> values(<http://rdf.data-vocabulary.org/#nickname>, ?x, ?y) . " +
	  		"has(<http://xmlns.com/foaf/0.1/homepagehomepage>, ?x, ?y) -> has(<http://rdf.data-vocabulary.org/#url>, ?x, ?y) . " +
	  		"values(<http://dbpedia.org/ontology/address>, ?x, ?y) -> values(<http://rdf.data-vocabulary.org/#address>, ?x, ?y). " +
	  		"values(<http://xmlns.com/foaf/0.1/homepagename>, ?x, ?y) -> values(<http://rdf.data-vocabulary.org/#name>, ?x, ?y) . " +
	  		"has(<http://dbpedia.org/ontology/thumbnail>, ?x, ?y) -> has(<http://rdf.data-vocabulary.org/#photo>, ?x, ?y) . " +
	  		"is(<http://dbpedia.org/ontology/Person>, ?x) -> is(<http://rdf.data-vocabulary.org/#Person>, ?x) . " +
	  		"has(<http://dbpedia.org/ontology/employer>, ?x, ?y) -> has(<http://rdf.data-vocabulary.org/#affiliation>, ?x, ?y) . " +
	  		"has(<http://xmlns.com/foaf/0.1/homepageknows>, ?x, ?y) -> has(<http://rdf.data-vocabulary.org/#friend>, ?x, ?y) . " +
	  		"has(<http://dbpedia.org/ontology/profession>, ?x, ?y) -> has(<http://rdf.data-vocabulary.org/#title>, ?x, ?y) . " +
	  		"has(<http://dbpedia.org/ontology/occupation>, ?x, ?y) -> has(<http://rdf.data-vocabulary.org/#title>, ?x, ?y)";
	  KReSKB kReSKB = KReSRuleParser.parse(rule);
		if(kReSKB != null){
			KReSRuleList kReSRuleList = kReSKB.getkReSRuleList();
			if(kReSRuleList != null){
				for(KReSRule kReSRule : kReSRuleList){
					System.out.println("RULE : "+kReSRule.toString());
				}
			}
			System.out.println("RULE LIST IS NULL");
		}
		else{
			System.out.println("KB IS NULL");
		}
  }
}
