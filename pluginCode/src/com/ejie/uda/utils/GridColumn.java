package com.ejie.uda.utils;


public class GridColumn {

	// Propiedades de la columna de un grid
	private String tableName;
	private String columnName;
	private String align;
	private int alignIndex;
	private boolean editable;
	private boolean hidden;
	private String index;
	private String label;
	private String name;
	private boolean resizable;
	private boolean sortable;
	private boolean title;
	private String width;
	private String unformat;
	private String editType;
	private int editTypeIndex;
	private String firstSortOrder;
	private int firstSortOrderIndex;
	private boolean fixed;
	private String rupType;

	// Grupo editOptions
	private String valueEditOptions;
	private String dataUrlEditOptions;
	private String buildSelectEditOptions;
	private String defaultValueEditOptions;
	private String otherOptionsEditOptions;
	
	//	Grupo formatter;
	private String formatter;
	private int formatterIndex;
	private String formatOptions;
	
	// Grupo editRules
	private boolean enableEditRules;
	private boolean editHiddenEditRules;
	private boolean requiredEditRules;
	private boolean numberEditRules;
	private boolean integerEditRules;
	private String minValueEditRules;
	private String maxValueEditRules;
	private boolean emailEditRules;
	private boolean urlEditRules;
	private boolean dateEditRules;
	private boolean timeEditRules;
	private boolean customEditRules;
	private String customFuncEditRules;
	
	//Propiedad de activación
	private boolean activated;

	/**
	 * Contructor
	 */
	
	public GridColumn(){
		
	}
			
