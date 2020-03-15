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
package fr.inria.shapedesigner.model.validationShEx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import fr.inria.lille.shexjava.GlobalFactory;
import fr.inria.lille.shexjava.schema.Label;
import fr.inria.lille.shexjava.schema.ShexSchema;
import fr.inria.lille.shexjava.schema.parsing.ShExCParser;
import fr.inria.lille.shexjava.util.Pair;
import fr.inria.lille.shexjava.validation.ComputationController;
import fr.inria.lille.shexjava.validation.RecursiveValidationWithMemorization;
import fr.inria.lille.shexjava.validation.RefineValidation;
import fr.inria.lille.shexjava.validation.ValidationAlgorithm;
import fr.inria.shapedesigner.MainApp;
import fr.inria.shapedesigner.ProjectFactory;
import fr.inria.shapedesigner.model.util.LoadData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ValidationShEx {
	private RDF4J factory;
	private Model RDF4Jmodel;
	
	private Path dataFilename;
	private Graph dataGraph;
	
	private Path schemaFilename;
	private ShexSchema schema;

    private Map<String,String> prefixes;

	private ValidationAlgorithm algorithm;
    private ObservableList<MatchingExplanation> results; 
    private Map<Pair<RDFTerm,Label>,MatchingExplanation> resultsMap;
	
    private ComputationController compController = new ComputationController() {
		private boolean stop;
		
		@Override
		public void start() { stop = false; }
		
		@Override
		public void stop() { stop = true; }
		
		@Override
		public void canContinue() throws Exception {
			if (stop) throw new Exception("Computation must stop");
		}		
	};
    
	public ValidationShEx() {
		prefixes = new HashMap<>();
		this.factory = new RDF4J(); 
		GlobalFactory.RDFFactory = factory;
		results = FXCollections.observableArrayList();
	}
		

	public void parseAndLoadSchema(InputStream stream) throws Exception {
		ShExCParser parser = new ShExCParser();
		this.schema = new ShexSchema(parser.getRules(stream));
		prefixes.putAll(parser.getPrefixes());
	}
	
	public void LoadRDFGraph(File fp) throws RDFParseException, UnsupportedRDFormatException, IOException  {
		RDF4Jmodel = LoadData.loadModel(fp);
		for (Namespace name:RDF4Jmodel.getNamespaces()) {
			prefixes.put(name.getPrefix()+":", name.getName());
		}
		dataGraph = ProjectFactory.factory.asGraph(RDF4Jmodel);
	}
	
	public void setDataGraph(Graph dataGraph) {
		this.dataGraph = dataGraph;
	}
	
	
	public void initRefineValidation() {
		algorithm = new RefineValidation(schema, dataGraph);
		
		results.clear();
		resultsMap = new HashMap<>();
	}
	
	
	public void performRefineValidation() throws Exception {
		((RefineValidation) algorithm).validate(compController);
	}	
	
	
	public void createResult() {
		MainApp.logApp("Validation","typing size: "+algorithm.getTyping().getStatusMap().size());
		for (Pair<RDFTerm, Label> key:algorithm.getTyping().getStatusMap().keySet())
			addMatchingExplanation(new MatchingExplanation(this, key.one, schema.getShapeExprsMap().get(key.two)));
	}


	public void initRecursiveValidation() {
		algorithm = new RecursiveValidationWithMemorization(schema, dataGraph);

		results.clear();
		resultsMap = new HashMap<>();		
	}
	
	
	public void performRecursiveValidation(Label shape, Set<RDFTerm> nodes) throws Exception {
		for(RDFTerm node:nodes) {
			algorithm.validate(node, shape, compController);
		}
	}	
	
	public void stopValidation() {
		this.compController.stop();
	}

	
	private void addMatchingExplanation(MatchingExplanation me) {
		results.add(me);
		resultsMap.put(new Pair<RDFTerm,Label>(me.getNode(),me.getLabel()), me);
	}
	
	public void saveChangesInData() {
		try {
			File file = dataFilename.toFile();
			RDFFormat format = Rio.getParserFormatForFileName(file.getAbsolutePath().toString()).orElse(RDFFormat.TURTLE);
			FileOutputStream out = new FileOutputStream(file.getAbsolutePath().toString());
			Rio.write(this.getRDF4Jmodel(), out, format);
		} catch (Exception e) {
			MainApp.logApp("Error", "Error when saving changes in data.");
	    	return;
		}
	}
	
	
	////-------------------------------------
	//// Getter and setter
	////-------------------------------------
   

	
	public MatchingExplanation getMatchingExplanation(RDFTerm node, Label label) {
		return resultsMap.get(new Pair<RDFTerm,Label>(node,label));
	}


	public ObservableList<MatchingExplanation> getResults() {
		return results;
	}


	public ValidationAlgorithm getValidationAlgorithm() {
		return algorithm;
	}
	
	
	public Graph getDataGraph() {
		return dataGraph;
	}


	public ShexSchema getSchema() {
		return schema;
	}


	public Path getDataFilename() {
		return dataFilename;
	}


	public Path getSchemaFilename() {
		return schemaFilename;
	}


	public Model getRDF4Jmodel() {
		return RDF4Jmodel;
	}


	public Map<String, String> getPrefixes() {
		return prefixes;
	}

	
}
