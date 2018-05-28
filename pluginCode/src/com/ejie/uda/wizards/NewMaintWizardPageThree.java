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
package com.ejie.uda.wizards;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.widgets.Text;

import com.ejie.uda.operations.DataBaseWorker;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.Grid;
import com.ejie.uda.utils.TreeNode;
import com.ejie.uda.utils.Utilities;

/**
 *  Clase que define la tercera pantalla del asistente "Generar mantenimiento"
 */
public class NewMaintWizardPageThree extends WizardPage {
	
	// Propiedades del grid
	private Button[] radios;
	private Text urlText;
	private Text aliasText;
	private Button loadOnStartUpCheck;
	private Combo sortOrderCombo;
	private Combo sortNameCombo;
	
	// Contenedores
	private Composite containerProperties;
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
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(4, true));
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		// Descripción de la operación
		Label descLabel = new Label(container, SWT.NULL);
		descLabel.setText("Seleccione la entidad a mantener y defina sus propiedades");
		descLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		// Descripción de la operación
		Label auxLabel = new Label(container, SWT.NULL);
		auxLabel.setText("(*) Campos obligatorios");
		auxLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Contenedor del tablas/entidades
		containerTables = new ScrolledComposite(container, SWT.H_SCROLL	| SWT.V_SCROLL | SWT.BORDER);
		containerTables.setLayout(new GridLayout(1, true));
		//gd_containerTables.widthHint = 158;
		containerTables.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		// Inicializa el contenedor de radios
		containerRadios = new Composite(containerTables, SWT.NONE);
		
		// Contenedor de propiedades
		containerProperties = new Composite(container, SWT.BORDER);
		containerProperties.setLayout(new GridLayout(3, false));
		containerProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		// Propiedad URL 
		Label urlLabel = new Label(containerProperties, SWT.NULL);
		urlLabel.setText("URL(*):");
		urlLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));

		// Campo texto URL
		urlText = new Text(containerProperties, SWT.BORDER | SWT.SINGLE);
		urlText.setToolTipText("Define la url a través de la cual se carga el grid");
		urlText.setText("./[nombre entidad]");
		urlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Propiedad alias 
		Label aliasLabel = new Label(containerProperties, SWT.NULL);
		aliasLabel.setText("Alias(*):");
		aliasLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));

		// Campo texto alias
		aliasText = new Text(containerProperties, SWT.BORDER | SWT.SINGLE);
		aliasText.setToolTipText("Define un alias a la entidad generada");
		aliasText.setText("[nombre entidad]");
		aliasText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Propiedad inicio ventana
		Label loadOnStartUpLabel = new Label(containerProperties, SWT.NULL);
		loadOnStartUpLabel.setText("Cargar al inicio de la ventana:  ");
		loadOnStartUpLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1 ,1));

		// Campo check inicio ventana
		loadOnStartUpCheck = new Button(containerProperties, SWT.CHECK);
		loadOnStartUpCheck.setSelection(true);
		loadOnStartUpCheck.setToolTipText("Indica si se cargará el grid a la hora de crearlo o se deberá invocar al reloadGrid para cargarlos");
		loadOnStartUpCheck.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2 ,1));
		
		// Propiedad tipo de ordenación
		Label sortOrderLabel = new Label(containerProperties, SWT.NULL);
		sortOrderLabel.setText("Ordenación:");
		sortOrderLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		// Campo combo de tipo de ordenación
		sortOrderCombo = new Combo(containerProperties, SWT.READ_ONLY);
		sortOrderCombo.add("asc");
		sortOrderCombo.add("desc");
		sortOrderCombo.select(0);
		sortOrderCombo.setToolTipText("Indica el orden (ascendente o descendente) de la columna por la que ordenar el grid en su primera carga");

		Label hiddenLabel = new Label(containerProperties, SWT.NONE);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		// Propiedad ordenación por
		Label sortNameLabel = new Label(containerProperties, SWT.NULL);
		sortNameLabel.setText("Ordenación por:");
		sortNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		// Combo de ordenación por columnas
		sortNameCombo = new Combo(containerProperties, SWT.READ_ONLY);
		sortNameCombo.setToolTipText("Indica el nombre de la columna por el que ordenar la primera vez que se carga el grid");
		new Label(containerProperties, SWT.NONE);

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
		
		//Validaciones campos obligatorios
		if (Utilities.isBlank(urlText.getText())){
				this.setMessage("El campo 'URL' para la entidad seleccionada es obligatorio", IMessageProvider.ERROR);
				return null;
		}

		if (Utilities.isBlank(aliasText.getText())){
			this.setMessage("El campo 'Alias' para el mantenimiento de la entidad seleccionada es obligatorio", IMessageProvider.ERROR);
			return null;
	}

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
		if (loadOnStartUpCheck != null){
			grid.setLoadOnStartUp(loadOnStartUpCheck.getSelection());	
		}
		if (sortOrderCombo != null){
			grid.setSortOrder(sortOrderCombo.getText());	
		}
		if (sortNameCombo != null){
			grid.setSortName(sortNameCombo.getText());	
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
							urlText.setText("./" + widButton.getText().replace("_", "").toLowerCase());
							aliasText.setText(widButton.getText().replace("_", "").toLowerCase());
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
					aliasText.setText(radios[0].getText().replace("_", "").toLowerCase());
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
        setMessage("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
        return true;
    }
    
 
	public boolean getConfirmation() {

		boolean b = MessageDialog.openConfirm(getShell(),"Confirmación",
							"Si ya existe un mantenimiento sobre la entidad seleccionada con ese 'alias', se sobreescribirá. ¿Desea Continuar?");
		return b;
}
	
}