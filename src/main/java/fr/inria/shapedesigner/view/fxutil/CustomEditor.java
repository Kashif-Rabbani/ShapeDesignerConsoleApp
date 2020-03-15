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
package fr.inria.shapedesigner.view.fxutil;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class CustomEditor extends CodeArea {
	
	public CustomEditor() {
		super();
		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}
	
	public void setText(String s) {
		this.replaceText("");
		this.replaceText(s);
	}
	

	public StyleSpans<Collection<String>> computeHighlighting(String t) {
		Pattern p = Pattern.compile(
				"(?<KEYWORD>" + 	"(SELECT|WHERE|FILTER|HAVING|CONSTRUCT|PREFIX|IRI)" + ")"
				+ "|(?<COMMENT>" + 	"#.*[$\n\r]" + ")"
				+ "|(?<VARIABLE>" + "\\?[a-zA-Z0-9]+" + ")"
				+ "|(?<POSTFIX>" + 	"(?<=:)[^\r\n\t\0,;. ]+" + ")"
				+ "|(?<PREFIX>" + 	"([a-zA-Z0-9]+(-[a-zA-Z0-9]+)?)?:" + ")"
				+ "|(?<SYMBOL>" + 	"[\\(\\[;\\|\\]\\)\\{\\},\\*]" + ")"
				+ "|(?<URL>" + 		"<(http://|https://).*?>" + ")"
				+ "|(?<PATTERN>" + 	"<.*?>" + ")"
				);
		Matcher m = p.matcher(t);
		StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<Collection<String>>();
		int pos = 0;
		while (m.find()) {
			String styleclass =
					m.group("KEYWORD") != null ? "keyword" :
					m.group("VARIABLE") != null ? "variable" :
					m.group("POSTFIX") != null ? "postfix" :
					m.group("PREFIX") != null ? "prefix" :
					m.group("SYMBOL") != null ? "symbol" :
					m.group("URL") != null ? "url" :
					m.group("PATTERN") != null ? "pattern" :
					m.group("COMMENT") != null ? "comment" :
					null;
			builder.add(Collections.emptyList(), m.start() - pos);
			builder.add(Collections.singleton(styleclass), m.end() - m.start());
			pos = m.end();
		}
		builder.add(Collections.emptyList(), t.length() - pos);
		return builder.create();
	}
	
	public StyleSpans<Collection<String>> computePatternHighlighting(String t) {
		Pattern p = Pattern.compile(
				  "(?<PCOMMENT>" + "#.*($|\n|\r)" + ")"
				+ "|(?<PVARIABLE>" + "__" + ")"
				+ "|(?<PPOSTFIX>" + "(?<=:)[^\r\n\t\0,;. ]+" + ")"
				+ "|(?<PPREFIX>" + "([a-zA-Z0-9]+(-[a-zA-Z0-9]+)?):" + ")"
				+ "|(?<PURL>" + "<.*>" + ")"
				);
		Matcher m = p.matcher(t);
		StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<Collection<String>>();
		int pos = 0;
		while (m.find()) {
			String styleclass =
					m.group("PVARIABLE") != null ? "postfix" :
					m.group("PPOSTFIX") != null ? "postfix" :
					m.group("PPREFIX") != null ? "prefix" :
					m.group("PURL") != null ? "url" :
					m.group("PCOMMENT") != null ? "comment" :
					null;
			builder.add(Collections.emptyList(), m.start() - pos);
			builder.add(Collections.singleton(styleclass), m.end() - m.start());
			pos = m.end();
		}
		builder.add(Collections.emptyList(), t.length() - pos);
		return builder.create();
	}
	
}
