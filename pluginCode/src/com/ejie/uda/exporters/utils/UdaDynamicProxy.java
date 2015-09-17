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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
/**
 * Proxy dinímico necesario para la generación del Stub
 */
@SuppressWarnings({ "rawtypes" })
public class UdaDynamicProxy implements java.lang.reflect.InvocationHandler
{
	 private Map map; 
	 
	  public static Object newInstance(Map map, Class[] interfaces,ClassLoader classLoader)
	  {
	    return Proxy.newProxyInstance(classLoader,
	                                  interfaces,
	                                  new UdaDynamicProxy(map));
	  }
	  public UdaDynamicProxy(Map map)
	  {
		    this.map = map; }
	 
	  
	  public Object invoke(Object proxy, Method m, Object[] args)
		throws Throwable {
	try {
		String methodName = m.getName();
		if (methodName.startsWith("get")) {
			String name = methodName
					.substring(methodName.indexOf("get") + 3);
			return map.get(name);
		}
		return null;
	} catch (Exception e) {
		throw e;
	}
}
}
