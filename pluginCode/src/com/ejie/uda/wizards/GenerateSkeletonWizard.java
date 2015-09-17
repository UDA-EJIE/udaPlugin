package com.ejie.uda.wizards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.DirectoryScanner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ejie.uda.Activator;
import com.ejie.uda.exporters.utils.ControllerUtils;
import com.ejie.uda.exporters.utils.SkeletonUtils;
import com.ejie.uda.exporters.utils.StubClassUtils;
import com.ejie.uda.exporters.utils.UdaDynamicProxy;
import com.ejie.uda.operations.ProjectWorker;
import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;
/**
 * 
 * Clase encargada de generar todo lo necesario para el asistente "Generar código para EJB Servidor"
 *
 */
public class GenerateSkeletonWizard extends Wizard implements INewWizard {
	private GenerateSkeletonWizardPage page;
	private ISelection selection;
	private String errorMessage;
	private String summary;
	private static final Class<?>[] parameters = new Class[]{URL.class};
	private static final String[] genericMethods = {"public final boolean $Proxy0.equals(java.lang.Object)", "public final java.lang.String $Proxy0.toString()", "public final int $Proxy0.hashCode()"};
	private SkeletonUtils skeletonUtils= new SkeletonUtils();
	private ControllerUtils ctrUtils = new ControllerUtils();
	  private static StubClassUtils getStub= new StubClassUtils();

	// Logs en la consola
	private ConsoleLogger consola = new ConsoleLogger(Constants.CONSOLE_NAME);
	
	/**
	 * Constructor
	 */
	public GenerateSkeletonWizard() {
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
		page = new GenerateSkeletonWizardPage(selection);
		addPage(page);
		
	}

	/**
	 * Recupera los datos de la ventana e inicia el tratamiento del plugin
	 * 
	 * @return true si la ejecución es correcta, false ecc.
	 */
	public boolean performFinish() {

		// Recupera la información de la ventana
		final String ejbProyName = page.getEjbNameText();
		final String ejbProyPath = page.getEjbPath();
		final String serviceName = page.getServiceNameOnlyText();
		final String jndiName = page.getJndiNameText();
		final String earClassesProyPath = page.getProjectEARClassesLocation();
		final String earProyPath = page.getProjectEARLocation();
		final String packageName= page.getPackageName();
		final String workspacePath= page.getWorkspacePath();
		final IProject ejbProject=page.getProjectEJB();
		final IProject earClasses=page.getEarClassesProject(ejbProject);
		String pathTemplates = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH);
		
