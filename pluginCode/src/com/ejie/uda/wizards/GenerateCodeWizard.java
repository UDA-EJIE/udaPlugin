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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.hibernate.cfg.JDBCMetaDataConfiguration;

import com.ejie.uda.Activator;
import com.ejie.uda.operations.GenerateCodeWorker;
import com.ejie.uda.operations.ProjectWorker;
import com.ejie.uda.operations.PropertiesWorker;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.TreeNode;
import com.ejie.uda.utils.Utilities;

/**
 * Clase encargada de generar todo lo necesario para el asistente "Generar código de negocio y control"
 * 
 */
public class GenerateCodeWizard extends Wizard implements INewWizard {

	private GenerateCodeWizardPageOne page;
	private GenerateCodeWizardPageTwo pageTwo;
	private GenerateCodeWizardPageThree pageThree;
	private GenerateCodeWizardPageFour pageFour;	
	private ISelection selection;
	private String errorMessage;
	private String summary;
	// Logs en la consola
	private ConsoleLogger consola = new ConsoleLogger(Constants.CONSOLE_NAME);
	// Fichero de propiedades
	private PropertiesWorker udaProperties;
	
	/**
	 * Constructor
	 */
	public GenerateCodeWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Método de acceso desde otra página a la página Two
	 * @return pagina Two
	 */
	public GenerateCodeWizardPageTwo getPageGenerateCodeWizardPageTwo(){
		return this.pageTwo;
	}
	
	/**
	 * Método de configuración de variable locales para la información de contexto
	 * 
	 * @param workbench - workbench de eclipse
	 * @param selection - selecction de eclipse 
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		
		//Inicializa el properties de la aplicación
		String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		workspacePath += "/.metadata/.plugins/com.ejie.uda";
		udaProperties = new PropertiesWorker("uda.properties", workspacePath);
	}

	/**
	 * Crea una instancia de nueva ventana del wizard
	 */
	public void addPages() {
		page = new GenerateCodeWizardPageOne(selection);
		pageTwo = new GenerateCodeWizardPageTwo(selection);
		pageThree = new GenerateCodeWizardPageThree(selection);
		pageFour = new GenerateCodeWizardPageFour(selection);
		addPage(page);
		addPage(pageTwo);
		addPage(pageThree);
		addPage(pageFour);
	}
	
