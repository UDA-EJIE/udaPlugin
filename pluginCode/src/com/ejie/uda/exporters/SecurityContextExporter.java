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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.ejie.uda.exporters.utils.ControllerUtils;

/**
 * Exporter encargado de la generación del fichero security-config.xml
 */
public class SecurityContextExporter extends GenericExporter {

	private final static Logger logger = Logger.getLogger(SecurityContextExporter.class);

	public SecurityContextExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		init();
	}

	protected void init() {
		setTemplateName(TemplatePath.SECURITY_CONTEXT_FTL);
		setFilePattern(TemplatePath.SECURITY_CONTEXT_PATTERN);
	}

	public void start() {
		try {
 			Iterator<?> iterator = super.getCfg2JavaTool().getPOJOIterator(super.getConfiguration().getClassMappings());
			List<String> additionalContext = new ArrayList<String>();
			while (iterator.hasNext()) {
				POJOClass element = (POJOClass) iterator.next();
				PersistentClass clazz = (PersistentClass) element.getDecoratedObject();
				String nombre = ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(clazz.getEntityName()));
				additionalContext.add(nombre);
			}
			getProperties().put("listaClases", additionalContext);
			
			super.start();

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