package com.ejie.uda.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Clase estática encargada de copiar un fichero de un origen a un destino
 *
 */
public class CopyWorker {
		
	/**
	 * Constructor
	 */
	private CopyWorker() {
			//No es instanciable
	}
	
	/**
	 * Copia un fichero de una origen a un destino
	 * 
	 * @param inputFile - Fichero de entrada
	 * @param outputFile - Fichero de salida
	 * @throws IOException
	 */
	public static void executeOperation (File inputFile, File outputFile) throws IOException {
		
		InputStream in = new FileInputStream(inputFile);
        OutputStream out = new FileOutputStream(outputFile);
        byte buf[] = new byte[1024];
        int len;
        while((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
	}	   

}