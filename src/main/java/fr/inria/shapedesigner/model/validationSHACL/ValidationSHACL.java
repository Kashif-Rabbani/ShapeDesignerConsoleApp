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
package fr.inria.shapedesigner.model.validationSHACL;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.rdf4j.RDF4JTerm;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.eclipse.rdf4j.sail.shacl.ShaclSailValidationException;

import fr.inria.shapedesigner.ProjectFactory;

public class ValidationSHACL {
	private SailRepository sailRepository;
	private Graph dataGraph;
	private List<SHACLValidationResult> results;
	private Model schema;
	
	public ValidationSHACL() {
		ShaclSail shaclSail = new ShaclSail(new MemoryStore());
		sailRepository = new SailRepository(shaclSail);
		sailRepository.init();
		
		results = new ArrayList<>();
	}

	public void parseAndLoadSchema(String schema) throws Exception {
		SailRepositoryConnection connection = sailRepository.getConnection() ;
		
		connection.begin();
		Reader shaclRules = new StringReader(schema);
		connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
		connection.commit();
		connection.close();
		
		this.schema = Rio.parse(new StringReader(schema), "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
	}
	
	public void setDataGraph(Graph dataGraph) {
		this.dataGraph = dataGraph;
	}
	
	
	public void validate() {
		SailRepositoryConnection connection = sailRepository.getConnection() ;
		
		connection.begin();
		connection.add(dataGraph.stream().map(tr -> ProjectFactory.factory.asStatement(tr)).collect(Collectors.toList()));
		
		try {
            connection.commit();
		} catch (RepositoryException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof ShaclSailValidationException) {
            	Model vrm = ((ShaclSailValidationException) cause).validationReportAsModel();
           	
            	for (Statement tr:vrm.filter(null, null, SHACL.VALIDATION_RESULT)) {
            		results.add(processAResutlt(vrm,tr));
            	}
            	
            	//Rio.write(vrm, System.out, RDFFormat.TURTLE);
            }
		}
		
	}
	
	private SHACLValidationResult processAResutlt(Model vrm, Statement tr) {
		
		RDF4JTerm shape = vrm.filter(tr.getSubject(), SHACL.SOURCE_SHAPE, null).stream()
				.map(st -> ProjectFactory.factory.asRDFTerm(st.getObject())).collect(Collectors.toList()).get(0 );
		RDF4JTerm source = vrm.filter(tr.getSubject(), SHACL.SOURCE_CONSTRAINT_COMPONENT, null).stream()
				.map(st -> ProjectFactory.factory.asRDFTerm(st.getObject())).collect(Collectors.toList()).get(0);
		RDF4JTerm constraintVal = getConstraintValue(vrm,tr);
		RDF4JTerm node = vrm.filter(tr.getSubject(), SHACL.FOCUS_NODE, null).stream()
				.map(st -> ProjectFactory.factory.asRDFTerm(st.getObject())).collect(Collectors.toList()).get(0);
		RDF4JTerm path = vrm.filter(tr.getSubject(), SHACL.RESULT_PATH, null).stream()
				.map(st -> ProjectFactory.factory.asRDFTerm(st.getObject())).collect(Collectors.toList()).get(0);
		List<RDFTerm> neighborhood = dataGraph.stream((BlankNodeOrIRI) node, (IRI) path, null).
				map(t -> t.getObject()).collect(Collectors.toList());
		return new SHACLValidationResult(shape, source, constraintVal, node, path, neighborhood);
	}

	
	private RDF4JTerm getConstraintValue(Model vrm, Statement tr) {
		Value shape = vrm.filter(tr.getSubject(), SHACL.SOURCE_SHAPE, null).stream().map(t -> t.getObject())
				.collect(Collectors.toList()).get(0 );
		Value constraintComponent = vrm.filter(tr.getSubject(), SHACL.SOURCE_CONSTRAINT_COMPONENT, null).stream().map(t -> t.getObject())
				.collect(Collectors.toList()).get(0 );;
		
		List<RDF4JTerm> result = null;
		if (constraintComponent.equals(SHACL.MIN_COUNT_CONSTRAINT_COMPONENT))
			result = schema.filter((Resource) shape, SHACL.MIN_COUNT, null).stream()
					.map(st -> ProjectFactory.factory.asRDFTerm(st.getObject())).collect(Collectors.toList());
		if (constraintComponent.equals(SHACL.MAX_COUNT_CONSTRAINT_COMPONENT))
			result = schema.filter((Resource) shape, SHACL.MAX_COUNT, null).stream()
					.map(st -> ProjectFactory.factory.asRDFTerm(st.getObject())).collect(Collectors.toList());
		if (constraintComponent.equals(SHACL.DATATYPE_CONSTRAINT_COMPONENT))
			result = schema.filter((Resource) shape, SHACL.DATATYPE, null).stream()
					.map(st -> ProjectFactory.factory.asRDFTerm(st.getObject())).collect(Collectors.toList());

		if (result == null || result.size()==0)		
			return null;
		return result.get(0);
	}
	
	
	public List<SHACLValidationResult> getResults() {
		return results;
	}
	
	
}
