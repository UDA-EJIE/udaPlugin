package com.ejie.uda.exporters.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
/**
 * 
 * Clases principalmente utilizada en la generación de la capa de acceso a base de datos, la cual contiene las funciones específicas para el comportamiento  de la persistencia JDBC
 *
 */
@SuppressWarnings({ "unchecked" })
public class DaoGenerationJdbc {
	private static Logger log = Logger.getLogger(DaoGenerationJdbc.class);
	private static Cfg2HbmTool c2h= new Cfg2HbmTool();
	private static Cfg2JavaTool c2j = new Cfg2JavaTool();
	private static WarningSupressorJdbc wsJdbc = new WarningSupressorJdbc();
	
	public DaoGenerationJdbc() {
		
	}
	
	public List<String[]> getPrimaryKeyPojo(POJOClass pojo,Configuration cfg){
		try {
			
			List<String[]> result =  new ArrayList<String[]>();
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			String tipo =null;
			String getter=null;
			if (!c2h.isCollection(pojo.getIdentifierProperty()) && !c2j.isComponent(pojo.getIdentifierProperty())){
				Property prop = pojo.getIdentifierProperty();
				if (!c2h.isOneToMany(prop) && !c2h.isManyToOne(prop)){
					getter = pojo.getDeclarationName().toLowerCase();
					tipo = wsJdbc.getJavaTypeName(prop, true, true);
					String[] strAt= {prop.getName(), tipo,getter};
					result.add(strAt);
				}else{
					PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
					if (!c2j.isComponent(subclass.getIdentifier())){
						tipo = wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, true);
						String[] strAt= {subclass.getIdentifierProperty().getName(), tipo};
						result.add(strAt);
					}else{//subclase compuesta
						Iterator<Property> itPrimariaSubclass= (Iterator<Property>) c2h.getProperties((Component)subclass.getIdentifier());
						while (itPrimariaSubclass.hasNext()){
							Property propSubclas = itPrimariaSubclass.next();
							if (!c2h.isOneToMany(propSubclas)||!c2h.isManyToOne(propSubclas)){
								getter = pojo.getDeclarationName().toLowerCase()+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"()";
								tipo = wsJdbc.getJavaTypeName(propSubclas, true, true);
								String[] strAt= {propSubclas.getName(), tipo,getter};
								result.add(strAt);
							}else{
								PersistentClass subclaseTwo = cfg.getClassMapping(propSubclas.getType().getName());
								if (!c2j.isComponent(subclaseTwo.getIdentifier())){
									getter = pojo.getDeclarationName().toLowerCase()+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
									tipo = wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, true);
									String[] strAt= {subclaseTwo.getIdentifierProperty().getName(), tipo,getter};
									result.add(strAt);
								}else{	
									Iterator<Property> itPrimSubclasTwo= (Iterator<Property>) c2h.getProperties((Component)subclaseTwo.getIdentifier());
									while (itPrimSubclasTwo.hasNext()){
										Property itPropSubClassTwo= itPrimSubclasTwo.next();
										getter = pojo.getDeclarationName().toLowerCase()+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
										tipo = wsJdbc.getJavaTypeName(itPropSubClassTwo, true, true);
										String[] strAt= {itPropSubClassTwo.getName(), tipo,getter};
										result.add(strAt);
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
						getter = pojo.getDeclarationName().toLowerCase();
						tipo = wsJdbc.getJavaTypeName(prop, true, true);
						String[] strAt= {prop.getName(), tipo,getter};
						result.add(strAt);
					}else{
						PersistentClass subclass = cfg.getClassMapping(prop.getType().getName());
						if (!c2j.isComponent(subclass.getIdentifier())){
							getter = pojo.getDeclarationName().toLowerCase()+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"()";
							tipo = wsJdbc.getJavaTypeName(subclass.getIdentifierProperty(), true, true);
							String[] strAt= {subclass.getIdentifierProperty().getName(), tipo,getter};
							result.add(strAt);
						}else{//subclase compuesta
							Iterator<Property> itPrimariaSubclass= (Iterator<Property>) c2h.getProperties((Component)subclass.getIdentifier());
							while (itPrimariaSubclass.hasNext()){
								Property propSubclas = itPrimariaSubclass.next();
								if (!c2h.isOneToMany(propSubclas)||!c2h.isManyToOne(propSubclas)){
									getter = pojo.getDeclarationName().toLowerCase()+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"()";
									tipo = wsJdbc.getJavaTypeName(propSubclas, true, true);
									String[] strAt= {propSubclas.getName(), tipo,getter};
									result.add(strAt);
								}else{
									PersistentClass subclaseTwo = cfg.getClassMapping(propSubclas.getType().getName());
									if (!c2j.isComponent(subclaseTwo.getIdentifier())){
										getter = pojo.getDeclarationName().toLowerCase()+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
										tipo = wsJdbc.getJavaTypeName(subclaseTwo.getIdentifierProperty(), true, true);
										String[] strAt= {subclaseTwo.getIdentifierProperty().getName(), tipo ,getter};
										result.add(strAt);
									}else{	
										Iterator<Property> itPrimSubclasTwo= (Iterator<Property>) c2h.getProperties((Component)subclaseTwo.getIdentifier());
										while (itPrimSubclasTwo.hasNext()){
											getter = pojo.getDeclarationName().toLowerCase()+".get"+ControllerUtils.findNameFromEntity(subclass.getEntityName())+"().get"+ControllerUtils.findNameFromEntity(subclaseTwo.getEntityName())+"()";
											Property itPropSubClassTwo= itPrimSubclasTwo.next();
											tipo = wsJdbc.getJavaTypeName(itPropSubClassTwo, true, true);
											String[] strAt= {itPropSubClassTwo.getName(), tipo,getter};
											result.add(strAt);
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
}
