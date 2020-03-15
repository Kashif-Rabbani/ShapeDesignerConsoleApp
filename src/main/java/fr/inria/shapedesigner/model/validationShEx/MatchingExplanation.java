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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;

import fr.inria.lille.shexjava.schema.Label;
import fr.inria.lille.shexjava.schema.abstrsynt.EachOf;
import fr.inria.lille.shexjava.schema.abstrsynt.EmptyTripleExpression;
import fr.inria.lille.shexjava.schema.abstrsynt.NodeConstraint;
import fr.inria.lille.shexjava.schema.abstrsynt.OneOf;
import fr.inria.lille.shexjava.schema.abstrsynt.RepeatedTripleExpression;
import fr.inria.lille.shexjava.schema.abstrsynt.Shape;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeAnd;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeExpr;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeExprRef;
import fr.inria.lille.shexjava.schema.abstrsynt.ShapeOr;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleConstraint;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleExpr;
import fr.inria.lille.shexjava.schema.abstrsynt.TripleExprRef;
import fr.inria.lille.shexjava.schema.analysis.ShapeExpressionVisitor;
import fr.inria.lille.shexjava.schema.analysis.TripleExpressionVisitor;
import fr.inria.lille.shexjava.util.CollectionToString;
import fr.inria.lille.shexjava.util.CommonGraph;
import fr.inria.lille.shexjava.util.Pair;
import fr.inria.lille.shexjava.validation.DynamicCollectorOfTripleConstraints;
import fr.inria.lille.shexjava.validation.PreMatching;
import fr.inria.lille.shexjava.validation.Status;
import fr.inria.lille.shexjava.validation.ValidationUtils;
import fr.inria.shapedesigner.model.util.RDFPrintingUtil;
import fr.inria.shapedesigner.model.validationShEx.MyErrorReport.ErrorType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

/** 
 * 
 * @author Iovka Boneva
 * 31 juil. 2018
 */
public class MatchingExplanation {
	private static DynamicCollectorOfTripleConstraints tcCollector = new DynamicCollectorOfTripleConstraints();
	 
	private ValidationShEx validation; 
	private RDFTerm node;
	private ShapeExpr shape;
	private boolean result;
	private MyErrorReport report = null;

	
	public MatchingExplanation (ValidationShEx validation, RDFTerm node, ShapeExpr shape) {
		this.shape = shape;
		this.node = node;
		this.validation = validation;
		result = validation.getValidationAlgorithm().getTyping().isConformant(node, shape.getId());
		report = new MyErrorReport();
		if (!result) 
			createTree(); // find the errors during the creation of the tree
	}
		
	
	////--------------------------------------------------------------------------
	//// Deal with the result display
	////--------------------------------------------------------------------------
		
	public boolean isResult() {
		return result;
	}
	
	public RDFTerm getNode() {
		return node;
	}
	
	public ShapeExpr getShape() {
		return shape;
	}
	
	public Label getLabel() {
		return shape.getId();
	}
	
	public StringProperty getMessageString() {
		if (report == null)
			return new SimpleStringProperty("");
		String result = CollectionToString.collectionToString(new HashSet<>(report.getReport().values()),
				"", "[", "]");
		return new SimpleStringProperty(result);
	}	
		
	public StringProperty getResultString() {
		return new SimpleStringProperty(result+"");
	}
	
	public StringProperty getShapeString() {
		return new SimpleStringProperty(shape.getId().toPrettyString(validation.getPrefixes())+"");
	}
		
	public StringProperty getNodeString() {
		return new SimpleStringProperty(RDFPrintingUtil.toPrettyString(node,validation.getPrefixes()));
	}
	
	
	////--------------------------------------------------------------------------
	//// Create tree
	////--------------------------------------------------------------------------
		
	public TreeItem<MatchingElement> createTree (){
		ShapeExprTreeCreator creator = new ShapeExprTreeCreator();
		this.shape.accept(creator);
		TreeItem<MatchingElement> root = new TreeItem<>(new MatchingElement.RootMatchingElement(shape, node, validation.getPrefixes()));
		root.getChildren().add(creator.getResult());
		root.setExpanded(true);
		return root;
	}
	
