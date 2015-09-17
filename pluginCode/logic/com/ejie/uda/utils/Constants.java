package com.ejie.uda.utils;

/**
 * Clase de Constantes del plugin 
 *
 */
public class Constants {
	//Encoding
	public static final String ENCODING_UTF8 = "UTF-8";
	public static final String ENCODING_ISO = "ISO-8859-1";
	
	//Plug-in ID
	public static final String PLUGIN_ID = "com.ejie.uda";
	
	//Fichero de estado del plugin
	public static final String PLUGIN_FILE = "com.ejie.uda.xml";
	
	//Layouts
	public static final String LAYOUT_PATTERN = "layout";
	public static final String LAYOUT_VERTICAL = "vertical";
	public static final String LAYOUT_HORIZONTAL = "horizontal";
	public static final String LAYOUT_MIXTO = "mixto";
	public static final String APP_TYPE_INTRANET = "intranet";
	public static final String APP_TYPE_INTERNET = "internet";
	public static final String CATEGORY_HORIZONTAL = "horizontal";
	public static final String CATEGORY_DEPARTAMENTAL = "departamental";

	//Idioma
	public static final String LANGUAGES_PATTERN = "languages";
	public static final String LANGUAGES_WITHOUT_QUOTES_PATTERN = "languageswithoutquotes";
	public static final String DEFAULT_LANGUAGE_PATTERN = "defaultlanguage";
	
	//Tipos de aplicación
	public static final String APP_TYPE_INTRANET_PATTERN = "appintranet";
	public static final String APP_TYPE_INTERNET_PATTERN = "appinternet";

	//Patrones	
	public static final String CODAPP_PATTERN = "codapp";
	public static final String RADJPA_PATTERN = "radjpa";
	public static final String RADSPRINGJDBC_PATTERN = "radspringjdbc";
	public static final String CONFIG_NAME_PATTERN = "configName";
	public static final String STATICS_PATTERN = "staticsName";	
	public static final String EARCLASSES_NAME_PATTERN = "earClassesName";
	public static final String WAR_NAME_PATTERN = "warName";
	public static final String WAR_NAME_SHORT_PATTERN = "warNameShort";
	public static final String EAR_NAME_PATTERN = "earName";
	public static final String EJB_NAME_PATTERN = "ejbName";
	public static final String PREF_EJIE_PATTERN = "entornoEjie";
	public static final String[] RENAME_PATTERNS = {Constants.CODAPP_PATTERN};
	public static final String STATICS_NAME = "Statics";
	public static final String CONFIG_NAME = "Config";
	public static final String DATOS_NAME = "Datos";
	public static final String EARCLASSES_NAME = "EARClasses";
	public static final String WAR_NAME = "War";
	public static final String EAR_NAME = "EAR";
	public static final String EJB_NAME = "EJB";
	public static final String CODROLE_PATTERN = "codrole";
	public static final String PACKAGE_PATTERN = "packageName";
	public static final String ENTITY_PATTERN = "entity";
	public static final String MAINT = "maint";
	public static final String GRID = "grid";
	public static final String GRID_COLUMNS = "gridColumns";
	
	//Prefijos y sufijos para identificar elementos
	public static final String FREEMARKER_SUFFIX = ".ftl";
	
	//Preferencias
	public static final String PREF_TEMPLATES_UDA_LOCALPATH = "templatesUDA";
	public static final String PREF_EJIE = "true";
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_STATICS =  "\\statics";
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_CONFIG =  "\\config";
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EARCLASSES =  "\\earclasses";
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_WAR= "\\war";
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EAR= "\\ear";
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_EJB= "\\ejb";
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_MAINT= "\\maint";
	public static final String PERSISTENCE_LOCALPATH = "\\EarContent\\META-INF\\weblogic-application.xml";
	public static final String WEBLOGIC_LIB_APP = "oracle.eclipse.tools.weblogic.lib.application";
	public static final String WEBLOGIC_LIB_SYS = "oracle.eclipse.tools.weblogic.lib.system";
	public static final String DEPLOY_PATH = "org.eclipse.wst.common.component";
	public static final String MAVEN_HOME = "mavenHome";
	public static final String MAVEN_SETTINGS = "mavenSettings";
	public static final String MAVEN_REPOSITORY = "mavenRepository";
	public static final String PATH_CONFIG = "C:\\config\\dominio_desa\\";
	public static final String PATH_DATOS = "C:\\datos\\";
	public static final String PREF_DEFAULT_BUILD_PATH= "\\build\\classes";
	public static final String PREF_DEFAULT_EAR_LIBS= "\\EarContent\\APP-INF\\lib";
	
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_STATICS_RUP = "\\rup";
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_STATICS_APLIC = "\\aplic";
	public static final String PREF_DEFAULT_TEMPLATES_UDA_LOCALPATH_STATICS_WEB_INF = "\\WEB-INF";
	
	public static final String SERVER_JNI_NAMING = "java.naming.provider.url";
	public static final String SERVER_FACTORY = "java.naming.factory.initial";
	
	public static final String DEFAULT_SERVER_JNI_NAMING = "t3://127.0.0.1:7001";
	public static final String DEFAULT_SERVER_FACTORY = "weblogic.jndi.WLInitialContextFactory";
	
	//Preferencias jar
	public static final String PREF_DEFAULT_X38_LIBS= "x38ShLibClasses";
	public static final String PREF_DEFAULT_UDA_JAR= "uda";

	
	//Id security
	public static final String ID_SECURITY = "idSecurity";
	
	//Framework
	public static final String UDA = "UDA";
	public static final String WORKSETNAME = "workSetName";
	
	//Ant Tasks
	public static final String ANT_BUILD_FILE = "build.xml";
	
	//Console Log
	public static final String CONSOLE_NAME = "UDA - EJIE";
	public static final int MSG_INFORMATION = 1;
	public static final int MSG_ERROR = 2;
	public static final int MSG_WARNING = 3;
	
}