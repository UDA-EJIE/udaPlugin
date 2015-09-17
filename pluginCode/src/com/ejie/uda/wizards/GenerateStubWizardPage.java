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
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jst.j2ee.project.EarUtilities;
import org.eclipse.swt.SWT;
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

import com.ejie.uda.Activator;
import com.ejie.uda.operations.ProjectWorker;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;
/**
 * Clase la cual define la pantalla del asistente "Generar código para EJB Cliente"
 */
public class GenerateStubWizardPage extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private Text ejbNameText;
	private String ejbName;
	private IProject projectEJB;
	private Text serviceNameText;
	private String packageName;
	private Text jndiNameText;
	private Button button;
	private String projectConfigLocation;
	private String projectEARLocation;
	private String projectEARClassesLocation;
	private String workspacePath;
	private String path;
	private ArrayList<String> jarService= new ArrayList<String>();
	private Button radEJBUda;
	private Button radEJBGeremua;
	private Group businessGroup;
	private Text weblogicIp;
	private Text portIp;
	private Group remoteGroup;
	private Text nameServer;
	private Text portServer;
	private Text userServer;
	private Text pwdServer;
	private Text ipServer;
	
	
	
	
	
	
	
	/**
	 * Primera ventana del Wizard de Plugin, donde se selecciona
	 * la opción de generar stub
	 * @param selection
	 */
	public GenerateStubWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Generar EJB Cliente");
		setDescription("Este Wizard el EJB Cliente de un servicio existente");
		
	}

	/**
	 * Creación de controles de la ventana
	 * @param parent - controlador padre
	 */
	public void createControl(Composite parent) {
		
	
		
		final Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		Group padre=new Group(container, SWT.NONE);
		padre.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true));
		padre.setLayout(new GridLayout(4, false));
	    
	  
		// Campo texto de nombre del EJB
		Label labelProjectEJB = new Label(padre, SWT.NULL);
		labelProjectEJB.setText("Proyecto EJB contenedor:");
		labelProjectEJB.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));
		ejbNameText = new Text(padre, SWT.BORDER | SWT.SINGLE);
		ejbNameText.setEnabled(false);
		ejbNameText.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,2,1));
		Button buttonAux = new Button(padre, SWT.NONE);
		buttonAux.setText("&Buscar Proyecto");
		buttonAux.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					setProjectEJB(handleBrowseEJBProject());
					if (getProjectEJB() != null &&  !getProjectEJB().equals("")) {
						ejbNameText.setText(getProjectEJB().getName());
						setServiceNameText("");
							
						button.setEnabled(true);
						setProjectConfigLocation(getProjectEJB());
						setProjectEARLocation(getProjectEJB());
						setProjectEARClassesLocation(getProjectEJB());
					}else{
						serviceNameText.setText("");
						button.setEnabled(false);
					}
					break;
				}
			}
		});
		buttonAux.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));
	

		// Radio de seguridad
		Label labelSecurity = new Label(padre, SWT.NULL);
		labelSecurity.setText("Tipo de EJB Remoto:");
		labelSecurity.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));

		//XLNets
		radEJBUda = new Button(padre, SWT.RADIO);
		 if (Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE).equals("true")){
			 radEJBUda.setText("EJB 3.0 (UDA)");
		}else{
			radEJBUda.setText("EJB 3.0");
			
		}
		radEJBUda.setSelection(true);
		radEJBUda.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radEJBUda.getSelection()){
                	serviceNameText.setText("");
                	setEjbName("");
                }
            }
        });
		radEJBUda.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,1,1));
		
		// Salto de línea
		Label hiddenLabel2= new Label(padre, SWT.NULL);
		hiddenLabel2.setLayoutData(new GridData (SWT.CENTER, SWT.CENTER, false, false,2,1));
		
		// Salto de línea
		Label hiddenLabel3= new Label(padre, SWT.NULL);
		hiddenLabel3.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));
	
		//No XLNets
		radEJBGeremua = new Button(padre, SWT.RADIO);
		
		 if (Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE).equals("true")){
			 radEJBGeremua.setText("EJB 2.0 (Geremua)");
		}else{
			 radEJBGeremua.setText("EJB 2.0");
		}
			radEJBGeremua.setSelection(false);
		radEJBGeremua.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radEJBGeremua.getSelection()){
                	serviceNameText.setText("");
                	setEjbName("");
                }
            }
        });
		radEJBGeremua.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,1,1));	
		
		// Salto de línea
		Label hiddenLabel4= new Label(padre, SWT.NULL);
	    hiddenLabel4.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,2,1));
	
		// Campo nombre servicio
		Label labelClass = new Label(padre, SWT.NULL);
		labelClass.setText("Interface del EJB Remoto:");
		labelClass.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));  
		serviceNameText = new Text(padre, SWT.BORDER | SWT.SINGLE);
		serviceNameText.setEnabled(false);
		button=new Button(padre, SWT.NONE);
		button.setText("Buscar Interface");
		button.setEnabled(false);
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					setServiceNameText(handleBrowseService());
					if (getServiceNameText() != null) {
						serviceNameText.setText(getServiceNameText().toString());
						if (getRadEJBUda()){
							setEjbName(getServiceNameOnlyText().replace(GenerateStubWizard.udaEjb, "")+"Stub");
						}else{
							setEjbName(getServiceNameOnlyText().replace(GenerateStubWizard.geremuaEjb, "")+"Stub");
						}
						setPackageName("com.ejie." +  Utilities.getAppName(getProjectEJB().getName())+".remoting");
					}
					break;
				}
			}

			
		});
				
		// Salto de línea
		Label hiddenLabelSalt= new Label(container, SWT.NULL);
		hiddenLabelSalt.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, false, false,4,1) );
		
		
		businessGroup = new Group(container, SWT.NONE);
	    businessGroup.setText("Parámetros Servidor Despliegue");
	    businessGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true));
	    businessGroup.setLayout(new GridLayout(4, false));
	    
	  	Label labelWeblogicIp = new Label(businessGroup, SWT.NULL);
	  	labelWeblogicIp.setText("IP Servidor:");
	  	labelWeblogicIp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));
		
	 // Campo nombre Servidor Ip
		weblogicIp = new Text(businessGroup, SWT.BORDER | SWT.SINGLE);
		weblogicIp.setText("127.0.0.1");
     	weblogicIp.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
				if (!Utilities.isBlank(getWeblogicIp())){
					if (Utilities.validateNumber(getWeblogicIp().replace(".", "")) && !Utilities.validateIPAdderess(getWeblogicIp())){
						setMessage("IP no válida para en campo 'IP Servidor Despliegue'", IMessageProvider.ERROR);
						
					}else if (!Utilities.validateHostName(getWeblogicIp())){
						setMessage("Nombre no válidos para en campo 'IP Servidor Despliegue'", IMessageProvider.ERROR);
						
					}
				}
				
			}
		});
		weblogicIp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));
		
		Label labelIpWeb = new Label(businessGroup, SWT.NULL);
		labelIpWeb.setText("Puerto:");
		labelIpWeb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,1,1));
		
	 // Campo nombre Servidor Ip
		portIp = new Text(businessGroup, SWT.BORDER | SWT.SINGLE);
		portIp.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,1,1));
		portIp.setText("7001");
		portIp.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
			
				if (portIp.getText().length() > 4 || !Utilities.validateNumber(portIp.getText())) {
					setMessage("Caracteres no válidos para en campo 'Puerto servidor despliegue'", IMessageProvider.ERROR);
				}else{
					setMessage("Este Wizard genera el Stub de un servicio");
				}
			}
		});
		portIp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));
		
		// Salto de línea
		Label hiddenLabelSaltos= new Label(container, SWT.NULL);
		hiddenLabelSaltos.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, false, false,4,1) );
		
		remoteGroup = new Group(container, SWT.NONE);
		remoteGroup.setText("Parámetros EJB remoto");
		remoteGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true));
		remoteGroup.setLayout(new GridLayout(4, false));
	   
		
		Label labelRemoteGroup = new Label(remoteGroup, SWT.NULL);
		labelRemoteGroup.setText("Nombre Servidor EJB:");
		labelRemoteGroup.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));
		
		 // Campo nombre Servidor Ip
		nameServer = new Text(remoteGroup, SWT.BORDER | SWT.SINGLE);
		nameServer.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,1,1));
		
		Label labelRemoteGroupInvi = new Label(remoteGroup, SWT.NULL);
		labelRemoteGroupInvi.setVisible(false);
		labelRemoteGroupInvi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,2,1));
		
		
		Label labelDirection = new Label(remoteGroup, SWT.NULL);
		labelDirection.setText("Direccion IP:");
		labelDirection.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));
		 // Campo nombre Servidor Ip
		ipServer = new Text(remoteGroup, SWT.BORDER | SWT.SINGLE);
		ipServer.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
			
				if (!Utilities.isBlank(getIpServer())){
					if (Utilities.validateNumber(getIpServer().replace(".", "")) && !Utilities.validateIPAdderess(getIpServer())){
						setMessage("IP no válida para en campo 'IP Servidor'", IMessageProvider.ERROR);
						
					}else if (!Utilities.validateHostName(getIpServer())){
						setMessage("Nombre no válidos para en campo 'IP Servidor'", IMessageProvider.ERROR);
						
					}
				}
				
			}
		});
		ipServer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));
		
		
		
		Label labelPort = new Label(remoteGroup, SWT.NULL);
		labelPort.setText("Puerto:");
		labelPort.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));
		
		portServer = new Text(remoteGroup, SWT.BORDER | SWT.SINGLE);
		portServer.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
			
				if (portServer.getText().length() > 4 || !Utilities.validateNumber(portServer.getText())) {
					setMessage("Caracteres no válidos para en campo 'Puerto Servidor EJB'", IMessageProvider.ERROR);
				}else{
					setMessage("Este Wizard genera el Stub de un servicio");
				}
			}
		});
		portServer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));
		
		
		Label labelUser = new Label(remoteGroup, SWT.NULL);
		labelUser.setText("Usuario:");
		labelUser.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));
		
		
		userServer = new Text(remoteGroup, SWT.BORDER | SWT.SINGLE);
			
		userServer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));
		
		Label labelPwd = new Label(remoteGroup, SWT.NULL);
		labelPwd.setText("Password:");
		labelPwd.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));

		pwdServer = new Text(remoteGroup, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		pwdServer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1));
		
		serviceNameText.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,2,1));
		button.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));
	
		
		// Campo nombre jndi
		Label labelJndi = new Label(remoteGroup, SWT.NULL);
		labelJndi.setText("Nombre JNDI:");
		labelJndi.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));  
		jndiNameText = new Text(remoteGroup, SWT.BORDER | SWT.SINGLE);
		jndiNameText.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,2,1));
		jndiNameText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
				/*
				 * Valida el contenido del campo 
				 */
				if (jndiNameText.getText().length() > 0 && !Utilities.validateJNDIText(jndiNameText.getText())) {
					setMessage("Caracteres no válidos para en campo 'Nombre JNDI'", IMessageProvider.ERROR);
				}else{
					setMessage("Este Wizard genera el stub de un servicio");
				}
			}
		});
		setControl(container);
	}
	
	/*************/
	/*  Getters  */
	/*************/
	
	public String getEjbNameText() {
		if (ejbNameText != null) {
			return ejbNameText.getText();
		} else {
			return "";
		}
	}
	public String getEjbPath() {
		if (getProjectEJB() != null) {
			String proyecto= getProjectEJB().getLocation().toString();
			String locationFinal=proyecto.replace("/","\\");
			return locationFinal;
		} else {
			return "";
		}
	}
	
	public IProject getProjectEJB(){
		return this.projectEJB;
	}
	
	public void setProjectEJB(IProject project){
		this.projectEJB = project;
		
	}
	
	public String getEjbCodName(){
		String codApp = getEjbNameText();
		
		if (!Utilities.isBlank(codApp) && codApp.endsWith(Constants.EJB_NAME)){
			codApp = codApp.substring(0, codApp.length() - Constants.EJB_NAME.length());
		}
		
		return codApp;
	}
	
	
	
	/**********************/
	/*  Métodos privados  */
	/**********************/
	
	private String handleBrowseService() {
		List<String> libraries=new ArrayList<String>();
		List<String> librerias = Utilities.findFileEndsLike(new File( getProjectEARLocation() + Constants.PREF_DEFAULT_EAR_LIBS),"Remoting");
		Iterator<?> itLib = librerias.iterator();
		while (itLib.hasNext()){
			String nombreLib= (String) itLib.next();
			String ruta =  getProjectEARLocation() + Constants.PREF_DEFAULT_EAR_LIBS+"\\" +nombreLib;
			libraries.add(ruta);
		}	
		setJarService(getClassesFromJars(libraries,getRadEJBUda()));
		if (getJarService().size()==0){
			MessageDialog.openInformation(getShell(), "Información", "No se ha encontrado ningún Interface de EJB Remoto" );
			return null;
		}
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(),  new LabelProvider());
		dialog.setTitle("Services");
		dialog.setMessage("Service que se desea vincular al proyecto EJB");
		dialog.setElements(getJarService().toArray(new String[getJarService().size()]));

		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				String resultado = ((String) result[0]);
					return resultado;	
			}
		}
		return null;
	}
 
	private IProject handleBrowseEJBProject() {

		IProject[] projectsEAR;

		projectsEAR = EarUtilities.getAllProjectsInWorkspaceOfType("jst.ejb");
		
		ILabelProvider labelProvider = new  JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Proyectos EJB");
		dialog.setMessage("Proyectos EJB al que se desea vincular");
		dialog.setElements(projectsEAR);

		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				IProject project = ((IProject) result[0]);
				if (isProjectEJBEjie(project)){
					
					return project;	
				}
			}
		}
		return null;
	}
	public IProject getEarClassesProject(IProject ejbProy) {

		IProject[] projectsEARClasses;

		projectsEARClasses = EarUtilities.getAllProjectsInWorkspaceOfType("jst.java");
		List<?> listaEar=Arrays.asList(projectsEARClasses);
		Iterator<?> itEarClasses=listaEar.iterator();
		String nombreApp = Utilities.getAppName(ejbProy.getName())+ Constants.EARCLASSES_NAME;
		while (itEarClasses.hasNext()){
			IProject earClass= (IProject) itEarClasses.next();
			if( nombreApp.equals(earClass.getName())){
				return earClass;
			}
		}
		return null;
	}
	
	
	
	private boolean isProjectEJBEjie(IProject project){
		
		if (project != null){
			String nameProject = project.getName();
			
			if (!Utilities.isBlank(nameProject) && nameProject.endsWith(Constants.EJB_NAME)){
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<String> getJarService(){
		return this.jarService;
	}
	public void setJarService( List<String>  list){
		this.jarService=(ArrayList<String>) list;
	}
	public String getServiceNameText() {
		if (!serviceNameText.getText().equals("") && serviceNameText.getText()!=null){
			return serviceNameText.getText();
		}else{
			return "";
		}
	}
	
	public String getServiceNameOnlyText() {
		if (!getServiceNameText().equals("") && getServiceNameText()!=null){
			String texto = getServiceNameText();
			return texto.substring(texto.lastIndexOf(".")+1,texto.length());
		}else{
			return "";
		}
	}

	public void setServiceNameText(String serviceNameText) {
		this.serviceNameText.setText( serviceNameText);
	}
	public String getPackageName(){
		return this.packageName;
	}
	public void setPackageName(String packageName){
		this.packageName = packageName;
	}
	public String getPath(){
		return this.path;
	}
	public void setPath(String path){
		this.path = path;
	}
	public String getJndiNameText() {
		if (!jndiNameText.getText().equals("") && jndiNameText.getText()!=null){
			return jndiNameText.getText();
		}else{
			return "";
		}
	}

	public void setJndiNameText(String jndiNameText) {
		this.jndiNameText.setText( jndiNameText);
	}
	public String getProjectConfigLocation(){
		return projectConfigLocation;
	}
	public void setProjectConfigLocation(IProject project){
		this.projectConfigLocation = Constants.UNIDAD_HD + Constants.PATH_CONFIG+Utilities.getAppName(getProjectEJB().getName());
		
	}
	public String getProjectEARLocation(){
		return projectEARLocation;
	}
	public void setProjectEARLocation(IProject project){
		this.projectEARLocation =getWorkspace(project)+"\\"+Utilities.getAppName(getProjectEJB().getName())+Constants.EAR_NAME;
	}
	public String getProjectEARClassesLocation(){
		return projectEARClassesLocation;
	}
	public void setProjectEARClassesLocation(IProject project){
		this.projectEARClassesLocation =getWorkspace(project)+"\\"+Utilities.getAppName(getProjectEJB().getName())+Constants.EARCLASSES_NAME;
		
	}
	public String getWorkspace(IProject project){
		String location= project.getParent().getLocation().toString();
		String locationFinal=location.replace("/","\\");
		setWorkspacePath(locationFinal);
		return locationFinal;
	
	}
	public String getWorkspacePath(){
	  return workspacePath;
	}
	/**
	 * Recupera el valor del radio de EJBUda
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getRadEJBUda() {
		return radEJBUda.getSelection();
	}
	/**
	 * Recupera el valor del radio de EJBGeremua
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getRadEJBGeremua() {
		return radEJBGeremua.getSelection();
	}
	public String getWeblogicIp() {
		return weblogicIp.getText();
	}
public void setEjbName(String valor){
	this.ejbName=valor;
}
public String getEjbName(){
	return ejbName;
}
	public String getPortIp() {
		return portIp.getText();
	}
	public String getNameServer() {
		return nameServer.getText();
	}

	public String getPortServer() {
		return portServer.getText();
	}

	public String getUserServer() {
		return userServer.getText();
	}

	public String getPwdServer() {
		return pwdServer.getText();
	}

	public String getIpServer() {
		return ipServer.getText();
	}

	public void setWorkspacePath(String locationFinal){
		  this.workspacePath=locationFinal;
		}
	public static List<String> getClasseNamesInPackage
    (String jarName, boolean isEJBThree ){
  ArrayList<String> classes = new ArrayList<String> ();

  try{
    JarInputStream jarFile = new JarInputStream
       (new FileInputStream (jarName));
    JarEntry jarEntry;

    while(true) {
      jarEntry=jarFile.getNextJarEntry ();
      if(jarEntry == null){
        break;	
      }
      if (isEJBThree){
    	  if((jarEntry.getName ().endsWith (GenerateStubWizard.udaEjb+".class")) ) {
  	        classes.add (jarEntry.getName().replaceAll("/", "\\.").substring(0,jarEntry.getName().replaceAll("/", "\\.").length()-6));
  	      }
      }else{
	      if((jarEntry.getName ().endsWith (GenerateStubWizard.geremuaEjb+".class")) ) {
	        classes.add (jarEntry.getName().replaceAll("/", "\\.").substring(0,jarEntry.getName().replaceAll("/", "\\.").length()-6));
	      }
      }   
    }
  }
  catch( Exception e){
    e.printStackTrace ();
  }
  return classes;
}
	private List<String> getClassesFromJars(List<String> jarlist,boolean isEJBThree){
		List<String> auxiliar=new ArrayList<String>();
		Iterator<String> itjars=jarlist.iterator();
		while (itjars.hasNext()){
			String nombreJar= itjars.next();
			List<String> lista =getClasseNamesInPackage(nombreJar,isEJBThree);
			Iterator<String> itLista = lista.iterator();
			while (itLista.hasNext()){
				String clase= itLista.next();
				auxiliar.add(clase);
			}
		}
		return auxiliar;
	}
	public boolean getConfirmation(){
		if (findDirectory(new File(getProjectEJB().getLocation()+"\\ejbModule\\com\\ejie\\" +  Utilities.getAppName(getProjectEJB().getName())+"\\remoting"))){
		   		boolean b = MessageDialog.openConfirm(getShell(), "Confirmación", "Al ejecutar el plugin se va a sobreescribir el código generado. ¿Desea Continuar?");
		   		return b;
		}else{
		   		try {
			ProjectWorker.createGetFolderPath(getProjectEJB(), "ejbModule/com");
			ProjectWorker.createGetFolderPath(getProjectEJB(), "ejbModule/com/ejie/");
			String nombreApp= Utilities.getAppName(getProjectEJB().getName());
			ProjectWorker.createGetFolderPath(getProjectEJB(), "ejbModule/com/ejie/"+nombreApp); 
			ProjectWorker.createGetFolderPath(getProjectEJB(), "ejbModule/com/ejie/"+nombreApp+"/remoting");
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}  
		return false;	 
	}
	private boolean findDirectory( File directory){
		 //List<Class> classes = new ArrayList<Class>();
	     //List<String> servicios = new ArrayList<String>();
	     if (!directory.exists()) {
	     	return false;
	     }else{
	    	 return true;
	     }
	  
	    
	}
	
}