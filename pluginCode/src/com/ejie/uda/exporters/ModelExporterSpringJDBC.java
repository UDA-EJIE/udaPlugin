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
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.ejie.uda.exporters.utils.ControllerUtilsJdbc;
import com.ejie.uda.exporters.utils.PojoUtils;
import com.ejie.uda.exporters.utils.WarningSupressorJdbc;

/**
 * Exporter encargado de la generación de la capa de modelo (model) en SpringJDBC
 */
public class ModelExporterSpringJDBC extends GenericExporter {

	private final static Logger logger = Logger.getLogger(ModelExporterSpringJDBC.class);
	
	public ModelExporterSpringJDBC(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		//init();
	}

	protected void init() {
	}

	public void start(){
		try {
			Properties properties = getProperties();
    		properties.put("utilidades", new PojoUtils());
    		properties.put("ejb3", "false");
			properties.put("jdk5", "false");
			properties.put("warSupresor", new WarningSupressorJdbc());
			properties.put("ctrlUtils", new ControllerUtilsJdbc());
			
			setTemplateName(TemplatePath.MODEL_JDBC_FTL);
			setFilePattern(TemplatePath.MODEL_PATTERN);
			super.start();
			
		} catch(Exception e){
			logger.error("", e);
		}
	}
	
	protected void setupContext() {
		getProperties().put("sessionFactoryName", "SessionFactory");
		super.setupContext();
	}
	
	@SuppressWarnings("rawtypes")
	protected void exportComponent(Map additionalContext, POJOClass element) {
	}
}