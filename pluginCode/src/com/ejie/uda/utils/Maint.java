package com.ejie.uda.utils;

public class Maint {

	// Propiedades del mantenimiento
	private String nameMaint;
	private String titleMaint;
	private String detailMaint;
	private String detailMaintButtons;
	private String searchMaint;
	private String toolbarMaint;
	private boolean toolbarMaintAutoSize;
	private boolean toolbarMaintButtonsDefault;
	private String feedbackMaint;
	private boolean feedbackMaintShowAll;
	private boolean feedbackMaintCollapsible;
	private String imgPathMaint;
	private boolean validationModeMaint;
	private boolean detailServerMaint;
	private String modelObject;
	private String primaryKey;

	// Propiedades de los eventos del mantenimiento
	private String eventOnbeforeDetailShow;
	private String eventOnafterDetailShow;
	
	
	/**
	 * Constructores
	 */
	public Maint() {
	}
	
	public Maint(String nameMaint, String titleMaint) {
		this.nameMaint = nameMaint;
		this.titleMaint = titleMaint;
	}
	
	public Maint(String nameMaint, String titleMaint, String detailMaint,
			String detailMaintButtons, String searchMaint, String toolbarMaint,
			boolean toolbarMaintAutoSize, boolean toolbarMaintButtonsDefault,
			String feedbackMaint, boolean feedbackMaintShowAll, boolean feedbackMaintCollapsible,
			String imgPathMaint, boolean validationModeMaint, boolean detailServerMaint, String modelObject, String primaryKey,
			String eventOnbeforeDetailShow, String eventOnafterDetailShow) {
		this.nameMaint = nameMaint;
		this.titleMaint = titleMaint;
		this.detailMaint = detailMaint;
		this.detailMaintButtons = detailMaintButtons;
		this.searchMaint = searchMaint;
		this.toolbarMaint = toolbarMaint;
		this.toolbarMaintAutoSize = toolbarMaintAutoSize;
		this.toolbarMaintButtonsDefault = toolbarMaintButtonsDefault;
		this.feedbackMaint = feedbackMaint;
		this.feedbackMaintShowAll = feedbackMaintShowAll;
		this.feedbackMaintCollapsible = feedbackMaintCollapsible;
		this.imgPathMaint = imgPathMaint;
		this.validationModeMaint = validationModeMaint;
		this.detailServerMaint = detailServerMaint;
		this.modelObject = modelObject;
		this.primaryKey = primaryKey;
		this.eventOnbeforeDetailShow = eventOnbeforeDetailShow;
		this.eventOnafterDetailShow = eventOnafterDetailShow;
	}

	/**
	 * Getters y setters
	 */
	public String getNameMaint() {
		return nameMaint;
	}

	public void setNameMaint(String nameMaint) {
		this.nameMaint = nameMaint;
	}
	
	public String getTitleMaint() {
		return titleMaint;
	}

	public void setTitleMaint(String titleMaint) {
		this.titleMaint = titleMaint;
	}

	public String getDetailMaint() {
		return detailMaint;
	}

	public void setDetailMaint(String detailMaint) {
		this.detailMaint = detailMaint;
	}

	public String getDetailMaintButtons() {
		return detailMaintButtons;
	}

	public void setDetailMaintButtons(String detailMaintButtons) {
		this.detailMaintButtons = detailMaintButtons;
	}

	public String getSearchMaint() {
		return searchMaint;
	}

	public void setSearchMaint(String searchMaint, boolean searchMaintAutomatic) {
		if (searchMaintAutomatic){
			this.searchMaint = "searchForm";
		}else{
			this.searchMaint = searchMaint;	
		}
	}

	public String getToolbarMaint() {
		return toolbarMaint;
	}

	public void setToolbarMaint(String toolbarMaint) {
		this.toolbarMaint = toolbarMaint;
	}

	public boolean isToolbarMaintAutoSize() {
		return toolbarMaintAutoSize;
	}

	public void setToolbarMaintAutoSize(boolean toolbarMaintAutoSize) {
		this.toolbarMaintAutoSize = toolbarMaintAutoSize;
	}

	public boolean isToolbarMaintButtonsDefault() {
		return toolbarMaintButtonsDefault;
	}

	public void setToolbarMaintButtonsDefault(boolean toolbarMaintButtonsDefault) {
		this.toolbarMaintButtonsDefault = toolbarMaintButtonsDefault;
	}

	public String getFeedbackMaint() {
		return feedbackMaint;
	}

	public void setFeedbackMaint(String feedbackMaint) {
		this.feedbackMaint = feedbackMaint;
	}

	public boolean isFeedbackMaintShowAll() {
		return feedbackMaintShowAll;
	}

	public void setFeedbackMaintShowAll(boolean feedbackMaintShowAll) {
		this.feedbackMaintShowAll = feedbackMaintShowAll;
	}

	public boolean isFeedbackMaintCollapsible() {
		return feedbackMaintCollapsible;
	}

