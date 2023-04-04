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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ejie.uda.Activator;
import com.ejie.uda.operations.ProjectWorker;
import com.ejie.uda.operations.PropertiesWorker;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Grid;
import com.ejie.uda.utils.GridColumn;
import com.ejie.uda.utils.Maint;
import com.ejie.uda.utils.Utilities;

/**
 * Clase encargada de generar todo lo necesario para el asistente "Generar mantenimiento"
 * 
 */
public class NewMaintWizard extends Wizard implements INewWizard {

	private NewMaintWizardPageOne pageOne;
	private NewMaintWizardPageTwo pageTwo;
	private NewMaintWizardPageThree pageThree;
	private NewMaintWizardPageFour pageFour;
	
	private ISelection selection;
	private String errorMessage;
	private String summary;
	// Logs en la consola
	private ConsoleLogger console = new ConsoleLogger(Constants.CONSOLE_NAME);
	// Fichero de propiedades
	private PropertiesWorker udaProperties;
	
	
	/**
	 * Constructor
	 */
	public NewMaintWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Método de acceso desde otra página a la página One
	 * @return pagina One
	 */
	public NewMaintWizardPageOne getPageNewMaintWizardPageOne(){
		return this.pageOne;
	}

	/**
	 * Método de acceso desde otra página a la página Two
	 * @return pagina Two
	 */
	public NewMaintWizardPageTwo getPageNewMaintWizardPageTwo(){
		return this.pageTwo;
	}

	/**
	 * Método de acceso desde otra página a la página Three
	 * @return pagina Three
	 */
	public NewMaintWizardPageThree getPageNewMaintWizardPageThree(){
		return this.pageThree;
	}
	
	/**
	 * Método de acceso desde otra página a la página Four
	 * @return pagina Four
	 */
	public NewMaintWizardPageFour getPageNewMaintWizardPageFour(){
		return this.pageFour;
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
		pageOne = new NewMaintWizardPageOne(selection);
		pageTwo = new NewMaintWizardPageTwo(selection);
		pageThree = new NewMaintWizardPageThree(selection);
		pageFour = new NewMaintWizardPageFour(selection);
		addPage(pageOne);
		addPage(pageTwo);
		addPage(pageThree);
		addPage(pageFour);
		
	}
	
