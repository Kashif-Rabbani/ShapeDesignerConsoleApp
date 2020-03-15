package fr.inria.prefix;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.junit.Test;

import fr.inria.shapedesigner.model.prefix.PrefixParsing;

public class PrefixParsingTest {
	private PrefixParsing parser = new PrefixParsing();
	
	@Test
	public void test1() {
		String inp = "PREFIX geo: <http://www.opengis.net/ont/geosparql#>";
		try {
			Map<String, String> result = parser.getPrefixes(new ByteArrayInputStream(inp.getBytes()));
			if (result.size() != 1)
				fail();
			if (!result.get("geo:").equals("http://www.opengis.net/ont/geosparql#"))
				fail();
		} catch (Exception e) {
			fail("Error when parsing");
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2() {
		String inp = "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
				+ ""
				+ "PREFIX wikibase: <http://wikiba.se/ontology#>"
				+ ""
				+ "";
		try {
			Map<String, String> result = parser.getPrefixes(new ByteArrayInputStream(inp.getBytes()));
			if (result.size() != 2)
				fail();
			if (!result.get("geo:").equals("http://www.opengis.net/ont/geosparql#"))
				fail();
			if (!result.get("wikibase:").equals("http://wikiba.se/ontology#"))
				fail();
		} catch (Exception e) {
			fail("Error when parsing");
			e.printStackTrace();
		}
	}
	
}
