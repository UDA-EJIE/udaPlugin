package com.ejie.uda.operations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

import com.ejie.uda.ControllerContexExporter;
import com.ejie.uda.ControllerExporter;
import com.ejie.uda.DaoContextExporter;
import com.ejie.uda.DaoExporter;
import com.ejie.uda.PersistenceExporter;
import com.ejie.uda.PojoExporter;
import com.ejie.uda.Reveng;
import com.ejie.uda.SecurityContextExporter;
import com.ejie.uda.ServiceContextExporter;
import com.ejie.uda.ServiceExporter;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.TreeNode;
import com.ejie.uda.utils.Utilities;
/**
 * Clase de tratamiento de ficheros con platillas freemarker
 * 
 */
public class GenerateCodeWorker {

	/**
	 * Constructor
	 */
	private GenerateCodeWorker() {
		// No es instanciable
	}

	public static JDBCMetaDataConfiguration getConfigurationReveng(ConnectionData conData, String appName, boolean isJPA, String revengXML){
		JDBCMetaDataConfiguration jmdc = null;
		InputStream is = null;

		try {
			
			if (conData != null && !Utilities.isBlank(appName)){
				jmdc = DataBaseWorker.getConfiguration(conData);
				
				if (isJPA) {
					jmdc.setPreferBasicCompositeIds(true);
					jmdc.setProperty("reversestrategy", "Reveng");
				} else {
					jmdc.setPreferBasicCompositeIds(false);
					jmdc.setProperty("reversestrategy", "RevengJdbc");
				}
				
				String packageName = "com.ejie." + appName;
				
				DefaultReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy();
				ReverseEngineeringStrategy reveng = null;
				
				if (!Utilities.isBlank(revengXML)){
					
					is = new ByteArrayInputStream(revengXML.getBytes());
					
					OverrideRepository or = new OverrideRepository();
					or.addInputStream(is);
			
					reveng = new Reveng(strategy);
					ReverseEngineeringSettings  settings2 = new ReverseEngineeringSettings(reveng);
					settings2.setDefaultPackageName(packageName);
					settings2.setDetectManyToMany(true);
					settings2.setDetectOneToOne(true);
					settings2.setDetectOptimisticLock(false);
					reveng.setSettings(settings2);
					jmdc.setReverseEngineeringStrategy(reveng);
					ReverseEngineeringStrategy revengAux = or.getReverseEngineeringStrategy(new Reveng(strategy));
					jmdc.setReverseEngineeringStrategy(revengAux);
		
				}else{
					reveng = new Reveng(strategy);
					ReverseEngineeringSettings settings = new ReverseEngineeringSettings(reveng);
					settings.setDefaultPackageName(packageName);
					settings.setDetectManyToMany(true);
					settings.setDetectOneToOne(true);
					settings.setDetectOptimisticLock(false);
					reveng.setSettings(settings);
					jmdc.setReverseEngineeringStrategy(reveng);
				}
				
				jmdc.readFromJDBC();
				jmdc.buildMappings();
			}
			
			

		} catch (Exception e) {
			System.out.println("ERROR:" + e.getMessage() + ". " + e.getCause());
		}
		return jmdc;
	}
	
