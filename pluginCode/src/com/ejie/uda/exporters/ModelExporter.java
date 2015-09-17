package com.ejie.uda.exporters;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.GenericExporter;

/**
 * Exporter encargado de la generación de la capa de modelo (model) que invocará a su subExporter:
 * 		JPA => PojoExporterJPA 
 * 		Spring JDBC => PojoExporterSpringJDBC
 */
public class ModelExporter extends GenericExporter {

	private final static Logger logger = Logger.getLogger(ModelExporter.class);
	
	public ModelExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
	}

	public void start(){
		try {
	    	String directory = getOutputDirectory().getParent();
	    	if (directory.toUpperCase().endsWith("EARCLASSES") ){	
				Properties properties = getProperties();
	    		if ((Boolean) properties.get("isJpa")) {
	    			ModelExporterJPA modelExporter = new ModelExporterJPA( super.getConfiguration(),super.getOutputDirectory());
	    			modelExporter.setTemplatePath(super.getTemplatePath());
	    			modelExporter.start();
				}else{
					ModelExporterSpringJDBC modelExporter = new ModelExporterSpringJDBC( super.getConfiguration(),super.getOutputDirectory());
					modelExporter.setTemplatePath(super.getTemplatePath());
					modelExporter.start();
				}
			} else {
				throw new RuntimeException("El modelo se debe generar en el proyecto EARClases");
			}
		} catch(Exception e){
			logger.error("", e);
		}
	}
}