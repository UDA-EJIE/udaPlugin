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