	/**
	 * Lanzamiento pojoExporter
	 * 
	 * @param jmdc - configuracion JDBC
	 * @param pathTemplates - ruta de la plantilla
	 * @param pathProject - PathProjecto
	 */
	public static void pojoExporter(JDBCMetaDataConfiguration jmdc,
			String pathTemplates, String pathProject) {

		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		PojoExporter pojoExporter = new PojoExporter(jmdc, sourceDir);

		// Configura la ruta de las plantillas
		pojoExporter.setTemplatePath(templates);

		// Ejecuta el exporter
		pojoExporter.start();
		

	}
	/**
	 * Lanzamiento daoExporter
	 * 
	 * @param jmdc - configuracion JDBC
	 * @param pathTemplates - ruta de la plantilla
	 * @param pathProject - PathProjecto
	 * @param annotCheck - Check de generación mediante anotaciones/XML
	 */
	public static void daoExporter(JDBCMetaDataConfiguration jmdc,
			String pathTemplates, String pathProject, boolean annotCheck) {

		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		DaoExporter daoExporter = new DaoExporter(jmdc, sourceDir);

		// Configura la ruta de las plantillas
		daoExporter.setTemplatePath(templates);
		if (annotCheck){
			daoExporter.getProperties().put("annot", new Long(1));
		}else{
			daoExporter.getProperties().put("annot", new Long(0));
		}
		// Ejecuta el exporter
		daoExporter.start();
	}
	/**
	 * Lanzamiento serviceExporter
	 * 
	 * @param jmdc - configuracion JDBC
	 * @param pathTemplates - ruta de la plantilla
	 * @param pathProject - PathProjecto
	 * @param annotCheck - Check de generación mediante anotaciones/XML
	 */
	public static void serviceExporter(JDBCMetaDataConfiguration jmdc,
			String pathTemplates, String pathProject, boolean annotCheck) {

		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		ServiceExporter serviceExporter = new ServiceExporter(jmdc, sourceDir);

		// Configura la ruta de las plantillas
		serviceExporter.setTemplatePath(templates);
		if (annotCheck){
			serviceExporter.getProperties().put("annot", new Long(1));
		}else{
			serviceExporter.getProperties().put("annot", new Long(0));
		}
		// Ejecuta el exporter
		serviceExporter.start();
	}
	/**
	 * Lanzamiento daoExporter
	 * 
	 * @param jmdc - configuracion JDBC
	 * @param pathTemplates - ruta de la plantilla
	 * @param pathProject - PathProjecto
	 * @param annotControlCheck - Check de generación mediante anotaciones/XML
	 * @param isJPA - Check que indica si la persistencia es JPA o JDBC
	 */
	public static void controllerExporter(JDBCMetaDataConfiguration jmdc,
			String pathTemplates, String pathProject, boolean annotControlCheck, boolean isJPA) {

		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		ControllerExporter controllerExporter = new ControllerExporter(jmdc,
				sourceDir);

		// Configura la ruta de las plantillas
		controllerExporter.setTemplatePath(templates);
		if (annotControlCheck){
			controllerExporter.getProperties().put("annot", new Long(1));
		}else{
			controllerExporter.getProperties().put("annot", new Long(0));
		}
		if(isJPA){
			controllerExporter.getProperties().put("isJpa", new Long(1));
		}else{
			controllerExporter.getProperties().put("isJpa", new Long(0));
		}
		// Ejecuta el exporter
		controllerExporter.start();
		
	}
	/**
	 * Lanzamiento persistenceExporter
	 * 
	 * @param jmdc - configuracion JDBC
	 * @param pathTemplates - ruta de la plantilla
	 * @param pathProject - PathProjecto
	 */
	public static void persistenceExporter(JDBCMetaDataConfiguration jmdc,
			String pathTemplates, String pathProject) {

		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		PersistenceExporter persistenceExporter = new PersistenceExporter(jmdc,
				sourceDir);

		// Configura la ruta de las plantillas
		persistenceExporter.setTemplatePath(templates);

		// Ejecuta el exporter
		persistenceExporter.start();
	
	}
	/**
	 * Lanzamiento serviceContextExporter
	 * 
	 * @param jmdc - configuracion JDBC
	 * @param pathTemplates - ruta de la plantilla
	 * @param pathProject - PathProjecto
	 * @param annotCheck - Check de generación mediante anotaciones/XML
	 * @param isJPA - Check que indica si la persistencia es JPA o JDBC
	 */
	public static void serviceContextExporter (JDBCMetaDataConfiguration jmdc,
			String pathTemplates, String pathProject, boolean annotCheck, boolean isJPA) {

		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\src\\");
		ServiceContextExporter serviceContextExporter = new ServiceContextExporter(jmdc, sourceDir);
		serviceContextExporter.setTemplatePath(templates);
		if (annotCheck){
			serviceContextExporter.getProperties().put("annot", new Long(1));
		}else{
			serviceContextExporter.getProperties().put("annot", new Long(0));
		}
		if(isJPA){
			serviceContextExporter.getProperties().put("isJpa",true);
		}else{
			serviceContextExporter.getProperties().put("isJpa",false);
		}
		serviceContextExporter.start();
	
	}
	/**
	 * Lanzamiento daoContextExporter
	 * 
	 * @param jmdc - configuracion JDBC
	 * @param pathTemplates - ruta de la plantilla
	 * @param pathProject - PathProjecto
	 * @param annotCheck - Check de generación mediante anotaciones/XML
	 */
	public static void DaoContextExporter (JDBCMetaDataConfiguration jmdc,
			String pathTemplates, String pathProject, boolean annotCheck) {

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
	 * Lanzamiento controllerContextExporter
	 * 
	 * @param jmdc - configuracion JDBC
	 * @param pathTemplates - ruta de la plantilla
	 * @param pathProject - PathProjecto
	 * @param annotControlCheck - Check de generación mediante anotaciones/XML
	 * @param appName - Nombre Aplicacion
	 */
	public static void ControllerContextExporter (JDBCMetaDataConfiguration jmdc,
			String pathTemplates, String pathProject, boolean annotControlCheck, String appName) {

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
	 * Lanzamiento securityContextExporter
	 * 
	 * @param jmdc - configuracion JDBC
	 * @param pathTemplates - ruta de la plantilla
	 * @param pathProject - PathProjecto
	 * @param chkXLNets - check de seguridad XLNets
	 * @param chkIdXlNets - IsSeguridad XLNets
	 */
	public static void SecurityContextExporter (JDBCMetaDataConfiguration jmdc,
			String pathTemplates, String pathProject, boolean chkXLNets, String chkIdXlNets) {

		String[] templates = new String[] { pathTemplates };
		File sourceDir = new File(pathProject + "\\");
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
	 * 
	 * @param schemaFilter - seleccion del esquema realizada por el usuario
	 */
	public static String getRevengXML(List<TreeNode> schemaFilter){
		final StringBuffer xml = new StringBuffer();
		List<TreeNode> treeNodesColumns;
		List<TreeNode> treeNodesComposite;
		
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append(System.getProperty("line.separator"));
		xml.append("<!DOCTYPE hibernate-reverse-engineering PUBLIC \"-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-reverse-engineering-3.0.dtd\" >");
		xml.append(System.getProperty("line.separator"));
		xml.append("<hibernate-reverse-engineering>");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t<type-mapping>");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t<sql-type jdbc-type=\"CHAR\" hibernate-type=\"String\"/>");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t<sql-type jdbc-type=\"TIMESTAMP\" hibernate-type=\"java.sql.Timestamp\"/>");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t<sql-type jdbc-type=\"OTHER\" hibernate-type=\"java.sql.Timestamp\" />");
		xml.append(System.getProperty("line.separator"));
		xml.append("\t</type-mapping>");
		xml.append(System.getProperty("line.separator"));

		for (TreeNode treeNodeTable : schemaFilter) {
		//	xml.append("\t<table-filter match-name=\"" + treeNodeTable.toString() + "\" />");
			xml.append("\t<table-filter match-name=\"" + treeNodeTable.getNameBBDD() + "\" />");
			xml.append(System.getProperty("line.separator"));
		}	
		
		for (TreeNode treeNodeTable : schemaFilter) {
			
			if (!treeNodeTable.isCheckedChildren()){

				//xml.append("\t<table name=\"" + treeNodeTable.getName() + "\">");
				xml.append("\t<table name=\"" + treeNodeTable.getNameBBDD() + "\">");
				xml.append(System.getProperty("line.separator"));
				
				treeNodesColumns = treeNodeTable.getChildren();
				
				for (TreeNode treeNodeColumn : treeNodesColumns) {
					
					if (!treeNodeColumn.isComposite()){
						if (!treeNodeColumn.isChecked()){
							xml.append("\t\t<column name=\"" + treeNodeColumn.getNameBBDD() + "\" exclude=\"true\"/>");
							xml.append(System.getProperty("line.separator"));
						}
					}else{
						treeNodesComposite = treeNodeColumn.getChildren();
						for (TreeNode treeNodeComposite : treeNodesComposite) {
							if (!treeNodeComposite.isChecked()){
								xml.append("\t\t<column name=\"" + treeNodeComposite.getNameBBDD() + "\" exclude=\"true\"/>");
								xml.append(System.getProperty("line.separator"));
							}
						}
					}
				}
				xml.append("\t</table>");
				xml.append(System.getProperty("line.separator"));
			}
		}
		
		xml.append("</hibernate-reverse-engineering>");
		System.out.println(xml.toString());
		return xml.toString();
	}

}