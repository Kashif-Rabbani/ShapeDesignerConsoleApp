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

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

public class LoadData {
	
	public static final List<RDFFormat> RDFFormats = Arrays.asList(new RDFFormat[] {
			RDFFormat.BINARY,
			RDFFormat.JSONLD,
			RDFFormat.N3,
			RDFFormat.NQUADS,
			RDFFormat.NTRIPLES,
			RDFFormat.RDFA,
			RDFFormat.RDFJSON,
			RDFFormat.RDFXML,
			RDFFormat.TRIG,
			RDFFormat.TRIX,
			RDFFormat.TURTLE
	});

	public static Model loadModel(File path) throws RDFParseException, UnsupportedRDFormatException, IOException {
		Optional<RDFFormat> format = Rio.getParserFormatForFileName(path.getName());
		if (!format.isPresent())
			throw new UnsupportedRDFormatException("File format not found");
			
		RDFFormat foundformat = format.get();
//		RDFFormat foundformat = null;
//		for (RDFFormat format:RDFFormats) {
//			for (String ext:format.getFileExtensions()) {
//				if (path.toString().endsWith(ext)) {
//					foundformat = format;	
//				}
//			}
//		} 
//		System.out.println(foundformat);


		System.out.println("Streaming file to create a model.");
		FileInputStream fin = new FileInputStream(path);

		ProgressBarBuilder pbb = new ProgressBarBuilder()
				.setTaskName("Reading")
				.setUnit("MB", 1048576); // setting the progress bar to use MB as the unit
		Model model;
		try (Reader reader = new BufferedReader(new InputStreamReader(ProgressBar.wrap(fin, pbb)))) {
			 model =	Rio.parse(reader,"", foundformat);
		}


		//Model model = Rio.parse(fin, "", foundformat);
		return model;
	}
	
	
	
}
