package com.ejie.uda.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.ejie.uda.operations.ProjectWorker;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;
/**
 *  Clase la cual define la pantalla del asistente "Generar código para EJB Servidor"
 */
public class GenerateSkeletonWizardPage extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private Text ejbNameText;
	private Text ejbName;
	private IProject projectEJB;
	private Text serviceNameText;
	private String packageName;
	private Text jndiNameText;
	private Button button;
	private String projectEARClassesLocation;
	private String projectEARLocation;
	private String workspacePath;
	private String path;
	private ArrayList<String> jarService= new ArrayList<String>();
	
	
	
	
	/**
	 * Primera ventana del Wizard de Plugin, donde se selecciona
	 * la opción de generar una aplicación
	 * @param selection
	 */
	public GenerateSkeletonWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Generar EJB");
		setDescription("Este Wizard el EJB de un servicio existente");
		
	}

	/**
	 * Creación de controles de la ventana
	 * @param parent - controlador padre
	 */
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 3;
		
		// Campo texto de nombre del EJB
		Label labelProjectEJB = new Label(container, SWT.NULL);
		labelProjectEJB.setText("Proyecto EJB contenedor:");
		labelProjectEJB.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));
		ejbNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		ejbNameText.setEnabled(false);
		ejbNameText.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,2,1));
		Button buttonAux = new Button(container, SWT.NONE);
		buttonAux.setText("&Buscar Proyecto");
		buttonAux.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					setProjectEJB(handleBrowseEJBProject());
					if (getProjectEJB() != null &&  !getProjectEJB().equals("")) {
						ejbNameText.setText(getProjectEJB().getName());
						setServiceNameText("");
						String app = getAppName(getProjectEJB());
						setPath(getProjectEJB().getParent().getLocation()+"\\"+ app +"EARClasses\\src\\com\\ejie\\" + app +"\\service\\");
						setPackageName("com.ejie." + app +".service");
						button.setEnabled(true);
						setProjectEARClassesLocation(getProjectEJB());
						setProjectEARLocation(getProjectEJB());
					}else{
						serviceNameText.setText("");
						button.setEnabled(false);
					}
					break;
				}
			}
		});
		buttonAux.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));
	

		
		// Campo nombre servicio
		Label labelClass = new Label(container, SWT.NULL);
		labelClass.setText("Servicio:");
		labelClass.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));  
		serviceNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		serviceNameText.setEnabled(false);
		//Button button = new Button(container, SWT.NONE);
		button=new Button(container, SWT.NONE);
		button.setText("Buscar Servicio");
		button.setEnabled(false);
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					setServiceNameText(handleBrowseService());
					if (getServiceNameText() != null) {
						serviceNameText.setText(getServiceNameText().toString());
						ejbName.setText(getServiceNameOnlyText()+"Skeleton");
					}
					break;
				}
			}

			
		});
		
     	
		serviceNameText.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,2,1));
		button.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));
	
		
		// Campo nombre jndi
		Label labelJndi = new Label(container, SWT.NULL);
		labelJndi.setText("Nombre JNDI:");
		labelJndi.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));  
		jndiNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		jndiNameText.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,2,1));
		jndiNameText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
				/*
				 * Valida el contenido del campo 
				 */
				if (jndiNameText.getText().length() > 0 && !Utilities.validateJNDIText(jndiNameText.getText())) {
					setMessage("Caracteres no válidos para en campo 'Nombre JNDI'", IMessageProvider.ERROR);
				}else{
					setMessage("Este Wizard genera el skeleton de un servicio");
				}
			}
		});
		// Salto de línea
		Label hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, false, false,1,1));
		Label labelNombreEJB = new Label(container, SWT.NULL);
		labelNombreEJB.setText("Nombre del EJB:");
		labelNombreEJB.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, false, false,1,1));
		ejbName = new Text(container, SWT.BORDER | SWT.SINGLE);
		ejbName.setEnabled(false);
		ejbName.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,2,1));
	
		
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
		try {
			setJarService(findClasses(new File(getPath()),getPackageName()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    private static List<String> findClasses(File directory, String packageName) throws ClassNotFoundException
	{
        List<Class> classes = new ArrayList<Class>();
        List<String> servicios = new ArrayList<String>();
        if (!directory.exists()) {

        	return servicios;
        }
     
        File[] files = directory.listFiles();
        for (File file : files) {
        	String fileName = file.getName();
            if (fileName.endsWith(".java") && !fileName.contains("$") && fileName.contains("Service") && !fileName.endsWith("Impl.java")) {
            	String name = "" ;
            	Class _class;
				try {		
					name = packageName + '.' + fileName.substring(0, fileName.length() - 5);
				} catch (Exception e) {
					e.getMessage();
			
				}
			
				servicios.add(name);
            }
        }
 
        return servicios;
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
		List listaEar=Arrays.asList(projectsEARClasses);
		Iterator itEarClasses=listaEar.iterator();
		String nombreApp = getAppName(ejbProy)+ Constants.EARCLASSES_NAME;
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
	
	private String getAppName(IProject project){
		String app="";
		boolean found=false;
		int contCaracter=0;
		String proyectName =project.getName();
	
		while (!found){
			contCaracter=contCaracter+1;
			String  aux = proyectName.substring(contCaracter-1,contCaracter);
			if (aux.equals("A")||aux.equals("B")||aux.equals("C")||
					aux.equals("D")||aux.equals("E")||aux.equals("F")||
					aux.equals("G")||aux.equals("H")||aux.equals("I")||
					aux.equals("J")||aux.equals("K")||aux.equals("L")||
					aux.equals("M")||aux.equals("N")||aux.equals("Ñ")||
					aux.equals("O")||aux.equals("P")||aux.equals("Q")||
					aux.equals("R")||aux.equals("S")||aux.equals("T")||
					aux.equals("U")||aux.equals("V")||aux.equals("W")||
					aux.equals("X")||aux.equals("Y")||aux.equals("Z")){
			found = true;
			}	
		}
		if (found){
			app= proyectName.substring(0,contCaracter-1);
		}
		  
	      return app;
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
	public String getProjectEARClassesLocation(){
		return projectEARClassesLocation;
	}
	public void setProjectEARClassesLocation(IProject project){
		
		this.projectEARClassesLocation = getWorkspace(project)+"\\"+getAppName(getProjectEJB())+Constants.EARCLASSES_NAME;
		
	}
	public String getProjectEARLocation(){
		return projectEARLocation;
	}
	public void setProjectEARLocation(IProject project){
		
		this.projectEARLocation =getWorkspace(project)+"\\"+getAppName(getProjectEJB())+Constants.EAR_NAME;
		
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
	public void setWorkspacePath(String locationFinal){
		  this.workspacePath=locationFinal;
		}
	public boolean getConfirmation(){
		if (findDirectory(new File(getProjectEJB().getLocation()+"\\ejbModule\\com\\ejie\\" +  getAppName(getProjectEJB())+"\\remoting"))){
		   		boolean b = MessageDialog.openConfirm(getShell(), "Confirmación", "Al ejecutar el plugin se va a sobreescribir el código generado. ¿Desea Continuar?");
		   		return b;
		}else{
		   		try {
			ProjectWorker.createGetFolderPath(getProjectEJB(), "ejbModule/com");
			ProjectWorker.createGetFolderPath(getProjectEJB(), "ejbModule/com/ejie/");
			String nombreApp= getAppName(getProjectEJB());
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
		 List<Class> classes = new ArrayList<Class>();
	     List<String> servicios = new ArrayList<String>();
	     if (!directory.exists()) {
	     	return false;
	     }else{
	    	 return true;
	     }
	  
	    
	}
}