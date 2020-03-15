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
package fr.inria.shapedesigner;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.inria.shapedesigner.view.main.MainLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainApp extends Application {
	private Logger logger;
	
	private Stage primaryStage;
	
	private Scene sceneBaseForm;
	private MainLayoutController controllerBaseForm;
	
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		logger = Logger.getLogger("ShExApp logger");
		logger.setLevel(Level.FINE);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("ShapeDesigner");
		this.primaryStage.setMaximized(true);
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("view/main/MainLayout.fxml"));
		sceneBaseForm = new Scene(loader.load());
		sceneBaseForm.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		controllerBaseForm = loader.getController();
		controllerBaseForm.setMaster(this);	        

		primaryStage.setScene(sceneBaseForm);
		primaryStage.show();
		logger.fine("Starting the application");
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	public File createFileChooser(String title, FileChooser.ExtensionFilter... extensions) {
		FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(extensions);
		return fileChooser.showOpenDialog(this.getPrimaryStage());		
	}
	
	
	public File createFileChooserSave(String title, FileChooser.ExtensionFilter... extensions) {
		FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(extensions);
		return fileChooser.showSaveDialog(this.getPrimaryStage());
	}
	
	
	public File createDirectoryChooserSave(String title) {
		DirectoryChooser fileChooser = new DirectoryChooser();
    	fileChooser.setTitle(title);
		return fileChooser.showDialog(this.getPrimaryStage());
	}
	
	
	public void createAlertError(String title, String header,String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setResizable(true);
    	alert.setTitle(title);
    	alert.setHeaderText(header);
    	alert.setContentText(message);
    	alert.showAndWait();
	}
	
	
	public void createAlertMessage(String title, String header,String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setResizable(true);
    	alert.setTitle(title);
    	alert.setHeaderText(header);
    	alert.setContentText(message);
    	alert.showAndWait();
	}
	
	
	public static void logApp (String title, String message) {
		Logger logger = Logger.getLogger("ShExApp logger");
		if (logger!=null)
			logger.fine(title+": "+message);
	}	
	////-------------------------------------
	//// Getter and setter
	////-------------------------------------


	public Stage getPrimaryStage() {
		return primaryStage;
	}

	
	public Logger getLogger() {
		return logger;
	}

	
}
