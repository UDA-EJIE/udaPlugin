package com.ejie.uda.utils;

import java.io.PrintStream;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
 
/**
 * Clase encargada de generar y escribir los Logs del plugin 
 */
public class ConsoleLogger
{
	private static ConsoleLogger fDefault = null;
	private String fTitle = null;
	private MessageConsole fMessageConsole = null;
	
	/**
	 * Constructor
	 * 
	 * @param messageTitle - título de la consola creada
	 */
	public ConsoleLogger(String messageTitle){		
		fDefault = this;
		fTitle = messageTitle;
	}
	
	/**
	 * Recupera la instancia d ela consola
	 * 
	 * @return - consola instanciada
	 */
	public static ConsoleLogger getDefault() {
		return fDefault;
	}	
		
	/**
	 * Escribe en consola
	 * 
	 * @param msg - mensaje del Log
	 * @param msgKind - nivel de traza
	 */
	public void println(String msg, int msgKind){		
		if( msg == null ){
			return;
		}
		
		// Activa la vista de la consola en eclipse
		displayConsoleView();
		
		// Enseña el mensaje en la consola	
		getNewMessageConsoleStream(msgKind).println(msg);	
		// Saca todo el System.out en la consola
		System.setOut(new PrintStream(getNewMessageConsoleStream(msgKind)));
	}
	
	/**
	 * Activa la vista 'Console' del IDE para visualizar las trazas
	 * 
	 */
	private void displayConsoleView(){
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(getMessageConsole());
	}
	
	/**
	 * Recupera el flujo de mensajes de la consola
	 * 
	 * @param msgKind - nivel de trazas
	 * @return flujo de mensajes de la consola
	 */
	private MessageConsoleStream getNewMessageConsoleStream(int msgKind){		
//		int swtColorId = SWT.COLOR_DARK_GREEN;
//		
//		switch (msgKind)
//		{
//			case Constants.MSG_INFORMATION:
//				swtColorId = SWT.COLOR_DARK_GREEN;				
//				break;
//			case Constants.MSG_ERROR:
//				swtColorId = SWT.COLOR_DARK_MAGENTA;
//				break;
//			case Constants.MSG_WARNING:
//				swtColorId = SWT.COLOR_DARK_BLUE;
//				break;
//			default:				
//		}	
//		msgConsoleStream.setColor(Display.getCurrent().getSystemColor(swtColorId));
		
		MessageConsoleStream msgConsoleStream = getMessageConsole().newMessageStream();		
		
		return msgConsoleStream;
	}
	
	/**
	 * Recupera la consola
	 * 
	 * @return - consola
	 */
	private MessageConsole getMessageConsole(){
		if( fMessageConsole == null )
			createMessageConsoleStream(fTitle);	
		
		return fMessageConsole;
	}
		
	/**
	 * Crea el flujo de mensajes de la consola
	 * 
	 * @param title - nombre de la consola
	 */
	private void createMessageConsoleStream(String title){
		fMessageConsole = new MessageConsole(title, null); 
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ fMessageConsole });
	}
	
}