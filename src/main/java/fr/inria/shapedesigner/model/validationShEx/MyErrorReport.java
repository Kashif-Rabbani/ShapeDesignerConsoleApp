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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.rdf.api.Triple;

import fr.inria.lille.shexjava.schema.abstrsynt.EachOf;
import fr.inria.lille.shexjava.schema.abstrsynt.RepeatedTripleExpression;
import fr.inria.lille.shexjava.schema.abstrsynt.Shape;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleConstraint;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleExpr;
import fr.inria.lille.shexjava.util.Interval;
import fr.inria.lille.shexjava.util.Pair;
import fr.inria.lille.shexjava.validation.PreMatching;
import fr.inria.lille.shexjava.validation.ValidationUtils;

/** Constructs an error report for a node, a shape and a typing, when the node does not match the shape.
 * 
 * @author Iovka Boneva
 * 6 août 2018
 */
public class MyErrorReport {

	public enum ErrorType {
		UNMACHED_TRIPLE("Triple didn't match any triple consraint or extra."),
		WRONG_CARDINALITY_TC("The number of matched triples does not satisfy the required cardinality.");
		
		private String message;
		
		private ErrorType (String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
		
		@Override
		public String toString() {
			return message;
		}
	}

	/** Annotates triples and triple expressions with the corresponding error. */
	private Map<Object, ErrorType> report;
	
	public MyErrorReport(){
		report = new HashMap<>();
	}
	
	public void populateReports (PreMatching predOnlyMatching,PreMatching predAndValueMatching, Shape shape, List<TripleConstraint> tripleConstraints) {
		Map<TripleConstraint, Interval> cardinalityMap = new HashMap<>();
		if (! isSimpleShape(shape, cardinalityMap)) {
			return ;
		}
		
		// Mark the unmatched triples
		for (Triple triple: predAndValueMatching.getUnmatched()) 
			if (predOnlyMatching.getPreMatching().containsKey(triple))
				report.put(triple, ErrorType.UNMACHED_TRIPLE);
			
		// Mark the triple constraints that do not have the right number of matching triples
		Map<TripleConstraint, List<Triple>> inversePreMatching = 
				ValidationUtils.inversePreMatching(predAndValueMatching.getPreMatching(), tripleConstraints);
		for (Map.Entry<TripleConstraint, List<Triple>> e : inversePreMatching.entrySet()) {
			if (! cardinalityMap.get(e.getKey()).contains(e.getValue().size()))
				report.put(e.getKey(), ErrorType.WRONG_CARDINALITY_TC);
		}
	}
	
	/** Returns a map that annotates unmatched triples and triple constraints with wrong cardinality. 
	 * @see ErrorType 
	 */
	public Map<Object, ErrorType> getReport () {
		return report;
	}
	
	/** Checks whether the schema is simple and if it is the case, fills the cardinality map with the cardinality of every triple constraint. */
	private boolean isSimpleShape (Shape shape, Map<TripleConstraint, Interval> cardinalityMap) {
		boolean result = true;
		TripleExpr expr = shape.getTripleExpression();
		if (! (expr instanceof EachOf))
			return false;
		for (TripleExpr e : ((EachOf) expr).getSubExpressions()) {
			Pair<TripleConstraint, Interval> p = extractTripleConstraintAndCardinality(e);
			if (p == null) {
				result = false;
				break;
			}
			else 
				cardinalityMap.put(p.one, p.two);
		}
		if (! result)
			cardinalityMap.clear();
		return result;
	}
	
	
	/** If expr is a triple constraint or a repeated triple expression over a triple constraint, returns the triple constraint and its cardinality.
	 * Otherwise returns null.
	 * 
	 * @param expr
	 * @return
	 */
	private Pair<TripleConstraint, Interval> extractTripleConstraintAndCardinality (TripleExpr expr) {
		if (expr instanceof TripleConstraint)
			return new Pair<>((TripleConstraint) expr, Interval.ONE);
		if (expr instanceof RepeatedTripleExpression) {
			TripleExpr subExpr = ((RepeatedTripleExpression) expr).getSubExpression();
			if (subExpr instanceof TripleConstraint)
				return new Pair<>((TripleConstraint) subExpr, ((RepeatedTripleExpression)expr).getCardinality());
		}
		return null;
	}
}
