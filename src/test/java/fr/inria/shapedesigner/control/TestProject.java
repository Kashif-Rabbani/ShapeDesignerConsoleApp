package fr.inria.shapedesigner.control;

/**
 * 
 * @author Iovka Boneva
 *
 */
public class TestProject extends CreateValidateProject{

	public TestProject(GraphStore g) {
		super(g,PROJECT_TYPE.SHEX);
		String s = "PREFIX : <http://example.org/>\n" + 
		"PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n" + 
		"PREFIX tmp: <http://tmp.io/>\n" + 
		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
		"PREFIX gn: <http://www.geonames.org/ontology#> \n" + 
		"PREFIX tg: <http://telegraphis.net/ontology/geography/geography#>\n" + 
		"\n" + 
		"tmp:Country {\n" + 
		"    rdf:type [gn:Country] ;\n" + 
		"    tg:currency . *;\n" + 
		"    gn:population xsd:string;\n" + 
		"    tg:isoShortName [ @fr ] ;\n" + 
		"    tg:isoShortName [ @en ] ;\n" + 
		"}" ;
		
		setSchema(s);
		String pattern = "{ http: [__] }";
		NamedPattern np = createNamedPattern("test", pattern);
		addPattern(np);
		NamedQuery nq = new NamedQuery("Select country","x","Select ?x WHERE {?x a <http://www.geonames.org/ontology#Country>.}");
		addQuery(nq);
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	

}
