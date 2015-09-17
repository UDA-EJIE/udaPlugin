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