		//Validación de configuración de plantillas
		if (Utilities.isBlank(pathTemplates)) {
			page.setMessage("No está configurada la ruta de las plantillas en Window > Preferences > UDA",
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
		
		// Validación de campo obligatorio y su contenido
		if (ejbProyName.equals("")) {
			page.setMessage("Es obligatorio elegir un proyecto EJB", IMessageProvider.ERROR);
			return false; 
		}
		
		// Validación de campo obligatorio y su contenido
		if (serviceName.equals("")) {
			page.setMessage("Es obligatorio elegir un servicio", IMessageProvider.ERROR);
			return false; 
		}
		
		// Validación de campo obligatorio y su contenido
		if (jndiName.equals("") ) {
			page.setMessage("Es obligatorio introducir el JNDI", IMessageProvider.ERROR);
			return false; 
		}
		if (!Utilities.validateJNDIText(jndiName)) {
			page.setMessage("Carácteres no válidos en JNDI", IMessageProvider.ERROR);
			return false; 
		}
		// Validación de tecnologias JPA 2.0 o Spring JDBC en los proyectos seleccionados
		 final boolean  isJPAEARClasses ;
		
		if (earClasses != null){
			isJPAEARClasses = isJPAProjectEARClasses(earClasses);
		}else{
			isJPAEARClasses = false;
		}
		//comprobamos si quieres machacar o no
		 if (!page.getConfirmation()){
			 //le ha dado que si al check de generar
			// MessageDialog.openError(getShell(), "Error", "Operación cancelada por el usuario");
			 return false;
		 }
		
		// Inicia la ejecución de proceso
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				try {
					monitor.beginTask("Compilando el proyecto", 2);
					earClasses.build(IncrementalProjectBuilder.CLEAN_BUILD,null);
					earClasses.build(IncrementalProjectBuilder.FULL_BUILD,null);
					ProjectWorker.refresh(earClasses);
					monitor.beginTask("Generando Skeleton", 2);
					// Inicia tratamiento del plugin
					
					doFinish(monitor, ejbProyName,serviceName,jndiName,earClassesProyPath,earProyPath,packageName,workspacePath,ejbProyPath,isJPAEARClasses);
					ProjectWorker.refresh(ejbProject);
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
			MessageDialog.openError(getShell(), "Error", "Error en la generación del código: " + errorMessage);
		}
		return true;
	}

	
	/**
	 * Genera los ejbs seleccionados
	 * 
	 * 
	 * @param monitor - monitor de progreso del plugin
	 * @param ejbProyName - nombre proyecto EJB a generar
	 * @param serviceName - nombre del servicio seleccionado
	 * @param jndiName - nombre del jndi
	 * @param earClassesProyPath - path del proyecto EARClasses
	 * @param earProyPath - path del proyecto EAR
	 * @param isJPAEARClasses - indica si el proyecto EARClasses es JPA
	 * @throws Exception
	 */
	private void doFinish(IProgressMonitor monitor,String ejbProyName,String serviceName,String jndiName,
			String earClassesProyPath,String earProyPath, String packageName, String workspacePath,
			String ejbProyPath, boolean isJPAEARClasses) throws Exception {
		
		consola = ConsoleLogger.getDefault();
		consola.println("UDA - INI", Constants.MSG_INFORMATION);
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(Constants.EJB_NAME_PATTERN, ejbProyName);
		context.put(Constants.PACKAGE_PATTERN, packageName);
		context.put("serviceName", serviceName);
		context.put("jndiName",jndiName);
		context.put("isJpa",isJPAEARClasses);
			
		// Recupera el Workspace para crear los proyectos
		String path = earClassesProyPath + Constants.PREF_DEFAULT_BUILD_PATH;
		List<String[]> metodos = getClasses(packageName, serviceName,path,earProyPath,workspacePath);
		createSkeletonRemoting(metodos,ejbProyPath,context);
		createSkeleton(metodos,ejbProyPath,context);
		editXMLFile(ejbProyPath+ "/ejbModule/META-INF", new File(ejbProyPath+ "/ejbModule/META-INF/weblogic-ejb-jar.xml"),context);
		this.summary = createSummary(context);
		
		consola.println(" EJB " + context.get("serviceName") + " del proyecto " + ((String)ejbProyName), Constants.MSG_INFORMATION);
		consola.println(" generado en " + ((String) context.get(Constants.PACKAGE_PATTERN)).replace(".service", ".remoting"), Constants.MSG_INFORMATION);
		consola.println("UDA - END", Constants.MSG_INFORMATION);
	}

	/**
	 * Genera el texto que se sacará de sumario con las operaciones realizadas.
	 * @param context - contexto con la información del los proyectos
	 * @return - texto del sumario
	 */
	private String createSummary(Map<String, Object> context){
		StringBuffer summaryText = new StringBuffer();
		String packageRemoting= ((String) context.get(Constants.PACKAGE_PATTERN)).replace(".service", ".remoting");
		summaryText.append("\n\n- EJB generado para ");
		summaryText.append(context.get("serviceName"));
		summaryText.append(" en:  ");
		summaryText.append(packageRemoting);
		
		return summaryText.toString();
	}

	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}

    public static void addFile(File f) throws IOException {
    	addURL(f.toURI().toURL());
    }

    public static void addURL(URL u) throws IOException {
    	URLClassLoader sysloader = (URLClassLoader) ClassLoader
    	.getSystemClassLoader();
    	Class<?> sysclass = URLClassLoader.class;
    	try {
    		Method method = sysclass.getDeclaredMethod("addURL", parameters);
    		method.setAccessible(true);
    		method.invoke(sysloader, new Object[] { u });
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new IOException(
    				"Error, could not add URL to system classloader");
    	}
    }

