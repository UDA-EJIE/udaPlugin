/*
* Copyright 2022 E.J.I.E., S.A.
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
package com.ejie.uda.exporters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ejie.uda.exporters.utils.ControllerUtils;


/**
 * Exporter encargado de la edición del fichero jackson-config.xml
 */
public class JacksonExporter extends GenericExporter {
	private final static Logger logger = Logger.getLogger(JacksonExporter.class);
	
	public JacksonExporter(Configuration cfg, File outputdir) {
		super(cfg, outputdir);
		init();
	}

	protected void init() {
		setTemplateName(TemplatePath.JACKSON_FTL);
		setFilePattern(TemplatePath.JACKSON_PATTERN);
	}

	public void start() {
		try {
			String directory = getOutputDirectory().toString();
			
			if (directory.toUpperCase().endsWith("WAR")) {
				List<String> additionalContext = new ArrayList<String>();
				
				Iterator<?> iterator = super.getCfg2JavaTool().getPOJOIterator(super.getConfiguration().getClassMappings());
				while (iterator.hasNext()) {
					POJOClass element = (POJOClass) iterator.next();
					PersistentClass clazz = (PersistentClass) element.getDecoratedObject();
					String nombre = ControllerUtils.findNameFromEntity(clazz.getEntityName());
					additionalContext.add(nombre);
				}
				
				Collections.sort(additionalContext);
				
				String nombreWar = directory.substring(directory.lastIndexOf(File.separator) + 1, directory.length()-3);
				
				// Recupera el fichero jackson-config.xml
				File jacksonConfigFile = new File(directory + "/" + TemplatePath.JACKSON_PATTERN);
				
				if (jacksonConfigFile.exists()) {
					// Crea la instancia de DocumentBuilderFactory
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setValidating(false);
					factory.setNamespaceAware(true);
					factory.setIgnoringElementContentWhitespace(true);
					
					// Recuperar DocumentBuilder
					DocumentBuilder docBuilder = factory.newDocumentBuilder();
					
					// Parsear fichero de configuración de jackson
					Document doc = docBuilder.parse(jacksonConfigFile);
					
					// Normalizar el fichero
					doc.getDocumentElement().normalize();
					
					// Obtener udaModule
					NodeList nodeList = doc.getElementsByTagName("bean");
					Node udaModule = getNode(nodeList, "id", "udaModule");
					
					// Se utilizará para saber si el nodo "serializers" existe
					boolean serializersExist = false;
					
					// Obtener los nodos hijos
					NodeList udaModuleChildren = udaModule.getChildNodes();
					
					// Itera los nodos hijos
					for (int i=0; i < udaModuleChildren.getLength(); i++) {
						  Node childNode = udaModuleChildren.item(i);
						  NamedNodeMap attributes = childNode.getAttributes();
						  Node tag = attributes == null ? null : attributes.getNamedItem("name");
						  
						  // Comprueba si este es el nodo "serializers"
						  if (tag == null ? false : tag.getNodeValue().equals("serializers")) {
							  serializersExist = true;
							  
							  // Añade un nodo por cada entidad seleccionada
							  populateSerializerNode(nombreWar, additionalContext, doc, childNode.getChildNodes().item(1));
							  
							  break;
						  }
					}

					// Si previamente no se han incluido el elemento property de los serializadores, se genera la estructura
					if (!serializersExist) {
						// Crear elemento property
						Element serializerNode = doc.createElement("property");
						serializerNode.setAttribute("name", "serializers");
						udaModule.insertBefore(serializerNode, udaModule.getFirstChild());
						
						// Crear elemento util:map
						Element mapNode = doc.createElement("util:map");
						serializerNode.appendChild(mapNode);
						
						// Añade un nodo por cada entidad seleccionada
						populateSerializerNode(nombreWar, additionalContext, doc, mapNode);						
					}
					
					try (FileOutputStream output = new FileOutputStream(directory + "/" + TemplatePath.JACKSON_PATTERN)) {
		                writeXml(doc, output);
		            }
				}				
			} else {
				throw new RuntimeException("Los ficheros de configuracion de los controllers se debe generar en el proyecto WAR");
			}
		} catch (ParserConfigurationException | SAXException| IOException | TransformerException e) {
			logger.error("", e);
		}
	}

	protected void setupContext() {
		getProperties().put("sessionFactoryName", "SessionFactory");
		super.setupContext();
	}
	
	@SuppressWarnings("rawtypes")
	protected void exportComponent(Map additionalContext, POJOClass element) {
	}
	
	/*
	 * Permite buscar un nodo.
	 * 
	 * @param nodeList NodeList
	 * @param attr String
	 * @param value String
	 * 
	 * @return childNode Node
	 */
	private Node getNode(NodeList nodeList, String attr, String value) {
		for (int i = 0; i < nodeList.getLength(); i++) {
	        Node childNode = nodeList.item(i);
	        NamedNodeMap attributes = childNode.getAttributes();
	        Node tag = attributes.getNamedItem(attr);
	        
	        if (tag.getNodeValue().equals(value)) {
	        	return childNode;
	        }        
	    }
		
		return null;
	}
	
	/*
	 * Añade un nodo por cada entidad seleccionada al mapa de serializadores.
	 * 
	 * @param nombreWar String
	 * @param additionalContext List<String>
	 * @param doc Document
	 * @param nodeContainer Node
	 */
	private void populateSerializerNode(String nombreWar, List<String> additionalContext, Document doc, Node nodeContainer) {
		for (String entityName : additionalContext) {
			StringBuilder keyValue = new StringBuilder("#{T(com.ejie.");
			keyValue.append(nombreWar.split("[A-Z]")[0]);
			keyValue.append(".model.");
			keyValue.append(entityName);
			keyValue.append(")}");
		  
			// Genera el nuevo elemento
			Element entidad = doc.createElement("entry");
			entidad.setAttribute("key", keyValue.toString());
			entidad.setAttribute("value-ref", "customSerializer");
			
			// Inserta el nuevo elemento
			nodeContainer.appendChild(entidad);
		}
	}
	
	/*
	 * Escribir en el fichero recibido.
	 * 
	 * @param doc Document
	 * @param output FileOutputStream
	 */
	private static void writeXml(Document doc, FileOutputStream output) throws TransformerException, UnsupportedEncodingException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);
		Transformer transformer = transformerFactory.newTransformer();
		
		// Añade las propiedades
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(output);

        transformer.transform(source, result);
	}
}