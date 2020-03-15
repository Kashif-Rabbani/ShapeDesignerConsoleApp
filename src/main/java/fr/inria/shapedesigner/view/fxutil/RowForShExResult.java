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
package fr.inria.shapedesigner.view.fxutil;


import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import fr.inria.shapedesigner.model.validationShEx.MatchingExplanation;
import fr.inria.shapedesigner.view.main.ValidationShExTabController;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class RowForShExResult extends TableRow<MatchingExplanation> { 
	private ValidationShExTabController controller;
	private ContextMenu rowMenu;	

	
	public RowForShExResult(ValidationShExTabController validationShExTabController){
		this.setOnMouseClicked(new SelectRowEvent(this));
		this.controller = validationShExTabController;

		rowMenu = new ContextMenu();
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new MenuForShExResultRowEventHandler(this));
		this.contextMenuProperty().bind(
				Bindings.when(Bindings.isNotNull(this.itemProperty()))
				.then(rowMenu)
				.otherwise((ContextMenu)null));
	}

	@Override
	protected void updateItem(MatchingExplanation item,  boolean empty) {
		super.updateItem(item, empty);
		this.setStyle("-fx-background-color:white");
		this.setStyle("-fx-background-color-selected:blue");
		if  (this.getItem()!=null) {
			if (this.getItem().isResult())
				this.setStyle("-fx-background-color:lightgreen");
			else
				this.setStyle("-fx-background-color:lightcoral");
		}
	}

	public ValidationShExTabController getController() {
		return controller;
	}

	public ContextMenu getRowMenu() {
		return rowMenu;
	}


}

class SelectRowEvent implements EventHandler<MouseEvent>{
	private RowForShExResult row;

	public SelectRowEvent(RowForShExResult row) {
		this.row = row;
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getClickCount() == 1 && (!row.isEmpty())) {
			MatchingExplanation rowData = row.getItem();
			row.getController().selectRow(rowData);
		}
	}
}

class MenuForShExResultRowEventHandler implements EventHandler<MouseEvent>{
	private RowForShExResult row;

	public MenuForShExResultRowEventHandler(RowForShExResult row) {
		this.row = row;
	}

	@Override
	public void handle(MouseEvent t) {
		if(t.getButton() == MouseButton.SECONDARY) {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				if (row.getItem()!=null) {
					row.getRowMenu().getItems().clear();

					MenuItem editItem = new MenuItem("go to "+row.getItem().getNode());
					editItem.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							try {
								if( Desktop.isDesktopSupported() )
								{
									new Thread(() -> {
										try {
											Desktop.getDesktop().browse( new URI( row.getItem().getNode().toString() ) );
										} catch (IOException | URISyntaxException e1) {
											e1.printStackTrace();
										}
									}).start();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});

					row.getRowMenu().getItems().addAll(editItem);
				}
			}
		}
	}
}