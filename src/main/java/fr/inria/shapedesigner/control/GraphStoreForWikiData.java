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

import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.inria.shapedesigner.ProjectFactory;

public class GraphStoreForWikiData extends GraphStore {
	protected GraphStore cache;
	
	public GraphStoreForWikiData() {
		super();
		initCache();
	}

	public GraphStoreForWikiData(String path) {
		super(path);
		initCache();
	}
	
	
	private void initCache() {
		cache = new GraphStore();
	}
	
	private IRI subjectMark = ProjectFactory.factory.createIRI("http://shex.io/subjectMarked");
	private IRI predicateMark = ProjectFactory.factory.createIRI("http://shex.io/predicateMarked");
	@Override
	public Stream<? extends Triple> stream(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		if (subject != null && ! cache.contains(subjectMark, predicateMark, subject)) {
			optimizeQuery(subject);
		} 		
		if (subject == null && object!=null && ! cache.contains(subjectMark, predicateMark,object)) {
			super.stream(null, null, object).forEach(tr -> cache.add(tr.getSubject(), tr.getPredicate(), tr.getObject()));
			cache.add(subjectMark,predicateMark,object);
		}		
		if (subject == null && object==null && predicate!=null && !cache.contains(subjectMark, predicateMark,predicate)) {
			super.stream(null, predicate, null).forEach(tr -> cache.add(tr.getSubject(), tr.getPredicate(), tr.getObject()));
			cache.add(subjectMark,predicateMark,predicate);
		}
		
		if (subject == null && object==null && predicate==null) {
			return super.stream();
		}
		
		return cache.stream(subject,predicate,object).filter(tr -> !tr.getPredicate().equals(predicateMark));
	}
	
	
	@Override
	public void clear() {
		cache.clear();
	}

	
	protected Stream<? extends Triple> optimizeQuery(BlankNodeOrIRI subject) {
		String request;
		if (subject.ntriplesString().startsWith("<http://www.wikidata.org/entity/Q"))
			request = "SELECT ?prop ?obj ?prop2 ?obj2 WHERE {  "
				+ "{ "+subject.ntriplesString()+" ?prop ?obj. }  "
				+ " UNION "
				+ " { "+subject.ntriplesString()+" ?prop ?obj. ?obj  ?prop2 ?obj2. FILTER regex(str(?prop), \"^http://www.wikidata.org/prop/P\") } "
				+ "} ";
		else
			request = "SELECT ?prop ?obj ?prop2 ?obj2 WHERE {  "+subject.ntriplesString()+" ?prop ?obj. }"  ;
		System.out.println(request);

		boolean status = false ;
		int trial = 10;

		while (!status && trial>0) {
			RepositoryConnection conn = repository .getConnection();
			try {
				trial--;
				TupleQuery query = conn.prepareTupleQuery(request);
				TupleQueryResult result = query.evaluate();

				while (result.hasNext()) {
					BindingSet solution = result.next();
					IRI predSol = (IRI) ProjectFactory.factory.asRDFTerm(solution.getBinding("prop").getValue());
					RDFTerm objSol = (RDFTerm) ProjectFactory.factory.asRDFTerm(solution.getBinding("obj").getValue());
					cache.add(ProjectFactory.factory.createTriple(subject, predSol, objSol));
				
					if (subject.ntriplesString().startsWith("<http://www.wikidata.org/entity/Q")
							&& predSol.getIRIString().startsWith("http://www.wikidata.org/prop/P")) {
						cache.add(subjectMark,predicateMark,objSol);
					}
					
					if (solution.hasBinding("prop2")) {
						IRI predSol2 = (IRI) ProjectFactory.factory.asRDFTerm(solution.getBinding("prop2").getValue());
						RDFTerm objSol2 = (RDFTerm) ProjectFactory.factory.asRDFTerm(solution.getBinding("obj2").getValue());
						cache.add(ProjectFactory.factory.createTriple((BlankNodeOrIRI) objSol, predSol2, objSol2));
					}
				}
				status = true;
				cache.add(subjectMark,predicateMark,subject);
			} catch (Exception e) {
				status = false;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} finally {
	        	conn.close();
	        }
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return cache.stream(subject,null,null).filter(tr -> !tr.getPredicate().equals(predicateMark));
	}
}
