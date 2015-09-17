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

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Ejecuta el ProgressMonitor de manera bloqueante
 * 
 */
public class HaltProgressMonitor extends NullProgressMonitor {

	private boolean isDone = false;
	
	/**
	 *  Método encargado del estado de cancelación
	 *  @param cancelled - estado de cancelación
	 */
	@Override
	public void setCanceled(boolean cancelled) {
		isDone = true;
		super.setCanceled(cancelled);
	}

	/**
	 * Método que verifica si el ProgressMonitor está haciendo algo
	 * @return estado de ejecución del monitor
	 */
	public synchronized boolean isDone() {
		return isDone;
	}

	/**
	 * Método que termina el ProgressMonitor
	 */
	@Override
	public void done() {
		isDone = true;
		super.done();
	}

}