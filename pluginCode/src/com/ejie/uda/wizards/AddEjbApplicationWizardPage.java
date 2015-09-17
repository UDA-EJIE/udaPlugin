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

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jst.j2ee.project.EarUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

/**
 *  Clase la cual define la pantalla del asistente "Añadir Proyecto EJB".
 */
public class AddEjbApplicationWizardPage extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private Text ejbNameText;
	private Text ejbFullNameText;
	private Text earNameText;
	private IProject projectEAR;
	
	/**
	 * Primera ventana del Wizard de Plugin, donde se selecciona
	 * la opción de generar una aplicación
	 * @param selection
	 */
	public AddEjbApplicationWizardPage(ISelection selection) {
		super("wizardPage");

		setTitle("Añadir un módulo EJB a la aplicación");
		setDescription("Este Wizard genera un nuevo modulo EJB y lo añade a un EAR existente");
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
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 3;
		
		// Campo texto de nombre del EAR
		Label labelProjectEAR = new Label(container, SWT.NULL);
		labelProjectEAR.setText("EAR a vincular:");
		earNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		earNameText.setEnabled(false);
		earNameText.setTextLimit(25);
		
		Button button = new Button(container, SWT.NONE);
		button.setText("&Buscar Proyecto");
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					setProjectEAR(handleBrowseEARProject());
					if (getProjectEAR() != null) {
						earNameText.setText(getProjectEAR().getName());
						ejbFullNameText.setText(buildEjbFullName(earNameText.getText(), ejbNameText.getText()));
					}
					break;
				}
			}
		});
		
		Label labelLine = new Label(container, SWT.NULL);
		labelLine.setLayoutData(gd3);
		
		// Campo texto de nombre del EJB
		Label labelEJBName = new Label(container, SWT.NULL);
		labelEJBName.setText("&Nombre del proyecto EJB:");
		ejbNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		ejbNameText.setTextLimit(25);
		ejbNameText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
				/*
				 * Valida el contenido del campo y monta el nombre del WAR completo 
				 */
				if (ejbNameText.getText().length() > 0 && !Utilities.validateWARText(ejbNameText.getText())) {
					setMessage("Caracteres no válidos para en campo 'Nombre del módulo EJB'", IMessageProvider.ERROR);
				}else{
					setMessage("Este Wizard genera un nuevo módulo EJB y lo añade a un EAR existente");
					ejbFullNameText.setText(buildEjbFullName(earNameText.getText(), ejbNameText.getText()));
				}
			}
		});
		ejbNameText.addListener(SWT.FocusOut, new Listener() {
			/*
			 * Al salir del campo se capitaliza (camelCase) los caracteres 
			 */
			public void handleEvent(Event e) {
				ejbNameText.setText(Utilities.camelCase(ejbNameText.getText()));
			}
		});

		Label labelHide = new Label(container, SWT.NULL);
		labelHide.setVisible(false);
		
		// Campo texto de nombre del WAR
		Label labelFullNameEJB = new Label(container, SWT.NULL);
		labelFullNameEJB.setText("Nombre Completo del módulo EJB:");
		ejbFullNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		ejbFullNameText.setEnabled(false);

		Label labelHide2 = new Label(container, SWT.NULL);
		labelHide2.setVisible(false);

		labelLine = new Label(container, SWT.NULL);
		labelLine.setLayoutData(gd3);
		

		earNameText.setLayoutData(gd);
		ejbNameText.setLayoutData(gd);
		ejbFullNameText.setLayoutData(gd);
		
		setControl(container);
	}
	
	/*************/
	/*  Getters  */
	/*************/
	
	/**
	 * Recupera el nombre del WAR
	 * 
	 * @return nombre del WAR
	 */
	public String getWarNameText() {
		if (ejbNameText != null) {
			return ejbNameText.getText();
		} else {
			return "";
		}
	}
	/**
	 * Recupera el nombre completo del WAR
	 * 
	 * @return nombre del WAR
	 */
	public String getWarFullNameText() {
		if (ejbFullNameText != null) {
			return ejbFullNameText.getText();
		} else {
			return "";
		}
	}
	/**
	 * Recupera el nombre completo del EAR
	 * 
	 * @return nombre del EAR
	 */
	public String getEarNameText() {
		if (earNameText != null) {
			return earNameText.getText();
		} else {
			return "";
		}
	}
	/**
	 * Recupera proyecto EAR
	 * 
	 * @return proyecto EAR
	 */
	public IProject getProjectEAR(){
		return this.projectEAR;
	}
	
	/**
	 * Establece el proyecto EAR
	 * 
	 * @return proyecto EAR
	 */
	public void setProjectEAR(IProject project){
		this.projectEAR = project;
	}
	
	/**
	 * Recupera el código EJB
	 * 
	 * @return código EJB 
	 */
	public String getEJBCodName(){
		String codApp = getWarFullNameText();
		
		if (!Utilities.isBlank(codApp) && codApp.endsWith(Constants.EJB_NAME)){
			codApp = codApp.substring(0, codApp.length() - Constants.EJB_NAME.length());
		}
		
		return codApp;
	}
	
	
	/**********************/
	/*  Métodos privados  */
	/**********************/
	/**
	 * Función utilizada en el popup del EAR
	 * 
	 * @return nombre
	 */
	private IProject handleBrowseEARProject() {

		IProject[] projectsEAR;

		projectsEAR = EarUtilities.getAllProjectsInWorkspaceOfType("jst.ear");

		ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Proyectos EAR");
		dialog.setMessage("Proyectos EAR al que se desea vincular");
		dialog.setElements(projectsEAR);

		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				IProject project = ((IProject) result[0]);
				if (isProjectEAREjie(project)){
					
					return project;	
				}
			}
		}
		return null;
	}
	/**
	 * Verifica el nombre del proyecto
	 * 
	 * @return correcto
	 */
	private boolean isProjectEAREjie(IProject project){
		
		if (project != null){
			String nameProject = project.getName();
			
			if (!Utilities.isBlank(nameProject) && nameProject.endsWith(Constants.EAR_NAME)){
				return true;
			}
		}
		
		return false;
	}
	/**
	 * Calcula el nombre completo del EJB a generar
	 * 
	 * @return nombre
	 */
	private String buildEjbFullName(String earName, String warName){
		String fullName = "";
		String codApp = "";
		
		if (!Utilities.isBlank(earName) && !Utilities.isBlank(warName)){
			if (earName.endsWith(Constants.EAR_NAME)){
				codApp = earName.substring(0, earName.length() - Constants.EAR_NAME.length());
				if (!Utilities.isBlank(codApp)){
					fullName = codApp.toLowerCase() + Utilities.camelCase(warName) + Constants.EJB_NAME;	
				}
			}
		}
		
		return fullName;
	}
	
}