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
package com.ejie.uda.operations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jst.j2ee.classpathdep.UpdateClasspathAttributeUtil;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

public class ProjectWorker {
	
	/**
	 * Constructor
	 */
	private ProjectWorker(){
		//No es instanciable
	}

	/**
	 * Relaciona los proyectos generando las referencias entre ellos (EAR -> WAR y EAR -> EARClasses)
	 * 
	 * @param projectEAR - proyecto xxxEAR
	 * @param projectWAR - proyecto xxxWAR
	 * @param projectEARClasses - proyecto xxxEARClasses
	 * @throws CoreException
	 */
	public static void linkedReferencesProjects(IProject projectEAR,
			IProject project) throws CoreException {

		linkedReferencesProjects(projectEAR, project, null);
	}
	
	public static void addToClasspath(final IJavaProject javaProject, final IClasspathEntry cpEntry) throws JavaModelException
 	{
 		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
 		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
 		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
 		newEntries[oldEntries.length] = cpEntry;
 		javaProject.setRawClasspath(newEntries, null);
 	}
	/**
	 * Relaciona los proyectos generando las referencias entre ellos (EAR -> WAR y EAR -> EARClasses) con deploy-path
	 * 
	 * @param projectEAR - proyecto xxxEAR
	 * @param projectWAR - proyecto xxxWAR
	 * @param projectEARClasses - proyecto xxxEARClasses
	 * @param deployPath - ruta del deploy-path
	 * @throws CoreException
	 */
	public static void linkedReferencesProjects(IProject projectEAR,
			IProject project, String deployPath) throws CoreException {

		// Al deployear que genere el war con el jar dentro del ear
		IVirtualComponent earComponent = null;
		IVirtualComponent projectComponent = null;

		// Refenrencias los proyectos entre ellos
		if (projectEAR != null && project != null) {
			ProjectUtilities.addReferenceProjects(projectEAR, project);

			earComponent = ComponentCore.createComponent(projectEAR);
			projectComponent = ComponentCore.createComponent(project);

			if (!projectsContainsReference(earComponent, projectComponent)) {
				// Genera la dependencia en Deployment Assembly entre el EAR y el proyecto a relacionarse
				IVirtualReference ref = ComponentCore.createReference(earComponent, projectComponent);
				ref.setDependencyType(IVirtualReference.DEPENDENCY_TYPE_USES);
				if (!Utilities.isBlank(deployPath)){
					ref.setRuntimePath(new Path(deployPath));
				}
				earComponent.addReferences(new IVirtualReference[] { ref });
			}
		}
	}

	public static void linkedProjectsClasspath(IProject projectSource,
			IProject project) throws CoreException {
		
		 String classFolder = "/" + project.getName() + "/build/classes";
         
         IClasspathAttribute[] attributes =  {UpdateClasspathAttributeUtil.createNonDependencyAttribute()};
         IClasspathEntry entryClassFolder = JavaCore.newLibraryEntry(new Path(classFolder), null, null, null, attributes, false);
         addToClasspath(JavaCore.create(projectSource), entryClassFolder);
		
	}

