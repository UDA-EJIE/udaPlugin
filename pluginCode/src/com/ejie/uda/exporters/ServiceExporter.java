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
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.ejie.uda.exporters.utils.ControllerUtils;
import com.ejie.uda.exporters.utils.ServiceUtilsJpa;

/**
 * Exporter encargado de la generación de la capa de servicio(service)
 */
public class ServiceExporter extends POJOExporter {

	private final static Logger logger = Logger.getLogger(ServiceExporter.class);

	public ServiceExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		//init();
	}

	protected void init() {
	}

	public void start() {
		try {
			String directory = getOutputDirectory().getParent();
	    	if (directory.toUpperCase().endsWith("EARCLASSES") ){	
				
				Properties properties = getProperties();
				properties.put("ctrTl", new ControllerUtils());
				
				if ((Boolean) properties.get("isJpa")) {
					getProperties().put("servJpa", new ServiceUtilsJpa());

					setTemplateName(TemplatePath.SERVICE_JPA_FTL);
					setFilePattern(TemplatePath.SERVICE_PATTERN);
					super.start();
					
					setTemplateName(TemplatePath.SERVICE_JPA_Impl_FTL);
					setFilePattern(TemplatePath.SERVICE_Impl_PATTERN);
					super.start();
	
				} else {
					
					setTemplateName(TemplatePath.SERVICE_JDBC_FTL);
					setFilePattern(TemplatePath.SERVICE_PATTERN);
					super.start();
					
					setTemplateName(TemplatePath.SERVICE_JDBC_Impl_FTL);
					setFilePattern(TemplatePath.SERVICE_Impl_PATTERN);
					super.start();
	
				}
			} else {
				throw new RuntimeException("Los servicios se deben generar en el proyecto EARClases");
			}
		} catch (Exception e) {
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