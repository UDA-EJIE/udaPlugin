package com.ejie.uda.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jst.j2ee.project.EarUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.ejie.uda.operations.DataBaseWorker;
import com.ejie.uda.operations.PropertiesWorker;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

/**
 * Clase la cual define la primera pantalla del asistente "Generar mantenimiento"
 */
public class NewMaintWizardPageOne extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private Text warLocationText;
	private Button warLocationButton;
	private IProject warProject;
	// Propiedades de conexión
	private Text sidText;
	private Text hostText;
	private Text portNumberText;
	private Text userNameText;
	private Text passwordText;
	private Text schemaText;
	private Text catalogText;
	private Text urlText;
	
	/**
	 * Primera ventana del Wizard de Plugin
	 * @param selection
	 */
	public NewMaintWizardPageOne(ISelection selection) {
		super("wizardPage");

		setTitle("Generar nuevo mantenimiento para una aplicación");
		setDescription("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
		
		setPageComplete(false);
	}

	/**
	 * Creación de controles de la ventana
	 * @param parent - controlador padre
	 */
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		
		// Descripción de la operación
		Label descLabel= new Label(container, SWT.NULL);
		descLabel.setText("Seleccione el WAR al que se quiere Añadir el mantenimiento y configure una conexión a la base de datos");
		descLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		
		// Salto de línea
		Label hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		
		// Grupo de proyecto WAR
		Group presentationGroup = new Group(container, SWT.NONE);
		presentationGroup.setText("selección de proyecto WAR");
		presentationGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 3, 1));
		presentationGroup.setLayout(new GridLayout(4, true));
		
		// Campo texto de la ruta de localización de xxxWAR
		warLocationText = new Text(presentationGroup, SWT.BORDER | SWT.SINGLE);
		warLocationText.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, true,3,1));
		warLocationText.setEnabled(false);
		warLocationText.setToolTipText("Nombre del proyecto WAR donde se generará el mantenimiento");
		warLocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		
		warLocationButton = new Button(presentationGroup, SWT.PUSH);
		warLocationButton.setText("Buscar...");
		warLocationButton.setEnabled(true);
		warLocationButton.setToolTipText("Buscar un proyecto WAR");
		warLocationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setWarProject(handleBrowseWARProject());
				if (getWarProject() != null) {
					warLocationText.setText(getWarProject().getName());
				}
			}
		});
		warLocationButton.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, true,1,1));
		
		// Salto de línea
		hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		
		// Grupo de conexión a la BBDD
		Group connectionGroup = new Group(container, SWT.NONE);
		connectionGroup.setText("Datos de conexión de la base de datos");
		connectionGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 3, 1));
		connectionGroup.setLayout(new GridLayout(4, true));
		
		// Campo SID de la BBDD
		Label sidLabel= new Label(connectionGroup, SWT.NULL);
		sidLabel.setText("&SID:");
		sidText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		sidText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		sidText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		// Campo host de la BBDD
		Label hostLabel= new Label(connectionGroup, SWT.NULL);
		hostLabel.setText("&Host:");
		hostText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		hostText.addListener(SWT.FocusOut, new Listener() {
			/*
			 * Al salir del campo lanza el evento 
			 */
			public void handleEvent(Event e) {
				setPageComplete(validatePage());
				}
		});
		hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		// Campo puerto de la BBDD
		Label portNumberLabel= new Label(connectionGroup, SWT.NULL);
		portNumberLabel.setText("&Puerto:");
		portNumberText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		portNumberText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		portNumberText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));		
		
		// Campo usuario de la BBDD
		Label userNameLabel= new Label(connectionGroup, SWT.NULL);
		userNameLabel.setText("&Usuario:");
		userNameText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		userNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));	
		
		// Campo contraseña de la BBDD
		Label passwordLabel= new Label(connectionGroup, SWT.NULL);
		passwordLabel.setText("Contraseña:");
		passwordText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));	
		
		// Campo esquema de la BBDD
		Label schemaLabel= new Label(connectionGroup, SWT.NULL);
		schemaLabel.setText("&Esquema:");
		schemaText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		schemaText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		schemaText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
				/*
				 * Valida el contenido del campo 
				 */
				if (schemaText.getText().length() > 0 ) {
					catalogText.setText(schemaText.getText());
				}
			}
		});
		schemaText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		// Campo catalogo de la BBDD
		Label catalogLabel = new Label(connectionGroup, SWT.NULL);
		catalogLabel.setText("Catálogo:");
		catalogText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		catalogText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		catalogText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		// Campo url de la BBDD
		Label urlLabel= new Label(connectionGroup, SWT.NULL);
		urlLabel.setText("URL:");
		urlText = new Text(connectionGroup, SWT.BORDER | SWT.SINGLE);
		urlText.setEnabled(false);
		urlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));	
		
		//Nota esquema mayúsculas
		Label texto= new Label(connectionGroup, SWT.NULL);
		texto.setText("");
		Label esquemaLabel = new Label(connectionGroup, SWT.NULL);
		esquemaLabel.setText("NOTA: En algunos SGBD el esquema/catálogo se deben informar en mayúsculas");
		esquemaLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));	
		
		// Salto de línea
		hiddenLabel= new Label(connectionGroup, SWT.NULL);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
		
		//botón para probar la conexión configurada
		Button testConnectionButton = new Button(connectionGroup, SWT.NONE);
		testConnectionButton.setText("Probar conexión");
		testConnectionButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (!Utilities.isBlank(getSidText()) && !Utilities.isBlank(getHostText()) && !Utilities.isBlank(getPortNumberText())
					&& !Utilities.isBlank(getUserNameText()) && !Utilities.isBlank(getPasswordText()) && !Utilities.isBlank(getUrlText())
					&& !Utilities.isBlank(getSchemaText()) && !Utilities.isBlank(getCatalogText())){
					
					ConnectionData conData = new ConnectionData(getSidText(), getHostText(), getPortNumberText(), getSchemaText(), getCatalogText(), getUserNameText(), getPasswordText(), getUrlText());
					
					if (DataBaseWorker.testConnection(conData)){
						setMessage("conexión correcta!", IMessageProvider.INFORMATION);
					}else{
						setMessage("conexión errónea!", IMessageProvider.ERROR);
					}
				}else{
					setMessage("No hay datos suficientes para probar la conexión!", IMessageProvider.ERROR);
				}
			}
		});
		//TODO: Añadir colores para la comprobación de la conexión
		
		// Salto de línea
		hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
		
		// Recupera los valores de la configuración desde el properties
		getConfigDatabaseProperties();
		
		//Asigna el foco inicial
		sidText.setFocus();
		
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
		
		if (getWarProject() == null) {
			setErrorMessage("Se debe seleccionar algún proyecto tipo WAR para generar el mantenimiento");
			return getWizard().getContainer().getCurrentPage();
		}
		if (Utilities.isBlank(getSidText())){
			setErrorMessage("El campo 'SID' obligatorio");
			return getWizard().getContainer().getCurrentPage();
		}
    	if (Utilities.isBlank(getHostText())){
    		setErrorMessage("El campo 'Host' obligatorio");
    		return getWizard().getContainer().getCurrentPage();
		}
    	if (Utilities.isBlank(getPortNumberText())){
    		setErrorMessage("El campo 'Puerto' obligatorio");
    		return getWizard().getContainer().getCurrentPage();
		}
    	if (Utilities.isBlank(getUserNameText())){
    		setErrorMessage("El campo 'Usuario' obligatorio");
    		return getWizard().getContainer().getCurrentPage();
		}
    	if (Utilities.isBlank(getPasswordText())){
    		setErrorMessage("El campo 'Contraseña' obligatorio");
    		return getWizard().getContainer().getCurrentPage();
		}
    	if (Utilities.isBlank(getSchemaText())){
    		setErrorMessage("El campo 'Esquema' obligatorio");
    		return getWizard().getContainer().getCurrentPage();
		}
    	if (Utilities.isBlank(getCatalogText())){
    		setErrorMessage("El campo 'Catálogo' obligatorio");
    		return getWizard().getContainer().getCurrentPage();
		}
		
    	ConnectionData conData = getConnectionData();
    	if (conData == null || !DataBaseWorker.testConnection(conData)){
			setErrorMessage("conexión errónea");
			return getWizard().getContainer().getCurrentPage();
		}
    	
    	NewMaintWizard newMaintWizard = (NewMaintWizard)getWizard();
		if (newMaintWizard != null) {
			NewMaintWizardPageThree newMaintWizardPageThree = newMaintWizard.getPageNewMaintWizardPageThree();
			if (newMaintWizardPageThree != null) {
				// Genera radios para cada tabla leída
				newMaintWizardPageThree.setTablesRadios(conData);
				return super.getNextPage();	
			}
		}

    	return super.getNextPage();	
	}
	
	/*************/
	/*  Getters  */
	/*************/
	
	/**
	 * Recupera el proyecto WAR
	 * 
	 * @return proyecto WAR
	 */
	public IProject getWarProject(){
		return this.warProject;
	}
	
	/**
	 * Asigna el proyecto WAR
	 * 
	 * @param warProject - proyecto WAR
	 */
	public void setWarProject(IProject warProject){
		this.warProject = warProject;
	}
	
	/**
	 * Recupera la ruta del proyecto seleccionado
	 * 
	 * @return ruta del proyecto seleccionado
	 */
	public String getWarLocationText() {
		if (warLocationText != null) {
			return warLocationText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre del SID de BBDD
	 * 
	 * @return nombre del SID de BBDD
	 */
	public String getSidText() {
		if (sidText != null) {
			return sidText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre del host
	 * 
	 * @return nombre del host
	 */
	public String getHostText() {
		if (hostText != null) {
			return hostText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre del puerto
	 * 
	 * @return nombre del puerto
	 */
	public String getPortNumberText() {
		if (portNumberText != null) {
			return portNumberText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre del usuario de BBDD
	 * 
	 * @return nombre del usuario de BBDD
	 */
	public String getUserNameText() {
		if (userNameText != null) {
			return userNameText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera la contraseña de la BBDD
	 * 
	 * @return contraseña de la BBDD
	 */
	public String getPasswordText() {
		if (passwordText != null) {
			return passwordText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre del esquema de la BBDD
	 * 
	 * @return nombre del esquema de la BBDD
	 */
	public String getSchemaText() {
		if (schemaText != null) {
			return schemaText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre del Catálogo de la BBDD
	 * 
	 * @return nombre del Catálogo de la BBDD
	 */
	public String getCatalogText() {
		if (catalogText != null) {
			return catalogText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre de la url de conexión
	 * 
	 * @return nombre de la url de conexión
	 */
	public String getUrlText() {
		if (urlText != null) {
			return urlText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera todos los datos de conexión para la BBDD
	 * 
	 * @return objeto de conexión para la BBDD
	 */
	public ConnectionData getConnectionData() {
		ConnectionData conData = null;

		if (!Utilities.isBlank(getSidText())
				&& !Utilities.isBlank(getHostText())
				&& !Utilities.isBlank(getPortNumberText())
				&& !Utilities.isBlank(getUserNameText())
				&& !Utilities.isBlank(getPasswordText())
				&& !Utilities.isBlank(getUrlText())
				&& !Utilities.isBlank(getSchemaText())
				&& !Utilities.isBlank(getCatalogText())) {

			conData = new ConnectionData(getSidText(), getHostText(),
					getPortNumberText(), getSchemaText(), getCatalogText(), getUserNameText(),
					getPasswordText(), getUrlText());

		}
		return conData;
	}
	
	/**********************/
	/*  Métodos privados  */
	/**********************/
	
	/**
	 * Construye la url de conexión de la BBDD
	 * con los campos indicados
	 * 
	 * @return url de la BBDD
	 */
	private String buildUrlConnection(){
		String url = "";
		
		if (!Utilities.isBlank(getSidText()) && !Utilities.isBlank(getHostText()) && !Utilities.isBlank(getPortNumberText())){
			url = "jdbc:oracle:thin:@" + getHostText() + ":" + getPortNumberText() + ":" + getSidText();
		}
		
		return url;
	}
	
    /**
     * Valida los campos de la pantalla
     * 
     * @return true si todos los controles están validados, false si algún campo no es válido.
     */
    protected boolean validatePage() {
    	
    	setErrorMessage(null);
    	
    	// Validación de selección de proyecto WAR de UDA
		if (!Utilities.isBlank(getWarLocationText()) && getWarProject() == null) {
			setErrorMessage("Error en la selección del proyecto");
			return false;
		}
    	
    	if (!Utilities.isBlank(getSidText()) && !Utilities.validateText(getSidText())) {
			setErrorMessage("Caracteres no válidos para en campo 'SID'");
			return false;
		}
    	
    	if (!Utilities.isBlank(getHostText()) ){
			if (Utilities.validateNumber(getHostText().replace(".", "")) && !Utilities.validateIPAdderess(getHostText())){
				setErrorMessage("IP no válida para en campo 'Host'");
				return false;
			}else if (!Utilities.validateHostName(getHostText())){
				setErrorMessage("Nombre no válidos para en campo 'Host'");
				return false;
			}
		}
    	
    	if (!Utilities.isBlank(getPortNumberText()) && !Utilities.validateNumber(getPortNumberText())) {
    		setErrorMessage("El campo 'Puerto' sólo acepta Números");
    		return false;
		}
    	
    	//Intenta montar la URL de conexión
		urlText.setText(buildUrlConnection());
		
    	if (!Utilities.isBlank(getUserNameText()) && !Utilities.validateText(getUserNameText())) {
    		setErrorMessage("Caracteres no válidos para en campo 'Usuario'");
			return false;
		}
    	
    	if (!Utilities.isBlank(getSchemaText()) && !Utilities.validateText(getSchemaText())) {
    		setErrorMessage("Caracteres no válidos para en campo 'Esquema'");
			return false;
		}
    	
    	if (!Utilities.isBlank(getCatalogText()) && !Utilities.validateText(getCatalogText())) {
    		setErrorMessage("Caracteres no válidos para en campo 'Catálogo'");
			return false;
		}
    	
    	if (Utilities.isBlank(getSidText())
				|| Utilities.isBlank(getHostText())
				|| Utilities.isBlank(getPortNumberText())
				|| Utilities.isBlank(getUserNameText())
				|| Utilities.isBlank(getPasswordText())
				|| Utilities.isBlank(getUrlText())
				|| Utilities.isBlank(getSchemaText())
				|| Utilities.isBlank(getCatalogText())) {
    		setErrorMessage(null);
            setMessage("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
            return false;
		}
    	
        setErrorMessage(null);
        setMessage("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
        return true;
    }
    
    /**
     * Rellena los campo de la pantalla con los datos de conexión
     * guardados en el properties previamente
     */
    private void getConfigDatabaseProperties(){
    	
    	NewMaintWizard newMaintWizard = (NewMaintWizard)getWizard();
		if (newMaintWizard != null) {
			
			PropertiesWorker udaProperties  = newMaintWizard.getProperties();
			
			if (udaProperties != null){
				if (!Utilities.isBlank(udaProperties.readValue("sid"))){
		    		sidText.setText(udaProperties.readValue("sid"));	
		    	}
		    	if (!Utilities.isBlank(udaProperties.readValue("host"))){
		    		hostText.setText(udaProperties.readValue("host"));	
		    	}
		    	if (!Utilities.isBlank(udaProperties.readValue("portnumber"))){
		    		portNumberText.setText(udaProperties.readValue("portnumber"));	
		    	}
		    	if (!Utilities.isBlank(udaProperties.readValue("username"))){
		    		userNameText.setText(udaProperties.readValue("username"));	
		    	}
		    	if (!Utilities.isBlank(udaProperties.readValue("password"))){
		    		passwordText.setText(udaProperties.readValue("password"));	
		    	}
		    	if (!Utilities.isBlank(udaProperties.readValue("schema"))){
		    		schemaText.setText(udaProperties.readValue("schema"));	
		    	}
		    	if (!Utilities.isBlank(udaProperties.readValue("catalog"))){
		    		catalogText.setText(udaProperties.readValue("catalog"));	
		    	}
			}
		}
    }
    
    /**
     * Diálogo de selección de proyectos WAR
     * 
     * @return - proyecto WAR de UDA
     */
	private IProject handleBrowseWARProject() {

		IProject[] projectsWAR;

		projectsWAR = EarUtilities.getAllProjectsInWorkspaceOfType("jst.web");

		ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Proyectos WAR");
		dialog.setMessage("Proyectos WAR al que se generará el código");
		dialog.setElements(getProjectsWAR(projectsWAR));

		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				return ((IProject) result[0]);
			}
		}
		return null;
	}
	
	/**
	 * Filtra el array con los proyectos que sean de tipo WAR de UDA
	 * 
	 * @param projects - array de proyectos abiertos del workspace
	 * @return - array de proyectos WAR de UDA
	 */
	private IProject[] getProjectsWAR(IProject[] projects){
		List<IProject> projectsWAR = new ArrayList<IProject>(0);
		
		for (IProject project : projects) {
			if (project != null){
				String nameProject = project.getName();
				
				if (!Utilities.isBlank(nameProject) && nameProject.endsWith(Constants.WAR_NAME)){
					projectsWAR.add(project);
				}
			}	
		}

		if (projectsWAR != null && !projectsWAR.isEmpty()){
			projects = projectsWAR.toArray(new IProject[projectsWAR.size()]);	
		}else{
			projects = null;
		}
		return projects;
	}
	
}