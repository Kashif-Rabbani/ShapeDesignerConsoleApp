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
package fr.inria.shapedesigner.view.util;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;

import fr.inria.shapedesigner.MainApp;
import fr.inria.shapedesigner.control.CreateValidateProject;
import fr.inria.shapedesigner.model.util.RDFParsingUtil;
import fr.inria.shapedesigner.model.validationShEx.MatchingElement;
import fr.inria.shapedesigner.model.validationShEx.MatchingElement.BaseMatchingElementTE;
import fr.inria.shapedesigner.model.validationShEx.MatchingExplanation;
import fr.inria.shapedesigner.view.main.ValidationShExTabController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditNodeController {
	protected ValidationShExTabController main;
	protected CreateValidateProject project; 
	
	private MatchingExplanation matchingExpl;
	private MatchingElement matchingElem;
	
	private Stage dialogStage;	
	@FXML
	private TextField nodeSubjectField;
	@FXML
	private TextField nodePredicateField;
	@FXML
	private TextField nodeObjectField;
	
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	
	public void setEditContext(MatchingExplanation matchingExpl,MatchingElement matchingElem) {
		this.matchingExpl = matchingExpl;
		this.matchingElem = matchingElem;
		nodeSubjectField.setText(matchingExpl.getNode().ntriplesString());
		if (matchingElem instanceof MatchingElement.BaseMatchingElementTE) {
			MatchingElement.BaseMatchingElementTE rowTE = (BaseMatchingElementTE) matchingElem;
			if (rowTE.getTriple()!=null) {
				nodePredicateField.setText(rowTE.getTriple().getPredicate().ntriplesString());
				nodeObjectField.setText(rowTE.getTriple().getObject().ntriplesString());
			}else
				nodePredicateField.setText(rowTE.getTripleConstraint().getProperty().getIri().ntriplesString());
		}
	}
	
	
	public void handleAdd() {
		BlankNodeOrIRI subject;
		IRI predicate;
		RDFTerm object;
		
		try {
			subject = (BlankNodeOrIRI) matchingExpl.getNode();
		} catch (Exception e) {
			MainApp.logApp("Error", "Subject is not a valid BlankNode or IRI.");
	    	return;
		}
		
		try {
			predicate = RDFParsingUtil.parseIRI(nodePredicateField.getText());
		} catch (Exception e) {
			MainApp.logApp("Error", "Predicate is not a valid IRI.");
	    	return;
		}
		
		try {
			object = RDFParsingUtil.parseRDFTerm(nodeObjectField.getText());
		} catch (Exception e) {
			MainApp.logApp("Error", "Object is not a valid BlankNode or IRI or Literal.");
	    	return;
		}
		
		Graph dataGraph = project.getGraph();

		if (matchingElem instanceof MatchingElement.BaseMatchingElementTE) {
			MatchingElement.BaseMatchingElementTE rowTE = (BaseMatchingElementTE) matchingElem;
			if (rowTE.getTriple()!=null)
				dataGraph.remove(rowTE.getTriple().getSubject(),
						rowTE.getTriple().getPredicate(),
						rowTE.getTriple().getObject());
		}
		dataGraph.add(subject, predicate, object);
		
		this.dialogStage.close();
		
		main.getValidationRun().saveChangesInData();
	}
	
	
	public void handleCancel() {
		this.dialogStage.close();
	}	
	

	public void setMainApp(ValidationShExTabController mainApp) {
		this.main = mainApp;
	}


	public void setProject(CreateValidateProject project) {
		this.project = project;
	}
	
	
}
