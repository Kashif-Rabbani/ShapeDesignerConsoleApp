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
package fr.inria.shapedesigner.view.conception;

import java.time.Duration;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;

import fr.inria.shapedesigner.control.CreateValidateProject;
import fr.inria.shapedesigner.control.NamedQuery;
import fr.inria.shapedesigner.view.fxutil.CustomEditor;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public class QueryConceptionController {
	protected CreateValidateProject project; 
	private AnchorPane contener;
	
	@FXML
	private BorderPane displayPane;
	@FXML
	private TableView<NamedQuery> queries;
	@FXML
	private TableColumn<NamedQuery, String> name;
	@FXML
	private TableColumn<NamedQuery, String> description;
	@FXML
	private GridPane editPane;
	@FXML
	private TextField editName;
	@FXML
	private TextField editVariable;
	@FXML
	private HBox queryArea;
	
	private CustomEditor editQuery;
	
	
	public void initialize(AnchorPane contener) {
		queries.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		this.contener = contener;
		
		AnchorPane.setBottomAnchor(displayPane, 0.0);
		AnchorPane.setTopAnchor(displayPane, 0.0);
		AnchorPane.setLeftAnchor(displayPane, 0.0);
		AnchorPane.setRightAnchor(displayPane, 0.0);
		AnchorPane.setBottomAnchor(editPane, 0.0);
		AnchorPane.setTopAnchor(editPane, 0.0);
		AnchorPane.setLeftAnchor(editPane, 0.0);
		AnchorPane.setRightAnchor(editPane, 0.0);
		showDisplayPane();
		
		name.setCellValueFactory(new Callback<CellDataFeatures<NamedQuery, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<NamedQuery, String> param) {
				return param.getValue().getName();
			}
		});
		description.setCellValueFactory(new Callback<CellDataFeatures<NamedQuery, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<NamedQuery, String> param) {
				return param.getValue().getDescription();
			}
		});
		queries.setItems(project.getListOfQuery());
		
		/*
		 * Query Editor
		 */
		
		
		queries.setItems(project.getListOfQuery());
		this.editQuery = new CustomEditor();
		this.queryArea.getChildren().add(this.editQuery);
		
		VirtualizedScrollPane<CustomEditor> scroll = new VirtualizedScrollPane<CustomEditor>(this.editQuery);
		HBox.setHgrow(scroll, Priority.ALWAYS);
		
		this.queryArea.getChildren().add(scroll);
		
		this.editQuery.getStylesheets().add(getClass().getResource("CustomEditor.css").toExternalForm());
		
		this.editQuery.setParagraphGraphicFactory(LineNumberFactory.get(this.editQuery));
		this.editQuery.multiPlainChanges().successionEnds(Duration.ofMillis(500))
				.subscribe(ignore -> this.editQuery.setStyleSpans(0, this.editQuery.computeHighlighting(this.editQuery.getText())));
		
		// -----
	}
	
	public ObservableList<NamedQuery> getSelectedQuery() {
		return queries.selectionModelProperty().get().getSelectedItems();
	}
	
	
	public void goBack() {
		showDisplayPane();
	}
	
	public void add() {
		selection = null;
		showEditPane();
		editName.setText("");
		editVariable.setText("");
		editQuery.setText("");
	}
	
	private NamedQuery selection = null;
	public void edit() {
		selection = queries.selectionModelProperty().get().getSelectedItem();
		if (selection!=null) {
			showEditPane();
			editName.setText(selection.getName().get());
			editVariable.setText(selection.getVariable().get());
			editQuery.setText(selection.getDescription().get());
		}
	}
	
	public void duplicate() {
		NamedQuery selected = queries.selectionModelProperty().get().getSelectedItem();
		if (selected!=null) {
			queries.getItems().add(new NamedQuery(selected.getName().get(), 
					selected.getVariable().get(), selected.getDescription().get()));
		}
	}
	
	public void delete() {
		ObservableList<NamedQuery> selected = this.getSelectedQuery();
		if (selected!=null) {			
			queries.getItems().removeAll(selected);
		}
	}
	
	public void validate() {
		if (selection != null) {
			selection.setName(editName.getText());
			selection.setVariable(editVariable.getText());
			selection.setDescription(editQuery.getText());
		}else {
			queries.getItems().add(new NamedQuery(editName.getText(), editVariable.getText(), editQuery.getText()));
		}
		goBack();
	}
	
	
	public void showDisplayPane() {
		contener.getChildren().clear();
		contener.getChildren().add(displayPane);

	}
	
	
	public void showEditPane() {
		contener.getChildren().clear();
		contener.getChildren().add(editPane);
	}
	
	public void setProject(CreateValidateProject controller) {
		this.project = controller;
	}
	
}
