package com.ejie.uda.exporters;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.ejie.uda.exporters.utils.ControllerUtils;
import com.ejie.uda.exporters.utils.ControllerUtilsJdbc;
import com.ejie.uda.exporters.utils.ControllerUtilsJpa;

/**
 * Exporter encargado de la generación de la capa de control
 */
public class ControllerExporter extends GenericExporter {
	private final static Logger logger = Logger.getLogger(ControllerExporter.class);
	ControllerUtilsJdbc ctrlUtils = new ControllerUtilsJdbc();
	ControllerUtilsJpa ctrlUtilsJpa = new ControllerUtilsJpa();
	ControllerUtils ctrl = new ControllerUtils();

	public ControllerExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		init();
	}

	protected void init() {
		setTemplateName(TemplatePath.CONTROLLER_FTL);
		setFilePattern(TemplatePath.CONTROLLER_PATTERN);
	}

	public void start() {
		try {
			String directory = getOutputDirectory().getParent();
			if (directory.toUpperCase().endsWith("WAR")) {
				
				Properties properties = getProperties();
				properties.put("ejb3", "true");
				properties.put("jdk5", "true");
				properties.put("warName", directory.substring(directory.lastIndexOf(File.separator) + 1, directory.length()));
				
				properties.put("ctrl", new ControllerUtils());
				if ((Boolean) properties.get("isJpa")) {
					properties.put("ctrlUtils",  new ControllerUtilsJpa());
				} else {
					properties.put("ctrlUtils", new ControllerUtilsJdbc());
				}

				super.start();
				
			} else {
				throw new RuntimeException("Los controllers se deben generar en el proyecto WAR");
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