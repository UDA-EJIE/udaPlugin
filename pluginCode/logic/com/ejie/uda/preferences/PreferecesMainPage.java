package com.ejie.uda.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
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

	/**
	 * Crea un campo para la introducción de la ruta de la plantilla
	 */
	@Override
	protected void createFieldEditors() {
		// Campo de texto acompañado de un botín para seleccionar una ruta
		DirectoryFieldEditor templatesUDAField = new DirectoryFieldEditor(
				Constants.PREF_TEMPLATES_UDA_LOCALPATH, "&Plantillas UDA:",
				getFieldEditorParent());
		// añade el campo
		addField(templatesUDAField);
		// Check que guardará el si estamos o no en un entorno EJIE
		BooleanFieldEditor checkEjie = new BooleanFieldEditor(
				Constants.PREF_EJIE, "Desarrollo para EJIE", getFieldEditorParent());
		checkEjie.loadDefault();
		// Añade el campo
		addField(checkEjie);
	}

	/**
	 * Inicializa la preferecia de eclipse con la descripción indicada
	 * @param arg0 - workbench
	 */
	@Override
	public void init(IWorkbench arg0) {

		setDescription("Indique la carpeta raiz de las plantillas:");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		Activator.getDefault().getPreferenceStore().getBoolean("Constants.PREF_EJIE");

	}
}
