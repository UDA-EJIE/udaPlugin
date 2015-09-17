package com.ejie.uda.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.JDBCToHibernateTypeHelper;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import com.ejie.uda.exporters.Reveng;
import com.ejie.uda.exporters.utils.ControllerUtils;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.TreeNode;
import com.ejie.uda.utils.TreeRelation;
import com.ejie.uda.utils.Utilities;



/**
 * 
 *
 */
public class DataBaseWorker {
	
	private static Cfg2JavaTool c2j = new Cfg2JavaTool();
	private static Cfg2HbmTool c2h = new Cfg2HbmTool();
	
	private static String COMPOSITE_KEY ="Composite primary key";
	
	private static ReverseEngineeringStrategy reveng;
	
	/**
	 * Constructor
	 */
	private DataBaseWorker() {
		//No es instanciable
	}

	public static JDBCMetaDataConfiguration getConfiguration(ConnectionData conData){
		
		JDBCMetaDataConfiguration jmdc = new JDBCMetaDataConfiguration();
		jmdc.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect")
			//	"org.hibernate.dialect.Oracle8iDialect")
				.setProperty("hibernate.connection.driver_class","oracle.jdbc.driver.OracleDriver")
				.setProperty("hibernate.connection.url", conData.getUrl())
				.setProperty("hibernate.connection.username", conData.getUserName())
				.setProperty("hibernate.connection.password", conData.getPassword())
				.setProperty("hibernate.default_catalog", conData.getCatalog())
				.setProperty("hibernate.default_schema", conData.getSchema());
		
		return jmdc;
	}
	
