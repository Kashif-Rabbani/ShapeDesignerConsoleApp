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

import java.util.Map;

public class SparQLProject extends CreateValidateProject {
	public static String jsonKey = "SparQL";
	
	protected String address;
	
	public SparQLProject(String address, PROJECT_TYPE type) {
		super(new GraphStore(address),type);
		this.address = address;
	}

	
	@Override
	public void initialize() {
		super.initialize();
		appendPrefixIfAbsent("dct:","http://purl.org/dc/terms/");
		appendPrefixIfAbsent("ontolex:","http://www.w3.org/ns/lemon/ontolex#");
		appendPrefixIfAbsent("owl:","http://www.w3.org/2002/07/owl#");
		appendPrefixIfAbsent("rdf:","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		appendPrefixIfAbsent("rdfs:","http://www.w3.org/2000/01/rdf-schema#");
		appendPrefixIfAbsent("schema:","http://schema.org/");
		appendPrefixIfAbsent("skos:","http://www.w3.org/2004/02/skos/core#");
		appendPrefixIfAbsent("xsd:","http://www.w3.org/2001/XMLSchema#");	
		
		addQuery(new NamedQuery("Select all nodes with given type", "x", "SELECT ?x WHERE { ?x a <ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C55029>. } LIMIT 10"));
		addPattern(createNamedPattern("list of rdf:type values, all properties", "{a [__] ; ~ __}"));
		addPattern(createNamedPattern("list of rdf:type values", "{a [__] }"));
		addPattern(createNamedPattern("list of rdf:type values, list of values for all properties", "{a [__]; ~ [__] }"));	
	}

	@Override
	public Map<String,Object> toJson(){
		Map<String, Object> result = super.toJson();
		result.put(jsonKey, address);
		return result;
	}
}
