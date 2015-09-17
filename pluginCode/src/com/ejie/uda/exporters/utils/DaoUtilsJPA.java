package com.ejie.uda.exporters.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
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
 * Clase principalmente utilizada en la generación de la capa de acceso a base de datos, la cual contiene las funciones específicas para el comportamiento  de la persistencia JPA
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused", "finally"})
public class DaoUtilsJPA {
	private static Logger log = Logger.getLogger(DaoUtilsJPA.class);
	private static Cfg2HbmTool c2h= new Cfg2HbmTool();
	private static Cfg2JavaTool c2j = new Cfg2JavaTool();
	private  static WarningSupressorJPA wsJpa = new WarningSupressorJPA();

	public DaoUtilsJPA() {
	}
	public static List<String[]> getFirsLevelFields(POJOClass pojo, Boolean desglose) {
		List<String[]> result = new ArrayList<String[]>();
		Iterator<Property> ite = pojo.getAllPropertiesIterator();
		PersistentClass clazz = (PersistentClass) pojo.getDecoratedObject();
		String name1 = clazz.getEntityName().substring(
				clazz.getEntityName().lastIndexOf(".") + 1,
				clazz.getEntityName().length());
		while (ite.hasNext()) {
			Property prop = ite.next();
			if (desglose){
				if (!c2h.isCollection(prop) && !c2h.isManyToOne(prop) && !c2h.isOneToMany(prop) && !c2h.isOneToOne(prop)) {
						String  getterPropiedad=null;
						getterPropiedad = "get"+BasicPOJOClass.beanCapitalize(prop.getName())+"()";
						String name1Aux = name1.toLowerCase()+ "Aux";
						if (clazz.getIdentifierProperty().isComposite() && prop.equals(clazz.getIdentifierProperty())){
							 Iterator<Property> itPrimariaAux= (Iterator) c2h.getProperties((Component) clazz.getIdentifierProperty().getValue());
							  String compositionId = "" ;
							  while (itPrimariaAux.hasNext()){
								  Property propiedadComp = (Property) itPrimariaAux.next();
								  compositionId= compositionId + " && "+pojo.getDeclarationName().toLowerCase() + ".getId().get"+BasicPOJOClass.beanCapitalize(propiedadComp.getName())+"()!=null";
							  }
							  if(!WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("CLOB") 
							    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("BLOB")
							    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("LOB")
							    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("OBJECT")){
								  if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("STRING")){
									  String[] strAr = { name1Aux, prop.getName() ,getterPropiedad,"1","",compositionId};
									  result.add(strAr);
								  }else{
									  String[] strAr = { name1Aux, prop.getName() ,getterPropiedad,"0","",compositionId};
									  result.add(strAr);
								  }
								 
							  }	  
						}else{
							
						  if(!WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("CLOB") 
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("BLOB")
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("LOB")
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("OBJECT")){
							  if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("STRING")){
								  String[] strAr = { name1Aux, prop.getName() ,getterPropiedad,"1","",""};
								  result.add(strAr);
							  }else{
								  String[] strAr = { name1Aux, prop.getName() ,getterPropiedad,"0","",""};
								  result.add(strAr);
							  }
							 
						  }	  
				}
				}
			}	else{ // primaria desglosada en dos campos
				 if (!c2h.isCollection(prop) && !c2h.isManyToOne(prop) && !c2h.isOneToMany(prop) && !c2h.isOneToOne(prop)) {
					 if (clazz.getIdentifierProperty().isComposite() && prop.equals(clazz.getIdentifierProperty())){
						  Iterator<Property> itPrimariaAux= (Iterator) c2h.getProperties((Component) clazz.getIdentifierProperty().getValue());
						  String compositionId = "" ;
						  while (itPrimariaAux.hasNext()){
							  Property propiedadComp = (Property) itPrimariaAux.next();
							  compositionId= compositionId + " && "+pojo.getDeclarationName().toLowerCase() + ".getId().get"+BasicPOJOClass.beanCapitalize(propiedadComp.getName())+"()!=null";
						  }
						 
						  Iterator<Property> itPrimaria= (Iterator) c2h.getProperties((Component) clazz.getIdentifierProperty().getValue());
						  while (itPrimaria.hasNext()){
						    	Property propiedadComp = (Property) itPrimaria.next();
						    	String  getterPropiedad=null;
								getterPropiedad = "get"+BasicPOJOClass.beanCapitalize(prop.getName())+"()";
								String name1Aux = name1.toLowerCase()+ "Aux";
								 if(!WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("CLOB") 
								    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("BLOB")
								    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("LOB")
								    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("OBJECT")){
									  if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("STRING")){
										  String[] strAr = { name1Aux,"id",getterPropiedad,"1", propiedadComp.getName(),compositionId };
										  result.add(strAr);
									  }else{
											  String[] strAr = { name1Aux,"id",getterPropiedad,"0", propiedadComp.getName(),compositionId};
										 	  result.add(strAr);
									  }
									 
								  }
						    	
						  }
					 }else{
						String  getterPropiedad=null;
						getterPropiedad = "get"+BasicPOJOClass.beanCapitalize(prop.getName())+"()";
						String name1Aux = name1.toLowerCase()+ "Aux";
						
						  if(!WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("CLOB") 
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("BLOB")
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("LOB")
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("OBJECT")){
							  if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().endsWith("STRING")){
								  String[] strAr = { name1Aux, prop.getName() ,getterPropiedad,"1","",""};
								  result.add(strAr);
							  }else{
								  String[] strAr = { name1Aux, prop.getName() ,getterPropiedad,"0","",""};
								  result.add(strAr);
							  }
							 
						  }
					 }			  
				
			}
			}
		}
		return result;
	}


	public static List<String[]> getSecondLevelFields(POJOClass pojo, Configuration cfg, Boolean desglose) {		
		try{			
			List<String[]> result = new ArrayList<String[]>();
			Iterator<Property> ite = pojo.getAllPropertiesIterator();
			PersistentClass clazz = (PersistentClass) pojo.getDecoratedObject();
			String name1 = clazz.getEntityName().substring(clazz.getEntityName().lastIndexOf(".") + 1, clazz.getEntityName().length());
			while (ite.hasNext()) {
				Property prop = ite.next();
				if (!c2h.isCollection(prop)	&& (c2h.isOneToOne(prop) || c2h.isManyToOne(prop))) {					
					PersistentClass subclase = cfg.getClassMapping(prop.getType().getName());
					String name2 = subclase.getEntityName().substring(subclase.getEntityName().lastIndexOf(".") + 1, subclase.getEntityName().length());
					// Esto lleva todo menos la clave primaria
					String nameAux= name1.substring(0,1).toLowerCase()+name1.substring(1,name1.length())+ "_" + name2.substring(0,1).toLowerCase()+name2.substring(1,name2.length());
					Iterator<Property> ite2 =  subclase.getPropertyIterator();
					
					if (c2j.isComponent(subclase.getIdentifier())) {
						if (desglose){
							  Iterator<Property> itPrimariaAux= (Iterator) c2h.getProperties((Component) subclase.getIdentifier());
							  String compositionId = "" ;
							  String nombreSubclase = ControllerUtils.findNameFromEntity( subclase.getEntityName());
								
							  while (itPrimariaAux.hasNext()){
								  Property propiedadComp = (Property) itPrimariaAux.next();
								  compositionId= compositionId + " && "+pojo.getDeclarationName().toLowerCase() + ".get"+BasicPOJOClass.beanCapitalize(nombreSubclase)+"().getId().get"+BasicPOJOClass.beanCapitalize(propiedadComp.getName())+"()!=null";
							  }
							String  getterPropiedad="getId()";
							String[] map = { name1, name2, "id",getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()),"0","",compositionId };
							result.add(map);
							
								  
						}else{
									  Iterator<Property> itPrimaria= (Iterator) c2h.getProperties((Component) subclase.getIdentifier());
									  while (itPrimaria.hasNext()){
									    	Property propiedadComp = (Property) itPrimaria.next();
									    	String  getterPropiedad="getId()";
											String[] map = { name1, name2, "id",getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()),"0",propiedadComp.getName(),"" };
											result.add(map);
											
									    	
									  }
								 }
					}else {
						String   getterPropiedad = "get"+BasicPOJOClass.beanCapitalize(subclase.getIdentifierProperty().getName())+"()";
						
						 if(!WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){
							 if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("STRING")){ 
								 String[] map = { name1, name2, subclase.getIdentifierProperty().getName(),getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()),"1","",""};
								 result.add(map);
							 }else{
								 String[] map = { name1, name2, subclase.getIdentifierProperty().getName(),getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()),"0","",""};
								 result.add(map);
							 }
							
						 }
					}
					while (ite2.hasNext()) {
						Property prop2 = ite2.next();
						if (!c2h.isCollection(prop2) && !c2h.isManyToOne(prop2) && !c2h.isOneToMany(prop2)	&& !c2h.isOneToOne(prop2)) {
							//if ((!prop2.equals(subclase.getIdentifierProperty()) || (prop2.equals(subclase.getIdentifierProperty()))){
								String  getterPropiedad=null;
								getterPropiedad = "get"+BasicPOJOClass.beanCapitalize(prop2.getName())+"()";
								//String[] map = { name1,	name2, prop2.getName(),getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()) };
								 if(!WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop2, true, false),false).toUpperCase().endsWith("CLOB") 
								    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop2, true, false),false).toUpperCase().endsWith("BLOB")
								    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop2, true, false),false).toUpperCase().endsWith("OBJECT")
								    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop2, true, false),false).toUpperCase().endsWith("LOB")){
									 
									 if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop2, true, false),false).toUpperCase().endsWith("STRING")){ 
										 String[] map = { name1,	name2, prop2.getName(),getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()) ,"1","",""};
										 result.add(map);
									 }else{
										 String[] map = { name1,	name2, prop2.getName(),getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()) ,"0","",""};
										 result.add(map);
									 }
									
							     }
						}
					}
				}
			}
			return result;
		}catch(Exception e){
			log.error("error:" + e.getMessage() + e.getCause());
			return null;
		}		
	}


	public static List<String[]> getThirdLevelFields(POJOClass pojo, Configuration cfg, Boolean desglose) {
		try{			
			List<String[]> result = new ArrayList<String[]>();
			Iterator<Property> ite = pojo.getAllPropertiesIterator();
			PersistentClass clazz = (PersistentClass) pojo.getDecoratedObject();
			String name1 = clazz.getEntityName().substring(clazz.getEntityName().lastIndexOf(".") + 1, clazz.getEntityName().length());
			while (ite.hasNext()) {
				Property prop = ite.next();
				if (!c2h.isCollection(prop)	&& (c2h.isOneToOne(prop) || c2h.isManyToOne(prop))) {					
					PersistentClass subclase = cfg.getClassMapping(prop.getType().getName());
					String name2 = subclase.getEntityName().substring(subclase.getEntityName().lastIndexOf(".") + 1, subclase.getEntityName().length());
					
					// Esto lleva todo menos la clave primaria
					Iterator<Property> ite2 = subclase.getPropertyIterator();
					while (ite2.hasNext()) {
						Property prop2 = ite2.next();
						if (!c2h.isCollection(prop2)	&& (c2h.isOneToOne(prop2) || c2h.isManyToOne(prop2))) {
							PersistentClass subclase2 = cfg.getClassMapping(prop2.getType().getName());
							if(subclase2.getEntityName()!=clazz.getEntityName()){
								String name3 = subclase2.getEntityName().substring(subclase2.getEntityName().lastIndexOf(".") + 1, subclase2.getEntityName().length());
								// Esto lleva todo menos la clave primaria
								String nameAux= name2.substring(0,1).toLowerCase()+name2.substring(1,name2.length())+ "_" + name3.substring(0,1).toLowerCase()+name3.substring(1,name3.length());
								Iterator<Property> ite3 = subclase2.getPropertyIterator();
								if (c2j.isComponent(subclase2.getIdentifier())) {
									if (desglose){
										 Iterator<Property> itPrimariaAux= (Iterator) c2h.getProperties((Component) subclase2.getIdentifier());
										  String compositionId = "" ;
										  String nombreSubclase = ControllerUtils.findNameFromEntity( subclase2.getEntityName());
										  String nombreSubclaseAnt = ControllerUtils.findNameFromEntity( subclase.getEntityName());
											
										  while (itPrimariaAux.hasNext()){
											  Property propiedadComp = (Property) itPrimariaAux.next();
											  compositionId= compositionId + " && "+pojo.getDeclarationName().toLowerCase() + ".get"+BasicPOJOClass.beanCapitalize(nombreSubclaseAnt)+"().get"+BasicPOJOClass.beanCapitalize(nombreSubclase)+"().getId().get"+BasicPOJOClass.beanCapitalize(propiedadComp.getName())+"()!=null";
										  }
										
										String   getterPropiedad = "getId()";
										String[] map = { name1, prop.getName(), name3,"id",getterPropiedad ,nameAux,"0","",compositionId};
										result.add(map);
									}else{
										  Iterator<Property> itPrimaria= (Iterator) c2h.getProperties((Component) subclase2.getIdentifier());
										  while (itPrimaria.hasNext()){
										    	Property propiedadComp = (Property) itPrimaria.next();
										    	String  getterPropiedad="getId()";
												String[] map = { name1, prop.getName(), name3,"id",getterPropiedad ,nameAux,"0",propiedadComp.getName(),""};
												result.add(map);
										  }
									}
									 
								}else{
									String  getterPropiedad = "get"+BasicPOJOClass.beanCapitalize(subclase2.getIdentifierProperty().getName())+"()";
									//String[] map = { name1, prop.getName(), name3, subclase2.getIdentifierProperty().getName(),getterPropiedad,nameAux};
									 if(!WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase2.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
									    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase2.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
									    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase2.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
									    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase2.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){
										 if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase2.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("STRING") ){ 
											 String[] map = { name1, prop.getName(), name3, subclase2.getIdentifierProperty().getName(),getterPropiedad,nameAux,"1","",""};
											 if(name1.equalsIgnoreCase(prop.getName())){
													//Relación recíproca
													map[5] = map[1] + "_" + map[5];
											 }
											 result.add(map);
										 }else{
											 String[] map = { name1, prop.getName(), name3, subclase2.getIdentifierProperty().getName(),getterPropiedad,nameAux,"0","",""};
											 if(name1.equalsIgnoreCase(prop.getName())){
													//Relación recíproca
													map[5] = map[1] + "_" + map[5];
											 }
											 result.add(map);
										 }

										 
									 }	 
								}
								while (ite3.hasNext()) {
									Property prop3 = ite3.next();
									if (!c2h.isCollection(prop3) && !c2h.isManyToOne(prop3) && !c2h.isOneToMany(prop3)	&& !c2h.isOneToOne(prop3)) {
										
											String  getterPropiedad=null;
											getterPropiedad = "get"+BasicPOJOClass.beanCapitalize(prop3.getName())+"()"; 
											//String[] map = { name1,	prop.getName(), name3, prop3.getName() ,getterPropiedad,nameAux};
											 if(!WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop3, true, false),false).toUpperCase().endsWith("CLOB") 
											    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop3, true, false),false).toUpperCase().endsWith("BLOB")
											    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop3, true, false),false).toUpperCase().endsWith("OBJECT")
											    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop3, true, false),false).toUpperCase().endsWith("LOB")){
												 if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop3, true, false),false).toUpperCase().endsWith("STRING")  ){ 
													 String[] map = { name1,	prop.getName(), name3, prop3.getName() ,getterPropiedad,nameAux,"1","",""};
													 if(name1.equalsIgnoreCase(prop.getName())){
															//Relación recíproca
															map[5] = map[1] + "_" + map[5];
													 }
													 result.add(map);
												 }else{
													 String[] map = { name1,	prop.getName(), name3, prop3.getName() ,getterPropiedad,nameAux,"0","",""};
													 if(name1.equalsIgnoreCase(prop.getName())){
															//Relación recíproca
															map[5] = map[1] + "_" + map[5];
													 }
													 result.add(map);
												 }
											 }	
									}
								}
							}
						}
					}
				}
			}
			return result;
		}catch(Exception e){
			log.error("error:" + e.getMessage() + e.getCause());
			return null;
		}	
	}
	

	public static List<String[]> getFromParams(POJOClass pojo, Configuration cfg) {
		try {
			List<String[]> result = new ArrayList<String[]>();
			Iterator<Property> ite = pojo.getAllPropertiesIterator();
			PersistentClass clazz = (PersistentClass) pojo.getDecoratedObject();
			String name1 = pojo.getDeclarationName();//clazz.getEntityName().substring(clazz.getEntityName().lastIndexOf(".") + 1, clazz.getEntityName().length());
			//String[] item = { name1, name1 + "1" };
			//result.add(item);
			while (ite.hasNext()) {
				Property prop = ite.next();
				if (!c2h.isCollection(prop)	&& (c2h.isOneToOne(prop) || c2h.isManyToOne(prop))) {
					PersistentClass subclase = cfg.getClassMapping(prop.getType().getName());
					// Esto lleva todo menos la clave primaria
					Iterator<Property> ite2 = subclase.getPropertyIterator();
					String name2 = subclase.getEntityName().substring(subclase.getEntityName().lastIndexOf(".") + 1, subclase.getEntityName().length());
					name2= name2.substring(0,1).toLowerCase() + name2.substring(1,name2.length());
					String name2Aux = name1.substring(0,1).toLowerCase() + name1.substring(1,name1.length())+ "_" + name2.substring(0,1).toLowerCase() + name2.substring(1,name2.length());
					String relation = name1.toLowerCase() + "Aux";
					String[] item2 = {name1,name2, name2Aux, relation,prop.getName() };
					result.add(item2);
					
					while (ite2.hasNext()) {
						Property prop2 = ite2.next();
						if (!c2h.isCollection(prop2) && (c2h.isOneToOne(prop2) || c2h.isManyToOne(prop2))) {
							PersistentClass subclase2 = cfg.getClassMapping(prop2.getType().getName());
							if (subclase2 != null && subclase2.getEntityName()!=clazz.getEntityName()) {
								String name3 = subclase2.getEntityName().substring(subclase2.getEntityName().lastIndexOf(".") + 1, subclase2.getEntityName().length());
								String relation3 = name2Aux;
								name3= name3.substring(0,1).toLowerCase() + name3.substring(1,name3.length());
								String name3Aux = name2.substring(0,1).toLowerCase() + name2.substring(1,name2.length())+ "_" + name3.substring(0,1).toLowerCase() + name3.substring(1,name3.length());
								String[] item3 = { name2, name3,name3Aux,relation3,prop2.getName() };
								if(name1.equalsIgnoreCase(name2)){
									//Relación recíproca
									item3[2] = item3[0] + "_" + item3[2];
								}
								result.add(item3);
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

	public static String getFieldNameFromType(POJOClass pojo, Configuration cfg,String fieldType){
		String resultado="";
		try{
		Iterator<Property> propiedades =pojo.getAllPropertiesIterator();
		while (propiedades.hasNext()){
			Property prop= propiedades.next();
				if (c2h.isCollection(prop) && c2h.isManyToMany(prop)){
				Collection col=(Collection) prop.getValue();
				ManyToOne manytomany= (ManyToOne) col.getElement();
				PersistentClass subclaseMany = cfg.getClassMapping(manytomany.getReferencedEntityName());	
				if (ControllerUtils.findNameFromEntity(subclaseMany.getEntityName()).equals(fieldType)){
					 resultado = prop.getName();
				}
			}else{
				if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(prop, true, false),false).toUpperCase().equals(fieldType)){
					resultado = prop.getName();
				}
			}
		}
		}catch(Exception e){
			e.getStackTrace();
		}finally{
			return resultado;
		}
		
	}
	public static List<String[]> getSecondLevelFieldsPK(POJOClass pojo, Configuration cfg, Boolean desglose) {		
		try{			
			List<String[]> result = new ArrayList<String[]>();
			Iterator<Property> ite = pojo.getAllPropertiesIterator();
			PersistentClass clazz = (PersistentClass) pojo.getDecoratedObject();
			String name1 = clazz.getEntityName().substring(clazz.getEntityName().lastIndexOf(".") + 1, clazz.getEntityName().length());
			while (ite.hasNext()) {
				Property prop = ite.next();
				if (!c2h.isCollection(prop)	&& (c2h.isOneToOne(prop) || c2h.isManyToOne(prop))) {					
					PersistentClass subclase = cfg.getClassMapping(prop.getType().getName());
					String name2 = subclase.getEntityName().substring(subclase.getEntityName().lastIndexOf(".") + 1, subclase.getEntityName().length());
					// Esto lleva todo menos la clave primaria
					String nameAux= name1.substring(0,1).toLowerCase()+name1.substring(1,name1.length())+ "_" + name2.substring(0,1).toLowerCase()+name2.substring(1,name2.length());
					Iterator<Property> ite2 =  subclase.getPropertyIterator();
					
					if (c2j.isComponent(subclase.getIdentifier())) {
						if (desglose){
							String  getterPropiedad="getId()";
							String[] map = { name1, name2, "id",getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()),"0","" };
							result.add(map);
						}else{
									  Iterator<Property> itPrimaria= (Iterator) c2h.getProperties((Component) subclase.getIdentifier());
									  while (itPrimaria.hasNext()){
									    	Property propiedadComp = (Property) itPrimaria.next();
									    	String  getterPropiedad="getId()";
											String[] map = { name1, name2, "id",getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()),"0",propiedadComp.getName() };
											result.add(map);
											
									    	
									  }
								 }
					}else {
						String   getterPropiedad = "get"+BasicPOJOClass.beanCapitalize(subclase.getIdentifierProperty().getName())+"()";
						
						 if(!WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
						    		&& !WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){
							 if (WarningSupressorJPA.typeConverter(wsJpa.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("STRING")){ 
								 String[] map = { name1, name2, subclase.getIdentifierProperty().getName(),getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()),"1",""};
								 result.add(map);
							 }else{
								 String[] map = { name1, name2, subclase.getIdentifierProperty().getName(),getterPropiedad,nameAux,BasicPOJOClass.beanCapitalize(prop.getName()),"0",""};
								 result.add(map);
							 }
							
						 }
					}
			
				}
			}
			return result;
		}catch(Exception e){
			log.error("error:" + e.getMessage() + e.getCause());
			return null;
		}		
	}


}
