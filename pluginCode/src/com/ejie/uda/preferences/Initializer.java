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
package com.ejie.uda.preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 *  Clase de preferencias de eclipse utilizada para guardar la ruta de las plantillas
 *
 */
public class Initializer extends AbstractPreferenceInitializer {

	/**
	 * Inicializa las preferencias de eclipse con un valor por defecto
	 */
	@Override
	public void initializeDefaultPreferences() {
		//IPreferenceStore store = Activator.getDefault().getPreferenceStore(); 
		//store.setValue(Constants.PREF_TEMPLATES_UDA_LOCALPATH,""); 
	}
}	
