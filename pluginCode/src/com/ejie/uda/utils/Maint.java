/*
* Copyright 2012 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.uda.utils;

public class Maint {

	// Propiedades del mantenimiento
	private String modelObject;
	private String primaryKey;
	private String nameMaint;
	private String titleMaint;
	private boolean isMaint;
	private String typeMaint;
	private boolean detailServerMaint;
	private String detailMaintButtons;
	private boolean feedbackMaintCollapsible;
	private boolean toolBarButtonsMaint;
	private boolean contextMenuMaint;
	private boolean fluidMaint;
	private boolean filterMaint;
	private boolean searchMaint;
	private boolean clientValidationMaint;
	private boolean multiSelectMaint;
	private boolean hierarchyMaint;

	/**
	 * Constructores
	 */
	public Maint() {
	}
	
	public Maint(String nameMaint, String titleMaint) {
		this.nameMaint = nameMaint;
		this.titleMaint = titleMaint;
	}
	
	public Maint(String modelObject, String primaryKey, String nameMaint,
			String titleMaint, boolean isMaint, String typeMaint,
			boolean detailServerMaint, String detailMaintButtons,
			boolean feedbackMaintCollapsible, boolean toolBarButtonsMaint,
			boolean multiSelectMaint, boolean filterMaint, boolean searchMaint,
			boolean hierarchyMaint, boolean fluidMaint,
			boolean clientValidationMaint, boolean contextMenuMaint) {
		super();
		this.modelObject = modelObject;
		this.primaryKey = primaryKey;
		this.nameMaint = nameMaint;
		this.titleMaint = titleMaint;
		this.isMaint = isMaint;
		this.typeMaint = typeMaint;
		this.detailServerMaint = detailServerMaint;
		this.detailMaintButtons = detailMaintButtons;
		this.feedbackMaintCollapsible = feedbackMaintCollapsible;
		this.toolBarButtonsMaint = toolBarButtonsMaint;
		this.multiSelectMaint = multiSelectMaint;
		this.filterMaint = filterMaint;
		this.searchMaint = searchMaint;
		this.hierarchyMaint = hierarchyMaint;
		this.fluidMaint = fluidMaint;
		this.clientValidationMaint = clientValidationMaint;
		this.contextMenuMaint = contextMenuMaint;
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

	public String getDetailMaintButtons() {
		return detailMaintButtons;
	}

	public void setDetailMaintButtons(String detailMaintButtons) {
		this.detailMaintButtons = detailMaintButtons;
	}

	public boolean getFeedbackMaintCollapsible() {
		return feedbackMaintCollapsible;
	}

	public void setFeedbackMaintCollapsible(boolean feedbackMaintCollapsible) {
		this.feedbackMaintCollapsible = feedbackMaintCollapsible;
	}
	
	public boolean getDetailServerMaint() {
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

	public void setIsMaint(boolean isMaint) {
		this.isMaint = isMaint;
	}

	public boolean getIsMaint() {
		return isMaint;
	}

	public void setTypeMaint(String typeMaint) {
		this.typeMaint = typeMaint;
	}

	public String getTypeMaint() {
		return typeMaint;
	}

	public void setToolBarButtonsMaint(boolean toolBarButtonsMaint) {
		this.toolBarButtonsMaint = toolBarButtonsMaint;
	}

	public boolean getToolBarButtonsMaint() {
		return toolBarButtonsMaint;
	}

	public void setMultiSelectMaint(boolean multiSelectMaint) {
		this.multiSelectMaint = multiSelectMaint;
	}

	public boolean getMultiSelectMaint() {
		return multiSelectMaint;
	}

	public void setFilterMaint(boolean filterMaint) {
		this.filterMaint = filterMaint;
	}

	public boolean getFilterMaint() {
		return filterMaint;
	}

	public void setHierarchyMaint(boolean hierarchyMaint) {
		this.hierarchyMaint = hierarchyMaint;
	}

	public boolean getHierarchyMaint() {
		return hierarchyMaint;
	}

	public void setContextMenuMaint(boolean contextMenuMaint) {
		this.contextMenuMaint = contextMenuMaint;
	}

	public boolean getContextMenuMaint() {
		return contextMenuMaint;
	}
	
	public void setFluidMaint(boolean fluidMaint) {
		this.fluidMaint = fluidMaint;
	}

	public boolean getFluidMaint() {
		return fluidMaint;
	}

	public void setClientValidationMaint(boolean clientValidationMaint) {
		this.clientValidationMaint = clientValidationMaint;
	}

	public boolean getClientValidationMaint() {
		return clientValidationMaint;
	}

	public void setSearchMaint(boolean searchMaint) {
		this.searchMaint = searchMaint;
	}

	public boolean getSearchMaint() {
		return searchMaint;
	}

}