	public GridColumn(String tableName, String columnName, String align, int alignIndex, boolean editable, String rupType, String valueEditOptions,
			String dataUrlEditOptions, String buildSelectEditOptions,
			String defaultValueEditOptions, String otherOptionsEditOptions,
			boolean enableEditRules, boolean editHiddenEditRules, boolean requiredEditRules,
			boolean numberEditRules, boolean integerEditRules,
			String minValueEditRules, String maxValueEditRules,
			boolean emailEditRules, boolean urlEditRules,
			boolean dateEditRules, boolean timeEditRules,
			boolean customEditRules, String customFuncEditRules,
			String editType, int editTypeIndex, String firstSortOrder, int firstSortOrderIndex, boolean fixed,
			String formatter, int formatterIndex, String formatOptions, boolean hidden,
			String index, String label, String name, boolean resizable,
			boolean sortable, boolean title, String width,
			String unformat, boolean activated) {
		this.tableName = tableName;
		this.columnName = columnName;
		this.align = align;
		this.alignIndex = alignIndex;
		this.editable = editable;
		this.rupType = rupType;
		this.valueEditOptions = valueEditOptions;
		this.dataUrlEditOptions = dataUrlEditOptions;
		this.buildSelectEditOptions = buildSelectEditOptions;
		this.defaultValueEditOptions = defaultValueEditOptions;
		this.otherOptionsEditOptions = otherOptionsEditOptions;
		this.enableEditRules = enableEditRules;
		this.editHiddenEditRules = editHiddenEditRules;
		this.requiredEditRules = requiredEditRules;
		this.numberEditRules = numberEditRules;
		this.integerEditRules = integerEditRules;
		this.minValueEditRules = minValueEditRules;
		this.maxValueEditRules = maxValueEditRules;
		this.emailEditRules = emailEditRules;
		this.urlEditRules = urlEditRules;
		this.dateEditRules = dateEditRules;
		this.timeEditRules = timeEditRules;
		this.customEditRules = customEditRules;
		this.customFuncEditRules = customFuncEditRules;
		this.editType = editType;
		this.editTypeIndex = editTypeIndex;
		this.firstSortOrder = firstSortOrder;
		this.firstSortOrderIndex = firstSortOrderIndex;
		this.fixed = fixed;
		this.formatter = formatter;
		this.formatterIndex = formatterIndex;
		this.formatOptions = formatOptions;
		this.hidden = hidden;
		this.index = index;
		this.label = label;
		this.name = name;
		this.resizable = resizable;
		this.sortable = sortable;
		this.title = title;
		this.width = width;
		this.unformat = unformat;
		this.activated = activated;
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

	public boolean isEditable() {
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

	
	public String getValueEditOptions() {
		return valueEditOptions;
	}

	public void setValueEditOptions(String valueEditOptions) {
		this.valueEditOptions = valueEditOptions;
	}

	public String getDataUrlEditOptions() {
		return dataUrlEditOptions;
	}

	public void setDataUrlEditOptions(String dataUrlEditOptions) {
		this.dataUrlEditOptions = dataUrlEditOptions;
	}

	public String getBuildSelectEditOptions() {
		return buildSelectEditOptions;
	}

	public void setBuildSelectEditOptions(String buildSelectEditOptions) {
		this.buildSelectEditOptions = buildSelectEditOptions;
	}

	public String getDefaultValueEditOptions() {
		return defaultValueEditOptions;
	}

	public void setDefaultValueEditOptions(String defaultValueEditOptions) {
		this.defaultValueEditOptions = defaultValueEditOptions;
	}

	public String getOtherOptionsEditOptions() {
		return otherOptionsEditOptions;
	}

	public void setOtherOptionsEditOptions(String otherOptionsEditOptions) {
		this.otherOptionsEditOptions = otherOptionsEditOptions;
	}

	public boolean isEnableEditRules() {
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

	public boolean isRequiredEditRules() {
		return requiredEditRules;
	}

	public void setRequiredEditRules(boolean requiredEditRules) {
		this.requiredEditRules = requiredEditRules;
	}

	public boolean isNumberEditRules() {
		return numberEditRules;
	}

	public void setNumberEditRules(boolean numberEditRules) {
		this.numberEditRules = numberEditRules;
	}

	public boolean isIntegerEditRules() {
		return integerEditRules;
	}

	public void setIntegerEditRules(boolean integerEditRules) {
		this.integerEditRules = integerEditRules;
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

	public boolean isEmailEditRules() {
		return emailEditRules;
	}

	public void setEmailEditRules(boolean emailEditRules) {
		this.emailEditRules = emailEditRules;
	}

	public boolean isUrlEditRules() {
		return urlEditRules;
	}

	public void setUrlEditRules(boolean urlEditRules) {
		this.urlEditRules = urlEditRules;
	}

	public boolean isDateEditRules() {
		return dateEditRules;
	}

	public void setDateEditRules(boolean dateEditRules) {
		this.dateEditRules = dateEditRules;
	}

	public boolean isTimeEditRules() {
		return timeEditRules;
	}

	public void setTimeEditRules(boolean timeEditRules) {
		this.timeEditRules = timeEditRules;
	}

	public boolean isCustomEditRules() {
		return customEditRules;
	}

	public void setCustomEditRules(boolean customEditRules) {
		this.customEditRules = customEditRules;
	}

	public String getCustomFuncEditRules() {
		return customFuncEditRules;
	}

	public void setCustomFuncEditRules(String customFuncEditRules) {
		this.customFuncEditRules = customFuncEditRules;
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

	public String getFirstSortOrder() {
		return firstSortOrder;
	}

	public void setFirstSortOrder(String firstSortOrder) {
		this.firstSortOrder = firstSortOrder;
	}

	public int getFirstSortOrderIndex() {
		return firstSortOrderIndex;
	}

	public void setFirstSortOrderIndex(int firstSortOrderIndex) {
		this.firstSortOrderIndex = firstSortOrderIndex;
	}
	
	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}
	
	public int getFormatterIndex() {
		return formatterIndex;
	}

	public void setFormatterIndex(int formatterIndex) {
		this.formatterIndex = formatterIndex;
	}

	public String getFormatOptions() {
		return formatOptions;
	}

	public void setFormatOptions(String formatOptions) {
		this.formatOptions = formatOptions;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
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

	public boolean isTitle() {
		return title;
	}

	public void setTitle(boolean title) {
		this.title = title;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getUnformat() {
		return unformat;
	}

	public void setUnformat(String unformat) {
		this.unformat = unformat;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public void initializeColumnProperties(){
		setAlign("");
		setAlignIndex(0);
		setEditable(true);
		
		setValueEditOptions("");
		setDataUrlEditOptions("");
		setBuildSelectEditOptions("");
		setDefaultValueEditOptions("");
		setOtherOptionsEditOptions("");
		
		setEnableEditRules(false);
		setEditHiddenEditRules(false);
		setRequiredEditRules(false);
		setNumberEditRules(false);
		setIntegerEditRules(false);
		setMinValueEditRules("");
		setMaxValueEditRules("");
		setEmailEditRules(false);
		setUrlEditRules(false);
		setDateEditRules(false);
		setTimeEditRules(false);
		setCustomEditRules(false);
		setCustomFuncEditRules("");
		
		setEditType("text");
		setEditTypeIndex(0);
		setRupType("");
		setFirstSortOrder("");
		setFirstSortOrderIndex(0);
		setFixed(false);
		
		setFormatter("");
		setFormatterIndex(0);
		
		setFormatOptions("");
		
		setHidden(false);
		setIndex("");
		setLabel("");
		setResizable(true);
		setSortable(true);
		setTitle(true);
		setWidth("150");
		
		setName("");
		setUnformat("");
		
		setActivated(true);
	}
	
	public String printProperties(){
		
		StringBuffer properties = new StringBuffer();
		
		properties.append("{");
		if (!Utilities.isBlank(getName())){
			properties.append(" name: \"" + getName() + "\",");
		}
		if (!Utilities.isBlank(getLabel())){
			if (getLabel().startsWith("$")){
				properties.append("\n\t\t\t\tlabel: " + getLabel() + ",");
			}else{
				properties.append("\n\t\t\t\tlabel: \"" + getLabel() + "\",");	
			}
		}
		if (!Utilities.isBlank(getIndex())){
			properties.append("\n\t\t\t\tindex: \"" + getIndex() + "\",");
		}
		if (!Utilities.isBlank(getWidth())){
			properties.append("\n\t\t\t\twidth: \"" + getWidth() + "\",");
		}
		properties.append("\n\t\t\t\teditable: " + isEditable() + ",");
		if (!Utilities.isBlank(getEditType())){
			if ("Combo".equalsIgnoreCase(getEditType())){
				properties.append("\n\t\t\t\tedittype: \"select\",");
				setRupType("combo");
			}else if ("Autocomplete".equalsIgnoreCase(getEditType())){
				properties.append("\n\t\t\t\tedittype: \"text\",");
				setRupType("autocomplete");
			}else if ("Datepicker".equalsIgnoreCase(getEditType())){
				properties.append("\n\t\t\t\tedittype: \"text\",");
				setRupType("datepicker");
			}else{
				properties.append("\n\t\t\t\tedittype: \"" + getEditType().toLowerCase() + "\",");
				setRupType("");
			}
		}
		if (!Utilities.isBlank(getFirstSortOrder())){
			properties.append("\n\t\t\t\truptype: \"" + getRupType() + "\",");
		}
		if (!Utilities.isBlank(getAlign()) && !"left".equalsIgnoreCase(getAlign())){
			properties.append("\n\t\t\t\talign: \"" + getAlign() + "\",");
		}	
		if (!Utilities.isBlank(getFirstSortOrder())){
			properties.append("\n\t\t\t\tfirstsortorder: \"" + getFirstSortOrder() + "\",");
		}
		if (!Utilities.isBlank(getUnformat())){
			properties.append("\n\t\t\t\tunformat: \"" + getUnformat() + "\",");
		}
		if (isFixed()){
			properties.append("\n\t\t\t\tfixed: " + isFixed() + ",");
		}
		if (isHidden()){
			properties.append("\n\t\t\t\thidden: " + isHidden() + ",");
		}
		if (!isResizable()){
			properties.append("\n\t\t\t\tresizable: " + isResizable() + ",");
		}
		if (!isSortable()){
			properties.append("\n\t\t\t\tsortable: " + isSortable() + ",");
		}
		if (!isTitle()){
			properties.append("\n\t\t\t\ttitle: " + isTitle() + "},");
		}
		
		////Quita la última coma sobrante
		//properties = new StringBuffer(Utilities.removeFinalComma(properties.toString()));
		
		if (!Utilities.isBlank(getValueEditOptions()) || !Utilities.isBlank(getDataUrlEditOptions()) 
				|| !Utilities.isBlank(getBuildSelectEditOptions()) || !Utilities.isBlank(getDefaultValueEditOptions())
				|| !Utilities.isBlank(getOtherOptionsEditOptions())){
			properties.append("\n\t\t\t\teditoptions: {");
			
			if (!Utilities.isBlank(getDataUrlEditOptions())){
				properties.append("\n\t\t\t\t\tdataUrl:\"" + getDataUrlEditOptions() + "\",");
			}
			if (!Utilities.isBlank(getBuildSelectEditOptions())){
				properties.append("\n\t\t\t\t\tbuildSelect:\"" + getBuildSelectEditOptions() + "\",");
			}
			if (!Utilities.isBlank(getDefaultValueEditOptions())){
				properties.append("\n\t\t\t\t\tdefaultValue:\"" + getDefaultValueEditOptions() + "\",");
			}
			if (!Utilities.isBlank(getValueEditOptions())){
				properties.append("\n\t\t\t\t\tvalue:\"" + getValueEditOptions() + "\",");
			}
			if (!Utilities.isBlank(getOtherOptionsEditOptions())){
				properties.append("\n\t\t\t\t\t" + getOtherOptionsEditOptions() + "\",");
			}
			//Quita la última coma sobrante
			properties = new StringBuffer(Utilities.removeFinalComma(properties.toString()));
			//TODO escapar other options?
			properties.append("\n\t\t\t\t},");
		}
		
		if (!Utilities.isBlank(getFormatter()) && !Utilities.isBlank(getFormatOptions())){
			properties.append("\n\t\t\t\t\tformatter: {");
			
			if (!Utilities.isBlank(getFormatter())){
				properties.append("\n\t\t\t\t\t\t" + getFormatter() + ": " + getBuildSelectEditOptions());
			}
			//TODO escapar other options?
			properties.append("\n\t\t\t\t\t},");
		}
		
		if (isEnableEditRules()){
			properties.append("\n\t\t\t\t\teditrules: {");
			
			properties.append("\n\t\t\t\t\t\trequired:" + isRequiredEditRules() + ",");
			
			if (!Utilities.isBlank(getMinValueEditRules())){
				properties.append("\n\t\t\t\t\t\tminValue:" + getMinValueEditRules() + ",");
			}
			if (!Utilities.isBlank(getMaxValueEditRules())){
				properties.append("\n\t\t\t\t\t\tmaxValue:" + getMaxValueEditRules() + ",");
			}
			if (!Utilities.isBlank(getCustomFuncEditRules())){
				properties.append("\n\t\t\t\t\t\tcustom_func:\"" + getCustomFuncEditRules() + "\",");
			}
			properties.append("\n\t\t\t\t\t\tedithidden:" + isEditHiddenEditRules() + ",");
			properties.append("\n\t\t\t\t\t\tnumber:" + isNumberEditRules() + ",");
			properties.append("\n\t\t\t\t\t\tinteger:" + isIntegerEditRules() + ",");
			properties.append("\n\t\t\t\t\t\tinteger:" + isIntegerEditRules() + ",");
			properties.append("\n\t\t\t\t\t\temail:" + isEmailEditRules() + ",");
			properties.append("\n\t\t\t\t\t\turl:" + isUrlEditRules() + ",");
			properties.append("\n\t\t\t\t\t\tdate:" + isDateEditRules() + ",");
			properties.append("\n\t\t\t\t\t\ttime:" + isTimeEditRules() + ",");
			properties.append("\n\t\t\t\t\t\tcustom" + isCustomEditRules() + ",");
			
			//Quita la última coma sobrante
			properties = new StringBuffer(Utilities.removeFinalComma(properties.toString()));
			
			properties.append("\n\t\t\t\t\t},");	
		}
		
		//Quita la última coma sobrante
		properties = new StringBuffer(Utilities.removeFinalComma(properties.toString()));
		properties.append("\n\t\t\t}");
		
		return properties.toString(); 
	}
	
	public String getColumnSearchForm(){
		
		String searchFormCode = "";
			
		if (isActivated() && ("Text".equalsIgnoreCase(getEditType()) || "Textarea".equalsIgnoreCase(getEditType())
				|| "Checkbox".equalsIgnoreCase(getEditType()) || "Select".equalsIgnoreCase(getEditType()) || "Combo".equalsIgnoreCase(getEditType())
				|| "Autocomplete".equalsIgnoreCase(getEditType()) || "Datepicker".equalsIgnoreCase(getEditType()))){

			searchFormCode = "\n\t\t\t\t\t\t<div class=\"formulario_linea_izda_float\">";
			searchFormCode += "\n\t\t\t\t\t\t\t<label class=\"formulario_linea_label\">" + getLabel() + ":</label>";
			
			if ("Text".equalsIgnoreCase(getEditType())){
				searchFormCode += "\n\t\t\t\t\t\t\t<input type=\"text\" name=\"" + getName() + "\" class=\"formulario_linea_input\" id=\"" + getName() + "_search\" />";
			}else if ("Textarea".equalsIgnoreCase(getEditType())){
				searchFormCode += "\n\t\t\t\t\t\t\t<textarea name=\"" + getName() + "\" class=\"formulario_linea_input\" id=\"" + getName() + "_search\"></textarea>";
			}else if ("Checkbox".equalsIgnoreCase(getEditType())){
				searchFormCode += "\n\t\t\t\t\t\t\t<input type=\"checkbox\" name=\"" + getName() + "\" class=\"formulario_linea_input\" id=\"" + getName() + "_search\" />";
			}else if ("Select".equalsIgnoreCase(getEditType()) || "Combo".equalsIgnoreCase(getEditType())){
				searchFormCode += "\n\t\t\t\t\t\t\t<select name=\"" + getName() + "\" class=\"combo\" id=\"" + getName() + "_search\"></select>";
			}else if ("Autocomplete".equalsIgnoreCase(getEditType())){
				searchFormCode += "\n\t\t\t\t\t\t\t<input type=\"text\" name=\"" + getName() + "\" class=\"autocomplete\" id=\"" + getName() + "_search\" />";
			}else if ("Datepicker".equalsIgnoreCase(getEditType())){
				searchFormCode += "\n\t\t\t\t\t\t\t<input type=\"text\" name=\"" + getName() + "\" class=\"datepicker\" id=\"" + getName() + "_search\" />";
			}
			searchFormCode += "\n\t\t\t\t\t\t</div>";
		}
		return searchFormCode;
	}
}
