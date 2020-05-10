package com.sap.start.run.bean;

import java.util.List;
import java.util.Map;

public class TaBean {
	
	
	String id;
	String name;
	String description;
	Map<String, String> techVarables;
	
	List<Step> steps;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, String> getTechVarables() {
		return techVarables;
	}

	public void setTechVarables(Map<String, String> techVarables) {
		this.techVarables = techVarables;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	
	

}