    /**
     * Obtiene las classes existentes
     * @param packageName - nombre paquete
     * @param fileName - nombre del fichero
     * @param earClassesPath - path del proyecto EARClasses
     * @param earPath - path del proyecto EAR
     * @param workspacePath - ruta del workspace
     * @return - lista de nombres de clases existentes
     */
    public static  List<String[]> getClasses(String packageName, String fileName, String earClassesPath, String earPath,
    		String workspacePath) throws IOException,
    		SecurityException, ClassNotFoundException,
    		IllegalArgumentException, InstantiationException,
    		IllegalAccessException, InvocationTargetException,
    		NoSuchMethodException {
    	fileDelete(workspacePath+"/.metadata");
    	org.apache.tools.ant.taskdefs.Jar jarMaker = new org.apache.tools.ant.taskdefs.Jar();
    	DirectoryScanner ds = new DirectoryScanner();
    	ds.setBasedir(earClassesPath);
    	String[] includes = { "**" };
    	// String[] excludes = {"modules/*/**"};
    	// ds.setExcludes(excludes);
    	ds.setIncludes(includes);
    	ds.scan();

    	String[] files = ds.getIncludedFiles();
    	for (int i = 0; i < files.length; i++) {
    		jarMaker.add(ds.getResource((files[i])));
    	}
    	long secuentialUda = System.currentTimeMillis();
    	String udaJar = workspacePath+ "/.metadata/"+Constants.PREF_DEFAULT_UDA_JAR+ secuentialUda +".jar"; 
    	jarMaker.setDestFile(new File(udaJar));
    	jarMaker.execute();

    	addFile(udaJar);
    	String x38JarName =Utilities.findFileStartsLike(new File(earPath + Constants.PREF_DEFAULT_EAR_LIBS),Constants.PREF_DEFAULT_X38_LIBS);
    	if (x38JarName.equals("")){
    		throw new IOException(
    				"La libreria x38ShLibClasses no está instalada en: "+earPath + Constants.PREF_DEFAULT_EAR_LIBS + "/"  + x38JarName);
    	}
    	addFile(earPath + Constants.PREF_DEFAULT_EAR_LIBS + "/"  + x38JarName);
    	Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(
    			packageName + "." + fileName);

    	Map<?, ?> identity = new HashMap<Object, Object>();
    	Proxy someServiceImpl = null;
    	List<String[]> list = new ArrayList<String[]>();
    	boolean bol = true;

    	try {
    		someServiceImpl = (Proxy) UdaDynamicProxy.newInstance(identity, new Class[]
    		                                                                          { clazz },ClassLoader.getSystemClassLoader());
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		Method[] methods = someServiceImpl.getClass().getDeclaredMethods();
    		for(Method met:methods){
    			for(String str:genericMethods){
    				if(str.equalsIgnoreCase(met.toGenericString())){
    					bol = false;
    					break;
    				}            		
    			}
    			if (bol){
    				//String str = met.toGenericString();
    				//String str2 = str.replace("$Proxy0.", "");
    				String[] auxiliar = getMetodCaracteristics(met);
    				if (auxiliar!=null){
    					list.add(auxiliar);
    				}	
    			}
    			bol = true;
    		}
    	}
    	return list;
    }

