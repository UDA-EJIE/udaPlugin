package com.ejie.uda.exporters.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Clase principalmente utilizada en la generación de los skeletons, la cual contiene las funciones genéricas independientemente de la persistencia utilizada
 */
@SuppressWarnings({"rawtypes", "finally"})
public class SkeletonUtils {
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
					String parametroCambio=parameterRemp;
					String objectType = parametroCambio.substring(parametroCambio.lastIndexOf(".")+1,parametroCambio.length());
					if (isJpa && parameterRemp.trim().startsWith("com.ejie") && !parameterRemp.trim().startsWith("com.ejie.x38")){
						parametroCambio = parameterRemp.substring(0,parameterRemp.lastIndexOf("."))+".dto."+ parameterRemp.substring(parametroCambio.lastIndexOf(".")+1,parametroCambio.length())+"Dto";
						objectType = parametroCambio.substring(parametroCambio.lastIndexOf(".")+1,parametroCambio.length());
					}
					
					if (!parametroCambio.startsWith("java.Lang")){
						if (imports){
							resultado.add(parametroCambio);
						}	else{
							String parametros=objectType;
							resultado.add(parametros);
						}
					}else{
						String parametros=objectType;
						resultado.add(parametros);
					}
					cadena = auxiliar.substring(auxiliar.indexOf(";")+1,auxiliar.length());
					auxiliar = cadena;
				}
			return resultado;
		}else{
			return resultado;
		}
		}catch(Exception e){
			return resultado;
		}
		
	}

	public static List<String> generateParameterImports(List<String[]> listaMetodos,boolean isJpa){
		List<String> resultado= new ArrayList<String>();
		try {
		
			Iterator itmet= listaMetodos.iterator();
			while (itmet.hasNext()){
				String cadenaMet= itmet.next().toString();
				String[] cadenaMetAux = cadenaMet.split(","); 
				String clase=cadenaMetAux[3].toString();
				List<String> listaAux = getParametersSkeleton(clase.substring(0,clase.length()-1),true,isJpa);
				Iterator<String> imports= listaAux.iterator();
				//parametros dle metodo
				while (imports.hasNext()){
					String subst=imports.next();
					if (!isJpa && subst.contains(".dto.")) {
						String auxiliar = StubClassUtils.replaceDto(subst);
						subst=auxiliar;
					}
					if (!alreadyExists(resultado,subst)){
						resultado.add(subst);
					}
				}
				//parametros del retorno
				String retorno=cadenaMetAux[1].toString();
				String retornoFinal=retorno;
				if (isJpa && retorno.startsWith("com.ejie") && !retorno.startsWith("com.ejie.x38")){
					retornoFinal=retorno+"Dto";
				}
				if (!isJpa && retornoFinal.contains(".dto.")) {
					String auxiliar = StubClassUtils.replaceDto(retornoFinal);
					retornoFinal=auxiliar;
				}
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
