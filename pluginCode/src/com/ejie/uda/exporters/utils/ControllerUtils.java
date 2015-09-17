package com.ejie.uda.exporters.utils;

import com.ejie.uda.utils.Utilities;




/**
 * 
 * Clase principalmente utilizada en la generación de la capa de control, la cual contiene las funciones genéricas independientemente de la persistencia utilizada
 *
 */

public class ControllerUtils {
	
	public ControllerUtils() {
	}
	
	public static String findHibernateName(String field){
		String nombre=null;
		String auxiliar= field.toLowerCase();
		if (field.contains("_")){
			while (auxiliar.contains("_")){
				nombre=auxiliar.substring(0,auxiliar.indexOf("_")) + stringCapitalize(auxiliar.substring(auxiliar.indexOf("_")+1,auxiliar.length()));
				auxiliar = nombre;
			}
			
		}else{
			auxiliar = field;
		}
		
		return auxiliar;
	}
	
	public static String findDataBaseName(String fieldHibernate){
		String result = ControllerUtils.stringDecapitalize(fieldHibernate).replaceAll("[A-Z]", "_");
		if (result.indexOf("_")==-1){
			result = fieldHibernate.toUpperCase();
		} else {
			String[] aux  = result.split("_");
			String last = aux[0];
			for (int i = 1; i < aux.length; i++) {
				last += "_" + fieldHibernate.charAt(result.indexOf("_")) + aux[i];
				result = result.replaceFirst("_", "@");
			}
			result = last;
		}
		return result;
	}
	
	public static String findNameFromEntity(String entityName){
		String nombre=null;
		nombre= entityName.substring(entityName.lastIndexOf(".")+1);
		return nombre;
	}
	public static String stringDecapitalize(String decapitalize){
		String nombre=null;
		if ( decapitalize!=null && !decapitalize.equals("")){
			nombre= decapitalize.substring(0,1).toLowerCase() + decapitalize.substring(1,decapitalize.length());
		}	else{
			nombre=decapitalize;
		}
		return nombre;
	}
	
	public static String stringCapitalize(String capitalize){
		String nombre=null;
		if ( capitalize!=null && !capitalize.equals("")){
			nombre= capitalize.substring(0,1).toUpperCase() + capitalize.substring(1,capitalize.length());
		}	else{
			nombre=capitalize;
		}
		return nombre;
	}

	public static String getRelationName(String tableName){
		return Utilities.getRelationName(tableName);
	}
}
