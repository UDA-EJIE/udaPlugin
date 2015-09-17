package com.ejie.uda.wizards;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
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
import com.ejie.uda.operations.ProjectWorker;
import com.ejie.uda.operations.PropertiesWorker;
import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

/**
 * Clase encargada de generar todo lo necesario para el asistente "Añadir Proyecto WAR"
 * 
 */
public class AddWarApplicationWizard extends Wizard implements INewWizard {

	private AddWarApplicationWizardPage page;
	private ISelection selection;
	private String errorMessage;
	private String summary;
	// Logs en la consola
	private ConsoleLogger consola = new ConsoleLogger(Constants.CONSOLE_NAME);
	
	/**
	 * Constructor
	 */
	public AddWarApplicationWizard() {
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
		page = new AddWarApplicationWizardPage(selection);
		addPage(page);
	}

	/**
	 * Recupera los datos de la ventana e inicia el tratamiento del plugin
	 * 
	 * @return true si la ejecución es correcta, false ecc.
	 */
	public boolean performFinish() {
		// Recupera la información de la ventana
		final String warNameText = page.getWarNameText();
		final String warName = page.getWarCodName();
		final IProject projectEAR = page.getProjectEAR();
		final String idSecurity = "";
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
		if (warNameText == null || "".equals(warNameText)) {
			page.setMessage("Campo 'Nombre del WAR' obligatorio",
					IMessageProvider.ERROR);
			return false;
		}else if (!Utilities.validateWARText(warNameText)){
			page.setMessage("Caracteres no válidos para en campo 'Nombre del WAR'",
					IMessageProvider.ERROR);
			return false;
		}else if(ResourcesPlugin.getWorkspace().getRoot().getProject(warName + Constants.WAR_NAME).exists()){
				page.setMessage("Ya existe un proyecto WAR con este nombre.",
						IMessageProvider.ERROR);
				return false;
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
		
		page.setMessage("Este Wizard genera un nuevo WAR y lo añade a un EAR existente", IMessageProvider.NONE);	

		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				try {
					monitor.beginTask("Añadiendo WAR a la aplicación", 2);
					// Inicia tratamiento del plugin
					doFinish(monitor, warName, projectEAR, radSpringJDBC, radJPA, idSecurity, layout, appType, category, languages, defaultLanguage, languagesWithoutQuotes);
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
	 * Genera los proyectos xxEARClasses, xxxWAR, xxxEAR, los enlaza y lanza una
	 * tarea Ant para descargar las dependencias desde Maven
	 * 
	 * @param appCode - código de aplicación
	 * @param layoutType - layout de presentación seleccionado
	 * @param monitor - monitor de progreso del plugin
	 * @param radSpringJDBC - radio que indica si la capa de datos será con tecnología Spring-JDBC
	 * @param radJPA - radio que indica si la capa de datos será con tecnologia JPA 2.0
	 * @throws Exception
	 */
	private void doFinish(IProgressMonitor monitor, String warName, IProject projectEAR, boolean radSpringJDBC,
			boolean radJPA, String idSecurity, String layout, String appType, String category, 
			String languages, String defaultLanguage, String languagesWithoutQuotes) throws Exception {
		
		consola = ConsoleLogger.getDefault();
		consola.println("UDA - INI", Constants.MSG_INFORMATION);
		
		String appCode = getCodAppFromEAR(projectEAR);

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
		context.put(Constants.WAR_NAME_PATTERN, warName + Constants.WAR_NAME);
		context.put(Constants.EAR_NAME_PATTERN, appCode + Constants.EAR_NAME);
		context.put(Constants.EARCLASSES_NAME_PATTERN, appCode + Constants.EARCLASSES_NAME);
		context.put(Constants.RADJPA_PATTERN, radJPA);
		context.put(Constants.RADSPRINGJDBC_PATTERN, radSpringJDBC);
		context.put(Constants.ID_SECURITY, idSecurity);
		context.put(Constants.CODROLE_PATTERN, "UDA");
		context.put("codroleAux", "hasRole('ROLE_UDA')");
		context.put("listaClases", "");
		context.put(Constants.LAYOUT_PATTERN, layout.toLowerCase());
		context.put(Constants.LANGUAGES_PATTERN, languages);
		context.put(Constants.LANGUAGES_WITHOUT_QUOTES_PATTERN, languagesWithoutQuotes);
		context.put(Constants.DEFAULT_LANGUAGE_PATTERN, defaultLanguage);
		context.put(Constants.WAR_NAME_SHORT_PATTERN, warName);
		context.put(Constants.PREF_EJIE_PATTERN, Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE));		

		// Recupera el Workspace para crear los proyectos
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		// Crea el proyecto de xxxWAR
		monitor.setTaskName("Creando el proyecto WAR...");
		IProject projectWAR = createProjectWAR(root, radJPA, layout, appType, category, context, monitor);
		monitor.worked(1);
		consola.println("Proyecto WAR generado.", Constants.MSG_INFORMATION);
		
		// Relaciona el proyecto WAR al EAR
		monitor.setTaskName("Enlazando el WAR a la aplicación...");
		ProjectWorker.createEARDependency(projectEAR, projectWAR);
		monitor.worked(1);
		consola.println("Proyecto WAR enlazado a la aplicación.", Constants.MSG_INFORMATION);
		
		// Actualiza la configuración de la aplicación
		setWarInConfigProject(context);
		consola.println("Generada la configuración del WAR.", Constants.MSG_INFORMATION);
		
		// Actualiza lso estáticos de la aplicación
		setWarInStaticsProject(context);
		consola.println("Generada el contenido estático del WAR.", Constants.MSG_INFORMATION);
		
		// Actualiza los proyectos al finalizar la ejecución
		ProjectWorker.refresh(projectWAR);
		ProjectWorker.refresh(projectEAR);
		
		this.summary = createSummary(context);

		consola.println("UDA - END", Constants.MSG_INFORMATION);
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
			String layout, String appType, String category,
			Map<String, Object> context, IProgressMonitor monitor) throws Exception {

		String path = "";
		String pathWar = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
				+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_WAR;

		// Crea el proyecto de xxxWAR
		IProject projectWAR = root.getProject((String)context.get(Constants.WAR_NAME_PATTERN));
		projectWAR.create(null);
		projectWAR.open(null);

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
		
		//PMD y CheckStyle
		path =  projectWAR.getLocation().toString();
		ProjectWorker.copyFile(pathWar, path, ".pmd", context);
		ProjectWorker.copyFile(pathWar, path, ".checkstyle", context);
		
		//Ruta para las plantillas que se deben procesar (.ftl)
		String pathFileTemplate = projectWAR.getLocation().toString();
	
		//Settings
		path =  projectWAR.getLocation().toString() + "/.settings";
		ProjectWorker.copyFile(pathWar, path, ".settings/org.eclipse.jdt.ui.prefs", context);
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, ".settings/oracle.eclipse.tools.weblogic.syslib.xml", context);
		ProjectWorker.copyFile(pathWar, path, ".settings/oracle.eclipse.tools.webtier.ui.prefs", context);		
		if (radJPA){
			//Fichero encargado de indicar que el proyecto tendrá tecnologia JPA 2.0
			ProjectWorker.copyFile(pathWar, path, ".settings/com.ejie.uda.xml", context);
		}
		
		//WEB
		ProjectWorker.createGetFolderPath(projectWAR, "WebContent");
		ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF");
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF");
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/weblogic.xml", context);
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/web.xml", context);
		
		//.inc
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/includeTemplate.inc", context);
		
		//Spring
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/spring");
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/spring/app-config.xml", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/spring/log-config.xml", context);
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/spring/mvc-config.xml", context);
		context.put("listaClases", "");
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/spring/security-config.xml", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/spring/security-core-config.xml", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/spring/validation-config.xml", context);
		//context.put("listaClases", "");
		
		//i18n
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/resources");
		String languages = (String) context.get(Constants.LANGUAGES_PATTERN);
		if (languages.indexOf("es")!=-1){
			ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/resources/xxxWarName_es.properties", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_es.properties");
		}
		if (languages.indexOf("eu")!=-1){
			ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/resources/xxxWarName_eu.properties", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_eu.properties");
		}
		if (languages.indexOf("en")!=-1){
			ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/resources/xxxWarName_en.properties", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_en.properties");
		}
		if (languages.indexOf("fr")!=-1){
			ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/resources/xxxWarName_fr.properties", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_fr.properties");
		}
		
		//TLD
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/tld");
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/tld/c.tld", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/tld/fmt.tld", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/tld/security.tld", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/tld/spring.tld", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/tld/tiles-jsp.tld", context);
		
		//VIEWS
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/views");
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/views/welcome.jsp", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/views/error.jsp", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/views/accessDenied.jsp", context);
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/views/tiles.xml", context);
		
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
		
		//LAYOUTS
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/layouts");
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/layouts/errorTemplate.jsp", context);
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/base-includes.jsp", context);
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/breadCrumb.jsp", context);
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/language.jsp", context);
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/menu.jsp", context);

		if (Constants.LAYOUT_HORIZONTAL.equalsIgnoreCase(layout)){
			ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/template.jsp", context);
		}else if (Constants.LAYOUT_VERTICAL.equalsIgnoreCase(layout)){
			ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/templateVertical.jsp", context, "WebContent/WEB-INF/layouts/template.jsp");
		}else if (Constants.LAYOUT_MIXTO.equalsIgnoreCase(layout)){
			ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/templateMixto.jsp", context, "WebContent/WEB-INF/layouts/template.jsp");
			ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/menuMixto.jsp", context);
		}	
			
		if(Constants.APP_TYPE_INTRANET.equalsIgnoreCase(appType)){
			ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/layouts/footer.jsp", context);
			if(Constants.CATEGORY_DEPARTAMENTAL.equalsIgnoreCase(category)){
				ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/headerIntraDept.jsp", context, "WebContent/WEB-INF/layouts/header.jsp");
			} else {
				ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/headerIntraHori.jsp", context, "WebContent/WEB-INF/layouts/header.jsp");
			}
		} else {
			ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/layouts/footerInternet.jsp", context, "footer.jsp");
			ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/headerInternet.jsp", context, "WebContent/WEB-INF/layouts/header.jsp");
		}

		
		//LAYOUTS-INCLUDES
		path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/layouts/includes");
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/layouts/includes/rup.scripts.inc", context);
		ProjectWorker.copyFile(pathWar, path, "WebContent/WEB-INF/layouts/includes/rup.styles.inc", context);
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/includes/xxx.scripts.inc", context, "WebContent/WEB-INF/layouts/includes/"+context.get(Constants.CODAPP_PATTERN)+".scripts.inc");
		ProjectWorker.createFileTemplate(pathWar, pathFileTemplate, "WebContent/WEB-INF/layouts/includes/xxx.styles.inc", context, "WebContent/WEB-INF/layouts/includes/"+context.get(Constants.CODAPP_PATTERN)+".styles.inc");
		
		
		return projectWAR;
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
		
		summaryText.append("\n\n- Proyecto WAR generado para la aplicación ");
		summaryText.append(context.get(Constants.CODAPP_PATTERN));
		summaryText.append(": \n\t - ");
		summaryText.append(context.get(Constants.WAR_NAME_PATTERN));
		summaryText.append("\n - Enlaza el WAR a la aplicación.");
		
		return summaryText.toString();
	}
	
	/**
	 * Añade el WAR a la configuración de la aplicación
	 * @param context - variables de contexto
	 */
	private void setWarInConfigProject(Map<String, Object> context){
		
		String codApp = (String)context.get(Constants.CODAPP_PATTERN);
		String warName = (String)context.get(Constants.WAR_NAME_PATTERN);
		String layout = (String)context.get(Constants.LAYOUT_PATTERN);
		String defaultLanguage = (String)context.get(Constants.DEFAULT_LANGUAGE_PATTERN);
		
		IProject projectConfig = ResourcesPlugin.getWorkspace().getRoot().getProject(codApp + Constants.CONFIG_NAME);
		String path = projectConfig.getLocation().toString();
		PropertiesWorker configProperties = new PropertiesWorker(codApp + ".properties", path);

		if (configProperties != null && !Utilities.isBlank(warName) && !Utilities.isBlank(defaultLanguage) && !Utilities.isBlank(layout)){
			configProperties.writeProperty(warName + ".default.language", defaultLanguage);
			configProperties.writeProperty(warName + ".default.layout", layout);
			configProperties.saveProperties();//Guardar las propiedades en el fichero
		}
	}
	
	
	/**
	 * Añade los ficheros correspondientes del WAR al proyecto de Statics de la aplicación
	 * @param context - variables de contexto
	 * @throws Exception 
	 */
	private void setWarInStaticsProject(Map<String, Object> context) throws Exception{
		
		String codApp = (String)context.get(Constants.CODAPP_PATTERN);
		String warName = (String)context.get(Constants.WAR_NAME_PATTERN);
		
		IProject projectStatics = ResourcesPlugin.getWorkspace().getRoot().getProject(codApp + Constants.STATICS_NAME);
		String pathStatics = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH) + Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_STATICS;

		if (projectStatics != null && !Utilities.isBlank(warName)){

			//i18n
			String path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/" +(String)context.get(Constants.CODAPP_PATTERN) + "/resources");
			String languages = (String) context.get(Constants.LANGUAGES_PATTERN);
			if (languages.indexOf("es")!=-1){
				ProjectWorker.createFileTemplate(pathStatics, path, "aplic/resources/xxx.i18n_es.json", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_es.json");
			}
			if (languages.indexOf("eu")!=-1){
				ProjectWorker.createFileTemplate(pathStatics, path, "aplic/resources/xxx.i18n_eu.json", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_eu.json");
			}
			if (languages.indexOf("en")!=-1){
				ProjectWorker.createFileTemplate(pathStatics, path, "aplic/resources/xxx.i18n_en.json", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_en.json");
			}
			if (languages.indexOf("fr")!=-1){
				ProjectWorker.createFileTemplate(pathStatics, path, "aplic/resources/xxx.i18n_fr.json", context, context.get(Constants.WAR_NAME_SHORT_PATTERN) + ".i18n_fr.json");
			}
			
			path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/" + context.get(Constants.CODAPP_PATTERN) + "/scripts/" + context.get(Constants.WAR_NAME_SHORT_PATTERN));
			ProjectWorker.createFileTemplate(pathStatics, path, "_layoutLoader.js", context);
			
		}
		// Refresca el proyecto
		ProjectWorker.refresh(projectStatics);
		
	}
}