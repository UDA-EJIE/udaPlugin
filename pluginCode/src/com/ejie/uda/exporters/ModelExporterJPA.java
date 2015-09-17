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
package com.ejie.uda.exporters;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.GenericExporter;

import com.ejie.uda.exporters.utils.ControllerUtils;
import com.ejie.uda.exporters.utils.PojoUtils;
import com.ejie.uda.exporters.utils.PojoUtilsJpa;
import com.ejie.uda.exporters.utils.WarningSupressorJPA;

/**
 * Exporter encargado de la generación de la capa de modelo (model) en JPA
 */
public class ModelExporterJPA extends GenericExporter {

	private final static Logger logger = Logger.getLogger(ModelExporterJPA.class);
	
	public ModelExporterJPA(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		//init();
	}

	protected void init() {
	}

	public void start(){
		try {
			Properties properties = getProperties();
    		properties.put("utilidades", new PojoUtils());
			properties.put("ejb3", "true");
			properties.put("jdk5", "true");
			properties.put("ctrUtils", new ControllerUtils());			
			properties.put("warSupressor", new WarningSupressorJPA());
			properties.put("utilesDto", new PojoUtilsJpa());
			
			properties.put("isDto", false);
			setTemplateName(TemplatePath.MODEL_JPA_FTL);
			setFilePattern(TemplatePath.MODEL_PATTERN);
			super.start();	
			
			properties.put("isDto", true);
			setTemplateName(TemplatePath.MODEL_JPA_DTO_FTL);
			setFilePattern(TemplatePath.MODEL_DTO_PATTERN);
			super.start();	
					
		} catch(Exception e){
			logger.error("", e);
		}
	}
	
	protected void setupContext() {
		getProperties().put("sessionFactoryName", "SessionFactory");
		super.setupContext();
	}

	/**
	 * JPA no requiere este método porque debe generar las claves compuestas como objetos
	 */
//	@SuppressWarnings("rawtypes")
//	protected void exportComponent(Map additionalContext, POJOClass element) {
//	}
}