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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.ejie.uda.exporters.utils.ControllerUtils;


/**
 * Exporter encargado de la generación del fichero mvc-config.xml
 */
public class ControllerDIExporter extends GenericExporter {

	private final static Logger logger = Logger.getLogger(ControllerDIExporter.class);
	
	public ControllerDIExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		init();
	}

	protected void init() {
		setTemplateName(TemplatePath.DI_FTL);
		setFilePattern(TemplatePath.DI_PATTERN);
	}

	public void start() {
		try {
			String directory = getOutputDirectory().toString();
			if (directory.toUpperCase().endsWith("WAR")) {
				Iterator<?> iterator = super.getCfg2JavaTool().getPOJOIterator(super.getConfiguration().getClassMappings());
				List<String> additionalContext = new ArrayList<String>();
				while (iterator.hasNext()) {
					POJOClass element = (POJOClass) iterator.next();
					PersistentClass clazz = (PersistentClass) element.getDecoratedObject();
					String nombre = ControllerUtils.findNameFromEntity(clazz.getEntityName());
					additionalContext.add(nombre);
				}
				Collections.sort(additionalContext);
				
				String nombreWar = directory.substring(directory.lastIndexOf(File.separator) + 1, directory.length()-3);
				
				Properties properties = getProperties();
				properties.put("listaClases", additionalContext);
				properties.put("codapp", nombreWar.split("[A-Z]")[0]);
				properties.put("ctrUtils", new ControllerUtils());
	
				super.start();
				
			} else {
				throw new RuntimeException("Los ficheros de configuracion de los controllers se debe generar en el proyecto WAR");
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