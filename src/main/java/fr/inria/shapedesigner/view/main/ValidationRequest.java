package fr.inria.shapedesigner.view.main;

import fr.inria.lille.shexjava.schema.Label;
import fr.inria.shapedesigner.control.NamedQuery;
import javafx.beans.property.SimpleObjectProperty;

public class ValidationRequest {
	private SimpleObjectProperty<Label> shapeLabel;
	private SimpleObjectProperty<NamedQuery> nodeRequest;
	
	public ValidationRequest() {
		super();
		this.shapeLabel = new SimpleObjectProperty<Label>();
		this.nodeRequest = new SimpleObjectProperty<NamedQuery>();		
	}


	public SimpleObjectProperty<NamedQuery> getNodeRequest() {
		return nodeRequest;
	}

	public SimpleObjectProperty<Label> getShapeLabel() {
		return shapeLabel;
	}
	
}
