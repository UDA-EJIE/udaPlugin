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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Clase principalmente utilizada en la generación de los skeletons, la cual contiene las funciones genéricas independientemente de la persistencia utilizada
 */
@SuppressWarnings({"rawtypes", "finally"})
public class SkeletonUtils {
	
	//Fix: utilidad que comprueba que no es tipo primitivo o clase de Java (java.*)
	private static boolean isAvoidable(String param){
		if (param.equalsIgnoreCase("boolean") ||
				param.equalsIgnoreCase("byte") ||
				param.equalsIgnoreCase("char") ||
				param.equalsIgnoreCase("short") ||
				param.equalsIgnoreCase("int") ||
				param.equalsIgnoreCase("long") ||
				param.equalsIgnoreCase("float") ||
				param.equalsIgnoreCase("double") ||
				param.toLowerCase().startsWith("java.lang.")){
			return true;
		} else {
			return false;
		}
	}
	
	public SkeletonUtils(){
		
	}
	public static List<String> getParametersSkeleton(String cadena, boolean imports,boolean isJpa){
		List<String> resultado= new ArrayList<String>();
		try{
			if (cadena!=null && !cadena.equals("")){
				String auxiliar = cadena;
				while (cadena.contains(";") && cadena!=null && !cadena.equals("") ){
					String parameter =  auxiliar.substring(0,auxiliar.indexOf(";"));
					String parameterRemp = parameter.replace("class ", "");
					
					//FIX
					if (imports){
						parameterRemp = parameter.replace("[]", "").trim();
						if (isAvoidable(parameterRemp)){
							cadena = auxiliar.substring(auxiliar.indexOf(";")+1,auxiliar.length());
							auxiliar = cadena;
							continue;
						}
					}
					
					String parametroCambio=parameterRemp;
					String objectType = parametroCambio.substring(parametroCambio.lastIndexOf(".")+1,parametroCambio.length());
					if (isJpa && parameterRemp.trim().startsWith("com.ejie") && !parameterRemp.trim().startsWith("com.ejie.x38")){
						parametroCambio = parameterRemp.substring(0,parameterRemp.lastIndexOf("."))+".dto."+ parameterRemp.substring(parametroCambio.lastIndexOf(".")+1,parametroCambio.length())+"Dto";
						objectType = parametroCambio.substring(parametroCambio.lastIndexOf(".")+1,parametroCambio.length());
					}
					
					if (imports){
						resultado.add(parametroCambio);
					}	else{
						resultado.add(objectType);
					}
					cadena = auxiliar.substring(auxiliar.indexOf(";")+1,auxiliar.length());
					auxiliar = cadena;
				}
			}
		}catch(Exception e){
		}
		return resultado;
		
	}

	public static List<String> generateParameterImports(List<String[]> listaMetodos,boolean isJpa){
		List<String> resultado= new ArrayList<String>();
		try {
		
			Iterator itmet= listaMetodos.iterator();
			while (itmet.hasNext()){
				String cadenaMet= itmet.next().toString();
				String[] cadenaMetAux = cadenaMet.split(",");
				//Param
				String clase=cadenaMetAux[3].toString();
				if (clase.endsWith("]")){
					clase = clase.substring(0, clase.length()-1);
				}
				//Excep
				clase += cadenaMetAux[5].toString().substring(0,cadenaMetAux[5].toString().length()-1).trim();
				List<String> listaAux = getParametersSkeleton(clase,true,isJpa);
				Iterator<String> imports= listaAux.iterator();
				//parametros dle metodo
				while (imports.hasNext()){
					String subst=imports.next();
//					if (!isJpa && subst.contains(".dto.")) {
//						String auxiliar = StubClassUtils.replaceDto(subst);
//						subst=auxiliar;
//					}
					if (!alreadyExists(resultado,subst)){
						resultado.add(subst);
					}
				}
				//parametros del retorno
				String retorno=cadenaMetAux[1].toString().trim();
				if (isAvoidable(retorno)){
					continue;
				}
				String retornoFinal=retorno;
				if (isJpa && retorno.startsWith("com.ejie") && !retorno.startsWith("com.ejie.x38")){
					retornoFinal=retorno+"Dto";
				}
//				if (!isJpa && retornoFinal.contains(".dto.")) {
//					String auxiliar = StubClassUtils.replaceDto(retornoFinal);
//					retornoFinal=auxiliar;
//				}
				if (retornoFinal.contains(".") && !retornoFinal.toUpperCase().startsWith("JAVA.LANG.") && !retornoFinal.equals("void")){
					if (!alreadyExists(resultado,retornoFinal)){
						resultado.add(retorno);
					}
				}
			}
		}catch(Exception e){
			e.getStackTrace();
		}finally{
			return resultado;
		}
		
	}
	public static boolean alreadyExists(List<String> listaImports, String newImport){
		boolean found=false;
		Iterator<String> importados= listaImports.iterator();
		while (importados.hasNext() && !found){
			String nombreImport = importados.next();
			if (nombreImport.equals(newImport)){
				found=true;
			}
		}
		return found;
		
	}
	public static String generateTransactionAttribute(String metodName){
		String resultado="";
		if (!metodName.toUpperCase().startsWith("FIND")){
			resultado = "@TransactionAttribute (TransactionAttributeType.REQUIRED)";
		}
		return resultado;
	}
}