	private class ShapeExprTreeCreator extends ShapeExpressionVisitor<TreeItem<MatchingElement>> {
		private TreeItem<MatchingElement> resultTree;
		
		@Override
		public TreeItem<MatchingElement> getResult() {
			return resultTree;
		}
		
		@Override
		public void visitShapeOr(ShapeOr expr, Object... arguments) {
			TreeItem<MatchingElement> root = new TreeItem<>(new MatchingElement.MatchedSEOr(expr, true, false, validation.getPrefixes()));
			for (ShapeExpr te: expr.getSubExpressions()) {
				te.accept(this, arguments);
				root.getChildren().add(getResult());
			}
			root.setExpanded(true);
			resultTree = root;
		}
		
		@Override
		public void visitShapeAnd(ShapeAnd expr, Object... arguments) {
			TreeItem<MatchingElement> root = new TreeItem<>(new MatchingElement.MatchedSEAnd(expr, true, false, validation.getPrefixes()));
			for (ShapeExpr te: expr.getSubExpressions()) {
				te.accept(this, arguments);
				root.getChildren().add(getResult());
			}
			root.setExpanded(true);
			resultTree = root;
		}

		@Override
		public void visitShape(Shape expr, Object... arguments) {
			List<TripleConstraint> tripleConstraints = tcCollector.getTCs(expr.getTripleExpression());
			List<Triple> neighbourhood = getWholeNeighbourhood(node, expr, validation.getDataGraph(), tripleConstraints);

			PreMatching prePredOnlyMatching = ValidationUtils.computePreMatching(
					node, neighbourhood, tripleConstraints, expr.getExtraProperties(), 
					ValidationUtils.getPredicateOnlyMatcher());
			
			PreMatching prePredValueMatching = ValidationUtils.computePreMatching(
					node, neighbourhood, tripleConstraints, expr.getExtraProperties(), 
					ValidationUtils.getPredicateAndValueMatcher(validation.getValidationAlgorithm().getTyping()));
			
			Map<TripleConstraint, List<Triple>> inverseMatching;
			List<Triple> matchedToExtra;			
			List<Triple> unmatchedTriples;	
			if (result) {
				inverseMatching = ValidationUtils.inversePreMatching(prePredValueMatching.getPreMatching(), tripleConstraints);
				matchedToExtra = prePredValueMatching.getMatchedToExtra();			
				unmatchedTriples = getAllUnmatchedTriples(neighbourhood, matchedToExtra, prePredValueMatching.getPreMatching().keySet());	
			} else {
				report.populateReports(prePredOnlyMatching,prePredValueMatching,expr,tripleConstraints);

				inverseMatching = ValidationUtils.inversePreMatching(prePredOnlyMatching.getPreMatching(), tripleConstraints);
				matchedToExtra = prePredOnlyMatching.getMatchedToExtra();			
				unmatchedTriples = getAllUnmatchedTriples(neighbourhood, matchedToExtra, prePredOnlyMatching.getPreMatching().keySet());
			}
						
			resultTree = new TreeItem<>(new MatchingElement.MatchedSEShape(expr, true, false, validation.getPrefixes())); // The name of the shape
			
			TripleExprTreeCreator c = new TripleExprTreeCreator(inverseMatching);
			expr.getTripleExpression().accept(c);
			resultTree.getChildren().add(c.getResult());
			
			TreeItem<MatchingElement> extraTree = createExtraTree(matchedToExtra);
			if (extraTree != null)
				resultTree.getChildren().add(extraTree);
			
			TreeItem<MatchingElement> unmatchedTree = createUnmatchedTree(unmatchedTriples,prePredOnlyMatching);
			if (unmatchedTree != null)
				resultTree.getChildren().add(unmatchedTree);
			
			resultTree.setExpanded(true);
		}

		@Override
		public void visitNodeConstraint(NodeConstraint expr, Object... arguments) {

			resultTree = new TreeItem<>(new MatchingElement.MatchedSENodeConstraint(expr, true, false, validation.getPrefixes()));
			resultTree.setExpanded(true);
		}