	/**
	 * Deshabilita el boton finish cuando está en algunas pantallas
	 */
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == pageOne || getContainer().getCurrentPage() == pageTwo || getContainer().getCurrentPage() == pageThree) {
			return false;
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
		final ConnectionData conData = pageOne.getConnectionData();
		final IProject projectWar = pageOne.getWarProject();
		final Maint maint = pageTwo.getMaint();
		final Grid grid = pageThree.getGrid();
		final List<GridColumn> gridColumns = pageFour.getColumns();
		
		if(gridColumns != null && gridColumns.size() > 1 && grid != null && grid.getSortName() != null) {//meter el num order
			int pos = 0;
			for(GridColumn gridColumn:gridColumns) {
				if(gridColumn.getActivated()) {
					if(gridColumn.getLabel().equals(grid.getSortName())) {
						grid.setSortPosition(pos);
						break;
					}
					
					pos++;
				}
			}
		}
		
		String pathTemplates = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH);
		
		//Validación de configuración de plantillas
		if (Utilities.isBlank(pathTemplates)) {
			pageFour.setMessage("No está configurada la ruta de las plantillas en Window > Preferences > UDA",
					IMessageProvider.ERROR);
			return false;
		}else{
			// Verifica que es correcta la ruta que se ha indicado en las preferencias de UDA
			File subTemplates = new File(pathTemplates + Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EAR);
			if (!subTemplates.exists()){
				pageFour.setMessage("La ruta configurada en Windows > Preferences > UDA no es válida",
						IMessageProvider.ERROR);
				return false;
			}
		}
		
		// Validación de selección de proyecto WAR de UDA
		if (projectWar == null) {
			pageFour.setMessage(
					"Se debe seleccionar algun proyecto tipo WAR para generar el mantenimiento",
					IMessageProvider.ERROR);
			return false;
		}
		
		// Validacion de la conexión
		if (conData == null){
			pageFour.setMessage("Conexión no válida", IMessageProvider.ERROR);
			return false;
		}
		// Validación de las propiedades generales del mantenimiento
		if (maint == null || Utilities.isBlank(maint.getNameMaint())){
			pageFour.setMessage("Propiedades generales de mantenimiento no válidas", IMessageProvider.ERROR);
			return false;
		}
		
		//Validación de la propiedades del grid
		if (grid == null){ // || Utilities.isBlank(grid.get.getNameMaint())){
			pageFour.setMessage("Propiedades de la entidad seleccionada no válidas", IMessageProvider.ERROR);
			return false;
		}
		
		//Validación de la propiedades de las columnas del grid
		if (gridColumns == null || gridColumns.isEmpty()){ // || Utilities.isBlank(grid.get.getNameMaint())){
			pageFour.setMessage("Propiedades de la columnas no válidas", IMessageProvider.ERROR);
			return false;
		}else{
			for (Iterator<GridColumn> iterator = gridColumns.iterator(); iterator.hasNext();) {
				GridColumn gridColumn = (GridColumn) iterator.next();

				if (gridColumn.getActivated()){
					if (Utilities.isBlank(gridColumn.getName())){
						pageFour.setMessage("El campo 'Name' de la columna " + gridColumn.getColumnName() + " es obligatorio", IMessageProvider.ERROR);
						return false;
					}else if (Utilities.isBlank(gridColumn.getLabel())){
						pageFour.setMessage("El campo 'Label' de la columna " + gridColumn.getColumnName() + " es obligatorio", IMessageProvider.ERROR);
						return false;
					}
				}
			}
		}
		
		// Inserta en nombre de la clase en el mantenimiento para imprimirlo
		maint.setModelObject(grid.getTableName());
		
		// Recupera las claves primarias
		maint.setPrimaryKey(pageFour.getPrimaryKeys());
		
		// Guarda los valores de la conexión en un fichero de propiedades
		setConfigDatabaseProperties(conData);
		
		pageFour.setMessage("Este Wizard genera un nuevo mantenimiento para una aplicación UDA", IMessageProvider.NONE);
		
		// Inicia la ejecución de proceso
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				try {
					monitor.beginTask("Generando mantenimiento", 3);
					
					doFinish(monitor, projectWar, maint, grid, gridColumns, conData);
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
			
			pageFour.getControl().setEnabled(false);
			
			MessageDialog.openInformation(getShell(), "Información", "¡Las operaciones se han realizado con éxito!" + this.summary);
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Error", "Error en la generación de la aplicación: " + errorMessage);
		}
		return true;
	}

	/**
	 * Genera un mantenimiento para un aplicación
	 * 
	 * @param monitor - monitor de progreso del plugin
	 * @param projectWar - projecto WAR de UDA
	 * @param maint - objecto de mantenimiento
	 * @param grid - objecto de propiedades del grid
	 * @param gridColumns - lista de propíedades de las columnas
	 * @param conData - datos de conexión
	 * @throws Exception 
	 */
	private void doFinish(IProgressMonitor monitor, IProject projectWar, Maint maint, Grid grid, List<GridColumn> gridColumns, ConnectionData conData) throws Exception {
		
		console = ConsoleLogger.getDefault();
		console.println("UDA - INI", Constants.MSG_INFORMATION);
		
		String appCode = getAppNameFromWar(projectWar).toLowerCase();
		String entityTableName = grid.getTableName().replace("_", "").toLowerCase();
		String warName = projectWar.getName().substring(0, projectWar.getName().length()-3);
		String entityName = entityTableName; 
		//Cambia el nombre de la entidad a generar en el caso que se desea modificar su nombre,
		//dando la posibilidad tener dos mantenimientos de la misma entidad en distintos WARs.
		if (!Utilities.isBlank(grid.getAlias())){
			entityName = grid.getAlias().toLowerCase();
		}
		
		monitor.setTaskName("Generando mantenimiento...");
		monitor.worked(1);
		
		console.println("Generación del mantenimiento: " + maint.getNameMaint(), Constants.MSG_INFORMATION);
		console.println("Entidad a mantener: " + entityName, Constants.MSG_INFORMATION);
		
		// Contexto del plugin
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(Constants.STATICS_PATTERN, appCode + Constants.STATICS_NAME);
		context.put(Constants.CODAPP_PATTERN, appCode);
		context.put(Constants.WAR_NAME_PATTERN, projectWar.getName());
		context.put(Constants.WAR_NAME_SHORT_PATTERN, warName);
		context.put(Constants.ENTITY_PATTERN, entityName);
		context.put(Constants.MAINT, maint);
		context.put(Constants.GRID, grid);
		context.put(Constants.GRID_COLUMNS, filterGridColumnsActivated(gridColumns));
		context.put(Constants.PRIMARY_KEY, maint.getPrimaryKey());
		context.put(Constants.MULTIPK, maint.getPrimaryKey().contains(";"));
		
		try{
			
			final String pathTemplates = Activator.getDefault().getPreferenceStore().getString(Constants.PREF_TEMPLATES_UDA_LOCALPATH);
			
			String path = "";
			String pathWar = pathTemplates + Constants.PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_MAINT;
			
			// Añadir las JSP del mantenimiento
			path = ProjectWorker.createGetFolderPath(projectWar, "WebContent/WEB-INF/views/" + entityName);
			monitor.setTaskName("Generando JSPs...");
			ProjectWorker.createFileTemplate(pathWar, path, "maintSimple-includes.jsp", context, entityName + "-includes.jsp");
			ProjectWorker.createFileTemplate(pathWar, path, "maintSimple.jsp", context, entityName + ".jsp");
			monitor.worked(1);
			//Se crean los includes
			String pathInclude = ProjectWorker.createGetFolderPath(projectWar, "WebContent/WEB-INF/views/" + entityName+"/includes");
			if(maint.getIsMaint()) {//Si no quieres mantenimiento no se crea la jsp.
				ProjectWorker.createFileTemplate(pathWar, pathInclude, "maintEdit.jsp", context, maint.getNameMaint() + "Edit.jsp");
			}
			if(maint.getFilterMaint()) {//Si no quieres filtro no se crea la jsp.
				ProjectWorker.createFileTemplate(pathWar, pathInclude, "maintFilterForm.jsp", context, maint.getNameMaint() + "FilterForm.jsp");
			}
			console.println("JSPs generados en el proyecto WAR: " + (String)context.get(Constants.WAR_NAME_PATTERN), Constants.MSG_INFORMATION);
			console.println("\t" + entityName + "-includes.jsp", Constants.MSG_INFORMATION);
			console.println("\t" + entityName + ".jsp", Constants.MSG_INFORMATION);
	
			monitor.setTaskName("Modificando tiles.xml...");
			console.println("Modificación del fichero tiles.xml", Constants.MSG_INFORMATION);
			// Recupera el tiles.xml
			path = ProjectWorker.createGetFolderPath(projectWar, "WebContent/WEB-INF/views/");
			File xmlFile = new File(path + "/tiles.xml");
			//Edita el tiles.xml y Añade la referencia del nuevo mantenimiento en el caso que no exista
			editTiles(path, entityName, entityTableName, xmlFile);
			
			monitor.worked(1);
			
			// Recupera el Workspace
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			// Recupero el proyecto de Statics de UDA
			IProject projectStatics = root.getProject((String)context.get(Constants.STATICS_PATTERN));
			
			if (projectStatics != null){
				monitor.setTaskName("Generando JavaScript...");
				
				// Añadir el JS del mantenimiento al proyecto de estáticos
				path = ProjectWorker.createGetFolderPath(projectStatics, "WebContent/" + appCode + "/scripts/" + warName);
				ProjectWorker.createFileTemplate(pathWar, path, "maintSimple.js", context, entityName + ".js");
				
				monitor.worked(1);
				console.println("JavaScript generado en el proyecto de estáticos: " + (String)context.get(Constants.STATICS_PATTERN), Constants.MSG_INFORMATION);
				console.println("\t" + entityName + ".js", Constants.MSG_INFORMATION);
				// Refresca el proyecto de Statics
				ProjectWorker.refresh(projectStatics);
			}
			
			// Refresca el proyecto WAR
			ProjectWorker.refresh(projectWar);
			
			// Visualiza el sumario de tareas
			this.summary = createSummary(context);
		}catch(Exception e){
			console.println(e.toString(), Constants.MSG_ERROR);
			throw e;
		}		
		console.println("UDA - END", Constants.MSG_INFORMATION);
	}
	
	private List<GridColumn> filterGridColumnsActivated(List<GridColumn> gridColumns){
		List<GridColumn> filter = new ArrayList<GridColumn>(0);
		
		for (Iterator<GridColumn> iterator = gridColumns.iterator(); iterator.hasNext();) {
			GridColumn gridColumn = (GridColumn) iterator.next();
			
			if (gridColumn.getActivated()){
				filter.add(gridColumn);
			}
		}
		
		return filter;
	}
	
	//TODO: poner en la clase de utilidades
	private String getAppNameFromWar(IProject project){
		
		String appName = "";
		
		if (project != null){
			String projectName = project.getName();
			
			if (!Utilities.isBlank(projectName) && projectName.endsWith(Constants.WAR_NAME)){
				appName = projectName.substring(0, projectName.length() - Constants.WAR_NAME.length());
				
				int size = appName.length();
				
				for (int i = 0; i < size; i++) {
					if (Character.isUpperCase(appName.charAt(i))){
						appName = appName.substring(0, i);
						break;
					}
				}
			}	
		}
		return appName;
	}	
	
	/**
	 * Genera el texto que se sacará de sumario con las operaciones realizadas.
	 * @param context - contexto con la información del los proyectos
	 * @return - texto del sumario
	 */
	private String createSummary(Map<String, Object> context){
		StringBuffer summaryText = new StringBuffer();
		
		summaryText.append("\n\n- Generación del mantenimiento " + ((Maint)context.get(Constants.MAINT)).getNameMaint() + ".");
		summaryText.append("\n- Entidad a mantener " + (String)context.get(Constants.ENTITY_PATTERN) + ".");
		summaryText.append("\n- Proyecto WAR: " + (String)context.get(Constants.WAR_NAME_PATTERN));
		summaryText.append("\n\t - Generación de las JSPs.");
		summaryText.append("\n\t - Modificación del fichero de tiles.xml.");
		summaryText.append("\n- Proyecto de estáticos: " + (String)context.get(Constants.STATICS_PATTERN));		
		summaryText.append("\n\t - Generación de JavaScript.");
		
		
		return summaryText.toString();
	}
	
	
	
	//TODO: fichero de propiedades
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
	
	private void editTiles(String path, String entityName, String entityTableName, File xmlFile){
		try {
			String relativePathIncludesJsp = "/WEB-INF/views/" + entityName + "/" + entityName + "-includes.jsp";
			String relativePathJsp = "/WEB-INF/views/" + entityName + "/" + entityName + ".jsp";

			if (xmlFile.exists()) {
				// Crea la instancia de DocumentBuilderFactory
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

				factory.setValidating(false);
				factory.setNamespaceAware(true);
				factory.setIgnoringElementContentWhitespace(true);
				
				// recupera DocumentBuilder
				DocumentBuilder docBuilder = factory.newDocumentBuilder();
				
				docBuilder.setEntityResolver(new EntityResolver() {
					  @Override
					  public InputSource resolveEntity(String arg0, String arg1)
					        throws SAXException, IOException {
					    if(arg0.contains("Tiles")) {
					        return new InputSource(new StringReader(""));
					    } else {
					        // TODO Auto-generated method stub
					        return null;
					    }
					  }
					});

				
				// utiliza el fichero xml
				Document doc = docBuilder.parse(xmlFile);
				
				if (findNodeAttribute(doc, "definition", "name", entityName)){
					console.println("Mantenimiento ya definido en el tiles.xml", Constants.MSG_INFORMATION);
				}else{
					
					doc.getDoctype();
					doc.setXmlStandalone(true);
					
					// recoge el elemento raiz
					Element rootElement = doc.getDocumentElement();
					// crea un elemento nuevo de definition
					Element definitionElement = doc.createElement("definition");
					// Añade atributos
					definitionElement.setAttribute("extends", "template");
					definitionElement.setAttribute("name", entityName);
					rootElement.appendChild(definitionElement);
					// crea elemento hijo para la JSP
					Element contentElement = doc.createElement("put-attribute");
					contentElement.setAttribute("name", "content");
					contentElement.setAttribute("value", relativePathJsp);
					definitionElement.appendChild(contentElement);
					
					// crea elemento hijo para la JSP-include
					Element includesElement = doc.createElement("put-attribute");
					includesElement.setAttribute("name", "includes");
					includesElement.setAttribute("value", relativePathIncludesJsp);
					definitionElement.appendChild(includesElement);

					// configura transformer
					TransformerFactory transfac = TransformerFactory.newInstance();
					transfac.setAttribute("indent-number", 4);
					
					Transformer trans = transfac.newTransformer();
					// Añade las propiedades
					trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, Constants.TILES_CONFIG_CONFIG);
					trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, Constants.TILES_CONFIG_DTD);
					trans.setOutputProperty(OutputKeys.INDENT, "yes");
					trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
					
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean findNodeAttribute(Document document, String nodeName, String attributeName, String attributeValue){
		
		boolean match = false;
		
		if (document != null && !Utilities.isBlank(attributeName) && !Utilities.isBlank(attributeValue) && !Utilities.isBlank(nodeName)){
			NodeList nodeList = document.getElementsByTagName(nodeName);
			
			int size = nodeList.getLength();
			
			for(int i=0; i < size; i++){
				  Node childNode = nodeList.item(i);
				  
				  NamedNodeMap nodeMap = childNode.getAttributes();
				  
				  String valor =nodeMap.getNamedItem(attributeName).getNodeValue();
				  
				  if (attributeValue.equals(valor)){
					  match = true;
					  break;
				  }
			}
		}
				
		return match;
	}
	
}