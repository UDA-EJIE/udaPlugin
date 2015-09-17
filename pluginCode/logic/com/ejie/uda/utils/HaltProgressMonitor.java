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