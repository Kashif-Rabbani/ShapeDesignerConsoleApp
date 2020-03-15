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

import fr.inria.shapedesigner.model.ShExCSerializerForWikidata;
import fr.inria.shapedesigner.model.util.RDFPrintingUtil;

public class WikidataProject extends CreateValidateProject {
	public static String jsonKey = "Wikidata";

	public WikidataProject(PROJECT_TYPE type) {
		super(new GraphStoreForWikiData("https://query.wikidata.org/sparql"),type);
		this.shexcSerializer = new ShExCSerializerForWikidata();
	}
	
	@Override
	public void initialize() {
		super.initialize();
		for (String key:RDFPrintingUtil.commonWikiDataPrefixes.keySet()) 
			appendPrefixIfAbsent(key,RDFPrintingUtil.commonWikiDataPrefixes.get(key));
		addQuery(new NamedQuery("Select all nodes with given type", "x", "SELECT DISTINCT ?x WHERE { ?x wdt:P31 <replace-this-by-iri-of-the-class-such-as: wd:Q1549591>. } LIMIT 10"));
		addPattern(createNamedPattern("Wikidata direct properties", "{ wdt:~ __;}"));
		addPattern(createNamedPattern("Wikidata properties and their qualifiers", "{ p:P~ {ps:~ __; psn:~ __ ; a[__];  } };"));
		addPattern(createNamedPattern("All properties", "{ p:P~ {ps:~ __; psn:~ __ ; a[__];  }; wdt:~ __; ~ __ }"));
	}

	@Override
	public Map<String,Object> toJson(){
		Map<String, Object> result = super.toJson();
		result.put(jsonKey, "");
		return result;
	}
	
}
