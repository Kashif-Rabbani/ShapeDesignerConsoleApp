/*******************************************************************************
 * Copyright (C) 2019 Université de Lille - Inria
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.inria.shapedesigner.control;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.inria.shapedesigner.view.fxutil.CustomEditor;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.fxmisc.richtext.CodeArea;

import fr.inria.lille.shexjava.pattern.PatternParsing;
import fr.inria.lille.shexjava.pattern.abstrt.Pattern;
import fr.inria.lille.shexjava.pattern.indications.PatternInstantiation;
import fr.inria.lille.shexjava.pattern.indications.RecursiveShexFromPatternConstructor;
import fr.inria.lille.shexjava.pattern.indications.SHACLFromPatternConstructor;
import fr.inria.lille.shexjava.pattern.indications.ShexFromPatternConstructor;
import fr.inria.lille.shexjava.util.Pair;
import fr.inria.shapedesigner.MainApp;
import fr.inria.shapedesigner.model.ShExCSerializer;
import fr.inria.shapedesigner.model.prefix.PrefixParsing;
import fr.inria.shapedesigner.view.main.ConceptionTabController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 * @author Iovka Boneva
 * @author Jérémie Dusart
 *
 */
public class CreateValidateProject {
	public static String jsonKey = "";

	public enum PROJECT_TYPE {SHEX,SHACL};
		
	protected PROJECT_TYPE type;
	protected ShExCSerializer shexcSerializer = new ShExCSerializer();
	protected PatternParsing patParser = new PatternParsing();
	protected PrefixParsing preParser = new PrefixParsing();

	protected ObservableList<NamedPattern> patternsList;
	protected ObservableList<NamedQuery> queriesList;
	protected GraphStore graph;
	protected CodeArea editor;
	protected String resultString;

	public String getResultString() {
		return resultString;
	}

	public CreateValidateProject(GraphStore g, PROJECT_TYPE type) {
		this.type = type;
		graph = g;

		patternsList = FXCollections.observableList(new ArrayList<NamedPattern>());
		queriesList = FXCollections.observableList(new ArrayList<NamedQuery>());
	}
		
	
	// Add the pattern, query, prefixes initialization in this function
	public void initialize() {	
		if (type.equals(PROJECT_TYPE.SHACL)) {
			//System.out.println("editor.replaceText(\"PREFIX sh: <http://www.w3.org/ns/shacl#>\\n\");");
			editor = new CustomEditor();
			//editor.setAccessibleText("PREFIX sh: <http://www.w3.org/ns/shacl#>\n");

			editor.setAccessibleText("PREFIX sh: <http://www.w3.org/ns/shacl#>\n");

			//System.out.println(editor.getAccessibleText());
			//editorString += "PREFIX sh: <http://www.w3.org/ns/shacl#>\n";
		}
		if (type.equals(PROJECT_TYPE.SHEX))
			appendToSchema("# Cardinality: ?=zero or one; +=at least one; *=zero or more\n"); 

		Map<String, String> prefixes = graph.getPrefixes();

		//System.out.println(graph.getPrefixes().toString());

		for (String key:prefixes.keySet())
			appendPrefixIfAbsent(key,prefixes.get(key));
	}
	
