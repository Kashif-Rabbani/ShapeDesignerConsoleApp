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
package fr.inria.shapedesigner.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.rdf.api.RDFTerm;

import fr.inria.lille.shexjava.pattern.indications.ShexFromPatternConstructor;
import fr.inria.lille.shexjava.schema.Label;
import fr.inria.lille.shexjava.schema.abstrsynt.Annotation;
import fr.inria.lille.shexjava.schema.abstrsynt.EachOf;
import fr.inria.lille.shexjava.schema.abstrsynt.EmptyShape;
import fr.inria.lille.shexjava.schema.abstrsynt.EmptyTripleExpression;
import fr.inria.lille.shexjava.schema.abstrsynt.NodeConstraint;
import fr.inria.lille.shexjava.schema.abstrsynt.OneOf;
import fr.inria.lille.shexjava.schema.abstrsynt.RepeatedTripleExpression;
import fr.inria.lille.shexjava.schema.abstrsynt.Shape;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeAnd;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeExpr;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeNot;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeOr;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleConstraint;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleExpr;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleExprRef;
import fr.inria.lille.shexjava.schema.concrsynt.Constraint;
import fr.inria.lille.shexjava.schema.concrsynt.DatatypeConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.FacetNumericConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.FacetStringConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.IRIStemConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.IRIStemRangeConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.LanguageConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.LanguageStemConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.LanguageStemRangeConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.LiteralStemConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.LiteralStemRangeConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.NodeKindConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.ValueConstraint;
import fr.inria.lille.shexjava.schema.concrsynt.ValueSetValueConstraint;
import fr.inria.lille.shexjava.util.Interval;
import fr.inria.shapedesigner.model.util.RDFPrintingUtil;

public class ShExCSerializer {
	protected int depth;
	protected String indent="    ";
	protected Map<String,String> prefixes = Collections.emptyMap();

	public String ToShexC(Map<Label,ShapeExpr> rules) {
		this.depth = 0;
		String result = "";

		for (ShapeExpr shape:rules.values())
			result += shape.getId().toPrettyString(prefixes)+" "+convertShapeExpr(shape)+"\n\n";
		return result;
	}

	
	
	
	//--------------------------------------------------
	// Shape conversion
	//--------------------------------------------------

	
	public String convertShapeExpr(ShapeExpr shape) {
		if (shape instanceof Shape)
			return convertShape((Shape) shape);
		if (shape instanceof EmptyShape)
			return ".";
		if (shape instanceof ShapeAnd)
			return convertShapeAnd((ShapeAnd) shape);
		if (shape instanceof ShapeOr)
			return convertShapeOr((ShapeOr) shape);
		if (shape instanceof ShapeNot)
			return convertShapeNot((ShapeNot) shape);
		if (shape instanceof NodeConstraint)
			return convertNodeConstraint((NodeConstraint) shape);
		return shape.toPrettyString(prefixes);
	}

	
	protected String convertShape(Shape shape) {
		String result = "{\n";
		depth+=1;
		result += convertTripleExpr(shape.getTripleExpression())+"\n";
		depth-=1;
		result += getCurrentIndent()+"}";
		return result;
	}

	
	protected String convertShapeAnd(ShapeAnd shape) {
		String result = "";
		Iterator<ShapeExpr> ite = shape.getSubExpressions().iterator();
		while (ite.hasNext()) {
			result += "("+convertShapeExpr(ite.next())+")";
			if (ite.hasNext())
				result += " AND ";
		}
		return result;
	}
	
	
	protected String convertShapeOr(ShapeOr shape) {
		String result = "";
		Iterator<ShapeExpr> ite = shape.getSubExpressions().iterator();
		while (ite.hasNext()) {
			result += "("+convertShapeExpr(ite.next())+")";
			if (ite.hasNext())
				result += " OR ";
		}
		return result;
	}
	
	
	protected String convertShapeNot(ShapeNot shape) {
		return "NOT ("+convertShapeExpr(shape.getSubExpression())+")";
	}
	
	
	
	//--------------------------------------------------
	// Triple conversion
	//--------------------------------------------------

	
	public String convertTripleExpr(TripleExpr triple) {
		if (triple instanceof EachOf)
			return convertEachOf((EachOf) triple);
		if (triple instanceof EmptyTripleExpression)
			return "";		
		if (triple instanceof OneOf)
			return convertOneOf((OneOf) triple);
		if (triple instanceof TripleExprRef)
			return convertTripleExprRef((TripleExprRef) triple);
		if (triple instanceof RepeatedTripleExpression)
			return convertRepeatedTripleExpression((RepeatedTripleExpression) triple);
		if (triple instanceof TripleConstraint)
			return convertTripleConstraint((TripleConstraint) triple);
		return null;
	}
	
	
	protected String convertRepeatedTripleExpression(RepeatedTripleExpression triple) {
		if (triple.getCardinality().equals(Interval.ONE))
			return convertTripleExpr(triple.getSubExpression());
		if (triple.getCardinality().equals(Interval.OPT))
			return convertTripleExpr(triple.getSubExpression())+" ?";
		if (triple.getCardinality().equals(Interval.PLUS))
			return convertTripleExpr(triple.getSubExpression())+" +";
		if (triple.getCardinality().equals(Interval.STAR))
			return convertTripleExpr(triple.getSubExpression())+" *";
		return convertTripleExpr(triple.getSubExpression())+"{"+triple.getCardinality().min+","+triple.getCardinality().max+"}";
	}

	
	protected String convertEachOf(EachOf triple) {
		String result = "";
		Iterator<TripleExpr> ite = triple.getSubExpressions().iterator();
		while(ite.hasNext()) {
			result += convertTripleExpr(ite.next());
			if (ite.hasNext())
				result += " ;\n";
		}
		
		return result;
	}
	

