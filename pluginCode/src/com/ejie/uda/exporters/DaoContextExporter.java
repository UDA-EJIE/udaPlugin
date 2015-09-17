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
 * Exporter encargado de la generación del fichero dao-config.xml
 */
public class DaoContextExporter extends GenericExporter {
	
	private final static Logger logger = Logger.getLogger(DaoContextExporter.class);
	
	public DaoContextExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		init();
	}

	protected void init() {
		if ((new File(getOutputDirectory()	+ "/META-INF/udaPersistence.xml")).exists()) {
			setTemplateName(TemplatePath.DAO_CONTEXT_JPA_FTL);
		} else {
			setTemplateName(TemplatePath.DAO_CONTEXT_JDBC_FTL);
		}
		setFilePattern(TemplatePath.DAO_CONTEXT_PATTERN);
	}

	public void start() {
		try {
			String directory = getOutputDirectory().getParent();
			if (directory.toUpperCase().endsWith("EARCLASSES")) {
				Iterator<?> iterator = super.getCfg2JavaTool().getPOJOIterator(super.getConfiguration().getClassMappings());
				List<String[]> additionalContext = new ArrayList<String[]>();
				while (iterator.hasNext()) {
					POJOClass element = (POJOClass) iterator.next();
					PersistentClass clazz = (PersistentClass) element.getDecoratedObject();
					String nombre = ControllerUtils.findNameFromEntity(clazz.getEntityName());
					String[] auxiliar = {
							element.getPackageName() + ".dao." + nombre + "Dao",
							ControllerUtils.stringDecapitalize(nombre) + "Dao" 
					};
					additionalContext.add(auxiliar);

				}
				Properties properties = getProperties();
				properties.put("listaClases", additionalContext);
				properties.put("codapp", directory.substring(directory.lastIndexOf(File.separator) + 1, directory.toUpperCase().indexOf("EARCLASSES")));
				
				super.start();
	
			} else {
				throw new RuntimeException("Los ficheros de configuracion de los DAOs se deben generar en el proyecto EARClases");
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