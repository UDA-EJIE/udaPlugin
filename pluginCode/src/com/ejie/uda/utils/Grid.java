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

public class Grid {

	// Propiedades del grid
	private String tableName;
	private boolean hasMaint;
	
	private String url;
	private boolean multiSelect;
	private String width;
	private String pagerName;
	private boolean loadOnStartUp;
	private String rowNum;
	private boolean sortable;
	private String sortOrder;
	private String sortName;
	private boolean rowEdit;
	private String alias;
	
	// Eventos del grid
	private String beforeRequest;
	private String loadBeforeSend;
	private String gridComplete;
	private String loadComplete;
	private String ondblClickRow;
	private String onSelectAll;
	private String onSelectRow;
	
	/**
	 * Constructor
	 * 
	 */
	public Grid() {
		
	}
	
	/**
	 * Constructor
	 * 
	 */
	public Grid(String tableName, boolean hasMaint, String url, boolean multiSelect, String width,
			String pagerName, boolean loadOnStartUp, String rowNum,
			boolean sortable, String sortOrder, String sortName,
			boolean rowEdit, String alias, String beforeRequest, String loadBeforeSend,
			String gridComplete, String loadComplete, String ondblClickRow,
			String onSelectAll, String onSelectRow) {
		this.tableName = tableName;
		this.hasMaint = hasMaint;
		this.url = url;
		this.multiSelect = multiSelect;
		this.width = width;
		this.pagerName = pagerName;
		this.loadOnStartUp = loadOnStartUp;
		this.rowNum = rowNum;
		this.sortable = sortable;
		this.sortOrder = sortOrder;
		this.sortName = sortName;
		this.rowEdit = rowEdit;
		this.alias = alias;
		this.beforeRequest = beforeRequest;
		this.loadBeforeSend = loadBeforeSend;
		this.gridComplete = gridComplete;
		this.loadComplete = loadComplete;
		this.ondblClickRow = ondblClickRow;
		this.onSelectAll = onSelectAll;
		this.onSelectRow = onSelectRow;
	}

	// Getters & Setters
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public boolean isHasMaint() {
		return hasMaint;
	}

	public void setHasMaint(boolean hasMaint) {
		this.hasMaint = hasMaint;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getPagerName() {
		return pagerName;
	}

	public void setPagerName(String pagerName) {
		this.pagerName = pagerName;
	}

	public boolean isLoadOnStartUp() {
		return loadOnStartUp;
	}

	public void setLoadOnStartUp(boolean loadOnStartUp) {
		this.loadOnStartUp = loadOnStartUp;
	}

	public String getRowNum() {
		return rowNum;
	}

	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public boolean isRowEdit() {
		return rowEdit;
	}

	public void setRowEdit(boolean rowEdit) {
		this.rowEdit = rowEdit;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getBeforeRequest() {
		return beforeRequest;
	}

	public void setBeforeRequest(String beforeRequest) {
		this.beforeRequest = beforeRequest;
	}
	
	public String getLoadBeforeSend() {
		return loadBeforeSend;
	}
	
	public void setLoadBeforeSend(String loadBeforeSend) {
		this.loadBeforeSend = loadBeforeSend;
	}
	
	public String getGridComplete() {
		return gridComplete;
	}
	
	public void setGridComplete(String gridComplete) {
		this.gridComplete = gridComplete;
	}
	
	public String getLoadComplete() {
		return loadComplete;
	}
	
	public void setLoadComplete(String loadComplete) {
		this.loadComplete = loadComplete;
	}
	
	public String getOndblClickRow() {
		return ondblClickRow;
	}
	
	public void setOndblClickRow(String ondblClickRow) {
		this.ondblClickRow = ondblClickRow;
	}
	
	public String getOnSelectAll() {
		return onSelectAll;
	}
	
	public void setOnSelectAll(String onSelectAll) {
		this.onSelectAll = onSelectAll;
	}
	
	public String getOnSelectRow() {
		return onSelectRow;
	}
	
	public void setOnSelectRow(String onSelectRow) {
		this.onSelectRow = onSelectRow;
	}
	
	public String printGridProperties(){
		
		StringBuffer properties = new StringBuffer();
		
		if (!isLoadOnStartUp()){
			properties.append("loadOnStartUp: " + isLoadOnStartUp() + ",");
		}
		if (isHasMaint()){
			properties.append("\n\t\thasMaint: " + isHasMaint() + ",");
		}
		if (!Utilities.isBlank(getWidth())){
			properties.append("\n\t\twidth: \"" + getWidth() + "\",");
		}
		if (!Utilities.isBlank(getUrl())){
			properties.append("\n\t\turl: \"" + getUrl() + "\",");
		}
		if (!Utilities.isBlank(getPagerName())){
			properties.append("\n\t\tpagerName: \"" + getPagerName() + "\",");
		}
		if (isMultiSelect()){
			properties.append("\n\t\tmultiselect: " + isMultiSelect() + ",");
		}
		if (!Utilities.isBlank(getRowNum())){
			properties.append("\n\t\trowNum: \"" + getRowNum() + "\",");
		}
		if (!isSortable()){
			properties.append("\n\t\tsortable: " + isSortable() + ",");
		}
		if (!Utilities.isBlank(getSortOrder())){
			properties.append("\n\t\tsortorder: \"" + getSortOrder() + "\",");
		}
		if (!Utilities.isBlank(getSortName())){
			properties.append("\n\t\tsortname: \"" + getSortName() + "\",");
		}
		if (isRowEdit()){
			properties.append("\n\t\teditable: " + isRowEdit() + ",");
		}
		
		return properties.toString(); 
	}
	
	public String printGridEvents(){
		
		StringBuffer events = new StringBuffer();
		
		if (!Utilities.isBlank(getBeforeRequest())){
			events.append("beforeRequest: \"" + getBeforeRequest() + "\",");
		}
		if (!Utilities.isBlank(getLoadBeforeSend())){
			events.append("\n\t\tloadBeforeSend: \"" + getLoadBeforeSend() + "\",");
		}
		if (!Utilities.isBlank(getGridComplete())){
			events.append("\n\t\tgridComplete: \"" + getGridComplete() + "\",");
		}
		if (!Utilities.isBlank(getLoadComplete())){
			events.append("\n\t\tloadComplete: \"" + getLoadComplete() + "\",");
		}
		if (!Utilities.isBlank(getOndblClickRow())){
			events.append("\n\t\tondblClickRow: \"" + getOndblClickRow() + "\",");
		}
		if (!Utilities.isBlank(getOnSelectRow())){
			events.append("\n\t\tonSelectRow: \"" + getOnSelectRow() + "\",");
		}
		if (!Utilities.isBlank(getOnSelectAll())){
			events.append("\n\t\tonSelectAll: \"" + getOnSelectAll() + "\",");
		}
		
		//Quita la última coma sobrante
		events = new StringBuffer(Utilities.removeFinalComma(events.toString()));

		return events.toString(); 
	}
	
	public boolean hasGridEvents(){
		
		boolean hasEvents = false;
		
		if (!Utilities.isBlank(getBeforeRequest()) || !Utilities.isBlank(getLoadBeforeSend()) ||!Utilities.isBlank(getGridComplete())
				|| !Utilities.isBlank(getLoadComplete()) || !Utilities.isBlank(getOndblClickRow()) ||!Utilities.isBlank(getOnSelectAll())
				|| !Utilities.isBlank(getOnSelectRow())){
			
			hasEvents = true;
		}

		return hasEvents;
	}
}
