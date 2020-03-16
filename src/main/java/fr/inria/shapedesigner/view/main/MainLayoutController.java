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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

import org.apache.commons.rdf.api.RDFTerm;
import org.apache.jena.ext.com.google.common.io.Files;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import com.github.jsonldjava.utils.JsonUtils;

import fr.inria.shapedesigner.MainApp;
import fr.inria.shapedesigner.control.CreateValidateProject;
import fr.inria.shapedesigner.control.CreateValidateProject.PROJECT_TYPE;
import fr.inria.shapedesigner.control.MonadicSparqlQuery;
import fr.inria.shapedesigner.control.NamedQuery;
import fr.inria.shapedesigner.control.RDFTypedGraphProject;
import fr.inria.shapedesigner.control.SailRepoProject;
import fr.inria.shapedesigner.control.SparQLProject;
import fr.inria.shapedesigner.control.WikidataProject;
import fr.inria.shapedesigner.model.util.RDFPrintingUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;

public class MainLayoutController {
	protected MainApp main;

	protected CreateValidateProject project; 
	protected File projectFile;

	public CreateValidateProject getProject() {
		return project;
	}

	public void setMaster(MainApp mainApp) {
		main = mainApp;
		tabs.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv)-> {
			tabSelectionChanged();
        });
		main.getLogger().addHandler(new Handler() {
			
			@Override
			public void publish(LogRecord record) {
				updateLog(record);
			}
			
			@Override
			public void flush() {}
			
			@Override
			public void close() throws SecurityException {}
		});
		closeProject();
	}
	
	
	//----------------------------------------------------
	//  File menu
	//----------------------------------------------------

	@FXML
	protected Menu fileMenu;
	
	public void newShExRDFProject() { newRDFProject(PROJECT_TYPE.SHEX); }

	public void newShExWikidataProject() { newWikidataProject(PROJECT_TYPE.SHEX); }

	public void newShExSparQLProject() { newSparQLProject(PROJECT_TYPE.SHEX); }

	public void newShExSailRepositoryProject() { newSailRepositoryProject(PROJECT_TYPE.SHEX); }

	public void newSHACLRDFProject() { newRDFProject(PROJECT_TYPE.SHACL); }

	public void newSHACLWikidataProject() { newWikidataProject(PROJECT_TYPE.SHACL); }

	public void newSHACLSparQLProject() { newSparQLProject(PROJECT_TYPE.SHACL); }

	public void newSHACLSailRepositoryProject() { newSailRepositoryProject(PROJECT_TYPE.SHACL); }
	
	
	public void newRDFProject(PROJECT_TYPE type) {
		if (closeProjectConfirmation()) {
			System.out.println("Type is: " + type);
			File rdfFile = main.createFileChooser("Select input file");
			if (rdfFile!=null) {
				try {
					System.out.println("Rdf file: " + rdfFile.getPath());
					projectFile = null;
					project = new RDFTypedGraphProject(rdfFile,type);					
					initInterface();
					project.initialize();
				} catch (RDFParseException | UnsupportedRDFormatException | IOException e) {
					main.createAlertError("Error", "Error when loading the graph.", e.getMessage());
					return ;
				}
			}
		}
	}

	public void newRDFProjectConsole(PROJECT_TYPE type, String arg) {
		if (closeProjectConfirmation()) {
			System.out.println("Type is: " + type);
			File rdfFile = new File(arg);
			if (rdfFile!=null) {
				try {
					System.out.println("Rdf file: " + rdfFile.getPath());
					projectFile = null;
					project = new RDFTypedGraphProject(rdfFile,type);
					//initInterface();
					project.initialize();
				} catch (RDFParseException | UnsupportedRDFormatException | IOException e) {
					main.createAlertError("Error", "Error when loading the graph.", e.getMessage());
					return ;
				}
			}
		}
	}


	public void newWikidataProject(PROJECT_TYPE type) {
		if (closeProjectConfirmation()) {
			projectFile = null;
			project = new WikidataProject(type);
			main.getLogger().fine("Starting new wikidata project.");
			initInterface();
			project.initialize();
		}
	}


	public void newSparQLProject(PROJECT_TYPE type) {
		if (closeProjectConfirmation()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("SparQL endpoint");
			alert.setHeaderText("Enter the adress of the SparQL endpoint");
			TextField textArea = new TextField("https://query.wikidata.org/sparql");
			alert.getDialogPane().setContent(textArea);
			Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get().equals(ButtonType.OK)) {
				projectFile = null;
				project = new SparQLProject(textArea.getText(),type);
				main.getLogger().fine("Starting new SparQL project.");
				initInterface();
				project.initialize();
			}
		}
	}


	public void newSailRepositoryProject(PROJECT_TYPE type) {
		if (closeProjectConfirmation()) {
			File directory = main.createDirectoryChooserSave("Select directory to create repository.");
			if (directory!=null) {
				try {
					projectFile = null;
					project = new SailRepoProject(directory,type);
					main.getLogger().fine("Starting new RDF4J Sailrepository project.");
					initInterface();
					project.initialize();
				} catch (RDFParseException | UnsupportedRDFormatException | IOException e) {
					main.createAlertError("Error", "Error when loading the graph.", e.getMessage());
					return ;
				}				
			}
		}
	}
	
	
	public void saveProject() {
		if (project!=null) {
			if (projectFile!=null) {
				saveProjectInFile(projectFile);
			} else {
				saveProjectAs();
			}
		}
	}

	public void saveProjectHere(String jsonFileWithNameAndAddress){
		if (project!=null) {
			File saveFile = new File(jsonFileWithNameAndAddress);
			if (saveFile!=null) {
				if (!saveFile.getPath().endsWith(".json"))
					saveFile = new File(saveFile.getPath()+".json");
				projectFile = saveFile;
				saveProjectInFile(saveFile);
			}
		}
	}
	public void saveProjectAs() {
		if (project!=null) {
			File saveFile = main.createFileChooserSave("Select file to save project");
			if (saveFile!=null) {
				if (!saveFile.getPath().endsWith(".json"))
					saveFile = new File(saveFile.getPath()+".json");
				projectFile = saveFile;
				saveProjectInFile(saveFile);
			}
		}
	}
	

	private void saveProjectInFile(File dest) {
		System.out.println(dest.getPath());
		Map<String,Object> result = project.toJson();
		try {
			BufferedWriter fw = Files.newWriter(dest, Charset.defaultCharset());
			JsonUtils.writePrettyPrint(fw, result);
			//main.getLogger().fine("Project saved.");
		} catch (IOException e) {
			MainApp.logApp("Error when saving", e.getClass()+"  "+e.getMessage());
		}
	}


	public void openProjectFromFile() {
		if (closeProjectConfirmation()) {
			projectFile = main.createFileChooser("Select project",new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
			if (projectFile!=null) {
				try {
					InputStream is = new FileInputStream(projectFile);
					Map<String,Object> jsonObject = (Map<String, Object>) JsonUtils.fromInputStream(is,Charset.defaultCharset().name());
					PROJECT_TYPE type = PROJECT_TYPE.SHEX;
					if (jsonObject.containsKey("shacl"))
						type = PROJECT_TYPE.SHACL;
					project = null;
					if (jsonObject.containsKey(RDFTypedGraphProject.jsonKey))
						project = new RDFTypedGraphProject(new File((String) jsonObject.get(RDFTypedGraphProject.jsonKey)),type);
					if (jsonObject.containsKey(SailRepoProject.jsonKey))
						project = new SailRepoProject(new File((String) jsonObject.get(SailRepoProject.jsonKey)),type);
					if (jsonObject.containsKey(WikidataProject.jsonKey))
						project = new WikidataProject(type);
					if (jsonObject.containsKey(SparQLProject.jsonKey))
						project = new SparQLProject((String) jsonObject.get(SparQLProject.jsonKey),type);
					if (project == null) {
						MainApp.logApp("Error", "project type not found.");
						return ;
					}
					initInterface();
					project.initializeFromJson(jsonObject);	
					main.getLogger().fine("Project "+projectFile+" loaded.");
				} catch ( IOException e) {
					closeProject();
					MainApp.logApp("Error when opening project", e.getClass()+"  "+e.getMessage());
				}

			}
		}
	}


	public void openProjectWikidata() {
		if (closeProjectConfirmation()) {
			TextInputDialog dialog = new TextInputDialog("E80");
			dialog.setTitle("Enter the schema ID");
			dialog.setHeaderText("Enter the schema ID:");
			dialog.setContentText("Schema ID:");
			Optional<String> result = dialog.showAndWait();

			if (result.isPresent()) {
				try {
					URL url = new URL("https://www.wikidata.org/wiki/Special:EntitySchemaText/"+result.get());
					BufferedReader buf = new BufferedReader(new InputStreamReader(url.openStream()));
					newWikidataProject(PROJECT_TYPE.SHEX);
					project.setSchema(String.join("\n", buf.lines().collect(Collectors.toList())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public void closeProject() {
		if (project!=null) {
			project.setSchema("");
			project.getListOfPattern().clear();
			project.getListOfQuery().clear();
			project=null;
			main.getLogger().fine("Project closed.");
		}
		closeInterfaceTabs();
	}
	
	
	protected boolean closeProjectConfirmation() {
		if (project!=null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Close project");
			alert.setHeaderText("There is currently a project opened. Are you sure that you want to close it?");
			Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get().equals(ButtonType.OK)) {
				closeProject();
				return true;
			}
			return false;
		}
		return true;
	}
	
	
	public void close() {
		if (project==null || closeProjectConfirmation())
			main.getPrimaryStage().close();
	}
	
	
	
	//----------------------------------------------------
	// schema menu
	//----------------------------------------------------	
	
	@FXML
	protected Menu schemaMenu;
	
	public void loadSchema() {
		File dest = main.createFileChooser("Select file for saving:");
		if (dest!=null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(dest));
			    project.setSchema(String.join("\n".subSequence(0, 1), reader.lines().collect(Collectors.toList())));
			    reader.close();
			} catch (IOException e) {
				main.createAlertError("Error when saving", "Error when parsing the pattern.", "Message:"+e.getMessage());
				e.printStackTrace();
			}

		}
	}
	
	
	public void saveSchema() {
		File dest = main.createFileChooserSave("Select file for saving:");
		if (dest!=null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
			    writer.write(project.getSchema());		     
			    writer.close();
			} catch (IOException e) {
				main.createAlertError("Error when saving", "Error when parsing the pattern.", "Message:"+e.getMessage());
				e.printStackTrace();
			}

		}
	}
	
	
	//----------------------------------------------------
	// Graph menu
	//----------------------------------------------------			

	@FXML
	protected Menu graphMenu;
	@FXML
	protected MenuItem reloadMenuItem;
	@FXML
	protected MenuItem loadMenuItem;
	@FXML
	protected MenuItem clearMenuItem;
	
	protected void initMenuGraph() {
		if (project instanceof WikidataProject) {
			reloadMenuItem.setDisable(true);
			loadMenuItem.setDisable(true);
			clearMenuItem.setDisable(false);
			clearMenuItem.setText("Clear cache");
		} else {
			reloadMenuItem.setDisable(false);
			loadMenuItem.setDisable(false);
			clearMenuItem.setDisable(false);
			clearMenuItem.setText("Clear graph");
		}
	}
	
	
	public void reloadData() {
		if (project!=null) {
			project.getGraph().clear();
			if (project instanceof RDFTypedGraphProject) {
				project.getGraph().loadDataFromFile(((RDFTypedGraphProject) project).getRdfFile());
			}
		}
	}
	

	public void loadDataFile() {
		if (project!=null) {
			File loadFile = main.createFileChooser("Select directory to create repository.");
			if (loadFile!=null) {
				project.getGraph().loadDataFromFile(loadFile);
			}
		}
	}


	public void clear() {
		if (project!=null) {			
			project.getGraph().clear();
		}
	}

	
	//----------------------------------------------------
	// Pattern menu
	//----------------------------------------------------	
	
	@FXML
	protected Menu patternMenu;
	
	
	public void clearPatterns() {
		if (project!=null) project.getListOfPattern().clear();
	}
	
	
	public void loadPatterns() {
		if (project!=null) {
			File loadFile = main.createFileChooser("Select file with patterns",new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
			if (loadFile!=null) {
				try {
					InputStream is = new FileInputStream(loadFile);
					Map<String,Object> jsonObject = (Map<String, Object>) JsonUtils.fromInputStream(is,Charset.defaultCharset().name());
					project.loadListOfPattern((List<Object>) jsonObject.get("patterns"));
					main.getLogger().fine("Patterns loaded from "+loadFile+".");
				} catch ( IOException e) {
					closeProject();
					MainApp.logApp("Error when opening patterns file ", e.getClass()+"  "+e.getMessage());
				}
			}
		}
	}
	
	
	public void savePatterns() {
		if (project!=null) {
			File saveFile = main.createFileChooser("Select file to save patterns");
			if (saveFile!=null) {
				if (!saveFile.getPath().endsWith(".json"))
					saveFile = new File(saveFile.getPath()+".json");
				Map<String,Object> result = new LinkedHashMap<String, Object>();
				result.put("patterns", project.getListOfPattern().stream().map(x -> x.toJson()).collect(Collectors.toList()));
				try {
					BufferedWriter fw = Files.newWriter(saveFile, Charset.defaultCharset());
					JsonUtils.writePrettyPrint(fw, result);
				} catch (IOException e) {
					MainApp.logApp("Error when saving patterns", e.getClass()+"  "+e.getMessage());
				}
			}
		}
	}
	
	

	//----------------------------------------------------
	// Query menu
	//----------------------------------------------------	
	
	@FXML
	protected Menu queryMenu;
	@FXML
	protected MenuItem rdfHelpMenuItem;
	@FXML
	protected MenuItem wdHelpMenuItem;
	
	
	protected void initMenuQuery() {
		if (project instanceof WikidataProject) {
			rdfHelpMenuItem.setDisable(true);
			wdHelpMenuItem.setDisable(false);
		} else {
			rdfHelpMenuItem.setDisable(false);
			wdHelpMenuItem.setDisable(true);
		}
	}
	
	public void clearQueries() {
		if (project!=null) project.getListOfQuery().clear();
	}
	
	public void createQueryForWikidataWithLabel() {
		if (this.project!=null && project instanceof WikidataProject) {
			TextInputDialog dialog = new TextInputDialog("Create query");
			dialog.setTitle("wikidata label");
			dialog.setHeaderText("Enter the wikidata label:");
			dialog.setContentText("Label:");
			Optional<String> result = dialog.showAndWait();
			 
			if (result.isPresent()) {
				Set<RDFTerm> entities = project.getGraph().getNodes(MonadicSparqlQuery.formQueryText("SELECT ?item  WHERE "
						+ "{ ?item <http://www.w3.org/2000/01/rdf-schema#label> \""+result.get()+"\"@en . }", "item"), "");
				RDFTerm selection = null;
				if (entities.size()==0) {
					MainApp.logApp("No entities found with the label: "+result.get(),"");
					return ;
				}
				if (entities.size()==1) {
					selection = entities.stream().collect(Collectors.toList()).get(0);
				}
				if (entities.size()>1) {
					ChoiceDialog<RDFTerm> dialog2 = new ChoiceDialog<>();
					dialog2.getItems().addAll(entities);
					dialog2.setTitle("Create query");
					dialog2.setHeaderText("Multiple Wikidata items has been found with this label");
					dialog2.setContentText("Choose a Wikidata item:");

					Optional<RDFTerm> tmp = dialog2.showAndWait();
					if (tmp.isPresent())
						selection = tmp.get();
				}
				if (selection != null)
					try {
						project.getListOfQuery().add(new NamedQuery("Select 10 "+result.get(),"x", 
								"SELECT DISTINCT ?x WHERE {?x wdt:P31 "+RDFPrintingUtil.toPrettyString(selection, project.parsePrefixes())+". } LIMIT 10"));
					} catch (Exception e) {
						MainApp.logApp("Error when when parsing the prefixes ", e.getClass()+"  "+e.getMessage());
					}
			} 
		}
	}
	
	
	public void generateAllTypeQueries() {
		if (project!=null) {
			try {
				MonadicSparqlQuery query = MonadicSparqlQuery.formQueryText("SELECT ?type WHERE { ?sub <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. } LIMIT 1000000","type");
				Set<RDFTerm> types = project.getGraph().getNodes(query, "");
				for (RDFTerm type:types) {
					project.getListOfQuery().add(new NamedQuery("Select all "+RDFPrintingUtil.toPrettyString(type, project.parsePrefixes()), 
							"x", "SELECT ?x WHERE {?x a "+RDFPrintingUtil.toPrettyString(type, project.parsePrefixes())+". }"));
				}
				System.out.println("Size of getListOfQuery "+project.getListOfQuery().size());
				System.out.println("Size of getListOfPattern "+project.getListOfPattern().size());

			} catch (Exception e) {	
				MainApp.logApp("Error when when parsing the prefixes ", e.getClass()+"  "+e.getMessage());
			}
		}
	}
	
	public void loadQueries() {
		if (project!=null) {
			File loadFile = main.createFileChooser("Select file with queries",new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
			if (loadFile!=null) {
				try {
					InputStream is = new FileInputStream(loadFile);
					Map<String,Object> jsonObject = (Map<String, Object>) JsonUtils.fromInputStream(is,Charset.defaultCharset().name());
					project.loadListOfQuery((List<Object>) jsonObject.get("queries"));
					main.getLogger().fine("Queries loaded from "+loadFile+".");
				} catch ( IOException e) {
					closeProject();
					MainApp.logApp("Error when opening patterns file ", e.getClass()+"  "+e.getMessage());
				}
			}
		}
	}
	
	public void saveQueries() {
		if (project!=null) {
			File saveFile = main.createFileChooser("Select file to save queries");
			if (saveFile!=null) {
				if (!saveFile.getPath().endsWith(".json"))
					saveFile = new File(saveFile.getPath()+".json");
				Map<String,Object> result = new LinkedHashMap<String, Object>();
				result.put("queries", project.getListOfQuery().stream().map(x -> x.toJson()).collect(Collectors.toList()));
				try {
					BufferedWriter fw = Files.newWriter(saveFile, Charset.defaultCharset());
					JsonUtils.writePrettyPrint(fw, result);
				} catch (IOException e) {
					MainApp.logApp("Error when saving queries", e.getClass()+"  "+e.getMessage());
				}
			}
		}
	}
	
	//----------------------------------------------------
	//  Tabs part
	//----------------------------------------------------	
	
	@FXML 
	private TabPane tabs;
	@FXML
	private Tab conceptionTab;
	@FXML
	private Tab validationShexTab;
	@FXML
	private Tab validationShaclTab;
	
	public void tabSelectionChanged() {
		Tab tab = tabs.getSelectionModel().getSelectedItem();
		if (tab!=null) {
			if (tab.equals(validationShexTab)) this.validationShExTabSelected();
			if (tab.equals(validationShaclTab)) this.validationSHACLTabSelected();
		}
	}
	
	
	private void initInterface() {
		if (project!=null) {
			initConception();					
			tabs.getTabs().add(0, conceptionTab);
			
			initMenuQuery();
			initMenuGraph();
			
			if (project.getType().equals(PROJECT_TYPE.SHACL)) {
				this.initSHACLValidation();
				tabs.getTabs().add(1, validationShaclTab);
			}
			if (project.getType().equals(PROJECT_TYPE.SHEX)) {
				this.initShExValidation();
				tabs.getTabs().add(1, validationShexTab);
			}
		}
	}
	
	
	private void closeInterfaceTabs() {
		conceptionTabController=null;
		valShExTab = null;
		valSHACLTab = null;
		tabs.getTabs().clear();
	}

	
	
	//----------------------------------------------------
	//  Conception tab
	//----------------------------------------------------

	
	private ConceptionTabController conceptionTabController;
	
	
	public void initConception() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(main.getClass().getResource("view/main/ConceptionTabLayout.fxml"));
			loader.load();
			conceptionTabController = loader.getController();
			conceptionTabController.initialize(main,project);
			conceptionTab.setContent(conceptionTabController.getBasePane());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	
	
	//----------------------------------------------------
	//  ShEx Validation tab
	//----------------------------------------------------
	
	private ValidationShExTabController valShExTab;
	
	
	public void initShExValidation() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(main.getClass().getResource("view/main/ValidationShExTabLayout.fxml"));
			loader.load();
			valShExTab = loader.getController();
			valShExTab.initialize(main,project);
			validationShexTab.setContent(valShExTab.getBasePane());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void validationShExTabSelected() {
		if (valShExTab != null)
			valShExTab.validationShExTabSelected();
	}

	
	
	//----------------------------------------------------
	//  SHACL Validation tab
	//----------------------------------------------------
	
	private ValidationSHACLTabController valSHACLTab;
	
	
	public void initSHACLValidation() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(main.getClass().getResource("view/main/ValidationSHACLTabLayout.fxml"));
			loader.load();
			valSHACLTab = loader.getController();
			valSHACLTab.initialize(project);
			validationShaclTab.setContent(valSHACLTab.getBasePane());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void validationSHACLTabSelected() {
		if (valSHACLTab != null)
			valSHACLTab.validate();
	}
	
	
	
	//----------------------------------------------------
	//  Logger
	//----------------------------------------------------

	
	@FXML
	private TextArea logArea;

	
	public void updateLog(LogRecord record) {
		DateFormat simple = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
        Date result = new Date(record.getMillis()); 
		logArea.setText(logArea.getText()+simple.format(result)+" : "+record.getMessage()+"\n");
		logArea.end();
	}
	

}