	protected void appendPrefixIfAbsent(String name, String prefix) {
		try {
			Map<String, String> prefixes = parsePrefixes();
			if (!prefixes.containsKey(name))
				appendToSchema("PREFIX "+name+" <"+prefix+">\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void initializeFromJson(Map<String,Object> jsonObject) {
		loadListOfPattern((List<Object>) jsonObject.get("patterns"));
		loadListOfQuery((List<Object>) jsonObject.get("queries"));
		editor.replaceText(this.getSchema()+jsonObject.get("schema"));
	}
	
	
	public void loadListOfPattern(List<Object> json) {
		this.getListOfPattern().addAll( json.stream().map(x-> new NamedPattern((Map<String,Object>) x)).collect(Collectors.toList()) );		
	}
	
	
	public void loadListOfQuery(List<Object> json) {
		this.getListOfQuery().addAll( json.stream().map(x-> new NamedQuery((Map<String,Object>) x)).collect(Collectors.toList()) );
	}
		
	
	public Map<String,Object> toJson() {
		Map<String,Object> result = new LinkedHashMap<String, Object>();
		result.put("patterns", this.getListOfPattern().stream().map(x -> x.toJson()).collect(Collectors.toList()));
		result.put("queries", this.getListOfQuery().stream().map(x -> x.toJson()).collect(Collectors.toList()));
		result.put("schema",this.getSchema());
		if (type.equals(PROJECT_TYPE.SHACL))
			result.put("shacl","");
		if (type.equals(PROJECT_TYPE.SHEX))
			result.put("shex","");
		return result;
	}
		
	//FIXME a better way to stop a thread?
	protected Thread analysisThread = null;

	public void performAnalysis(NamedPattern pattern, List<NamedQuery> queries, ConceptionTabController conceptionTabController) {
		analysisThread = new Thread(){
			boolean stop = false;
			@Override
			public void run() {
				Map<NamedQuery,String> result = new HashMap<NamedQuery, String>();
				Pattern pat = computePattern(pattern.getDescription().getValue());
				for (NamedQuery query:queries) {
					List<RDFTerm> sample = new ArrayList<>(executeQuery(query));
					if (getType().equals(PROJECT_TYPE.SHEX))
						result.put(query, createShapeShEx(new PatternInstantiation(pat, sample, graph)));
					if (getType().equals(PROJECT_TYPE.SHACL))
						result.put(query, createShapeSHACL(new PatternInstantiation(pat, sample, graph)));
					if (stop) return;
				}
				if (!stop) {
					String resultText = "";
					for (NamedQuery query:queries) {
						resultText += "# Result for pattern "+pattern.getDescription().get().toString().replaceAll("\n", "")+
								" and query "+query.getDescription().get().toString().replaceAll("\n", "")+"\n";
						resultText += result.get(query);
						resultText += "\n\n";
					}
					//System.out.println(resultText);
					//editor.appendText(resultText);
					String resultIs = editor.getAccessibleText() + resultText;
					//System.out.println("result is printing here. ");
					//System.out.println(resultIs);
					//resultString = resultIs;
					//setSchema(resultIs);
					//conceptionTabController.analysisDone(resultText);
				}
			}
			
			@Override
			public void interrupt() {
				stop = true;
			}
			
			@Override
			public boolean isInterrupted() {
				return stop;
			}
		};
		analysisThread.start();
	}

	public void performAnalysis(NamedPattern pattern, List<NamedQuery> queries, ConceptionTabController conceptionTabController, String outputFile) {
		analysisThread = new Thread(){
			boolean stop = false;
			@Override
			public void run() {
				Map<NamedQuery,String> result = new HashMap<NamedQuery, String>();
				Pattern pat = computePattern(pattern.getDescription().getValue());
				for (NamedQuery query:queries) {
					List<RDFTerm> sample = new ArrayList<>(executeQuery(query));
					if (getType().equals(PROJECT_TYPE.SHEX))
						result.put(query, createShapeShEx(new PatternInstantiation(pat, sample, graph)));
					if (getType().equals(PROJECT_TYPE.SHACL))
						result.put(query, createShapeSHACL(new PatternInstantiation(pat, sample, graph)));
					if (stop) return;
				}
				if (!stop) {
					String resultText = "";
					for (NamedQuery query:queries) {
						resultText += "# Result for pattern "+pattern.getDescription().get().toString().replaceAll("\n", "")+
								" and query "+query.getDescription().get().toString().replaceAll("\n", "")+"\n";
						resultText += result.get(query);
						resultText += "\n\n";
					}
					//System.out.println(resultText);
					//editor.appendText(resultText);

					System.out.println("Schema is being set now ... ");
					//System.out.println(resultIs);
					setSchema(editor.getAccessibleText() + resultText);
					System.out.println("Schema set complete.");

					//System.out.println(getSchema());


					File dest = new File(outputFile);
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
						System.out.println("Started writing schema...");
						writer.write(getSchema());
						writer.close();
						System.out.println("Closing....");
					} catch (IOException e) {
						System.out.println("Error when saving Error when parsing the pattern. Message:"+ e.getMessage());
						e.printStackTrace();
					}

					//conceptionTabController.analysisDone(resultText);
				}
			}

			@Override
			public void interrupt() {
				stop = true;
			}

			@Override
			public boolean isInterrupted() {
				return stop;
			}
		};
		analysisThread.start();
	}

	public void performRecAnalysisShEx(NamedPattern pattern, Set<IRI> types, int sampleSize, ConceptionTabController conceptionTabController) {
		analysisThread = new Thread(){
			boolean stop = false;
			@Override
			public void run() {
				Pattern pat = computePattern(pattern.getDescription().getValue());
				
				Map<IRI,PatternInstantiation> patterns = new HashMap<>();
				for(IRI type:types) {
					MonadicSparqlQuery query = MonadicSparqlQuery.formQueryText("SELECT ?x WHERE { ?x a "+type.ntriplesString()+" . } LIMIT "+sampleSize,"x");
					List<RDFTerm> sample = new ArrayList<>(graph.getNodes(query, getPrefixes()));
					patterns.put(type, new PatternInstantiation(pat, sample, graph));
				}
				
				try {
					shexcSerializer.setPrefixes(parsePrefixes());
				} catch (Exception e) {
					MainApp.logApp("Error when parsing the prefixes", e.getMessage());
				}
				
				RecursiveShexFromPatternConstructor c = new RecursiveShexFromPatternConstructor();
				String resultText = shexcSerializer.ToShexC(c.construct(graph, patterns,"http://shex.gen/"));	
				conceptionTabController.analysisDone(resultText);

			}
			
			@Override
			public void interrupt() {
				stop = true;
			}
			
			@Override
			public boolean isInterrupted() {
				return stop;
			}
		};
		analysisThread.start();
	}
	
	
	
	public boolean stopAnalysis() {
		if (analysisThread!=null) {
			analysisThread.interrupt();
			boolean result = analysisThread.isInterrupted();

			if (result) 
				analysisThread=null;
			return result;
		}
		return true;
	}
	
	
	public Set<RDFTerm> executeQuery(NamedQuery query){
		String pre = "";
		try {
			Map<String, String> prefixes = parsePrefixes();
			for (String key:prefixes.keySet())
				pre += "PREFIX "+key+" <"+prefixes.get(key)+">\n";
		} catch (Exception e) {
			MainApp.logApp("Error when parsing the prefixes", e.getMessage());
		}
		return graph.getNodes(query.getQuery(),pre);
	}
	
		
	protected String createShape (PatternInstantiation pi) {
		if (type.equals(PROJECT_TYPE.SHEX))
			return createShapeShEx(pi);
		if (type.equals(PROJECT_TYPE.SHACL)) 
			return createShapeSHACL(pi);
		return "";
	}
	
	
	protected String createShapeShEx(PatternInstantiation pi) {
		try {
			this.shexcSerializer.setPrefixes(parsePrefixes());
		} catch (Exception e) {
			MainApp.logApp("Error when parsing the prefixes", e.getMessage());
		}
		ShexFromPatternConstructor c = new ShexFromPatternConstructor();
		return this.shexcSerializer.convertShapeExpr(c.construct(pi));	
	}
	
	
	protected String createShapeSHACL(PatternInstantiation pi) {
		String baseSHACL = "http://generated-shacl.io/";
		
		SHACLFromPatternConstructor c = new SHACLFromPatternConstructor(baseSHACL);
		Model model = c.construct(pi);

		boolean genPre = false;
		try {
			Map<String, String> prefixes = this.parsePrefixes();
			prefixes.keySet().stream().forEach(key -> model.setNamespace(key.substring(0,key.length()-1), prefixes.get(key)));
			genPre = prefixes.values().contains(baseSHACL);
			if (!genPre) model.setNamespace("shgen",baseSHACL);				
		} catch (Exception e) {
			MainApp.logApp("Error when parsing the prefixes", e.getMessage());
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Rio.write(model, baos, RDFFormat.TURTLE);
					
		String result = splitPrefixAndText(baos.toString()).two;
		if (!genPre) result = "PREFIX shgen: <"+baseSHACL+"> \n"+result;
		
		return result;
	}
	
	
	public Map<String,String> parsePrefixes () throws Exception {
		return preParser.getPrefixes(new ByteArrayInputStream(getPrefixes().getBytes()));
	}
	
	
	public void appendToSchema (String text) {
		editor.setAccessibleText(editor.getAccessibleText()+ text);
	}
	
	
	public void addQuery (NamedQuery q) {
		queriesList.add(q);
	}
	
	
	public void addPattern (NamedPattern p) {
		patternsList.add(p);
	}
	
	
	public NamedPattern createNamedPattern (String name, String descr) {
		Pattern p = computePattern (descr);
		if (p != null)
			return new NamedPattern(name, descr, p);
		else 
			return null;
	}
	
	
	protected Pattern computePattern (String text) {
		try {
			patParser.setPrefixesMap(parsePrefixes());
			return patParser.getPattern(new ByteArrayInputStream(text.getBytes()));
		} catch (Exception e) {
			MainApp.logApp("Error when parsing the pattern", e.getMessage());
			return null;
		}
	}
	
	
	public boolean setPatternDescription(NamedPattern np, String newDescription) {
		Pattern p = computePattern(newDescription);
		if (p != null)
			np.setDescription(newDescription);
		return p != null;
	}
	
	
	protected Pair<String,String> splitPrefixAndText(String text) {
		String one = "", two = "";
		String[] lines = text.split("\n");
		for (int i=0; i<lines.length;i++)
			if (lines[i].trim().toLowerCase().startsWith("prefix") || lines[i].trim().toLowerCase().startsWith("@prefix"))
				one += lines[i]+"\n";
			else
				two += lines[i]+"\n";
		return new Pair<String,String>(one,two);
	}
		
	// setter
	
	public void setEditorCodeArea(CodeArea editor) {
		this.editor = editor;
	}
	
	
	public void setSchema (String s) {
		editor.setAccessibleText(s);
	}
	
	
	//getters
	
	public GraphStore getGraph() {
		return graph;
	}
	
	
	public ShExCSerializer getShexcSerializer () throws Exception {
		Map<String, String> prefixMap = parsePrefixes();
		shexcSerializer.setPrefixes(prefixMap);
		return shexcSerializer;
	}
	
	
	public String getSchema() {
		return editor.getAccessibleText();
	}
	
		
	public String getPrefixes () {
		return splitPrefixAndText(editor.getText()).one;
	}
	
	
	public ObservableList<NamedPattern> getListOfPattern(){
		return patternsList;
	}
	
	
	public ObservableList<NamedQuery> getListOfQuery() {
		return queriesList;
	}


	public PROJECT_TYPE getType() {
		return type;
	}
	
	
}
