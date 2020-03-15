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
package fr.inria.shapedesigner.control;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NamedQuery {
	private StringProperty name= new SimpleStringProperty();
	private StringProperty variable= new SimpleStringProperty();
	private StringProperty description= new SimpleStringProperty();
	private MonadicSparqlQuery query;
	
	public NamedQuery(Map<String,Object> jsonObject) {
		this.setName((String) jsonObject.get("name"));
		this.setVariable((String) jsonObject.get("variable"));
		this.setDescription((String) jsonObject.get("description"));
	}
	
	public NamedQuery(String name, String variable, String description) {
		setName(name);
		setVariable(variable);
		setDescription(description);
	}
	
    public StringProperty getName() {
    	return name;
    }
    
    public StringProperty getDescription() {
    	return description;
    }
    
    public StringProperty getVariable() {
    	return variable;
    }
    
	public void setName(String name) {
		this.name.set(name);
	}
	
	public void setVariable(String variable) {
		this.variable.set(variable);
		this.query = MonadicSparqlQuery.formQueryText(this.description.get(), this.variable.get());
	}

	public void setDescription(String description) {
		this.description.set(description);
		this.query = MonadicSparqlQuery.formQueryText(this.description.get(), this.variable.get());
	}

	public MonadicSparqlQuery getQuery() {
		return query;
	}
	
	@Override
	public String toString() {
		return name.get() + " -- " + description.get() ;
	}
	
	public Object toJson() {
		Map<String,Object> result = new LinkedHashMap<String, Object>();
		result.put("name", this.name.get());
		result.put("variable", this.variable.get());
		result.put("description", this.description.get());
		return result;
	}
    
}
