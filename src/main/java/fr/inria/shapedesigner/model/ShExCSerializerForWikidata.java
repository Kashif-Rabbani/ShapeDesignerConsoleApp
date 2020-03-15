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
package fr.inria.shapedesigner.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.parser.QueryParserFactory;
import org.eclipse.rdf4j.query.parser.QueryParserRegistry;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParserFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import fr.inria.lille.shexjava.pattern.indications.ShexFromPatternConstructor;
import fr.inria.lille.shexjava.schema.abstrsynt.Annotation;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleConstraint;
import fr.inria.shapedesigner.MainApp;
import fr.inria.shapedesigner.ProjectFactory;

public class ShExCSerializerForWikidata extends ShExCSerializer {
	protected Map<String,String> propertiesName = null;
	
	
	@Override
	protected String convertTripleConstraint(TripleConstraint triple) {
		String result = "";
		String name = ((IRI) ProjectFactory.factory.asValue(triple.getProperty().getIri())).getLocalName();
		if (getName(name)!=null) { 
			result += getCurrentIndent();
			Optional<Annotation> freq = triple.getAnnotations().stream()
				.filter(a -> a.getPredicate().equals(ShexFromPatternConstructor.FREQ_ANNOTATION)).findFirst();
			String freqString = "";
			if (freq.isPresent()) {
				String s = freq.get().getObjectValue().toString();
				freqString = "; frequency: " + s.substring(1, s.length()-1);
			}
			result += "# "+getName(name)+freqString+"\n";
		}
		result += getCurrentIndent();
		if (triple.getId()!=null && !triple.getId().isGenerated()) result += "$"+triple.getId().toPrettyString()+" ";
		result += triple.getProperty().toPrettyString(prefixes)+" "+convertShapeExpr(triple.getShapeExpr()); 
		return result;
	}
	
	
	protected String getName(String name) {
		if (propertiesName == null) {
			Repository repository = new SPARQLRepository("https://query.wikidata.org/sparql");
			repository.init();
			
			Optional<QueryParserFactory> factory = QueryParserRegistry.getInstance().get(QueryLanguage.SPARQL);
			if (!factory.isPresent()) {
				QueryParserRegistry.getInstance().add(new SPARQLParserFactory());
			}
			
			RepositoryConnection con = repository.getConnection();
			con.begin();
			TupleQuery query = con.prepareTupleQuery("SELECT * WHERE {\n" + 
					" ?property wikibase:directClaim  ?propertyNumber ;      \n" + 
					"           rdfs:label ?propertyLabel .\n" + 
					"  FILTER (lang(?propertyLabel) = \"en\")\n" + 
					"}");
			try (TupleQueryResult eval = query.evaluate()) {
				propertiesName = new HashMap<>();
				while (eval.hasNext()) {
					BindingSet solution = eval.next();
					propertiesName.put(((IRI) solution.getBinding("property").getValue()).getLocalName(),
							solution.getBinding("propertyLabel").getValue().stringValue());
				}
			} catch(Exception e) {
				MainApp.logApp(e.getClass()+"",e.getLocalizedMessage());
			}
			con.close();
		}
		if (propertiesName!=null)
			return propertiesName.get(name);
		return null;
	}

}
