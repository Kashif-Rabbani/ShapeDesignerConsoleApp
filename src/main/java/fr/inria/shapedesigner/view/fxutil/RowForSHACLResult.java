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

import fr.inria.shapedesigner.model.validationSHACL.SHACLValidationResult;
import javafx.scene.control.TableRow;

public class RowForSHACLResult extends TableRow<SHACLValidationResult> { 
	
	@Override
    protected void updateItem(SHACLValidationResult item,  boolean empty) {
		super.updateItem(item, empty);
		this.setStyle("-fx-background-color:white");
		this.setStyle("-fx-background-color-selected:blue");
		if  (this.getItem()!=null) {
    		this.setStyle("-fx-background-color:lightcoral");
	    }
	}


}
