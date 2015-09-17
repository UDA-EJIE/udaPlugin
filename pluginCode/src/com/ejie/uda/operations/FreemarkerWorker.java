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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;

import freemarker.template.Configuration;

/**
 * Clase de tratamiento de ficheros con platillas freemarker 
 *
 */
public class FreemarkerWorker {

	/**
	 * Constructor
	 */
	private FreemarkerWorker() {
		//No es instanciable
	}

	/**
	 * Mezcla los datos con las plantillas freemarker
	 * 
	 * @param templatePath - ruta de la plantilla
	 * @param templateName - nombre de la plantilla
	 * @param context - datos de contexto
	 * @param outputFile - fichero de salida
	 * @throws Exception
	 */
	public static void executeOperation (String templatePath, String templateName,
			Map<String, Object> context, File outputFile) throws Exception {
		
		ConsoleLogger consola = ConsoleLogger.getDefault();
        
        try{
        	
        	// Inicia la configuración de freemarker
	        Configuration cfg = new Configuration();
	        	        
	        // Localización de las plantillas
	        cfg.setDirectoryForTemplateLoading(new File(templatePath));
	        // Recupera la plantilla
	        freemarker.template.Template tpl = cfg.getTemplate(templateName, Constants.ENCODING_UTF8);
	        // Mezcla la template con los datos y lo escribe en la salida
	        FileOutputStream fos = new FileOutputStream(outputFile);
	        Writer fileWriter = new OutputStreamWriter(fos, Constants.ENCODING_UTF8);
	        tpl.process(context, fileWriter);
	        
	        fileWriter.flush();
	        fileWriter.close();
	        
        }catch (Exception e) {
        	consola.println("Error freemarker: " + e.getMessage(), Constants.MSG_ERROR);
        }
	}
}
