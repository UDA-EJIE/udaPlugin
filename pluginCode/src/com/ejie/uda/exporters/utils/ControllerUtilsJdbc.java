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
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.BasicPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
/**
 * 
 *  Clase principalmente utilizada en la generación de la capa de control, la cual contiene las funciones específicas para el comportamiento  de la persistencia JDBC
 */

@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class ControllerUtilsJdbc {
	private static Logger log = Logger.getLogger(ControllerUtilsJdbc.class);
	private static Cfg2HbmTool c2h = new Cfg2HbmTool();
	private static Cfg2JavaTool c2j = new Cfg2JavaTool();
	WarningSupressorJdbc wsJdbc = new WarningSupressorJdbc(); 
	
	public List<String[]> getPrimaryKeyInitilize(POJOClass pojo, Configuration cfg){
		try {
			List<String[]> result =  new ArrayList<String[]>();
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			String tipo =null;
			String getter=null;
			if (c2j.isComponent(pojo.getIdentifierProperty())){
				Iterator<Property> itPrimariaSub= (Iterator<Property>) c2h.getProperties((Component)clazz.getIdentifier());
				while (itPrimariaSub.hasNext()){
					 Property prop= itPrimariaSub.next();
					if (c2h.isOneToMany(prop) || c2h.isManyToAny(prop) || c2h.isManyToOne(prop)){
						PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
						if (!c2j.isComponent(subclass.getIdentifier())){
							String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName());
							getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"()";
							tipo = wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, true);
							String[] strAt= {nombre, tipo,getter,subclass.getIdentifierProperty().getName()};
							 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){
		
								result.add(strAt);
							 }	
						}else{//subclase compuesta
							Iterator<Property> itPrimariaSubclass= (Iterator<Property>) c2h.getProperties((Component)subclass.getIdentifier());
							while (itPrimariaSubclass.hasNext()){
								Property propSubclas = itPrimariaSubclass.next();
								if (!c2h.isOneToMany(propSubclas)||!c2h.isManyToOne(propSubclas)){
									String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(propSubclas.getName());
									getter =ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"()";
									tipo = wsJdbc.getJavaTypeName(propSubclas, true, true);
									String[] strAt= {nombre, tipo,getter,propSubclas.getName()};
									 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("CLOB") 
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("BLOB")
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("OBJECT")
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("LOB")){

										 result.add(strAt);
									 }	 
								}else{
									PersistentClass subclaseTwo = cfg.getClassMapping(propSubclas.getType().getName());
									if (!c2j.isComponent(subclaseTwo.getIdentifier())){
										String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclaseTwo.getIdentifierProperty().getName());
										getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
										tipo = wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, true);
										String[] strAt= {nombre, tipo ,getter,subclaseTwo.getIdentifierProperty().getName()};
										 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){

							
											 result.add(strAt);
										 }	 
									}else{	
										Iterator<Property> itPrimSubclasTwo= (Iterator<Property>) c2h.getProperties((Component)subclaseTwo.getIdentifier());
										while (itPrimSubclasTwo.hasNext()){
											Property itPropSubClassTwo= itPrimSubclasTwo.next();
											getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
											
											String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize(itPropSubClassTwo.getName());
											tipo = wsJdbc.getJavaTypeName(itPropSubClassTwo, true, true);
											String[] strAt= {nombre, tipo,getter,itPropSubClassTwo.getName()};
											 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("CLOB") 
											    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("BLOB")
											    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("OBJECT")
											    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("LOB")){

												 result.add(strAt);
											 }	 
										}
									}
										
								}
							}	
						}
					}
					
			}}
			
			return result;
		}catch (Exception e){	
			log.error("Error:" +e.getMessage() + e.getCause());
			return null;
		}
	}

	public List<String[]> getPrimaryKey(POJOClass pojo, Configuration cfg){
		try {
			List<String[]> result =  new ArrayList<String[]>();
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			String tipo =null;
			String getter=null;
			if (!c2h.isCollection(pojo.getIdentifierProperty()) && !c2j.isComponent(pojo.getIdentifierProperty())){
				Property prop = pojo.getIdentifierProperty();
				
				if (!c2h.isOneToMany(prop) && !c2h.isManyToOne(prop)){
					getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName());
					tipo = wsJdbc.getJavaTypeName(prop, true, true);
					String[] strAt= {prop.getName(), tipo,getter,prop.getName()};
					 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("CLOB") 
					    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("BLOB")
					    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("OBJECT")
					    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("LOB")){
	
						 result.add(strAt);
					 }	 
				}else{
					PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
					if (!c2j.isComponent(subclass.getIdentifier())){
						getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"()";
						tipo = wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, true);
						String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName());
						String[] strAt= {nombre, tipo,getter,subclass.getIdentifierProperty().getName()};
						 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
						    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
						    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
						    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){
		
							 result.add(strAt);
						 }	 
					}else{//subclase compuesta
						Iterator<Property> itPrimariaSubclass= (Iterator<Property>) c2h.getProperties((Component)subclass.getIdentifier());
						while (itPrimariaSubclass.hasNext()){
							Property propSubclas = itPrimariaSubclass.next();
							if (!c2h.isOneToMany(propSubclas)||!c2h.isManyToOne(propSubclas)){
								String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(propSubclas.getName());
								getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"()";
								tipo = wsJdbc.getJavaTypeName(propSubclas, true, true);
								String[] strAt= {nombre, tipo,getter,propSubclas.getName()};
								 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("CLOB") 
								    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("BLOB")
								    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("OBJECT")
								    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("LOB")){
									 result.add(strAt);
								 }	 
							}else{
								PersistentClass subclaseTwo = cfg.getClassMapping(propSubclas.getType().getName());
								if (!c2j.isComponent(subclaseTwo.getIdentifier())){
									getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
									tipo = wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, true);
									String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclaseTwo.getIdentifierProperty().getName());
									String[] strAt= {nombre, tipo,getter,subclaseTwo.getIdentifierProperty().getName()};
									 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){
					
										 result.add(strAt);
									 }	 
								}else{	
									Iterator<Property> itPrimSubclasTwo= (Iterator<Property>) c2h.getProperties((Component)subclaseTwo.getIdentifier());
									while (itPrimSubclasTwo.hasNext()){
										Property itPropSubClassTwo= itPrimSubclasTwo.next();
										getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
										tipo = wsJdbc.getJavaTypeName(itPropSubClassTwo, true, true);
										String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize(itPropSubClassTwo.getName());
										String[] strAt= {nombre, tipo,getter,itPropSubClassTwo.getName()};
										 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("CLOB") 
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("BLOB")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("OBJECT")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("LOB")){
							
											 result.add(strAt);
										 }	 
									}
								}
									
							}
						}	
					}
				}
			}else {
				if (c2j.isComponent(pojo.getIdentifierProperty())){ //clave compuesta 
				Iterator<Property> itPrimariaSub= (Iterator<Property>) c2h.getProperties((Component)clazz.getIdentifier());
				while (itPrimariaSub.hasNext()){
					 Property prop= itPrimariaSub.next();
					
					if (!c2h.isOneToMany(prop) && !c2h.isManyToAny(prop) && !c2h.isManyToOne(prop)){
						getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName());
						tipo = wsJdbc.getJavaTypeName(prop, true, true);
						String[] strAt= {prop.getName(), tipo,getter,prop.getName()};
						 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("CLOB") 
						    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("BLOB")
						    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("OBJECT")
						    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("LOB")){
	
							 result.add(strAt);
						 }	 
					}else{
						PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
						if (!c2j.isComponent(subclass.getIdentifier())){
							String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName());
							getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"()";
							tipo = wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, true);
							String[] strAt= {nombre, tipo,getter,subclass.getIdentifierProperty().getName()};
							 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){
		
								result.add(strAt);
							 }	
						}else{//subclase compuesta
							Iterator<Property> itPrimariaSubclass= (Iterator<Property>) c2h.getProperties((Component)subclass.getIdentifier());
							while (itPrimariaSubclass.hasNext()){
								Property propSubclas = itPrimariaSubclass.next();
								if (!c2h.isOneToMany(propSubclas)||!c2h.isManyToOne(propSubclas)){
									String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(propSubclas.getName());
									getter =ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"()";
									tipo = wsJdbc.getJavaTypeName(propSubclas, true, true);
									String[] strAt= {nombre, tipo,getter,propSubclas.getName()};
									 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("CLOB") 
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("BLOB")
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("OBJECT")
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("LOB")){

										 result.add(strAt);
									 }	 
								}else{
									PersistentClass subclaseTwo = cfg.getClassMapping(propSubclas.getType().getName());
									if (!c2j.isComponent(subclaseTwo.getIdentifier())){
										String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclaseTwo.getIdentifierProperty().getName());
										getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
										tipo = wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, true);
										String[] strAt= {nombre, tipo ,getter,subclaseTwo.getIdentifierProperty().getName()};
										 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){

							
											 result.add(strAt);
										 }	 
									}else{	
										Iterator<Property> itPrimSubclasTwo= (Iterator<Property>) c2h.getProperties((Component)subclaseTwo.getIdentifier());
										while (itPrimSubclasTwo.hasNext()){
											Property itPropSubClassTwo= itPrimSubclasTwo.next();
											getter = ControllerUtils.stringDecapitalize(pojo.getDeclarationName())+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
											
											String nombre= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize(itPropSubClassTwo.getName());
											tipo = wsJdbc.getJavaTypeName(itPropSubClassTwo, true, true);
											String[] strAt= {nombre, tipo,getter,itPropSubClassTwo.getName()};
											 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("CLOB") 
											    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("BLOB")
											    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("OBJECT")
											    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("LOB")){

												 result.add(strAt);
											 }	 
										}
									}
										
								}
							}	
						}
					}
					
			}}}
			
			return result;
		}catch (Exception e){	
			log.error("Error:" +e.getMessage() + e.getCause());
			return null;
		}
	}
	
	public List<String[]> getPojoFieldsParameter(POJOClass pojo, Configuration cfg){
		try{
			List<String[]> result =  new ArrayList<String[]>();
			
			//Primero obtenemos la sprimarias
			PersistentClass clazz = (PersistentClass) pojo.getDecoratedObject();
			List<String[]> resultado = getPrimaryKey(pojo, cfg );
			Iterator itAux=	resultado.iterator();
			while (itAux.hasNext()){
				String[] auxiliar= (String[]) itAux.next();
				result.add(auxiliar); 
			}
			//Obtenemos todos los campos, pero si son de primaria los descartamos que ya están calculados arriba
			Iterator<Property> itFields= pojo.getAllPropertiesIterator();
			while (itFields.hasNext()){
				Property prop= itFields.next();
				if(!clazz.getIdentifierProperty().equals(prop)) {
					if (!c2h.isCollection(prop)){
						if (!c2h.isOneToMany(prop) && !c2h.isManyToOne(prop) && !c2h.isOneToOne(prop)){
							String tipo = wsJdbc.getJavaTypeName(prop, true, true);	
							String[] strAt= {prop.getName(),  tipo ,"",prop.getName()};
							 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("CLOB") 
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("BLOB")
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("OBJECT")
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("LOB")){

								 result.add(strAt);
							 }	 
						}else if (c2h.isOneToMany(prop) || c2h.isManyToOne(prop)){
							PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
							int contador=0;
								Iterator<Column> columnas= prop.getColumnIterator();
								while (columnas.hasNext()){
									contador=contador+1;
									Column columns= columnas.next();
									Iterator<Column> it =  columns.getValue().getColumnIterator();
									String columnName= null;
									int contadorAux= contador;
									while(it.hasNext() && contadorAux>0){
										Column columna= it.next();
										if (contadorAux==1){
										    columnName= columna.getName();
											contadorAux=contadorAux-1;
										}else{
											contadorAux=contadorAux-1;
										}
										
									}
									
									List<String[]> listaAux= getFieldsSubclass( subclass, cfg, clazz.getEntityName(), columnName, contador);
									Iterator<String[]> itAuxiliar=listaAux.iterator();
									while (itAuxiliar.hasNext()){
										String[] regAux= itAuxiliar.next();
										result.add(regAux);
									}
								}	
							}
					}
				}
			}
			return result;
		}catch(Exception e){
			log.error("Error:"+e.getCause()+e.getMessage());
			return null;
		}
	}

	public List<String[]> getFieldsSubclass(PersistentClass subclass,Configuration cfg, String entityName, String columnaPadre, int contador){
		List<String[]> result =  new ArrayList<String[]>();
		if (!c2h.isCollection(subclass.getIdentifierProperty()) && !c2j.isComponent(subclass.getIdentifierProperty())){
				//es un campo de tipo simple
			if (subclass.getEntityName().equals(entityName)){
				String nombreAux= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(columnaPadre);
				String  tipo = wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, true);	
				String[] strAt= {nombreAux,  tipo ,"",columnaPadre};
				 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){

					 result.add(strAt);
				 }	 
			}else{
				String nombreAux= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName());
				String tipo = wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, true);	
				String[] strAt= {nombreAux,  tipo ,"",subclass.getIdentifierProperty().getName()};
				 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){

					 result.add(strAt);
				 }	 
			}	
		 
		}else if(c2j.isComponent(subclass.getIdentifierProperty())) { //componente
			Iterator iterador = c2h.getProperties((Component)subclass.getIdentifier());
			int contadorAux = contador;
			while (iterador.hasNext() ){
				Property propiedad = (Property) iterador.next();
				if (contadorAux==1)		{	
						List<String[]> listaAux =  getSubclassRelations(propiedad,cfg,entityName, columnaPadre,subclass.getEntityName());
						Iterator<String[]> itAux=listaAux.iterator();
						while (itAux.hasNext()){
							String[] regAux= itAux.next();
							result.add(regAux);
						}
						contadorAux=contadorAux-1;
				}	
						else{
							contadorAux=contadorAux-1;
						}
									
			}
		}
		return result;
		
	}
	public List<String[]> getSubclassRelations(Property propiedad,Configuration cfg, String entityName,String nombre,String nombreHijo){ // calculamos si es relacion y si es asi escalamos al 3º nivel
		List<String[]> result =  new ArrayList<String[]>();
		if (!c2h.isManyToOne(propiedad) && !c2h.isOneToMany(propiedad) && !c2h.isOneToOne(propiedad)){
			String tipo = wsJdbc.getJavaTypeName(propiedad, true, true);	
			if (nombreHijo.equals(entityName)){
				String nombreAux= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(entityName))+ BasicPOJOClass.beanCapitalize(nombre.toLowerCase());
				String[] strAt= {nombreAux,  tipo ,"",nombre.toLowerCase()};
				 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("CLOB") 
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("BLOB")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("OBJECT")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("LOB")){

					 result.add(strAt);
				 }	 
			}else{
				String nombreAux= ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(entityName))+ BasicPOJOClass.beanCapitalize(propiedad.getName());
				String[] strAt= {nombreAux,  tipo ,"",propiedad.getName()};
				 if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("CLOB") 
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("BLOB")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("OBJECT")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("LOB")){

					 result.add(strAt);
				 }	 
				
			}
			
		}else{//es una relacion. Hay que escalar al tercer nivel.
			PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
			if (!subclase.getEntityName().equals(entityName)){
			List<String[]> listAux= getFieldsSubclass(subclase,cfg,entityName,propiedad.getName(),1);	
			Iterator<String[]> itAux=listAux.iterator();
			while (itAux.hasNext()){
				String[] regAux= itAux.next();
				if (subclase.getEntityName().equals(entityName)){
					regAux[0] = nombre ; 	
				}
				result.add(regAux);
			}}
			
			
		}
		return result;
	}
	public String getConstructor(POJOClass pojo, Configuration cfg){
		try{
			String result=null;
			PersistentClass clazz = (PersistentClass) pojo.getDecoratedObject();
			result = getPrimaryKeyConstructor(pojo, cfg);
			
			String resultAux= getPrimaryFieldsConstructor(pojo,cfg);
			if (result!= null ){
				if (resultAux!=null){
					result= result + ", " +resultAux;
				}
			}else{
				result= resultAux;
			}
		    if (result==null){
		    	result="(";
		    }
			return "new " + pojo.getDeclarationName() + result+")";
		}catch(Exception e){
			log.error("Error:"+e.getCause() + e.getMessage());
			return null;
		}
		
	}

	public String getPrimaryKeyConstructor(POJOClass pojo, Configuration cfg){
		try {
			String result =  null;
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			String resultAux= null;
			
			if (!c2h.isCollection(pojo.getIdentifierProperty()) && !c2j.isComponent(pojo.getIdentifierProperty())){
				Property prop = pojo.getIdentifierProperty();
				if (!c2h.isOneToMany(prop) && !c2h.isManyToOne(prop)){
				  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("CLOB") 
					    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("BLOB")
					    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("OBJECT")
					    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("LOB")){

						if (result!=null){
							result= result + ", " + prop.getName();
						}else{
							result= "("+ prop.getName();
						}
				  }	else{
					  if (result!=null){
							result= result + ", null" ;
						}else{
							result= "(null" ;
						}
				  }
				}else{
					PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
					pojo.importType(pojo.getPackageName()+".model."+ ControllerUtils.findNameFromEntity(subclass.getEntityName()));
					if (!c2j.isComponent(subclass.getIdentifier())){
						resultAux= null;
						if(result!=null){
							 resultAux= ", new " + ControllerUtils.findNameFromEntity(subclass.getEntityName()) + "(";
						}else{
							resultAux= " new " + ControllerUtils.findNameFromEntity(subclass.getEntityName()) + "(";
						}
						String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName());
						resultAux = resultAux +nombre ;
						Iterator<Property> itSubProp= subclass.getPropertyIterator();
						while (itSubProp.hasNext()){
							Property propRest= itSubProp.next();
							resultAux = resultAux + ", null";
							
						}
					}else{//subclase compuesta
						Iterator<Property> itPrimariaSubclass= (Iterator<Property>) c2h.getProperties((Component)subclass.getIdentifier());
						if(result!=null){
							 resultAux= ", new " + ControllerUtils.findNameFromEntity(subclass.getEntityName()) + "(";
						}else{
							resultAux= " new " + ControllerUtils.findNameFromEntity(subclass.getEntityName()) + "(";
						}
						String auxiliar= null;
						while (itPrimariaSubclass.hasNext()){
							Property propSubclas = itPrimariaSubclass.next();
							if (!c2h.isOneToMany(propSubclas)||!c2h.isManyToOne(propSubclas)){
								String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize( propSubclas.getName());
								  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("CLOB") 
								    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("BLOB")
								    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("OBJECT")
								    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("LOB")){

									if (auxiliar!=null){
										auxiliar = auxiliar +", " + nombre;
									}else{
										auxiliar = nombre ;
									}
								  }	else{
									    if (auxiliar!=null){
										  auxiliar= auxiliar + ", null" ;
										}else{
											auxiliar= "null" ;
										}
								  }
								  resultAux= resultAux + auxiliar;
								 
							}else{
								PersistentClass subclaseTwo = cfg.getClassMapping(propSubclas.getType().getName());
								pojo.importType(pojo.getPackageName()+".model."+ ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()));
								if (resultAux!=null){
									resultAux= ", new " + ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()) + "(";
								}else{
									resultAux= " new " + ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()) + "(";
								}
								
								if (!c2j.isComponent(subclaseTwo.getIdentifier())){
									String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclaseTwo.getIdentifierProperty().getName());
									auxiliar= auxiliar + " " + nombre;
									Iterator<Property> itSubPropTwo= subclaseTwo.getPropertyIterator();
									while (itSubPropTwo.hasNext()){
										Property propRest= itSubPropTwo.next();
										auxiliar = auxiliar + ", null";
										
									}
									resultAux = resultAux+ auxiliar + ")";
									
								}else{	
									Iterator<Property> itPrimSubclasTwo= (Iterator<Property>) c2h.getProperties((Component)subclaseTwo.getIdentifier());
									if (auxiliar!=null){
										resultAux= ", new " + ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()) + "(";
										
									}else{
										resultAux= " new " + ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()) + "(";
										
									}
									String auxiliares=null;
									while (itPrimSubclasTwo.hasNext()){
										Property itPropSubClassTwo= itPrimSubclasTwo.next();
										  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("CLOB") 
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("BLOB")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("OBJECT")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("LOB")){

									
											String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize(itPropSubClassTwo.getName());
	
											if (auxiliares!=null){
												auxiliares=auxiliares+", "+ nombre;
											}else{
												auxiliares= nombre;
											}
										  }
										  else{
											    if (auxiliares!=null){
											    	auxiliares= auxiliares + ", null" ;
												}else{
													auxiliares= "null" ;
												}
										  }
										
									}
									Iterator<Property> itSuclassTwo = subclaseTwo.getPropertyIterator();
									while (itSuclassTwo.hasNext()){
										if (auxiliares!=null){
											auxiliares=auxiliares+", null";
										}else{
											auxiliares= "null";
										}
									}
									resultAux = resultAux + auxiliares + ")";
									Iterator<Property> itSubPropTwo= subclaseTwo.getPropertyIterator();
									while (itSubPropTwo.hasNext()){
										Property propRest= itSubPropTwo.next();
										resultAux = resultAux + ", null";
										
									}
									resultAux = resultAux + ")";
									
								}
									
							}
						}	
					}
					if (result!=null){
						result= result + resultAux;
					}else{
						result=  resultAux;
					}
					
					
				} //hacer la suma total de strings
				
			}else {
				if (c2j.isComponent(pojo.getIdentifierProperty())){ //clave compuesta 
				Iterator<Property> itPrimariaSub= (Iterator<Property>) c2h.getProperties((Component)clazz.getIdentifier());
				while (itPrimariaSub.hasNext()){
					 Property prop= itPrimariaSub.next();
					if (!c2h.isOneToMany(prop) && !c2h.isManyToAny(prop) && !c2h.isManyToOne(prop)){
						  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("CLOB") 
						    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("BLOB")
						    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("OBJECT")
						    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("LOB")){

								if (result!=null){
									result= result + ", " + prop.getName();
								}else{
									result= "("+ prop.getName();
								}
						  }	 else{
							  if (result!=null){
									result= result + ", null";
								}else{
									result= "(null";
								}
						  }
					}else{
						
						PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
						pojo.importType(pojo.getPackageName()+".model."+ ControllerUtils.findNameFromEntity(subclass.getEntityName()));
						resultAux= null;
						if(result!=null){
							 resultAux= ", new " + ControllerUtils.findNameFromEntity(subclass.getEntityName()) + "(";
						}else{
							resultAux= "( new " + ControllerUtils.findNameFromEntity(subclass.getEntityName()) + "(";
						}
						if (!c2j.isComponent(subclass.getIdentifier())){
							String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName());
							resultAux = resultAux +nombre ;
							Iterator<Property> itSubProp= subclass.getPropertyIterator();
							while (itSubProp.hasNext()){
								Property propRest= itSubProp.next();
								resultAux = resultAux + ", null";
							}
						}else{//subclase compuesta
							Iterator<Property> itPrimariaSubclass= (Iterator<Property>) c2h.getProperties((Component)subclass.getIdentifier());
							
							String auxiliar= null;
							while (itPrimariaSubclass.hasNext()){
								Property propSubclas = itPrimariaSubclass.next();
								if (!c2h.isOneToMany(propSubclas)||!c2h.isManyToOne(propSubclas)){
									  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("CLOB") 
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("BLOB")
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("OBJECT")
									    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propSubclas, true, false),false).toUpperCase().endsWith("LOB")){

											String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize( propSubclas.getName());
		
											if (auxiliar!=null){
												auxiliar = auxiliar +", " +nombre;
											}else{
												auxiliar = nombre;
											}
									  }	
									  else{
										  if (auxiliar!=null){
												auxiliar = auxiliar +", null" ;
											}else{
												auxiliar = "null";
											}
									  }
								}else{
									PersistentClass subclaseTwo = cfg.getClassMapping(propSubclas.getType().getName());
									pojo.importType(pojo.getPackageName()+".model."+ ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()));
									if (auxiliar!=null){
										auxiliar= ", new " + ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()) + "(";
									}else{
										auxiliar= " new " + ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()) + "(";
									}
									if (!c2j.isComponent(subclaseTwo.getIdentifier())){
										String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize(  subclaseTwo.getIdentifierProperty().getName());

										auxiliar= auxiliar + " " + nombre;
										Iterator<Property> itSubPropTwo= subclaseTwo.getPropertyIterator();
										while (itSubPropTwo.hasNext()){
											Property propRest= itSubPropTwo.next();
											auxiliar = auxiliar + ", null";
										}
										auxiliar = auxiliar + ")";
									}else{	
										Iterator<Property> itPrimSubclasTwo= (Iterator<Property>) c2h.getProperties((Component)subclaseTwo.getIdentifier());
										if (auxiliar!=null){
											auxiliar= ", new " + ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()) + "(";
										}else{
											auxiliar= " new " + ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()) + "(";
										}
										String auxiliares=null;
										while (itPrimSubclasTwo.hasNext()){
											Property itPropSubClassTwo= itPrimSubclasTwo.next();
											String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName()))+ BasicPOJOClass.beanCapitalize( itPropSubClassTwo.getName());
											  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("CLOB") 
											    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("BLOB")
											    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("OBJECT")
											    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(itPropSubClassTwo, true, false),false).toUpperCase().endsWith("LOB")){

												if (auxiliares!=null){
													auxiliares=auxiliares+", "+ nombre;
												}else{
													auxiliares= nombre;
												}
											  }	  else{
												  if (auxiliares!=null){
														auxiliares=auxiliares+", null";
													}else{
														auxiliares= "null";
													}
											  }
											
										}
										Iterator<Property> itSuclassTwo = subclaseTwo.getPropertyIterator();
										while (itSuclassTwo.hasNext()){
											if (auxiliares!=null){
												auxiliares=auxiliares+", null";
											}else{
												auxiliares= "null";
											}
										}
										
										resultAux = resultAux + auxiliar + auxiliares + ")";
									
									}
										
								}
							}	
						}
						if (result!=null){
							result= result + resultAux + ")";
						}else{
							result=  resultAux + ")";
						}
					
					}
					
			}}}
			
			return  result;
		}catch (Exception e){	
			log.error("Error:" +e.getMessage() + e.getCause());
			return null;
		}
	}
	
	public String getPrimaryFieldsConstructor(POJOClass pojo, Configuration cfg){
		try {
			String result =  null;
			String resultSubmap =  "";
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			Iterator<Property> itFields= pojo.getAllPropertiesIterator();
			while (itFields.hasNext()){
				Property prop= itFields.next();
				if(!clazz.getIdentifierProperty().equals(prop)) {
					if (!c2h.isCollection(prop)){
						if (!c2h.isOneToMany(prop) && !c2h.isManyToOne(prop) && !c2h.isOneToOne(prop) ){
							  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("CLOB") 
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("BLOB")
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("OBJECT")
							    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("LOB")){

								if (result!=null){
									result = result + ", " + prop.getName();
								}else{
									result = prop.getName();
								}
							  }
							  else{
								  if (result!=null){
										result = result + ", null" ;
									}else{
										result ="null";
									}
							  }
						}else if (c2h.isOneToOne(prop)){
						   if (resultSubmap!=null){
							   resultSubmap = resultSubmap + ", null"  ;
							}else{
								resultSubmap = "null";
							}
						}else if (c2h.isOneToMany(prop) || c2h.isManyToOne(prop)){
							PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
							if (resultSubmap.equals("")){
								resultSubmap= resultSubmap + "new "+ ControllerUtils.findNameFromEntity(subclass.getEntityName())+ "(";
							}else{
								resultSubmap= resultSubmap + ", new "+ ControllerUtils.findNameFromEntity(subclass.getEntityName())+ "(";	
							}
							int contador=0;
								Iterator<Column> columnas= prop.getColumnIterator();
								while (columnas.hasNext()){
									contador=contador+1;
									Column columns= columnas.next();
									Iterator it =  columns.getValue().getColumnIterator();
									String columnName= null;
									int contadorAux= contador;
									while(it.hasNext() && contadorAux>0){
										Column columna= (Column) it.next();
										if (contadorAux==1){
										    columnName= columna.getName();
											contadorAux=contadorAux-1;
										}else{
											contadorAux=contadorAux-1;
										}
										
									}
									
									String listaAux = getFieldsSubclassConstructor( subclass, cfg, clazz.getEntityName(), columnName, contador);
								  
									if (!resultSubmap.equals("") )
										 if (!resultSubmap.substring(resultSubmap.length()-1,resultSubmap.length()).equals("(") )
									resultSubmap= resultSubmap +", "+ listaAux; else	resultSubmap= resultSubmap + listaAux;
								
								}
								Iterator<Property> propieds = subclass.getPropertyIterator();
								while (propieds.hasNext()){
									Property pors = propieds.next();
									if (resultSubmap.equals("")){
										resultSubmap = resultSubmap + "null";
									}else{
										resultSubmap = resultSubmap  + ", null";
									}
									
									
								}	
								
								resultSubmap=resultSubmap+")";
							}
					}
				}
			}
			if (!resultSubmap.equals("") ){ 
				if (resultSubmap.trim().substring(0,1).equals(","))
					if (result!=null){
						result = result + resultSubmap;
					}else{
						result =  resultSubmap;
					}
					else {
						if (result!=null){
							result = result + ", "+resultSubmap;
						}else{
							result = resultSubmap;
						}
					}	
				
			}
			return result;
		}catch (Exception e){	
			log.error("Error:" +e.getMessage() + e.getCause());
			return null;
		}
	}

	public String getFieldsSubclassConstructor(PersistentClass subclass,Configuration cfg, String entityName, String columnaPadre, int contador){
		String result = "";
		if (!c2h.isCollection(subclass.getIdentifierProperty()) && !c2j.isComponent(subclass.getIdentifierProperty())){
				//es un campo de tipo simple
			
			if (subclass.getEntityName().equals(entityName)){
				  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){

					String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize( columnaPadre);
	
					if (result.equals("")){
						result = result + nombre;
					}else{
						result = result + ", " + nombre;
					}
				  }  else{
					  if (result.equals("")){
							result = result + "null";
						}else{
							result = result + ", null" ;
						}
				  }	
			}else{
				  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){

						String nombre=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(subclass.getEntityName()))+ BasicPOJOClass.beanCapitalize( subclass.getIdentifierProperty().getName());
		
						if (result.equals("")){
							result = result + nombre;
						}else{
							result = result + ", " + nombre;
						}
				  }	 else{
					  if (result.equals("")){
							result = result + "null";
						}else{
							result = result + ", null" ;
						}
				  }		
			}	
			
		 
		}else if(c2j.isComponent(subclass.getIdentifierProperty())) { //componente
			Iterator iterador = c2h.getProperties((Component)subclass.getIdentifier());
			int contadorAux = contador;
			while (iterador.hasNext() ){
				Property propiedad = (Property) iterador.next();
				if (contadorAux==1)		{
						String listaAux =  getSubclassRelationsConstructor(propiedad,cfg,entityName, columnaPadre,subclass.getEntityName());
						if (result.equals("")){
							result = result + listaAux;
						}else{
							result = result  + listaAux;
						}
						
						contadorAux=contadorAux-1;
				}	
						else{
							contadorAux=contadorAux-1;
						}
									
			}
			
		}
		return result;
		
	}
	public String getSubclassRelationsConstructor(Property propiedad,Configuration cfg, String entityName,String nombre,String nombreHijo){ // calculamos si es relacion y si es asi escalamos al 3º nivel
		String result = "";
		if (!c2h.isManyToOne(propiedad) && !c2h.isOneToMany(propiedad) && !c2h.isOneToOne(propiedad)){
			if (nombreHijo.equals(entityName)){
				  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("CLOB") 
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("BLOB")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("OBJECT")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("LOB")){

						String nombreAux=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(entityName))+ BasicPOJOClass.beanCapitalize(  nombre.toLowerCase());
						if (result.equals("")){
							result= result +  nombreAux;
						}else{
							result= result + ", " + nombreAux;
						}
				  }	else{
						if (result.equals("")){
							result= result +  "null";
						}else{
							result= result + ", null" ;
						}
				  }			
			}else{
				  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("CLOB") 
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("BLOB")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("OBJECT")
				    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("LOB")){

						String nombreAux=ControllerUtils.stringDecapitalize(ControllerUtils.findNameFromEntity(entityName))+ BasicPOJOClass.beanCapitalize(   propiedad.getName());
						if (result.equals("")){
							
							result= result + nombreAux;
						}else{
							result= result + ", " +  nombreAux;
						}
				  }else{
					  if (result.equals("")){
							
							result= result +  "null";
						}else{
							result= result + ", null" ;
						}
				  }				
				
				
			}
			
		}else{//es una relacion. Hay que escalar al tercer nivel.
			PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
			if (result.equals("")){
				result = "new " + ControllerUtils.findHibernateName(subclase.getEntityName()) + "(";
			}else{
				result = ", new " + ControllerUtils.findHibernateName(subclase.getEntityName()) + "(";
			}
			if (!subclase.getEntityName().equals(entityName)){
				String listAux= getFieldsSubclassConstructor(subclase,cfg,entityName,propiedad.getName(),1);	
				result=result + listAux;
			}
			
			
		}
		return result;
	}

	
	public static List<String> getFromParams(POJOClass pojo, Configuration cfg) {
		try {
			List<String> result = new ArrayList<String>();
			Iterator<Property> ite = pojo.getAllPropertiesIterator();
			PersistentClass clazz = (PersistentClass) pojo.getDecoratedObject();
			while (ite.hasNext()) {
				Property prop = ite.next();
				if(!clazz.getIdentifierProperty().equals(prop)) {
					if (!c2h.isCollection(prop)	&& (c2h.isOneToMany(prop) || c2h.isManyToOne(prop))) {
						PersistentClass subclase = cfg.getClassMapping(prop.getType().getName());
						// Esto lleva todo menos la clave primaria
						Iterator auxiliar = result.iterator();
						String name2 = ControllerUtils.findNameFromEntity(subclase.getEntityName().toString());
						boolean encontrado=false;
						while (auxiliar.hasNext() && !encontrado){
							String nombre = (String) auxiliar.next();
							if (nombre.equals(name2)){ 
								encontrado=true;
							}
						}
						if (!encontrado){
							result.add(name2);
							
						}
						Iterator<Property> ite2 = subclase.getPropertyIterator();
						while (ite2.hasNext()) {
							Property prop2 = ite2.next();
							if (!c2h.isCollection(prop2) && (c2h.isOneToMany(prop2) || c2h.isManyToOne(prop2))) {
								PersistentClass subclase2 = cfg.getClassMapping(prop2.getType().getName());
								//String name3 = subclase2.getEntityName();
								//result.add(name3);
								
							}
						}
					}
				}else{
					if (!clazz.getIdentifierProperty().isComposite() && prop.equals(clazz.getIdentifierProperty())){ //clave simple
						if (!c2h.isCollection(prop)  && (c2h.isOneToMany(prop) || c2h.isManyToOne(prop))){
							PersistentClass subclase = cfg.getClassMapping(prop.getType().getName());
							String name3 = ControllerUtils.findNameFromEntity(subclase.getEntityName().toString());
							result.add(name3);
						}
					}//clave compuesta*/
					else if(clazz.getIdentifierProperty().isComposite()){
						Iterator propiedades =  c2h.getProperties((Component)clazz.getIdentifier());
						while (propiedades.hasNext()){
							Property pops= (Property) propiedades.next();
							if (!c2h.isCollection(pops) &&(c2h.isOneToMany(pops) || c2h.isManyToOne(pops))){
								PersistentClass subclase = cfg.getClassMapping(pops.getType().getName());
								String nameAux = ControllerUtils.findNameFromEntity(subclase.getEntityName().toString());
									result.add( nameAux);
								
								
							}
						}
						
					}
				}
			}
			return result;
		} catch (Exception e) {
			log.error("error:" + e.getMessage() + e.getCause());
			return null;
		}
	}
	public List<String[]> getMNPk(POJOClass pojo, Configuration cfg, Property propiedad){
		try{
			List<String[]> result = new ArrayList<String[]>();
			//<#-- Obtenemos el nombre de la tabla hijo -->
			String vsRecupera= "";
			PersistentClass clazz=(PersistentClass) pojo.getDecoratedObject();
			
			Collection collection = (Collection)propiedad.getValue();

			ManyToOne manyToOne = (ManyToOne)collection.getElement();
			PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
			String nombreSubclass = ControllerUtils.findNameFromEntity(subclass.getEntityName());
		    if (c2j.isComponent(subclass.getIdentifier())){
		    	Iterator<Property> primRelacionada = c2h.getProperties((Component)subclass.getIdentifier());
		    	while (primRelacionada.hasNext()){
		    		Property camposPrim= primRelacionada.next();
		    		String[] auxiliar= {ControllerUtils.stringDecapitalize(nombreSubclass)+BasicPOJOClass.beanCapitalize(camposPrim.getName()),WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(camposPrim, true, true),true),BasicPOJOClass.beanCapitalize(camposPrim.getName())}; 
		    		result.add( auxiliar) ;
		    	}
		    }else{
		    	String[] auxiliar= {ControllerUtils.stringDecapitalize(nombreSubclass)+BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName()),WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, true),true),BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName())}; 
	    		result.add( auxiliar) ;
		    }
				
			return result;
		}catch(Exception e){
			log.error("Error:" + e.getCause() + e.getMessage());
			return null;
			
		}

	}
	public List<String[]> getPrimaryKeyCreator(POJOClass pojo, Configuration cfg){
		try {
			List<String[]> result =  new ArrayList<String[]>();
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			String getter=null;
			if (c2j.isComponent(clazz.getIdentifierProperty())){
					Iterator<Property> itPrimariaSub= (Iterator<Property>) c2h.getProperties((Component)clazz.getIdentifier());
					while (itPrimariaSub.hasNext()){
						Property prop= itPrimariaSub.next();						
						if (c2h.isOneToMany(prop) || c2h.isManyToOne(prop)){
					
							PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
							if (!c2j.isComponent(subclass.getIdentifier())){
								getter = ControllerUtils.findNameFromEntity(subclass.getEntityName())+"(new "+ ControllerUtils.findNameFromEntity(subclass.getEntityName())+"())";
								String[] strAt= {getter};
								result.add(strAt);
							}else{//subclase compuesta
							Iterator<Property> itPrimariaSubclass= (Iterator<Property>) c2h.getProperties((Component)subclass.getIdentifier());
							while (itPrimariaSubclass.hasNext()){
								Property propSubclas = itPrimariaSubclass.next();
								if (!c2h.isOneToMany(propSubclas)||!c2h.isManyToOne(propSubclas)){
									getter = ControllerUtils.findNameFromEntity(subclass.getEntityName())+"(new "+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"())";
									String[] strAt= {getter};
									 result.add(strAt);
								}else{
									PersistentClass subclaseTwo = cfg.getClassMapping(propSubclas.getType().getName());
								
										getter =ControllerUtils.findNameFromEntity(subclass.getEntityName())+"( new "+ ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"())";
										String[] strAt= {getter};
										  if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
										    		&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){

											  	result.add(strAt);
										  }	  	
									
										
								}
							}	
						}
					}
					
			}}
			
			
			
			return result;
		}catch (Exception e){	
			log.error("Error:" +e.getMessage() + e.getCause());
			return null;
		}
	}
}
