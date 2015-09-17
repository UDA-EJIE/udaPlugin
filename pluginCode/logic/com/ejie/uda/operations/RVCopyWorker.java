package com.ejie.uda.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

/**
 * Clase estática que gestiona el copiado y renombrado de un fichero o generación del mismo por una plantilla freemarker 
 *
 */
public class RVCopyWorker {
	
	/**
	 * Constructor
	 */
	private RVCopyWorker() {
		//No es instanciable
	}

	/**
	 * Copia un fichero a un destino o lo trata mediante una plantilla freemarker.
	 * El nombre del fichero de salida será el mismo del fichero de entrada.
	 * 
	 * @param inputFile - fichero de entrada
	 * @param outputPath - ruta final del fichero
	 * @param context - datos de contexto
	 * @throws Exception
	 */
	public static void executeOperation(File inputFile, String outputPath,
			Map<String, Object> context) throws Exception {
		
		executeOperation(inputFile, outputPath, context, null); 
	}
	
	/**
	 * Copia un fichero a un destino o lo trata mediante una plantilla freemarker.
	 * El nombre del fichero de salida estará especificado.
	 * 
	 * @param inputFile - fichero de entrada
	 * @param outputPath - ruta final del fichero
	 * @param context - datos de contexto
	 * @param outputFileName - nombre del fichero de salida
	 * @throws Exception
	 */
	public static void executeOperation(File inputFile, String outputPath,
			Map<String, Object> context, String outputFileName) throws Exception {
		
		if (inputFile != null && inputFile.exists()) {			
			String inputFileName = inputFile.getName();
			
			if (outputFileName == null || ("").equals(outputFileName)){
				outputFileName = inputFileName;
				// Renombrado de fichero			
				outputFileName = RenameWorker.executeOperation(inputFileName, Constants.RENAME_PATTERNS, context);
			}

			if (inputFileName.endsWith(Constants.FREEMARKER_SUFFIX)) {
				// Tratamiento mediante freemarker
				outputFileName = outputFileName.substring(0, outputFileName.indexOf(Constants.FREEMARKER_SUFFIX));
				File outputFile = new File(outputPath + "/" + outputFileName);				
				FreemarkerWorker.executeOperation(inputFile.getParent(), inputFileName, context, outputFile);					
			} else {				
				// Copiado del fichero
				File outputFile = new File(outputPath + "/" + outputFileName); 
				if (!inputFile.isDirectory()) {
					// Si es un fichero, se copia
					CopyWorker.executeOperation(inputFile, outputFile);				
				} else if (inputFile.isDirectory()) {
					// Si es un directorio, se crea
					outputFile.mkdir();					
				}				
				// Si es un directorio, continuar recursivamente
				if (inputFile.isDirectory()) {					
					String children[] = inputFile.list();
		            for(int i = 0; i < children.length; i++) {
		            	// Llamada recursiva
		                executeOperation(new File(inputFile, children[i]), outputFile.getPath(), context, outputFileName);
		            }
				}		
			}			
		} // Fin de si el inputPath existe
	}
	
	
	
	/**
     * Copia un directorio con todo y su contendido
     * @param srcDir
     * @param dstDir
     * @throws IOException
     */
    public static void copyDirectory(File srcDir, File dstDir) throws IOException {
        if (srcDir.isDirectory()) {
        	if (!srcDir.isHidden()){
        		if (!dstDir.exists()) {
                    dstDir.mkdir();
                }
                
                String[] children = srcDir.list();
                for (int i=0; i<children.length; i++) {
                	if (!srcDir.isHidden()){
                		copyDirectory(new File(srcDir, children[i]),
                                new File(dstDir, children[i]));	
                	}
                }
        	}
        } else {
            copy(srcDir, dstDir);
        }
    }
    
    /**
     * Copia un solo archivo
     * @param src
     * @param dst
     * @throws IOException
     */
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        
        
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    } 

    
	/**
     * Copia un directorio con sus ficheros y fichero como plantillas
     * @param srcDir
     * @param dstDir
	 * @throws Exception 
     */
    public static void copyDirectoryTemplate(File srcDir, File dstDir, Map<String, Object> context) throws Exception {
        if (srcDir.isDirectory()) {
        	if (!srcDir.isHidden()){
        		if (!dstDir.exists()) {
                    dstDir.mkdir();
                }
                
                String[] children = srcDir.list();
                for (int i=0; i<children.length; i++) {
                	if (!srcDir.isHidden()){
                		copyDirectoryTemplate(new File(srcDir, children[i]), new File(dstDir, children[i]), context);	
                	}
                }
        	}
        } else {
        	String outputFileName = srcDir.getName();
        	
        	if (!Utilities.isBlank(outputFileName)){
        		if (!outputFileName.endsWith(Constants.FREEMARKER_SUFFIX)) {
        			copy(srcDir, dstDir);
        		}else{
        			
        			outputFileName = outputFileName.substring(0, outputFileName.length() - 4);
        			String pathTemplate = srcDir.getPath().substring(0, srcDir.getPath().length() - (srcDir.getName().length() + 1));
        			String pathTemplateDestination = dstDir.getPath().substring(0, dstDir.getPath().length() - (dstDir.getName().length() + 1));
        			
        			File file = new File(pathTemplateDestination + "\\" + outputFileName);
        			FreemarkerWorker.executeOperation(pathTemplate, srcDir.getName(), context, file);	
        		}
        	}
        }
    }

	
}