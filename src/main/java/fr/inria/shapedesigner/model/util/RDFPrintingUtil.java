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
package fr.inria.shapedesigner.model.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;

public class RDFPrintingUtil {
	public static Map<String, String> commonWikiDataPrefixes = new HashMap<String, String>()
	{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
	    	put("rdf:","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	    	put("xsd:","http://www.w3.org/2001/XMLSchema#");
	    	put("ontolex:","http://www.w3.org/ns/lemon/ontolex#");
	    	put("dct:","http://purl.org/dc/terms/");
	    	put("rdfs:","http://www.w3.org/2000/01/rdf-schema#");
	    	put("owl:","http://www.w3.org/2002/07/owl#");
	    	put("skos:","http://www.w3.org/2004/02/skos/core#");
	    	put("schema:","http://schema.org/");
	    	put("cc:","http://creativecommons.org/ns#");
	    	put("geo:","http://www.opengis.net/ont/geosparql#");
	    	put("prov:","http://www.w3.org/ns/prov#");
	    	put("wikibase:","http://wikiba.se/ontology#");
	    	put("wdata:","http://www.wikidata.org/wiki/Special:EntityData/");
	    	put("bd:","http://www.bigdata.com/rdf#");
	    	put("wd:","http://www.wikidata.org/entity/");
	    	put("wdt:","http://www.wikidata.org/prop/direct/");
	    	put("wdtn:","http://www.wikidata.org/prop/direct-normalized/");
	    	put("wds:","http://www.wikidata.org/entity/statement/");
	    	put("p:","http://www.wikidata.org/prop/");
	    	put("wdref:","http://www.wikidata.org/reference/");
	    	put("wdv:","http://www.wikidata.org/value/");
	    	put("ps:","http://www.wikidata.org/prop/statement/");
	    	put("psv:","http://www.wikidata.org/prop/statement/value/");
	    	put("psn:","http://www.wikidata.org/prop/statement/value-normalized/");
	    	put("pq:","http://www.wikidata.org/prop/qualifier/");
	    	put("pqv:","http://www.wikidata.org/prop/qualifier/value/");
	    	put("pqn:","http://www.wikidata.org/prop/qualifier/value-normalized/");
	    	put("pr:","http://www.wikidata.org/prop/reference/");
	    	put("prv:","http://www.wikidata.org/prop/reference/value/");
	    	put("prn:","http://www.wikidata.org/prop/reference/value-normalized/");
	    	put("wdno:","http://www.wikidata.org/prop/novalue/");
	    }
	};
	
	public static String toPrettyString(RDFTerm node,Map<String, String> prefixes) {
		if (node instanceof BlankNode)
			return node.ntriplesString();
		if (node instanceof IRI) {
			IRI iri = (IRI) node;
			if (iri.getIRIString().toLowerCase().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
				return "a";
			String bestPrefix = null;
			for (String prefix:prefixes.keySet()) {
				if (iri.getIRIString().startsWith(prefixes.get(prefix)))
					if (bestPrefix == null || prefixes.get(bestPrefix).length()<prefixes.get(prefix).length())
						bestPrefix = prefix;
			}
			if (bestPrefix!=null)
				return bestPrefix+(iri.getIRIString().substring(prefixes.get(bestPrefix).length()));

			return iri.ntriplesString();
		}
		Literal lit = (Literal) node;
		if (lit.getLanguageTag().isPresent())
			return node.ntriplesString();
		String type = toPrettyString(lit.getDatatype(), prefixes);
		return "\""+lit.getLexicalForm()+"\"^^"+type;
	}
}
