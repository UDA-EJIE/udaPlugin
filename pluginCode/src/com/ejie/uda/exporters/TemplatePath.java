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

public class TemplatePath {
	
	/**
	 * _FTL -> Plantilla de FreeMaker en el proyecto "templates"
	 * _PATTERN -> Fichero que se generará (destino)
	 */
	
	//PERSISTENCE [JPA] 
	public static final String PERSISTENCE_FTL = "earclasses/src/META-INF/udaPersistence.xml.ftl";
	public static final String PERSISTENCE_PATTERN = "META-INF/udaPersistence.xml";
	
	//MODEL
	public static final String MODEL_JDBC_FTL = "generateCode/model/springJDBC/model.ftl";
	public static final String MODEL_JPA_FTL = "generateCode/model/JPA/model.ftl";
	public static final String MODEL_JPA_DTO_FTL = "generateCode/model/JPA/dto/modelDto.ftl";

	public static final String MODEL_PATTERN = "{package-name}/model/{class-name}.java";
	public static final String MODEL_DTO_PATTERN = "{package-name}/model/dto/{class-name}Dto.java";
	
	
	//DAO CONTEXT
	public static final String DAO_CONTEXT_FTL = "generateCode/dao/daoDIConfig.ftl";
	public static final String DAO_CONTEXT_PATTERN ="spring/daoDI-config.xml";
	
	
	//DAO
	public static final String DAO_JDBC_FTL = "generateCode/dao/springJDBC/dao.ftl";
	public static final String DAO_JDBC_Impl_FTL = "generateCode/dao/springJDBC/daoImpl.ftl";
	public static final String DAO_JPA_FTL = "generateCode/dao/JPA/dao.ftl";
	public static final String DAO_JPA_Impl_FTL = "generateCode/dao/JPA/daoImpl.ftl";
	
	public static final String DAO_PATTERN = "{package-name}/dao/{class-name}Dao.java";
	public static final String DAO_Impl_PATTERN = "{package-name}/dao/{class-name}DaoImpl.java";

	
	//SERVICE CONTEXT
	public static final String SERVICE_CONTEXT_FTL = "generateCode/service/serviceDIConfig.ftl";
	public static final String SERVICE_CONTEXT_PATTERN ="spring/serviceDI-config.xml";
	
	//SERVICE
	public static final String SERVICE_JDBC_FTL = "generateCode/service/springJDBC/service.ftl";
	public static final String SERVICE_JDBC_Impl_FTL = "generateCode/service/springJDBC/serviceImpl.ftl";
	public static final String SERVICE_JPA_FTL = "generateCode/service/JPA/service.ftl";
	public static final String SERVICE_JPA_Impl_FTL = "generateCode/service/JPA/serviceImpl.ftl";

	public static final String SERVICE_PATTERN = "{package-name}/service/{class-name}Service.java";
	public static final String SERVICE_Impl_PATTERN = "{package-name}/service/{class-name}ServiceImpl.java";
	
	
	//CONTROLLER CONTEXT
	public static final String JACKSON_FTL = "war/WebContent/WEB-INF/spring/jackson-config.xml.ftl";
	public static final String JACKSON_PATTERN ="WebContent/WEB-INF/spring/jackson-config.xml";
	public static final String DI_FTL = "generateCode/controller/controllerDIConfig.ftl";
	public static final String DI_PATTERN ="WebContent/WEB-INF/spring/controllerDI-config.xml";
	
	//CONTROLLER
	public static final String CONTROLLER_FTL = "generateCode/controller/controllerImpl.ftl";
	public static final String CONTROLLER_PATTERN =	"{package-name}/control/{class-name}Controller.java";

	
	//SECURITY CONTEXT
	public static final String SECURITY_CONTEXT_FTL = "war/WebContent/WEB-INF/spring/security-config.xml.ftl";
	public static final String SECURITY_CONTEXT_PATTERN = "WebContent/WEB-INF/spring/security-config.xml";
}