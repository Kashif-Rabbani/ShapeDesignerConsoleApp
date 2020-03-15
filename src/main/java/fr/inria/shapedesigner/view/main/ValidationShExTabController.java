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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.rdf.api.RDFTerm;

import fr.inria.lille.shexjava.schema.Label;
import fr.inria.shapedesigner.MainApp;
import fr.inria.shapedesigner.control.CreateValidateProject;
import fr.inria.shapedesigner.control.NamedQuery;
import fr.inria.shapedesigner.model.validationShEx.MatchingElement;
import fr.inria.shapedesigner.model.validationShEx.MatchingElement.BaseMatchingElementTE;
import fr.inria.shapedesigner.model.validationShEx.MatchingExplanation;
import fr.inria.shapedesigner.model.validationShEx.ValidationShEx;
import fr.inria.shapedesigner.view.fxutil.CellForMatchingExplanation;
import fr.inria.shapedesigner.view.fxutil.CustomTextFieldTableCell;
import fr.inria.shapedesigner.view.fxutil.RowForMatchingExplanation;
import fr.inria.shapedesigner.view.fxutil.RowForShExResult;
import fr.inria.shapedesigner.view.util.EditNodeController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ValidationShExTabController implements TabController {
	protected MainApp main;
	protected CreateValidateProject project; 
	
	private ValidationShEx currentValidationRun;
	private MatchingExplanation currentMatchingExplanation;
	
	@FXML
	private Button validationButton;
	@FXML
	private Button stopButton;
	@FXML
	private SplitPane basePane;
	// validation interface
	@FXML 
	private ToggleGroup validationAlgorithm;
	@FXML
	private RadioButton refine;
	@FXML
	private RadioButton recursive;
	@FXML
	private TableView<ValidationRequest> validationRequestsTable;
	@FXML
	private TableColumn<ValidationRequest,NamedQuery> nodeRequestColumn;	
	@FXML
	private TableColumn<ValidationRequest,Label> shapeRequestColumn;
	private ObservableList<Label> labelOptions;
	@FXML
	private AnchorPane resultPane;
	// result interface
	@FXML
	private CheckBox showPositives;
	@FXML
	private CheckBox showNegatives;
	@FXML
	private CheckBox generated;
	@FXML
    private TableView<MatchingExplanation> result;
	@FXML
    private TableColumn<MatchingExplanation, String> nodeColumn;
	@FXML
	private TableColumn<MatchingExplanation, String> shapeColumn;
	@FXML
	private TableColumn<MatchingExplanation, String> resultColumn;
	@FXML
	private TableColumn<MatchingExplanation, String> messageColumn;
	// matching interface
	@FXML
	private AnchorPane matchingPane;
	@FXML
	private TreeTableView<MatchingElement> shapeTreeView;
	@FXML
	private TreeTableColumn<MatchingElement, String> exprTypeColumn;
	@FXML
	private TreeTableColumn<MatchingElement, String> exprDescrColumn;
	@FXML
	private TreeTableColumn<MatchingElement, MatchingElement> matchingTriplesColumn;
	@FXML
	private TreeTableColumn<MatchingElement, String> matchingTriplesPredicateColumn;
	@FXML
	private TreeTableColumn<MatchingElement, String> matchingTriplesObjectColumn;
	
	
	public void initialize(MainApp main, CreateValidateProject project) {
		this.main = main;
		this.project = project;
		
		hideNodeLayout();		
		hideResultLayout();
		
		// validation initialization
		
		nodeRequestColumn.setCellValueFactory(i -> i.getValue().getNodeRequest());
		nodeRequestColumn.setCellFactory(c -> new ComboBoxTableCell<ValidationRequest,NamedQuery>(project.getListOfQuery()){
			@Override
			public void updateItem(NamedQuery item, boolean empty) {
				super.updateItem(item, empty);
				this.setStyle("-fx-background-color:white");
				this.setStyle("-fx-background-color-selected:blue");
				if  (this.getTableRow().getItem()!=null && this.getItem()==null) {					
					this.setStyle("-fx-background-color: #d6eaf8 ");
				}
			}
		});

		labelOptions = FXCollections.observableArrayList() ;
		shapeRequestColumn.setCellValueFactory(i -> i.getValue().getShapeLabel());
		shapeRequestColumn.setCellFactory(c -> new ComboBoxTableCell<ValidationRequest,Label>(labelOptions){
			@Override
			public void updateItem(Label item, boolean empty) {
				super.updateItem(item, empty);
				this.setStyle("-fx-background-color:white");
				this.setStyle("-fx-background-color-selected:blue; ");
				if  (this.getTableRow().getItem()!=null && this.getItem()==null) {					
					this.setStyle("-fx-background-color: #d6eaf8 ");
				}
			}
		});
		
		//result initialization
		messageColumn.setCellFactory(CustomTextFieldTableCell.forTableColumn());
		result.setRowFactory(tv -> { return new RowForShExResult(this); });
		
		nodeColumn.setCellValueFactory(cellData -> cellData.getValue().getNodeString());
		shapeColumn.setCellValueFactory(cellData -> cellData.getValue().getShapeString());
		resultColumn.setCellValueFactory(cellData -> cellData.getValue().getResultString());
		messageColumn.setCellValueFactory(cellData -> cellData.getValue().getMessageString());
		
		// matching initialization
		shapeTreeView.setRowFactory(tv -> { return new RowForMatchingExplanation(this); });
		exprTypeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("exprType"));
		exprDescrColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("exprDescr"));
		
		matchingTriplesPredicateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("triplePredicate"));		
		matchingTriplesPredicateColumn.setCellFactory(tc -> {
			return new CellForMatchingExplanation(row -> row.getTriplePredicate()); });		
		
		matchingTriplesObjectColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("tripleObject"));
		matchingTriplesObjectColumn.setCellFactory(tc -> { 
			return new CellForMatchingExplanation(row -> row.getTripleObject()); });

	}
	
	
	//----------------------------------------------------
	// Validation
	//----------------------------------------------------

	public void addValidationRequest() {
		validationRequestsTable.getItems().add(new ValidationRequest());
	}
	
	public void removeValidationRequest() {
		if (validationRequestsTable.getSelectionModel().getSelectedItem()!=null) {
			validationRequestsTable.getItems().remove(validationRequestsTable.getSelectionModel().getSelectedItem());
		}
	}
	
	public void validationShExTabSelected() {
		if (instantiateThread!=null)
			// A validation is running
			return ;
		
		stopButton.setDisable(true);
		validationButton.setDisable(false);
		hideNodeLayout();		
		hideResultLayout();
		currentValidationRun = new ValidationShEx();

		InputStream stream = new ByteArrayInputStream((project.getSchema()).getBytes(StandardCharsets.UTF_8));
		try {
			currentValidationRun.parseAndLoadSchema(stream);
		} catch (Exception e) {
			MainApp.logApp("Error loading the schema", e.getMessage());
			return ;
		}
		
		if (project!=null) {			
			List<Label> labels = new LinkedList<>(currentValidationRun.getSchema().getShapeExprsMap().keySet());
			labels = labels.stream().filter(label -> !label.isGenerated()).collect(Collectors.toList());
			labelOptions.clear();
			labelOptions.addAll(labels);
			
			Iterator<ValidationRequest> ite = validationRequestsTable.getItems().iterator();
			while (ite.hasNext()) {
				ValidationRequest valRequest = ite.next();
				if (!project.getListOfQuery().contains(valRequest.getNodeRequest().get()) || 
						!labels.contains(valRequest.getShapeLabel().get()))
					ite.remove();
			}
		}

	}
	
	
	//FIXME a better way to stop a thread?
	protected Thread instantiateThread = null;
	public  void performValidation() {
		main.getLogger().fine("Running validation");

		hideNodeLayout();
		hideResultLayout();
		validationButton.setDisable(true);
		stopButton.setDisable(false);
		
		instantiateThread = new Thread(){
			boolean stop = false;
			@Override
			public void run() {
				currentValidationRun.setDataGraph(project.getGraph());
				
				boolean result = false;
				if (recursive.isSelected()) {
					result = performRecursiveValidation();
				}
				if (refine.isSelected()) {
					result = performRefineValidation();
				}
				displayValidationResult (result);
			}

			@Override
			public void interrupt() {
				stop = true;
				currentValidationRun.stopValidation();
			}

			@Override
			public boolean isInterrupted() {
				return stop;
			}
		};
		instantiateThread.start();
	}
	
	public void displayValidationResult(Boolean res) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (res) {
					showResultLayout();
					hideNodeLayout();
					updateResult();
				}
				validationButton.setDisable(false);
				stopButton.setDisable(true);
				instantiateThread = null;
			}			
		});
	}
	
	
	public void stopValidation() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (instantiateThread!=null) {
					instantiateThread.interrupt();
					boolean result = instantiateThread.isInterrupted();
					if (result) {
						instantiateThread=null;
						validationShExTabSelected();
					}
				}
			}			
		});
		
	}
	
	
	private boolean performRecursiveValidation() {
		try {
			currentValidationRun.initRecursiveValidation();
			for (ValidationRequest valRequest:validationRequestsTable.getItems()) {
				if (valRequest.getNodeRequest().get()!=null && valRequest.getShapeLabel().get()!=null) {
					Set<RDFTerm> nodes = project.executeQuery(valRequest.getNodeRequest().get());

					MainApp.logApp("Validation","validating \""+valRequest.getNodeRequest().get().getName().get()+
							"\" with shape "+valRequest.getShapeLabel().get());
					currentValidationRun.performRecursiveValidation(valRequest.getShapeLabel().get(), nodes);
				}
			}
			currentValidationRun.createResult();
			return true;
		} catch (Exception e) {
			MainApp.logApp("Error during the recursive validation", e.getMessage());
			return false;
		}
	}


	private boolean performRefineValidation() {
		try {
			MainApp.logApp("Validation","validating with the refine algorithm.");
			currentValidationRun.initRefineValidation();
			currentValidationRun.performRefineValidation();
			currentValidationRun.createResult();
			return true;
		} catch (Exception e) {
			MainApp.logApp("Error during the refine validation", e.getMessage());
			return false;
		}
	}
	
	

	//----------------------------------------------------
	// Result 
	//----------------------------------------------------

	
	
	
	public void hideResultLayout() {
		if (basePane.getItems().size()>=2) {
			basePane.getItems().remove(1);
		}
	}
	
	
	public void showResultLayout() {
		if (basePane.getItems().size()==1) {
			basePane.getItems().add(1, resultPane);
			basePane.setDividerPositions(0.2);
		}

	}
	
	
	public void updateResult() {
		result.getItems().clear();
		for (MatchingExplanation res : currentValidationRun.getResults()) {
			if (res.isResult() && showPositives.selectedProperty().get() )
				if (!generated.selectedProperty().get() || !res.getLabel().isGenerated())
					result.getItems().add(res);
			if (showNegatives.selectedProperty().get() && !res.isResult())
				if (!generated.selectedProperty().get() || !res.getLabel().isGenerated())
					result.getItems().add(res);
		}
	}
	
	
	public void extractConformantPart() {
		
	}
	
	//----------------------------------------------------
	// Matching explanation
	//----------------------------------------------------

	
	public void updateMatchingExplanation() {
		showNodeLayout();
		shapeTreeView.setRoot(currentMatchingExplanation.createTree());
	}
	
	
	public void hideNodeLayout() {
		if (basePane.getItems().size()>=3) {
			basePane.getItems().remove(2);
		}				
	}
	
	
	public void showNodeLayout() {
		if (basePane.getItems().size()<=2) {
			basePane.getItems().add(2,matchingPane);
		}
	}
	
	public void editRow(TreeTableRow<MatchingElement> row) {
		if (row.getItem() !=null) {
			MatchingElement matchingElement = row.getItem();
			try {
				// Load the fxml file and create a new stage for the popup dialog.
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainApp.class.getResource("view/util/AddNode.fxml"));
				AnchorPane page = (AnchorPane) loader.load();

				// Create the dialog Stage.
				Stage dialogStage = new Stage();
				dialogStage.setTitle("Edit node");
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.initOwner(main.getPrimaryStage());
				Scene scene = new Scene(page);
				dialogStage.setScene(scene);

				EditNodeController controller = loader.getController();
				controller.setDialogStage(dialogStage);
				controller.setMainApp(this);
				controller.setProject(project);
				controller.setEditContext(currentMatchingExplanation,matchingElement);
				// Show the dialog and wait until the user closes it
				dialogStage.showAndWait();
				updateMatchingExplanation();
			} catch (IOException e) {
				e.printStackTrace();
				return ;
			}
		} 
	}
	
	
	public void removeRow(TreeTableRow<MatchingElement> row) {
		if (row.getItem() !=null && row.getItem() instanceof MatchingElement.BaseMatchingElementTE) {
			MatchingElement.BaseMatchingElementTE me = (BaseMatchingElementTE) row.getItem();
			if (me.getTriple() != null) {
				project.getGraph().remove(me.getTriple().getSubject(), 
						me.getTriple().getPredicate(),
						me.getTriple().getObject());
				currentValidationRun.saveChangesInData();
				this.updateMatchingExplanation();
			}
		}
	}
	
	
	public void investigateRow(RDFTerm node, Label label) {
		currentMatchingExplanation = currentValidationRun.getMatchingExplanation(node, label);
		updateMatchingExplanation();
	}	


	public void selectRow(MatchingExplanation rowData) {
		this.currentMatchingExplanation = rowData;
		updateMatchingExplanation();
		
	}
	

	public ValidationShEx getValidationRun() {
		return currentValidationRun;
	}
	
	
	@Override
	public Node getBasePane() {
		return basePane;
	}

}
