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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ejie.uda.Activator;
import com.ejie.uda.exporters.utils.ControllerUtils;
import com.ejie.uda.exporters.utils.SkeletonUtils;
import com.ejie.uda.exporters.utils.StubClassUtils;
import com.ejie.uda.operations.ProjectWorker;
import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;
/**
 * 
 * Clase encargada de generar todo lo necesario para el asistente "Generar código para EJB Cliente"
 *
 */
public class GenerateStubWizard extends Wizard implements INewWizard {
	
	private final static Logger logger = Logger.getLogger(GenerateStubWizard.class);
	
	private GenerateStubWizardPage page;
	private ISelection selection;
	private String errorMessage;
	private String summary;
	private static final Class<?>[] parameters = new Class[]{URL.class};
	private static final String[] genericMethods = {"equals(java.lang.Object)", "toString()", "hashCode()","remove() throws java.rmi.RemoteException,javax.ejb.RemoveException","getEJBHome() throws java.rmi.RemoteException","getPrimaryKey() throws java.rmi.RemoteException","getHandle() throws java.rmi.RemoteException","isIdentical(javax.ejb.EJBObject) throws java.rmi.RemoteException"};

	private static StubClassUtils getStub= new StubClassUtils();
	private SkeletonUtils skeletonUtils= new SkeletonUtils();
	private ControllerUtils ctrUtils = new ControllerUtils();

	protected static final String udaEjb = "SkeletonRemote";
	protected static final String geremuaEjb = "Home";
	
	// Logs en la consola
	private ConsoleLogger consola = new ConsoleLogger(Constants.CONSOLE_NAME);
	
	/**
	 * Constructor
	 */
	public GenerateStubWizard() {
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
		page = new GenerateStubWizardPage(selection);
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
		final String serviceName = page.getServiceNameText();
		final String jndiName = page.getJndiNameText();
		final String configProyPath = page.getProjectConfigLocation();
		final String earProyPath = page.getProjectEARLocation();
		final String packageName= page.getPackageName();
		final String workspacePath= page.getWorkspacePath();
		final IProject ejbProject=page.getProjectEJB();
		final IProject earClasses=page.getEarClassesProject(ejbProject);
		final String weblogicIp=page.getWeblogicIp();
		final String portIp=page.getPortIp();
		final String nameServer=page.getNameServer();		
		final String portServer=page.getPortServer();
		final String userServer=page.getUserServer();
		final String pwdServer=page.getPwdServer();
		final String ipServer=page.getIpServer();
		final String codapp = Utilities.getAppName(ejbProject.getName());
		final String earClassesProyPath=page.getProjectEARClassesLocation();
		
		final boolean isEjb3 = page.getRadEJBUda();
		
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
		if (weblogicIp.equals("") ) {
			page.setMessage("Es obligatorio introducir la dirección IP del servidor de despliegue", IMessageProvider.ERROR);
			return false; 
		}
		if (!weblogicIp.equals("") ){
				if (Utilities.validateNumber(weblogicIp.replace(".", "")) && !Utilities.validateIPAdderess(weblogicIp)){
					page.setMessage("El campo IP del servidor de despliegue no tiene una dirección IP válida", IMessageProvider.ERROR);
					return false;
				}	else if (!Utilities.validateHostName(weblogicIp)){
					page.setMessage("El campo IP del servidor de despliegue no tiene una dirección IP válida", IMessageProvider.ERROR);
					return false;
				}
		}
			
		if (portIp.equals("") ) {
			page.setMessage("Es obligatorio introducir el puerto del servidor de despliegue", IMessageProvider.ERROR);
			return false; 
		}
		if (!portIp.equals("") &&(portIp.length() > 4 ||!Utilities.validateNumber(portIp)) ){
			page.setMessage("El campo puerto del servidor de despliegue no es correcto", IMessageProvider.ERROR);
			return false;
		}
		if (nameServer.equals("") ) {
			page.setMessage("Es obligatorio introducir el nombre del servidor EJB", IMessageProvider.ERROR);
			return false; 
		}
		if (ipServer.equals("") ) {
			page.setMessage("Es obligatorio introducir la dirección IP del servidor EJB", IMessageProvider.ERROR);
			return false; 
		}
		if (!ipServer.equals("") ){
			if (Utilities.validateNumber(ipServer.replace(".", "")) && !Utilities.validateIPAdderess(ipServer)){
				page.setMessage("Dirección IP no válida en 'IP Servidor'", IMessageProvider.ERROR);
				return false;
			}	else if (!Utilities.validateHostName(ipServer)){
				page.setMessage("Dirección IP no válida en 'IP Servidor'", IMessageProvider.ERROR);
				return false;
			}
	    }		
		if (portServer.equals("") ) {
			page.setMessage("Es obligatorio introducir el puerto del servidor EJB", IMessageProvider.ERROR);
			return false; 
		}
		if (!portServer.equals("") &&(portServer.length() > 4 ||!Utilities.validateNumber(portServer)) ){
			page.setMessage("El campo puerto del servidor de EJB no es correcto", IMessageProvider.ERROR);
			return false; 
		}
		if (userServer.equals("") ) {
			page.setMessage("Es obligatorio introducir el usuario del servidor EJB", IMessageProvider.ERROR);
			return false; 
		}
		if (pwdServer.equals("") ) {
			page.setMessage("Es obligatorio introducir la contraseña del servidor EJB", IMessageProvider.ERROR);
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
					monitor.beginTask("Generando Stub", 1);
					// Inicia tratamiento del plugin
					
					doFinish(monitor, ejbProyName,serviceName,jndiName,configProyPath,earProyPath,packageName,workspacePath,ejbProyPath,isJPAEARClasses,isEjb3,weblogicIp,portIp,nameServer,ipServer,portServer,userServer,pwdServer,codapp,earClassesProyPath);
				
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
			
			MessageDialog.openInformation(getShell(), "Información", "!Las operaciones se han realizado con Exito!" + this.summary);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Error", "Error en la generación del código: " + errorMessage);
		}
		return true;
	}