	protected String convertOneOf(OneOf triple) {
		String result = "";
		Iterator<TripleExpr> ite = triple.getSubExpressions().iterator();
		while(ite.hasNext()) {
			result += convertTripleExpr(ite.next());
			if (ite.hasNext())
				result += " |\n";
		}
		return result;
	}


	protected String convertTripleExprRef(TripleExprRef triple) {
		String result = "";
		result += getCurrentIndent();
		if (triple.getId()!=null && !triple.getId().isGenerated()) result += "$"+triple.getId().toPrettyString()+" ";
		result += "&"+triple.getLabel().toPrettyString(prefixes); 
		return result;
	}


	protected String convertTripleConstraint(TripleConstraint triple) {
		String result = "";
		result += getCurrentIndent();
		Optional<Annotation> freq = triple.getAnnotations().stream()
				.filter(a -> a.getPredicate().equals(ShexFromPatternConstructor.FREQ_ANNOTATION)).findFirst();
		if (freq.isPresent()) {
			String s = freq.get().getObjectValue().toString();
			result+= "# frequency: " + s.substring(1, s.length()-1)+"\n";
			result += getCurrentIndent();
		}
		if (triple.getId()!=null && !triple.getId().isGenerated()) result += "$"+triple.getId().toPrettyString()+" ";
		result += triple.getProperty().toPrettyString(prefixes)+" "+convertShapeExpr(triple.getShapeExpr()); 
		return result;
	}

	//--------------------------------------------------
	// Constraint conversion
	//--------------------------------------------------

	protected String convertNodeConstraint(NodeConstraint shape) {
		String result = "";
		Iterator<Constraint> ite = shape.getConstraints().iterator();

		while(ite.hasNext()) {
			result += convertConstraint(ite.next());
			if (ite.hasNext())
				result += " ";
		}
		
		return result;
	}
	
	protected String convertConstraint(Constraint constraint) {
		String result= "";
		if (constraint.equals(NodeKindConstraint.BNodeKind))
			result += "BNODE";
		if (constraint.equals(NodeKindConstraint.IRIKind))
			result += "IRI";
		if (constraint.equals(NodeKindConstraint.LiteralKind))
			result += "LITERAL";
		if (constraint.equals(NodeKindConstraint.NonLiteralKind))
			result += "NONLITERAL";
		if (constraint instanceof DatatypeConstraint) 
			result += RDFPrintingUtil.toPrettyString(((DatatypeConstraint) constraint).getDatatypeIri(),prefixes);
		if (constraint instanceof FacetNumericConstraint)
			result += convertNumericFacet((FacetNumericConstraint) constraint);
		if (constraint instanceof FacetStringConstraint)
			result += convertStringFacet((FacetStringConstraint) constraint);
		if (constraint instanceof ValueSetValueConstraint)
			result += convertValueSetValueConstraint((ValueSetValueConstraint) constraint);
		return result;
	}
	
	protected String convertNumericFacet(FacetNumericConstraint facet) {
		if (facet.getMinincl() != null)
			return "MININCLUSIVE " + facet.getMinincl();
		if (facet.getMinexcl() != null)
			return "MINEXCLUSIVE " + facet.getMinincl();
		if (facet.getMaxincl() != null)
			return "MAXINCLUSIVE " + facet.getMinincl();
		if (facet.getMaxexcl() != null)
			return "MINEXCLUSIVE " + facet.getMinincl();
		if (facet.getTotalDigits() != null)
			return "TOTALDIGITS " + facet.getMinincl();
		if (facet.getFractionDigits() != null)
			return "FRACTIONDIGITS " + facet.getMinincl();
		return "";
	}

	protected String convertStringFacet(FacetStringConstraint facet) {
		if (facet.getLength()!=null)
			return "LENGTH " + facet.getLength();
		if (facet.getMinlength()!=null)
			return "MINLENGTH " + facet.getLength();
		if (facet.getMaxlength()!=null)
			return "MAXLENGTH " + facet.getLength();
		if (facet.getPatternString()!=null)
			return "PATTERN " + facet.getLength();
		if (facet.getFlags()!=null)
			return "FLAGS " + facet.getLength();
		return "";
	}

