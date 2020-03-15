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

import org.apache.commons.rdf.api.IRI;

/**
 * 
 * @author Iovka Boneva
 *
 */
public class MonadicSparqlQuery {

	private String queryText;
	private int limit = -1;
	private String queryVariableName;
	
	private MonadicSparqlQuery (String queryText) {
		this.queryText = queryText; 
	}
	
	public static MonadicSparqlQuery formQueryText (String text, String queryVariableName) {
		MonadicSparqlQuery r = new MonadicSparqlQuery(text);
		r.queryVariableName = queryVariableName;
		return r;
	}
	
	public static MonadicSparqlQuery fromRdfType (IRI rdfType) {
		MonadicSparqlQuery r = new MonadicSparqlQuery(String.format("SELECT ?x WHERE ?x a %s", rdfType.getIRIString()));
		r.queryVariableName = "x";
		return r;
	}

	/** A negative or zero limit means no limit. */
	public void setLimit (int limit) {
		if (limit > 0)
			this.limit = limit;
	}
	
	
	public String getQueryString() {
		String limitText = limit > 0 ? "\nLIMIT " + limit : "";
		return queryText + limitText;
	}
	
	public String getQueryVariableName () {
		return queryVariableName;
	}
	
}
