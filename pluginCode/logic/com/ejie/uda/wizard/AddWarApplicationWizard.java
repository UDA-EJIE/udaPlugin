package com.ejie.uda.wizard;

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
import com.ejie.uda.operations.RVCopyWorker;
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
		final boolean examples = page.getExamplesCheck();
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
					doFinish(monitor, warName, projectEAR, radSpringJDBC, radJPA, idSecurity, layout, appType, category, examples, languages, defaultLanguage, languagesWithoutQuotes);
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
			boolean examples, String languages, String defaultLanguage, String languagesWithoutQuotes) throws Exception {
		
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
		context.put(Constants.RUP_EXAMPLE, examples);
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
		IProject projectWAR = createProjectWAR(root, radJPA, layout, appType, category, examples, context, monitor);
		monitor.worked(1);
		consola.println("Proyecto WAR generado.", Constants.MSG_INFORMATION);
		
		// Relaciona el proyecto WAR al EAR
		monitor.setTaskName("Enlazando el WAR a la aplicación...");
		ProjectWorker.createEARDependency(projectEAR, projectWAR);
		monitor.worked(1);
		consola.println("Proyecto WAR enlazado a la aplicación.", Constants.MSG_INFORMATION);
		
		// Actualiza la configuración de la aplicación
		setWarInConfigProject(context);
		
		// Actualiza lso estáticos de la aplicación
		setWarInStaticsProject(context);
		
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
			String layout, String appType, String category, boolean examples,
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
		// Añade la carpeta de código text a petición de EJIE
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
			// Añade el nature de Checkstyle al proyecto  
			ProjectUtilities.addNatureToProject(projectWAR, "net.sf.eclipsecs.core.CheckstyleNature");
			
		} catch (Exception e) {
			consola.println("No tiene Plugin de Checkstyle instalado en el Eclipse!", Constants.MSG_ERROR);
			consola.println("Error: " + e.getMessage(), Constants.MSG_ERROR);
		}
		
		// Creación de layout según Tipo de aplicación, Disposición y Categoría
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
		
		// Crear ejemplo de RUP si marcado en pantalla
		if (examples){
			path = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/views");
			ProjectWorker.copyFile(pathWar, path, "welcomeRUP.jsp", context);
			
			String pathExamples = pathWar + "\\rup";
			String pathDestination = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/layouts/rup");
			RVCopyWorker.copyDirectoryTemplate(new File(pathExamples), new File(pathDestination), context);
			
			pathExamples = pathWar + "\\ejemplos";
			pathDestination = ProjectWorker.createGetFolderPath(projectWAR, "WebContent/WEB-INF/views/ejemplos");
			RVCopyWorker.copyDirectoryTemplate(new File(pathExamples), new File(pathDestination), context);
			
			pathExamples = pathWar + "\\ejemplosControllers";
			path = ProjectWorker.createGetFolderPath(projectWAR, "src/com");
			path = ProjectWorker.createGetFolderPath(projectWAR, "src/com/ejie");
			path = ProjectWorker.createGetFolderPath(projectWAR, "src/com/ejie/" + context.get(Constants.CODAPP_PATTERN));
			path = ProjectWorker.createGetFolderPath(projectWAR, "src/com/ejie/" + context.get(Constants.CODAPP_PATTERN) + "/control");			
			pathDestination = ProjectWorker.createGetFolderPath(projectWAR, "src/com/ejie/" + context.get(Constants.CODAPP_PATTERN) + "/control/ejemplos");
			RVCopyWorker.copyDirectoryTemplate(new File(pathExamples), new File(pathDestination), context);
		}
		
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

			String path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/" + (String)context.get(Constants.CODAPP_PATTERN) + "/scripts");
			ProjectWorker.createFileTemplate(pathStatics, path, "layoutLoader.js", context, "layoutLoader" + Utilities.camelCase((String)context.get(Constants.WAR_NAME_SHORT_PATTERN)+".js"));
			
			path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/" +(String)context.get(Constants.CODAPP_PATTERN) + "/resources");
			String languages = (String) context.get(Constants.LANGUAGES_PATTERN);
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
			
		}
		// Refresca el proyecto
		ProjectWorker.refresh(projectStatics);
		
	}
}