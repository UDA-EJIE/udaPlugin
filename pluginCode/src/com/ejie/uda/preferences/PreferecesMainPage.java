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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ejie.uda.Activator;
import com.ejie.uda.utils.Constants;

/**
 * Crea en las preferencias del eclipse una entrada referente a este plugin 
 * para indicar la ruta física de las plantillas  
 *
 */

public class PreferecesMainPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	private static IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	/**
	 * Crea un campo para la introducción de la ruta de la plantilla
	 */
	@Override
	protected void createFieldEditors() {
		// Campos de texto acompañados de un botón para seleccionar una ruta.
		DirectoryFieldEditor templatesUDAField = new DirectoryFieldEditor(
				Constants.PREF_TEMPLATES_UDA_LOCALPATH, "&Plantillas UDA:",
				getFieldEditorParent());
		DirectoryFieldEditor configUDAField = new DirectoryFieldEditor(
				Constants.PATH_CONFIG_APP, "&Configuraciones:",
				getFieldEditorParent());
		DirectoryFieldEditor dataUDAField = new DirectoryFieldEditor(
				Constants.PATH_DATOS_APP, "&Datos:",
				getFieldEditorParent());
		
		// Check que guardará el si estamos o no en un entorno EJIE.
		BooleanFieldEditor checkEjie = new BooleanFieldEditor(
				Constants.PREF_EJIE, "Desarrollo para EJIE", getFieldEditorParent());
		checkEjie.loadDefault();
		
		// Añade los campos.
		addField(templatesUDAField);
		addField(configUDAField);
		addField(dataUDAField);
		addField(checkEjie);
		
		store.setDefault(Constants.PATH_CONFIG_APP, Constants.UNIDAD_HD + Constants.PATH_CONFIG);
		store.setDefault(Constants.PATH_DATOS_APP, Constants.UNIDAD_HD + Constants.PATH_DATOS);
	}

	/**
	 * Inicializa la preferecia de eclipse con la descripción indicada
	 * @param arg0 - workbench
	 */
	@Override
	public void init(IWorkbench arg0) {
		setDescription("Indique la carpeta raiz de las plantillas:");
		setPreferenceStore(store);
		store.getBoolean("Constants.PREF_EJIE");
	}
}
