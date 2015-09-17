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
	public static final String DAO_CONTEXT_JDBC_FTL = "generateCode/dao/springJDBC/appConfigDao.ftl";
	public static final String DAO_CONTEXT_JPA_FTL = "generateCode/dao/JPA/appConfigDao.ftl";
	
	public static final String DAO_CONTEXT_PATTERN = "spring/dao-config.xml";
	
	//DAO
	public static final String DAO_JDBC_FTL = "generateCode/dao/springJDBC/dao.ftl";
	public static final String DAO_JDBC_Impl_FTL = "generateCode/dao/springJDBC/daoImpl.ftl";
	public static final String DAO_JPA_FTL = "generateCode/dao/JPA/dao.ftl";
	public static final String DAO_JPA_Impl_FTL = "generateCode/dao/JPA/daoImpl.ftl";
	
	public static final String DAO_PATTERN = "{package-name}/dao/{class-name}Dao.java";
	public static final String DAO_Impl_PATTERN = "{package-name}/dao/{class-name}DaoImpl.java";

	
	//SERVICE CONTEXT
	public static final String SERVICE_CONTEXT_FTL = "generateCode/service/appConfigService.ftl";
	public static final String SERVICE_CONTEXT_PATTERN = "spring/service-config.xml";
	
	//SERVICE
	public static final String SERVICE_JDBC_FTL = "generateCode/service/springJDBC/service.ftl";
	public static final String SERVICE_JDBC_Impl_FTL = "generateCode/service/springJDBC/serviceImpl.ftl";
	public static final String SERVICE_JPA_FTL = "generateCode/service/JPA/service.ftl";
	public static final String SERVICE_JPA_Impl_FTL = "generateCode/service/JPA/serviceImpl.ftl";

	public static final String SERVICE_PATTERN = "{package-name}/service/{class-name}Service.java";
	public static final String SERVICE_Impl_PATTERN = "{package-name}/service/{class-name}ServiceImpl.java";
	
	
	//CONTROLLER CONTEXT
	public static final String CONTROLLER_CONTEXT_FTL = "generateCode/controller/appConfigController.ftl";
	public static final String CONTROLLER_CONTEXT_PATTERN ="WebContent/WEB-INF/spring/mvc-config.xml";
	
	//CONTROLLER
	public static final String CONTROLLER_FTL = "generateCode/controller/controllerImpl.ftl";
	public static final String CONTROLLER_PATTERN =	"{package-name}/control/{class-name}Controller.java";

	
	//SECURITY CONTEXT
	public static final String SECURITY_CONTEXT_FTL = "war/WebContent/WEB-INF/spring/security-config.xml.ftl";
	public static final String SECURITY_CONTEXT_PATTERN = "WebContent/WEB-INF/spring/security-config.xml";
}