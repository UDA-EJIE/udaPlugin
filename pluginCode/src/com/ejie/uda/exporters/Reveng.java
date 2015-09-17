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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

/**
 * Clase contenedora de la definición de la estrategia a utilizar en la ingeniería inversa del modelado de base de datos.
 */

public class Reveng extends DelegatingReverseEngineeringStrategy  {
	
	private final static Logger logger = Logger.getLogger(Reveng.class);
	
    private ReverseEngineeringStrategy delegate;
    private ReverseEngineeringRuntimeInfo runtimeInfo;
    public static HashMap<String,String> synonymous;
   
    //Constructor
    public Reveng(ReverseEngineeringStrategy delegate) {
    	super(delegate);
        this.delegate = delegate;
        synonymous = new HashMap<String,String>();
    }

    @Override
    public void configure(ReverseEngineeringRuntimeInfo runtimeInfo) {
  	  this.runtimeInfo = runtimeInfo;
  	
  	  //Obtener sinónimos
  	  if (Reveng.synonymous.isEmpty()){
  		  getSynonymous();
  	  }

  	  this.delegate.configure(runtimeInfo);
    }

    /**
     * Recupera los sinónimos privados con su correspondiente tabla del esquema
     */
	protected void getSynonymous(){
		Reveng.synonymous = new HashMap<String,String>();
		Connection con = getConnectionReveng();
		try{

		//Obtener sinonimos del usuario
			String user = con.getMetaData().getUserName(); //usuario conectado (owner)
			
			String query = "SELECT TABLE_NAME, SYNONYM_NAME FROM SYS.ALL_SYNONYMS WHERE OWNER=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, user);
			
//			logger.info("sinonimos del usuario: " + query.replaceAll("\\?", user));
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) { 
//				logger.info("table:" + rs.getString(1) + ", synonymous:" + rs.getString(2));
				Reveng.synonymous.put(rs.getString(1), rs.getString(2));
			}	
			
		//Obtener sinonimos publicos
			
//			//query = "SELECT TABLE_NAME, SYNONYM_NAME FROM DBA_SYNONYMS WHERE OWNER='PUBLIC' AND UPPER(TABLE_NAME) NOT LIKE '%$%' AND UPPER(TABLE_NAME)<>UPPER(SYNONYM_NAME) AND TABLE_NAME  IN (SELECT TABLE_NAME FROM DBA_TABLES WHERE OWNER NOT IN ('SYS','SYSTEM','OUTLN','TSMSYS','DBSNMP','CTXSYS','XDB','MDSYS','HR') AND OWNER NOT LIKE ('FLOWS%')) ORDER BY TABLE_NAME DESC";
//			query = "SELECT TABLE_NAME, SYNONYM_NAME FROM SYS.ALL_SYNONYMS WHERE OWNER='PUBLIC' AND UPPER(TABLE_NAME) NOT LIKE '%$%' AND UPPER(TABLE_NAME)<>UPPER(SYNONYM_NAME) AND TABLE_NAME  IN (SELECT TABLE_NAME FROM SYS.ALL_TABLES WHERE OWNER NOT IN ('SYS','SYSTEM','OUTLN','TSMSYS','DBSNMP','CTXSYS','XDB','MDSYS','HR') AND OWNER NOT LIKE ('FLOWS%')) ORDER BY TABLE_NAME DESC";
//			ps = con.prepareStatement(query);
//			
//			logger.info("sinonimos publicos: " + query);
//			rs = ps.executeQuery();
//
//			while (rs.next()) { 
//				if (synonymous.get(rs.getString(1))==null){ //Si no está se añade
//					logger.info("table:" + rs.getString(1) + ", synonymous:" + rs.getString(2));
//					Reveng.synonymous.put(rs.getString(1), rs.getString(2));
//				}
//			}	
			
		}catch(Exception e){
			logger.error("", e);  
		}finally{
			closeConnectionReveng(con);
		}
	}
    
    /**
     * Filtra las tablas (antiguamente se eliminaban las vistas)
     */
    @Override
    public boolean excludeTable(TableIdentifier ti) {
//    	// Recupera todas las tablas y las carga en una lista (una sola vez)
//    	if (Reveng.tables.isEmpty()){
//    		//Antiguo getTables()
//    		logger.info("getTables() - INIT");
//	    	Reveng.tables = new ArrayList<String>();
//	    	Connection con = getConnectionReveng();
//		  	try {
//		  		DatabaseMetaData dbmt = con.getMetaData();
//		  		//Recupera la información de las tablas
//		  		ResultSet rs = dbmt.getTables(ti.getCatalog(), ti.getSchema(), null, new String[]{"TABLE"});
//				while (rs.next()) {
//					//Guarda el nombre en una lista
//					Reveng.tables.add(rs.getString("TABLE_NAME"));
//	    	  		logger.info("table:" + rs.getString("TABLE_NAME"));
//				}	
//			} catch (Exception e) {
//				logger.error("Error: " + e.getMessage());
//				e.printStackTrace();
//			}finally {
//				closeConnectionReveng(con);
//				logger.info("getTables() - END");
//			}
//    	}
//    	
//    	if (!Reveng.tables.contains(ti.getName())){
//    		return true; //Es una VISTA, se excluye
//    	}else{
//    		return super.excludeTable(ti);
//    	}
    	return super.excludeTable(ti);
    }
    
    /**
     * Recupera el nombre del sinónimo para nombrar a la clase de una tabla, en el caso que no tenga sinónimo se queda con el nombre de la tabla
     */
    @Override
    public String tableToClassName(TableIdentifier tableIdentifier) {
    	//Recupera el sinónimo de la tabla
		String synonymousTable = (String)synonymous.get(tableIdentifier.getName());
		
//		logger.info("table:" + tableIdentifier.getName() + ", synonymous:" + synonymousTable);
		
		if (synonymousTable!=null){//Crea la clase con el nombre del sinónimo
			return this.delegate.tableToClassName(new TableIdentifier(tableIdentifier.getCatalog(), tableIdentifier.getSchema(), synonymousTable));
		}else{
			return this.delegate.tableToClassName(tableIdentifier);	
		}
    }
	
    /**
  	 * Recupera la conexión de la base de datos
  	 */
    protected Connection getConnectionReveng() {
		try {
			return runtimeInfo.getConnectionProvider().getConnection();
		} catch (SQLException e) { 
			logger.error("", e);  
			return null;
		}
	}

  	/**
  	 * Cierre la conexión de la base de datos
  	 */
  	protected void closeConnectionReveng(Connection con) {
  		try {
  			if (con!=null && !con.isClosed()) {
				runtimeInfo.getConnectionProvider().closeConnection(con);
  			}
  		} catch (SQLException e) {
  			logger.error("", e);
  		}
  	}
  	
  	/**
  	 * Estrategia de Ingenería Inversa (Reveng)
  	 */
  	public static ReverseEngineeringSettings getRevengSettings(ReverseEngineeringStrategy reveng, boolean setPackageName, String appName){
		ReverseEngineeringSettings settings = new ReverseEngineeringSettings(reveng);
		if (setPackageName){
			settings.setDefaultPackageName("com.ejie." + appName);
		}
		settings.setDetectManyToMany(true);
		settings.setDetectOneToOne(true);
		settings.setDetectOptimisticLock(false);	
		return settings;
	}
}