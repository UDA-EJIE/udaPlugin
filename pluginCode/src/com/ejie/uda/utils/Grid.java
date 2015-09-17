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
	private String alias;
	private boolean loadOnStartUp;
	private String sortOrder;
	private String sortName;
	
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
	public Grid(String tableName, boolean hasMaint, String url, String alias,
			boolean loadOnStartUp, String sortOrder, String sortName) {
		super();
		this.tableName = tableName;
		this.hasMaint = hasMaint;
		this.url = url;
		this.alias = alias;
		this.loadOnStartUp = loadOnStartUp;
		this.sortOrder = sortOrder;
		this.sortName = sortName;
	}
	

	// Getters & Setters
	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public boolean getHasMaint() {
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


	public boolean getLoadOnStartUp() {
		return loadOnStartUp;
	}

	public void setLoadOnStartUp(boolean loadOnStartUp) {
		this.loadOnStartUp = loadOnStartUp;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
