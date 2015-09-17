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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.eclipse.ant.core.AntCorePlugin;

import com.ejie.uda.utils.Constants;

/**
 * Clase estática encargada de ejecutar tareas ant
 *
 */
public class AntTaskWorker {
	
	private static Project ant = null;
	
	/**
	 * Constructor
	 */
	private AntTaskWorker() {
		//No es instanciable
	}
	
	/**
	 * Ejecuta una tarea ANT pasándole la ruta del build.xml y el nombre del target
	 * 
	 * @param pathProject - ruta raiz del proyecto
	 * @param taskTarget - nombre del target a ejecutar
	 * @throws Exception
	 */
	public static void executeOperation (String pathProject, String taskTarget) throws Exception {
		// Inicializa el proyecto Ant de ejecución en eclipse
		init(pathProject + "/" + Constants.ANT_BUILD_FILE, pathProject);
		
		// Asigna las propiedades necesarias par ala ejecución
		Map<String,String> propMap = new HashMap<String,String>();
		propMap.put("App.BaseDir",pathProject);
		//propMap.put("ant.home",Constants.ANT_HOME);
		propMap.put("ant.home", AntCorePlugin.getPlugin().getPreferences().getAntHome());
		setProperties(propMap,true);
		
		// Ejecuta el target ANT
		runTarget(taskTarget);
	}

	/**
	 * Inicializa y configura el proyecto de ejecución ANT
	 * 
	 * @param buildFile - ruta del fichero de build.xml de Ant
	 * @param baseDir - ruta del proyecto
	 * @throws Exception
	 */
	private static void init(String buildFile, String baseDir) throws Exception{
		ant = new Project();
		ant.init();
		
		if(buildFile == null){
			buildFile = Constants.ANT_BUILD_FILE;
		}
		
		if(baseDir == null){
			baseDir = ".";
		}

		ant.setBasedir(baseDir);
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		ant.setProjectReference(helper);
		helper.parse(ant, new File(buildFile));
		
		DefaultLogger logger = new DefaultLogger();
		logger.setErrorPrintStream(System.err);
		logger.setOutputPrintStream(System.out);
		logger.setMessageOutputLevel(Project.MSG_INFO);
		
		ant.addBuildListener(logger);
		
	}
	
	/**
	 * Asigna las propiedades al proyecto de ejecución ANT. Las  propiedades suelen ser al directorio base y al ANT_HOME para su ejecución
	 * 
	 * @param propertyMap - mapa de propiedades
	 * @param overridable - reemplaza las propiedades por defecto de ANT 
	 * @throws Exception
	 */
	private static void setProperties(Map<String,String> propertyMap, boolean overridable ) throws Exception{
		if(ant == null){
			throw new Exception("Las propiedades no pueden ser asignadas, proyecto Ant no inicializado.");
		}
		
		if(propertyMap == null){
			throw new Exception("Las propiedades del proyecto Ant son nulas.");
		}
		
		Set<String> propKeys = propertyMap.keySet();
		Iterator<String> iterator = propKeys.iterator();
		
		while (iterator.hasNext()) {
			String propertyKey = (String) iterator.next();
			String propertyValue = propertyMap.get(propertyKey);
			
			if(propertyValue == null) continue;
			
			if(overridable){
				ant.setProperty(propertyKey,propertyValue);
			} else {
				ant.setUserProperty(propertyKey,propertyValue);
			}
		}
		ant.setSystemProperties();
	}

	/**
	 * Ejecuta la tarea ANT pasándole el nombre del target
	 * @param targetName - nombre del target de ejecuición
	 * @throws Exception
	 */
	private static void runTarget(String targetName) throws Exception{
		if(ant == null){
			throw new Exception("Las propiedades no pueden ser asignadas, proyecto Ant no inicializado.");
		}
		
		if(targetName == null){
			targetName = ant.getDefaultTarget();
		}
		
		if(targetName == null){
			throw new Exception("Target no encontrado :" + targetName);
		}
		
		//Ejecuta el target
		ant.executeTarget(targetName);
	}

}