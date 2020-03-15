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
package fr.inria.shapedesigner.model.prefix;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import fr.inria.shapedesigner.model.prefix.parsing.PrefixBaseVisitor;
import fr.inria.shapedesigner.model.prefix.parsing.PrefixLexer;
import fr.inria.shapedesigner.model.prefix.parsing.PrefixParser;
import fr.inria.shapedesigner.model.prefix.parsing.PrefixParser.PrefixContext;
import fr.inria.shapedesigner.model.prefix.parsing.PrefixParser.PrefixDeclContext;

public class PrefixParsing extends PrefixBaseVisitor<Object> {
	private Map<String,String> prefixes;
	
	public Map<String,String> getPrefixes(InputStream is) throws Exception{
		prefixes = new HashMap<>();
		
		Reader isr = new InputStreamReader(is,Charset.defaultCharset().name());
		CharStream inputStream = CharStreams.fromReader(isr);
		PrefixLexer lexer = new PrefixLexer(inputStream);
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		PrefixParser parser = new PrefixParser(commonTokenStream);   
		PrefixParser.PrefixContext context = parser.prefix();      
				
		visitPrefix(context);
		
		return prefixes;
		
	}

	@Override
	public Object visitPrefix(PrefixContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Object visitPrefixDecl(PrefixDeclContext ctx) {
		String pre = ctx.PNAME_NS().getText();
		String iri = ctx.IRIREF().getText();
		
		prefixes.put(pre, iri.substring(1,iri.length()-1));
		return null;
	}

}
