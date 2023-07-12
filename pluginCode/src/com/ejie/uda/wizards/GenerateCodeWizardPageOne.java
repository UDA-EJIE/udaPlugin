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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ejie.uda.operations.DataBaseWorker;
import com.ejie.uda.operations.PropertiesWorker;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.Utilities;

/**
 *  Clase la cual define la primera pantalla del asistente "Generar código de negocio y control"
 */
public class GenerateCodeWizardPageOne extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private Text serviceText;
	private Text sidText;
	private Text hostText;
	private Text portNumberText;
	private Text userNameText;
	private Text passwordText;
	private Text schemaText;
	private Text catalogText;
	private Text urlText;
	
	/**
	 * Primera ventana del Wizard de Plugin, donde se selecciona
	 * la opción de generar una aplicación
	 * @param selection
	 */
	public GenerateCodeWizardPageOne(ISelection selection) {
		super("wizardPage");

		setTitle("Generar código para una aplicación UDA");
		setDescription("Este Wizard genera el código fuente para desplegar una aplicación UDA");
		
		setPageComplete(false);
	}

	/**
	 * Creación de controles de la ventana
	 * @param parent - controlador padre
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 2;
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 3;
		
		// Descripción de la operación
		Label descLabel= new Label(container, SWT.NULL);
		descLabel.setText("Configure una conexión a la base de datos");
		descLabel.setLayoutData(gd3);
		
		// Salto de línea
		Label hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(gd3);
		
		
		// Campo Service Name de la BBDD
		Label serviceLabel= new Label(container, SWT.NULL);
		serviceLabel.setText("&Service Name:");
		serviceText = new Text(container, SWT.BORDER | SWT.SINGLE);
		serviceText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		serviceText.setLayoutData(gd2);
		
		// Campo SID de la BBDD
		Label sidLabel= new Label(container, SWT.NULL);
		sidLabel.setText("&SID:");
		sidText = new Text(container, SWT.BORDER | SWT.SINGLE);
		sidText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		sidText.setLayoutData(gd2);
		
		// Campo host de la BBDD
		Label hostLabel= new Label(container, SWT.NULL);
		hostLabel.setText("&Host:");
		hostText = new Text(container, SWT.BORDER | SWT.SINGLE);
		hostText.addListener(SWT.FocusOut, new Listener() {
			/*
			 * Al salir del campo lanza el evento 
			 */
			public void handleEvent(Event e) {
				setPageComplete(validatePage());
				}
		});
		hostText.setLayoutData(gd2);
		
		// Campo puerto de la BBDD
		Label portNumberLabel= new Label(container, SWT.NULL);
		portNumberLabel.setText("&Puerto:");
		portNumberText = new Text(container, SWT.BORDER | SWT.SINGLE);
		portNumberText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		portNumberText.setLayoutData(gd2);		
		
		// Campo usuario de la BBDD
		Label userNameLabel= new Label(container, SWT.NULL);
		userNameLabel.setText("&Usuario:");
		userNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		userNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		userNameText.setLayoutData(gd2);	
		
		// Campo contraseña de la BBDD
		Label passwordLabel= new Label(container, SWT.NULL);
		passwordLabel.setText("&Contraseña:");
		passwordText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		passwordText.setLayoutData(gd2);	
		
		// Campo esquema de la BBDD
		Label schemaLabel= new Label(container, SWT.NULL);
		schemaLabel.setText("&Esquema:");
		schemaText = new Text(container, SWT.BORDER | SWT.SINGLE);
		schemaText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		schemaText.setLayoutData(gd2);
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
		
		// Campo catalogo de la BBDD
		Label catalogLabel = new Label(container, SWT.NULL);
		catalogLabel.setText("Catálogo:");
		catalogText = new Text(container, SWT.BORDER | SWT.SINGLE);
		catalogText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		catalogText.setLayoutData(gd2);
		
		// Campo url de la BBDD
		Label urlLabel= new Label(container, SWT.NULL);
		urlLabel.setText("URL:");
		urlText = new Text(container, SWT.BORDER | SWT.SINGLE);
		urlText.setEnabled(false);
		urlText.setLayoutData(gd2);	
		
		//Nota esquema mayúsculas
		Label texto= new Label(container, SWT.NULL);
		texto.setText("");
		Label esquemaLabel = new Label(container, SWT.NULL);
		esquemaLabel.setText("NOTA: El esquema/catálogo se deben informar en mayúsculas");
		//esquemaLabel.setText("NOTA: En algunos SGBD el esquema/catálogo se deben informar en mayúsculas");
		esquemaLabel.setLayoutData(gd2);	
				
		//Botón para probar la conexión configurada
		Button testConnectionButton = new Button(container, SWT.NONE);
		testConnectionButton.setText("Probar conexión");
		testConnectionButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (!(Utilities.isBlank(getServiceText()) && Utilities.isBlank(getSidText())) 
					&& !Utilities.isBlank(getHostText()) && !Utilities.isBlank(getPortNumberText())
					&& !Utilities.isBlank(getUserNameText()) && !Utilities.isBlank(getPasswordText()) && !Utilities.isBlank(getUrlText())
					&& !Utilities.isBlank(getSchemaText()) && !Utilities.isBlank(getCatalogText())){
					
					ConnectionData conData = new ConnectionData(getServiceText(),getSidText(), getHostText(), getPortNumberText(), getSchemaText(), getCatalogText(), getUserNameText(), getPasswordText(), getUrlText());
					
					if (DataBaseWorker.testConnection(conData)){
						setMessage("Conexión correcta!", IMessageProvider.INFORMATION);
					}else{
						setMessage("Conexión errónea!", IMessageProvider.ERROR);
					}
				}else{
					setMessage("Faltan datos para probar la conexión!", IMessageProvider.ERROR);
				}
			}
		});
		
		// Recupera los valores de la configuración desde el properties
		getConfigDatabaseProperties();
		
		//Asigna el foco inicial
		serviceText.setFocus();
		setControl(container);
	}
	
	public boolean canFlipToNextPage() {
		return validatePage();
	}
	
	public IWizardPage getNextPage() {
		
		if (Utilities.isBlank(getSidText()) && Utilities.isBlank(getServiceText())){
			setErrorMessage("Se debe informar el campo 'Service Name' o el 'SID'");
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
			setErrorMessage("Conexión errónea");
			return getWizard().getContainer().getCurrentPage();
		}

		GenerateCodeWizard generateCodeWizard = (GenerateCodeWizard)getWizard();
		if (generateCodeWizard != null) {
			GenerateCodeWizardPageTwo generateCodeWizardPageTwo = generateCodeWizard.getPageGenerateCodeWizardPageTwo();
			if (generateCodeWizardPageTwo != null) {
				generateCodeWizardPageTwo.setSchemaCheckboxTree(conData);
				// Selecciona todos los checks como estado inicial
				generateCodeWizardPageTwo.setInitialCheckedState();
				setPageComplete(true);
				return super.getNextPage();	
			}
		}
		
		return getWizard().getContainer().getCurrentPage();
	}
	
	/*************/
	/*  Getters  */
	/*************/

	/**
	 * @return the serviceText
	 */
	public String getServiceText() {
		if (serviceText != null) {
			return serviceText.getText();
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
	 * Recupera el nombre del catálogo de la BBDD
	 * 
	 * @return nombre del catálogo de la BBDD
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
	
	public ConnectionData getConnectionData() {
		ConnectionData conData = null;

		if ( !(Utilities.isBlank(getServiceText()) && Utilities.isBlank(getSidText()))
				&& !Utilities.isBlank(getHostText())
				&& !Utilities.isBlank(getPortNumberText())
				&& !Utilities.isBlank(getUserNameText())
				&& !Utilities.isBlank(getPasswordText())
				&& !Utilities.isBlank(getUrlText())
				&& !Utilities.isBlank(getSchemaText())
				&& !Utilities.isBlank(getCatalogText())) {

			conData = new ConnectionData(getServiceText(), getSidText(), getHostText(),
					getPortNumberText(), getSchemaText(), getCatalogText(), getUserNameText(),
					getPasswordText(), getUrlText());

		}
		return conData;
	}
	
	/**********************/
	/*  Métodos privados  */
	/**********************/
	
	//Si se informa el SID y el Service Name se generará la conexión mediante service name.
	private String buildUrlConnection(){
		String url = "";
		
		if (!Utilities.isBlank(getSidText()) && !Utilities.isBlank(getHostText()) && !Utilities.isBlank(getPortNumberText())){
			url = "jdbc:oracle:thin:@" + getHostText() + ":" + getPortNumberText() + ":" + getSidText();
		}
		
		else if (!Utilities.isBlank(getServiceText()) && !Utilities.isBlank(getHostText()) && !Utilities.isBlank(getPortNumberText())){
			url = "jdbc:oracle:thin:@//" + getHostText() + ":" + getPortNumberText() + "/" + getServiceText();
		}
		
		return url;
	}
	
    /**
     * Returns whether this page's controls currently all contain valid 
     * values.
     *
     * @return <code>true if all controls are valid, and
     *   <code>false if at least one is invalid
     */
    protected boolean validatePage() {
    	
    	setErrorMessage(null);
    	
    	if (!Utilities.isBlank(getServiceText()) && !Utilities.validateServiceText(getServiceText())) {
			setErrorMessage("El campo 'Service Name' no cumple con ninguno de los formatos esperados: codapp.subdominio.dominio o codapp.subdominio.subdominio.dominio");
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
    		setErrorMessage("El campo 'Puerto' sólo acepta números");
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
    	
    	if ((Utilities.isBlank(getServiceText()) && Utilities.isBlank(getSidText()))
				|| Utilities.isBlank(getHostText())
				|| Utilities.isBlank(getPortNumberText())
				|| Utilities.isBlank(getUserNameText())
				|| Utilities.isBlank(getPasswordText())
				|| Utilities.isBlank(getUrlText())
				|| Utilities.isBlank(getSchemaText())
				|| Utilities.isBlank(getCatalogText())) {
    		setErrorMessage(null);
            setMessage("Este Wizard genera el código fuente para desplegar una aplicación UDA");
            return false;
		}
    	
        setErrorMessage(null);
        setMessage("Este Wizard genera el código fuente para desplegar una aplicación UDA");
        return true;
    }
    
	private void getConfigDatabaseProperties() {

		GenerateCodeWizard newMaintWizard = (GenerateCodeWizard) getWizard();
		if (newMaintWizard != null) {

			PropertiesWorker udaProperties = newMaintWizard.getProperties();

			if (udaProperties != null) {
				if (!Utilities.isBlank(udaProperties.readValue("service"))) {
					serviceText.setText(udaProperties.readValue("service"));
				}
				if (!Utilities.isBlank(udaProperties.readValue("sid"))) {
					sidText.setText(udaProperties.readValue("sid"));
				}
				if (!Utilities.isBlank(udaProperties.readValue("host"))) {
					hostText.setText(udaProperties.readValue("host"));
				}
				if (!Utilities.isBlank(udaProperties.readValue("portnumber"))) {
					portNumberText.setText(udaProperties.readValue("portnumber"));
				}
				if (!Utilities.isBlank(udaProperties.readValue("username"))) {
					userNameText.setText(udaProperties.readValue("username"));
				}
				if (!Utilities.isBlank(udaProperties.readValue("password"))) {
					passwordText.setText(udaProperties.readValue("password"));
				}
				if (!Utilities.isBlank(udaProperties.readValue("schema"))) {
					schemaText.setText(udaProperties.readValue("schema"));
				}
				if (!Utilities.isBlank(udaProperties.readValue("catalog"))) {
					catalogText.setText(udaProperties.readValue("catalog"));
				}
			}
		}
	}
    
}