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
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.ejie.uda.exporters.utils.ControllerUtils;

/**
 * Exporter encargado de la generación del fichero udaPersistence.xml en JPA
 */
public class PersistenceExporter extends GenericExporter {

	private final static Logger logger = Logger.getLogger(PersistenceExporter.class);

	public PersistenceExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		init();
	}

	protected void init() {
		setTemplateName(TemplatePath.PERSISTENCE_FTL);
		setFilePattern(TemplatePath.PERSISTENCE_PATTERN);
	}

	public void start() {
		try {
			String directory = getOutputDirectory().getParent();
			if (directory.toUpperCase().endsWith("EARCLASSES")) {
				Iterator<?> iterator = super.getCfg2JavaTool().getPOJOIterator(super.getConfiguration().getClassMappings());
				List<String> additionalContext = new ArrayList<String>();
				while (iterator.hasNext()) {
					POJOClass element = (POJOClass) iterator.next();
					PersistentClass clazz = (PersistentClass) element.getDecoratedObject();
					if (new Cfg2JavaTool().isComponent(element.getIdentifierProperty())) {
						additionalContext.add(element.getPackageName() + ".model." + ControllerUtils.findNameFromEntity(clazz.getEntityName()));
						additionalContext.add(element.getPackageName() + ".model." + ControllerUtils.findNameFromEntity(clazz.getEntityName()) + "Id");
					} else {
						additionalContext.add(element.getPackageName() + ".model." + ControllerUtils.findNameFromEntity(clazz.getEntityName()));
					}
				}
				
				String nombreWar = directory.substring(directory.lastIndexOf(File.separator) + 1, directory.length()-3);
				
				Properties properties = getProperties();
				properties.put("listaClases", additionalContext);
				properties.put("warName", nombreWar.split("[A-Z]")[0].toUpperCase());
				
				super.start();
					
			} else {
				throw new RuntimeException("El persistence se debe de crear en el proyecto EARClasses");
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