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

package fr.inria.shapedesigner.model.validationShEx;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;

import fr.inria.lille.shexjava.schema.abstrsynt.NodeConstraint;
import fr.inria.lille.shexjava.schema.abstrsynt.RepeatedTripleExpression;
import fr.inria.lille.shexjava.schema.abstrsynt.Shape;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeAnd;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeExpr;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeExprRef;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeOr;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleConstraint;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleExpr;
import fr.inria.shapedesigner.model.util.RDFPrintingUtil;

/**
 * @author Iovka Boneva
 * 1 août 2018
 */
public interface MatchingElement {
	public String getExprType();
	public String getExprDescr();
	public String getTriplePredicate();
	public String getTripleObject ();
	public boolean getIsComputed();
	public boolean getIsError();
	public String getErrorMessage ();
	

	class BaseMatchingElement implements MatchingElement {

		protected String exprType;
		protected String exprDescr;
		protected String triplePredicate;
		protected String tripleObject;
		protected boolean isError;
		protected boolean isComputed;
		protected String errorMessage;
		
		
		protected BaseMatchingElement (boolean isComputed, boolean isError, String... errorMessage) {
			exprType = "";
			exprDescr = "";
			triplePredicate = "";
			tripleObject = "";
			this.isComputed = isComputed;
			setIsError(isError, errorMessage);
		}

		public final String getExprType() {
			return exprType;
		}

		public final String getExprDescr() {
			return exprDescr;
		}

		public final String getTriplePredicate() {
			return triplePredicate;
		}

		public final String getTripleObject () {
			return tripleObject;
		}
		
		public final boolean getIsError () {
			return isError;
		}
		
		public final String getErrorMessage () {
			return errorMessage;
		}

		protected final void setIsError (boolean isError, String ... errorMessage) {
			this.isError = isError;
			if (isError && errorMessage.length>0) 
				this.errorMessage = errorMessage[0];
		}

		@Override
		public boolean getIsComputed() {
			return isComputed;
		}

	}
	
	class BaseMatchingElementTE extends BaseMatchingElement {	
		protected Triple triple;
		protected TripleConstraint tripleConstraint;
		Set<ShapeExpr> investigations;

		protected BaseMatchingElementTE (boolean isComputed, boolean isError, String... errorMessage) {
			super(isComputed,isError,errorMessage);
			investigations = new HashSet<>();
		}

		public final Triple getTriple() {
			return triple;
		}

		public final TripleConstraint getTripleConstraint() {
			return tripleConstraint;
		}
		
		protected final void setPropertiesForTriple (Triple triple, Map<String,String> prefixes) {
			if (triple != null) {
				this.triple = triple;
				triplePredicate = RDFPrintingUtil.toPrettyString(triple.getPredicate(), prefixes);
				tripleObject = RDFPrintingUtil.toPrettyString(triple.getObject(), prefixes);
			}
		}

		protected final void setPropertiesForTripleExpr (TripleExpr expr, Map<String,String> prefixes) {
			if (expr != null) {
				exprType = expr.getClass().getSimpleName();
				if (expr instanceof TripleConstraint) {
					TripleConstraint tc = (TripleConstraint) expr;
					this.tripleConstraint = tc;
					exprDescr = tc.toPrettyString(prefixes);
					investigations.add(tc.getShapeExpr());
				} else if (expr instanceof RepeatedTripleExpression) {
					RepeatedTripleExpression re = (RepeatedTripleExpression) expr;
					exprDescr = re.getCardinality().toString();
				}
			}
		}


		

		protected final void setInvestigations(Set<ShapeExpr> investigations) {
			this.investigations = investigations;
		}
		
		public Set<ShapeExpr> getInvestigations(){
			return investigations;
		}

	}
	
	class BaseMatchingElementSE extends BaseMatchingElement{

		protected BaseMatchingElementSE(boolean isComputed, boolean isError, String... errorMessage) {
			super(isComputed,isError, errorMessage);
		}
		
	}
	
	class RootMatchingElement extends BaseMatchingElement {
		protected RootMatchingElement(ShapeExpr shape, RDFTerm node, Map<String,String> prefixes) {
			super(false,false, "");
			this.exprType = "( "+RDFPrintingUtil.toPrettyString(node, prefixes)+" , "+shape.getId().toPrettyString(prefixes)+" )";
		}
	}

	// --------------------------------------------------------------------------------
	// Hierarchy of classes representing the different situations for shape expressions
	// --------------------------------------------------------------------------------	
	