    /**
     * Obtiene las caracterésticas de un método
     * @param method - método
     * @return - características del método utilizadas en la generación del skeleton
     */
    @SuppressWarnings("unchecked")
    public static String[] getMetodCaracteristics(Method method) {
    	String methodName= method.getName().replace("$Proxy0.", "");
    	if (!methodName.equals("toString") && !methodName.equals("hashCode") && !methodName.equals("equals") ){
    		method.getReturnType().getName().equals("void"); 
    		String tipoRetorno = "";
    		tipoRetorno =  method.getReturnType().getName();
    		String tipoSimpleName = "";
    		if (method.getReturnType().isArray()){
    			tipoRetorno = tipoRetorno.substring(2,tipoRetorno.length()-1);
    			tipoSimpleName = method.getReturnType().getName().substring(method.getReturnType().getName().lastIndexOf(".")+1,method.getReturnType().getName().length()-1)+"[]";
    		}else if (!method.getReturnType().isPrimitive() && !method.getReturnType().getName().equals("void")){
    			tipoSimpleName = method.getReturnType().getName().substring(method.getReturnType().getName().lastIndexOf(".")+1,method.getReturnType().getName().length());
    		} else {
    			tipoSimpleName= method.getReturnType().getName();
    		}

    		//Parametros
    		String parametros = "";
    		for (Class<Type> tipo : (Class<Type>[]) method.getGenericParameterTypes()) {
    			parametros = parametros + tipo.getCanonicalName() +";";
    		}

    		//Excepciones
    		String excepciones = "";
    		String fullExcepciones = "";
    		for (Class<Type> excepcion : (Class<Type>[]) method.getExceptionTypes()) {
    			excepciones = excepciones + excepcion.getCanonicalName().substring(excepcion.getCanonicalName().lastIndexOf(".")+1,excepcion.getCanonicalName().length()) + ", ";
    			fullExcepciones =  fullExcepciones + excepcion.getCanonicalName() + ";";
    		}
    		if (!excepciones.equals("")){
    			excepciones = excepciones.substring(0, excepciones.length()-2);
    		}

    		String[] lista={methodName,tipoRetorno,tipoSimpleName,parametros,excepciones,fullExcepciones};
    		return lista;
    	}else{
    		return null;
    	}
    }

