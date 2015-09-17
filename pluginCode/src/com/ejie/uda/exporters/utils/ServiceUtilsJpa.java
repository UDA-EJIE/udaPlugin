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
package com.ejie.uda.exporters.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
/**
 * 
 * Clase principalmente utilizada en la generación de la capa de servicio, la cual contiene las funciones específicas para el comportamiento  de la persistencia JPA
 *
 */
@SuppressWarnings({ "unchecked", "unused" })
public class ServiceUtilsJpa {
	 private final static Logger log = Logger.getLogger(PojoUtils.class);
	 WarningSupressorJdbc warjdbc = new 	WarningSupressorJdbc();
	 private static Cfg2HbmTool c2h = new Cfg2HbmTool();
	 private static Cfg2JavaTool c2j = new Cfg2JavaTool();
	 public ServiceUtilsJpa(){

	 }
	 public static List<String> getRelatedEntities(POJOClass pojo, Configuration cfg) {
		 List<String> result = new ArrayList<String>();
		 Iterator<Property> itProp = pojo.getAllPropertiesIterator();
	     while (itProp.hasNext()){
			 Property prop = itProp.next(); 
			 if(c2h.isManyToMany(prop) && c2h.isCollection(prop)){
					Collection collection = (Collection)prop.getValue();
					ManyToOne manyToOne = (ManyToOne)collection.getElement();
					PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
					String nombreSubclass = ControllerUtils.findNameFromEntity(subclass.getEntityName());
					String auxiliar = ControllerUtils.stringDecapitalize(nombreSubclass);
					Iterator<String> aux =  result.iterator();
					boolean found = false;
					while (aux.hasNext()){
						String auxiliarClase = aux.next();
						if (auxiliar.equals(auxiliarClase)){
							found=true;
							break;
						}
						
					}	
					if (!found){
						result.add(auxiliar);
						found=false;
					}	
			 }
		 }
		 return result;
	 }

}