	/**
	 * Genera el stub seleccionado
	 * 
	 * 
	 * @param monitor - monitor de progreso del plugin
	 * @param ejbProyName - nombre proyecto EJB a generar
	 * @param serviceName - nombre del servicio seleccionado
	 * @param jndiName - nombre del jndi
	 * @param configProyPath - path del proyecto config
	 * @param earProyPath - path del proyecto EAR
	 * @param packageName - nombre del paquete
	 * @param workspacePath - path del workspace
	 * @param ejbProyPath - path proyecto EJB
	 * @param isJPAEARClasses - indicativo si el proyecto EARClasses es JPA o no
	 * @param isEjb3 - indicador si el proyecto es EJB3
	 * @param weblogicIp - dirección IP o nombre máquina servidor despliegue
	 * @param portIp - puerto servidor despliegue
	 * @param nameServer - nombre servidor servicio a consumir
	 * @param ipServer - ip o nombre de máquina del servidor servicio a consumir
	 * @param portServer - puerto del servidor servicio a consumir
	 * @param userServer - usuario del servidor donde se encuentra el ejb a consumir
	 * @param pwdServer - password del servidor donde se encuentra el ejb a consumir
	 * @param codapp - código aplicación
	 * @param earClassesProyPath - path proyecto EARClasses
	 * @throws Exception
	 */
	private void doFinish(IProgressMonitor monitor,String ejbProyName,String serviceName,String jndiName,
			String configProyPath, String earProyPath, String packageName, String workspacePath,
			String ejbProyPath, boolean isJPAEARClasses, boolean isEjb3	,String weblogicIp,
			String portIp,String nameServer,String ipServer,String portServer,String userServer
			,String pwdServer , String codapp, String earClassesProyPath) throws Exception {

		consola = ConsoleLogger.getDefault();
		consola.println("UDA - INI", Constants.MSG_INFORMATION);
		
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(Constants.EJB_NAME_PATTERN, ejbProyName);
		context.put(Constants.PACKAGE_PATTERN, packageName);
		context.put("serviceName", serviceName);
		String namePackage=serviceName.substring(0,serviceName.lastIndexOf(".")) ;
		context.put("namePackageEjb",namePackage);
		String namePackageService= namePackage.replace(".remoting", ".service");
		context.put("namePackageService",namePackageService);
		context.put("jndiName",jndiName);
		context.put("isJpa",isJPAEARClasses);
		context.put("isEjb3",isEjb3);
		context.put("weblogicIp",weblogicIp);
		context.put("portIp",portIp);
		context.put("nameServer",nameServer);
		context.put("ipServer",ipServer);
		context.put("portServer",portServer);
		context.put("userServer",userServer);
		context.put("pwdServer",pwdServer);
		context.put("codapp",codapp);
		context.put("earClassesPath",earClassesProyPath);

		try{
			// Recupera el Workspace para crear los proyectos
			//IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			if (addNeededLibraries(earProyPath,isEjb3)){
				if(!isEjb3) {
					context.put("serviceName", serviceName.replace("Home", ""));
					List<String[]> metodos = StubClassUtils.getClasses((String) context.get("serviceName"));
					context.put("methods",metodos);
				}else{
					List<String[]> metodos = getClassesEJB3(serviceName.replace("Home", ""),earProyPath);
					context.put("methods",metodos);
				}
			}
	
			createAdministrationStub(ejbProyPath,context);
			createAdministrationStubRemote(ejbProyPath,context);
			createAppConfigXml(configProyPath,context);
			editXMLFile(context.get("earClassesPath")+"/src/spring", new File(context.get("earClassesPath")+"/src/spring/service-config.xml"),context);
			editXMLFileWeblogicEjbJar(ejbProyPath+"/ejbModule/META-INF", new File(ejbProyPath+"/ejbModule/META-INF/weblogic-ejb-jar.xml"),context);
			this.summary = createSummary(context);
			
		}catch(Exception e){
			consola.println(e.toString(), Constants.MSG_ERROR);
			throw e;
		}
		consola.println("Cliente EJB " + context.get("serviceName")+ "Stub del proyecto " + ((String)ejbProyName), Constants.MSG_INFORMATION);
		consola.println("generado en " + ((String) context.get(Constants.PACKAGE_PATTERN)).replace(".service", ".remoting"), Constants.MSG_INFORMATION);
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
		summaryText.append("\n\n- EJB ");
		summaryText.append(context.get("serviceName")+ "Stub");
		summaryText.append(" generado para ");
		if ((Boolean)context.get("isEjb3")){
			summaryText.append(context.get("serviceName")+"SkeletonRemote");
		}else{
			summaryText.append(context.get("serviceName")+"Home");
		}
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
	 * Obtiene las classes existentes del tipo EJB 3
	 * @param fileName - nombre del fichero
	 * @param earProyPath - path del proyecto EAR
	 * @return - lista de nombres de clases existentes
	 */
	public static  List<String[]> getClassesEJB3( String fileName, String earProyPath) throws IOException,
	SecurityException, ClassNotFoundException,
	IllegalArgumentException, InstantiationException,
	IllegalAccessException, InvocationTargetException,
	NoSuchMethodException {

		Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(
				fileName);
		//Map<?, ?> identity = new HashMap<Object, Object>();
		//Proxy someServiceImpl = null;
		List<String[]> list = new ArrayList<String[]>();
		boolean bol = true;

		Method[] methods = clazz.getDeclaredMethods();
		for(Method met:methods){
			for(String str:genericMethods){
				//if(str.equalsIgnoreCase(met.toGenericString())){
				if(met.toGenericString().endsWith(str)){
					bol = false;
					break;
				}            		
			}
			if (bol){
				//String str = met.toGenericString();
				String[] auxiliar = StubClassUtils.getMetodCaracteristics(met);
				if (auxiliar!=null){
					list.add(auxiliar);
				}	
			}
			bol = true;
		}
		return list;

	}
	/**
	 * Genera el stub del método indicado
	 * @param ejbProyPath - path proyecto EJB
	 * @param context - Contexto Freemarker
	 * @return - devuelve el resultado de la ejecución
	 */
	public boolean createAdministrationStub(String ejbProyPath,Map<String, Object> context){
		String packageName= (String) context.get(Constants.PACKAGE_PATTERN);
		String serviceName=(String) context.get("serviceName");
		String serviceNameAux = "";
		if ((Boolean) context.get("isEjb3")){
			serviceNameAux = serviceName.substring(serviceName.lastIndexOf(".")+1,serviceName.length()).replace(udaEjb, "");
		}else{
			serviceNameAux = serviceName.substring(serviceName.lastIndexOf(".")+1,serviceName.length()).replace(geremuaEjb, "");
		}

		serviceName = serviceName.substring(0,serviceName.lastIndexOf("."));
		context.put("skeletonUtils", skeletonUtils);
		context.put("ctrUtils", ctrUtils);
		context.put("packageNameRemoting",packageName);
		context.put("serviceName",serviceNameAux);
		context.put("stubUtils", getStub);

		String pathTemplatesEjb = Activator.getDefault().getPreferenceStore()
				.getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
				+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EJB;
		try {
			ProjectWorker.createFileTemplateRename(pathTemplatesEjb+"/client",ejbProyPath+ "/ejbModule/"+packageName.replace(".", "/"),"stub", serviceNameAux + "Stub.java",context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Genera el stub remote del método indicado
	 * @param ejbProyPath - path proyecto EJB
	 * @param context - Contexto Freemarker
	 * @return - devuelve el resultado de la ejecución
	 */  
	public boolean createAdministrationStubRemote(String ejbProyPath,Map<String, Object> context){
		String packageName= (String) context.get(Constants.PACKAGE_PATTERN);

		String serviceName=(String) context.get("serviceName");
		String serviceNameAux = "";
		if ((Boolean) context.get("isEjb3")){
			serviceNameAux = serviceName.substring(serviceName.lastIndexOf(".")+1,serviceName.length()).replace(udaEjb, "");
		}else{
			serviceNameAux = serviceName.substring(serviceName.lastIndexOf(".")+1,serviceName.length()).replace(geremuaEjb, "");
		}
		context.put("serviceName",serviceNameAux);
		context.put("skeletonUtils", skeletonUtils);
		context.put("ctrUtils", ctrUtils);
		String pathTemplatesEjb = Activator.getDefault().getPreferenceStore()
				.getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH)
				+ Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EJB;
		try {
			ProjectWorker.createFileTemplateRename(pathTemplatesEjb+"/client",ejbProyPath+ "/ejbModule/"+packageName.replace(".", "/"),"stubRemote", serviceNameAux + "StubRemote.java",context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Actualiza el app.properties
	 * @param configProyPath - path proyecto config
	 * @param context - Contexto Freemarker
	 * @return - devuelve el resultado de la ejecución
	 */
	public boolean createAppConfigXml(
			String configProyPath,Map<String, Object> context){
		try {
			insertStringInFile(new File(configProyPath+"/"+context.get("codapp")+".properties"),context);
			logger.info(configProyPath+"/"+context.get("codapp")+".properties actualizado");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Verifica si el proyecto EARClasses es de persistencia JPA
	 * @param project - proyecto EARClasses
	 * @return - devuelve el resultado de la ejecución
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
	 * añade al classpath las librerias necesarias
	 * @param earPath - path proyecto EAR
	 * @param isEjb3 - indica si el proyecto es ejb3
	 * @throws IOException
	 */     
	public boolean addNeededLibraries(String earPath,boolean isEjb3) throws IOException{
		String x38JarName =Utilities.findFileStartsLike(new File(earPath + Constants.PREF_DEFAULT_EAR_LIBS),Constants.PREF_DEFAULT_X38_LIBS);
		if (x38JarName.equals("")){
			throw new IOException(
					"La libreria x38ShLibClasses no está instalada en: "+earPath + Constants.PREF_DEFAULT_EAR_LIBS + "/"  + x38JarName);
		}
		addFile(earPath + Constants.PREF_DEFAULT_EAR_LIBS+"/"+x38JarName);
		//Add libraries where name like '%Remoting.jar'
		List<String> librerias = Utilities.findFileEndsLike(new File(earPath + Constants.PREF_DEFAULT_EAR_LIBS),"Remoting");
		Iterator<String> itLib = librerias.iterator();
		while (itLib.hasNext()){
			String nombreLib= (String) itLib.next();
			addFile(earPath+ Constants.PREF_DEFAULT_EAR_LIBS+"/" +nombreLib);
		}	
		if (!isEjb3){
			try{
				String property = System.getProperty("eclipse.home.location").replace("file:/", "");
				String directorio =findDirectory(new File(property+"/plugins"));
				if (directorio!=null){
					// addFile("D:/UDALast/tools/z98/code/com.ejie.uda/lib/ejb-api-3.0.jar");
					addFile(property + "plugins/" + directorio + "/lib/tools/ejb-api-3.0.jar");
				}else{
					throw new IOException(
							"No se encuentra la carpeta de hibernate para obtener el jar ejb-api-3.0.jar ");
				}
			}catch(Exception e){
				throw new IOException(
						"La libreria ejb-api-3.0.jar  no está introducida en:"+  System.getProperty("eclipse.home.location").replace("file:/", "")+"/plugins/" );

			}

		}
		return true;
	}
	/**
	 * Busca el directorio de hibernate desde la raíz del eclipse
	 * @param directory - directorio
	 * @return - nombre del directorio
	 */  
	private static String findDirectory(File directory){
		String directoryName="";
		if (!directory.exists()) {
			return "";
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (file.isDirectory() && fileName.startsWith("org.hibernate.eclipse_")){
				directoryName=fileName;
			}
		}
		return directoryName;
	}
	/**
	 * Busca un nodo dentro de un fichero XML
	 * @param document - docuemnto
	 * @param nodeName- nombre del nodo
	 * @param attributeName - nombre del atributo
	 * @param attributeValue - valor del atributo
	 * @return - indicador de encontrado
	 */  
	private boolean findNodeAttribute(Document document, String nodeName, String attributeName, String attributeValue){

		boolean match = false;

		if (document != null   && !Utilities.isBlank(nodeName)){
			NodeList nodeList = document.getElementsByTagName(nodeName);

			int size = nodeList.getLength();

			for(int i=0; i < size; i++){
				Node childNode = nodeList.item(i);

				NamedNodeMap nodeMap = childNode.getAttributes();
				if (attributeName.equals("")){
					match=true;
				}	  
				if (!attributeName.equals("")){
					String valor =nodeMap.getNamedItem(attributeName).getNodeValue();

					if (attributeValue.equals(valor)){
						match = true;
						break;
					}
				}    
			}
		}
		return match;
	}
	/**
	 * Inserta una cadena de carácteres en un fichero
	 * @param inFile - ficehro
	 * @param context- contexto Freemarker
	 */  	
	public void insertStringInFile(File inFile,Map<String, Object> context) throws Exception {
		boolean foundJavaNaming=false;

		File outFile = new File("prop.tmp");
		FileInputStream fis  = new FileInputStream(inFile);
		BufferedReader in = new BufferedReader(new InputStreamReader(fis));
		FileOutputStream fos = new FileOutputStream(outFile);
		PrintWriter out = new PrintWriter(fos);

		String thisLine = "";
		while ((thisLine = in.readLine()) != null) {
			if (thisLine.toUpperCase().contains(Constants.SERVER_JNI_NAMING.toUpperCase())){
				foundJavaNaming=true;
			}
			out.println(thisLine);
		}
		out.println(" ");
		if (!foundJavaNaming){
			out.println(Constants.SERVER_FACTORY + "="+ Constants.DEFAULT_SERVER_FACTORY);
			out.println(Constants.SERVER_JNI_NAMING + "="+ Constants.DEFAULT_SERVER_JNI_NAMING);
			out.println(" ");
		}
		// añadimos el nuevo Stub
		if ((Boolean) context.get("isEjb3")){
			String key= context.get("nameServer")+"."+context.get("serviceName")+ "SkeletonRemote.jndi";
			out.println(key + " = "+(String) context.get("jndiName") + "#" + (String)context.get("namePackageEjb")+ "." + (String)context.get("serviceName")+ "SkeletonRemote");
		}else{
			String key= context.get("nameServer")+"."+context.get("serviceName")+ "Home.jndi";
			out.println(key + " = "+(String) context.get("jndiName") );
		}
		String key= context.get("nameServer")+".url";
		out.println(key + " = "+(String) "t3://" + context.get("ipServer")+":"+ context.get("portServer") );
		key= context.get("nameServer")+".user";
		out.println(key +" = " +(String)  context.get("userServer"));
		key= context.get("nameServer")+".password";
		out.println(key +" = " +(String)  context.get("pwdServer"));

		out.flush();
		out.close();
		in.close();

		inFile.delete();
		outFile.renameTo(inFile);



	}
	/**
	 * Edita un fichero XML
	 * @param path - ruta
	 * @param xmlFile- fichero XML
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
				boolean found=findNodeAttribute(doc, "bean","id","remoteEJBFactory");

				doc.getDoctype();
				doc.setXmlStandalone(true);

				// recoge el elemento raiz
				Element rootElement = doc.getDocumentElement();
				// crea un elemento nuevo de definition
				if (!found){
					//	Element definitionElement = doc.getDocumentElement()nt("util:properties");
					// Añade atributos

					//definitionElement.setAttribute("id", "appConfiguration");	
					//definitionElement.setAttribute("location", "classpath:"+context.get("codapp")+"/"+context.get("codapp")+".properties");
					//rootElement.appendChild(definitionElement);
					Element definitionElementBean = doc.createElement("bean");
					definitionElementBean.setAttribute("id", "remoteEJBFactory");	
					definitionElementBean.setAttribute("class", "com.ejie.x38.remote.RemoteEJBFactoryImpl");
					Element definitionElementBeanChild  = doc.createElement("property");
					definitionElementBeanChild.setAttribute("name", "appConfiguration");	
					definitionElementBeanChild.setAttribute("ref", "appConfiguration");
					definitionElementBean.appendChild(definitionElementBeanChild);
					rootElement.appendChild(definitionElementBean);

					// Añade atributos

				}	

				Element definitionElementEJB = doc.createElement("jee:remote-slsb");
				definitionElementEJB.setAttribute("business-interface",context.get("packageName")+"."+context.get("serviceName") + "StubRemote");
				definitionElementEJB.setAttribute("environment-ref", "appConfiguration");
				definitionElementEJB.setAttribute("id",  ControllerUtils.stringDecapitalize((String)context.get("serviceName")) + "StubRemote");
				definitionElementEJB.setAttribute("jndi-name", (String) context.get("jndiName")+"#"+context.get("packageName")+"."+context.get("serviceName") + "StubRemote");
				definitionElementEJB.setAttribute("lookup-home-on-startup", "true");
				definitionElementEJB.setAttribute("refresh-home-on-connect-failure", "true");
				rootElement.appendChild(definitionElementEJB);

				// crea elemento hijo para la JSP
				//contentElement.setAttribute("value", relativePathJsp);

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
				logger.info(xmlFile.getPath() + " actualizado");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Edita un fichero XML weblogic
	 * @param path - ruta
	 * @param xmlFile- fichero XML
	 * @param context - contexto de Freemarker
	 */ 
	private void editXMLFileWeblogicEjbJar(String path, File xmlFile,Map<String, Object> context){
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
				definitionEjbname.setTextContent((String) context.get("serviceName")+"Stub");
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
