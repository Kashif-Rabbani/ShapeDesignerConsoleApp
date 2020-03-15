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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.parser.QueryParserFactory;
import org.eclipse.rdf4j.query.parser.QueryParserRegistry;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParserFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import fr.inria.shapedesigner.MainApp;
import fr.inria.shapedesigner.ProjectFactory;
import fr.inria.shapedesigner.model.util.LoadData;

public class GraphStore implements Graph {
	
	protected Repository repository;
	
	/** Initialize a new empty memory store
	 * 
	 * @param graph
	 */
	public GraphStore () {
		repository = new SailRepository(new MemoryStore());
		repository.init();
		init();
	}
	
	/** Creates a store at the file path or load it if it already exists.
	 * 
	 * @param dataDir
	 */
	public GraphStore (File dataDir) {
		repository = new SailRepository(new NativeStore(dataDir));
		repository.init();
		init();
	}
	
	/** Creates a connection to the database at the indicated url. For wikidata "https://query.wikidata.org/sparql";
	 * 
	 * @param sparqlURL
	 */
	public GraphStore(String sparqlURL) {
		repository = new SPARQLRepository(sparqlURL);
		repository.init();
		init();
	}

	public void init() {
		Optional<QueryParserFactory> factory = QueryParserRegistry.getInstance().get(QueryLanguage.SPARQL);
		if (!factory.isPresent()) {
			QueryParserRegistry.getInstance().add(new SPARQLParserFactory());
		}
	}
	

	public Set<RDFTerm> getNodes(MonadicSparqlQuery filter, String prefixes) {
		String queryString = filter.getQueryString();
		String variable = filter.getQueryVariableName();
		Set<RDFTerm> result = new HashSet<>();
		
		try (RepositoryConnection con = repository.getConnection()) {
			con.begin();
			TupleQuery query = con.prepareTupleQuery(prefixes+"\n"+queryString);
			try (TupleQueryResult eval = query.evaluate()) {
				while (eval.hasNext()) {
					BindingSet solution = eval.next();
					result.add(ProjectFactory.factory.asRDFTerm(solution.getValue(variable)));
				}
			} catch(Exception e) {
				MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
			} finally {
	        	con.close();
	        }
		} catch(Exception e) {
			MainApp.logApp(e.getClass()+" 2",e.getLocalizedMessage());
		}
		return result;
	}
		

	@Override
	public void close() throws IOException {
		repository.shutDown();
	}

	@Override
	public Stream<? extends Triple> stream(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		List<Statement> result = null;
		RepositoryConnection con = repository.getConnection();
		try {
			Resource sub = (Resource) ProjectFactory.factory.asValue(subject);
			org.eclipse.rdf4j.model.IRI pred =  (org.eclipse.rdf4j.model.IRI) ProjectFactory.factory.asValue(predicate);
			Value obj = ProjectFactory.factory.asValue(object);
			
            result = Iterations.asList(con.getStatements(sub,pred,obj));
        } catch(Exception e) {
			MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
		} finally {
        	con.close();
        }
		
		if (result == null)
			return null;
		else
			return result.stream().map(statement -> ProjectFactory.factory.asTriple(statement));
	}
	
	@Override
	public boolean contains(Triple triple) {
		return contains(triple.getSubject(),triple.getPredicate(),triple.getObject());

	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return stream(subject,predicate,object).count()>0;
	}

	@Override
	public void clear() {
		RepositoryConnection con = repository.getConnection();
		try {
			con.begin();
			con.remove(con.getStatements(null, null, null));
			con.commit();
		} catch (RepositoryException e) {
            con.rollback();
    		MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
        } finally {
        	con.close();
        }
	}

	@Override
	public long size() {
		return stream(null,null,null).count();
	}

	@Override
	public Stream<? extends Triple> stream() {
		return stream(null,null,null);
	}

	@Override
	public void add(Triple triple) {
		this.add(triple.getSubject(),triple.getPredicate(),triple.getObject());
	}

	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		RepositoryConnection con = repository.getConnection();
		try {
			Resource sub = (Resource) ProjectFactory.factory.asValue(subject);
			org.eclipse.rdf4j.model.IRI pred =  (org.eclipse.rdf4j.model.IRI) ProjectFactory.factory.asValue(predicate);
			Value obj = ProjectFactory.factory.asValue(object);
			
			con.begin();
			con.add(sub, pred, obj);
			con.commit();
		} catch (RepositoryException e) {
            con.rollback();
    		MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
      } finally {
        	con.close();
        }	
	}

	@Override
	public void remove(Triple triple) {
		this.remove(triple.getSubject(),triple.getPredicate(),triple.getObject());
	}

	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		RepositoryConnection con = repository.getConnection();
		try {
			Resource sub = (Resource) ProjectFactory.factory.asValue(subject);
			org.eclipse.rdf4j.model.IRI pred =  (org.eclipse.rdf4j.model.IRI) ProjectFactory.factory.asValue(predicate);
			Value obj = ProjectFactory.factory.asValue(object);
			
			con.begin();
			con.remove(sub, pred, obj);
			con.commit();
		} catch (RepositoryException e) {
            con.rollback();
    		MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
        } finally {
        	con.close();
        }	
	}


	public void loadDataFromFile(File loadFile) {
		System.out.println("loadDataFromFile, line 242 of GraphStore.java");
		Model model;
		try {
			model = LoadData.loadModel(loadFile);
			System.out.println("Model prepared.");
			loadDataFromModel(model);
			System.out.println("Model loaded...");
		} catch (RDFParseException e) {
    		MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
		} catch (UnsupportedRDFormatException e) {
    		MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
		} catch (IOException e) {
    		MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
		}	
	}
	

	public void loadDataFromModel(Model model) {
		loadDataFromGraph(ProjectFactory.factory.asGraph(model));
		
		RepositoryConnection con = repository.getConnection();
		try {
			con.begin();
			for (Namespace ns:model.getNamespaces())
				con.setNamespace(ns.getName(), ns.getPrefix());			
			con.commit();
		} catch(Exception e) {
			con.rollback();
			MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
		} finally {
			con.close();
			Logger logger = Logger.getLogger("ShExApp logger");
		}
	}


	public void loadDataFromGraph(Graph graph) {
		RepositoryConnection con = repository.getConnection();
		try {
			con.begin();
			for (Triple t : graph.iterate()) {
				con.add(ProjectFactory.factory.asStatement(t));
			}
			con.commit();
		} catch(Exception e) {
			con.rollback();
			MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
		} finally {
			con.close();
			Logger logger = Logger.getLogger("ShExApp logger");
			logger.fine("Data loaded. New store size "+this.size()+".");
		}
	}
	
	
	public Map<String, String> getPrefixes() {
		Map<String,String> result = new HashMap<>();
		RepositoryConnection con = repository.getConnection();

		for(Namespace ns:Iterations.asList(con.getNamespaces())) {
			result.put(ns.getName()+":", ns.getPrefix());
		}
		
		return result;
	}

	
}


