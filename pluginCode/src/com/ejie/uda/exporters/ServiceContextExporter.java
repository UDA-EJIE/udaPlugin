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
import com.ejie.uda.exporters.utils.ServiceUtilsJpa;

/**
 * Exporter encargado de la generación del fichero service-config.xml
 */
public class ServiceContextExporter extends GenericExporter {

	private final static Logger logger = Logger.getLogger(ServiceContextExporter.class);

	public ServiceContextExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		init();
	}

	protected void init() {
		setTemplateName(TemplatePath.SERVICE_CONTEXT_FTL);
		setFilePattern(TemplatePath.SERVICE_CONTEXT_PATTERN);
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
							element.getPackageName() + ".service." + nombre	+ "Service",
							ControllerUtils.stringDecapitalize(nombre) + "Dao",
							ControllerUtils.stringDecapitalize(nombre) + "Service" };
					additionalContext.add(auxiliar);
					if ((Boolean) getProperties().get("isJpa")) {
						// miramos las M:N porque también hay injectarlas
						List<String> lista = ServiceUtilsJpa.getRelatedEntities(element, super.getConfiguration());
						Iterator<String> iterador = lista.iterator();
						while (iterador.hasNext()) {
							String[] aux = {element.getPackageName() + ".service." + nombre + "Service",
									ControllerUtils.stringDecapitalize(iterador.next()) + "Dao",
									ControllerUtils.stringDecapitalize(nombre) + "Service" 
							};
							additionalContext.add(aux);
						}
					}
				}
				
				Properties properties = getProperties();
				properties.put("listaClases", additionalContext);
				properties.put("codapp", directory.substring(directory.lastIndexOf(File.separator) + 1, directory.toUpperCase().indexOf("EARCLASSES")));
				
				super.start();

			} else {
				throw new RuntimeException("Los ficheros de configuracion de los servicios se deben generar en el proyecto EARClases");
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