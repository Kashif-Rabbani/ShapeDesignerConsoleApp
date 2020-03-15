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
package fr.inria.shapedesigner.view.main;

import java.util.Map;
import java.util.stream.Collectors;

import fr.inria.shapedesigner.MainApp;
import fr.inria.shapedesigner.control.CreateValidateProject;
import fr.inria.shapedesigner.model.util.RDFPrintingUtil;
import fr.inria.shapedesigner.model.validationSHACL.SHACLValidationResult;
import fr.inria.shapedesigner.model.validationSHACL.ValidationSHACL;
import fr.inria.shapedesigner.view.fxutil.RowForSHACLResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class ValidationSHACLTabController implements TabController {
	protected CreateValidateProject project; 
	
	@FXML
	private AnchorPane basePane;
	@FXML
    private TableView<SHACLValidationResult> resultSHACL;
	@FXML
	private TableColumn<SHACLValidationResult, String> shapeSHACLColumn;
	@FXML
	private TableColumn<SHACLValidationResult, String> constraintComponentSHACLColumn;
	@FXML
	private TableColumn<SHACLValidationResult, String> constraintValueColumn;
	@FXML
    private TableColumn<SHACLValidationResult, String> nodeSHACLColumn;
	@FXML
	private TableColumn<SHACLValidationResult, String> pathSHACLColumn;
	@FXML
	private TableColumn<SHACLValidationResult, String> neighborhoodSHACLColumn;

	
	public void initialize(CreateValidateProject project) {
		this.project = project;
		
		resultSHACL.setRowFactory(tv -> { return new RowForSHACLResult(); });

		shapeSHACLColumn.setCellValueFactory(cellData -> {
			try {
				return new SimpleStringProperty(RDFPrintingUtil.toPrettyString(cellData.getValue().getSourceShape(),project.parsePrefixes()));
			} catch (Exception e) {
				return new SimpleStringProperty(cellData.getValue().getSourceShape().ntriplesString());
			}
		});
		constraintComponentSHACLColumn.setCellValueFactory(cellData -> {
			try {
				return new SimpleStringProperty(RDFPrintingUtil.toPrettyString(cellData.getValue().getSourceConstraintComponent(),project.parsePrefixes()));
			} catch (Exception e) {
				return new SimpleStringProperty(cellData.getValue().getSourceConstraintComponent().ntriplesString());
			}
		});
		constraintValueColumn.setCellValueFactory(cellData -> {
			try {
				if (cellData.getValue().getConstraintValue() == null)
					return new SimpleStringProperty("");
				return new SimpleStringProperty(RDFPrintingUtil.toPrettyString(cellData.getValue().getConstraintValue(),project.parsePrefixes()));
			} catch (Exception e) {
				return new SimpleStringProperty(cellData.getValue().getConstraintValue().ntriplesString());
			}
		});
		nodeSHACLColumn.setCellValueFactory(cellData -> {
			try {
				return new SimpleStringProperty(RDFPrintingUtil.toPrettyString(cellData.getValue().getFocusNode(),project.parsePrefixes()));
			} catch (Exception e) {
				return new SimpleStringProperty(cellData.getValue().getFocusNode().ntriplesString());
			}
		});
		pathSHACLColumn.setCellValueFactory(cellData -> {
			try {
				return new SimpleStringProperty(RDFPrintingUtil.toPrettyString(cellData.getValue().getResultPath(),project.parsePrefixes()));
			} catch (Exception e) {
				return new SimpleStringProperty(cellData.getValue().getResultPath().ntriplesString());
			}
		});
		
		neighborhoodSHACLColumn.setCellValueFactory(cellData -> {
			try {
				Map<String, String> prefixes = project.parsePrefixes();
				return new SimpleStringProperty(cellData.getValue().getNeighborhood().stream()
						.map(neigh -> RDFPrintingUtil.toPrettyString(neigh,prefixes)).collect(Collectors.toList())+"");
			} catch (Exception e) {
				return new SimpleStringProperty(cellData.getValue().getNeighborhood()+"");
			}
		});
	}
	
	
	public void validate() {
		MainApp.logApp("SHACL validation", "starting");
		ValidationSHACL validation = new ValidationSHACL();

		try {
			validation.parseAndLoadSchema(project.getSchema());
		} catch (Exception e) {
			MainApp.logApp("SHACL validation", "Error during the parsing of the schema"+e.getMessage()+".");
			return ;
		}
		
		validation.setDataGraph(project.getGraph());
		
		validation.validate();
		
		resultSHACL.getItems().clear();
		if (validation.getResults().size() == 0) {
                MainApp.logApp("SHACL validation", "the data respect the schema");
		} else {
               resultSHACL.getItems().addAll(validation.getResults());
               MainApp.logApp("SHACL validation", resultSHACL.getItems().size()+" validation errors.");
  		}
	}


	@Override
	public Node getBasePane() {
		return basePane;
	}

}