	public class MatchedSEAnd extends BaseMatchingElementSE {
		public MatchedSEAnd(ShapeAnd se,boolean isComputed, boolean isError, Map<String,String> prefixes) {
			super(isComputed, isError);
			this.exprType = "ShapeAnd with label "+se.getId().toPrettyString(prefixes);
		}
	}
	
	public class MatchedSEOr extends BaseMatchingElementSE {
		public MatchedSEOr(ShapeOr se, boolean isComputed, boolean isError, Map<String,String> prefixes) {
			super(isComputed,isError);
			this.exprType = "ShapeOr with label "+se.getId().toPrettyString(prefixes);
		}
	}
	
	public class MatchedSEShape extends BaseMatchingElementSE {
		public MatchedSEShape(Shape se, boolean isComputed, boolean isError, Map<String,String> prefixes) {
			super(isComputed,isError);
			this.exprType = "Shape with label "+se.getId().toPrettyString(prefixes);
		}
	}
	
	public class MatchedSENodeConstraint extends BaseMatchingElementSE {
		public MatchedSENodeConstraint(NodeConstraint se, boolean isComputed, boolean isError, Map<String,String> prefixes) {
			super(isComputed,isError);
			this.exprType = "NodeConstraint with label "+se.getId();
			this.exprDescr = se.toPrettyString(prefixes).trim();
		}
	}
	
	public class MatchedSEShapeExprRef extends BaseMatchingElementSE {
		public MatchedSEShapeExprRef(ShapeExprRef se, boolean isComputed, boolean isError, Map<String,String> prefixes) {
			super(isComputed,isError);
			this.exprType = "ShapeExprRef with label "+se.getId().toPrettyString(prefixes);
		}
	}
	
	// --------------------------------------------------------------------------------
	// Hierarchy of classes representing the different situations for triple expressions
	// --------------------------------------------------------------------------------

	class MatchedTC extends BaseMatchingElementTE {
		protected final TripleConstraint tc;
		protected final Triple triple;
		protected MatchedTC(boolean first, TripleConstraint tc, Triple triple, boolean isComputed, boolean isError, Map<String,String> prefixes, String ... errorMessage) {
			super(isComputed, isError, errorMessage);
			this.tc = tc;
			this.triple = triple;
			super.setPropertiesForTriple(triple,prefixes);
			if (first)
				super.setPropertiesForTripleExpr(tc,prefixes);
		}
	}

	public class UnmatchedTC extends BaseMatchingElementTE {
		public UnmatchedTC(TripleExpr texpr, boolean isComputed, boolean isError, Map<String,String> prefixes, String ... errorMessage) {
			super(isComputed, isError, errorMessage);
			super.setPropertiesForTripleExpr(texpr,prefixes);
		}
	}
	
	public class UnmatchedOtherThanTC extends BaseMatchingElementTE {
		public UnmatchedOtherThanTC(TripleExpr texpr, boolean isComputed, boolean isError, Map<String,String> prefixes, String ... errorMessage) {
			super(isComputed, isError, errorMessage);
			super.setPropertiesForTripleExpr(texpr,prefixes);
		}
	}
	
	abstract class BaseExtra extends BaseMatchingElementTE{
		protected BaseExtra(boolean isComputed, boolean isError, String... errorMessage) {
			super(isComputed, isError, errorMessage);
		}
		
		protected void setPropertiesForExtra(IRI extraIRI) {
			exprType = "EXTRA";
			exprDescr = extraIRI.getIRIString();
		}
	}

	class MatchedExtra extends BaseExtra {
		protected final IRI iri;
		protected final Triple triple;
		protected MatchedExtra(boolean first, IRI iri, Triple triple, boolean isComputed, boolean isError, Map<String,String> prefixes, String ... errorMessage) {
			super(isComputed, isError, errorMessage);
			this.iri = iri;
			this.triple = triple;
			super.setPropertiesForTriple(triple,prefixes);
			if (first) {
				super.setPropertiesForExtra(iri);
			}
		}
	}

	public class UnmatchedExtra extends BaseExtra {
		public UnmatchedExtra(IRI iri, boolean isComputed, boolean isError, String ... errorMessage) {
			super(isComputed, isError, errorMessage);
			super.setPropertiesForExtra(iri);
		}
	}

	class UnmatchedTriple extends BaseMatchingElementTE {
		public UnmatchedTriple(boolean first, Triple triple, Set<ShapeExpr> investigations, boolean isComputed, boolean isError, Map<String,String> prefixes, String ... errorMessage) {
			super(isComputed, isError, errorMessage);
			super.setPropertiesForTriple(triple,prefixes);
			super.investigations.addAll(investigations);
			if (first) {
				exprType = "OTHER TRIPLES";
			}
		}		
	}
	
}

