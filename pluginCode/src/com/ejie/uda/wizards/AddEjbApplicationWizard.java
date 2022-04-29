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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jst.j2ee.project.EarUtilities;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

import com.ejie.uda.Activator;
import com.ejie.uda.operations.ProjectWorker;
import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

/**
 * Clase encargada de generar todo lo necesario para el asistente "Añadir Proyecto EJB"
 * 
 */
public class AddEjbApplicationWizard extends Wizard implements INewWizard {

	private AddEjbApplicationWizardPage page;
	private ISelection selection;
	private String errorMessage;
	private String summary;
	// Logs en la consola
	private ConsoleLogger consola = new ConsoleLogger(Constants.CONSOLE_NAME);
	
	/**
	 * Constructor
	 */
	public AddEjbApplicationWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Método de configuración de variable locales para la información de contexto
	 * 
	 * @param workbench - workbench de eclipse
	 * @param selection - selecction de eclipse 
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/**
	 * Crea una instancia de nueva ventana del wizard
	 */
	public void addPages() {
		page = new AddEjbApplicationWizardPage(selection);
		addPage(page);
	}

	/**
	 * Recupera los datos de la ventana e inicia el tratamiento del plugin
	 * 
	 * @return true si la ejecución es correcta, false ecc.
	 */
	public boolean performFinish() {
		// Recupera la información de la ventana
		final String ejbNameText = page.getWarNameText();
		final String ejbName = page.getEJBCodName();
		final IProject projectEAR = page.getProjectEAR();
		final String idSecurity = "";
		
		String pathTemplates = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH);
		
		//Validación de configuración de plantillas
		if (Utilities.isBlank(pathTemplates)) {
			page.setMessage("No está configurada la ruta de las plantillas en Windows > Preferences > UDA",
					IMessageProvider.ERROR);
			return false;
		}else{
			// Verifica que es correcta la ruta que se ha indicado en las preferencias de UDA
			File subTemplates = new File(pathTemplates + Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EAR);
			if (!subTemplates.exists()){
				page.setMessage("La ruta configurada en Window > Preferences > UDA no es válida",
						IMessageProvider.ERROR);
				return false;
			}
		}
		
		// Validación de campo obligatorio y su contenido
		if (projectEAR == null) {
			page.setMessage("Campo 'EAR a vincular' obligatorio", IMessageProvider.ERROR);
			return false;
		}
		if (ejbNameText == null || "".equals(ejbNameText)) {
			page.setMessage("Campo 'Nombre del módulo EJB' obligatorio",
					IMessageProvider.ERROR);
			return false;
		}else if (!Utilities.validateWARText(ejbNameText)){
			page.setMessage("Caracteres no válidos para en campo 'Nombre del módulo EJB'",
					IMessageProvider.ERROR);
			return false;
		}else if(ResourcesPlugin.getWorkspace().getRoot().getProject(ejbName + Constants.EJB_NAME).exists()){
				page.setMessage("Ya existe un módulo EJB con este nombre.",
						IMessageProvider.ERROR);
				return false;
		}
		//Comprobar si hay ya un módulo EJB existente, si es así error
		/*if (existsEJB(projectEAR)){
			MessageDialog.openError(getShell(), "Error", "únicamente se permite un módulo EJB por aplicación");
			return false;
		}*/
		boolean isJPA = false;
		IFile weblogicApplication = projectEAR.getFile(Constants.PERSISTENCE_LOCALPATH);
		
		// Verifica si existe el fichero weblogic-application.xml 
		// y busca en su contenido que tipo de tecnologia de persistencia tiene 
		if (weblogicApplication.exists()){
			isJPA = isPersistenceJPA(weblogicApplication);
		}else{
			page.setMessage("No es posible identificar si el proyecto es de tipo Spring-JDBC o JPA 2.0", IMessageProvider.ERROR);
			return false;
		}
			
