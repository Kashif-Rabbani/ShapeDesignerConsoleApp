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

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.rdf.api.IRI;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;

import fr.inria.lille.shexjava.GlobalFactory;
import fr.inria.shapedesigner.MainApp;
import fr.inria.shapedesigner.control.CreateValidateProject;
import fr.inria.shapedesigner.control.CreateValidateProject.PROJECT_TYPE;
import fr.inria.shapedesigner.control.NamedPattern;
import fr.inria.shapedesigner.control.NamedQuery;
import fr.inria.shapedesigner.control.WikidataProject;
import fr.inria.shapedesigner.view.conception.PatternConceptionController;
import fr.inria.shapedesigner.view.conception.QueryConceptionController;
import fr.inria.shapedesigner.view.fxutil.CustomEditor;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ConceptionTabController implements TabController {
	protected MainApp main;
	protected CreateValidateProject project; 
	
	@FXML
	private AnchorPane basePane;
	@FXML
	private AnchorPane analyzePane;
	@FXML
	private BorderPane analyzeOptionsPane;
	@FXML
	private AnchorPane queryPane;
	@FXML
	private AnchorPane patternPane;
	@FXML
	private BorderPane analyzeResultPane;
	@FXML
	private HBox analysisResult;
	@FXML
	private Button recAnalysisButton;
	@FXML
	private Button analysisButton;
	@FXML
	private Button stopButton;
	
	private CustomEditor resultEditor;
	
	@FXML
	private AnchorPane editorPane;
	private CustomEditor editor;
	
	private PatternConceptionController controllerPattern;
	private QueryConceptionController controllerQuery;
	
	public void initialize(MainApp main, CreateValidateProject project) {
		this.main = main;
		this.project = project;
		
		showAnalysisOptions();
		showPatternConception();
		showQueryConception();
		
		stopButton.setDisable(true);
		analysisButton.setDisable(false);
		if (project.getType().equals(PROJECT_TYPE.SHEX) && !(project instanceof WikidataProject)) recAnalysisButton.setDisable(false);
		else recAnalysisButton.setDisable(true);
		/*
		 * result
		 */
		
		this.resultEditor = new CustomEditor();
		this.resultEditor.setId("editor");
		this.resultEditor.setEditable(false);
		this.analysisResult.getChildren().add(this.resultEditor);
		
		VirtualizedScrollPane<CustomEditor> scroll = new VirtualizedScrollPane<CustomEditor>(this.resultEditor);
		HBox.setHgrow(scroll, Priority.ALWAYS);
		this.analysisResult.getChildren().add(scroll);
		
		HBox.setHgrow(this.resultEditor, Priority.ALWAYS);
		this.resultEditor.getStylesheets().add(main.getClass().getResource("view/conception/CustomEditor.css").toExternalForm());
		
		this.resultEditor.setParagraphGraphicFactory(LineNumberFactory.get(this.resultEditor));
		this.resultEditor.multiPlainChanges().successionEnds(Duration.ofMillis(500))
				.subscribe(ignore -> this.resultEditor.setStyleSpans(0, this.resultEditor.computeHighlighting(this.resultEditor.getText())));
	
		// -----
		
		editor = new CustomEditor();
		editor.setParagraphGraphicFactory(LineNumberFactory.get(editor));
		editorPane.getChildren().clear();
		VirtualizedScrollPane<CustomEditor> scroll2 = new VirtualizedScrollPane<CustomEditor>(this.editor);
		HBox.setHgrow(scroll2, Priority.ALWAYS);
		
		project.setEditorCodeArea(editor);
		AnchorPane.setBottomAnchor(scroll2, 0.0);
		AnchorPane.setTopAnchor(scroll2, 0.0);
		AnchorPane.setLeftAnchor(scroll2, 0.0);
		AnchorPane.setRightAnchor(scroll2, 0.0);
		editorPane.getChildren().add(scroll2);
		
		
		this.editor.getStylesheets().add(main.getClass().getResource("view/conception/CustomEditor.css").toExternalForm());
		
		this.editor.setParagraphGraphicFactory(LineNumberFactory.get(this.editor));
		this.editor.multiPlainChanges().successionEnds(Duration.ofMillis(500))
				.subscribe(ignore -> this.editor.setStyleSpans(0, this.editor.computeHighlighting(this.editor.getText())));
	}
	
	
	private void showPatternConception() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(main.getClass().getResource("view/conception/PatternConceptionLayout.fxml"));
			loader.load();
			controllerPattern = loader.getController();
			controllerPattern.setProject(project);
			controllerPattern.initialize(patternPane);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	private void showQueryConception() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(main.getClass().getResource("view/conception/QueryConceptionLayout.fxml"));
			loader.load();
			controllerQuery = loader.getController();
			controllerQuery.setProject(project);
			controllerQuery.initialize(queryPane);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	

	public void goBackFromAnalysis() {
		showAnalysisOptions();
	}


	public void performAnalysis() {
		NamedPattern pattern = controllerPattern.getSelectedPattern();
		ObservableList<NamedQuery> queries = controllerQuery.getSelectedQuery();
		if ( pattern != null && queries != null && queries.size()>0) {
			stopButton.setDisable(false);
			analysisButton.setDisable(true);
			if (project.getType().equals(PROJECT_TYPE.SHEX) && !(project instanceof WikidataProject)) recAnalysisButton.setDisable(true);
			project.performAnalysis(pattern, queries, this);
		} else {
			if (pattern == null) MainApp.logApp("Analysis", "A pattern must be selected.");
			if (queries == null || queries.size()==0) MainApp.logApp("Analysis", "A query must be selected.");
		}
	}
	
	
	public void performRecursiveShexAnalyze() {
		Set<IRI> types = project.getGraph().stream(null,GlobalFactory.RDFFactory.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),null)
					.map(tr -> tr.getObject()).filter(obj -> obj instanceof IRI).map(obj -> (IRI) obj).collect(Collectors.toSet());
			
		NamedPattern pattern = controllerPattern.getSelectedPattern();
		if ( pattern != null && types.size()>0) {
			stopButton.setDisable(false);
			analysisButton.setDisable(true);
			if (project.getType().equals(PROJECT_TYPE.SHEX) && !(project instanceof WikidataProject)) recAnalysisButton.setDisable(true);
			project.performRecAnalysisShEx(pattern, types, 100,this);
		}

	}
	
	
	public void analysisDone(String analysisResult) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {				
				analyzePane.getChildren().clear();
				stopButton.setDisable(true);
				analysisButton.setDisable(false);
				if (project.getType().equals(PROJECT_TYPE.SHEX) && !(project instanceof WikidataProject)) recAnalysisButton.setDisable(false);
				analyzePane.getChildren().add(analyzeResultPane);
				resultEditor.setText(analysisResult);
			}			
		});
	}
	
	public void stopAnalysis() {
		if (project.stopAnalysis()) {
			stopButton.setDisable(true);
			analysisButton.setDisable(false);
			if (project.getType().equals(PROJECT_TYPE.SHEX) && !(project instanceof WikidataProject)) recAnalysisButton.setDisable(false);
			project.stopAnalysis();
		};
	}
	
	
	public void addShape() {
		try {
			project.appendToSchema(resultEditor.getText());
			showAnalysisOptions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void showAnalysisOptions() {
		analyzePane.getChildren().clear();
		analyzePane.getChildren().add(analyzeOptionsPane);
	}

	@Override
	public Node getBasePane() {
		return basePane;
	}
		
}
