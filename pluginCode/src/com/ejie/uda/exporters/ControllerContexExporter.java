package com.ejie.uda.exporters;

import java.io.File;
import java.util.ArrayList;
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
public class ControllerContexExporter extends GenericExporter {

	private final static Logger logger = Logger.getLogger(ControllerContexExporter.class);
	
	public ControllerContexExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		init();
	}

	protected void init() {
		setTemplateName(TemplatePath.CONTROLLER_CONTEXT_FTL);
		setFilePattern(TemplatePath.CONTROLLER_CONTEXT_PATTERN);
	}

	public void start() {
		try {
			String directory = getOutputDirectory().toString();
			if (directory.toUpperCase().endsWith("WAR")) {
				Iterator<?> iterator = super.getCfg2JavaTool().getPOJOIterator(super.getConfiguration().getClassMappings());
				List<String[]> additionalContext = new ArrayList<String[]>();
				while (iterator.hasNext()) {
					POJOClass element = (POJOClass) iterator.next();
					PersistentClass clazz = (PersistentClass) element.getDecoratedObject();
					String nombre = ControllerUtils.findNameFromEntity(clazz.getEntityName());
					String[] auxiliar = {
							element.getPackageName() + ".control." + nombre + "Controller",
							ControllerUtils.stringDecapitalize(nombre) + "Service",
							ControllerUtils.stringDecapitalize(nombre) 
					};
					additionalContext.add(auxiliar);
				}
				
				String nombreWar = directory.substring(directory.lastIndexOf(File.separator) + 1, directory.length()-3);
				
				Properties properties = getProperties();
				properties.put("listaClases", additionalContext);
				properties.put("codapp", nombreWar.split("[A-Z]")[0]);
				properties.put("nombreWar", nombreWar);
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