/*
* Copyright 2012 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la Â«LicenciaÂ»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye Â«TAL CUALÂ»,
* SIN GARANTÃ�AS NI CONDICIONES DE NINGÃšN TIPO, ni expresas ni implÃ­citas.
* VÃ©ase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.uda.utils;


public class GridColumn {

	// Propiedades de la columna de un grid
	private String tableName;
	private String columnName;
	private String columnNameBBDD;

	// INTEGER, SQLXML, BLOB, VARBINARY, OTHER, DATALINK, LONGNVARCHAR, NCHAR,
	// LONGVARBINARY, NULL, CLOB, CHAR, VARCHAR, STRUCT, FLOAT, NUMERIC, NCLOB,
	// REF, REAL, TIME, BOOLEAN, DECIMAL, LONGVARCHAR, BIGINT, JAVA_OBJECT,
	// ROWID, TINYINT, DOUBLE, BIT, BINARY, DATE, NVARCHAR, DISTINCT, TIMESTAMP,
	// ARRAY, SMALLINT
	private String JDBCTypeName;
	private int length;
	private int scale;
	private int precision;

	// Propiedades básicas
	private String name;
	private String label;
	private String align;
	private int alignIndex;
	private String width;
	private boolean editable;

	// Propiedades avanzadas
	private String editType;
	private int editTypeIndex;
	private String rupType;
	private boolean hidden;
	private boolean resizable;
	private boolean sortable;

	// Grupo editRules
	private boolean enableEditRules;
	private boolean editHiddenEditRules;
	private boolean requiredEditRules;
	// "",number,integer,email,url,date,time
	private String typeEditRules;
	private int typeEditRulesIndex;
	private String minValueEditRules;
	private String maxValueEditRules;
	
	//Propiedad de activación
	private boolean activated;
	
	private boolean primaryKey = false;

	/**
	 * Contructor
	 */
	
	public GridColumn(){
		
	}
			
	public GridColumn(String tableName, String columnName, String align,
			int alignIndex, boolean editable, String rupType,  
			boolean enableEditRules, boolean editHiddenEditRules,
			boolean requiredEditRules, String typeEditRules,
			String minValueEditRules, String maxValueEditRules,
			String editType, int editTypeIndex, boolean hidden,
			String index, String label, String name, boolean resizable,
			boolean sortable, String width, boolean activated, boolean primaryKey) {
		this.tableName = tableName;
		this.columnName = columnName;
		this.align = align;
		this.alignIndex = alignIndex;
		this.editable = editable;
		this.rupType = rupType;
		this.enableEditRules = enableEditRules;
		this.editHiddenEditRules = editHiddenEditRules;
		this.requiredEditRules = requiredEditRules;
		this.typeEditRules = typeEditRules;
		this.minValueEditRules = minValueEditRules;
		this.maxValueEditRules = maxValueEditRules;
		this.editType = editType;
		this.editTypeIndex = editTypeIndex;
		this.hidden = hidden;
		this.label = label;
		this.name = name;
		this.resizable = resizable;
		this.sortable = sortable;
		this.width = width;
		this.activated = activated;
		this.primaryKey = primaryKey;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnNameBBDD() {
		return columnNameBBDD;
	}

	public void setColumnNameBBDD(String columnNameBBDD) {
		this.columnNameBBDD = columnNameBBDD;
	}

	/**
	 * @return the jDBCTypeName
	 */
	public String getJDBCTypeName() {
		return JDBCTypeName;
	}

	/**
	 * @param jDBCTypeName the jDBCTypeName to set
	 */
	public void setJDBCTypeName(String jDBCTypeName) {
		JDBCTypeName = jDBCTypeName;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}
	
	public int getAlignIndex() {
		return alignIndex;
	}

	public void setAlignIndex(int alignIndex) {
		this.alignIndex = alignIndex;
	}

	public boolean getEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getRupType() {
		return rupType;
	}

	public void setRupType(String rupType) {
		this.rupType = rupType;
	}

	
	public boolean getEnableEditRules() {
		return enableEditRules;
	}

	public void setEnableEditRules(boolean enableEditRules) {
		this.enableEditRules = enableEditRules;
	}

	public boolean isEditHiddenEditRules() {
		return editHiddenEditRules;
	}

	public void setEditHiddenEditRules(boolean editHiddenEditRules) {
		this.editHiddenEditRules = editHiddenEditRules;
	}

	public boolean getRequiredEditRules() {
		return requiredEditRules;
	}

	public void setRequiredEditRules(boolean requiredEditRules) {
		this.requiredEditRules = requiredEditRules;
	}

	public String getTypeEditRules() {
		return typeEditRules;
	}

	public void setTypeEditRules(String typeEditRules) {
		this.typeEditRules = typeEditRules;
	}

	public int getTypeEditRulesIndex() {
		return typeEditRulesIndex;
	}

	public void setTypeEditRulesIndex(int typeEditRulesIndex) {
		this.typeEditRulesIndex = typeEditRulesIndex;
	}
	
	public String getMinValueEditRules() {
		return minValueEditRules;
	}

	public void setMinValueEditRules(String minValueEditRules) {
		this.minValueEditRules = minValueEditRules;
	}

	public String getMaxValueEditRules() {
		return maxValueEditRules;
	}

	public void setMaxValueEditRules(String maxValueEditRules) {
		this.maxValueEditRules = maxValueEditRules;
	}

	public String getEditType() {
		return editType;
	}

	public void setEditType(String editType) {
		this.editType = editType;
	}

	public int getEditTypeIndex() {
		return editTypeIndex;
	}

	public void setEditTypeIndex(int editTypeIndex) {
		this.editTypeIndex = editTypeIndex;
	}

	public boolean getHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public void initializeColumnProperties(){
		setAlign("");
		setAlignIndex(0);
		setEditable(true);
		
		setEnableEditRules(false);
		setEditHiddenEditRules(false);
		setRequiredEditRules(false);
		setTypeEditRules("");
		setTypeEditRulesIndex(0);
		setMinValueEditRules("");
		setMaxValueEditRules("");
		
		setEditType("text");
		setEditTypeIndex(0);
		setRupType("");
		
		setHidden(false);
		setLabel("");
		setResizable(true);
		setSortable(true);
		setWidth("150");
		
		setName("");
		
		setActivated(true);
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

}
