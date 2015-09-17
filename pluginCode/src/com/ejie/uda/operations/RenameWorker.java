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
package com.ejie.uda.operations;
import java.util.Map;

/**
 * Clase estática de renombramiento de fichero 
 *
 */
public class RenameWorker {
	
	/**
	 * Constructor
	 */
	private RenameWorker() {
		//No es instanciable
	}

	/**
	 * Recupera el nombre del fichero a renombrar
	 * 
	 * @param stringToChange - texto a cambiar
	 * @param changePatterns - cambio a realizar
	 * @param context - Contexto del proyecto
	 * @return nombre al que se renombrará
	 * @throws Exception
	 */
	public static String executeOperation (String stringToChange, String[] changePatterns,
			Map<String, Object> context) throws Exception {
		
		for (String pattern : changePatterns) {			
			if (context.get(pattern) != null) {		
				stringToChange = stringToChange.replaceAll(pattern, (String) context.get(pattern));			
			}		
		}
		return stringToChange;
	}
}