	/**
	 * Deshabilita el boton finish cuando está en algunas pantallas
	 */
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == page || getContainer().getCurrentPage() == pageTwo ) {
			return false;
		}else if (getContainer().getCurrentPage() == pageThree){
			boolean resultado =  pageThree.getCanFinish();
		    return resultado	;
		    
		} else {
			return true;
		}
	}

	/**
	 * Recupera los datos de la ventana e inicia el tratamiento del plugin
	 * 
	 * @return true si la ejecución es correcta, false ecc.
	 */
	public boolean performFinish() {
		// Recupera la información de la ventana
		final ConnectionData conData = page.getConnectionData();
		final List<TreeNode> filterSchema = pageTwo.generateFilterReveng();
		final IProject projectEARClasses = pageThree.getEARClassesProject();
		final IProject projectWar = pageThree.getWarProject();
		final boolean dataModelCheck = pageThree.getDataModelCheck();
		final boolean daoCheck = pageThree.getDaoCheck();
		final boolean serviceCheck = pageThree.getServiceCheck();
		final boolean controllerCheck = pageThree.getControllerCheck();
		final boolean radAnot = pageThree.getRadAnot();
		final boolean chkXLNets  ;
		final String  chkIdXlNets ;

		
		String pathTemplates = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH);
		
		//Validación de configuración de plantillas
		if (Utilities.isBlank(pathTemplates)) {
			if (Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE).equals("true")){
				pageFour.setMessage("No está configurada la ruta de las plantillas en Window > Preferences > UDA", IMessageProvider.ERROR);
			}else{
				pageThree.setMessage("No está configurada la ruta de las plantillas en Window > Preferences > UDA", IMessageProvider.ERROR);				
			}
			return false;
		}else{
			// Verifica que es correcta la ruta que se ha indicado en las preferencias de UDA
			File subTemplates = new File(pathTemplates + Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EAR);
			if (!subTemplates.exists()){
				if (Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE).equals("true")){
					pageFour.setMessage("La ruta configurada en Windows > Preferences > UDA no es válida", IMessageProvider.ERROR);
				}else{
					pageThree.setMessage("La ruta configurada en Windows > Preferences > UDA no es válida", IMessageProvider.ERROR);
				}
				return false;
			}
		}
		
		// Validación de campo obligatorio y su contenido
		if (!dataModelCheck && !daoCheck && !serviceCheck && !controllerCheck) {
			pageThree.setMessage("No hay ninguna opción seleccionada para las capas", IMessageProvider.ERROR);
			return false; 
		}
		
		if ((dataModelCheck || daoCheck || serviceCheck) && projectEARClasses == null) {
			pageThree.setMessage("Se debe seleccionar algun proyecto tipo EARClasses para la capa de negocio", IMessageProvider.ERROR);
			return false;
		}
		
		if (controllerCheck && projectWar == null) {
			pageThree.setMessage("Se debe seleccionar algun proyecto tipo WAR para la capa de presentación", IMessageProvider.ERROR);
			return false;
		}
		//miramos si estamos en entorno de ejie o no
	    if (Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE).equals("true")){
			//Si hemos elegido XLNets se debe haber introducido el texto de role
			if (serviceCheck || controllerCheck ){
					chkXLNets = pageFour.getRadXLNets();
					chkIdXlNets = pageFour.getIdSecurityAux();
					if (chkIdXlNets.equals("") && chkXLNets){
						pageFour.setMessage("Es obligatorio introducir el Role", IMessageProvider.ERROR);
						return false;
					}
			}	else{
				chkXLNets = false;
				chkIdXlNets="";
			}	
		}else{
			chkXLNets = false;
			chkIdXlNets="";
		}			
		
		
		//isJPA
		final boolean isJPAEARClasses = (projectEARClasses != null)?isJPAProjectEARClasses(projectEARClasses):false;
		final boolean isJPAWar = (projectWar!= null)?isJPAProjectWar(projectWar):false;;
		final boolean isJPA = isJPAEARClasses || isJPAWar;
		
		// Validacion de la conexión
		if (conData == null){
			pageThree.setMessage("Conexión no válida", IMessageProvider.ERROR);
			return false;
		}

		// Guarda los valores de la conexión en un fichero de propiedades
		setConfigDatabaseProperties(conData);
		
		
		//comprobamos si quieres machacar o no
		 if (!pageThree.getConfirmation()){
			 //le ha dado que si al check de generar
			// MessageDialog.openError(getShell(), "Error", "Operación cancelada por el usuario");
			 return false;
		 }
		
		// Inicia la ejecución de proceso
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				try {
					monitor.beginTask("Añadiendo WAR a la aplicación", 2);
					// Inicia tratamiento del plugin
					
					doFinish(monitor, isJPA, projectEARClasses, projectWar, dataModelCheck, daoCheck, serviceCheck, controllerCheck,radAnot,chkXLNets,chkIdXlNets,filterSchema, conData);
				} catch (Exception e) {
					errorMessage = e.getLocalizedMessage();
					throw new CoreException(null);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
			
			pageThree.getControl().setEnabled(false);
			
			MessageDialog.openInformation(getShell(), "Información", "¡Las operaciones se han realizado con éxito!" + this.summary);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			consola = ConsoleLogger.getDefault();
			consola.println(sw.toString(), Constants.MSG_ERROR);
			MessageDialog.openError(getShell(), "Error", "Error en la generación de la aplicación: " + errorMessage);
		}
		return true;
	}

	/**
	 * Genera el código de backend en función de la selección realizada
	 * 
	 * @param monitor - monitor de progreso del plugin
	 * @param isJPA - indica si es un proyecto JPA
	 * @param projectEARClasses - proyecto EARClasses contenedor
	 * @param projectWar - proyecto WAR contenedor
	 * @param dataModelCheck - selección de generación de model
	 * @param daoCheck - selección de generación de DAO
	 * @param serviceCheck - selección de generación de service
	 * @param controllerCheck - selección de generación de controller
	 * @param annotCheck - generación capa de negocio mediante anotaciones
	 * @param annotControlCheck - generación capa de control mediante anotaciones
	 * @param chkXLNets - check seguridad XLNets
	 * @param chkIdXlNets - Is seguridad
	 * @param filterSchema - filtro de esquema de base de datos
	 * @param conData - datos de conexión de la base de datos
	 * @throws Exception
	 */
	private void doFinish(IProgressMonitor monitor, boolean isJPA, IProject projectEARClasses, IProject projectWar, 
			boolean dataModelCheck, boolean daoCheck, boolean serviceCheck, boolean controllerCheck,boolean annotCheck, 
			boolean chkXLNets, String chkIdXlNets, List<TreeNode> filterSchema, 
			ConnectionData conData) throws Exception  {
		
		consola = ConsoleLogger.getDefault();
		consola.println("UDA - INI", Constants.MSG_INFORMATION);
		
		final String pathTemplates = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH);
		

		//Recupera el listado de tablas que se incluirán y un listado de columnas que se excluirán
		String revengXML = GenerateCodeWorker.getRevengXML(filterSchema);
		
		//Lector de BD
		JDBCMetaDataConfiguration jmdc = null;

		try{
			/**
			 * EARClasses
			 */
			if (projectEARClasses != null){
				
				monitor.setTaskName("Generando código para la capa de negocio...");
				
				String pathProject =  projectEARClasses.getLocation().toString();
				String appName = Utilities.getAppName(projectEARClasses.getName()); 
				
				if (jmdc==null){
					jmdc = GenerateCodeWorker.getConfigurationReveng(conData, appName, isJPA, revengXML);
				}
				
				/**
				 * MODEL
				 */
				if (dataModelCheck){
					GenerateCodeWorker.modelExporter(jmdc, pathTemplates, pathProject, isJPA);
				}
	
				/**
				 * DAO
				 */
				if (daoCheck){
					if (!annotCheck){
						GenerateCodeWorker.daoDIExporter(jmdc, pathTemplates, pathProject,annotCheck);
					}
					GenerateCodeWorker.daoExporter(jmdc, pathTemplates, pathProject, annotCheck, isJPA);
				}
				
				/**
				 * SERVICE
				 */
				if (serviceCheck){
					GenerateCodeWorker.serviceExporter(jmdc, pathTemplates, pathProject,annotCheck, isJPA);
					if (!annotCheck){
						GenerateCodeWorker.serviceDIExporter(jmdc, pathTemplates, pathProject,annotCheck,isJPA);
					}
					String path = ProjectWorker.createGetFolderPath(projectEARClasses, "src/spring");
					Map<String, Object> context = new HashMap<String, Object>();
					if (chkIdXlNets.equals("")){
						context.put(Constants.CODROLE_PATTERN, "UDA");
						context.put("codroleAux",  "ROLE_UDA");
					}else{
						
						if (chkIdXlNets.contains("','ROLE")){
							String firstRole = chkIdXlNets.substring(1,chkIdXlNets.indexOf("',", 0));
							context.put(Constants.CODROLE_PATTERN, chkIdXlNets);
							context.put("codroleAux", firstRole);
						}	else{
							String firstRole = chkIdXlNets.substring(1,chkIdXlNets.length()-1 );
							context.put(Constants.CODROLE_PATTERN, chkIdXlNets);
							context.put("codroleAux",  firstRole);
						}
						
					}

				}
				
				if (!annotCheck){
					if (daoCheck && !serviceCheck){
						String pathTemplate = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH) 
							+ "/generateCode/dao/";
						ProjectWorker.createFileTemplate(pathTemplate, pathProject+"/src/", "beanRefContext.xml", null);
					} else if (!daoCheck && serviceCheck){
						String pathTemplate = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH) 
							+ "/generateCode/service/";
						ProjectWorker.createFileTemplate(pathTemplate, pathProject+"/src/", "beanRefContext.xml", null);
					} else if (daoCheck && serviceCheck){
						String pathTemplate = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH) 
							+ "/generateCode/";
						ProjectWorker.createFileTemplate(pathTemplate, pathProject+"/src/", "beanRefContext.xml", null);
					}
				}
				
				
				if (isJPA && (dataModelCheck || daoCheck || serviceCheck)){
				  GenerateCodeWorker.persistenceExporter(jmdc, pathTemplates, pathProject);
		    	}
				
				monitor.worked(1);
				consola.println("Código generado para la capa de negocio:", Constants.MSG_INFORMATION);
				if (dataModelCheck){
					consola.println("\tModelos -> "+projectEARClasses.getName()+"/com/ejie/" + appName + "/model", Constants.MSG_INFORMATION);	
				}
				
				if (daoCheck){
					consola.println("\tDAOs -> "+projectEARClasses.getName()+"/com/ejie/" + appName + "/dao", Constants.MSG_INFORMATION);
				}
				if (serviceCheck){
					consola.println("\tServicios -> "+projectEARClasses.getName()+"/com/ejie/" + appName + "/service", Constants.MSG_INFORMATION);
					if (chkIdXlNets.equals("")){
						consola.println("\tsecurity-config.xml -> "+projectEARClasses.getName()+"/src/spring", Constants.MSG_INFORMATION);
					}
				}	
				
				// Actualiza el proyecto al finalizar la ejecución
				ProjectWorker.refresh(projectEARClasses);
				projectEARClasses.build(IncrementalProjectBuilder.AUTO_BUILD,null);
				ProjectWorker.refresh(projectEARClasses);
			}
			
			
			/**
			 * WAR
			 */
			if (projectWar != null){
				monitor.setTaskName("Generando código para la capa de presentación...");
				String pathProject =  projectWar.getLocation().toString();
				String appName = Utilities.getAppName(projectWar.getName()); 
				
				if (jmdc==null){
					jmdc = GenerateCodeWorker.getConfigurationReveng(conData, appName, isJPA, revengXML);
				}
								
				if (controllerCheck){
					GenerateCodeWorker.controllerExporter(jmdc, pathTemplates, pathProject,annotCheck,isJPA);
					GenerateCodeWorker.jacksonExporter(jmdc, pathTemplates, pathProject,annotCheck,appName);
					
					if (!annotCheck){
						GenerateCodeWorker.controllerDIExporter(jmdc, pathTemplates, pathProject,annotCheck,appName);
						String pathTemplate = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH) 
							+ "/generateCode/controller/";
						ProjectWorker.createFileTemplate(pathTemplate, pathProject+"/WebContent/WEB-INF/spring/", "app-config.xml", null);
					}

				}
				monitor.worked(1);
				consola.println("Código generado para la capa de presentación:", Constants.MSG_INFORMATION);
				consola.println("\tControllers -> " + projectWar.getName()+"/com/ejie/" + appName + "/control", Constants.MSG_INFORMATION);
				if (chkIdXlNets.equals("")){
					consola.println("\tsecurity-config.xml -> " + projectWar.getName() + "/WebContent/WEB-INF/spring", Constants.MSG_INFORMATION);
				}
				// Actualiza el proyecto al finalizar la ejecución
				ProjectWorker.refresh(projectWar);
			}
			
			// Visualiza el sumario de tareas
			this.summary = createSummary(chkXLNets);
		}catch(Exception e){
			consola.println(e.toString(), Constants.MSG_ERROR);
			throw e;
		}		
		consola.println("UDA - END", Constants.MSG_INFORMATION);
	}

	/**
	 * Genera el texto que se sacará de sumario con las operaciones realizadas.
	 * @param context - contexto con la información del los proyectos
	 * @return - texto del sumario
	 */
	private String createSummary(boolean chkXLNets){
		StringBuffer summaryText = new StringBuffer();
		
		summaryText.append("\n\n- Generación de código para aplicación");
		summaryText.append("\n\t- Filtrado de tablas y columnas.");
		summaryText.append("\n\t- Selección de componentes de negocio y presentación.");
		if (chkXLNets && Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE).equals("true")){
			summaryText.append("\n\t- Seguridad XLNets aplicada.");
		}
		
		return summaryText.toString();
	}
	
	/**
	 * Método que indica si es un proyecto JPA o JDBC
	 * 
	 * @param project - Proyecto EARClasses
	 * @return - proyecto JPA
	 */
	public static boolean isJPAProjectEARClasses(IProject project){
		boolean isJPA = false;
		if (project != null){
			IFile persistence = project.getFile("src/META-INF/udaPersistence.xml");
			
			// Verifica si existe el fichero udaPersistence.xml
			// y busca en su contenido que tipo de tecnologia de persistencia tiene 
			if (persistence.exists()){
				isJPA = true;
			}	
		}
			
		return isJPA;
	}
	/**
	 * Método que indica si es un proyecto JPA o JDBC
	 * 
	 * @param project - Proyecto WAR
	 * @return - proyecto JPA
	 */
	public static boolean isJPAProjectWar(IProject project){
		boolean isJPA = false;
		
		if (project != null){
			// Fichero encargado de indicar que el proyecto tendrá tecnologia JPA 2.0 en el proyecto EARClasses
			String path =  project.getLocation().toString() + "/.settings/com.ejie.uda.xml";
			File jpaXml = new File(path);
			// Verifica si existe el fichero com.ejie.uda.xml
			if (jpaXml.exists()){
				isJPA = true;
			}
		}
	
		return isJPA;
	}
 
	/**
	 * Método que establecxe los parámetros de base de datos
	 * 
	 * @param conData - Parámetros de conexion
	 */
	private void setConfigDatabaseProperties(ConnectionData conData){
		
		if (conData != null && getProperties() != null){
	    	
			getProperties().writeProperty("service", conData.getService());
			getProperties().writeProperty("sid", conData.getSid());
			getProperties().writeProperty("host", conData.getHost());
			getProperties().writeProperty("portnumber", conData.getPortNumber());
			getProperties().writeProperty("username", conData.getUserName());
			getProperties().writeProperty("password", conData.getPassword());
			getProperties().writeProperty("schema", conData.getSchema());
			getProperties().writeProperty("catalog", conData.getCatalog());
			getProperties().saveProperties();//Guardar las propiedades en el fichero
		}
		
	}
	
	public PropertiesWorker getProperties(){
		return udaProperties;
	}
	


	
	
}