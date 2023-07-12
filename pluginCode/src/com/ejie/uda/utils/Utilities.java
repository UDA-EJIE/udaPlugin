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
package com.ejie.uda.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

import com.ejie.uda.exporters.Reveng;

public class Utilities {
	
	private final static Logger logger = Logger.getLogger(Utilities.class);
	
	/**
	 * Capitaliza una cadena de caracteres
	 * @param s - cadena de caracteres
	 * @return
	 */
	public static String capitalize(String s) {

		final StringTokenizer st = new StringTokenizer(s.toLowerCase(), " ", true);
		final StringBuilder sb = new StringBuilder();

		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			token = String.format("%s%s", Character.toUpperCase(token.charAt(0)), token.substring(1));
			sb.append(token);
		}

		return sb.toString();

	}
	
	/**
	 * Descapitaliza una cadena de caracteres poniéndolo a mayúsculas
	 * @param s - cadena de caracteres
	 * @return
	 */
	public static String descapitalize(String s) {

		String descapitalize = "";
		
		if (!Utilities.isBlank(s) && s.length()>1){
			descapitalize = s.substring(0,1).toLowerCase() + s.substring(1);	
		}
		
		return descapitalize;
	}
	
	/**
	 * Pone sólo la primera letra a mayúscula, las demas deja como están
	 * @param s - cadena de caracteres
	 * @return
	 */
	public static String camelCase(String s) {

		final StringTokenizer st = new StringTokenizer(s, " ", true);
		final StringBuilder sb = new StringBuilder();

		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			token = String.format("%s%s", Character.toUpperCase(token.charAt(0)), token.substring(1));
			sb.append(token);
		}

		return sb.toString();

	}
	
	/**
	 * Verifica si una cadena de caracteres es nula o vacía
	 * @param str - cadena de caracteres
	 * @return
	 */
	public static boolean isBlank(String str) {

		 if (str == null || "".equals(str)){
			 return true;
		 }else{
			 return false;
		 }
	 
	}
	 
	/**
	 * Busca una cadena de caracteres en un fichero
	 * @param fileName - nombre del fichero
	 * @param phrase - texto a buscar
	 * @return true si hay coincidencia, false ecc.
	 * @throws IOException
	 */
	public static boolean searchString(String fileName, String phrase) throws IOException {
		
		Scanner fileScanner = new Scanner(new File(fileName));
		Pattern pattern = Pattern.compile(phrase);
		Matcher matcher = null;
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Valida en contenido del un campo texto del plugin.
	 * Sólo permite letras y números
	 * @param text - texto a validar
	 * @return
	 */
	public static boolean validateText(String text) {
		Pattern pat = Pattern.compile("[a-zA-Z0-9-]+");
		return pat.matcher(text).matches();
	}
	
	/**
	 * Valida el contenido del campo service name del plugin.
	 * ej: codapp.ejie.eus o codapp.batera.euskadi.eus
	 * @param text - texto a validar
	 * @return
	 */
	public static boolean validateServiceText(String text) {
		Pattern pat = Pattern.compile("^([a-zA-Z])(([a-zA-Z0-9]+)\\.){2,3}([a-zA-Z]{3})");
		return pat.matcher(text).matches();
	}
	
	/**
	 * Valida en contenido del campo texto del nombre de un WAR del plugin.
	 * Sólo permite letras y números
	 * @param text - texto a validar
	 * @return
	 */
	public static boolean validateWARText(String text) {
		Pattern pat = Pattern.compile("^([a-zA-Z])|^([a-zA-Z])[a-zA-Z0-9]+");
		return pat.matcher(text).matches();
	}
	/**
	 * Valida en contenido del campo texto del nombre de un JNDI del plugin.
	 * Sólo permite letras, números, carácter / y _
	 * @param text - texto a validar
	 * @return
	 */
	public static boolean validateJNDIText(String text) {
		    //Pattern pat = Pattern.compile("[^A-Za-z0-9.@_-~#]+");
		   Pattern pat = Pattern.compile("[a-zA-Z0-9/_#.]+");
			return pat.matcher(text).matches();
	}
	
	
	
	/**
	 * Valida una la dirección ip.
	 * @param text - ip a validar
	 * @return
	 */
	public static boolean validateIPAdderess(String text) {
		String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		Pattern pat = Pattern.compile( "^(?:" + _255 + "\\.){3}" + _255 + "$");
		return pat.matcher(text).matches();
	}
	
	/**
	 * Valida un nombre de host
	 * @param text - nombre del host
	 * @return
	 */
	public static boolean validateHostName(String text) {
		Pattern pat = Pattern.compile("^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9])" +
									  "(.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]))*$");
		return pat.matcher(text).matches();
	}
	
	/**
	 * Valida en contenido del sólo contiene números
	 * @param text - cadena a validar
	 * @return
	 */
	public static boolean validateNumber(String text) {
		Pattern pat = Pattern.compile("[0-9]+");
		return pat.matcher(text).matches();
	}
	
	/**
	 * Convierte un texto a entero
	 * @param number - número en formato texto
	 * @return número entero
	 */
	public static int stringToInt(String number) {
		int value = 0;
		
		try {

			if (!isBlank(number)){
				value = Integer.parseInt(number.trim());
			}
		} catch (NumberFormatException e) {
			logger.error("", e);
		}
		
		return value;
	}
	
	/**
	 * Quita la coma situada al final de la cadena
	 * @param stringWithComma cadena con coma
	 * @return cadena sin coma
	 */
	public static String removeFinalComma(String stringWithComma){
		
		if (!Utilities.isBlank(stringWithComma) && stringWithComma.charAt(stringWithComma.length()-1)== ','){
			stringWithComma = stringWithComma.substring(0, stringWithComma.length()-1);
		}

		return stringWithComma; 
		
	}
	
	public static String findFileStartsLike(File directory, String fileNameLike) {

		if (!directory.exists()) {
			return "";
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.endsWith(".jar") && fileName.startsWith(fileNameLike)) {
				return fileName;
			}

		}
		return "";
	}

	public static List<String> findFileEndsLike(File directory,
			String fileNameLike) {
		List<String> fileList = new ArrayList<String>();
		if (!directory.exists()) {
			return fileList;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.contains(fileNameLike) && fileName.endsWith(".jar")) {
				fileList.add(fileName);
			}

		}
		return fileList;
	}
	
	
	/**
	 * Método que devuelve el código de la aplicación
	 * 
	 * @param name - Nombre del proyecto
	 * @return - Nombre de la aplicación
	 */
	public static String getAppName(String name){
		return name.split("[A-Z]")[0];
	}
	
	/**
	 * Método que devuelve el sinónimo de la tabla de la relación M:N
	 * @param tableName - Nombre tabla (ej. X2100T00)
	 * @return - Nombre del sinónimo (ej. PetsSpecialties)
	 */
	public static String getRelationName(String tableName){
		StringBuffer nameBBDD = new StringBuffer();
		String[] arrNames = Reveng.synonymous.get(tableName).split("_");
		for (int i = 0; i < arrNames.length; i++) {
			nameBBDD.append(Utilities.camelCase(arrNames[i].toLowerCase()));
		}
		return nameBBDD.toString();
	}
	
	
	public static IRuntime addServerRuntime(IFacetedProject fpProject, String facet, String version) throws CoreException {
		
		IRuntime runtime = null;
		fpProject = ProjectFacetsManager.create(fpProject.getProject(), true, null);
		// Añade el runTime de Oracle
		Set<IRuntime> runtimes = RuntimeManager.getRuntimes();
		for (Iterator<IRuntime> iterator = runtimes.iterator(); iterator.hasNext();) {
			runtime = (IRuntime) iterator.next();
			if (runtime.getName().compareToIgnoreCase(Constants.WEBLOGIC_SERVER_RUNTIME_NAME)==0 
					&& runtime.supports(ProjectFacetsManager.getProjectFacet(facet).getVersion(version))){
				return runtime;
			}
		}
		return runtime;
	}
	
}