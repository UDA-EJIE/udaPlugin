package com.ejie.uda.wizard;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.ejie.uda.operations.DataBaseWorker;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.Grid;
import com.ejie.uda.utils.TreeNode;
import com.ejie.uda.utils.Utilities;

/**
 *  Clase la cual define la tercera pantalla del asistente "Generar mantenimiento"
 */
public class NewMaintWizardPageThree extends WizardPage {
	
	// Propiedades del grid
	private Button[] radios;
	private Text urlText;
	private Text aliasText;
	private Button simpleSelectCheck;
	private Button multiSelectCheck;
	private Text widthText;
	private Text pagerNameText;
	private Button loadOnStartUpCheck;
	private Text rowNumText;
	private Button sortableCheck;
	private Combo sortOrderCombo;
	private Combo sortNameCombo;
	private Button rowEditCheck;
	// Eventos del grid
	private Text beforeRequestText;
	private Text loadBeforeSendText;
	private Text gridCompleteText;
	private Text loadCompleteText;
	private Text ondblclickRowText;
	private Text onSelectRowText;
	private Text onSelectAllText;
	
	// Contenedores
	private Composite containerPropertiesTab;
	private ScrolledComposite containerTables;
	private Composite containerRadios;
	private ConnectionData conData;
	
	/**
	 * Tercera ventana del Wizard de Plugin, donde se selecciona la tabla para generar el mantenimiento con su respectivas propiedades y eventos
	 * @param selection
	 */
	public NewMaintWizardPageThree(ISelection selection) {
		super("wizardPage");

		setTitle("Generar nuevo mantenimiento para una aplicación");
		setDescription("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
	}

	/**
	 * Creación de controles de la ventana
	 * @param parent - controlador padre
	 */
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, true));

		// Descripción de la operación
		Label descLabel = new Label(container, SWT.NULL);
		descLabel.setText("Seleccione la entidad a mantener");
		descLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		// Salto de línea
		Label hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

	    // Contenedor del tablas/entidades
		containerTables = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL);
		containerTables.setLayout(new GridLayout(1, false));
		containerTables.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		// Inicializa el contenedor de radios
		containerRadios = new Composite(containerTables, SWT.NONE);
		
		// Pestañas de propiedades y eventos
		final TabFolder tabFolder = new TabFolder(container, SWT.NULL);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

		TabItem propertiesTab = new TabItem(tabFolder, SWT.NULL);
		propertiesTab.setText("Propiedades");
		propertiesTab.setToolTipText("Propiedades");
		
		TabItem eventsTab = new TabItem(tabFolder, SWT.NULL);
		eventsTab.setText("Eventos");
		eventsTab.setToolTipText("Eventos");
		
		// Contenedor de la pestaña de propiedades
		containerPropertiesTab = new Composite(tabFolder, SWT.NULL);
		containerPropertiesTab.setLayout(new GridLayout(2, false));
		
		// Propiedad URL 
		Label urlLabel = new Label(containerPropertiesTab, SWT.NULL);
		urlLabel.setText("URL:");
		urlLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
		// Campo texto URL
		urlText = new Text(containerPropertiesTab, SWT.BORDER | SWT.SINGLE);
		urlText.setToolTipText("Define la url a través de la cual se carga el grid");
		urlText.setEnabled(false);
		urlText.setText("../[nombre entidad]");
		urlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));
		
		// Propiedad URL 
		Label aliasLabel = new Label(containerPropertiesTab, SWT.NULL);
		aliasLabel.setText("Alias:");
		aliasLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
		// Campo texto URL
		aliasText = new Text(containerPropertiesTab, SWT.BORDER | SWT.SINGLE);
		aliasText.setToolTipText("Define un alias a la entidad generada");
		aliasText.setText("");
		aliasText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));
		
		// Propiedad anchura
		Label widthLabel = new Label(containerPropertiesTab, SWT.NULL);
		widthLabel.setText("Anchura:");
		widthLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
		// Campo texto anchura
		widthText = new Text(containerPropertiesTab, SWT.BORDER | SWT.SINGLE);
		widthText.setToolTipText("Define la anchura del grid");
		widthText.setText("600");
		widthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));
		
		// Propiedad capa paginacion
		Label pagerNameLabel = new Label(containerPropertiesTab, SWT.NULL);
		pagerNameLabel.setText("Capa de paginación:");
		pagerNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		// Campo texto capa de paginación
		pagerNameText = new Text(containerPropertiesTab, SWT.BORDER | SWT.SINGLE);
		pagerNameText.setToolTipText("Indica el elemento HTML que contiene la paginación de resultados del grid");
		pagerNameText.setText("pager");
		pagerNameText.setEnabled(false);
		pagerNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));
		
		// Propiedad inicio ventana
		Label loadOnStartUpLabel = new Label(containerPropertiesTab, SWT.NULL);
		loadOnStartUpLabel.setText("Cargar al inicio de la ventana:");
		loadOnStartUpLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
		// Campo check inicio ventana
		loadOnStartUpCheck = new Button(containerPropertiesTab, SWT.CHECK);
		loadOnStartUpCheck.setSelection(true);
		loadOnStartUpCheck.setToolTipText("Indica si se cargará el grid a la hora de crearlo o se deberá invocar al reloadGrid para cargarlos");
		loadOnStartUpCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));
		
		// Propiedad Número de filas
		Label rowNumLabel = new Label(containerPropertiesTab, SWT.NULL);
		rowNumLabel.setText("Número de filas:");
		rowNumLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
		// Campo texto de Número de filas
		rowNumText = new Text(containerPropertiesTab, SWT.BORDER | SWT.SINGLE);
		rowNumText.setToolTipText("Define el Número de elementos en cada página del grid");
		rowNumText.setText("10");
		rowNumText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));

		// Propiedad ordenable
		Label sortableLabel = new Label(containerPropertiesTab, SWT.NULL);
		sortableLabel.setText("Columnas ordenables:");
		sortableLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
		// Campo check inicio ventana
		sortableCheck = new Button(containerPropertiesTab, SWT.CHECK);
		sortableCheck.setSelection(true);
		sortableCheck.setToolTipText("Indica si las columnas del grid son ordenables o no arrastrándolas con el ratón");
		sortableCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));

		// Propiedad tipo de ordenación
		Label sortOrderLabel = new Label(containerPropertiesTab, SWT.NULL);
		sortOrderLabel.setText("Ordenación:");
		sortOrderLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
		// Campo combo de tipo de ordenación
		sortOrderCombo = new Combo(containerPropertiesTab, SWT.READ_ONLY);
		sortOrderCombo.add("asc");
		sortOrderCombo.add("desc");
		sortOrderCombo.select(0);
		sortOrderCombo.setToolTipText("Indica el orden (ascendente o descendente) de la columna por la que ordenar el grid en su primera carga");
		sortOrderCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));

		// Propiedad ordenación por
		Label sortNameLabel = new Label(containerPropertiesTab, SWT.NULL);
		sortNameLabel.setText("Ordenación por:");
		sortNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
		
		// Combo de ordenación por columnas
		sortNameCombo = new Combo(containerPropertiesTab, SWT.READ_ONLY);
		sortNameCombo.setToolTipText("Indica el nombre de la columna por el que ordenar la primera vez que se carga el grid");
		sortNameCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		
		// Propiedad selección simple
		Label simpleSelectLabel = new Label(containerPropertiesTab, SWT.NULL);
		simpleSelectLabel.setText("Selección simple:");
		simpleSelectLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
		// Radio button para Multi
		simpleSelectCheck = new Button(containerPropertiesTab, SWT.RADIO);
		simpleSelectCheck.setSelection(true);
		simpleSelectCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));
		
		// Propiedad multiselector
		Label multiSelectLabel = new Label(containerPropertiesTab, SWT.NULL);
		multiSelectLabel.setText("Multiselección:");
		multiSelectLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
