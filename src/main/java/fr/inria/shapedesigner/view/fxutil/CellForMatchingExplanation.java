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

import java.util.function.Function;

import fr.inria.shapedesigner.model.validationShEx.MatchingElement;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;

public class CellForMatchingExplanation extends TreeTableCell<MatchingElement, String>{
	private Function<MatchingElement, String> textFunction;
	
	
	public CellForMatchingExplanation(Function<MatchingElement, String> textFunction) {
		super();
		this.textFunction = textFunction;
	}

	@Override
	protected void updateItem (String item, boolean empty) {
		super.updateItem(item, true);
		this.setStyle("-fx-background-color:white");
		this.setStyle("-fx-background-color-selected:blue");
		setText("");
		if (item!=null && this.getTreeTableRow().getItem()!=null) {
			MatchingElement itemMe = this.getTreeTableRow().getItem();
			if (textFunction!=null)
				setText(textFunction.apply(itemMe));
			if (itemMe instanceof MatchingElement.MatchedTC ||
					itemMe instanceof MatchingElement.UnmatchedTC ||
					itemMe instanceof MatchingElement.MatchedExtra ||
					itemMe instanceof MatchingElement.UnmatchedTriple ) {
				if (itemMe.getIsComputed()) {
					if (itemMe.getIsError()) {
						this.setStyle("-fx-background-color:lightcoral");
						this.setTooltip(new Tooltip(itemMe.getErrorMessage()));
					} else {
						this.setStyle("-fx-background-color:lightgreen");
					}
				} else {
					this.setStyle("-fx-background-color:lightblue");
				}
			}
		}
	}
}
