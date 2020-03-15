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
package fr.inria.shapedesigner;

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

public class AllRdfTypes {

	public static void main(String[] args) {

		try {
			java.net.URL documentUrl = new URL(args[0]);
			InputStream inputStream = documentUrl.openStream();
			Model model = Rio.parse(inputStream, documentUrl.toString(), RDFFormat.TURTLE);
			Set<Value> allTypes = new HashSet<>();
			for (Value type: model.filter(null, RDF.TYPE, null).objects()) {
				allTypes.add(type);
			}
			for (Value type : allTypes) {
				System.out.println(type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
