package com.sap.start.run.bean;

public class Step {

	String tempId;
	String stepName;
	String product;
	String app;
	String screen;
	String screenSelector;
	String subSelector;
	String action;
	String parameterType;
	String parameter;
	int browserSession;
	String techUISelector;
	String techUIMethod;
	String techIdletime;
	String optional;

	public String getOptional() {
		return optional;
	}

	public void setOptional(String optional) {
		this.optional = optional;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public String getScreenSelector() {
		return screenSelector;
	}

	public void setScreenSelector(String screenSelector) {
		this.screenSelector = screenSelector;
	}

	public String getSubSelector() {
		return (null == subSelector) ? "" : subSelector;
	}

	public void setSubSelector(String subSelector) {
		this.subSelector = subSelector;
	}

	public String getAction() {
		return (null == action) ? "input" : action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getParameter() {
		return (null == parameter) ? "" : parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public int getBrowserSession() {
		return browserSession;
	}

	public void setBrowserSession(int browserSession) {
		this.browserSession = browserSession;
	}

	public String getTechUISelector() {
		return (null == techUISelector) ? "" : techUISelector;
	}

	public void setTechUISelector(String techUISelector) {
		this.techUISelector = techUISelector;
	}

	public String getTechUIMethod() {
		return techUIMethod;
	}

	public void setTechUIMethod(String techUIMethod) {
		this.techUIMethod = techUIMethod;
	}
	
	public String getTechIdletime() {
		return (null == techIdletime) ? "0" : techIdletime;
	}

	public void setTechIdletime(String techIdletime) {
		this.techIdletime = techIdletime;
	}

}