    /**
     * Genera el skeleton del método indicado
     * @param methods - Lista de métodos
     * @param ejbProyPath - path proyecto EJB
     * @param context - Contexto Freemarker
     * @return - devuelve el resultado de la ejecución
     */
    public boolean createSkeleton(List<String[]> methods,
    		String ejbProyPath,Map<String, Object> context){
    	String packageName= (String) context.get(Constants.PACKAGE_PATTERN);
    	String serviceName=(String) context.get("serviceName");
    	String packageNameRemoting=packageName.replace(".service", ".remoting");
    	context.put("skeletonUtils", skeletonUtils);
    	context.put("ctrUtils", ctrUtils);
    	context.put("stubUtils", getStub);

    	context.put("methods", methods);
    	context.put("packageNameSkeleton", packageName.replace(".service", ".remoting"));


    	String pathTemplatesEjb = Activator.getDefault().getPreferenceStore()
    	.getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
    	+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EJB;
    	try {
    		ProjectWorker.createFileTemplateRename(pathTemplatesEjb+"/server", ejbProyPath+ "/ejbModule/"+packageNameRemoting.replace(".", "/"),"skeleton", serviceName + "Skeleton.java",context);
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }	
    /**
     * Genera el skeleton Remoting del método indicado
     * @param methods - Lista de métodos
     * @param ejbProyPath - path proyecto EJB
     * @param context - Contexto Freemarker
     * @return - devuelve el resultado de la ejecución
     */
    public boolean createSkeletonRemoting(List<String[]> methods,
    		String ejbProyPath,Map<String, Object> context){
    	String packageName= (String) context.get(Constants.PACKAGE_PATTERN);
    	String serviceName=(String) context.get("serviceName");
    	String packageNameRemoting=packageName.replace(".service", ".remoting");
    	context.put("skeletonUtils", skeletonUtils);
    	context.put("ctrUtils", ctrUtils);
    	context.put("methods", methods);
    	context.put("packageNameSkeleton",packageNameRemoting);
    	context.put("serviceNameDecapitalized", ControllerUtils.stringDecapitalize(serviceName));

    	String pathTemplatesEjb = Activator.getDefault().getPreferenceStore()
    	.getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
    	+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EJB;
    	try {
    		ProjectWorker.createFileTemplateRename(pathTemplatesEjb+"/server", ejbProyPath+ "/ejbModule/"+packageNameRemoting.replace(".", "/"),"skeletonRemote", serviceName + "SkeletonRemote.java",context);
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }	

    /**
     * Indica si el proyecto EARClasses es de persistencia JPA
     * @param project - proyecto
     * @return - resultado de persistencia JPA
     */
    private boolean isJPAProjectEARClasses(IProject project){
    	boolean isJPA = false;
    	if (project != null){
    		IFile persistence = project.getFile("/src/META-INF/udaPersistence.xml");

    		// Verifica si existe el fichero udaPersistence.xml
    		// y busca en su contenido que tipo de tecnologia de persistencia tiene 
    		if (persistence.exists()){
    			isJPA = true;
    		}	
    	}

    	return isJPA;
    }
    
    /**
     * Borrado de fichero
     * @param fileName - nombre Fichero
     */
    private static void fileDelete(String fileName){
    	File f = new File(fileName);

    	if (f.exists()){	
    		if (f.isDirectory()) {
    			String[] files = f.list();
    			if (files.length > 0){
    				List<?> filesList= Arrays.asList(files);
    				Iterator<?> it = filesList.iterator();
    				while (it.hasNext()){
    					String name=(String) it.next();
    					if (name.startsWith(Constants.PREF_DEFAULT_UDA_JAR) && name.endsWith(".jar")){
    						fileDelete(fileName+"/"+name);
    					}
    				}
    			}	
    		}
    		f.delete();
    	} 
    }
    
    /**
     * Edición de fichero XML
     * @param path - path Fichero
     * @param xmlFile - fichero XML
     * @param context - contexto de Freemarker
     */
    private void editXMLFile(String path, File xmlFile,Map<String, Object> context){
    	try {
    		if (xmlFile.exists()) {
    			// Crea la instancia de DocumentBuilderFactory
    			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    			//factory.setValidating(true);
    			factory.setNamespaceAware(true);
    			factory.setIgnoringElementContentWhitespace(true);

    			// recupera DocumentBuilder
    			DocumentBuilder docBuilder = factory.newDocumentBuilder();

    			// utiliza el fichero xml
    			Document doc = docBuilder.parse(xmlFile);	
    			doc.getDoctype();
    			doc.setXmlStandalone(true);

    			// recoge el elemento raiz
    			Element rootElement = doc.getDocumentElement();
    			// crea un elemento nuevo de definition
    			Element definitionElementwlsEnterprise = doc.createElement("wls:weblogic-enterprise-bean");
    			// Añade atributos
    			Element definitionEjbname = doc.createElement("wls:ejb-name");
    			definitionEjbname.setTextContent((String) context.get("serviceName")+"Skeleton");
    			Element definitionStateles= doc.createElement("wls:stateless-session-descriptor");
    			Element definitionPool= doc.createElement("wls:pool");

    			Element definitionMax= doc.createElement("wls:max-beans-in-free-pool");
    			definitionMax.setTextContent("1");
    			Element definitionInit= doc.createElement("wls:initial-beans-in-free-pool");
    			definitionInit.setTextContent("1");
    			Element definitionIdle= doc.createElement("wls:idle-timeout-seconds");
    			definitionIdle.setTextContent("60000");

    			definitionPool.appendChild(definitionMax);
    			definitionPool.appendChild(definitionInit);
    			definitionPool.appendChild(definitionIdle);
    			definitionStateles.appendChild(definitionPool);
    			definitionElementwlsEnterprise.appendChild(definitionEjbname);
    			definitionElementwlsEnterprise.appendChild(definitionStateles);
    			rootElement.appendChild(definitionElementwlsEnterprise);

    			// configura transformer
    			TransformerFactory transfac = TransformerFactory.newInstance();
    			transfac.setAttribute("indent-number", 4);

    			Transformer trans = transfac.newTransformer();
    			trans.setOutputProperty(OutputKeys.INDENT, "yes");

    			// genera una cadena con el arbol xml
    			StringWriter sw = new StringWriter();
    			StreamResult result = new StreamResult(sw);
    			DOMSource source = new DOMSource(doc);

    			trans.transform(source, result);
    			String xmlString = sw.toString();

    			OutputStream f0;
    			byte buf[] = xmlString.getBytes();
    			f0 = new FileOutputStream(path + "/" + xmlFile.getName());
    			for (int i = 0; i < buf.length; i++) {
    				f0.write(buf[i]);
    			}
    			f0.close();
    			buf = null;
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

}