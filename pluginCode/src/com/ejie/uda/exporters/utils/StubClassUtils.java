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
package com.ejie.uda.exporters.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * 
 * Clase principalmente utilizada en la generación de los stub, la cual contiene las funciones genéricas independientemente de la persistencia utilizada
 *
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class StubClassUtils {
	private static final Class[] parameters = new Class[]{URL.class};
	private static final String[] genericMethods = {"equals(java.lang.Object)", "toString()", "hashCode()","remove() throws java.rmi.RemoteException,javax.ejb.RemoveException","getEJBHome() throws java.rmi.RemoteException","getPrimaryKey() throws java.rmi.RemoteException","getHandle() throws java.rmi.RemoteException","isIdentical(javax.ejb.EJBObject) throws java.rmi.RemoteException"};

	public StubClassUtils(){
		
	}
	
	 public static  List<String[]> getClasses( String fileName
			 
	  ) throws IOException,
        SecurityException, ClassNotFoundException,
        IllegalArgumentException, InstantiationException,
        IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {

		 Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(
					fileName);
			
	Map<?, ?> identity = new HashMap<Object, Object>();
	Proxy someServiceImpl = null;
	List<String[]> list = new ArrayList<String[]>();
	boolean bol = true;
	try{

		someServiceImpl = (Proxy) UdaDynamicProxy.newInstance(identity, new Class[]
           { clazz },ClassLoader.getSystemClassLoader());
	}catch(Throwable e){
		e.printStackTrace();
		
	}
	    Method[] methods = someServiceImpl.getClass().getDeclaredMethods();
     for(Method met:methods){
     	for(String str:genericMethods){
     		if(met.toGenericString().endsWith(str)){
     			bol = false;
     			break;
     		}            		
     	}
     	if (bol){
         	String str = met.toGenericString();
         	String[] auxiliar = getMetodCaracteristics(met);
     		if (auxiliar!=null){
     			list.add(auxiliar);
     		}	
     	}
     	bol = true;
     	
     }
     return list;
 
}
@SuppressWarnings("unchecked")
public static String[] getMetodCaracteristics(Method method) {
      String methodName= method.getName().replace("$Proxy0.", "");
	   method.getReturnType().getName().equals("void");
	   String tipoRetorno = "";
	   tipoRetorno =  method.getReturnType().getName();
	   String tipoSimpleName = "";
	   if (method.getReturnType().isArray()){
		   tipoRetorno = tipoRetorno.substring(2,tipoRetorno.length()-1);
		   tipoSimpleName = method.getReturnType().getName().substring(method.getReturnType().getName().lastIndexOf(".")+1,method.getReturnType().getName().length()-1)+"[]";
	   }else if (!method.getReturnType().isPrimitive() &&   !method.getReturnType().getName().equals("void")){
		    tipoSimpleName = method.getReturnType().getName().substring(method.getReturnType().getName().lastIndexOf(".")+1,method.getReturnType().getName().length());
	   }else{
		   tipoSimpleName= method.getReturnType().getName();
	   }
	  
	   //Parametros
  	   String parametros = "";
  	   for (Class<Type> tipo : (Class<Type>[]) method.getGenericParameterTypes()) {
  		   if (!tipo.toString().endsWith("x38.remote.TransactionMetadata")){
  			   parametros = parametros + tipo.getCanonicalName() +";";
  		   }
  	   }
	   
  	 //Excepciones
  	   String excepciones = "";
  	   String fullExcepciones = "";
  	   for (Class<Type> excepcion : (Class<Type>[]) method.getExceptionTypes()) {
  		   excepciones = excepciones + excepcion.getCanonicalName().substring(excepcion.getCanonicalName().lastIndexOf(".")+1,excepcion.getCanonicalName().length()) + ", ";
  		   fullExcepciones =  fullExcepciones + excepcion.getCanonicalName() + ";";
  	   }
  	   if (!excepciones.equals("")){
  		   excepciones = excepciones.substring(0, excepciones.length()-2);
  	   }
  	   
	   String[] lista={methodName,tipoRetorno,tipoSimpleName,parametros,excepciones,fullExcepciones};
	   return lista;

}
public static void addFile(String s) throws IOException {
    File f = new File(s);
    addFile(f);
}

public static void addFile(File f) throws IOException {
    addURL(f.toURI().toURL());
}

public static void addURL(URL u) throws IOException {
    URLClassLoader sysloader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
    Class<?> sysclass = URLClassLoader.class;
    try {
          Method method = sysclass.getDeclaredMethod("addURL", parameters);
          method.setAccessible(true);
          method.invoke(sysloader, new Object[] { u });
    } catch (Throwable t) {
          t.printStackTrace();
          throw new IOException(
                     "Error, could not add URL to system classloader");
    }
}
public static String replaceDto(String field ){
	String resultado="";
	if (field.endsWith("Dto")){
		String res= field.replace(".dto.", ".");
		resultado = res.substring(0,res.lastIndexOf("Dto"));
	}else{
		resultado = field;
	}
	return resultado;
}
public static String generateParameterConstructor(String metodo){
	String resultado="";
	try {

				if (metodo.contains(".") && !metodo.toUpperCase().startsWith("JAVA.LANG.") && !metodo.equals("void") && metodo.toUpperCase().startsWith("COM.EJIE")){
				 resultado = "new " +  replaceDto(metodo).substring(replaceDto(metodo).lastIndexOf(".")+1,replaceDto(metodo).length())  ; 
		 	}
		
				return resultado;

	}catch(Exception e){
		e.getStackTrace();
		return "";
		
	}	
}
}
