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

import org.apache.commons.rdf.api.RDFTerm;

import fr.inria.lille.shexjava.schema.abstrsynt.ShapeExpr;
import fr.inria.shapedesigner.model.validationShEx.MatchingElement;
import fr.inria.shapedesigner.model.validationShEx.MatchingElement.BaseMatchingElementTE;
import fr.inria.shapedesigner.view.main.ValidationShExTabController;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class RowForMatchingExplanation extends TreeTableRow<MatchingElement>{
	private ContextMenu rowMenu;	
	
	public RowForMatchingExplanation(ValidationShExTabController main) {
		rowMenu = new ContextMenu();
		
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new MenuForRowEventHandler(main, this));
		
		this.contextMenuProperty().bind(
                Bindings.when(Bindings.isNotNull(this.itemProperty()))
                .then(rowMenu)
                .otherwise((ContextMenu)null));
	}

	public ContextMenu getRowMenu() {
		return rowMenu;
	}

}

class MenuForRowEventHandler implements EventHandler<MouseEvent>{
	private ValidationShExTabController main;
	private RowForMatchingExplanation row;
	
	public MenuForRowEventHandler(ValidationShExTabController main, RowForMatchingExplanation row) {
		this.main = main;
		this.row = row;
	}
	
    @Override
    public void handle(MouseEvent t) {
        if(t.getButton() == MouseButton.SECONDARY) {
        	if (row.getItem()!=null) {
	        	row.getRowMenu().getItems().clear();
    	
	        	MenuItem editItem = new MenuItem("Edit Triple");
	        	editItem.setOnAction(new EventHandler<ActionEvent>() {
	        		@Override
	        		public void handle(ActionEvent event) {
	        			main.editRow(row);
	        		}
	        	});
    	
	        	MenuItem removeItem = new MenuItem("Remove Triple");
	        	removeItem.setOnAction(new EventHandler<ActionEvent>() {
	        		@Override
	        		public void handle(ActionEvent event) {
	        			main.removeRow(row);
	        		}
	        	});
	        	
	        	if (row.getItem() instanceof MatchingElement.BaseMatchingElementTE) {
	        		MatchingElement.BaseMatchingElementTE rowItem = (BaseMatchingElementTE) row.getItem();
	        		if (rowItem.getTriple()!=null && rowItem.getInvestigations().size()>0) {
	        			RDFTerm node = rowItem.getTriple().getObject();
	        			Menu investigateMenu = new Menu("Investigate with");
	        			for (ShapeExpr shape:rowItem.getInvestigations()) {
	        				if (main.getValidationRun().getMatchingExplanation(node, shape.getId())!=null) {
	        					MenuItem investigation = new MenuItem(shape.getId().toPrettyString(main.getValidationRun().getPrefixes()));
	        					investigation.setOnAction(new EventHandler<ActionEvent>() {
	        						@Override
	        						public void handle(ActionEvent event) {
	        							main.investigateRow(node,shape.getId());
	        						}
	        					});
	        					investigateMenu.getItems().add(investigation);
	        				}
	        			}
	        			if (investigateMenu.getItems().size()>0)
	        				row.getRowMenu().getItems().addAll(investigateMenu);
	        		}
	        	}
	        	row.getRowMenu().getItems().addAll(editItem,removeItem);
        	}
        }
    }
}