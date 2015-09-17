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
 * Clase principalmente utilizada en la generación de la capa de modelo, la cual contiene las funciones específicas para el comportamiento  de la persistencia JPA
 *
 */
@SuppressWarnings({ "unchecked" })
public class PojoUtilsJpa {
	private static Cfg2HbmTool c2h = new Cfg2HbmTool();
	private static Cfg2JavaTool c2j = new Cfg2JavaTool();
	WarningSupressorJPA wsJpa = new WarningSupressorJPA(); 

	public PojoUtilsJpa(){
		
	}
	public List<String> getDtoImports(POJOClass pojo, Configuration cfg){		
		List<String> lista = new ArrayList<String>();
		try{
		if (!pojo.isComponent()){	
		
			if (c2j.isComponent(pojo.getIdentifierProperty())){
				lista.add(pojo.getPackageName()+".model."+pojo.getDeclarationName()+"Id");
			}
		}
		Iterator<Property> itPropiedades = pojo.getAllPropertiesIterator();
		while (itPropiedades.hasNext()){
			Property propiedad = itPropiedades.next();
			if (!c2h.isCollection(propiedad) ){
				if(c2h.isManyToOne(propiedad) || c2h.isOneToMany(propiedad) || c2h.isOneToOne(propiedad)){
					if (!SkeletonUtils.alreadyExists(lista, pojo.getPackageName()+".model."+wsJpa.getJavaTypeName(propiedad, true,pojo,true))){
					lista.add(pojo.getPackageName()+".model."+wsJpa.getJavaTypeName(propiedad, true,pojo,true));
					}			
				}
				
			}else{
				if (c2h.isOneToMany(propiedad)){
					Collection collection = (Collection)propiedad.getValue();
					String collectionName = collection.getElement().getType().getName();
					String clase=collectionName.substring(collectionName.lastIndexOf(".")+1,collectionName.length());	
					if (!SkeletonUtils.alreadyExists(lista, pojo.getPackageName()+".model."+ clase)){
						lista.add(pojo.getPackageName()+".model."+ clase);
					}
	
				}else{
					
					Collection collection = (Collection)propiedad.getValue();
					ManyToOne manyToOne = (ManyToOne)collection.getElement();
					PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
					if (!SkeletonUtils.alreadyExists(lista, pojo.getPackageName()+".model."+ ControllerUtils.findNameFromEntity(subclass.getEntityName()))){
						lista.add(pojo.getPackageName()+".model."+ ControllerUtils.findNameFromEntity(subclass.getEntityName()));
					}
					
				}
				
			}
		}
		return lista;
		}catch(Exception e){
			e.getStackTrace();
			return null;
		}
		
	}
	
}
