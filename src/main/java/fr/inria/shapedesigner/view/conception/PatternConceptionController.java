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

import fr.inria.lille.shexjava.pattern.PatternParsing;
import fr.inria.shapedesigner.control.CreateValidateProject;
import fr.inria.shapedesigner.control.NamedPattern;
import fr.inria.shapedesigner.view.fxutil.CustomEditor;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

public class PatternConceptionController {
	protected CreateValidateProject project;
	protected PatternParsing parser = new PatternParsing();

	private AnchorPane contener;
	@FXML
	private BorderPane displayPane;
	@FXML
	private TableView<NamedPattern> patterns;
	@FXML
	private TableColumn<NamedPattern, String> name;
	@FXML
	private TableColumn<NamedPattern, String> description;
	@FXML
	private GridPane editPane;
	@FXML
	private TextField editName;
	@FXML
	private HBox editPatternContainer;
	
	private CustomEditor editor;
		
	public void initialize(AnchorPane contener) {
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

		name.setCellValueFactory(new Callback<CellDataFeatures<NamedPattern, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<NamedPattern, String> param) {
				return param.getValue().getName();
			}
		});
		description.setCellValueFactory(new Callback<CellDataFeatures<NamedPattern, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<NamedPattern, String> param) {
				return param.getValue().getDescription();
			}
		});
		patterns.setItems(project.getListOfPattern());
		
		/*
		 * Pattern editor
		 */
		
		this.editor = new CustomEditor();
		
		VirtualizedScrollPane<CustomEditor> scroll = new VirtualizedScrollPane<CustomEditor>(this.editor);
		HBox.setHgrow(scroll, Priority.ALWAYS);
		this.editPatternContainer.getChildren().add(scroll);
		
		this.editor.getStylesheets().add(getClass().getResource("CustomEditor.css").toExternalForm());
		
		this.editor.setParagraphGraphicFactory(LineNumberFactory.get(this.editor));
		this.editor.multiPlainChanges().successionEnds(Duration.ofMillis(500))
				.subscribe(ignore -> this.editor.setStyleSpans(0, this.editor.computePatternHighlighting(this.editor.getText())));
	
		// -----
		
	}
	
	
	public NamedPattern getSelectedPattern() {
		return patterns.selectionModelProperty().get().getSelectedItem();
	}
	
	
	public void add() {
		selection = null;
		showEditPane();
		editName.setText("");
		editor.setText("");
	}
	
	
	private NamedPattern selection = null;
	public void edit() {
		selection = patterns.selectionModelProperty().get().getSelectedItem();
		if (selection!=null) {
			showEditPane();
			editName.setText(selection.getName().get());
			editor.setText(selection.getDescription().get());
		}
	}
	
	
	public void duplicate() {
		NamedPattern selected = patterns.selectionModelProperty().get().getSelectedItem();
		if (selected!=null) {
			patterns.getItems().add(project.createNamedPattern(selected.getName().get(), selected.getDescription().get()));
		}
	}
	
	
	public void delete() {
		selection = patterns.selectionModelProperty().get().getSelectedItem();
		if (selection!=null) {
			patterns.getItems().remove(selection);
		}
	}
	
	
	public void validate() {
		boolean success = false;
		if (selection!=null) {
			selection.setName(editName.getText());
			success = project.setPatternDescription(selection, editor.getText());
		}else {
			NamedPattern np = project.createNamedPattern(editName.getText(), editor.getText());
			if (np != null) { 
				patterns.getItems().add(np);
				success = true;
			}
		}
		if (success) {
			goBack();
		}
		
	}
	
	
	public void goBack() {
		showDisplayPane();
		selection = null;
	}
	
	
	public void showDisplayPane() {
		contener.getChildren().clear();
		contener.getChildren().add(displayPane);
	}
	
	
	public void showEditPane() {
		contener.getChildren().clear();
		contener.getChildren().add(editPane);
		editor.setText(" " + editor.getText());
		editor.setText(editor.getText().substring(1));
	}
	

	public void setProject(CreateValidateProject controller) {
		this.project = controller;
	}
	
	
	public void createAlertError(String title, String header,String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setResizable(true);
    	alert.setTitle(title);
    	alert.setHeaderText(header);
    	alert.setContentText(message);
    	alert.showAndWait();
	}
	
	
}
