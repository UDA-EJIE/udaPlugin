package com.ejie.uda.operations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

import com.ejie.uda.exporters.ControllerContexExporter;
import com.ejie.uda.exporters.ControllerExporter;
import com.ejie.uda.exporters.DaoContextExporter;
import com.ejie.uda.exporters.DaoExporter;
import com.ejie.uda.exporters.ModelExporter;
import com.ejie.uda.exporters.PersistenceExporter;
import com.ejie.uda.exporters.Reveng;
import com.ejie.uda.exporters.SecurityContextExporter;
import com.ejie.uda.exporters.ServiceContextExporter;
import com.ejie.uda.exporters.ServiceExporter;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.ConsoleLogger;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.TreeNode;
import com.ejie.uda.utils.Utilities;
/**
 * Clase de tratamiento de ficheros con platillas freemarker
 * 
 */
public class GenerateCodeWorker {

	private final static Logger logger = Logger.getLogger(GenerateCodeWorker.class);
	
	/**
	 * Constructor
	 */
	private GenerateCodeWorker() {
	}

	public static JDBCMetaDataConfiguration getConfigurationReveng(ConnectionData conData, String appName, boolean isJPA, String revengXML){
		JDBCMetaDataConfiguration jmdc = null;
		InputStream is = null;
		try {
			if (conData != null && !Utilities.isBlank(appName)){
				jmdc = DataBaseWorker.getConfiguration(conData);
				if (isJPA) {
					jmdc.setPreferBasicCompositeIds(true); // CÓMO DEBERÍA SER ????? 
				} else {
					jmdc.setPreferBasicCompositeIds(false); // CÓMO DEBERÍA SER ????? 
				}
				ReverseEngineeringStrategy reveng = DataBaseWorker.getReveng();
				reveng.setSettings(Reveng.getRevengSettings(reveng, true, appName));
				jmdc.setReverseEngineeringStrategy(reveng);
				if (!Utilities.isBlank(revengXML)){
					is = new ByteArrayInputStream(revengXML.getBytes());
					OverrideRepository or = new OverrideRepository();
					or.addInputStream(is);
					ReverseEngineeringStrategy revengAux = or.getReverseEngineeringStrategy(reveng);
					jmdc.setReverseEngineeringStrategy(revengAux);
				}
				jmdc.readFromJDBC();
				jmdc.buildMappings();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return jmdc;
	}
	
	/**
	 * Lanzamiento modelExporter [model]
	 */
	public static void modelExporter(JDBCMetaDataConfiguration jmdc, String pathTemplates, String pathProject, boolean isJPA) {
		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");

		ModelExporter modelExporter = new ModelExporter(jmdc, sourceDir);
		modelExporter.setTemplatePath(templates);
		modelExporter.getProperties().put("isJpa", isJPA);
		
		modelExporter.start();
	}
	
	/**
	 * Lanzamiento persistenceExporter [persistence.xml] (solo JPA)
	 */
	public static void persistenceExporter(JDBCMetaDataConfiguration jmdc, String pathTemplates, String pathProject) {
		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		
		PersistenceExporter persistenceExporter = new PersistenceExporter(jmdc, sourceDir);
		persistenceExporter.setTemplatePath(templates);

		persistenceExporter.start();
	}
	
	
	/**
	 * Lanzamiento daoExporter [dao]
	 */
	public static void daoExporter(JDBCMetaDataConfiguration jmdc, String pathTemplates, String pathProject, boolean annotCheck, boolean isJPA) {
		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		
		DaoExporter daoExporter = new DaoExporter(jmdc, sourceDir);
		daoExporter.setTemplatePath(templates);
		if (annotCheck){
			daoExporter.getProperties().put("annot", new Long(1));
		}else{
			daoExporter.getProperties().put("annot", new Long(0));
		}
		daoExporter.getProperties().put("isJpa", isJPA);

		daoExporter.start();
	}
	
	/**
	 * Lanzamiento daoContextExporter [dao-config.xml]
	 */
	public static void daoContextExporter (JDBCMetaDataConfiguration jmdc, String pathTemplates, String pathProject, boolean annotCheck) {
		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		
		DaoContextExporter daoContextExporter = new DaoContextExporter(jmdc, sourceDir);
		daoContextExporter.setTemplatePath(templates);
		if (annotCheck){
			daoContextExporter.getProperties().put("annot", new Long(1));
		}else{
			daoContextExporter.getProperties().put("annot", new Long(0));
		}
		
		daoContextExporter.start();
	}
	
	
	/**
	 * Lanzamiento serviceExporter [service]
	 */
	public static void serviceExporter(JDBCMetaDataConfiguration jmdc, String pathTemplates, String pathProject, boolean annotCheck, boolean isJPA) {
		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");

		ServiceExporter serviceExporter = new ServiceExporter(jmdc, sourceDir);
		serviceExporter.setTemplatePath(templates);
		if (annotCheck){
			serviceExporter.getProperties().put("annot", new Long(1));
		}else{
			serviceExporter.getProperties().put("annot", new Long(0));
		}
		serviceExporter.getProperties().put("isJpa", isJPA);
		
		serviceExporter.start();
	}
	
	/**
	 * Lanzamiento serviceContextExporter [service-config.xml]
	 */
	public static void serviceContextExporter (JDBCMetaDataConfiguration jmdc, String pathTemplates, String pathProject, boolean annotCheck, boolean isJPA) {
		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");

		ServiceContextExporter serviceContextExporter = new ServiceContextExporter(jmdc, sourceDir);
		serviceContextExporter.setTemplatePath(templates);
		if (annotCheck){
			serviceContextExporter.getProperties().put("annot", new Long(1));
		}else{
			serviceContextExporter.getProperties().put("annot", new Long(0));
		}
		serviceContextExporter.getProperties().put("isJpa",isJPA);
		
		serviceContextExporter.start();
	}
	
	
	/**
	 * Lanzamiento controllerExporter [control]
	 */
	public static void controllerExporter(JDBCMetaDataConfiguration jmdc, String pathTemplates, String pathProject, boolean annotControlCheck, boolean isJPA) {
		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		
		ControllerExporter controllerExporter = new ControllerExporter(jmdc, sourceDir);
		controllerExporter.setTemplatePath(templates);
		if (annotControlCheck){
			controllerExporter.getProperties().put("annot", new Long(1));
		}else{
			controllerExporter.getProperties().put("annot", new Long(0));
		}
		controllerExporter.getProperties().put("isJpa", isJPA);
		
		controllerExporter.start();
	}
	
	/**
	 * Lanzamiento controllerContextExporter [mvc-config.xml]
	 */
	public static void controllerContextExporter (JDBCMetaDataConfiguration jmdc, String pathTemplates, String pathProject, boolean annotControlCheck, String appName) {
		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\");
		
		ControllerContexExporter controllerContexExporter = new ControllerContexExporter(jmdc, sourceDir);
		controllerContexExporter.setTemplatePath(templates);
		if (annotControlCheck){
			controllerContexExporter.getProperties().put("annot", new Long(1));
		}else{
			controllerContexExporter.getProperties().put("annot", new Long(0));
		}
		controllerContexExporter.getProperties().put("codapp", appName);
		
		controllerContexExporter.start();
	}

	/**
	 * Lanzamiento securityContextExporter [security-config.xml] (WAR)
	 */
	public static void securityContextExporter (JDBCMetaDataConfiguration jmdc, String pathTemplates, String pathProject, boolean chkXLNets, String chkIdXlNets) {
		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "/");
		
		SecurityContextExporter securityContexExporter = new SecurityContextExporter(jmdc, sourceDir);
		securityContexExporter.setTemplatePath(templates);
		if (chkXLNets){
			if (chkIdXlNets.contains("','ROLE")){
				securityContexExporter.getProperties().put("codrole", chkIdXlNets);
				securityContexExporter.getProperties().put("codroleAux", "hasAnyRole("+chkIdXlNets+")");
			}	else{
				securityContexExporter.getProperties().put("codrole", chkIdXlNets);
				securityContexExporter.getProperties().put("codroleAux", "hasRole("+chkIdXlNets+")");
				
			}
			securityContexExporter.getProperties().put("idSecurity", "123");
		}else{
			securityContexExporter.getProperties().put("codrole", "UDA");
			securityContexExporter.getProperties().put("codroleAux", "hasRole('ROLE_UDA')");
			securityContexExporter.getProperties().put("idSecurity", "");
		}
		
		securityContexExporter.start();
	}

	/**
	 * Obtención fichero reveng.xml para el filtro de tablas y columnas
	 */
	public static String getRevengXML(List<TreeNode> schemaFilter){
		
		StringBuffer xml = new StringBuffer();
		StringBuffer xmlLog = new StringBuffer();
		List<TreeNode> treeNodesColumns;
		List<TreeNode> treeNodesComposite;
		Map<String, String> listaMN = new HashMap<String, String>();
		
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append(System.getProperty("line.separator"));
		xml.append("<!DOCTYPE hibernate-reverse-engineering PUBLIC \"-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-reverse-engineering-3.0.dtd\" >");
		xml.append(System.getProperty("line.separator"));
		xml.append("<hibernate-reverse-engineering>");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t<type-mapping>");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t\t<sql-type jdbc-type=\"CHAR\" hibernate-type=\"java.lang.String\"/>");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t\t<sql-type jdbc-type=\"TIMESTAMP\" hibernate-type=\"java.sql.Timestamp\"/>");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t\t<sql-type jdbc-type=\"OTHER\" hibernate-type=\"java.sql.Timestamp\" />");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t</type-mapping>");
		xml.append(System.getProperty("line.separator"));

		
		for (TreeNode treeNodeTable : schemaFilter) {
			xml.append("\t<table-filter match-name=\"" + treeNodeTable.getNameBBDD() + "\" />");
			xml.append(System.getProperty("line.separator"));
		}	
		
		xmlLog.append("Elementos seleccionados (Sinonimo [Tabla]):");
		
		for (TreeNode treeNodeTable : schemaFilter) {
			
			if (!treeNodeTable.isMN()){
				xmlLog.append(System.getProperty("line.separator"));
				xmlLog.append("\t" + treeNodeTable.getName() + " [" + treeNodeTable.getNameBBDD() + "]");
			} else {
				if (listaMN.get(treeNodeTable.getName())==null){
					listaMN.put(treeNodeTable.getName(), treeNodeTable.getNameBBDD());
				}
			}
			
			if (!treeNodeTable.isCheckedChildren()){

				xmlLog.append(" con las siguientes columnas excluidas:");
				
				xml.append("\t<table name=\"" + treeNodeTable.getNameBBDD() + "\">");
				xml.append(System.getProperty("line.separator"));
				
				treeNodesColumns = treeNodeTable.getChildren();
				
				for (TreeNode treeNodeColumn : treeNodesColumns) {
					
					if (!treeNodeColumn.isComposite()){
						if (!treeNodeColumn.isChecked()){
							xml.append("\t\t<column name=\"" + treeNodeColumn.getNameBBDD() + "\" exclude=\"true\"/>");
							xml.append(System.getProperty("line.separator"));
							
							xmlLog.append(System.getProperty("line.separator"));
							xmlLog.append("\t\t" + treeNodeColumn.getName() + " [" + treeNodeColumn.getNameBBDD() + "]");
						}
					}else{
						treeNodesComposite = treeNodeColumn.getChildren();
						for (TreeNode treeNodeComposite : treeNodesComposite) {
							if (!treeNodeComposite.isChecked()){
								xml.append("\t\t<column name=\"" + treeNodeComposite.getNameBBDD() + "\" exclude=\"true\"/>");
								xml.append(System.getProperty("line.separator"));
								
								xmlLog.append(System.getProperty("line.separator"));
								xmlLog.append("\t\t" + treeNodeColumn.getName() + " [" + treeNodeColumn.getNameBBDD() + "]");
							}
						}
					}
				}
				xml.append("\t</table>");
				xml.append(System.getProperty("line.separator"));
			} 
		}
		
		if (!listaMN.isEmpty()){
			xmlLog.append(System.getProperty("line.separator"));
			xmlLog.append("Tablas implicadas en relaciones M-N:");
			for (Iterator<String> iterator = listaMN.keySet().iterator(); iterator.hasNext();) {
				String tableName = (String) iterator.next();
				xmlLog.append(System.getProperty("line.separator"));
				xmlLog.append("\t" + tableName + " [" + listaMN.get(tableName) + "]");
			}
		}
		
		xml.append("</hibernate-reverse-engineering>");
		ConsoleLogger consola = ConsoleLogger.getDefault();
		consola.println(xmlLog.toString(), Constants.MSG_INFORMATION);
		return xml.toString();
	}

}