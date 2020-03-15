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

import java.util.Collections;
import java.util.Map;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.eclipse.rdf4j.model.datatypes.XMLDatatypeUtil;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class RDFParsingUtil {
	public static RDF4J factory = new RDF4J();
	public static SimpleValueFactory RD4JFactory = SimpleValueFactory.getInstance();
	
	
	public static BlankNodeOrIRI parseBlankNodeOrIRI(String value) throws Exception {
		return parseBlankNodeOrIRI(value,Collections.emptyMap());
	}
	
	public static RDFTerm parseRDFTerm(String value) throws Exception {
		return parseRDFTerm(value,Collections.emptyMap());
	}
	
	public static IRI parseIRI(String value) throws Exception {
		return parseIRI(value,Collections.emptyMap());
	}
	
	
	public static BlankNodeOrIRI parseBlankNodeOrIRI(String value, Map<String, String> prefixes) throws Exception {
		RDFTerm result = parseRDFTerm(value, prefixes);
		if ((result instanceof IRI) || (result instanceof BlankNode))
			return (BlankNodeOrIRI) result;
		throw new Exception("Failed to parse the string as IRI or BlankNode: "+value);
	}
		
	public static IRI parseIRI(String value, Map<String, String> prefixes) throws Exception {
		RDFTerm result = parseRDFTerm(value,prefixes);
		if ((result instanceof IRI))
			return (IRI) result;
		throw new Exception("Failed to parse the string as IRI: "+value);
	}
		
	public static RDFTerm parseRDFTerm(String value, Map<String, String> prefixes) throws Exception {
		value = value.trim();
		
		// test if it is a blank node
		if (value.startsWith("_:")) 
			return factory.createBlankNode(value);
		
		if (value.equals("a"))
			return factory.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		
		// test if it is an IRI
		if (value.startsWith("<") && value.endsWith(">"))
			return factory.createIRI(value.substring(1,value.length()-1));
		
		// test if start with a prefixes
		for (String pre:prefixes.keySet()) 
			if (value.startsWith(pre))
				return factory.createIRI(value.replace(pre, prefixes.get(pre)));
		
		// test if it contains a datatype (if no datatype then try to infer the datatype
		if (value.contains("^^")) {
			String datatype = value.split("\\^\\^")[1];
			datatype = datatype.substring(datatype.indexOf("<")+1, datatype.indexOf(">"));
			
			String val = value.split("\\^\\^")[0];
			val = val.substring(val.indexOf("\"")+1, val.length()-1);
			
			return factory.createLiteral(val,factory.createIRI(datatype));
		}
		
		if (XMLDatatypeUtil.isValidBoolean(value))
			return factory.asRDFTerm(RD4JFactory.createLiteral(XMLDatatypeUtil.parseBoolean(value)));
		
		if (XMLDatatypeUtil.isValidDecimal(value))
			return factory.asRDFTerm(RD4JFactory.createLiteral(XMLDatatypeUtil.parseDecimal(value)));
				
		if (value.startsWith("\"") && value.endsWith("\""))
			return factory.createLiteral(value.substring(1,value.length()-1));
		throw new Exception("Failed to parse the string: "+value);
	}

}
