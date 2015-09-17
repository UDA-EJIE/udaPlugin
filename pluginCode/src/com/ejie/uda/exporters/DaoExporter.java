package com.ejie.uda.exporters;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.ejie.uda.exporters.utils.ControllerUtils;
import com.ejie.uda.exporters.utils.DaoGenerationJdbc;
import com.ejie.uda.exporters.utils.DaoUtilsJPA;
import com.ejie.uda.exporters.utils.DaoUtilsJdbc;
import com.ejie.uda.exporters.utils.DaoUtilsJdbcAux;
import com.ejie.uda.exporters.utils.PojoUtils;
import com.ejie.uda.exporters.utils.WarningSupressorJPA;
import com.ejie.uda.exporters.utils.WarningSupressorJdbc;

/**
 * Exporter encargado de la generación de la capa de acceso a datos (DAO)
 */
public class DaoExporter extends GenericExporter{

    private final static Logger logger = Logger.getLogger(DaoExporter.class);

    public DaoExporter(Configuration cfg, File outputdir) {
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
	    		properties.put("utilidades",new PojoUtils());
	    		properties.put("ctrTl",new ControllerUtils());
	    		
	    		if ((Boolean) properties.get("isJpa")) {
					properties.put("ejb3", "true");
					properties.put("jdk5", "true");
					properties.put("daoUtilities", new DaoUtilsJPA());
					properties.put("warSupressor",new WarningSupressorJPA());
					properties.put("app", directory.substring(directory.lastIndexOf(File.separator) + 1, directory.toUpperCase().indexOf("EARCLASSES")));
					
					setTemplateName(TemplatePath.DAO_JPA_FTL);
					setFilePattern(TemplatePath.DAO_PATTERN);   
			        super.start();
			        
			        setTemplateName(TemplatePath.DAO_JPA_Impl_FTL);
					setFilePattern(TemplatePath.DAO_Impl_PATTERN);   
			        super.start();
			        
				} else {
					properties.put("ejb3", "false");
					properties.put("jdk5", "false");
					properties.put("daoUtilities", new DaoUtilsJdbc());
					properties.put("warSupresor", new WarningSupressorJdbc());
					properties.put("gener", new DaoGenerationJdbc());
					properties.put("utilidadesDao", new DaoUtilsJdbcAux());
					
					setTemplateName(TemplatePath.DAO_JDBC_FTL);
			        setFilePattern(TemplatePath.DAO_PATTERN);   
			        super.start();
			        
			        setTemplateName(TemplatePath.DAO_JDBC_Impl_FTL);
			        setFilePattern(TemplatePath.DAO_Impl_PATTERN);   
			        super.start();
			        
				}
	    	} else {
	    		throw new RuntimeException("Los DAOs se deben generar en el proyecto EARClases");
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