	protected String convertValueSetValueConstraint(ValueSetValueConstraint constraint) {
		String result = "[ ";
		Iterator<RDFTerm> ite2 = constraint.getExplicitValues().iterator();
		while (ite2.hasNext()) {	
			result += convertValue(ite2.next());
			if (ite2.hasNext())
				result += " "; 				
		}

		Iterator<ValueConstraint> ite = constraint.getConstraintsValue().iterator();
		while (ite.hasNext()) {
			result += convertValueConstraint(ite.next());
			if (ite.hasNext())
				result += " ";			
		}

		return result+" ]";
	}
	
	protected String convertValueConstraint(ValueConstraint constraint) {
		String result= "";
		if (constraint instanceof LanguageConstraint)
			result += convertLanguageConstraint((LanguageConstraint) constraint);
		if (constraint instanceof LanguageStemConstraint)
			result += convertLanguageStemConstraint((LanguageStemConstraint) constraint);
		if (constraint instanceof LanguageStemRangeConstraint)
			result += convertLanguageStemRangeConstraint((LanguageStemRangeConstraint) constraint);
		if (constraint instanceof IRIStemConstraint)
			result += convertIRIStemConstraint((IRIStemConstraint) constraint);
		if (constraint instanceof IRIStemRangeConstraint)
			result += convertIRIStemRangeConstraint((IRIStemRangeConstraint) constraint);
		if (constraint instanceof LiteralStemConstraint)
			result += convertLiteralStemConstraint((LiteralStemConstraint) constraint);
		if (constraint instanceof LiteralStemRangeConstraint)
			result += convertLiteralStemRangeConstraint((LiteralStemRangeConstraint) constraint);
		return result;
	}

	protected String convertLanguageConstraint(LanguageConstraint cons) {
		return "@"+cons.getLangTag();
	}

	protected String convertLanguageStemConstraint(LanguageStemConstraint cons) {
		return "@"+cons.getLangStem()+"~";
	}

	protected String convertLanguageStemRangeConstraint(LanguageStemRangeConstraint cons) {
//		Map<String,Object> result = new LinkedHashMap<>();
//		result.put("type", "LanguageStemRange");
//		if (!(cons.getStem() instanceof WildcardConstraint))
//			result.put("stem", ((LanguageStemConstraint) cons.getStem()).getLangStem());
//		else {
//			Map<String,Object> tmp = new LinkedHashMap<>();
//			tmp.put("type", "Wildcard");
//			result.put("stem",tmp);
//		}
//		List<Object> exclusions = (List) convertValueSetValueConstraint(cons.getExclusions());
//		if (exclusions.size()>0)
//			result.put("exclusions", exclusions);
//		return result;
		return null;
	}

	protected String convertIRIStemConstraint(IRIStemConstraint cons) {
		return "<"+cons.getIriStem()+">~";
	}

	protected String convertIRIStemRangeConstraint(IRIStemRangeConstraint cons) {
//		Map<String,Object> result = new LinkedHashMap<>();
//		result.put("type", "IriStemRange");
//		if (!(cons.getStem() instanceof WildcardConstraint))
//			result.put("stem", ((IRIStemConstraint) cons.getStem()).getIriStem());
//		else {
//			Map<String,Object> tmp = new LinkedHashMap<>();
//			tmp.put("type", "Wildcard");
//			result.put("stem",tmp);
//		}
//		List<Object> exclusions = (List) convertValueSetValueConstraint(cons.getExclusions());
//		if (exclusions.size()>0)
//			result.put("exclusions", exclusions);
//		return result;
		return null;
	}

	protected String convertLiteralStemConstraint(LiteralStemConstraint cons) {
		return "\""+cons.getLitStem()+"\"~";
	}

	protected String convertLiteralStemRangeConstraint(LiteralStemRangeConstraint cons) {
//		Map<String,Object> result = new LinkedHashMap<>();
//		result.put("type", "LiteralStemRange");
//		if (!(cons.getStem() instanceof WildcardConstraint))
//			result.put("stem", ((LiteralStemConstraint) cons.getStem()).getLitStem());
//		else {
//			Map<String,Object> tmp = new LinkedHashMap<>();
//			tmp.put("type", "Wildcard");
//			result.put("stem",tmp);
//		}
//		List<Object> exclusions = (List) convertValueSetValueConstraint(cons.getExclusions());
//		if (exclusions.size()>0)
//			result.put("exclusions", exclusions);
//		return result;
		return null;
	}

	//--------------------------------------------------
	// UTIL
	//--------------------------------------------------

	protected String convertValue(RDFTerm v) {
		return RDFPrintingUtil.toPrettyString(v,prefixes);
	}

	protected String getCurrentIndent() {
		String res = "";
		for (int i =0;i<depth;i++)
			res+=this.indent;
		return res;
	}


	public void setPrefixes(Map<String, String> prefixes) {
		this.prefixes = prefixes;
	}


}
