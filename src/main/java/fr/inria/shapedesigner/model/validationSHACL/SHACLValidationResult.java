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
package fr.inria.shapedesigner.model.validationSHACL;

import java.util.List;

import org.apache.commons.rdf.api.RDFTerm;

public class SHACLValidationResult {
	protected RDFTerm sourceShape;
	protected RDFTerm sourceConstraintComponent;
	protected RDFTerm constraintValue;
	protected RDFTerm focusNode;
	protected RDFTerm resultPath;
	protected List<RDFTerm> neighborhood;
	
	public SHACLValidationResult( RDFTerm sourceShape, RDFTerm sourceConstraintComponent, RDFTerm constraintValue, 
			RDFTerm focusNode, RDFTerm resultPath,  List<RDFTerm> neighborhood) {
		super();
		this.sourceShape = sourceShape;
		this.sourceConstraintComponent = sourceConstraintComponent;
		this.constraintValue = constraintValue;
		this.focusNode = focusNode;
		this.resultPath = resultPath;
		this.neighborhood = neighborhood;
	}
	
	public RDFTerm getSourceShape() {
		return sourceShape;
	}

	public RDFTerm getSourceConstraintComponent() {
		return sourceConstraintComponent;
	}
	
	public RDFTerm getConstraintValue() {
		return constraintValue;
	}

	public RDFTerm getFocusNode() {
		return focusNode;
	}
	
	public RDFTerm getResultPath() {
		return resultPath;
	}

	public List<RDFTerm> getNeighborhood() {
		return neighborhood;
	}




	
}