	public void setFeedbackMaintCollapsible(boolean feedbackMaintCollapsible) {
		this.feedbackMaintCollapsible = feedbackMaintCollapsible;
	}
	
	public String getImgPathMaint() {
		return imgPathMaint;
	}

	public void setImgPathMaint(String imgPathMaint) {
		this.imgPathMaint = imgPathMaint;
	}

	public boolean isValidationModeMaint() {
		return validationModeMaint;
	}

	public void setValidationModeMaint(boolean validationModeMaint) {
		this.validationModeMaint = validationModeMaint;
	}
	
	public boolean isDetailServerMaint() {
		return detailServerMaint;
	}

	public void setDetailServerMaint(boolean detailServerMaint) {
		this.detailServerMaint = detailServerMaint;
	}

	public String getModelObject() {
		return modelObject;
	}

	public void setModelObject(String modelObject) {
		this.modelObject = modelObject;
	}
	
	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getEventOnbeforeDetailShow() {
		return eventOnbeforeDetailShow;
	}

	public void setEventOnbeforeDetailShow(String eventOnbeforeDetailShow) {
		this.eventOnbeforeDetailShow = eventOnbeforeDetailShow;
	}

	public String getEventOnafterDetailShow() {
		return eventOnafterDetailShow;
	}

	public void setEventOnafterDetailShow(String eventOnafterDetailShow) {
		this.eventOnafterDetailShow = eventOnafterDetailShow;
	}
	
	public String printMaintProperties(){
		
		StringBuffer properties = new StringBuffer();
		
		if (!Utilities.isBlank(getNameMaint())){
			properties.append("jQueryGrid: \"GRID_" + getNameMaint() + "\",");
		}
		if (!Utilities.isBlank(getPrimaryKey())){
			properties.append("\n\t\tprimaryKey: \"" + getPrimaryKey().toLowerCase() + "\",");
		}
		if (!Utilities.isBlank(getModelObject())){
			properties.append("\n\t\tmodelObject: \"" + getModelObject() + "\",");
		}
		if (!Utilities.isBlank(getImgPathMaint()) && !"/rup/basic-theme/images".equals(getImgPathMaint())){
				properties.append("\n\t\timgPath: \"" + getImgPathMaint() + "\",");
		}
		if (!Utilities.isBlank(getDetailMaint())){
			properties.append("\n\t\tdetailForm: \"" + getDetailMaint() + "\",");
		}
		if (!Utilities.isBlank(getDetailMaintButtons())){
			properties.append("\n\t\tdetailButtons: $.rup.maint.detailButtons." + getDetailMaintButtons() + ",");
		}
		if (!Utilities.isBlank(getSearchMaint())){
			properties.append("\n\t\tsearchForm: \"" + getSearchMaint() + "\",");
		}
		if (!Utilities.isBlank(getToolbarMaint())){
			properties.append("\n\t\ttoolbar: \"" + getToolbarMaint() + "\",");
			if (!isToolbarMaintButtonsDefault()){
				properties.append("\n\t\tcreateDefaultToolButtons: " + isToolbarMaintButtonsDefault() + ",");
			}
		}
		if (!isToolbarMaintAutoSize()){
			properties.append("\n\t\tautoAjustToolbar: " + isToolbarMaintAutoSize() + ",");
		}
		if (!Utilities.isBlank(getFeedbackMaint())){
			properties.append("\n\t\tfeedback: \"" + getFeedbackMaint() + "\",");
		}
		if (isFeedbackMaintShowAll()){
			properties.append("\n\t\tshowMessages: " + isFeedbackMaintShowAll() + ",");
		}
		if (isFeedbackMaintCollapsible()){
			properties.append("\n\t\tshowFeedback: " + isFeedbackMaintCollapsible() + ",");
		}
		if (!isValidationModeMaint()){
			properties.append("\n\t\tvalidationMode: \"\",");
		}
		if (!isDetailServerMaint()){
			properties.append("\n\t\tdetailServer: " + isDetailServerMaint() + ",");
		}

		//Quita la última coma sobrante
		properties = new StringBuffer(Utilities.removeFinalComma(properties.toString()));
		
		return properties.toString(); 
	}
	
	public String printMaintEvents(){
		
		StringBuffer events = new StringBuffer();
		
		if (!Utilities.isBlank(getEventOnbeforeDetailShow())){
			events.append("onbeforeDetailShow: \"" + getEventOnbeforeDetailShow() + "\",");
		}
		if (!Utilities.isBlank(getEventOnafterDetailShow())){
			events.append("\n\t\tonafterDetailShow: \"" + getEventOnafterDetailShow() + "\",");
		}
		
		//Quita la última coma sobrante
		events = new StringBuffer(Utilities.removeFinalComma(events.toString()));

		return events.toString(); 
	}
	
	public boolean hasMaintEvents(){
		
		boolean hasEvents = false;
		
		if (!Utilities.isBlank(getEventOnbeforeDetailShow()) || !Utilities.isBlank(getEventOnafterDetailShow())){
			hasEvents = true;
		}

		return hasEvents;
	}
}