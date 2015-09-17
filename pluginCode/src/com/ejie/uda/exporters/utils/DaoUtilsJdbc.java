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
 * Clases principalmente utilizada en la generación de la capa de acceso a base de datos, la cual contiene las funciones específicas para el comportamiento  de la persistencia JDBC
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused", "finally" })
public class DaoUtilsJdbc {
	private static Logger log = Logger.getLogger(DaoUtilsJdbc.class);
	private static Cfg2HbmTool c2h = new Cfg2HbmTool();
	private static Cfg2JavaTool c2j = new Cfg2JavaTool();
	private  static WarningSupressorJdbc wsJdbc = new WarningSupressorJdbc();

	public DaoUtilsJdbc() {}

	public static List<List<String>> getDesglosePropiedadesSpringJdbc(POJOClass element,Configuration cfg){
		List<List<String>> lista=new ArrayList();
		int i =1;
		try{  
			PersistentClass clazz= (PersistentClass) element.getDecoratedObject();
			Iterator<Property> it= element.getAllPropertiesIterator();
			while (it.hasNext()){
				Property propiedad= (Property) it.next();
				if (!c2h.isCollection(propiedad)){
					if (c2h.isOneToOne(propiedad) || c2h.isManyToOne(propiedad)){
						i=i+1;
						String nameProp=propiedad.getName();
						PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());//.getEntityName();
						if (!c2j.isComponent(subclase.getIdentifier())){
							//hijo con clave simple
							Iterator itColumns=subclase.getIdentifier().getColumnIterator();
							while (itColumns.hasNext()){
								//Obtenemos el nombre real de la base de datos
								Column colOneToOne= (Column) itColumns.next(); 
								String nombreColumna="t"+i+"."+ colOneToOne.getName();
								List<String> listaAuxiliar= new ArrayList<String>(3);
								String getterProp="";
								getterProp = "get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"().get"+BasicPOJOClass.beanCapitalize(subclase.getIdentifierProperty().getName())+"()";
								listaAuxiliar.add(0,getterProp);
								listaAuxiliar.add(1,nameProp);
								listaAuxiliar.add(2,nombreColumna);
								String otros = ".get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"()";

								listaAuxiliar.add(3,otros );
								//miramos que no sea blob, clob o lob para el filtro
								if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("CLOB") 
										&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("BLOB")
										&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("OBJECT")
										&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("LOB")){
									if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("STRING") ){
										listaAuxiliar.add(4,"1" );
									}else{
										listaAuxiliar.add(4,"0" );
									}
									listaAuxiliar.add(5,"" );
									lista.add(listaAuxiliar);
								}
							}	

						}else{
							//subclase con clave compuesta
							Iterator itPrimariaSub= (Iterator) c2h.getProperties((Component) subclase.getIdentifier());
							while (itPrimariaSub.hasNext()){
								Property propiedadComp = (Property) itPrimariaSub.next();
								String namePropi=propiedadComp.getName();
								Iterator<Column> itColumns=propiedadComp.getColumnIterator();
								while (itColumns.hasNext()){
									//Obtenemos el nombre real de la base de datos
									Column colSubcla= (Column) itColumns.next(); 
									String nombreColumna="t"+i+"."+ colSubcla.getName();
									List<String> listaAuxiliar= new ArrayList<String>(3);
									String getterProp="";
									getterProp =  "get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"().get"+BasicPOJOClass.beanCapitalize(namePropi)+"()";
									listaAuxiliar.add(0,getterProp);
									listaAuxiliar.add(1,namePropi);
									listaAuxiliar.add(2,nombreColumna);
									String otros =  ".get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"()";
									listaAuxiliar.add(3,otros);
									if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("CLOB") 
											&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("BLOB")
											&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("OBJECT")
											&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("LOB")){
										if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("STRING") ){
											listaAuxiliar.add(4,"1" );
										}else{
											listaAuxiliar.add(4,"0" );
										}
										listaAuxiliar.add(5,"" );
										lista.add(listaAuxiliar);
									}
								}	
							}//END while (itPrimariaSuc.hasNext()){
						}
						//Despues de la primaria tengo que añadir los campos hijos con un iterador
						Iterator<Property> iterRestoCampos = subclase.getPropertyIterator();
						int contadorPropiedad=0;
						while (iterRestoCampos.hasNext()){
							Property propiedadCampos = (Property) iterRestoCampos.next();
							String namePropi=propiedadCampos.getName();

							if (propiedadCampos.isComposite() && (c2h.isManyToOne(propiedadCampos)|| c2h.isOneToOne(propiedadCampos))){
								contadorPropiedad = contadorPropiedad+1; 
							}else{
								contadorPropiedad = 0;
							}
							Iterator<Column> itColumns=propiedadCampos.getColumnIterator();
							while (itColumns.hasNext()){
								//Obtenemos el nombre real de la base de datos
								Column colSubcla= (Column) itColumns.next(); 
								String nombreColumna="t"+i+"."+ colSubcla.getName();
								List<String> listaAuxiliar= new ArrayList<String>(3);
								String getterProp="";

								if (c2h.isOneToOne(propiedadCampos)||c2h.isManyToOne(propiedadCampos)){
									contadorPropiedad=contadorPropiedad+1;
									//tercera relacion 
									PersistentClass subclaseDos = cfg.getClassMapping(propiedadCampos.getType().getName());//.getEntityName();
									if (!c2j.isComponent(subclaseDos.getIdentifier())){
										getterProp = "get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"().get"+BasicPOJOClass.beanCapitalize(namePropi)+"().get"+BasicPOJOClass.beanCapitalize(subclaseDos.getIdentifierProperty().getName())+"()";
										listaAuxiliar.add(0,getterProp);
										listaAuxiliar.add(1,namePropi);
										listaAuxiliar.add(2,nombreColumna);
										String otros = ".get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"()";
										listaAuxiliar.add(3,otros);
										if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseDos.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
												&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseDos.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
												&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseDos.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
												&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseDos.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){
											if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclaseDos.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("STRING") ){
												listaAuxiliar.add(4,"1" );
											}else{
												listaAuxiliar.add(4,"0" );
											}
											listaAuxiliar.add(5,"get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"().get"+BasicPOJOClass.beanCapitalize(namePropi)+"()"); 
											lista.add(listaAuxiliar);
										}
										
									}else{
										int contadorAuxiliares=0;
										Iterator<Property> itPrimariaSubClass= (Iterator) c2h.getProperties((Component) subclaseDos.getIdentifier());
										while (itPrimariaSubClass.hasNext()){
											Property propiedadCompuesta = (Property) itPrimariaSubClass.next();
											contadorAuxiliares=contadorAuxiliares+1;
											if (contadorAuxiliares == contadorPropiedad){
												getterProp = "get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"().get"+BasicPOJOClass.beanCapitalize(namePropi)+"().get"+BasicPOJOClass.beanCapitalize(propiedadCompuesta.getName())+"()";
												listaAuxiliar.add(0,getterProp);
												listaAuxiliar.add(1,namePropi);
												listaAuxiliar.add(2,nombreColumna);
												String otros = ".get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"()";
												listaAuxiliar.add(3,otros);
												if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCompuesta, true, false),false).toUpperCase().endsWith("CLOB") 
														&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCompuesta, true, false),false).toUpperCase().endsWith("BLOB")
														&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCompuesta, true, false),false).toUpperCase().endsWith("OBJECT")
														&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCompuesta, true, false),false).toUpperCase().endsWith("LOB")){
													if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCompuesta, true, false),false).toUpperCase().endsWith("STRING") ){
														listaAuxiliar.add(4,"1" );
													}else{
														listaAuxiliar.add(4,"0" );
													}
													listaAuxiliar.add(5,"get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"().get"+BasicPOJOClass.beanCapitalize(namePropi)+"()");
													lista.add(listaAuxiliar);
												}  
											}  
										} 		
									}
									
								}else{
									getterProp = "get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"().get"+BasicPOJOClass.beanCapitalize(namePropi)+"()";
									listaAuxiliar.add(0,getterProp);
									listaAuxiliar.add(1,namePropi);
									listaAuxiliar.add(2,nombreColumna);
									String otros = ".get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"()";
									listaAuxiliar.add(3,otros);
									if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCampos, true, false),false).toUpperCase().endsWith("CLOB") 
											&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCampos, true, false),false).toUpperCase().endsWith("BLOB")
											&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCampos, true, false),false).toUpperCase().endsWith("OBJECT")
											&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCampos, true, false),false).toUpperCase().endsWith("LOB")){
										if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadCampos, true, false),false).toUpperCase().endsWith("STRING") ){
											listaAuxiliar.add(4,"1" );
										}else{
											listaAuxiliar.add(4,"0" );
										}
										listaAuxiliar.add(5,"");
										lista.add(listaAuxiliar);
									}  
								}	  
							}	
						}//end while añadir los campos hijos con un iterador
						
					}else if (clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//clave primaria Compuesta por mas de un campo
						Iterator<Property> itPrimaria= (Iterator) c2h.getProperties((Component) clazz.getIdentifierProperty().getValue());
						while (itPrimaria.hasNext()){
							Property propiedadComp = (Property) itPrimaria.next();
							//Miramos si es una relacion
							if (c2h.isManyToOne(propiedadComp) || c2h.isOneToMany(propiedadComp)) {
								PersistentClass subclase = cfg.getClassMapping(propiedadComp.getType().getName());
								String getterProp="";
								getterProp = "get"+BasicPOJOClass.beanCapitalize(propiedadComp.getName())+"()";
								String nameProp=propiedadComp.getName();									   
								if (!c2j.isComponent(subclase.getIdentifier()) && !c2h.isCollection(subclase.getIdentifierProperty())){
									Iterator<Column> it2=subclase.getIdentifierProperty().getColumnIterator();
									while (it2.hasNext()){
										Column columna = (Column) it2.next();
										String nombreColumna="t1."+ columna.getName();
										List<String> listaAuxiliar= new ArrayList<String>(3);
										String getterPropiedad="";
										getterProp = "get"+BasicPOJOClass.beanCapitalize(propiedadComp.getName())+"().get"+BasicPOJOClass.beanCapitalize(subclase.getIdentifierProperty().getName())+"()";
										listaAuxiliar.add(0,getterProp);
										listaAuxiliar.add(1,subclase.getIdentifierProperty().getName());

										//obtenemos la columna de base de datos
										listaAuxiliar.add(2,nombreColumna);
										String otros = " ";
										listaAuxiliar.add(3,otros);
										if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("CLOB") 
												&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("BLOB")
												&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("OBJECT")
												&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("LOB")){
											if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(subclase.getIdentifierProperty(), true, false),false).toUpperCase().endsWith("STRING") ){
												listaAuxiliar.add(4,"1" );
											}else{
												listaAuxiliar.add(4,"0" );
											}
											listaAuxiliar.add(5,"" );
											lista.add(listaAuxiliar);
										}
									}
									
								}else{
									if (c2j.isComponent(subclase.getIdentifier())){
										//primaria compuesta por mas de un campo
										Iterator<Property> propPrimarias = c2h.getProperties((Component) subclase.getIdentifier());
										while (propPrimarias.hasNext()){
											Property proper =  propPrimarias.next();
											if (c2h.isOneToOne(proper) || c2h.isManyToOne(proper)){
												String namePropComp=proper.getName();
												PersistentClass subclaseCompues = cfg.getClassMapping(proper.getType().getName());//.getEntityName();
												if (!c2j.isComponent(subclaseCompues.getIdentifier())){
													Iterator<Column> itProper= subclaseCompues.getIdentifier().getColumnIterator();
													while (itProper.hasNext()){
														Column columna= (Column) itProper.next();
														String nombreColumna="t1."+ columna.getName();
														List<String> listaAuxiliar= new ArrayList<String>(3);
														String nombreHiber = ControllerUtils.findHibernateName(columna.getName());
														getterProp = "get"+BasicPOJOClass.beanCapitalize(propiedadComp.getName())+"().get"+BasicPOJOClass.beanCapitalize(proper.getName()) +"().get"+BasicPOJOClass.beanCapitalize(nombreHiber)+"()";
														listaAuxiliar.add(0,getterProp);
														listaAuxiliar.add(1,proper.getName());
														listaAuxiliar.add(1,nombreColumna);

														//obtenemos la columna de base de datos
														String otros = " ";
														listaAuxiliar.add(3,otros);
														if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("CLOB") 
																&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("BLOB")
																&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("OBJECT")
																&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("LOB")){
															if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("STRING") ){
																listaAuxiliar.add(4,"1" );
															}else{
																listaAuxiliar.add(4,"0" );
															}
															listaAuxiliar.add(5,"" );
															lista.add(listaAuxiliar);
														}
													}
												}
												
											}else{	
												Iterator<Column> itProper= proper.getColumnIterator();
												while (itProper.hasNext()){
													Column columna= (Column) itProper.next();
													String nombreColumna="t1."+ columna.getName();
													List<String> listaAuxiliar= new ArrayList<String>(3);
													String nombreHiber = ControllerUtils.findHibernateName(columna.getName());
													getterProp = "get"+BasicPOJOClass.beanCapitalize(propiedadComp.getName())+"().get"+BasicPOJOClass.beanCapitalize(nombreHiber)+"()";
													listaAuxiliar.add(0,getterProp);
													listaAuxiliar.add(1,proper.getName());
													listaAuxiliar.add(1,nombreColumna);

													//obtenemos la columna de base de datos
													String otros = " ";
													listaAuxiliar.add(3,otros);
													if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("CLOB") 
															&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("BLOB")
															&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("OBJECT")
															&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("LOB")){
														if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(proper, true, false),false).toUpperCase().endsWith("STRING") ){
															listaAuxiliar.add(4,"1" );
														}else{
															listaAuxiliar.add(4,"0" );
														}
														listaAuxiliar.add(5,"" );
														lista.add(listaAuxiliar);
													}
												} 
											}
										}//while propPrimarias.hasNext()	  
									}
								}

							}else{ // clave compuesta sin relaciones
								String getterProp="";
								getterProp ="get"+BasicPOJOClass.beanCapitalize(propiedadComp.getName())+"()";
								String nameProp=propiedadComp.getName();
								Iterator<Column> it2=propiedadComp.getColumnIterator();
								while (it2.hasNext()){
									Column columna = (Column) it2.next();
									String nombreColumna="t1."+ columna.getName();
									List<String> listaAuxiliar= new ArrayList<String>(3);
									listaAuxiliar.add(0,getterProp);
									listaAuxiliar.add(1,nameProp);
									listaAuxiliar.add(2,nombreColumna);
									String otros = " ";

									listaAuxiliar.add(3,otros);
									if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("CLOB") 
											&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("BLOB")
											&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("OBJECT")
											&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("LOB")){
										if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedadComp, true, false),false).toUpperCase().endsWith("STRING") ){
											listaAuxiliar.add(4,"1" );
										}else{
											listaAuxiliar.add(4,"0" );
										}
										listaAuxiliar.add(5,"" );
										lista.add(listaAuxiliar);
									}
								}
							}
						}

					}else{// campos normales
						String nameProp=propiedad.getName();
						String getterProp="";
						getterProp ="get"+BasicPOJOClass.beanCapitalize(propiedad.getName())+"()";
						Iterator<Column> it2=propiedad.getColumnIterator();
						while (it2.hasNext()){
							Column columna = (Column) it2.next();
							String nombreColumna="t1."+ columna.getName();
							List<String> listaAuxiliar= new ArrayList<String>(3);
							listaAuxiliar.add(0,getterProp);
							listaAuxiliar.add(1,nameProp);
							listaAuxiliar.add(2,nombreColumna);

							String otros = " ";
							listaAuxiliar.add(3,otros);
							if(!WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("CLOB") 
									&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("BLOB")
									&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("OBJECT")
									&& !WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("LOB")){
								if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(propiedad, true, false),false).toUpperCase().endsWith("STRING") ){
									listaAuxiliar.add(4,"1" );
								}else{
									listaAuxiliar.add(4,"0" );
								}
								listaAuxiliar.add(5,"" );
								lista.add(listaAuxiliar);
							}  
						}//end while
					}
				}
			}
			return lista;
		}catch(Exception e){
			log.error( "Error:"+e.getMessage()+ e.getCause()+e.getStackTrace().toString());
			return null;
		}	
	}
	
	public static List<List<String>> getFieldManyToManySpring(POJOClass pojo,Configuration cfg, Property propiedad){
		try{
			List<List<String>> lista=new ArrayList();

			if(c2h.isCollection(propiedad) && c2h.isManyToMany(propiedad)){
				Collection col=(Collection) propiedad.getValue();
				ManyToOne manytomany= (ManyToOne) col.getElement();
				PersistentClass subclaseMany = cfg.getClassMapping(manytomany.getReferencedEntityName());
				//obtengo la primaria de la subclase
				if (!c2j.isComponent(subclaseMany.getIdentifier()) && !c2h.isCollection(subclaseMany.getIdentifierProperty())){
					Iterator<Column> itProp=subclaseMany.getIdentifierProperty().getColumnIterator();
					while (itProp.hasNext()){
						Column columna= (Column) itProp.next();
						List<String> listaAuxiliar= new ArrayList<String>(3);
						String getterPropiedad="";
						getterPropiedad ="get" + BasicPOJOClass.beanCapitalize(subclaseMany.getIdentifierProperty().getName())+ "()";
						listaAuxiliar.add(0,getterPropiedad);
						listaAuxiliar.add(1,subclaseMany.getIdentifierProperty().getName());
						//obtenemos la columna de base de datos
						listaAuxiliar.add(2,columna.getName());
						lista.add(listaAuxiliar);
					}	  

				}else{
					if (c2j.isComponent(subclaseMany.getIdentifier())){
						//primaria compuesta por mas de un campo
						Iterator<Property> propPrimarias = c2h.getProperties((Component) subclaseMany.getIdentifier());
						while (propPrimarias.hasNext()){
							Property proper =  propPrimarias.next();
							Iterator<Column> itProper= proper.getColumnIterator();
							while (itProper.hasNext()){
								Column columna= (Column) itProper.next();
								List<String> listaAuxiliar= new ArrayList<String>(3);
								String getterPropiedad="";
								getterPropiedad ="get"+BasicPOJOClass.beanCapitalize(proper.getName())+"()";
								listaAuxiliar.add(0,getterPropiedad);
								listaAuxiliar.add(1,proper.getName());
								//obtenemos la columna de base de datos
								listaAuxiliar.add(2,columna.getName());
								lista.add(listaAuxiliar);
							}
						}		  
					}
				}	
				//Obtenemos los campos restantes
				Iterator<Property> restoCampos = subclaseMany.getPropertyIterator();
				while (restoCampos.hasNext()){
					Property proper =  restoCampos.next();
					if (!c2h.isCollection(proper)){
						Iterator<Column> itProper= proper.getColumnIterator();
						while (itProper.hasNext()){
							Column columna= (Column) itProper.next();
							List<String> listaAuxiliar= new ArrayList<String>(3);
							String getterPropiedad="";
							getterPropiedad = "get"+BasicPOJOClass.beanCapitalize(proper.getName())+"()";

							listaAuxiliar.add(0,getterPropiedad);
							listaAuxiliar.add(1,proper.getName());
							//obtenemos la columna de base de datos
							listaAuxiliar.add(2,columna.getName());
							lista.add(listaAuxiliar);
						}
					}
				}
			}
			return lista;
		}catch(Exception e){
			log.error("error:"+e.getMessage()+e.getCause());
			return null;
		}
	}
	
	public static String getDatabaseNameFromProperty(POJOClass pojo, String name){
		try{
			boolean found = false;
			String nombreCampo=null;
			Iterator<Property> camposPojo=pojo.getAllPropertiesIterator();
			while (camposPojo.hasNext() && !found){
				Property propiedad =camposPojo.next();
				//miramos si es parte de la calve primaria, y a su vez si esta es compuesta o simple
				if (!c2h.isCollection(propiedad) && !c2j.isComponent(propiedad)&& !c2h.isOneToOne(propiedad) && c2h.isManyToOne(propiedad)  ){
					//campo simple de primer nivel. Podemos comparar los nombre directamente
					if (name.toLowerCase().equals(propiedad.getName().toLowerCase())){
						found = true;
						//obtenemos el nombre de la base de datos
						nombreCampo = findDbName(propiedad,"t1");
					} 
				}
			}
			return nombreCampo;
		}catch(Exception e){
			log.error("error:"+e.getMessage()+e.getCause());
			return null;
		}
	}
	
	public static String findDbName(Property propiedad, String tabla){
		try{
			String nombreCol=null;
			Iterator<Column> columnasProp = propiedad.getColumnIterator();
			while (columnasProp.hasNext()){
				Column columna= columnasProp.next();
				nombreCol = tabla + "." + columna.getName();
			}
			return nombreCol;
		}	 catch(Exception e){
			log.error("error:"+e.getMessage()+e.getCause());
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
					if (WarningSupressorJdbc.typeConverter(wsJdbc.getJavaTypeName(prop, true, false),false).toUpperCase().equals(fieldType)){
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

}