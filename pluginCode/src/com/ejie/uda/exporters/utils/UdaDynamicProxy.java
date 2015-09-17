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
