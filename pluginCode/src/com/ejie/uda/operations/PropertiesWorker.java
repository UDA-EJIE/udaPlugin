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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
 
/**
 * Esta clase es utilizada para leer y escribir valores en un fichero de propiedades
 */
public class PropertiesWorker {
 
    private String propertiesFile;
    private Properties p;
 
    /**
     * Inicializa la clase y carga el fichero de propiedades
     * @param fileName a cargar
     */
    public PropertiesWorker(String fileName){
        this.propertiesFile = fileName;
        p = new Properties();
        this.loadProperties();
    }
 
    /**
     * Inicializa la clase y carga el fichero de propiedades
     * at the same time
     * @param fileName a cargar
     * @param path del fichero
     */
    public PropertiesWorker(String fileName, String path){
        this.propertiesFile = path + "/" +  fileName;
		
		try {
			ProjectWorker.createFolder(path);
			File fileProperties = new File(path, fileName);
			// A partir del objeto File creamos el fichero físicamente
			fileProperties.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        p = new Properties();
        this.loadProperties();
    }
    
    /**
     * Carga el contenido del fichero de propiedades en memoria
     */
    public void loadProperties(){
        try {
        	FileInputStream fis = new FileInputStream(propertiesFile);
            p.load(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Guardar las propiedades cargadas en memoria en el fichero
     */
    public void saveProperties(){
    	try {
    		//p.store(new FileOutputStream(propertiesFile), null);
    		Set<String> properties = p.stringPropertyNames();
    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(propertiesFile)));
    		for (Iterator<String> iterator = properties.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				writer.write(key+"="+p.getProperty(key)+"\n");
			}
    		writer.flush();
    		writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * Lee todos los valores del fichero de propiedades y los pasa oun ArrayList
     * @return ArrayList contiene todos los valores dl fichero de propiedades
     */
    public ArrayList<String> readAllValues(){
        ArrayList<String> values = new ArrayList<String>();
        Enumeration<Object> e = p.elements();
        while (e.hasMoreElements()){
            values.add((String)e.nextElement());
        }
        return values;
    }
 
    /**
     * Retorna el valor de una key
     * @param key palabra buscar en el fichero
     * @return value en el caso que encontre
     */
    public String readValue(String key){
        return p.getProperty(key);
    }
 
    /**
     * Escribe la clave y su valor en el fichero de propiedades
     * @param key clave a insertar
     * @param value valor a insertar
     */
    public void writeProperty(String key, String value){
        p.setProperty(key, value);
    }
 
}