	/**
	 * Verifica si los componentes ya están referenciados
	 * 
	 * @param sourceComponent - VirtualComponent de origen
	 * @param destinationComponent - VirtualComponente de destino
	 * @return true si están relacionados, false ecc.
	 */
	public static boolean projectsContainsReference(
			IVirtualComponent sourceComponent,
			IVirtualComponent destinationComponent) {
		if ((sourceComponent != null && sourceComponent.getProject() != null)
				&& (destinationComponent != null && destinationComponent
						.getProject() != null)) {

			IVirtualReference[] existingReferences = sourceComponent.getReferences();
			IVirtualComponent referencedComponent = null;
			if (existingReferences != null) {
				for (int i = 0; i < existingReferences.length; i++) {
					if (existingReferences[i] != null) {
						referencedComponent = existingReferences[i].getReferencedComponent();
						if (referencedComponent != null && referencedComponent.equals(destinationComponent))
							return true;
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * Crea una carpeta en el proyecto si no existe y devuelve la ruta absoluta de la misma
	 * 
	 * @param project - proyecto eclipse
	 * @param relativePath - ruta relativa de la carpeta a comprobar
	 * @return ruta absoluta de la carpeta
	 * @throws Exception
	 */
	public static String createGetFolderPath(IProject project, String relativePath)
			throws Exception {
		IFolder folder = project.getFolder(relativePath);
		if (!folder.exists()) {
			folder.create(false, true, null);
		}

		return folder.getLocation().toString();
	}
	
	/**
	 * Genera un fichero desde una plantilla
	 * 
	 * @param pathTemplate - ruta de la plantilla
	 * @param path - ruta del fichero de salida
	 * @param fileName - nombre del fichero
	 * @param context - contexto del plugin
	 * @param outputFileName - nombre de salida del fichero
	 * @throws Exception
	 */
	public static void createFileTemplate(String pathTemplate, String path,
			String fileName, Map<String, Object> context, String outputFileName) throws Exception {
		File file = new File(path + "/" + outputFileName);
		FreemarkerWorker.executeOperation(pathTemplate, fileName + ".ftl",
				context, file);
	}

	/**
	 * Genera un fichero desde una plantilla
	 * 
	 * @param pathTemplate - ruta de la plantilla
	 * @param path - ruta del fichero de salida
	 * @param fileName - nombre del fichero
	 * @param context - contexto del plugin
	 * @throws Exception
	 */
	public static void createFileTemplate(String pathTemplate, String path,
			String fileName, Map<String, Object> context) throws Exception {
		File file = new File(path + "/" + fileName);
		FreemarkerWorker.executeOperation(pathTemplate, fileName + ".ftl",
				context, file);
	}
	
	/**
	 * Genera un fichero desde una plantilla renombrandolo
	 * 
	 * @param pathTemplate - ruta de la plantilla
	 * @param path - ruta del fichero de salida
	 * @param fileName - nombre del fichero
	 * @param context - contexto del plugin
	 * @throws Exception
	 */
	public static void createFileTemplateRename(String pathTemplate, String path,
			String fileName,String newName, Map<String, Object> context) throws Exception {
		File file = new File(path + "/" + newName);
		FreemarkerWorker.executeOperation(pathTemplate, fileName + ".ftl",
				context, file);
	}

	/**
	 * Copia un fichero a la ruta esceficada
	 * 
	 * @param sourcePath - ruta de origen
	 * @param destinationPath - ruta de destino
	 * @param fileName - nombre del fichero
	 * @param context - contexto del plugin
	 * @param outputFileName - nombre de salida del fichero
	 * @throws Exception
	 */
	public static void copyFile(String sourcePath, String destinationPath,
			String fileName, Map<String, Object> context, String outputFileName) throws Exception {
		File file = new File(sourcePath + "/" + fileName);
		RVCopyWorker.executeOperation(file, destinationPath, context, outputFileName);
	}
	
	/**
	 * Copia un fichero a la ruta esceficada
	 * 
	 * @param sourcePath - ruta de origen
	 * @param destinationPath - ruta de destino
	 * @param fileName - nombre del fichero
	 * @param context - contexto del plugin
	 * @throws Exception
	 */
	public static void copyFile(String sourcePath, String destinationPath,
			String fileName, Map<String, Object> context) throws Exception {
		File file = new File(sourcePath + "/" + fileName);
		RVCopyWorker.executeOperation(file, destinationPath, context);
	}
	
	/**
	 * Refresca la información contenida en el proyecto de forma bloqueante
	 * 
	 * @param project - proyecto eclipse
	 * @throws CoreException
	 */
	public static void refresh(IProject project) throws CoreException {
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
	
	/**
	 * Crea una carpeta en el proyecto y la asigna como Output del proyecto
	 * @param project - proyecto al que se va a añadir
	 * @param outputName - nombre de la carpeta de clases compiladas
	 * @throws CoreException
	 */
	public static void createOutputFolder(IProject project, String outputName) throws CoreException{
		
		IJavaProject javaProject = JavaCore.create(project);
		
		IFolder binFolder = project.getFolder(outputName);
	    binFolder.create(false, true, null);

	    IPath outputLocation = binFolder.getFullPath();
	    javaProject.setOutputLocation(outputLocation, null);
	}

	/**
	 * Crea las dependencias entre un EAR y otro proyecto, añadiendo las referencias al application.xml
	 * @param earProject - proyecto EAR
	 * @param childProject - proyecto a vincular
	 * @throws ExecutionException
	 */
	public static void createEARDependency(IProject earProject, IProject childProject) throws ExecutionException {
		// create a new DocumentBuilderFactory
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    IFile earDescriptor = earProject.getFile(Constants.PERSISTENCE_DESC_LOCALPATH);
		
	    try {
	    	InputStream isEarDescriptor = earDescriptor.getContents();
	    	int earDescriptorType = earDescriptor.getType();
			
			// use the factory to create a documentbuilder
	        DocumentBuilder builder = factory.newDocumentBuilder();

	        // create a new document from input stream
	        Document doc = builder.parse(isEarDescriptor);

	        // get the first element
	        Element element = doc.getDocumentElement();
	        
	        // update ear descriptor with new web module
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(
	        		  "<module>"
		        		+ "<web>"
			        		+ "<web-uri>"+childProject.getName()+".war</web-uri>"
			        		+ "<context-root>"+childProject.getName()+"</context-root>"
		        		+ "</web>"
	        		+ "</module>"));
	        Node newWarModule = doc.importNode(builder.parse(is).getDocumentElement(), true);
	        element.appendChild(newWarModule);
	         
			// Configure transformer and write the Document
			TransformerFactory transfac = TransformerFactory.newInstance();
			transfac.setAttribute("indent-number", 4);
			Transformer transformer = transfac.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        Source xmlSource = new DOMSource(doc);
	        Result outputTarget = new StreamResult(outputStream);			
	        transformer.transform(xmlSource, outputTarget);
	        earDescriptor.setContents(new ByteArrayInputStream(outputStream.toByteArray()),earDescriptorType,null);
	         
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			ConsoleLogger consola = ConsoleLogger.getDefault();
			consola.println(sw.toString(), Constants.MSG_ERROR);
			throw new ExecutionException("Error actualizando el application.xml del EAR", e);
		}
	}
	
	/**
	 * Organiza el classpath del proyecto EARClasses
	 * @param project - proyecto EARClasses
	 * @param context - contexto
	 * @param monitor - monitor de progreso
	 * @throws JavaModelException
	 */
	public static void organizeEARClassesLibraries(IProject project, Map<String, Object> context, IProgressMonitor monitor) throws JavaModelException{

		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		
		if (oldEntries != null) {
			List<IClasspathEntry> listEntriesDeleted = new ArrayList<IClasspathEntry>();
            List<IClasspathEntry> listEntries = new ArrayList<IClasspathEntry>(Arrays.asList(oldEntries));

            for (IClasspathEntry entry : listEntries) {
            		if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
					
						String typeContainer = entry.getPath().toString();
						
						if (!typeContainer.contains("JRE_CONTAINER") && !typeContainer.contains("WebLogic")){
							listEntriesDeleted.add(entry);
						}
				}
			}
            listEntries.removeAll(listEntriesDeleted);
            String containerEAR = Constants.WEBLOGIC_LIB_APP + "/" + context.get(Constants.EAR_NAME_PATTERN);
            listEntries.add(JavaCore.newContainerEntry(new Path(containerEAR)));
            listEntries.add(JavaCore.newContainerEntry(new Path(Constants.WEBLOGIC_LIB_SYS)));
            IClasspathEntry[] newEntries = listEntries.toArray(new IClasspathEntry[listEntries.size()]);
            javaProject.setRawClasspath(newEntries, SubMonitor.convert(monitor,1));
           
		}
	}

	/**
	 * Organiza el classpath del proyecto WAR
	 * @param project - proyecto WAR
	 * @param context - contexto
	 * @param monitor - monitor de progreso
	 * @throws CoreException
	 */
	public static void organizeWARLibraries(IProject project, Map<String, Object> context, IProgressMonitor monitor) throws CoreException{

		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		
		if (oldEntries != null) {
            List<IClasspathEntry> listEntries = new ArrayList<IClasspathEntry>(Arrays.asList(oldEntries));

            String containerEAR = Constants.WEBLOGIC_LIB_APP + "/" + context.get(Constants.EAR_NAME_PATTERN);
            listEntries.add(JavaCore.newContainerEntry(new Path(containerEAR)));
            
            String classFolder = "/" + context.get(Constants.EARCLASSES_NAME_PATTERN) + "/build/classes";
            
            IClasspathAttribute[] attributes =  {UpdateClasspathAttributeUtil.createNonDependencyAttribute()};
            IClasspathEntry entryClassFolder = JavaCore.newLibraryEntry(new Path(classFolder), null, null, null, attributes, false);
            listEntries.add(entryClassFolder);
            
            IClasspathEntry[] newEntries = listEntries.toArray(new IClasspathEntry[listEntries.size()]);
            javaProject.setRawClasspath(newEntries, SubMonitor.convert(monitor,1));    
		}
	}
	 public static void addEjbModuleEARApplication(String path, File xmlFile,Map<String, Object> context){
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
							Element definitionModulename = doc.createElement("module");
							// Añade atributos
							Element definitionEjbname = doc.createElement("ejb");
							definitionEjbname.setTextContent((String)context.get(Constants.EJB_NAME_PATTERN)+".jar");
							definitionModulename.appendChild(definitionEjbname);
							rootElement.appendChild(definitionModulename);
				   
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
	
	/**
	 * Organiza el classpath del proyecto EJB
	 * @param project - proyecto EJB
	 * @param context - contexto
	 * @param monitor - monitor de progreso
	 * @throws CoreException
	 */
	public static void organizeEJBLibraries(IProject project, Map<String, Object> context, IProgressMonitor monitor) throws CoreException{

		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		
		if (oldEntries != null) {
            List<IClasspathEntry> listEntries = new ArrayList<IClasspathEntry>(Arrays.asList(oldEntries));

            String containerEAR = Constants.WEBLOGIC_LIB_APP + "/" + context.get(Constants.EAR_NAME_PATTERN);
            listEntries.add(JavaCore.newContainerEntry(new Path(containerEAR)));
            //listEntries.add(ne);
            IClasspathEntry[] newEntries = listEntries.toArray(new IClasspathEntry[listEntries.size()]);
            javaProject.setRawClasspath(newEntries, SubMonitor.convert(monitor,1));    
		}
	}

	/**
	 * Añade una carpeta de código al proyecto
	 * @param project - proyecto
	 * @param pathSource - ruta de la carpeta de código
	 * @param monitor - monitor de progreso
	 * @param sourceFolder - carpeta donde se localizaró el código
	 * @throws CoreException
	 */
	public static void addSourceProject(IProject project, String pathSource, IProgressMonitor monitor, IFolder sourceFolder) throws CoreException{
		
		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(sourceFolder);
		
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
		javaProject.setRawClasspath(newEntries, null);
	}
	
	/**
	 * Verifica si existe en el equipo el fichero de configuración settings.xml
	 * @param context - contexto
	 */
	public static void configSettingsMaven(Map<String, Object> context){
		String pathSettingsMaven = System.getenv("USERPROFILE") + "\\.m2\\settings.xml";
		File configMaven = new File(pathSettingsMaven);
		
		if (!configMaven.exists()){
			pathSettingsMaven = System.getenv("M2_HOME") + "\\conf\\settings.xml";
		}
		context.put(Constants.MAVEN_HOME, System.getenv("M2_HOME"));
		context.put(Constants.MAVEN_SETTINGS, pathSettingsMaven);
	}
	
	/**
	 * Verifica si existe la variable de entorno M2_REPO y lo ponen en el contexto
	 * @param context - contexto
	 */
	public static void configRepositoryMaven(Map<String, Object> context){
		String pathRepositoryMaven = System.getenv("M2_REPO");
		
		if (Utilities.isBlank(pathRepositoryMaven)){
			context.put(Constants.MAVEN_REPOSITORY, "");
		}else{
			context.put(Constants.MAVEN_REPOSITORY, pathRepositoryMaven);
		}
	}
	
	/**
	 * Genera una estructura d ecarpetas pasándole la ruta
	 * @param path - ruta de carpetas a generar
	 * @return true si ha generado correctamente, false ecc.
	 */
	public static boolean createFolder(String path){
		try{
			File folder = new File(path);
			folder.mkdirs();	
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Genera los proyectos en la ruta indicada
	 * @param project - proyecto a generar
	 * @param location - ruta donde se crea el proyecto
	 * @param folderProjectName - true si se genera una carpeta con el nombre del proyecto o
	 * 							  false si se genera sobre la ruta indicada.
	 * @return - proyecto creado en la ruta especificada.
	 * @throws CoreException
	 */
	public static IProject createProjectLocation(IProject project, String location, boolean folderProjectName) throws CoreException{
		
		String path = location;
		
		if (folderProjectName){
			path = location + "/" + project.getName();
		}
		
		if (ProjectWorker.createFolder(path)) {
			IPath folderConfig = new Path(path);
			IProjectDescription description;
			description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
			// genera el proyecto en la carpeta indicada
			description.setLocation(folderConfig);
			// crea el proyecto
			project.create(description, null);
			project.open(IResource.BACKGROUND_REFRESH, null);
			
		}	
		
		return project;
	}

}
