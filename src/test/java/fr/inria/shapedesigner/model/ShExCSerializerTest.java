package fr.inria.shapedesigner.model;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.junit.Test;

import fr.inria.lille.shexjava.schema.Label;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeExpr;
import fr.inria.lille.shexjava.schema.parsing.ShExCParser;

public class ShExCSerializerTest {
	ShExCSerializer serializer = new ShExCSerializer();
	ShExCParser parser = new ShExCParser();

	@Test
	public void test() {
		String schema = "<http://a.example/S1> {\n" + 
				"   &<http://a.example/S2e> |\n" + 
				"   <http://a.example/p1> .\n" + 
				"}\n" + 
				"\n" + 
				"<http://a.example/S2> {\n" + 
				"   $<http://a.example/S2e> <http://a.example/p2> .\n" + 
				"}\n" ;
		try {
			Map<Label, ShapeExpr> rules = parser.getRules(new ByteArrayInputStream(schema.getBytes()));
			System.out.println(serializer.ToShexC(rules));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