	public static ReverseEngineeringStrategy getReveng(){
		if (DataBaseWorker.reveng==null){
			DefaultReverseEngineeringStrategy strategy = new DefaultReverseEngineeringStrategy();
			DataBaseWorker.reveng = new Reveng(strategy);
			reveng.setSettings(Reveng.getRevengSettings(reveng, false, null));
		}
		return DataBaseWorker.reveng;
	}

	
	@SuppressWarnings("unchecked")
	public static TreeNode getSchemaTree(ConnectionData conData){

		TreeNode schema = null;
		String referenceClass = "";
		
		if (conData != null){
			schema = new TreeNode(conData.getSchema());
			
			JDBCMetaDataConfiguration jmdc = DataBaseWorker.getConfiguration(conData);
			jmdc.setPreferBasicCompositeIds(false);
			jmdc.setProperty("reversestrategy", "Reveng");
			DataBaseWorker.reveng=null; //Reiniciar el REVENG por si se vuelve a lanzar el plugin
			jmdc.setReverseEngineeringStrategy(getReveng());
			jmdc.readFromJDBC();
			
			Iterator<PersistentClass> ic = jmdc.getClassMappings();
			
			while (ic.hasNext()) {
				PersistentClass clase = (PersistentClass) ic.next();
				
				TreeNode table = new TreeNode(clase.getEntityName(), "table", clase.getTable().getName());
				
				TreeNode primaryKey = getPrimaryKey(clase, jmdc);
				if (primaryKey != null){
					table.addChild(primaryKey);
				}
		
				Iterator<Property> properties = clase.getPropertyIterator();
				while (properties.hasNext()) {
					Property prop = properties.next();

					if (!c2h.isCollection(prop) && !c2j.isComponent(prop) && (!c2h.isOneToOne(prop)) ) {
						
						
						if (c2h.isManyToOne(prop)) {
							PersistentClass subclase = jmdc.getClassMapping(prop.getType().getName());
							if (subclase != null) {
								referenceClass = ControllerUtils.findNameFromEntity(subclase.getEntityName());
							}
						}
						
						
						Iterator<Column> subprop = prop.getColumnIterator();
						while (subprop.hasNext()) {
							Column columna = subprop.next();
							
							TreeNode column = new TreeNode(ControllerUtils.findHibernateName(columna.getName().toLowerCase()) + getColumnLabel(columna, false), "column", columna.getName() + getColumnLabel(columna, false));
							column.setPrimaryKey(false);
							column.setReferenceClass(Utilities.descapitalize(referenceClass));
							
							table.addChild(column);
						}
						referenceClass = "";
					} else if (!c2h.isCollection(prop) && (!c2h.isOneToOne(prop))) {
						
						if (c2h.isManyToOne(prop)) {
							PersistentClass subclase = jmdc.getClassMapping(prop.getType().getName());
							if (subclase != null) {
								referenceClass = ControllerUtils.findNameFromEntity(subclase.getEntityName());
							}
						}
						
						Iterator<?> propiedades = c2h.getProperties((Component) prop.getValue());
						while (propiedades.hasNext()) {
							Property propAux = (Property) propiedades.next();
							Iterator<Column> subprop = propAux.getColumnIterator();
							while (subprop.hasNext()) {
								Column columna = subprop.next();
								
								
								TreeNode column = new TreeNode(ControllerUtils.findHibernateName(columna.getName().toLowerCase()) + getColumnLabel(columna, false), "column", columna.getName() + getColumnLabel(columna, false));
								
								column.setPrimaryKey(false);
								
								column.setReferenceClass(Utilities.descapitalize(referenceClass));
								
								table.addChild(column);
							}
						}
						referenceClass = "";
					}
				}
				schema.addChild(table);
			}
		}
		
		//Ordenar las tablas alfabeticamente
		Collections.sort(schema.getChildren());
		
		return schema;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	//Necesario para los métodos de gestión de tablas de relaciones M:N
	public static TreeRelation getSchemaTreeWithMN(ConnectionData conData){

		TreeRelation schema = null;
		
		if (conData != null){
			schema = new TreeRelation(conData.getSchema());
			
			JDBCMetaDataConfiguration jmdc = DataBaseWorker.getConfiguration(conData);
			jmdc.setReverseEngineeringStrategy(getReveng());
			jmdc.readFromJDBC();
			
			Iterator<PersistentClass> ic = jmdc.getClassMappings();
			
			while (ic.hasNext()) {
				PersistentClass clase = (PersistentClass) ic.next();
				
				TreeRelation tableAsoc = new TreeRelation(clase.getTable().getName(),"table");

				TreeRelation nuevaTupla = null;
				Iterator<Property> properties = clase.getPropertyIterator();
				while (properties.hasNext()) {
					Property prop = properties.next();
					//las M a N tb las metemos
					 if(c2h.isCollection(prop) && c2h.isManyToMany(prop)) {
						Collection collection = (Collection)prop.getValue();
						String tablaMN = collection.getCollectionTable().getName();
						TreeRelation schemaAux = schema;
						List<TreeRelation> listaSch =schemaAux.getChildren();
						boolean encontrado=false;
						for (TreeRelation relaciones: listaSch){
							if (relaciones.getName().equals(tablaMN)){
								nuevaTupla = relaciones;
								TreeRelation tabla = new TreeRelation( tablaMN, "table");
								//nuevaTupla.addChild(child);
								encontrado=true;
								break;
							}
							
						}
						if (!encontrado){
							 nuevaTupla = new TreeRelation( collection.getCollectionTable().getName(), "table");
							
						}
						
						nuevaTupla.addChild(tableAsoc);
						schema.updateChild(nuevaTupla);
					}
				}
				
			}
		}
		return schema;
	}
	
	@SuppressWarnings("unchecked")
	private static TreeNode getPrimaryKey(PersistentClass classTable,
			JDBCMetaDataConfiguration cfg) {

		TreeNode treeNodePrimaryKey = null;
		String referenceClass = "";

		if (!c2j.isComponent(classTable.getIdentifier())) {

			// Clave simple
			POJOClass pojo = c2j.getPOJOClass(classTable);
			PersistentClass clazzPojo = (PersistentClass) pojo.getDecoratedObject();
			if (c2h.isManyToOne(clazzPojo.getIdentifierProperty())) {
				PersistentClass subclase = cfg.getClassMapping(clazzPojo.getIdentifier().getType().getName());
				if (subclase != null) {
					referenceClass = ControllerUtils.findNameFromEntity(subclase.getEntityName());
				}
			}

			Iterator<Column> subprop = classTable.getIdentifierProperty().getColumnIterator();

			while (subprop.hasNext()) {
				Column columna = subprop.next();
				treeNodePrimaryKey = new TreeNode(
						ControllerUtils.findHibernateName(columna.getName().toLowerCase()) + getColumnLabel(columna, true),"column", columna.getName() + getColumnLabel(columna, true));
				treeNodePrimaryKey.setPrimaryKey(true);
				treeNodePrimaryKey.setReferenceClass(Utilities.descapitalize(referenceClass));
			}
			
			referenceClass = "";
		} else {

			treeNodePrimaryKey = new TreeNode(COMPOSITE_KEY, "column");
			treeNodePrimaryKey.setPrimaryKey(true);
			treeNodePrimaryKey.setComposite(true);
			POJOClass pojo = c2j.getPOJOClass(classTable);
			PersistentClass clazzPojo = (PersistentClass) pojo.getDecoratedObject();
			Iterator<Property> propertiesPrim = c2h.getProperties((Component) clazzPojo.getIdentifier());
			while (propertiesPrim.hasNext()) {

				Property propiedad = propertiesPrim.next();
				if (c2h.isManyToOne(propiedad)) {
					PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
					if (subclase != null) {
						referenceClass = ControllerUtils.findNameFromEntity(subclase.getEntityName());
					}
				}

				Iterator<Column> subprop = propiedad.getColumnIterator();

				while (subprop.hasNext()) {
					Column columna = subprop.next();
					TreeNode treeNodeCompositeKey = new TreeNode(
							ControllerUtils.findHibernateName(columna.getName().toLowerCase()) + getColumnLabel(columna, true), "column", columna.getName() + getColumnLabel(columna, true));
					treeNodeCompositeKey.setPrimaryKey(true);
					treeNodeCompositeKey.setReferenceClass(Utilities.descapitalize(referenceClass));
					treeNodePrimaryKey.addChild(treeNodeCompositeKey);
				}
				referenceClass = "";
			}
		}

		return treeNodePrimaryKey;
	}
	
	private static String getColumnLabel(Column c, boolean isPrimaryKey) {
		String desc = "";
		if (c.getSqlTypeCode() != null) {
			desc = " : "
					+ JDBCToHibernateTypeHelper.getJDBCTypeName(c
							.getSqlTypeCode().intValue());

			if (!c.isNullable()) {
				desc = desc + " (Not Nullable)";
			}
			if (isPrimaryKey) {
				desc = desc + " - PK";
			}

		}
		return desc;
	}
	
	public static boolean testConnection(ConnectionData conData) {
		
		JDBCMetaDataConfiguration jmdc = null;
		SessionFactory sessionFactory = null;
		Session session = null;
		boolean isValid = false;
		
		try {
			if (conData != null){
				jmdc = getConfiguration(conData);

				sessionFactory = jmdc.buildSessionFactory();
				session = sessionFactory.openSession();
				SQLQuery query = session.createSQLQuery("SELECT 1 FROM DUAL");
				if (query.list()!=null && !query.list().isEmpty()){
					isValid = true;
				}
			}
			return isValid;
		} catch (Exception ex) {
			//TODO: poner LOG
			return isValid;
		} finally {
			if (session != null){
				session.disconnect();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getTablesSchema(ConnectionData conData){
		List<String> tablesSchema = new ArrayList<String>(0);
		if (conData != null){
			JDBCMetaDataConfiguration jmdc = DataBaseWorker.getConfiguration(conData);
			DataBaseWorker.reveng=null; //Reiniciar el REVENG por si se vuelve a lanzar el plugin
			jmdc.setReverseEngineeringStrategy(getReveng());
			jmdc.readFromJDBC();
			Iterator<PersistentClass> ic = jmdc.getClassMappings();
			while (ic.hasNext()) {
				PersistentClass clase = (PersistentClass) ic.next();
				tablesSchema.add(clase.getEntityName());
			}
		}
		Collections.sort(tablesSchema);
		return tablesSchema;
	}
	
	public static TreeNode getTableNode(ConnectionData conData, String tableName){
		
		TreeNode columns = null;
		
		TreeNode schema = getSchemaTree(conData);
		
		List<TreeNode> tables = schema.getChildren();
		
		if (tables != null && !tables.isEmpty()){
			for (TreeNode tableNode : tables) {
				if (tableName.toUpperCase().equals(tableNode.getName().toUpperCase())){
					columns = tableNode;
					break;
				}
			}
		}
		
		return columns;
	}
	
}