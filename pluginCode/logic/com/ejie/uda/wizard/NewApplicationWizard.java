package com.ejie.uda.wizard;



import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jst.j2ee.classpathdep.UpdateClasspathAttributeUtil;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

import com.ejie.uda.Activator;
import com.ejie.uda.operations.AntTaskWorker;
import com.ejie.uda.operations.ProjectWorker;
import com.ejie.uda.operations.RVCopyWorker;
import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

/**
 *  Clase encargada de generar todo lo necesario para el asistente "Crear nueva aplicación"
 * 
 */
public class NewApplicationWizard extends Wizard implements INewWizard {

	private NewApplicationWizardPage page;
	private ISelection selection;
	private String errorMessage;
	private String summary;
	// Logs en la consola
	private ConsoleLogger consola = new ConsoleLogger(Constants.CONSOLE_NAME);
	
	/**
	 * Constructor
	 */
	public NewApplicationWizard() {
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
		page = new NewApplicationWizardPage(selection);
		addPage(page);
	}

	/**
	 * Recupera los datos de la ventana e inicia el tratamiento del plugin
	 * 
	 * @return true si la ejecución es correcta, false ecc.
	 */
	public boolean performFinish() {
		// Recupera la información de la ventana
		final String appCode = page.getAppCode();
		final String warName = page.getWarName();
		final boolean radSpringJDBC = page.getRadSpringJDBC();
		final boolean radJPA = page.getRadJPA();
		final boolean locationCheck = page.getLocationCheck();
		final String locationText = page.getLocationText();
		final String idSecurity = "";
		final boolean ejbCheck = page.getEjbCheck();
		final String ejbProject = page.getEjbFullNameText();
		final String layout = page.getDispositionCombo();
		final String appType = page.getAppTypeCombo();
		final String category = page.getCategoryCombo();
		final String defaultLanguage = page.getLanguageCombo();
		final String languages = page.getLanguages();
		final String languagesWithoutQuotes = page.getLanguagesWithoutQuotes();
		
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
				page.setMessage("La ruta configurada en Windows > Preferences > UDA no es válida",
						IMessageProvider.ERROR);
				return false;
			}
		}
		
		// Validación de campos obligatorios y su contenido
		if (appCode == null || "".equals(appCode)) {
			page.setMessage("Campo 'Código de aplicación' obligatorio",
					IMessageProvider.ERROR);
			return false;
		}else if (!Utilities.validateText(appCode)){
			page.setMessage("Caracteres no válidos para en campo 'Código de aplicación'",
					IMessageProvider.ERROR);
			return false;
		}
		if (warName == null || "".equals(warName)) {
			page.setMessage("Campo 'Nombre del WAR' obligatorio",
					IMessageProvider.ERROR);
			return false;
		}else if (!Utilities.validateWARText(warName)){
			page.setMessage("Caracteres no válidos para en campo 'Nombre del WAR'",
					IMessageProvider.ERROR);
			return false;
		// Verifica que no hay ningún proyecto con el mismo código de aplicación
		}else if(ResourcesPlugin.getWorkspace().getRoot().getProject(appCode + warName + Constants.WAR_NAME).exists()){
				page.setMessage("Ya existe proyectos con este código de aplicación",
						IMessageProvider.ERROR);
				return false;
		}
		// Verifica que se ha seleccionado una tecnología de persistencia
		if (!radSpringJDBC && !radJPA){
			page.setMessage("Campo 'Persistencia' obligatorio",
					IMessageProvider.ERROR);
			return false;
		}
		// En el caso que se indique la ruta de los proyecto, verificar si es correcta
		if(!locationCheck){
			if(!ProjectWorker.createFolder(locationText)){
				page.setMessage("La ruta indicada en el campo 'Localización' es incorrecta",
						IMessageProvider.ERROR);
				return false;
			}
		}

		// Validación idiomas
		if (Utilities.isBlank(languages) || Utilities.isBlank(languagesWithoutQuotes)) {
			page.setMessage("Debe estar seleccionado al menos un idioma",
					IMessageProvider.ERROR);
			return false;
		}
		if (Utilities.isBlank(defaultLanguage)) {
			page.setMessage("El campo 'Idioma por defecto' es obligatorio",
					IMessageProvider.ERROR);
			return false;
		}
		
		// Validación EJBProject
		if ( ejbCheck && Utilities.isBlank(ejbProject) ) {
			page.setMessage("El campo 'Nombre Proyecto EJBs' es obligatorio para remoting",
					IMessageProvider.ERROR);
			return false;
		}
		
		// Verifica si es correcta la instalación de maven
		if(Utilities.isBlank(System.getenv("M2_HOME"))){
			page.setMessage("No se ha detectado la variable de entorno 'M2_HOME' de maven",
					IMessageProvider.ERROR);
			return false;
		}
		
		page.setMessage("Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar", IMessageProvider.NONE);
		
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				try {
					monitor.beginTask("Generando proyectos UDA", 8);
					// Inicia tratamiento del plugin
					doFinish(appCode, warName, monitor, radSpringJDBC, radJPA, locationCheck, locationText, idSecurity, ejbCheck, ejbProject, layout, appType, category, languages, defaultLanguage, languagesWithoutQuotes);

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
			MessageDialog.openInformation(getShell(), "Información", "!Las operaciones se han realizado con éxito!" + this.summary);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Error", "Error en la generación de la aplicación: " + errorMessage);
		}
		return true;
	}

	/**
	 * Genera los proyectos xxEARClasses, xxxWAR, xxxEAR, los enlaza y lanza una
	 * tarea Ant para descargar las dependencias desde Maven
	 * 
	 * @param appCode - código de aplicación
	 * @param monitor - monitor de progreso del plugin
	 * @param radSpringJDBC - radio que indica si la capa de datos será con tecnología Spring-JDBC
	 * @param radJPA - radio que indica si la capa de datos será con tecnologia JPA 2.0
	 * @throws Exception
	 */
	private void doFinish(String appCode, String warName,
			IProgressMonitor monitor, boolean radSpringJDBC,
			boolean radJPA, boolean locationCheck, String locationText, String idSecurity, 
			boolean ejbCheck, String ejbProyName,
			String layout, String appType, String category, 
			String languages, String defaultLanguage, String languagesWithoutQuotes) throws Exception {
		
		consola = ConsoleLogger.getDefault();
		consola.println("UDA - INI", Constants.MSG_INFORMATION);
		
		// Pretedermina valor de Tipo de aplicación
		if(!"internet".equalsIgnoreCase(appType)){
			appType = "intranet";
		}
		// Si no es un desarrollo para EJIE son relevantes el tipo de aplicación y su categoria
		if(!"true".equalsIgnoreCase(Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE))){
			appType = "";
			category = "";
		}
		
		// Contexto del plugin
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(Constants.CODAPP_PATTERN, appCode.toLowerCase());
		context.put(Constants.WAR_NAME_PATTERN, appCode + warName + Constants.WAR_NAME);
		context.put(Constants.EAR_NAME_PATTERN, appCode + Constants.EAR_NAME);
		context.put(Constants.EARCLASSES_NAME_PATTERN, appCode + Constants.EARCLASSES_NAME);
		//context.put(Constants.EJB_NAME_PATTERN, appCode + Constants.EJB_NAME);
		context.put(Constants.STATICS_PATTERN, appCode + Constants.STATICS_NAME);
		context.put(Constants.CONFIG_NAME_PATTERN, appCode + Constants.CONFIG_NAME);
		context.put(Constants.RADJPA_PATTERN, radJPA);
		context.put(Constants.RADSPRINGJDBC_PATTERN, radSpringJDBC);
		context.put(Constants.PREF_EJIE_PATTERN, Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE));
		context.put(Constants.ID_SECURITY, idSecurity);
		context.put(Constants.CODROLE_PATTERN, "UDA");
		context.put("codroleAux", "hasRole('ROLE_UDA')");
		context.put(Constants.EJB_NAME_PATTERN, ejbProyName);
		context.put(Constants.LAYOUT_PATTERN, layout.toLowerCase());
		context.put(Constants.LANGUAGES_PATTERN, languages);
		context.put(Constants.LANGUAGES_WITHOUT_QUOTES_PATTERN, languagesWithoutQuotes);
		context.put(Constants.DEFAULT_LANGUAGE_PATTERN, defaultLanguage);
		context.put(Constants.WAR_NAME_SHORT_PATTERN, appCode + warName);
		
		// Recupera el Workspace para crear los proyectos
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		// Crea el proyecto xxxConfig
		monitor.setTaskName("Creando proyecto Config...");
		IProject projectConfig = createProjectConfig(root, context);
		monitor.worked(1);

		// Crea el proyecto xxxStatics
		monitor.setTaskName("Creando proyecto Statics...");
		IProject projectStatics = createProjectStatics(root, locationCheck, locationText, context, monitor);
		monitor.worked(1);
		
		// Crea el proyecto xxxEARClasses
		monitor.setTaskName("Creando proyecto EARClasses...");
		IProject projectEARClasses = createProjectEARClasses(root, radJPA, locationCheck, locationText, context, monitor);
		monitor.worked(1);
		
		// Crea el proyecto de xxxEAR
		monitor.setTaskName("Creando proyecto EAR...");
		IProject projectEAR = createProjectEAR(root, locationCheck, locationText, context, monitor);
		monitor.worked(1);
		
		// Crea el proyecto de xxxWAR
		monitor.setTaskName("Creando proyecto WAR...");
		IProject projectWAR = createProjectWAR(root, radJPA, locationCheck, locationText, layout, appType, category, context, monitor);
		monitor.worked(1);
		IProject projectEJB = null;
		if (ejbCheck){
			// Crea el proyecto de xxxEJB
			monitor.setTaskName("Creando proyecto EJB...");
			projectEJB = createProjectEJB(root, locationCheck, locationText, context, monitor);
			
			// Relaciona los proyectos al EJB
			monitor.setTaskName("Enlazando el EJB a la aplicación...");
			ProjectWorker.linkedReferencesProjects(projectEAR, projectEJB);
			monitor.worked(1);
			
			monitor.setTaskName("Enlazando el EJB a la aplicación...");
			//08/03/2011
		//	ProjectWorker.createEARDependency(projectEAR, projectEJB);
		    ProjectWorker.linkedProjectsClasspath(projectEARClasses,projectEJB);
			
		}
		// Relaciona los proyectos al EAR
		ProjectWorker.linkedReferencesProjects(projectEAR, projectWAR);
		ProjectWorker.linkedReferencesProjects(projectEAR, projectEARClasses, "/lib");
		monitor.setTaskName("Enlazando los proyectos generados...");
		monitor.worked(1);
		
		// Recupera la ruta raiz del proyecto, es donde situamos el pom.xml
		String pathProject = projectEAR.getLocation().toString();
		
		consola.println("DEPENDENCIES TASK - INI", Constants.MSG_INFORMATION);
		// Ejecutamos el target de la tarea Ant indicado
		
		try{
			monitor.setTaskName("Recuperando las librerías, puede tardar algunos minutos...");
			AntTaskWorker.executeOperation(pathProject, "mavenRunDependencies");
			
			if(monitor.isCanceled()){
				throw new OperationCanceledException("operación cancelada por el usuario");
			}
			monitor.worked(1);
				
		}catch (Exception e) {
			consola.println("Error detectado", Constants.MSG_ERROR);
		}
		
		consola.println("DEPENDENCIES TASK - END", Constants.MSG_INFORMATION);
		// Actualiza los proyectos al finalizar la ejecución
		ProjectWorker.refresh(projectConfig);
		ProjectWorker.refresh(projectStatics);
		ProjectWorker.refresh(projectEARClasses);
		ProjectWorker.refresh(projectWAR);
		if (ejbCheck){
			ProjectWorker.refresh(projectEJB);
		}
		ProjectWorker.refresh(projectEAR);
		monitor.worked(1);
		consola.println("***************************************************************", Constants.MSG_INFORMATION);
		consola.println("Ubicación de logs de la aplicación: " + Constants.PATH_DATOS + context.get(Constants.CODAPP_PATTERN)+"\\log" , Constants.MSG_INFORMATION);
		consola.println("" , Constants.MSG_INFORMATION);
		consola.println("Para revisar la configuración del log en " + Constants.PATH_CONFIG + context.get(Constants.CODAPP_PATTERN) + "log4j.properties", Constants.MSG_INFORMATION);
		consola.println("" , Constants.MSG_INFORMATION);
		consola.println("Recuerda que deberás tener configurado el entorno con: ", Constants.MSG_INFORMATION);
		consola.println("- el servidor Weblogic Server 10.3.1 ", Constants.MSG_INFORMATION);
		consola.println("- las reglas de los plugins de calidad: PMD, Checkstyle y Findbugs en las preferencias (Window>Preferences) ", Constants.MSG_INFORMATION);
		consola.println("- la librería de dependencias UDAWLS11Classpath ", Constants.MSG_INFORMATION);
		consola.println("***************************************************************", Constants.MSG_INFORMATION);
		consola.println("UDA - END", Constants.MSG_INFORMATION);
		
		//Generar sumario si ha generado correctamente
		this.summary = createSummary(context, ejbCheck);
	}
	
	/**
	 * Crea el proyecto de contenido Estático
	 * 
	 * @param root - workspace del eclipse
	 * @param context - contexto del plugin 
	 * @return proyecto tipo xxxStatics
	 * @throws Exception
	 */
	private IProject createProjectStatics(IWorkspaceRoot root, boolean locationCheck, String locationText, Map<String, Object> context, IProgressMonitor monitor) throws Exception {
		
		String pathStatics = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH) + Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_STATICS;
		
		// Crea el proyecto de xxxStatics
		IProject projectStatics = root.getProject((String)context.get(Constants.STATICS_PATTERN));

		// Verifica si crea el proyecto en el Workspace o en la ruta indicada  por el usuario
		if (locationCheck){
			projectStatics.create(null);
			projectStatics.open(null);
		}else{
			projectStatics = ProjectWorker.createProjectLocation(projectStatics, locationText, true);				
		}
		
		// Lo genera como un Dinamic Project
		IFacetedProject fpStaticsWAR = ProjectFacetsManager.create(projectStatics.getProject(), true, null);
		fpStaticsWAR.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.java").getVersion("1.6"), null, null);
		fpStaticsWAR.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.web").getVersion("2.5"), null, null);
		fpStaticsWAR.installProjectFacet(ProjectFacetsManager.getProjectFacet("wst.jsdt.web").getVersion("1.0"), null, null);
		try {
			// Facets de weblogic
			fpStaticsWAR.installProjectFacet(ProjectFacetsManager.getProjectFacet("wls.web").getVersion("10.3.1"), null, null);
		} catch (Exception e) {
			consola.println("No tiene OEPE con WebLogic instalado para el WAR!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
			page.setMessage("No tiene OEPE con WebLogic instalado para el WAR!",IMessageProvider.ERROR);
		}

		//Añade el runTime de Oracle
		Set<IRuntime> runtimes = RuntimeManager.getRuntimes();
		for (Iterator<IRuntime> iterator = runtimes.iterator(); iterator.hasNext();) {
			IRuntime runtime = (IRuntime) iterator.next();
			
			if (runtime.getName().contains("WebLogic") && runtime.supports(ProjectFacetsManager.getProjectFacet("jst.web").getVersion("2.5"))){
				fpStaticsWAR.addTargetedRuntime(runtime, new SubProgressMonitor(monitor,1));
			}
		}
		fpStaticsWAR.setFixedProjectFacets(new HashSet<IProjectFacet>(Arrays.asList(new IProjectFacet[]{
				ProjectFacetsManager.getProjectFacet("jst.java"),
				ProjectFacetsManager.getProjectFacet("jst.web")
			})));
		
//		//AÑADIR LA USER SYSTEM LIBRARY (UDAWLS11Classpath)
//		final IClasspathAttribute[] atribs = new IClasspathAttribute[]{UpdateClasspathAttributeUtil.createNonDependencyAttribute()};
//		final IClasspathEntry userLibCpEntry = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.USER_LIBRARY/UDAWLS11Classpath"), null, atribs, true);
//		ProjectWorker.addToClasspath(JavaCore.create(projectStatics), userLibCpEntry);		
		
				
//		//Organiza las librerias que debe tener un proyecto WAR
//		ProjectWorker.organizeWARLibraries(projectStatics, context, monitor);
				
		// Copia la configuración de RUP en la carpeta WebContent/rup del proyecto Statics
		String path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/rup");
		String pathSource = pathStatics + Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_STATICS_RUP;
		RVCopyWorker.copyDirectory(new File(pathSource), new File(path));
		
		//i18n
		String languages = (String) context.get(Constants.LANGUAGES_PATTERN);
		if (languages.indexOf("es")!=-1){
			ProjectWorker.copyFile(pathStatics, path+"/resources", "rup.i18n_es.json", context);
		}
		if (languages.indexOf("eu")!=-1){
			ProjectWorker.copyFile(pathStatics, path+"/resources", "rup.i18n_eu.json", context);
		}
		if (languages.indexOf("en")!=-1){
			ProjectWorker.copyFile(pathStatics, path+"/resources", "rup.i18n_en.json", context);
		}
		if (languages.indexOf("fr")!=-1){
			ProjectWorker.copyFile(pathStatics, path+"/resources", "rup.i18n_fr.json", context);
		}
		
		// Copia la configuración específica de la aplicación en la carpeta WebContent/codapp del proyecto Statics
		path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/" + (String)context.get(Constants.CODAPP_PATTERN));
		pathSource = pathStatics + Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_STATICS_APLIC;
		RVCopyWorker.copyDirectory(new File(pathSource), new File(path));
		
		ProjectWorker.refresh(projectStatics);

		// Fichero personalizados
		path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/" +(String)context.get(Constants.CODAPP_PATTERN) + "/styles");
		ProjectWorker.copyFile(pathStatics, path, "xxx.css", context, context.get(Constants.CODAPP_PATTERN) + ".css");
		path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/" +(String)context.get(Constants.CODAPP_PATTERN) + "/resources");
	
		//i18n
		//String languages = (String) context.get(Constants.LANGUAGES_PATTERN);
		if (languages.indexOf("es")!=-1){
			ProjectWorker.createFileTemplate(pathStatics, path, "xxx.i18n_es.json", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_es.json");
		}
		if (languages.indexOf("eu")!=-1){
			ProjectWorker.createFileTemplate(pathStatics, path, "xxx.i18n_eu.json", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_eu.json");
		}
		if (languages.indexOf("en")!=-1){
			ProjectWorker.createFileTemplate(pathStatics, path, "xxx.i18n_en.json", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_en.json");
		}
		if (languages.indexOf("fr")!=-1){
			ProjectWorker.createFileTemplate(pathStatics, path, "xxx.i18n_fr.json", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_fr.json");
		}
		
		path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/" +(String)context.get(Constants.CODAPP_PATTERN) + "/scripts");
		ProjectWorker.createFileTemplate(pathStatics, path, "layoutLoader.js", context, "layoutLoader" + Utilities.camelCase((String)context.get(Constants.WAR_NAME_SHORT_PATTERN)+".js"));
		
		
		// Genera los ficheros de configuración del proyecto
		path =  projectStatics.getLocation().toString() + "//.settings";
		ProjectWorker.copyFile(pathStatics, path, "org.eclipse.jdt.ui.prefs", context);
		ProjectWorker.copyFile(pathStatics, path, "oracle.eclipse.tools.weblogic.syslib.xml", context);
		ProjectWorker.copyFile(pathStatics, path, "oracle.eclipse.tools.webtier.ui.prefs", context);	

		return projectStatics;
	}
	
	/**
	 * Crea el proyecto de contenido Congiuración
	 * 
	 * @param root - workspace del eclipse
	 * @param context - contexto del plugin 
	 * @return projecto tipo xxxConfig
	 * @throws Exception
	 */
	private IProject createProjectConfig(IWorkspaceRoot root, Map<String, Object> context) throws Exception {
		String pathConfig = Activator.getDefault().getPreferenceStore()
				.getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
				+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_CONFIG;

		String path = "";

		// Crea el proyecto de xxxConfig
		IProject projectConfig = root.getProject((String)context.get(Constants.CONFIG_NAME_PATTERN));

		// Crea la carpeta datos del proyecto, donde estarán los logs
		ProjectWorker.createFolder(Constants.PATH_DATOS + context.get(Constants.CODAPP_PATTERN)+"\\log");
		
		// Crea la carpeta configuracián del proyecto, donde estarán los .properties
		path = Constants.PATH_CONFIG + context.get(Constants.CODAPP_PATTERN);
			
		//Crea el proyecto en la ruta indicada
		projectConfig = ProjectWorker.createProjectLocation(projectConfig, path, false);
		if (projectConfig.exists()) {
			// path raiz del proyecto
			path = projectConfig.getLocation().toString();
			// Genera los ficheros de configuración del proyecto
			ProjectWorker.createFileTemplate(pathConfig, path, "log4j.properties", context);
			ProjectWorker.createFileTemplate(pathConfig, path, "xxx.properties", context, context.get(Constants.CODAPP_PATTERN) + ".properties");
		}	
				
		return projectConfig;
	}

	/**
	 * Crea el proyecto EARClasses
	 * 
	 * @param root - workspace del eclipse
	 * @param radJPA - true si la tecnología en JPA 2.0 para la capa persistencia
	 * @param context - contexto del plugin 
	 * @return projecto tipo xxxEARClasses
	 * @throws Exception
	 */
	private IProject createProjectEARClasses(IWorkspaceRoot root, boolean radJPA,
			boolean locationCheck, String locationText, Map<String, Object> context, IProgressMonitor monitor) throws Exception {
		
		String pathEARClasses = Activator.getDefault().getPreferenceStore()
				.getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
				+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EARCLASSES;

		String path = "";

		// Crea el proyecto de xxxEARClasses
		IProject projectEARClasses = root.getProject((String)context.get(Constants.EARCLASSES_NAME_PATTERN));
		
		// Verifica si crea el proyecto en el Workspace o en la ruta indicada  por el usuario
		if (locationCheck){
		projectEARClasses.create(null);
		projectEARClasses.open(null);
		}else{
			projectEARClasses = ProjectWorker.createProjectLocation(projectEARClasses, locationText, true);
			
		}

		IFacetedProject fpEARClasses = ProjectFacetsManager.create(projectEARClasses.getProject(), true, null);
		// Lo genera como Java Project
		fpEARClasses.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.java").getVersion("1.6"), null, null);
		fpEARClasses.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.utility").getVersion("1.0"), null, null);
		
		// Añade el runTime de Oracle
		Set<IRuntime> runtimes = RuntimeManager.getRuntimes();
		for (Iterator<IRuntime> iterator = runtimes.iterator(); iterator.hasNext();) {
			IRuntime runtime = (IRuntime) iterator.next();
			
			if (runtime.getName().contains("WebLogic") && runtime.supports(ProjectFacetsManager.getProjectFacet("jst.utility").getVersion("1.0"))){
				fpEARClasses.addTargetedRuntime(runtime, new SubProgressMonitor(monitor,1));
			}
		}
		
		fpEARClasses.setFixedProjectFacets(new HashSet<IProjectFacet>(Arrays.asList(new IProjectFacet[]{
				ProjectFacetsManager.getProjectFacet("jst.utility"),
				ProjectFacetsManager.getProjectFacet("jst.java")
			})));
		
		//Organiza las librerias que debe tener un proyecto EARClasses
		ProjectWorker.organizeEARClassesLibraries(projectEARClasses, context, monitor);
		
		path =  projectEARClasses.getLocation().toString() + "//.settings";
		ProjectWorker.copyFile(pathEARClasses, path, "org.eclipse.jdt.ui.prefs", context);
		//ProjectWorker.copyFile(pathEARClasses, path, "oracle.eclipse.tools.weblogic.syslib.xml", context);
		//INCLUIR MODULES Y MODULES_EXTRA EN LA CONFIGURACIÓN DE Weblogic System Library del proyecto
		ProjectWorker.createFileTemplate(pathEARClasses, path, "oracle.eclipse.tools.weblogic.syslib.xml", context);
		
		// Añade las carpetas de test para la generación de pruebas de calidad
		path = ProjectWorker.createGetFolderPath(projectEARClasses, "test-integration");
		IFolder sourceFolder = projectEARClasses.getFolder("test-integration");
		ProjectWorker.addSourceProject(projectEARClasses, path, monitor, sourceFolder);
		path = ProjectWorker.createGetFolderPath(projectEARClasses, "test-system");
		sourceFolder = projectEARClasses.getFolder("test-system");
		ProjectWorker.addSourceProject(projectEARClasses, path, monitor, sourceFolder);
		path = ProjectWorker.createGetFolderPath(projectEARClasses, "test-unit");
		sourceFolder = projectEARClasses.getFolder("test-unit");
	
		ProjectWorker.addSourceProject(projectEARClasses, path, monitor, sourceFolder);
				
		if (radJPA){		

			// Genera la carpeta utilizada para las anotaciones de JPA2.0 generadas desde ant
			ProjectWorker.configRepositoryMaven(context);
			path =  projectEARClasses.getLocation().toString();
			//ProjectWorker.copyFile(pathEARClasses, path, "build.xml.ftl", context);

			path =  projectEARClasses.getLocation().toString() + "//.settings";
			ProjectWorker.copyFile(pathEARClasses, path, "org.eclipse.jdt.core.prefs", context);

			ProjectWorker.copyFile(pathEARClasses, path, "org.eclipse.jdt.apt.core.prefs", context);

			path =  projectEARClasses.getLocation().toString();
			
			ProjectWorker.copyFile(pathEARClasses, path, ".factorypath.ftl", context);
		}
		
		// Añade la configuración de PMD
		path =  projectEARClasses.getLocation().toString();
		ProjectWorker.copyFile(pathEARClasses, path, ".pmd", context);
		// Añade la configuración de checkstyle
		ProjectWorker.copyFile(pathEARClasses, path, ".checkstyle", context);

		
		// Genera los ficheros de configuración del proyecto
		path = ProjectWorker.createGetFolderPath(projectEARClasses, "src");
		ProjectWorker.copyFile(pathEARClasses, path, "beanRefContext.xml", context);
		
		// Crea la carpeta spring
		path = ProjectWorker.createGetFolderPath(projectEARClasses, "src/META-INF");
		if (radJPA){
			// Si es de tipo JPA 2.0 copia los ficheros necesarios 
				//ProjectWorker.createGetFolderPath(projectEARClasses, "src");
				//path = ProjectWorker.createGetFolderPath(projectEARClasses, "src/META-INF");
				ProjectWorker.copyFile(pathEARClasses, path, "MANIFEST.MF", context);
				context.put("listaClases", "");
				ProjectWorker.createFileTemplateRename(pathEARClasses, path, "persistence","udaPersistence.xml", context);
		
		}
	
		path = ProjectWorker.createGetFolderPath(projectEARClasses, "src/META-INF/spring");
		
		ProjectWorker.copyFile(pathEARClasses, path, "tx-config.xml", context);
		ProjectWorker.copyFile(pathEARClasses, path, "log-config.xml", context);
		ProjectWorker.createFileTemplate(pathEARClasses, path, "dao-config.xml", context);
		ProjectWorker.createFileTemplate(pathEARClasses, path, "service-config.xml", context);
		ProjectWorker.createFileTemplate(pathEARClasses, path, "security-config.xml", context);		
		
		path = ProjectWorker.createGetFolderPath(projectEARClasses, "resources");
		//ProjectWorker.copyFile(pathEARClasses, path, "messages.properties", context, context.get(Constants.CODAPP_PATTERN) + ".i18n.properties");
		String languages = (String) context.get(Constants.LANGUAGES_PATTERN);
		if (languages.indexOf("es")!=-1){
			ProjectWorker.copyFile(pathEARClasses, path, "messages_es.properties", context, context.get(Constants.CODAPP_PATTERN) + ".i18n_es.properties");
		}
		if (languages.indexOf("eu")!=-1){
			ProjectWorker.copyFile(pathEARClasses, path, "messages_eu.properties", context, context.get(Constants.CODAPP_PATTERN) + ".i18n_eu.properties");
		}
		if (languages.indexOf("en")!=-1){
			ProjectWorker.copyFile(pathEARClasses, path, "messages_en.properties", context, context.get(Constants.CODAPP_PATTERN) + ".i18n_en.properties");
		}
		if (languages.indexOf("fr")!=-1){
			ProjectWorker.copyFile(pathEARClasses, path, "messages_fr.properties", context, context.get(Constants.CODAPP_PATTERN) + ".i18n_fr.properties");
		}

		
		final IFolder srcFolder = projectEARClasses.getFolder("resources");
		final IClasspathEntry resourcesCpEntry = JavaCore.newSourceEntry(srcFolder.getFullPath().makeAbsolute());
		ProjectWorker.addToClasspath(JavaCore.create(projectEARClasses), resourcesCpEntry);
		
		//AÑADIR LA USER SYSTEM LIBRARY (UDAWLS11Classpath)
		final IClasspathAttribute[] atribs = new IClasspathAttribute[]{UpdateClasspathAttributeUtil.createNonDependencyAttribute()};
		final IClasspathEntry userLibCpEntry = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.USER_LIBRARY/UDAWLS11Classpath"), null, atribs, true);
		ProjectWorker.addToClasspath(JavaCore.create(projectEARClasses), userLibCpEntry);
				
		try {
	
			// AÑade el nature de PMD al proyecto  
			//ProjectUtilities.addNatureToProject(projectEARClasses, "net.sourceforge.pmd.runtime.pmdNature");
			ProjectUtilities.addNatureToProject(projectEARClasses, "net.sourceforge.pmd.eclipse.plugin.pmdNature");
			
		} catch (Exception e) {
			consola.println("No tiene Plugin de PMD instalado en el Eclipse!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
		}

		try {
			// Añade el nature de Checkstyle al proyecto  
			ProjectUtilities.addNatureToProject(projectEARClasses, "net.sf.eclipsecs.core.CheckstyleNature");
			
		} catch (Exception e) {
			consola.println("No tiene Plugin de Checkstyle instalado en el Eclipse!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
		}
		
		return projectEARClasses;
	}

	/**
	 * Crea el proyecto WAR
	 * 
	 * @param root - workspace del eclipse
	 * @param context - contexto del plugin
	 * @return projecto tipo xxxWAR
	 * @throws Exception
	 */
	private IProject createProjectWAR(IWorkspaceRoot root, boolean radJPA,
			boolean locationCheck, String locationText, 
			String layout, String appType, String category,  
			Map<String, Object> context, IProgressMonitor monitor) throws Exception {

		String path = "";
		String pathWar = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
				+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_WAR;

		// Crea el proyecto de xxxWAR
		IProject projectWAR = root.getProject((String)context.get(Constants.WAR_NAME_PATTERN));
		
		// Verifica si crea el proyecto en el Workspace o en la ruta indicada  por el usuario
		if (locationCheck){
			projectWAR.create(null);
			projectWAR.open(null);
		}else{
			projectWAR = ProjectWorker.createProjectLocation(projectWAR, locationText, true);			
		}
		// Lo genera como un Dinamic Project
		IFacetedProject fpWAR = ProjectFacetsManager.create(projectWAR.getProject(), true, null);
		fpWAR.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.java").getVersion("1.6"), null, null);
		fpWAR.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.web").getVersion("2.5"), null, null);
		fpWAR.installProjectFacet(ProjectFacetsManager.getProjectFacet("wst.jsdt.web").getVersion("1.0"), null, null);
		try {
			// Facets de weblogic
			fpWAR.installProjectFacet(
					ProjectFacetsManager.getProjectFacet("wls.web").getVersion("10.3.1"), null, null);
		} catch (Exception e) {
			consola.println("No tiene OEPE con WebLogic instalado para el WAR!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
			page.setMessage(
					"No tiene OEPE con WebLogic instalado para el WAR!",
					IMessageProvider.ERROR);
		}

		// Añade el runTime de Oracle
		Set<IRuntime> runtimes = RuntimeManager.getRuntimes();
		for (Iterator<IRuntime> iterator = runtimes.iterator(); iterator.hasNext();) {
			IRuntime runtime = (IRuntime) iterator.next();
			
			if (runtime.getName().contains("WebLogic") && runtime.supports(ProjectFacetsManager.getProjectFacet("jst.web").getVersion("2.5"))){
				fpWAR.addTargetedRuntime(runtime, new SubProgressMonitor(monitor,1));
			}
		}
		fpWAR.setFixedProjectFacets(new HashSet<IProjectFacet>(Arrays.asList(new IProjectFacet[]{
				ProjectFacetsManager.getProjectFacet("jst.java"),
				ProjectFacetsManager.getProjectFacet("jst.web")
			})));
		
		//AÑADIR LA USER SYSTEM LIBRARY (UDAWLS11Classpath)
		final IClasspathAttribute[] atribs = new IClasspathAttribute[]{UpdateClasspathAttributeUtil.createNonDependencyAttribute()};
		final IClasspathEntry userLibCpEntry = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.USER_LIBRARY/UDAWLS11Classpath"), null, atribs, true);
		ProjectWorker.addToClasspath(JavaCore.create(projectWAR), userLibCpEntry);
		
		//Organiza las librerias que debe tener un proyecto EARClasses
		ProjectWorker.organizeWARLibraries(projectWAR, context, monitor);
		// Genera las carpetas y ficheros de configuración del proyecto
		// Genera los ficheros de configuración del proyecto
		path =  projectWAR.getLocation().toString();
		ProjectWorker.copyFile(pathWar, path, ".pmd", context);

		// Añade la configuración de checkstyle
		ProjectWorker.copyFile(pathWar, path, ".checkstyle", context);
		
		path =  projectWAR.getLocation().toString() + "//.settings";
		ProjectWorker.copyFile(pathWar, path, "org.eclipse.jdt.ui.prefs", context);
		//ProjectWorker.copyFile(pathWar, path, "oracle.eclipse.tools.weblogic.syslib.xml", context);
		//INCLUIR MODULES Y MODULES_EXTRA EN LA CONFIGURACIÓN DE Weblogic System Library del proyecto
		ProjectWorker.createFileTemplate(pathWar, path, "oracle.eclipse.tools.weblogic.syslib.xml", context);
		ProjectWorker.copyFile(pathWar, path, "oracle.eclipse.tools.webtier.ui.prefs", context);		
		if (radJPA){
		// Fichero encargado de indicar que el proyecto tendrá tecnologia JPA 2.0
			ProjectWorker.copyFile(pathWar, path, "com.ejie.uda.xml", context);
		}
		
		ProjectWorker.createGetFolderPath(projectWAR, "WebContent");
		ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF");
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/layouts");
		ProjectWorker.copyFile(pathWar, path, "errorTemplate.jsp", context);

		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF");
		ProjectWorker.createFileTemplate(pathWar, path, "weblogic.xml", context);
		ProjectWorker.createFileTemplate(pathWar, path, "web.xml", context);
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/spring");
		ProjectWorker.copyFile(pathWar, path, "validation-config.xml", context);
		ProjectWorker.createFileTemplate(pathWar, path, "mvc-config.xml", context);
		ProjectWorker.copyFile(pathWar, path, "log-config.xml", context);
		ProjectWorker.copyFile(pathWar, path, "security-core-config.xml", context);
		context.put("listaClases", "");
		ProjectWorker.createFileTemplate(pathWar, path, "security-config.xml", context);
		ProjectWorker.createFileTemplate(pathWar, path, "app-config.xml", context);
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/resources");
		//ProjectWorker.copyFile(pathWar, path, "messages.properties", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n.properties");
		String languages = (String) context.get(Constants.LANGUAGES_PATTERN);
		if (languages.indexOf("es")!=-1){
			ProjectWorker.copyFile(pathWar, path, "messages_es.properties", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_es.properties");
		}
		if (languages.indexOf("eu")!=-1){
			ProjectWorker.copyFile(pathWar, path, "messages_eu.properties", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_eu.properties");
		}
		if (languages.indexOf("en")!=-1){
			ProjectWorker.copyFile(pathWar, path, "messages_en.properties", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_en.properties");
		}
		if (languages.indexOf("fr")!=-1){
			ProjectWorker.copyFile(pathWar, path, "messages_fr.properties", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_fr.properties");
		}
		
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/views");
		ProjectWorker.createFileTemplate(pathWar, path, "tiles.xml", context);
		ProjectWorker.copyFile(pathWar, path, "error.jsp", context);
		ProjectWorker.copyFile(pathWar, path, "accessDenied.jsp", context);
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/views/includes");
		ProjectWorker.copyFile(pathWar, path, "includeTemplate.inc", context);
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/tld");
		ProjectWorker.copyFile(pathWar, path, "c.tld", context);
		ProjectWorker.copyFile(pathWar, path, "fmt.tld", context);
		ProjectWorker.copyFile(pathWar, path, "tiles-jsp.tld", context);
		ProjectWorker.copyFile(pathWar, path, "security.tld", context);
		ProjectWorker.copyFile(pathWar, path, "spring.tld", context);
	
		/* 11/01/2011
		// Si es de tipo JPA 2.0 copia los ficheros necesarios 
		if (radJPA){
			ProjectWorker.createGetFolderPath(projectWAR, "src");
			path = ProjectWorker.createGetFolderPath(projectWAR, "src/META-INF");
			ProjectWorker.copyFile(pathWar, path, "MANIFEST.MF", context);
			ProjectWorker.createFileTemplate(pathWar, path, "persistence.xml", context);
		}*/ 
		// Añade las carpetas de test para la generación de pruebas de calidad
		path = ProjectWorker.createGetFolderPath(projectWAR, "test-integration");
		IFolder sourceFolder = projectWAR.getFolder("test-integration");
		ProjectWorker.addSourceProject(projectWAR, path, monitor, sourceFolder);
		path = ProjectWorker.createGetFolderPath(projectWAR, "test-system");
		sourceFolder = projectWAR.getFolder("test-system");
		ProjectWorker.addSourceProject(projectWAR, path, monitor, sourceFolder);
		path = ProjectWorker.createGetFolderPath(projectWAR, "test-unit");
		sourceFolder = projectWAR.getFolder("test-unit");
	
		ProjectWorker.addSourceProject(projectWAR, path, monitor, sourceFolder);
		
		try {
			// Añade el nature de PMD al proyecto
			//ProjectUtilities.addNatureToProject(projectWAR, "net.sourceforge.pmd.runtime.pmdNature");
			ProjectUtilities.addNatureToProject(projectWAR, "net.sourceforge.pmd.eclipse.plugin.pmdNature");
		
		} catch (Exception e) {
			consola.println("No tiene Plugin de PMD instalado en el Eclipse!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
		}

		try {
			// Añade el nature de checkstyle al proyecto  
			ProjectUtilities.addNatureToProject(projectWAR, "net.sf.eclipsecs.core.CheckstyleNature");
		} catch (Exception e) {
			consola.println("No tiene Plugin de Checkstyle instalado en el Eclipse!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
		}
		
		// Creación de layout segén Tipo de aplicación, Disposición y Categoría
		if(Constants.APP_TYPE_INTRANET.equalsIgnoreCase(appType)){
			if(Constants.CATEGORY_DEPARTAMENTAL.equalsIgnoreCase(category)){
				path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/layouts");
				ProjectWorker.copyFile(pathWar + "\\intraDept", path, "footer.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "header.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "breadCrumb.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "language.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "base-includes.jsp", context);

				if (Constants.LAYOUT_HORIZONTAL.equalsIgnoreCase(layout)){
					// Layout horizontal
					ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "menu.jsp", context);
					ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "template.jsp", context);

				}else if (Constants.LAYOUT_VERTICAL.equalsIgnoreCase(layout)){
					// Layout vertical
					ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "menu.jsp", context);
					ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "templateVertical.jsp", context, "template.jsp");
					
				}else if (Constants.LAYOUT_MIXTO.equalsIgnoreCase(layout)){
					// Layout mixto
					ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "menuMixto.jsp", context);
					ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "menu.jsp", context);
					ProjectWorker.createFileTemplate(pathWar + "\\intraDept", path, "templateMixto.jsp", context, "template.jsp");
				}	
			}else{
				path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/layouts");
				ProjectWorker.copyFile(pathWar + "\\intraHori", path, "footer.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "header.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "breadCrumb.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "language.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "base-includes.jsp", context);

				if (Constants.LAYOUT_HORIZONTAL.equalsIgnoreCase(layout)){
					// Layout horizontal
					ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "menu.jsp", context);
					ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "template.jsp", context);

				}else if (Constants.LAYOUT_VERTICAL.equalsIgnoreCase(layout)){
					// Layout vertical
					ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "menu.jsp", context);
					ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "templateVertical.jsp", context, "template.jsp");
					
				}else if (Constants.LAYOUT_MIXTO.equalsIgnoreCase(layout)){
					// Layout mixto
					ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "menuMixto.jsp", context);
					ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "menu.jsp", context);
					ProjectWorker.createFileTemplate(pathWar + "\\intraHori", path, "templateMixto.jsp", context, "template.jsp");
				}	
			}
		}else{
			path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/layouts");
			ProjectWorker.copyFile(pathWar + "\\internet", path, "footer.jsp", context);
			ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "header.jsp", context);
			ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "breadCrumb.jsp", context);
			ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "language.jsp", context);
			ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "base-includes.jsp", context);

			if (Constants.LAYOUT_HORIZONTAL.equalsIgnoreCase(layout)){
				// Layout horizontal
				ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "menu.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "template.jsp", context);

			}else if (Constants.LAYOUT_VERTICAL.equalsIgnoreCase(layout)){
				// Layout vertical
				ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "menu.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "templateVertical.jsp", context, "template.jsp");
				
			}else if (Constants.LAYOUT_MIXTO.equalsIgnoreCase(layout)){
				// Layout mixto
				ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "menuMixto.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "menu.jsp", context);
				ProjectWorker.createFileTemplate(pathWar + "\\internet", path, "templateMixto.jsp", context, "template.jsp");
			}
		}
		
		// Copia la página de Inicio
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/views");
		ProjectWorker.copyFile(pathWar, path, "welcome.jsp", context, "welcome.jsp");
		
		return projectWAR;
	}

	/**
	 * Crea el proyecto EAR
	 * 
	 * @param root - workspace del eclipse
	 * @param context - contexto del plugin
	 * @return proyecto tipo xxxEAR
	 * @throws Exception
	 */
	private IProject createProjectEAR(IWorkspaceRoot root,
			boolean locationCheck, String locationText, Map<String, Object> context, IProgressMonitor monitor) throws Exception {
		
		String path = "";
		String pathEar = Activator.getDefault().getPreferenceStore()
				.getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
				+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EAR;

		// Crea el proyecto de xxxEAR
		IProject projectEAR = root.getProject((String)context.get(Constants.EAR_NAME_PATTERN));
		// Verifica si crea el proyecto en el Workspace o en la ruta indicada  por el usuario
		if (locationCheck){
			projectEAR.create(null);
			projectEAR.open(null);
		}else{
			projectEAR = ProjectWorker.createProjectLocation(projectEAR, locationText, true);			
		}

		IFacetedProject fpEAR = ProjectFacetsManager.create(projectEAR.getProject(), true, null);
		fpEAR.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.ear").getVersion("5.0"), null, null);
		fpEAR.setFixedProjectFacets(new HashSet<IProjectFacet>(Arrays.asList(new IProjectFacet[]{ProjectFacetsManager.getProjectFacet("jst.ear")})));
		
		// Añade el runTime de Oracle
		Set<IRuntime> runtimes = RuntimeManager.getRuntimes();
		for (Iterator<IRuntime> iterator = runtimes.iterator(); iterator.hasNext();) {
			IRuntime runtime = (IRuntime) iterator.next();
			
			if (runtime.getName().contains("WebLogic") && runtime.supports(ProjectFacetsManager.getProjectFacet("jst.ear"))){
				fpEAR.addTargetedRuntime(runtime, new SubProgressMonitor(monitor,1));
			}
		}
		
		try {
				fpEAR.installProjectFacet(ProjectFacetsManager.getProjectFacet("wls.ear").getVersion("10.3.1"), null, null);
		} catch (Exception e) {
			consola.println("No tiene OEPE con WebLogic instalado para EAR!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
			page.setMessage(
					"No tiene OEPE con WebLogic instalado para el EAR!",
					IMessageProvider.ERROR);
		}

		// Detecta la localización del fichero de configuración de maven "settings.xml"
		ProjectWorker.configSettingsMaven(context);
		
		// Crea carpetas del proyecto
		ProjectWorker.createGetFolderPath(projectEAR, "EarContent");
		ProjectWorker.createGetFolderPath(projectEAR, "EarContent/APP-INF");
		ProjectWorker.createGetFolderPath(projectEAR, "EarContent/APP-INF/classes");
		ProjectWorker.createGetFolderPath(projectEAR, "EarContent/APP-INF/lib");

		// Genera los ficheros de configuración del proyecto
		path = projectEAR.getLocation().toString();
		ProjectWorker.createFileTemplate(pathEar, path, "pom.xml", context);
		ProjectWorker.createFileTemplate(pathEar, path, "build.xml", context);
		path = ProjectWorker.createGetFolderPath(projectEAR, "EarContent/META-INF");
		ProjectWorker.createFileTemplate(pathEar, path, "application.xml", context);
		ProjectWorker.createFileTemplate(pathEar, path, "weblogic-application.xml", context);

		// Copia la libreria de ant task para que se autoejecute las dependecias desde maven
		path = AntCorePlugin.getPlugin().getPreferences().getAntHome() + "\\lib";
		ProjectWorker.copyFile(pathEar, path, "maven-ant-tasks-2.1.1.jar", context);
		
		return projectEAR;
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
			boolean locationCheck, String locationText, Map<String, Object> context, IProgressMonitor monitor) throws Exception {
		

		
		// Crea el proyecto de xxxEAR
		IProject projectEJB = root.getProject((String)context.get(Constants.EJB_NAME_PATTERN));
		// Verifica si crea el proyecto en el Workspace o en la ruta indicada  por el usuario
		if (locationCheck){
			projectEJB.create(null);
			projectEJB.open(null);
		}else{
			projectEJB = ProjectWorker.createProjectLocation(projectEJB, locationText, true);			
		}
	

		IFacetedProject fpEJB = ProjectFacetsManager.create(projectEJB.getProject(), true, null);
		fpEJB.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.java").getVersion("1.6"), null, null);
		fpEJB.installProjectFacet(ProjectFacetsManager.getProjectFacet("jst.ejb").getVersion("3.0"), null, null);
		
		//asignamos la carpeta ejbModuke como la del source
		final IJavaProject javaP = JavaCore.create(projectEJB);
		removeAllSourceFolders(javaP);
	//	projectEJB.build(arg0, arg1);
		IFolder borrarCarpeta = projectEJB.getFolder("src");
		borrarCarpeta.deleteMarkers("src", true, IResource.DEPTH_ZERO);
		borrarCarpeta.delete( true, null);
		projectEJB.build(IncrementalProjectBuilder.CLEAN_BUILD,null);
		ProjectWorker.refresh(projectEJB);
		// Set ejbModule folder (created during jst.ejb facet installation) as source folder
		final IFolder srcFolder = projectEJB.getFolder("ejbModule");
		final IClasspathEntry ejbModuleCpEntry = JavaCore.newSourceEntry(srcFolder.getFullPath().makeAbsolute());
		//addToClasspath(JavaCore.create(projectEJB), ejbModuleCpEntry);
//		IClasspathEntry[] oldEntries = JavaCore.create(projectEJB).getRawClasspath();
//		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		ProjectWorker.addToClasspath(JavaCore.create(projectEJB), ejbModuleCpEntry);		
			
		 String pathEJB = Activator.getDefault().getPreferenceStore()
			.getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
			+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EJB;
		String path =  projectEJB.getLocation().toString() + "//.settings";
		ProjectWorker.copyFile(pathEJB, path, "org.eclipse.jdt.ui.prefs", context);
		
		//ProjectWorker.copyFile(pathEJB, path, "oracle.eclipse.tools.weblogic.syslib.xml", context);
		//INCLUIR MODULES Y MODULES_EXTRA EN LA CONFIGURACIÓN DE Weblogic System Library del proyecto
		ProjectWorker.createFileTemplate(pathEJB, path, "oracle.eclipse.tools.weblogic.syslib.xml", context);
						
		path = projectEJB.getLocation().toString();
		ProjectWorker.createFileTemplate(pathEJB, path +"\\ejbModule\\META-INF\\", "ejb-jar.xml", context); 

				
		// Añade la configuración de PMD
		path =  projectEJB.getLocation().toString();
		ProjectWorker.copyFile(pathEJB, path, ".pmd", context);
	
		// Añade la configuración de checkstyle
		ProjectWorker.copyFile(pathEJB, path, ".checkstyle", context);
	
		// Añade el runTime de Oracle
		try {
			// Añade el nature de PMD al proyecto
			//ProjectUtilities.addNatureToProject(projectEJB, "net.sourceforge.pmd.runtime.pmdNature");
			ProjectUtilities.addNatureToProject(projectEJB, "net.sourceforge.pmd.eclipse.plugin.pmdNature");
		
		} catch (Exception e) {
			consola.println("No tiene Plugin de PMD instalado en el Eclipse!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
		}
		
		try {
			// Añade el nature de checkstyle al proyecto  
			ProjectUtilities.addNatureToProject(projectEJB, "net.sf.eclipsecs.core.CheckstyleNature");
		} catch (Exception e) {
			consola.println("No tiene Plugin de Checkstyle instalado en el Eclipse!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
		}
		
		try {
			ProjectWorker.addEjbModuleEARApplication(locationText+"EAR\\EarContent\\META-INF\\",new File(locationText+"EAR\\EarContent\\META-INF\\application.xml"),context );
		
		} catch (Exception e) {
			consola.println("Error en la actualización del application.xml!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
		}
		
		Set<IRuntime> runtimes = RuntimeManager.getRuntimes();
		for (Iterator<IRuntime> iterator = runtimes.iterator(); iterator.hasNext();) {
			IRuntime runtime = (IRuntime) iterator.next();
			
			if (runtime.getName().contains("WebLogic") && runtime.supports(ProjectFacetsManager.getProjectFacet("jst.ejb"))){
				fpEJB.addTargetedRuntime(runtime, new SubProgressMonitor(monitor,1));
			}
		}
		
		try {
			// Facets de weblogic
			fpEJB.installProjectFacet(ProjectFacetsManager.getProjectFacet("wls.ejb").getVersion("10.3.1"), null, null);
		} catch (Exception e) {
			consola.println("No tiene OEPE con WebLogic instalado para el EJB!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
			page.setMessage(
					"No tiene OEPE con WebLogic instalado para el EJB!",
					IMessageProvider.ERROR);
		}
		
		fpEJB.setFixedProjectFacets(new HashSet<IProjectFacet>(Arrays.asList(new IProjectFacet[]{
				ProjectFacetsManager.getProjectFacet("jst.ejb"),
				ProjectFacetsManager.getProjectFacet("jst.java")
			})));
		
		//Organiza las librerias que debe tener un proyecto EJB
		ProjectWorker.organizeEJBLibraries(projectEJB, context, monitor);
		
		
		return projectEJB;
	}
	
	



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
	 * Genera el texto que se sacará de sumario con las operaciones realizadas.
	 * @param context - contexto con la información del los proyectos
	 * @return - texto del sumario
	 */
	private String createSummary(Map<String, Object> context, boolean ejbCheck){
		StringBuffer summaryText = new StringBuffer();
		
		summaryText.append("\n\n- Proyectos generados para la aplicación ");
		summaryText.append(context.get(Constants.CODAPP_PATTERN));
		summaryText.append(": \n\t - ");
		summaryText.append(context.get(Constants.EAR_NAME_PATTERN));
		summaryText.append("\n\t - ");
		summaryText.append(context.get(Constants.WAR_NAME_PATTERN));
		summaryText.append("\n\t - ");
		summaryText.append(context.get(Constants.EARCLASSES_NAME_PATTERN));
		if (ejbCheck){
			summaryText.append("\n\t - ");
			summaryText.append(context.get(Constants.EJB_NAME_PATTERN));
		}
		summaryText.append("\n\t - ");
		summaryText.append(context.get(Constants.STATICS_PATTERN));
		summaryText.append("\n\t - ");
		summaryText.append(context.get(Constants.CONFIG_NAME_PATTERN));
		summaryText.append("\n - Crea referencias entre los proyectos.");
		summaryText.append("\n - Recupera librerías desde maven.");
		
		return summaryText.toString();
	}
	
}