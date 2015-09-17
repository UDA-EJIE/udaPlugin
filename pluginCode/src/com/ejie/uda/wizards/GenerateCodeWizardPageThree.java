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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jst.j2ee.project.EarUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.ejie.uda.Activator;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

/**
 *  Clase la cual define la tercera pantalla del asistente "Generar código de negocio y control"
 */
public class GenerateCodeWizardPageThree extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private Button dataModelCheck;
	private Button daoCheck;
	private Button serviceCheck;
	private Button controllerCheck;
	private Button annotControlCheck;
	private Text earClassesLocationText;
	private Button earClassesLocationButton;
	private Text warLocationText;
	private Button warLocationButton;
	private IProject earClassesProject;
	private IProject warProject;
	private Group businessGroup;
	private Group presentationGroup;
	private Group inyectionGroup;
	private Button radAnot;
	private Button radXml;
	
	/**
	 * Primera ventana del Wizard de Plugin, donde se selecciona
	 * la opción de generar una aplicación
	 * @param selection
	 */
	public GenerateCodeWizardPageThree(ISelection selection) {
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
		//layout.numColumns = 4;

		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 2;
		GridData gd3 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd3.horizontalSpan = 3;
		GridData gd4 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd4.horizontalSpan = 4;
		

		// Descripción de la operación
		Label descLabel= new Label(container, SWT.NULL);
		descLabel.setText("Seleccione las opciones para las distintos componentes");
		
		// Salto de línea
		Label hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(gd2);
		
		//INYECCION
		inyectionGroup = new Group(container, SWT.NONE);
		inyectionGroup.setText("Inyección de dependencia");
		inyectionGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true));
		inyectionGroup.setLayout(new GridLayout(4, false));
		
			//Anotaciones
			radAnot = new Button(inyectionGroup, SWT.RADIO);
			radAnot.setText("Anotaciones");
			radAnot.setSelection(true);
			radAnot.setLayoutData(new GridData (SWT.CENTER, SWT.FILL, true, true,1,1));
		
			//XML
			radXml = new Button(inyectionGroup, SWT.RADIO);
			radXml.setText("XML");
			radXml.setSelection(false);
			radXml.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true,1,1));

			// Salto de línea
		hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(gd2);
			
		//NEGOCIO
	    businessGroup = new Group(container, SWT.NONE);
	    businessGroup.setText("Componentes de Negocio");
	    businessGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true));
	    businessGroup.setLayout(new GridLayout(4, false));
	
		    // Check box de generación de Modelo de Datos
			dataModelCheck = new Button(businessGroup, SWT.CHECK);
			dataModelCheck.setText("Modelo de datos");
			dataModelCheck.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	            	enableBrowserEARClassesProject();
	            	setPageComplete(getCanFinish());
	            }
	        });
			dataModelCheck.setLayoutData(gd4);
	
			// Check box de generación de los DAOs
			daoCheck = new Button(businessGroup, SWT.CHECK);
			daoCheck.setText("DAOs");
			daoCheck.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	            	enableBrowserEARClassesProject();
	            	setPageComplete(getCanFinish());
	            }
	        });
			daoCheck.setLayoutData(gd4);
			
			// Check box de generación de Servicos
			serviceCheck = new Button(businessGroup, SWT.CHECK);
			serviceCheck.setText("Servicios");
			serviceCheck.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	            	enableBrowserEARClassesProject();
	            	setPageComplete(getCanFinish());
	            	
	            }
	        });
			serviceCheck.setLayoutData(gd4);
		
			// Campo texto de la ruta de localización de xxxEARClasses
			earClassesLocationText = new Text(businessGroup, SWT.BORDER | SWT.SINGLE);
			earClassesLocationText.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, true,3,1));
			earClassesLocationText.setEnabled(false);
			
			earClassesLocationButton = new Button(businessGroup, SWT.PUSH);
			earClassesLocationButton.setText("        Buscar...      ");
			earClassesLocationButton.setEnabled(false);
			earClassesLocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					setEARClassesProject(handleBrowseEARClassesProject());
					if (getEARClassesProject() != null) {
						earClassesLocationText.setText(getEARClassesProject().getName());
					}
				}
			});
			earClassesLocationButton.setLayoutData(new GridData (SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		
		// Salto de línea
		hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(gd2);
		
		//PRESENTACION
		presentationGroup = new Group(container, SWT.NONE);
		presentationGroup.setText("Componentes de Presentación");
		presentationGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true));
		presentationGroup.setLayout(new GridLayout(4, false));
		
			// Check box de generación de Controllers
			controllerCheck = new Button(presentationGroup, SWT.CHECK);
			controllerCheck.setText("Controladores");
			controllerCheck.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	            	enableBrowserWARProject();
	            	setPageComplete(getCanFinish());
	            }
	        });
			controllerCheck.setLayoutData(gd4);
			
			// Campo texto de la ruta de localización de xxxWAR
			warLocationText = new Text(presentationGroup, SWT.BORDER | SWT.SINGLE);
			warLocationText.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, true,3,1));
			warLocationText.setEnabled(false);
			warLocationButton = new Button(presentationGroup, SWT.PUSH);
			warLocationButton.setText("        Buscar...      ");
			warLocationButton.setEnabled(false);
			warLocationButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					setWarProject(handleBrowseWARProject());
					if (getWarProject() != null) {
						warLocationText.setText(getWarProject().getName());
					}
				}
			});
			warLocationButton.setLayoutData(new GridData (SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		
		setControl(container);
	}
	
	/*************/
	/*  Getters  */
	/*************/
	
	/**
	 * Recupera el nombre del proyecto EARClasses
	 * 
	 * @return nombre del proyecto EARClasses
	 */
	public String getEarClassesLocationText() {
		if (earClassesLocationText != null) {
			return earClassesLocationText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre del proyecto WAR
	 * 
	 * @return nombre del proyecto WAR
	 */
	public String getWarLocationText() {
		if (warLocationText != null) {
			return warLocationText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el valor del check de Modelo de Datos
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getDataModelCheck() {
		return dataModelCheck.getSelection();
	}
	
	/**
	 * Recupera el valor del check de Dao
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getDaoCheck() {
		return daoCheck.getSelection();
	}
	
	/**
	 * Recupera el valor del check de Annot
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getAnnotControlCheck() {
		return annotControlCheck.getSelection();
	}
	/**
	 * Recupera el valor del check de Service
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getServiceCheck() {
		return serviceCheck.getSelection();
	}
	
	/**
	 * Recupera el valor del check de Controller
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getControllerCheck() {
		return controllerCheck.getSelection();
	}
	/**
	 * Recupera el valor del radio de Inyección de dependencias Anotaciones
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getRadAnot() {
		return radAnot.getSelection();
	}
	/**
	 * Recupera el valor del radio de Inyección de dependencias XML 
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getRadXml() {
		return radXml.getSelection();
	}
	/**
	 * Recupera el proyecto EARClasses
	 * 
	 * @return proyecto EARClasses
	 */
	public IProject getEARClassesProject(){
		return this.earClassesProject;
	}
	
	/**
	 * Asigna el proyecto EARClasses
	 * 
	 * @param earClassesProject - proyecto EARClasses
	 */
	public void setEARClassesProject(IProject earClassesProject){
		this.earClassesProject = earClassesProject;
	}

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

	/**********************/
	/*  Métodos privados  */
	/**********************/
	private IProject handleBrowseEARClassesProject() {

		IProject[] projectsEARClasses;

		projectsEARClasses = EarUtilities.getAllProjectsInWorkspaceOfType("jst.java");

		ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Proyectos EARClasses");
		dialog.setMessage("Proyectos EARClasses al que se generará el código");
		dialog.setElements(getProjectsEARClasses(projectsEARClasses));

		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				return ((IProject) result[0]);
			}
		}
		return null;
	}
	
	private IProject[] getProjectsEARClasses(IProject[] projects){
		List<IProject> projectsEARClasses = new ArrayList<IProject>(0);
		
		for (IProject project : projects) {
			if (project != null){
				String nameProject = project.getName();
				
				if (!Utilities.isBlank(nameProject) && nameProject.endsWith(Constants.EARCLASSES_NAME)){
					projectsEARClasses.add(project);
				}
			}	
		}

		if (projectsEARClasses != null && !projectsEARClasses.isEmpty()){
			projects = projectsEARClasses.toArray(new IProject[projectsEARClasses.size()]);	
		}else{
			projects = null; 
		}
		
		return projects;
	}
	
	private void enableBrowserEARClassesProject(){
		 if (dataModelCheck.getSelection() || daoCheck.getSelection() || serviceCheck.getSelection()) {
	        earClassesLocationButton.setEnabled(true);
         } else {
         	earClassesLocationText.setText("");
	       	earClassesLocationButton.setEnabled(false);
         }
	}
	
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
	
	private void enableBrowserWARProject(){
		 if (controllerCheck.getSelection()) {
	        warLocationButton.setEnabled(true);
         } else {
         	warLocationText.setText("");
	       	warLocationButton.setEnabled(false);
         }
	}
	
	public boolean getCanFinish() {
		
		        String opEjie = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE);
		  	    
				if ((getServiceCheck() || getControllerCheck()) &&  opEjie.equals("true")){
					return false;
				}else if (getDaoCheck() || getDataModelCheck() || getServiceCheck() || getControllerCheck()){
					return true;
				}else{
					return false;
				}
					
			
			
		}
	public boolean canFlipToNextPage() {
		
		String opEjie = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE);
		
		if ((getServiceCheck() || getControllerCheck()) && opEjie.equals("true")){
			return true;
		}else{
			return false;
		}
	}
	public boolean canFinish() {
			if (!getServiceCheck() && !getControllerCheck()){
				return true;
			}else{
				return false;
			}
	
	}
	public IWizardPage getNextPage() {
		if ( getServiceCheck() && getEarClassesLocationText().equals("")) {
			setMessage("Se debe seleccionar algun proyecto tipo EARClasses para la capa de negocio", IMessageProvider.ERROR);
			return getWizard().getContainer().getCurrentPage();
		}else if(getControllerCheck() && getWarLocationText().equals("")) {
			setMessage("Se debe seleccionar algun proyecto tipo WAR para la capa de presentación", IMessageProvider.ERROR);
			return getWizard().getContainer().getCurrentPage();
		//Validar misma tecnología
		} else {
			// Validación de tecnologias JPA 2.0 o Spring JDBC en los proyectos seleccionados
			final IProject projectEARClasses = getEARClassesProject();
			final IProject projectWar = getWarProject();
			boolean isJPAEARClasses = (projectEARClasses != null)?GenerateCodeWizard.isJPAProjectEARClasses(projectEARClasses):false;
			boolean isJPAWar = (projectWar!= null)?GenerateCodeWizard.isJPAProjectWar(projectWar):false;;
			if (projectEARClasses!=null && projectWar!=null && (isJPAEARClasses != isJPAWar)){
				setMessage("Los dos proyectos seleccionados no tienen la misma tecnologia", IMessageProvider.ERROR);
				return getWizard().getContainer().getCurrentPage();
			} else{
				setMessage("Este Wizard genera el código fuente para desplegar una aplicación UDA", IMessageProvider.NONE);
				return super.getNextPage();
			}
		}
	}
	public boolean getConfirmation(){
	    	// miramos los checks, y si existen las carpetas
		boolean askConfirmation= false;
		String packageName = "";
		if (getEARClassesProject()!=null){
			packageName= "com.ejie."+ Utilities.getAppName(getEARClassesProject().getName());
		}else{
			packageName="com.ejie."+ Utilities.getAppName(getWarProject().getName());
		}
		
		if (!askConfirmation && getDaoCheck() && findDirectory(new File(getEARClassesProject().getLocation().toString()+"\\src\\" + packageName.replace(".", "\\")+"\\dao" ))){
		//getControllerCheck() && getDaoCheck() && getDataModelCheck() && getServiceCheck()
			askConfirmation = true;
		}
		if (!askConfirmation && getDataModelCheck() && findDirectory(new File(getEARClassesProject().getLocation().toString()+"\\src\\"+ packageName.replace(".", "\\")+"\\model" ))){
			askConfirmation = true;
		}
		if (!askConfirmation && getServiceCheck() && findDirectory(new File(getEARClassesProject().getLocation().toString()+"\\src\\"+ packageName.replace(".", "\\")+"\\service" ))){
			askConfirmation = true;
		}
		if (!askConfirmation && getControllerCheck() && findDirectory(new File(getWarProject().getLocation().toString()+"\\src\\"+ packageName.replace(".", "\\")+"\\control" ))){
			askConfirmation = true;
		}
		if (askConfirmation){
		   boolean b = MessageDialog.openConfirm(getShell(), "Confirmación", "Al ejecutar el plugin se va a sobreescribir el código generado. ¿Desea Continuar?");
		   return b;
		}   else{
			return true;
		}
	}
	private boolean findDirectory( File directory){
	//	 List<Class> classes = new ArrayList<Class>();
	//   List<String> servicios = new ArrayList<String>();
	     if (!directory.exists()) {
	     	return false;
	     }else{
	    	 return true;
	     }
	}
}