		@Override
		public void visitShapeExprRef(ShapeExprRef shapeRef, Object[] arguments) {
			TreeItem<MatchingElement> root = new TreeItem<>(new MatchingElement.MatchedSEShapeExprRef(shapeRef,true, false, validation.getPrefixes()));
			shapeRef.getShapeDefinition().accept(this, arguments);
			root.getChildren().add(getResult());
			root.setExpanded(true);
			resultTree = root;
		}

	}
	
	private class TripleExprTreeCreator extends TripleExpressionVisitor<TreeItem<MatchingElement>> {
		private TreeItem<MatchingElement> result;
		private Map<TripleConstraint, List<Triple>> inverseMatching;
		
		public TripleExprTreeCreator(Map<TripleConstraint, List<Triple>> inverseMatching) {
			this.inverseMatching = inverseMatching;
		}
		
		@Override
		public TreeItem<MatchingElement> getResult() {
			return result;
		}

		@Override
		public void visitTripleConstraint(TripleConstraint tc, Object... arguments) {
				List<Triple> triples = inverseMatching.get(tc);
				TreeItem<MatchingElement> root;
				Pair<Boolean, String> errorAndMessage = getErrorAndErrorMessage(tc);

				if (triples.isEmpty())
					root = new TreeItem<>(new MatchingElement.UnmatchedTC(tc, true, errorAndMessage.one, 
							validation.getPrefixes(), errorAndMessage.two));
				else { 
					errorAndMessage = getErrorAndErrorMessage(tc);
					boolean isComputed = !validation.getValidationAlgorithm().getTyping().getStatus(triples.get(0).getObject(), tc.getShapeExpr().getId()).equals(Status.NOTCOMPUTED);
					boolean isConformant = !validation.getValidationAlgorithm().getTyping().getStatus(triples.get(0).getObject(), tc.getShapeExpr().getId()).equals(Status.NONCONFORMANT);
					root = new TreeItem<>(new MatchingElement.MatchedTC(true, tc, triples.get(0), isComputed, !isConformant || errorAndMessage.one, 
							validation.getPrefixes(), errorAndMessage.two));
					
					for (int i = 1; i < triples.size(); i++) {
						isComputed = !validation.getValidationAlgorithm().getTyping().getStatus(triples.get(i).getObject(), tc.getShapeExpr().getId()).equals(Status.NOTCOMPUTED);
						isConformant = !validation.getValidationAlgorithm().getTyping().getStatus(triples.get(i).getObject(), tc.getShapeExpr().getId()).equals(Status.NONCONFORMANT);
						root.getChildren().add(new TreeItem<>(new MatchingElement.MatchedTC(false, tc, triples.get(i), isComputed, 
								!isConformant || errorAndMessage.one, validation.getPrefixes())));
					}
				}
				root.setExpanded(true);
				this.result = root;
		}
		
		@Override
		public void visitEachOf(EachOf expr, Object... arguments) {
			TreeItem<MatchingElement> root = new TreeItem<>(new MatchingElement.UnmatchedOtherThanTC(expr, true, false, validation.getPrefixes()));
			for (TripleExpr te: expr.getSubExpressions()) {
				te.accept(this, arguments);
				root.getChildren().add(getResult());
			}
			root.setExpanded(true);
			result = root;
		}

		@Override
		public void visitOneOf(OneOf expr, Object... arguments) {
			TreeItem<MatchingElement> root = new TreeItem<>(new MatchingElement.UnmatchedOtherThanTC(expr, true, false, validation.getPrefixes()));
			for (TripleExpr te: expr.getSubExpressions()) {
				te.accept(this, arguments);
				root.getChildren().add(getResult());
			}
			root.setExpanded(true);
			result = root;
		}

		@Override
		public void visitRepeated(RepeatedTripleExpression expr, Object[] arguments) {
			TreeItem<MatchingElement> root = new TreeItem<>(new MatchingElement.UnmatchedOtherThanTC(expr, true, false, validation.getPrefixes()));
			expr.getSubExpression().accept(this, arguments);
			root.getChildren().add(getResult());
			root.setExpanded(true);
			result = root;
		}