		final boolean radJPA = isJPA;
		final boolean radSpringJDBC = !isJPA;
		
	

		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				try {
					monitor.beginTask("Añadiendo módulo EJB a la aplicación", 2);
					// Inicia tratamiento del plugin
					doFinish(monitor, ejbName, projectEAR, radSpringJDBC, radJPA, idSecurity);
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
			
			page.getControl().setEnabled(false);
			
			MessageDialog.openInformation(getShell(), "Información", "¡Las operaciones se han realizado con éxito!" + this.summary);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Error", "Error en la generación de la aplicación: " + errorMessage);
		}
		return true;
	}

	/**
	 * Genera el proyecto EJB para una aplicacion
	 * 
	 * @param monitor - monitor de progreso del plugin
	 * @param warName - nombreWar
	 * @param projectEAR - proyecto EAR contenedor
	 * @param radSpringJDBC - radio que indica si la capa de datos será con tecnología Spring-JDBC
	 * @param radJPA - radio que indica si la capa de datos será con tecnologia JPA 2.0
	 * @param idSecurity - idSeguridad
	 * @throws Exception
	 */
	private void doFinish(IProgressMonitor monitor, String warName, IProject projectEAR, boolean radSpringJDBC,
			boolean radJPA, String idSecurity) throws Exception  {
		
		consola = ConsoleLogger.getDefault();
		consola.println("UDA - INI", Constants.MSG_INFORMATION);
		
		String appCode = getCodAppFromEAR(projectEAR);
		
		// Contexto del plugin
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(Constants.CODAPP_PATTERN, appCode.toLowerCase());
		context.put(Constants.EJB_NAME_PATTERN, warName + Constants.EJB_NAME);
		context.put(Constants.EAR_NAME_PATTERN, appCode + Constants.EAR_NAME);
		context.put(Constants.EARCLASSES_NAME_PATTERN, appCode + Constants.EARCLASSES_NAME);
		context.put(Constants.RADJPA_PATTERN, radJPA);
		context.put(Constants.PREF_EJIE_PATTERN, Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE));
		context.put(Constants.RADSPRINGJDBC_PATTERN, radSpringJDBC);
		context.put(Constants.ID_SECURITY, idSecurity);

		try{
			// Recupera el Workspace para crear los proyectos
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			
			// Crea el proyecto de xxxEJB
			monitor.setTaskName("Creando el módulo EJB...");
			IProject projectEJB = createProjectEJB(root, context, monitor);
			monitor.worked(1);
			consola.println("Módulo EJB generado.", Constants.MSG_INFORMATION);
			
			// Relaciona el proyecto EJB al EAR
			monitor.setTaskName("Enlazando el módulo EJB a la aplicación...");
			ProjectWorker.linkedReferencesProjects(projectEAR, projectEJB);
			
			ProjectWorker.linkedProjectsClasspath(getProject(getCodAppFromEAR(projectEAR)+"EARClasses"),projectEJB);
			monitor.worked(1);
			consola.println("Módulo EJB enlazado a la aplicación.", Constants.MSG_INFORMATION);
	
			// Actualiza los proyectos al finalizar la ejecución
			ProjectWorker.refresh(projectEJB);
			ProjectWorker.refresh(projectEAR);
			
			this.summary = createSummary(context);
			
		}catch(Exception e){
			consola.println(e.toString(), Constants.MSG_ERROR);
			throw e;
		}
		consola.println("UDA - END", Constants.MSG_INFORMATION);
	}

	/**
	 * Crea el proyecto EJB
	 * 
	 * @param root - workspace del eclipse
	 * @param context - contexto del plugin
	 * @return proyecto tipo xxxEAR
	 * @throws Exception
	 */
	private IProject createProjectEJB(IWorkspaceRoot root,
			 Map<String, Object> context, IProgressMonitor monitor) throws Exception {
		
		// Crea el proyecto de xxxEJB
		IProject projectEJB = null;
		try {
			projectEJB = root.getProject((String)context.get(Constants.EJB_NAME_PATTERN));
			// Verifica si crea el proyecto en el Workspace o en la ruta indicada  por el usuario
			projectEJB.create(null);
			projectEJB.open(null);
	
			IFacetedProject fpEJB = ProjectFacetsManager.create(projectEJB.getProject(), true, null);
			fpEJB.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.java").getVersion(Constants.JST_JAVA_VERSION), null, null);
			fpEJB.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.ejb").getVersion(Constants.JST_EJB_VERSION), null, null);
			// Facets de weblogic
			fpEJB.installProjectFacet(ProjectFacetsManager.getProjectFacet("wls.ejb").getVersion(Constants.WEBLOGIC_SERVER_VERSION), null, null);
			// Añade el runTime de Oracle
			fpEJB.addTargetedRuntime(Utilities.addServerRuntime(fpEJB, "jst.ejb", Constants.JST_EJB_VERSION), new SubProgressMonitor(monitor,1));
			
			/*
			Set<IRuntime> runtimes = RuntimeManager.getRuntimes();
			for (Iterator<IRuntime> iterator = runtimes.iterator(); iterator.hasNext();) {
				IRuntime runtime = (IRuntime) iterator.next();
				
				if (runtime.getName().contains("WebLogic") && runtime.supports(ProjectFacetsManager.getProjectFacet("jst.ejb"))){
					fpEJB.addTargetedRuntime(runtime, new SubProgressMonitor(monitor,1));
				}
			}
			*/
			
			fpEJB.setFixedProjectFacets(new HashSet<IProjectFacet>(Arrays.asList(new IProjectFacet[]{
					ProjectFacetsManager.getProjectFacet("jst.ejb"),
					ProjectFacetsManager.getProjectFacet("jst.java")
				})));
		} catch (Exception e) {
			consola.println("¡No tiene OEPE con WebLogic instalado para el EJB!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
			page.setMessage(
					"¡No tiene OEPE con WebLogic instalado para el EJB!",
					IMessageProvider.ERROR);
		}
	
		//asignamos la carpeta ejbModuke como la del source
		final IJavaProject javaP = JavaCore.create(projectEJB);
		removeAllSourceFolders(javaP);
		IFolder borrarCarpeta = projectEJB.getFolder("src");
		borrarCarpeta.deleteMarkers("src", true, IResource.DEPTH_ZERO);
		borrarCarpeta.delete( true, null);
		projectEJB.build(IncrementalProjectBuilder.CLEAN_BUILD,null);
		ProjectWorker.refresh(projectEJB);
		// Set ejbModule folder (created during jst.ejb facet installation) as source folder
		final IFolder srcFolder = projectEJB.getFolder("ejbModule");
		final IClasspathEntry ejbModuleCpEntry = JavaCore.newSourceEntry(srcFolder.getFullPath().makeAbsolute());
		//IClasspathEntry[] oldEntries = JavaCore.create(projectEJB).getRawClasspath();
		//IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		addToClasspath(JavaCore.create(projectEJB), ejbModuleCpEntry);

			
		 String pathEJB = Activator.getDefault().getPreferenceStore()
			.getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
			+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EJB;
		String path =  projectEJB.getLocation().toString() + "/.settings";
		ProjectWorker.copyFile(pathEJB+"/.settings/", path, "org.eclipse.jdt.ui.prefs", context);

		//ProjectWorker.copyFile(pathEJB, path, "oracle.eclipse.tools.weblogic.syslib.xml", context);
		//INCLUIR MODULES Y MODULES_EXTRA EN LA CONFIGURACIÓN DE Weblogic System Library del proyecto
		ProjectWorker.createFileTemplate(pathEJB+"/.settings/", path, "oracle.eclipse.tools.weblogic.syslib.xml", context);
		
		path = projectEJB.getLocation().toString();
		ProjectWorker.createFileTemplate(pathEJB+"/ejbModule/META-INF/", path +"/ejbModule/META-INF/", "ejb-jar.xml", context);
		
		try {
			String locationText=projectEJB.getParent().getLocation().toString()+"/"+context.get(Constants.EAR_NAME_PATTERN);
			String locationAux=locationText.replace("/", "\\");
			ProjectWorker.addEjbModuleEARApplication(locationAux+"\\EarContent\\META-INF\\",new File(locationAux+"\\EarContent\\META-INF\\application.xml"),context );
		
		} catch (Exception e) {
			consola.println("Error en la actualización del application.xml!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
		}
				
		//Organiza las librerias que debe tener un proyecto EJB
		ProjectWorker.organizeEJBLibraries(projectEJB, context, monitor);
		
		
		return projectEJB;
	}
	/**
	 * Añade una entrada al classpath
	 * 
	 * @param javaProject - proyecto java 
	 * @param cpEntry - entrada a añadir en el classpath 
	 * @throws Exception
	 */
	private static void addToClasspath(final IJavaProject javaProject, final IClasspathEntry cpEntry) throws JavaModelException
 	{
 		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
 		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
 		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
 		newEntries[oldEntries.length] = cpEntry;
 		javaProject.setRawClasspath(newEntries, null);
 	}
	/**
	 * Borra las carpetas de código java
	 * 
	 * @param javaP - proyecto java 
	 * @throws Exception
	 */
    private static void removeAllSourceFolders(final IJavaProject javaP) throws JavaModelException
 	{
 		final List<IClasspathEntry> newClasspath = new ArrayList<IClasspathEntry>();
 		for(IClasspathEntry cpEntry : javaP.getRawClasspath())
 		{
 			if(cpEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE)
 			{
 				continue;
 			}
 			newClasspath.add(cpEntry);
 		}
 		javaP.setRawClasspath(newClasspath.toArray(new IClasspathEntry[newClasspath.size()]), new NullProgressMonitor());
 	}


	/**
	 * Verifica si el proyecto tiene la tecnologia JPA 2.0 o no
	 * @param persistence - fichero persistence.xml
	 * @return devuelve true si identifica que es de tipo JPA, false si es de tipo Spring-JDBC
	 */
	private boolean isPersistenceJPA(IFile persistence){
		try {
			return Utilities.searchString(persistence.getLocation().toString(), "persistence");
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	/**
	 * A partir del nombre del proyecto EAR de con nomenclatura EJIE, recupera el código de aplicación.
	 * @param project - proyecto EAR
	 * @return - retorna el código de aplicación
	 */
	private String getCodAppFromEAR(IProject project){
		String codApp = "";
		
		if (project != null){
			String nameProject = project.getName();
			
			if (!Utilities.isBlank(nameProject) && nameProject.endsWith(Constants.EAR_NAME)){
				codApp = nameProject.substring(0, nameProject.length() - Constants.EAR_NAME.length());
			}
		}
		
		return codApp;
	}
	
	/**
	 * Genera el texto que se sacará de sumario con las operaciones realizadas.
	 * @param context - contexto con la información del los proyectos
	 * @return - texto del sumario
	 */
	private String createSummary(Map<String, Object> context){
		StringBuffer summaryText = new StringBuffer();
		
		summaryText.append("\n\n- Módulo EJB generado para la aplicación ");
		summaryText.append(context.get(Constants.CODAPP_PATTERN));
		summaryText.append(": \n\t - ");
		summaryText.append(context.get(Constants.EJB_NAME_PATTERN));
		summaryText.append("\n - Enlaza el módulo EJB a la aplicación.");		
		
		return summaryText.toString();
	}
	/**
	 * Comprueba si existe el proyecto EJB
	 * @param projectEar - proyecto EAR contenedor
	 * @return - encontrado
	 */
	/*private boolean existsEJB(IProject projectEar){
		boolean found=false;
		String codApp= projectEar.getName().replace(Constants.EAR_NAME, "");
		IProject[] projectos=ResourcesPlugin.getWorkspace().getRoot().getProjects();
		Iterator<IProject>  itProyects =Arrays.asList(projectos).iterator();
		while(itProyects.hasNext() && !found){
			String nombreProy=itProyects.next().getName();
			if (nombreProy.toUpperCase().startsWith(codApp.toUpperCase()) && nombreProy.toUpperCase().endsWith(Constants.EJB_NAME)){
				found=true;
			}
		}
		return found;
	}*/
	
	private IProject getProject(String projectName) {

		IProject projectsEARClasses;
		projectsEARClasses = EarUtilities.getProject(projectName);
		return projectsEARClasses;
		
	}
	
}