//		// Campo check inicio ventana
//		multiSelectCheck = new Button(containerPropertiesTab, SWT.CHECK);
//		multiSelectCheck.setSelection(false);
//		multiSelectCheck.setToolTipText("Indica si el grid está en modo multiselección o no");
//		multiSelectCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));
		// Radio button para Multi
		multiSelectCheck = new Button(containerPropertiesTab, SWT.RADIO);
		multiSelectCheck.setSelection(false);
		multiSelectCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));

		// Propiedad líneas editables
		Label rowEditLabel = new Label(containerPropertiesTab, SWT.NULL);
		rowEditLabel.setText("Edición en línea:");
		rowEditLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));
//		// Campo check inicio ventana
//		rowEditCheck = new Button(containerPropertiesTab, SWT.CHECK);
//		rowEditCheck.setSelection(false);
//		rowEditCheck.setToolTipText("Propiedad que indica si las líneas del grid son editables");
//		rowEditCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));
		// Radio button Ed.Linea
		rowEditCheck = new Button(containerPropertiesTab, SWT.RADIO);
		rowEditCheck.setSelection(false);
		rowEditCheck.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false, 1 ,1));
		
		
		//Añade el contenedor a la pestaña de propiedades
		propertiesTab.setControl(containerPropertiesTab);
		
		///////////////////////////////
		// Contenedor del tab de eventos
		////////////////////////////////
		Composite containerEventsTab = new Composite(tabFolder, SWT.NULL);
		containerEventsTab.setLayout(new GridLayout(4, false));
		
		// Generación del evento beforeRequest
		Label beforeRequestLabel = new Label(containerEventsTab, SWT.NULL);
		beforeRequestLabel.setText("beforeRequest:");
		beforeRequestLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2 ,1));
		
		// Campo texto del nombre de la funcion del evento beforeRequest
		beforeRequestText = new Text(containerEventsTab, SWT.BORDER | SWT.SINGLE);
		beforeRequestText.setToolTipText("Nombre de la Función que se ejecuta antes de solicitar una petición");
		beforeRequestText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Generación del evento loadBeforeSend
		Label loadBeforeSendLabel = new Label(containerEventsTab, SWT.NULL);
		loadBeforeSendLabel.setText("loadBeforeSend:");
		loadBeforeSendLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2 ,1));
		
		// Campo texto del nombre de la funcion del evento loadBeforeSend
		loadBeforeSendText = new Text(containerEventsTab, SWT.BORDER | SWT.SINGLE);
		loadBeforeSendText.setToolTipText("Un pre-callback para modificar el objeto XMLHttpRequest antes de ser enviado");
		loadBeforeSendText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Generación del evento gridComplete
		Label gridCompleteLabel = new Label(containerEventsTab, SWT.NULL);
		gridCompleteLabel.setText("gridComplete:");
		gridCompleteLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2 ,1));
		
		// Campo texto del nombre de la funcion del evento gridComplete
		gridCompleteText = new Text(containerEventsTab, SWT.BORDER | SWT.SINGLE);
		gridCompleteText.setToolTipText("Nombre de la Función que se ejecuta despuás que todos los datos están cargados en el grid \ny todos los demás procesos se han completado");
		gridCompleteText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Generación del evento loadComplete
		Label loadCompleteLabel = new Label(containerEventsTab, SWT.NULL);
		loadCompleteLabel.setText("loadComplete:");
		loadCompleteLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2 ,1));
		
		// Campo texto del nombre de la funcion del evento loadComplete
		loadCompleteText = new Text(containerEventsTab, SWT.BORDER | SWT.SINGLE);
		loadCompleteText.setToolTipText("Nombre de la Función que se ejecuta inmediatamente después de cada petición al servidor");
		loadCompleteText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Generación del evento ondblclickRow
		Label ondblclickRowLabel = new Label(containerEventsTab, SWT.NULL);
		ondblclickRowLabel.setText("ondblclickRow:");
		ondblclickRowLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2 ,1));
		
		// Campo texto del nombre de la funcion del evento ondblclickRow
		ondblclickRowText = new Text(containerEventsTab, SWT.BORDER | SWT.SINGLE);
		ondblclickRowText.setToolTipText("Nombre de la Función que se ejecuta inmediatamente después de un doble click sobre una fila del grid");
		ondblclickRowText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Generación del evento onSelectRow
		Label onSelectRowLabel = new Label(containerEventsTab, SWT.NULL);
		onSelectRowLabel.setText("onSelectRow:");
		onSelectRowLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2 ,1));
		
		// Campo texto del nombre de la funcion del evento onSelectRow
		onSelectRowText = new Text(containerEventsTab, SWT.BORDER | SWT.SINGLE);
		onSelectRowText.setToolTipText("Nombre de la Función que se ejecuta inmediatamente después de un click sobre una fila del grid");
		onSelectRowText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Generación del evento onSelectAll
		Label onSelectAllLabel = new Label(containerEventsTab, SWT.NULL);
		onSelectAllLabel.setText("onSelectAll:");
		onSelectAllLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2 ,1));
		
		// Campo texto del nombre de la funcion del evento onSelectAll
		onSelectAllText = new Text(containerEventsTab, SWT.BORDER | SWT.SINGLE);
		onSelectAllText.setToolTipText("Nombre de la Función que se ejecuta cuando el grid es de múltiple selección y \nse selecciona el check de selección múltiple del grid");
		onSelectAllText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Añade el contenedor a la pestaña de eventos
		eventsTab.setControl(containerEventsTab);
		
		// Pasa el control al contenedor
		setControl(container);
	}
	
	/**
	 * Habilita el botón de Next si cumple la Validación
	 * 
	 * return true para habilitar el botón, false ecc
	 */
	public boolean canFlipToNextPage() {
		return validatePage();
	}
	
	/**
	 * Verifica si al pinchar en el botón Next puede pasar a la siguiente pantalla
	 * 
	 * @return página a direccionar
	 */
	public IWizardPage getNextPage() {
		
    	if (conData == null){
			setErrorMessage("Error en los datos de conexión");
			return getWizard().getContainer().getCurrentPage();
		}
    	
    	NewMaintWizard newMaintWizard = (NewMaintWizard)getWizard();
		if (newMaintWizard != null) {
			NewMaintWizardPageFour newMaintWizardPageFour = newMaintWizard.getPageNewMaintWizardPageFour();
			if (newMaintWizardPageFour != null) {
				// Cargar las columnas de una tabla en la siguiente ventana
				String tableName = getTableName(radios);
				
				if (!Utilities.isBlank(tableName) && getConfirmation()){
					newMaintWizardPageFour.setColumns(conData, tableName);
					return super.getNextPage();	
				}
			}
		}
		
		return getWizard().getContainer().getCurrentPage();
	}
	
	/*************/
	/*  Getters  */
	/*************/
	
	/**
	 * Recupera los valores de los campos de la pantalla
	 * el objecto de Grid
	 * 
	 * @return objecto de grid con todos sus valores
	 */
	public Grid getGrid(){
		
		Grid grid = new Grid();
		
		//Propiedad por defecto
		grid.setHasMaint(true);
		
		if (radios != null){
			grid.setTableName(getTableName(radios));
		}
		if (urlText != null){
			grid.setUrl(urlText.getText());	
		}
		if (aliasText != null){
			grid.setAlias(aliasText.getText());	
		}
		if (widthText != null){
			grid.setWidth(widthText.getText());	
		}
		if (pagerNameText != null){
			grid.setPagerName(pagerNameText.getText());	
		}
		if (loadOnStartUpCheck != null){
			grid.setLoadOnStartUp(loadOnStartUpCheck.getSelection());	
		}
		if (rowNumText != null){
			grid.setRowNum(rowNumText.getText());	
		}
		if (sortableCheck != null){
			grid.setSortable(sortableCheck.getSelection());	
		}
		if (sortOrderCombo != null){
			grid.setSortOrder(sortOrderCombo.getText());	
		}
		if (sortNameCombo != null){
			grid.setSortName(sortNameCombo.getText());	
		}
		if (multiSelectCheck != null){
			grid.setMultiSelect(multiSelectCheck.getSelection());	
		}
		if (rowEditCheck != null){
			grid.setRowEdit(rowEditCheck.getSelection());	
		}
		if (beforeRequestText != null){
			grid.setBeforeRequest(beforeRequestText.getText());	
		}
		if (loadBeforeSendText != null){
			grid.setLoadBeforeSend(loadBeforeSendText.getText());	
		}
		if (gridCompleteText != null){
			grid.setGridComplete(gridCompleteText.getText());	
		}
		if (loadCompleteText != null){
			grid.setLoadComplete(loadCompleteText.getText());	
		}
		if (ondblclickRowText != null){
			grid.setOndblClickRow(ondblclickRowText.getText());	
		}
		if (onSelectAllText != null){
			grid.setOnSelectAll(onSelectAllText.getText());	
		}
		if (onSelectRowText != null){
			grid.setOnSelectRow(onSelectRowText.getText());	
		}
		
		return grid;
	}
	
	/**
	 * Recupera el nombre de la entidad/tabla seleccionada
	 * 
	 * @param tableRadios - array de tablas en formato radios
	 * @return nombre de la entidad
	 */
	public String getTableName(Button[] tableRadios){
		String tableName = "";
		
		if (tableRadios != null){
			for (int i = 0; i < tableRadios.length; i++) {
				Button radio = tableRadios[i];
				
				if (radio != null && radio.getSelection()){
					tableName = radio.getText();
					break;
				}
			}			
		}
		
		return tableName;
	}
	
	/**
	 * Limpia los radios pintados anteriormente, pinta los nuevos radios
	 * de entidades según el esquema y asigna los valores predeterminados de algunos campos
	 * 
	 * @param con - Datos de conexión a la BBDD
	 */
	public void setTablesRadios(ConnectionData con){
		
		containerRadios.setLayout(new GridLayout(1, false));
		containerRadios.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		if (con != null){
			
			conData = con;
			
			List<String> tablesSchema = DataBaseWorker.getTablesSchema(conData);
			
			if (tablesSchema != null && !tablesSchema.isEmpty()){
				
				// Borra los radios de entidades existentes
				deleteRadiosEntities(radios);
				// Crea los nuevos radios según el esquema
				int sizeTables = tablesSchema.size();
				radios = new Button[sizeTables];
				// Añade listener a los radios
				Listener listener = new Listener() {
					public void handleEvent(Event e) {
						Button widButton = (Button) e.widget; 
						if (widButton != null && widButton.getSelection()){
							// Carga la combo de Ordenación por
							setColumnsCombo(conData, widButton.getText());
							//Inicializa el campo de URL
							urlText.setText("../" + widButton.getText().replace("_", "").toLowerCase());
				        }
					}
				};
				// Asigna los valores a los radios generados
				for (int i = 0; i < sizeTables; i++) {
					radios[i] = new Button(containerRadios, SWT.RADIO);
					radios[i].setText(tablesSchema.get(i));

					if (i == 0){
						radios[i].setSelection(true);	
					}
					radios[i].setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					radios[i].addListener(SWT.Selection, listener);
				}
				
				if (radios[0] != null){
					// Carga la combo de Ordenación por
					setColumnsCombo(conData, radios[0].getText());
					//Inicializa el campo de URL
					urlText.setText("../" + radios[0].getText().replace("_", "").toLowerCase());					
				}
			}
		}
		
		//Asigna el contenedor con scroll para los radios
		containerRadios.pack();
		containerTables.setContent(containerRadios);
		containerTables.setExpandHorizontal(true);
		containerTables.setExpandVertical(true);
		containerTables.setMinSize(containerRadios.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		containerTables.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}
	
	/**********************/
	/*  Métodos privados  */
	/**********************/
	
	/**
	 * Borra los controles tipo array de radio de la pantalla
	 * 
	 * @param radios - array de radios
	 */
	private void deleteRadiosEntities(Button[] radios){
		
		if (radios != null){
			
			int size = radios.length;

			if (size != 0){
				
				for (int i = 0; i < size; i++) {
					if (radios[i] != null){
						radios[i].dispose();	
					}
				}
			}
		}
	}
	
	/**
	 * Asignar valores a una combp de columna según su entidad
	 * @param conData - conexión
	 * @param tableName - nombre de la entidad / tabla
	 */
	public void setColumnsCombo(ConnectionData conData, String tableName){
		
		List<TreeNode> columns;
		List<TreeNode> columnsComposite;
		
		if (conData != null && !Utilities.isBlank(tableName)){
			TreeNode table = DataBaseWorker.getTableNode(conData, tableName);
			
			if (table != null){
				// Crea propiedades para las columnas
				columns = table.getChildren();
				// Inicializa la lista de propiedades de las columnas
				if (columns != null && !columns.isEmpty() && sortNameCombo != null){
					
					// Elimina todas las opciones del combo
					sortNameCombo.removeAll();
					
					for (Iterator<TreeNode> iterator = columns.iterator(); iterator.hasNext();) {
						TreeNode column = (TreeNode) iterator.next();
						
						if (column.isComposite()){
							columnsComposite = column.getChildren();
							for (Iterator<TreeNode> iteratorComposite = columnsComposite.iterator(); iteratorComposite.hasNext();) {
								TreeNode columnComposite = (TreeNode) iteratorComposite.next();
								// Añade las claves compuestas
								sortNameCombo.add(columnComposite.getName());
							}
						}else{
							// Añade las columnas de la tabla
							sortNameCombo.add(column.getName());
						}
					}
					// Selecciona la primera opción
					if (sortNameCombo.getItemCount() > 1){
						sortNameCombo.select(0);	
					}
				}
			}
		}
	}
	
    /**
     * Valida los campos de la pantalla
     * 
     * @return true si todos los controles están validados, false si algún campo no es válido.
     */
    protected boolean validatePage() {
    	
    	setErrorMessage(null);
	
		if (rowNumText != null && !Utilities.isBlank(rowNumText.getText()) && !Utilities.validateNumber(rowNumText.getText())) {
			setErrorMessage("Caracteres no válidos para en campo 'Número de filas'");
			return false;
		}
		
		if (beforeRequestText != null && !Utilities.isBlank(beforeRequestText.getText()) && !Utilities.validateText(beforeRequestText.getText())) {
			setErrorMessage("Caracteres no válidos para en campo 'beforeRequest'");
			return false;
		}
		
		if (loadBeforeSendText != null && !Utilities.isBlank(loadBeforeSendText.getText()) && !Utilities.validateText(loadBeforeSendText.getText())) {
			setErrorMessage("Caracteres no válidos para en campo 'loadBeforeSend'");
			return false;
		}
		
		if (gridCompleteText != null && !Utilities.isBlank(gridCompleteText.getText()) && !Utilities.validateText(gridCompleteText.getText())) {
			setErrorMessage("Caracteres no válidos para en campo 'gridComplete'");
			return false;
		}
		
		if (loadCompleteText != null && !Utilities.isBlank(loadCompleteText.getText()) && !Utilities.validateText(loadCompleteText.getText())) {
			setErrorMessage("Caracteres no válidos para en campo 'loadComplete'");
			return false;
		}
		
		if (loadCompleteText != null && !Utilities.isBlank(loadCompleteText.getText()) && !Utilities.validateText(loadCompleteText.getText())) {
			setErrorMessage("Caracteres no válidos para en campo 'loadComplete'");
			return false;
		}
		
		if (ondblclickRowText != null && !Utilities.isBlank(ondblclickRowText.getText()) && !Utilities.validateText(ondblclickRowText.getText())) {
			setErrorMessage("Caracteres no válidos para en campo 'ondblclickRow'");
			return false;
		}
		
		if (onSelectRowText != null && !Utilities.isBlank(onSelectRowText.getText()) && !Utilities.validateText(onSelectRowText.getText())) {
			setErrorMessage("Caracteres no válidos para en campo 'onSelectRow'");
			return false;
		}
		
		if (onSelectAllText != null && !Utilities.isBlank(onSelectAllText.getText()) && !Utilities.validateText(onSelectAllText.getText())) {
			setErrorMessage("Caracteres no válidos para en campo 'onSelectAllText'");
			return false;
		}
		
        setErrorMessage(null);
        setMessage("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
        return true;
    }
    
 
	public boolean getConfirmation() {
		boolean askConfirmation = false;
		String packageName = "";
		if (Utilities.isBlank(aliasText.getText())) {
			askConfirmation = true;
		}

		if (askConfirmation) {
			boolean b = MessageDialog
					.openConfirm(
							getShell(),
							"Confirmación",
							"Si ya existe un mantenimiento sobre la entidad seleccionada, se sobreescribirá a menos que se defina un 'alias' específico. ¿Desea Continuar?");
			return b;
		} else {
			return true;
		}
}
	
}