		@Override
		public void visitTripleExprReference(TripleExprRef expr, Object... arguments) {
			throw new UnsupportedOperationException("not yet implemented");
		}

		@Override
		public void visitEmpty(EmptyTripleExpression expr, Object[] arguments) {
			throw new UnsupportedOperationException("not yet implemented");
		}
		
	}
	
	private TreeItem<MatchingElement> createExtraTree (List<Triple> matchedToExtra) {
		if (matchedToExtra.isEmpty())
			return null;
		
		TreeItem<MatchingElement> root = null;
		for (Triple triple:matchedToExtra) {
			if (root == null)
				root = new TreeItem<>(new MatchingElement.MatchedExtra(true, triple.getPredicate(), 
						triple, true, false, validation.getPrefixes()));
			else
				root.getChildren().add(new TreeItem<>(new MatchingElement.MatchedExtra(false, 
						triple.getPredicate(), triple, true, false, validation.getPrefixes())));
		}
		root.setExpanded(true);

		return root;
	}
	
	private TreeItem<MatchingElement> createUnmatchedTree (List<Triple> unmatchedTriples,PreMatching prePredOnlyMatching) {
		if (unmatchedTriples.isEmpty())
			return null;
		
		TreeItem<MatchingElement> root = null;
		for (Triple triple:unmatchedTriples) {
			Pair<Boolean, String> errorAndMessage = getErrorAndErrorMessage(triple);
			Set<ShapeExpr> investigations = getInvestigations(prePredOnlyMatching.getPreMatching().get(triple));
			if (root == null)
				root = new TreeItem<>(new MatchingElement.UnmatchedTriple(true, triple, investigations, true,
						errorAndMessage.one, validation.getPrefixes(), errorAndMessage.two));
			else
				root.getChildren().add(new TreeItem<>(new MatchingElement.UnmatchedTriple(false, triple,
						investigations, true, errorAndMessage.one, validation.getPrefixes(), errorAndMessage.two)));
		}
		root.setExpanded(true);
		
		return root;
	}
	
	
	////--------------------------------------------------------------------------
	//// Utils
	////--------------------------------------------------------------------------

	private Set<ShapeExpr> getInvestigations(List<TripleConstraint> options){
		if (options==null)
			return Collections.emptySet();
		Set<ShapeExpr> result = new HashSet<>();
		for (TripleConstraint tc:options)
			if (tc.getShapeExpr() instanceof ShapeExprRef)
				result.add(((ShapeExprRef) tc.getShapeExpr()).getShapeDefinition());
			else
				result.add(tc.getShapeExpr());
		return result;
	}
	
	
	private List<Triple> getWholeNeighbourhood (RDFTerm node, Shape shape, Graph graph, List<TripleConstraint> tripleConstraints) {	
		List<Triple> result = new ArrayList<>();
		result.addAll(CommonGraph.getOutNeighbours(graph, node));	
		Set<IRI> backwardPredicates = tripleConstraints.stream().filter(tc -> ! tc.getProperty().isForward())
															.map(tc -> tc.getProperty().getIri())
															.collect(Collectors.toSet());
		result.addAll(CommonGraph.getInNeighboursWithPredicate(graph, node, backwardPredicates));
		return result;
	}
	
	private Pair<Boolean, String> getErrorAndErrorMessage (Object o) {
		if (report == null)
			return new Pair<>(false, null);
		ErrorType error = report.getReport().get(o);
		boolean isError = error != null;
		String errorMessage = null;
		if (isError) 
			errorMessage = error.getMessage();
		return new Pair<>(isError, errorMessage);
	}
		
	private List<Triple> getAllUnmatchedTriples (List<Triple> neighbourhood, 
			List<Triple> matchedToExtra, 
			Set<Triple> matchedTriples) {
		List<Triple> result = new ArrayList<>(neighbourhood);
		result.removeAll(matchedToExtra);
		result.removeAll(matchedTriples);
		return result;
	}
	
}
