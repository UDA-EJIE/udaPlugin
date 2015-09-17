package com.ejie.uda.exporters.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.BasicPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
/**
 * 
 * Clases principalmente utilizada en la generación de la capa de acceso a base de datos, la cual contiene las funciones específicas para el comportamiento  de la persistencia JDBC
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class DaoUtilsJdbcAux {
	private final static Logger log = Logger.getLogger(DaoUtilsJdbcAux.class);
	private static Cfg2HbmTool c2h = new Cfg2HbmTool();
	private static Cfg2JavaTool c2j = new Cfg2JavaTool();
	WarningSupressorJdbc warSupresor= new WarningSupressorJdbc();
	public DaoUtilsJdbcAux(){
		
	}
	
	public List<String> getInsertFields(POJOClass pojo, Configuration cfg){

		List<String> result=new ArrayList<String>();
		try{
			Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
			int contadorManyToOne = 0;
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			while (itPropiedades.hasNext()){
				Property propiedad = itPropiedades.next();
				if (!c2h.isCollection(propiedad)){
					if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
						contadorManyToOne = contadorManyToOne+1; 
						PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
						String nombreSubclase = ControllerUtils.findNameFromEntity( subclase.getEntityName());
						pojo.importType(pojo.getPackageName()+".model."+ nombreSubclase);
						Iterator<Column> camposOrigen = propiedad.getColumnIterator() ;
						int contador= 0 ;
						if (!c2h.isOneToOne(propiedad)){
						 //las onetoOne no deben participar ni en la insert ni en la update
						  while (camposOrigen.hasNext()){
							 Column orig= camposOrigen.next();
							 contador=contador + 1;
							 int contadoraux = contador;
							 result.add(orig.getName());
						  }
						}
					}else if( clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por más de un campo -->			
						Iterator<Property> primarias = c2h.getProperties((Component)clazz.getIdentifier());
						while (primarias.hasNext()){
							Property prim=primarias.next();
							Iterator<Column> itColumnasPrim=prim.getColumnIterator();
							while (itColumnasPrim.hasNext()){
								Column aux2=itColumnasPrim.next();
								result.add(aux2.getName());
							}
							
						}
					}else if(!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por 1 único campo -->
						Iterator<Column> itAuxCol = propiedad.getColumnIterator();
						while (itAuxCol.hasNext()){
							Column aux2= itAuxCol.next();
							result.add(aux2.getName());
						}
						
					}else{
						Iterator<Column> itAuxCol = propiedad.getColumnIterator();
						while (itAuxCol.hasNext()){
							Column aux2= itAuxCol.next();
							result.add(aux2.getName());
						}
					}
				}
			}	
			
			//return campos;
			return result;
		}catch(Exception e){
			log.error("Error:" + e.getCause() + e.getMessage());
			return result;
			
		}	
	}
	public List<String[]> getInsertValues(POJOClass pojo, Configuration cfg){
		List<String[]> result=new ArrayList<String[]>();
		try{
			Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			while (itPropiedades.hasNext()){
				Property propiedad = itPropiedades.next();
				if (!c2h.isCollection(propiedad)){
					if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
						PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
						String nombreSubclase = ControllerUtils.findNameFromEntity( subclase.getEntityName());
						pojo.importType(pojo.getPackageName()+".model."+ nombreSubclase);
						if (c2j.isComponent(subclase.getIdentifier())){
							Iterator<Property> primRelacionada= c2h.getProperties((Component)subclase.getIdentifier());
							while (primRelacionada.hasNext()){
								Property camposPrim = primRelacionada.next();
								if (!c2h.isOneToOne(camposPrim)){
									String[] auxiliar={pojo.getDeclarationName().toLowerCase()+"." + warSupresor.getGetterSignature(propiedad,pojo)+"().get"+ BasicPOJOClass.beanCapitalize(camposPrim.getName())+"()","1",pojo.getDeclarationName().toLowerCase()+"." + warSupresor.getGetterSignature(propiedad,pojo)+"()","",warSupresor.getGetterSignature(propiedad,pojo)+BasicPOJOClass.beanCapitalize(camposPrim.getName())};
									result.add(auxiliar);
								}	
								
								
							}				
						}else{
							if  (!c2h.isOneToOne(propiedad)){
								String[] auxiliar={ pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(propiedad,pojo)+"().get"+BasicPOJOClass.beanCapitalize(subclase.getIdentifierProperty().getName())+"()","1", pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(propiedad,pojo)+"()","",warSupresor.getGetterSignature(propiedad,pojo)+BasicPOJOClass.beanCapitalize(subclase.getIdentifierProperty().getName())};
								result.add(auxiliar);
						}	
	
						}
					}else if(clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
							//<#-- Primaria compuesta por más de un campo -->	
							  Iterator<Property> primarias = c2h.getProperties((Component)clazz.getIdentifier());
							  while(primarias.hasNext()){
								  Property prim = primarias.next(); 
								  if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
									  PersistentClass subclass = cfg.getClassMapping(prim.getType().getName());
									  String nombreSubclass =   ControllerUtils.findNameFromEntity(subclass.getEntityName());
									  pojo.importType(pojo.getPackageName()+".model."+ nombreSubclass);
									 
									 // <#--  Obtenemos la primaria de la tabla asociada -->
										if (c2j.isComponent(subclass.getIdentifier())){
											Iterator<Property> primRelacionada= c2h.getProperties((Component)subclass.getIdentifier());
											while (primRelacionada.hasNext()){
												  Property primar= primRelacionada.next();
												  if (c2h.isManyToOne(primar) || c2h.isOneToMany(primar)){ //28/02/2011
													  PersistentClass subclassSecondLevel = cfg.getClassMapping(primar.getType().getName());
													  String[] auxiliar={ pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(primar.getName())+"().get"+BasicPOJOClass.beanCapitalize(subclassSecondLevel.getIdentifierProperty().getName())+"()","1",pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"()",pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(primar.getName())+"()",BasicPOJOClass.beanCapitalize(nombreSubclass)+BasicPOJOClass.beanCapitalize(primar.getName())};
													  result.add(auxiliar);
												  }else{
													  String[] auxiliar={ pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+BasicPOJOClass.beanCapitalize(primar.getName())+"()","1",pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"()","",BasicPOJOClass.beanCapitalize(nombreSubclass)+BasicPOJOClass.beanCapitalize(primar.getName())};
													  result.add(auxiliar);
												  }	  
											    
											}//while (primRelacionada.hasNext()){
										}else{   //else if (c2j.isComponent(subclass.getIdentifier())){
											String[] auxiliar={  pojo.getDeclarationName().toLowerCase()+".get"+  BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName())+"()","1",pojo.getDeclarationName().toLowerCase()+".get"+  BasicPOJOClass.beanCapitalize(nombreSubclass)+"()","",BasicPOJOClass.beanCapitalize(nombreSubclass)+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName())};
											result.add(auxiliar);

										} //end else if (c2j.isComponent(subclass.getIdentifier())){
																			
								  }else{ //else  if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
									  
									  String[] auxiliar={ pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(prim,pojo)+"()","0","","","" };
									  result.add(auxiliar);
									 
								  }//end   if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
							  }// while primarias.hasNext()){
							  
						}else if (!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
							//<#-- Primaria compuesta por 1 único campo -->		
							 String[] auxiliar={ pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(propiedad,pojo)+"()","0","","",""};
							 result.add(auxiliar);

						}else{
							 String[] auxiliar={ pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(propiedad,pojo)+"()","0","","",""};
  							result.add(auxiliar);
						
						}//fin else final
			}
				}
				return result;
			}catch(Exception e){
				log.error("Error:" + e.getCause() + e.getMessage());
				return null;
				
			}
	}	
	public List<String> getUpdateFields(POJOClass pojo, Configuration cfg){

		List<String> result=new ArrayList<String>();
		try{
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
			while (itPropiedades.hasNext()){
				Property propiedad = itPropiedades.next();
				if (!c2h.isCollection(propiedad)){
					if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
						Iterator<Column> camposOrigen = propiedad.getColumnIterator() ;
						if (!c2h.isOneToOne(propiedad)){
						 //las onetoOne no deben participar ni en la insert ni en la update
						  while (camposOrigen.hasNext()){
							 Column orig= camposOrigen.next();
							 result.add(orig.getName());
							
						  	}
						}			
							
						}else if (!(clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())) && !(!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty()))){
							Iterator<Column> prop =  propiedad.getColumnIterator();
							while(prop.hasNext()){
								Column aux2 = prop.next();
								 result.add(aux2.getName());
								
							}
						}//fin else final

					}
				}
			return result;
			}catch(Exception e){
				log.error("Error:" + e.getCause() + e.getMessage());
				return null;
				
			}
	}	
	public List<String> getWherePk(POJOClass pojo, Configuration cfg){

		List<String> result=new ArrayList<String>();
		try{
			Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
			String where="";
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			while (itPropiedades.hasNext()){
				Property propiedad = itPropiedades.next();
				if (!c2h.isCollection(propiedad)){
					if(clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
							//<#-- Primaria compuesta por más de un campo -->	
							  Iterator<Property> primarias = c2h.getProperties((Component)clazz.getIdentifier());
							  while(primarias.hasNext()){
								  Property prim = primarias.next(); 
								  Iterator<Column> iterador = prim.getColumnIterator();
								  while (iterador.hasNext()){
									 Column aux2=iterador.next();
									 //where = where + aux2.getName()+ " = ? ";
									 result.add(aux2.getName());
								  }// while (iterador.hasNext()){
								
									
							  }// while primarias.hasNext()){
							  
						}else if (!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
							//<#-- Primaria compuesta por 1 único campo -->		
							  Iterator<Column> itColumns = propiedad.getColumnIterator();
							  while (itColumns.hasNext()){
								  Column aux2	= 	itColumns.next();				  
								  result.add(aux2.getName());
							  }// while (itColumns.hasNext()){
					

						}							
			}
				}
			return result;
			}catch(Exception e){
				log.error("Error:" + e.getCause() + e.getMessage());
				return null;
				
			}
			}
	public List<String[]> camposQueryUpdate(POJOClass pojo, Configuration cfg){
		try{
		
		List<String[]> result=new ArrayList<String[]>();
		List<String[]> resultPrimaria = new ArrayList<String[]>();
		Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
		PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
		while (itPropiedades.hasNext()){
			Property propiedad = itPropiedades.next();
			if (!c2h.isCollection(propiedad)){
				if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
					PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
					String nombreSubclase = ControllerUtils.findNameFromEntity( subclase.getEntityName());
					pojo.importType(pojo.getPackageName()+".model."+ nombreSubclase);
					
					if (c2j.isComponent(subclase.getIdentifier())){
						Iterator<Property> primRelacionada= c2h.getProperties((Component)subclase.getIdentifier());
						while (primRelacionada.hasNext()){
							Property camposPrim = primRelacionada.next();
							if (!c2h.isOneToOne(camposPrim)){
								String[] auxiliar={pojo.getDeclarationName().toLowerCase()+"." + warSupresor.getGetterSignature(propiedad,pojo)+"().get"+BasicPOJOClass.beanCapitalize(camposPrim.getName())+"()","1",pojo.getDeclarationName().toLowerCase()+"." + warSupresor.getGetterSignature(propiedad,pojo)+"()","", warSupresor.getGetterSignature(propiedad,pojo)+BasicPOJOClass.beanCapitalize(camposPrim.getName())};
								result.add(auxiliar);
							}				
							
						}				
					}else{
						if  (!c2h.isOneToOne(propiedad)){
							String[] auxiliar={pojo.getDeclarationName().toLowerCase()+"."+warSupresor.getGetterSignature(propiedad,pojo)+"().get"+BasicPOJOClass.beanCapitalize(subclase.getIdentifierProperty().getName())+"()","1",pojo.getDeclarationName().toLowerCase()+"."+warSupresor.getGetterSignature(propiedad,pojo)+"()","",warSupresor.getGetterSignature(propiedad,pojo)+BasicPOJOClass.beanCapitalize(subclase.getIdentifierProperty().getName())};
				    	   result.add(auxiliar);
					}
					}	
				}else if(clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por más de un campo -->	
						  Iterator<Property> primarias = c2h.getProperties((Component)clazz.getIdentifier());
						  while(primarias.hasNext()){
							  Property prim = primarias.next(); 
							  if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
								  PersistentClass subclass = cfg.getClassMapping(prim.getType().getName());
								  String nombreSubclass =   ControllerUtils.findNameFromEntity(subclass.getEntityName());
								  pojo.importType(pojo.getPackageName()+".model."+ nombreSubclass);
								 // <#--  Obtenemos la primaria de la tabla asociada -->
									if (c2j.isComponent(subclass.getIdentifier())){
										Iterator<Property> primRelacionada= c2h.getProperties((Component)subclass.getIdentifier());
										while (primRelacionada.hasNext()){
											  Property primar= primRelacionada.next();
											  if (c2h.isManyToOne(primar) || c2h.isOneToMany(primar)){ //28/02/2011
												  PersistentClass subclassSecondLevel = cfg.getClassMapping(primar.getType().getName());
												  String[] auxiliar={pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(primar.getName())+"().get"+ BasicPOJOClass.beanCapitalize(subclassSecondLevel.getIdentifierProperty().getName())+"()","1",pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"()",pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(primar.getName())+"()",nombreSubclass+ BasicPOJOClass.beanCapitalize(primar.getName())+ BasicPOJOClass.beanCapitalize(subclassSecondLevel.getIdentifierProperty().getName())};
												  resultPrimaria.add(auxiliar);
											  }else{
												  String[] auxiliar={pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(primar.getName())+"()","1",pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"()",nombreSubclass+ BasicPOJOClass.beanCapitalize(primar.getName())};
												  resultPrimaria.add(auxiliar);
											  }
										
									
										}//while (primRelacionada.hasNext()){
									}else{   //else if (c2j.isComponent(subclass.getIdentifier())){
										String[] auxiliar={pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName())+"()","1",pojo.getDeclarationName().toLowerCase()+".get"+ nombreSubclass+"()","",BasicPOJOClass.beanCapitalize(nombreSubclass)+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName())};
										resultPrimaria.add(auxiliar);
										
									
									} //end else if (c2j.isComponent(subclass.getIdentifier())){
									//<#-- Hay que pasar parametros nulos al resto de columnas, para que pille el sortzailea -->
							  }else{ //else  if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
   								String[] auxiliar={pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(prim,pojo)+"()","0","","",""};
								resultPrimaria.add(auxiliar);
								
							  }//end   if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
						  }// while primarias.hasNext()){
						  
					}else if (!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por 1 único campo -->		
						String[] auxiliar={pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(propiedad,pojo)+"()","0","","",""};
						
						resultPrimaria.add(auxiliar);
						
					}else{
						String[] auxiliar={pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(propiedad,pojo)+"()","0","","",""};
						
						result.add(auxiliar);					
				 	}//fin else final
		}
			}
		//Juntamos primero el result y luegoi la primaria, en ese orden porque el orden de los parametros importa
		if (resultPrimaria.size()>0){
			Iterator<String[]> auxIterador= resultPrimaria.iterator();
			while (auxIterador.hasNext()){
				
				result.add(auxIterador.next());
			}
		}
		return result;
		}catch(Exception e){
			log.error("Error:" + e.getCause() + e.getMessage());
			return null;
			
		}
	}
	public List<String> camposSelectFind(POJOClass pojo, Configuration cfg){
		try{
		
		List<String> result=new ArrayList<String>();
		List<String> resultSelectMany=new ArrayList<String>();
		List<String> resultSelectManyNiv2=new ArrayList<String>();
		Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
		int contadorManyToOne = 1;
		PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
		while (itPropiedades.hasNext()){
			Property propiedad = itPropiedades.next();
			if (!c2h.isCollection(propiedad)){
				if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
					contadorManyToOne = contadorManyToOne+1; 
					PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
					String nombreSubclase = ControllerUtils.findNameFromEntity( subclase.getEntityName());
					pojo.importType(pojo.getPackageName()+".model."+ nombreSubclase);
					//Recuperamos la primaria con el nombre real de la BBDD -->
					Property subproperty = subclase.getIdentifierProperty();						
					Iterator<Column> valoresSubproper=	subproperty.getColumnIterator();
					    while (valoresSubproper.hasNext()){
					    	Column aux2=valoresSubproper.next();
					    	resultSelectMany.add("t"+ contadorManyToOne+"."+aux2.getName() +" "+ nombreSubclase.toUpperCase()+ControllerUtils.findHibernateName(aux2.getName()).toUpperCase());
					    }
							
					//<#-- recuperamos todas las columnas para el mapeo directo de las clases -->					
					 Iterator<Property> camposSubclase=subclase.getPropertyIterator() ;
					 while (camposSubclase.hasNext()){
						 Property auxiliar2=camposSubclase.next();
							if (!c2h.isCollection(auxiliar2) && !c2h.isManyToOne(auxiliar2) && !c2h.isOneToOne(auxiliar2) && !auxiliar2.isComposite() && !c2h.isManyToAny(auxiliar2) && !c2h.isOneToMany(auxiliar2)){
								//	 <#-- hago esto para recuperar el nombre real de la columna en la tabla (Hibernate puede que quite _ -->
									 Iterator<Column> subCampos = auxiliar2.getColumnIterator();
									 while (subCampos.hasNext()){
										 Column aux2=subCampos.next();
										 //resultSelectMany.add("t"+contadorManyToOne+"."+aux2.getName()+" "+nombreSubclase + ControllerUtils.findHibernateName(aux2.getName()).toUpperCase());
										 resultSelectMany.add("t"+contadorManyToOne+"."+aux2.getName()+" "+nombreSubclase.toUpperCase() + ControllerUtils.findHibernateName(aux2.getName()).toUpperCase());
									 }
									 
							}else if (c2h.isManyToOne(auxiliar2) || c2h.isOneToOne(auxiliar2) || auxiliar2.isComposite()){
								//<#-- Tabla ManytoOne hija. Esta tiene otra relacion a otra tabla.
								//Generamos el constructor unicamente utilizando la primaria de los hijos 
								PersistentClass subclassMapeo = cfg.getClassMapping(propiedad.getType().getName());//auxiliar2.getValue().getReferencedEntityName());
								String nombreSubclassMapeo= ControllerUtils.findNameFromEntity(subclassMapeo.getClassName());//nombreSubclassEnteroMapeo.substring(nombreSubclassEnteroMapeo.lastIndexOf(".")+1,nombreSubclassEnteroMapeo.length()) ;
							   	 pojo.importType(pojo.getPackageName()+".model."+ nombreSubclassMapeo);
								 	
							    Iterator<Column> subCamposCol = auxiliar2.getColumnIterator();
							   
							    while (subCamposCol.hasNext()){
							    	Column auxCol=subCamposCol.next();
							    	String campo = "t"+contadorManyToOne+"."+ auxCol.getName()+" " + nombreSubclase.toUpperCase() + ControllerUtils.findHibernateName(auxCol.getName()).toUpperCase();
							    	//Evitar relaciones sobre sí mismo
							    	if (!resultSelectMany.contains(campo)){
							    		//resultSelectManyNiv2.add("t"+contadorManyToOne+"."+ auxCol.getName()+" " + nombreSubclase.toUpperCase() + ControllerUtils.findHibernateName(auxCol.getName()).toUpperCase());
							    		resultSelectManyNiv2.add(campo);
							    	}
								}
																		
								 
							}	
					 }
				
				
		
				}else if(clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por más de un campo -->	
						  Iterator<Property> primarias = c2h.getProperties((Component)clazz.getIdentifier());
						  while(primarias.hasNext()){
							  Property prim = primarias.next(); 
							  Iterator<Column> iterador = prim.getColumnIterator();
							  while (iterador.hasNext()){
								  Column aux2=iterador.next();
								  result.add("t1."+ aux2.getName()+" "+ ControllerUtils.findHibernateName(aux2.getName()).toUpperCase());
							  }// while (iterador.hasNext()){
						  }// while primarias.hasNext()){
						  
					}else if (!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por 1 único campo -->		
						  Iterator<Column> itColumns = propiedad.getColumnIterator();
						  while (itColumns.hasNext()){
							  Column aux2	= 	itColumns.next();		
							  result.add("t1."+ aux2.getName()+" "+ ControllerUtils.findHibernateName(aux2.getName()).toUpperCase());
							 
						  }// while (itColumns.hasNext()){
				

					}else{
						Iterator<Column> prop =  propiedad.getColumnIterator();
						while(prop.hasNext()){
							Column aux2 = prop.next();
							result.add("t1."+ aux2.getName()+" "+ ControllerUtils.findHibernateName(aux2.getName()).toUpperCase());
						
						}
					}//fin else final
						


		}
			}
			if (resultSelectMany.size()>0){
				Iterator<String> auxIterador= resultSelectMany.iterator();
				while (auxIterador.hasNext()){
					result.add(auxIterador.next());
				}
			}
			if (resultSelectManyNiv2.size()>0){
				Iterator<String> auxIterador= resultSelectManyNiv2.iterator();
				while (auxIterador.hasNext()){
					result.add(auxIterador.next());
				}
			}
		return result;
		}catch(Exception e){
			log.error("Error:" + e.getCause() + e.getMessage());
			return null;
			
		}
	}
	public List<String> tablasSelect(POJOClass pojo, Configuration cfg){
		try{
			
			List<String> result=new ArrayList<String>();
			Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
			int contadorManyToOne = 1;
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			result.add( ControllerUtils.findDataBaseName(pojo.getDeclarationName()).toUpperCase() +" t1 ");
			while (itPropiedades.hasNext()){
				Property propiedad = itPropiedades.next();
				if (!c2h.isCollection(propiedad)){
					if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
						contadorManyToOne = contadorManyToOne+1; 
						PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
						String nombreSubclase =ControllerUtils.findNameFromEntity(subclase.getEntityName());
						pojo.importType(pojo.getPackageName()+".model."+ nombreSubclase);
						result.add(ControllerUtils.findDataBaseName(nombreSubclase).toUpperCase() +" t"+contadorManyToOne+" ");
					}
				}
				}
			
			
			return result;
			}catch(Exception e){
				log.error("Error:" + e.getCause() + e.getMessage());
				return null;
				
			}
	} 
	
  public List<String> whereFindPK (POJOClass pojo, Configuration cfg){
		try{
			
			List<String> result = new ArrayList<String>();
			List<String> resultTablaJoin = new ArrayList<String>();
			Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
			int contadorManyToOne = 1;		
			PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
			while (itPropiedades.hasNext()){
				Property propiedad = itPropiedades.next();
				if (!c2h.isCollection(propiedad)){
					if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
						contadorManyToOne = contadorManyToOne+1; 
						PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
						String nombreSubclase = ControllerUtils.findNameFromEntity( subclase.getEntityName());
						pojo.importType(pojo.getPackageName()+".model."+ nombreSubclase);						
						Iterator<Column> camposOrigen = propiedad.getColumnIterator() ;
					
						int contador= 0 ;
						  while (camposOrigen.hasNext()){
							 Column orig= camposOrigen.next();
							 contador=contador + 1;
							 int contadoraux = contador;
					          Iterator itColumnas=subclase.getKey().getColumnIterator();
					          while (itColumnas.hasNext()){
					        	  Column columnasAux= (Column)itColumnas.next();
					        	  if (contadoraux == 1){
					        		  resultTablaJoin.add("t1."+ orig.getName() + "= t"+ contadorManyToOne +"." +columnasAux.getName()+"(+)");
					        		  contadoraux=0;
					        		  
					        	  }else{
					        		   contadoraux=contadoraux - 1 ;	
					        	  }
					          }
						  	
					}
			
					}else if(clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
							//<#-- Primaria compuesta por más de un campo -->	
							  Iterator<Property> primarias = c2h.getProperties((Component)clazz.getIdentifier());
							  while(primarias.hasNext()){
								  Property prim = primarias.next(); 		  
								  Iterator<Column> iterador = prim.getColumnIterator();
								  while (iterador.hasNext()){
									  Column aux2=iterador.next();
									  result.add("t1."+aux2.getName()+" = ?  ");
								  }// while (iterador.hasNext()){
							  }// while primarias.hasNext()){
							  
						}else if (!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
							//<#-- Primaria compuesta por 1 único campo -->		
								  Iterator<Column> itColumns = propiedad.getColumnIterator();
							  while (itColumns.hasNext()){
								  Column aux2	= 	itColumns.next();				  
								 result.add("t1."+ aux2.getName()+" = ?  ");
							  }// while (itColumns.hasNext()){
						}
				}
				}
			if (resultTablaJoin.size()>0){
				Iterator<String> auxIterador= resultTablaJoin.iterator();
				while (auxIterador.hasNext()){
					result.add(auxIterador.next());
				}
			}
			return result;
			}catch(Exception e){
				log.error("Error:" + e.getCause() + e.getMessage());
				return null;
				
			}
 }
	public String rowmapperUpdate(POJOClass pojo, Configuration cfg){
		try{
		
		List<String[]> result=new ArrayList<String[]>();
		Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
		String mapeosParaClasses = "";
		int contadorManyToOne = 1;
		String finalStringJoins="";
		String subMapeo="";
		PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
		while (itPropiedades.hasNext()){
			Property propiedad = itPropiedades.next();
			if (!c2h.isCollection(propiedad)){
				if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
					contadorManyToOne = contadorManyToOne+1; 
					PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
					String nombreSubclase = ControllerUtils.findNameFromEntity( subclase.getEntityName());
					pojo.importType(pojo.getPackageName()+".model."+ nombreSubclase);
					subMapeo="";
					mapeosParaClasses = mapeosParaClasses + ", new "+ nombreSubclase +"(";
					Iterator<Column> camposOrigen = propiedad.getColumnIterator() ;
					
					if (c2j.isComponent(subclase.getIdentifier())){
						Iterator<Property> primRelacionada= c2h.getProperties((Component)subclase.getIdentifier());
						while (primRelacionada.hasNext()){
							Property camposPrim = primRelacionada.next();
							String TypeSimp = warSupresor.getJavaTypeName(camposPrim, true, false);
							Iterator<Column> auxCol = camposPrim.getColumnIterator();
							while (auxCol.hasNext()){
								mapeosParaClasses= mapeosParaClasses + "resultSet.get"+BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\""+nombreSubclase + ControllerUtils.findHibernateName(auxCol.next().getName()).toUpperCase()+"\")";
							}
							
							if (primRelacionada.hasNext()){
								mapeosParaClasses= mapeosParaClasses + ", ";
							}
						}	
					}else{
						
						String TypeSimp = warSupresor.getJavaTypeName(subclase.getIdentifierProperty(), true, false);
						Iterator<Column> auxCol = subclase.getIdentifierProperty().getColumnIterator();
						while (auxCol.hasNext()){
						
							mapeosParaClasses = mapeosParaClasses + "resultSet.get"+BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\""+nombreSubclase + ControllerUtils.findHibernateName(auxCol.next().getName()).toUpperCase()+"\")";
						}
					}	

								
					//<#-- recuperamos todas las columnas para el mapeo directo de las clases -->	
				
					 Iterator<Property> camposSubclase=subclase.getPropertyIterator() ;
					 while (camposSubclase.hasNext()){
						 Property auxiliar2=camposSubclase.next();
								if (!c2h.isCollection(auxiliar2) && !c2h.isManyToOne(auxiliar2) && !c2h.isOneToOne(auxiliar2) && !auxiliar2.isComposite() && !c2h.isManyToAny(auxiliar2) && !c2h.isOneToMany(auxiliar2)){
								//	 <#-- hago esto para recuperar el nombre real de la columna en la tabla (Hibernate puede que quite _ -->
									 Iterator<Column> subCampos = auxiliar2.getColumnIterator();
									 while (subCampos.hasNext()){
										 Column aux2=subCampos.next();
										 String TypeSimp  = warSupresor.getJavaTypeName(auxiliar2, true, false);
										 
											if (mapeosParaClasses.trim().lastIndexOf(",")== mapeosParaClasses.trim().length()-1){
												mapeosParaClasses= mapeosParaClasses + "resultSet.get"+BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false))) +"(\""+nombreSubclase + ControllerUtils.findHibernateName(aux2.getName()).toUpperCase()+"\")";
											}else{
												mapeosParaClasses= mapeosParaClasses + ", resultSet.get"+BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false))) +"(\""+nombreSubclase + ControllerUtils.findHibernateName(aux2.getName()).toUpperCase()+"\")";
											}
					 
									 }
									 
							}else if (c2h.isManyToOne(auxiliar2) || c2h.isOneToOne(auxiliar2) || auxiliar2.isComposite()){
								//<#-- Tabla ManytoOne hija. Esta tiene otra relacion a otra tabla.
								//Generamos el constructor unicamente utilizando la primaria de los hijos 
								PersistentClass subclassMapeo = cfg.getClassMapping(auxiliar2.getType().getName());//auxiliar2.getValue().getReferencedEntityName());
								String nombreSubclassMapeo = ControllerUtils.findNameFromEntity(subclassMapeo.getClassName());
							   	 pojo.importType(pojo.getPackageName()+".model."+ nombreSubclassMapeo);
							   	
							  
							   	 if  (mapeosParaClasses.trim().lastIndexOf(",")== mapeosParaClasses.trim().length()-1){
							   		subMapeo = subMapeo + "new "+ nombreSubclassMapeo + "(";
							   	 }else{
							   		subMapeo = subMapeo + ", new "+ nombreSubclassMapeo + "(";
							   	 }
							   
							    Iterator<Column> subCamposCol = auxiliar2.getColumnIterator();
							    String subMapeoNiv2="";
								 if (c2j.isComponent(subclassMapeo.getIdentifier())){
									 Iterator<Property> primRelacionada= c2h.getProperties((Component)subclassMapeo.getIdentifier());
									 int contadorNivel3 = 0;
									 while (subCamposCol.hasNext()){
										 	Column camposPrim =subCamposCol.next();
										 	contadorNivel3=contadorNivel3 + 1 ;
										 	int contadorNivel3Aux = contadorNivel3 ;
										 	 Iterator<Property> itAuxiliar= c2h.getProperties((Component)subclassMapeo.getIdentifier());
										 	 while (itAuxiliar.hasNext()){
												 Property auxiliar3= itAuxiliar.next();
												 if  (contadorNivel3Aux == 1){
														 String TypeSimp = warSupresor.getJavaTypeName(auxiliar3, true, false);
													 //obtenemos el campo real de la bbdd
											          subMapeo = subMapeo + "resultSet.get"+ BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\""+ nombreSubclase + ControllerUtils.findHibernateName(camposPrim.getName()).toUpperCase()+"\")";
														if (itAuxiliar.hasNext()) {
															 subMapeo = subMapeo +", ";
														}
													  contadorNivel3Aux = 0 ;
												 }else{
													contadorNivel3Aux = contadorNivel3Aux - 1 ;
												 }
										 	 }
											
								
										 }
								 }else{
									 while (subCamposCol.hasNext()){
										 Column auxCol=subCamposCol.next();
										 String TypeSimp = warSupresor.getJavaTypeName(subclassMapeo.getIdentifierProperty(), true, false);
										 subMapeo = subMapeo + "resultSet.get"+ BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\""+ nombreSubclase + ControllerUtils.findHibernateName(auxCol.getName()).toUpperCase()+"\")";
										 if(subCamposCol.hasNext()){
											 subMapeo=subMapeo+", ";
										 }
									
									 }
									
								 }
								 Iterator<Property> resto=subclassMapeo.getPropertyIterator();
								 while (resto.hasNext()){
									 resto.next();
									subMapeo = subMapeo + ", null";
								 }
							
								 subMapeo = subMapeo + ")";
								
							} 
							
					 }// end while (camposSubclase.hasNext()){
				
					 mapeosParaClasses =mapeosParaClasses + subMapeo +")";
		
				}else if(clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por más de un campo -->	
						  Iterator<Property> primarias = c2h.getProperties((Component)clazz.getIdentifier());
						  while(primarias.hasNext()){
							  Property prim = primarias.next(); 
							  if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
								  PersistentClass subclass = cfg.getClassMapping(prim.getType().getName());
								  String nombreSubclass =   ControllerUtils.findNameFromEntity(subclass.getEntityName());
								  pojo.importType(pojo.getPackageName()+".model."+ nombreSubclass);
								 
								  if (finalStringJoins.equals("")){
									  finalStringJoins=finalStringJoins+"new "+nombreSubclass+"(" ;
								  }else{
									  finalStringJoins=finalStringJoins+", new "+nombreSubclass+"(" ;
								  }	
								 // <#--  Obtenemos la primaria de la tabla asociada -->
									if (c2j.isComponent(subclass.getIdentifier())){
										Iterator<Property> primRelacionada= c2h.getProperties((Component)subclass.getIdentifier());
										while (primRelacionada.hasNext()){
											  Property primar= primRelacionada.next();
											if (primar.isComposite() || c2h.isOneToMany(primar) || c2h.isOneToOne(primar) || c2h.isManyToOne(primar) ){
												 int contadorPadre =0 ;
												 String TypeSimp="";
												 String Typejo="";
												 PersistentClass subclassMap = cfg.getClassMapping(primar.getType().getName());
												 String nombreSubClaseMap= ControllerUtils.findNameFromEntity(subclassMap.getEntityName());
												 pojo.importType(pojo.getPackageName()+".model."+ nombreSubClaseMap);
												 if (finalStringJoins.equals("") || finalStringJoins.substring(finalStringJoins.length()-1,finalStringJoins.length()).equals("(")){
													finalStringJoins=finalStringJoins+"new "+ nombreSubClaseMap +"(";
												 }else	{
													 finalStringJoins=finalStringJoins+", new "+ nombreSubClaseMap +"(";
												 }	
												 //****
												 if(primar.isComposite()){
												 Iterator<Property> primRelInferior= c2h.getProperties((Component)primar.getValue());
												 while (primRelInferior.hasNext()){
													 Property porp = primRelInferior.next();
													 contadorPadre = contadorPadre + 1;
													 int  contadorHijo =  contadorPadre ;
													 Iterator<Column> variablePrimariasAux = subclassMap.getIdentifier().getColumnIterator();
													  while(variablePrimariasAux.hasNext()){
														  Column camposHijo= variablePrimariasAux.next();
														 // Column field= listaFieldPrimar.next();
														  if (contadorHijo == 1){
														 
															  TypeSimp = warSupresor.getJavaTypeName(porp, true, false);
															if (TypeSimp.equals("Map")){
																Typejo = camposHijo.getValue().getType().getClass().getName() ;
															}
															if (!TypeSimp.equals("ManyToOne") &&  !TypeSimp.equals("OneToMany")){
																   finalStringJoins=finalStringJoins + "resultSet.get" + BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\"" +ControllerUtils.findHibernateName(camposHijo.getName()).toUpperCase()+"\")"; 
																
																	 if (variablePrimariasAux.hasNext()){
																	      finalStringJoins=finalStringJoins+", ";
																	 }
																 
															}
															
													      }else{
													    	 contadorHijo = contadorHijo - 1 ;	
													      }//  if (contadorHijo == 1)
												     }
												 }
												 //***
												}else{
												 Iterator<Column> listaFieldPrimar=primar.getColumnIterator();
												 while (listaFieldPrimar.hasNext()){
													 Column field= listaFieldPrimar.next();
													 contadorPadre = contadorPadre + 1;
													int  contadorHijo =  contadorPadre ;
													  List<Column>  variablePrimariasAux = subclassMap.getTable().getPrimaryKey().getColumns();
													  Iterator<Column> variablePrimarias=variablePrimariasAux.iterator();
													  while(variablePrimarias.hasNext()){
														  Column camposHijo= variablePrimarias.next();
														  if (contadorHijo == 1){
															  TypeSimp = warSupresor.getJavaTypeName(primar, true, false);
															if (TypeSimp.equals("Map")){
																Typejo = camposHijo.getValue().getType().getClass().getName() ;
															}
															if (!TypeSimp.equals("ManyToOne") &&  !TypeSimp.equals("OneToMany")){
																   finalStringJoins=finalStringJoins + "resultSet.get" + BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\"" +ControllerUtils.findHibernateName(field.getName()).toUpperCase()+"\")"; 
																
																	 if (variablePrimarias.hasNext()){
																	      finalStringJoins=finalStringJoins+", ";
																	 }
																 
															}
															
													      }else{
													    	 contadorHijo = contadorHijo - 1 ;	
													      }//  if (contadorHijo == 1)
												     }//while(variablePrimarias.hasNext()){
													  if (listaFieldPrimar.hasNext() && !finalStringJoins.trim().substring(finalStringJoins.trim().length()-1,finalStringJoins.trim().length()).equals(",") ){
														  finalStringJoins=finalStringJoins+',';
													  }
											}// while (listaFieldPrimar.hasNext()){
												}
												 Iterator<Property> itProperty = subclassMap.getPropertyIterator();
												 while (itProperty.hasNext()){
													 Property restoNulos= itProperty.next();
													 finalStringJoins=finalStringJoins+", null";
												 }

												 finalStringJoins = finalStringJoins+")";
										}//if (primar.isComposite() || c2h.isOneToMany(primar) || c2h.isOneToOne(primar) || c2h.isManyToOne(primar) )
											else{
												String TypeSimp = warSupresor.getJavaTypeName(primar, true, false);
												Iterator<Column> auxiliarCampos =  primar.getColumnIterator();
												while (auxiliarCampos.hasNext()){
													Column aux2 = auxiliarCampos.next();
													if ((finalStringJoins.trim().equals("") && (finalStringJoins.trim().lastIndexOf(",")+1 != finalStringJoins.trim().length()))|| finalStringJoins.trim().substring(finalStringJoins.trim().length()-1,finalStringJoins.trim().length()).equals("(")){ 
													    finalStringJoins=finalStringJoins+"resultSet.get"+BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\"" + ControllerUtils.findHibernateName(aux2.getName()).toUpperCase()+"\")";  
													}else{
														 finalStringJoins=finalStringJoins+", resultSet.get"+BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\"" + ControllerUtils.findHibernateName(aux2.getName()).toUpperCase()+"\")";  
													}
													if(auxiliarCampos.hasNext()){
														finalStringJoins=finalStringJoins+", ";
													}
												}//while (auxiliarCampos.hasNext()){
												
											} //else if (primar.isComposite() || c2h.isOneToMany(primar) || c2h.isOneToOne(primar) || c2h.isManyToOne(primar) )
											
										}//while (primRelacionada.hasNext()){
									}else{   //else if (c2j.isComponent(subclass.getIdentifier())){
										  String TypeSimp= warSupresor.getJavaTypeName(subclass.getIdentifierProperty(), true, false)  ;
										  Iterator<Column> listColumnas = subclass.getIdentifierProperty().getColumnIterator() ;
										  while (listColumnas.hasNext()){
											  Column secuencia=listColumnas.next();
											  finalStringJoins=finalStringJoins+"resultSet.get"+ BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\""+ ControllerUtils.findHibernateName(secuencia.getName()).toUpperCase()+"\")";
										  }
									} //end else if (c2j.isComponent(subclass.getIdentifier())){
									//<#-- Hay que pasar parametros nulos al resto de columnas, para que pille el sortzailea -->
									  Iterator<Property> restoProps= subclass.getPropertyIterator();
									  while(restoProps.hasNext()){
										  Property restoNulos = restoProps.next();
										  finalStringJoins = finalStringJoins + ", null";
									  }
									 finalStringJoins=finalStringJoins + ")" ;
									
							  }else{ 
								   String TypeSimp = warSupresor.getJavaTypeName(prim, true, false);
								   Iterator<Column> listColumnas = prim.getColumnIterator() ;
								   while (listColumnas.hasNext()){
									   Column secuencia =listColumnas.next();
									 if (finalStringJoins.equals("") || finalStringJoins.substring(finalStringJoins.length()-1,finalStringJoins.length()).equals("(")){
										finalStringJoins=finalStringJoins+"resultSet.get"+ BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\""+ ControllerUtils.findHibernateName(secuencia.getName()).toUpperCase()+"\")"; 
									 }else{
										 finalStringJoins=finalStringJoins+", resultSet.get"+ BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(WarningSupressorJdbc.typeConverter(TypeSimp,false)))+"(\""+ ControllerUtils.findHibernateName(secuencia.getName()).toUpperCase()+"\")";  
									 }
								   }
									
							  }//end   if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
						  }// while primarias.hasNext()){
						  
					}else if (!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por 1 único campo -->		
						 
						  Iterator<Column> itColumns = propiedad.getColumnIterator();
						  while (itColumns.hasNext()){
							 Column aux2	= 	itColumns.next();				  
							 String Typejo = aux2.getValue().getType().getReturnedClass().getName() ;
							 String TypeSimp = Typejo.substring(Typejo.lastIndexOf(".")+1,Typejo.length());
							 if (finalStringJoins.equals("")|| finalStringJoins.substring(finalStringJoins.length()-1,finalStringJoins.length()).equals("(")){
									finalStringJoins=finalStringJoins+"resultSet.get"+ BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(warSupresor.getJavaTypeName(propiedad, true,pojo,false)))+"(\"" +ControllerUtils.findHibernateName(aux2.getName()).toUpperCase()+"\")"; 
								}else{
									finalStringJoins=finalStringJoins+", resultSet.get"+ BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(warSupresor.getJavaTypeName(propiedad, true,pojo,false)))+"(\"" +ControllerUtils.findHibernateName(aux2.getName()).toUpperCase()+"\")"; 
								}
						  }// while (itColumns.hasNext()){
				

					}else{
						Iterator<Column> prop =  propiedad.getColumnIterator();
						while(prop.hasNext()){
							Column aux2 = prop.next();
							
							if (finalStringJoins.equals("") || finalStringJoins.substring(finalStringJoins.length()-1,finalStringJoins.length()).equals("(")){
								finalStringJoins=finalStringJoins+"resultSet.get"+ BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(warSupresor.getJavaTypeName(propiedad, true,pojo,false)))+"(\"" +ControllerUtils.findHibernateName(aux2.getName()).toUpperCase()+"\")"; 
							}else{
								finalStringJoins=finalStringJoins+", resultSet.get"+ BasicPOJOClass.beanCapitalize(WarningSupressorJdbc.getIntegerGetter(warSupresor.getJavaTypeName(propiedad, true,pojo,false)))+"(\"" +ControllerUtils.findHibernateName(aux2.getName()).toUpperCase()+"\")"; 
							}
							
						}
					}


		}
			}
		finalStringJoins =finalStringJoins + mapeosParaClasses;
		return finalStringJoins;
		}catch(Exception e){
			log.error("Error:" + e.getCause() + e.getMessage());
			return null;
			
		}
	}

public List<String> commaPrimary(POJOClass pojo, Configuration cfg){
	try{
		
		List<String> result=new ArrayList<String>();
		Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
		String mapeosParaClasses = null;
		int contadorManyToOne = 1;
		String primariaUpdate="";
		PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
		while (itPropiedades.hasNext()){
			Property propiedad = itPropiedades.next();
			if (!c2h.isCollection(propiedad)){
				if(clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por más de un campo -->	
						  Iterator<Property> primarias = c2h.getProperties((Component)clazz.getIdentifier());
						  while(primarias.hasNext()){
							  Property prim = primarias.next(); 
							  if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
								  PersistentClass subclass = cfg.getClassMapping(prim.getType().getName());
								  String nombreSubclass =   ControllerUtils.findNameFromEntity(subclass.getEntityName());
								  pojo.importType(pojo.getPackageName()+".model."+ nombreSubclass);
								 
								 // <#--  Obtenemos la primaria de la tabla asociada -->
									if (c2j.isComponent(subclass.getIdentifier())){
										Iterator<Property> primRelacionada= c2h.getProperties((Component)subclass.getIdentifier());
										while (primRelacionada.hasNext()){
											  Property primar= primRelacionada.next();
											  if (c2h.isManyToOne(primar) || c2h.isOneToMany(primar)){
												  PersistentClass subclassTwo = cfg.getClassMapping(primar.getType().getName());
												  result.add(pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(primar.getName())+"().get"+BasicPOJOClass.beanCapitalize(subclassTwo.getIdentifierProperty().getName())+"()");
											  }else{
												  result.add(pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(primar.getName())+"()" );
											  }  
										    
													}//while (primRelacionada.hasNext()){
									}else{   //else if (c2j.isComponent(subclass.getIdentifier())){

										result.add(pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(nombreSubclass)+"().get"+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName())+"()");
										  
										  
									} //end else if (c2j.isComponent(subclass.getIdentifier())){
									//<#-- Hay que pasar parametros nulos al resto de columnas, para que pille el sortzailea -->
									 
							  }else{ //else  if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
								  result.add(pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(prim,pojo)+"()");
									
							  }//end   if (c2h.isManyToOne(prim) || c2h.isOneToMany(prim)){
						  }// while primarias.hasNext()){
						  
					}else if (!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
						//<#-- Primaria compuesta por 1 único campo -->	
						result.add(pojo.getDeclarationName().toLowerCase()+"."+ warSupresor.getGetterSignature(propiedad,pojo)+"()");
					}

		}
			}
		return result;
		}catch(Exception e){
			log.error("Error:" + e.getCause() + e.getMessage());
			return null;
			
		}
	
}
public List<String> whereDynamicSelect(POJOClass pojo, Configuration cfg){
	try{
		
		List<String> result = new ArrayList<String>();
		Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
		int contadorManyToOne = 1;		
		PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
		while (itPropiedades.hasNext()){
			Property propiedad = itPropiedades.next();
			if (!c2h.isCollection(propiedad)){
				if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
					contadorManyToOne = contadorManyToOne+1; 
					PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
					String nombreSubclase = ControllerUtils.findNameFromEntity( subclase.getEntityName());
					pojo.importType(pojo.getPackageName()+".model."+ nombreSubclase);						
					Iterator<Column> camposOrigen = propiedad.getColumnIterator() ;
				
					int contador= 0 ;
				
					 //las onetoOne no deben participar ni en la insert ni en la update
					  while (camposOrigen.hasNext()){
						 Column orig= camposOrigen.next();
						 contador=contador + 1;
						 int contadoraux = contador;
				          Iterator itColumnas=subclase.getKey().getColumnIterator();
				          while (itColumnas.hasNext()){
				        	  Column columnasAux= (Column)itColumnas.next();
				        	  if (contadoraux == 1){
				        		  result.add("t1."+ orig.getName() + "= t"+ contadorManyToOne +"." +columnasAux.getName()+"(+)");
				        		  contadoraux=0;
				        	  }else{
				        		   contadoraux=contadoraux - 1 ;	
				        	  }
				          
					  	}
				}
		            }
		
						
			}
			}
		
		return result;
		}catch(Exception e){
			log.error("Error:" + e.getCause() + e.getMessage());
			return null;
			
		}
}
public List<String> camposSelectFindDinamyc(POJOClass pojo, Configuration cfg){
	try{
	
	List<String> result=new ArrayList<String>();
	List<String> resultSelectMany=new ArrayList<String>();
	List<String> resultSelectManyNiv2=new ArrayList<String>();
	Iterator<Property> itPropiedades= pojo.getAllPropertiesIterator();
	int contadorManyToOne = 1;
	PersistentClass clazz= (PersistentClass) pojo.getDecoratedObject();
	while (itPropiedades.hasNext()){
		Property propiedad = itPropiedades.next();
		if (!c2h.isCollection(propiedad)){
			if (c2h.isManyToOne(propiedad) || c2h.isOneToOne(propiedad)){
				contadorManyToOne = contadorManyToOne+1; 
				PersistentClass subclase = cfg.getClassMapping(propiedad.getType().getName());
				String nombreSubclase = ControllerUtils.findNameFromEntity( subclase.getEntityName());
				pojo.importType(pojo.getPackageName()+".model."+ nombreSubclase);
				//Recuperamos la primaria con el nombre real de la BBDD -->
				Property subproperty = subclase.getIdentifierProperty();						
				Iterator<Column> valoresSubproper=	subproperty.getColumnIterator();
				    while (valoresSubproper.hasNext()){
				    	Column aux2=valoresSubproper.next();
				    	resultSelectMany.add("t"+ contadorManyToOne+"."+aux2.getName() +" "+ nombreSubclase.toLowerCase().toUpperCase() +ControllerUtils.findHibernateName(aux2.getName()).toUpperCase());
							
				    }
						
				//<#-- recuperamos todas las columnas para el mapeo directo de las clases -->	
			
				 Iterator<Property> camposSubclase=subclase.getPropertyIterator() ;
				 while (camposSubclase.hasNext()){
					 Property auxiliar2=camposSubclase.next();
						if (!c2h.isCollection(auxiliar2) && !c2h.isManyToOne(auxiliar2) && !c2h.isOneToOne(auxiliar2) && !auxiliar2.isComposite() && !c2h.isManyToAny(auxiliar2) && !c2h.isOneToMany(auxiliar2)){
							//	 <#-- hago esto para recuperar el nombre real de la columna en la tabla (Hibernate puede que quite _ -->
								 Iterator<Column> subCampos = auxiliar2.getColumnIterator();
								 while (subCampos.hasNext()){
									 Column aux2=subCampos.next();
									 resultSelectMany.add("t"+contadorManyToOne+"."+aux2.getName()+" "+nombreSubclase.toUpperCase() + ControllerUtils.findHibernateName(aux2.getName()).toUpperCase());
								 }
						 
						}else if (c2h.isManyToOne(auxiliar2) || c2h.isOneToOne(auxiliar2) || auxiliar2.isComposite()){
							//<#-- Tabla ManytoOne hija. Esta tiene otra relacion a otra tabla.
							//Generamos el constructor unicamente utilizando la primaria de los hijos 
							PersistentClass subclassMapeo = cfg.getClassMapping(propiedad.getType().getName());//auxiliar2.getValue().getReferencedEntityName());
							String nombreSubclassMapeo = ControllerUtils.findNameFromEntity(subclassMapeo.getClassName());
						   	pojo.importType(pojo.getPackageName()+".model."+ nombreSubclassMapeo);						   	
						    Iterator<Column> subCamposCol = auxiliar2.getColumnIterator();
						 	while (subCamposCol.hasNext()){
						 		Column auxCol=subCamposCol.next();
						 		String campo = "t"+contadorManyToOne+"."+ auxCol.getName()+" " + nombreSubclase.toUpperCase() + ControllerUtils.findHibernateName(auxCol.getName()).toUpperCase();
						    	//Evitar relaciones sobre sí mismo
						    	if (!resultSelectMany.contains(campo)){
						    		//resultSelectManyNiv2.add("t"+contadorManyToOne+"."+ auxCol.getName()+" " + nombreSubclase.toUpperCase() + ControllerUtils.findHibernateName(auxCol.getName()).toUpperCase());
						    		resultSelectManyNiv2.add(campo);
						    	}
							}
							
						}	
				 }
			
			
	
			}else if(clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
					//<#-- Primaria compuesta por más de un campo -->	
					  Iterator<Property> primarias = c2h.getProperties((Component)clazz.getIdentifier());
					  while(primarias.hasNext()){
						  Property prim = primarias.next(); 
						  Iterator<Column> iterador = prim.getColumnIterator();
						  while (iterador.hasNext()){
							  Column aux2=iterador.next();
							  result.add("t1."+ aux2.getName()+" "+ ControllerUtils.findHibernateName(aux2.getName()).toUpperCase());
							
						  }// while (iterador.hasNext()){
					  }// while primarias.hasNext()){
					  
				}else if (!clazz.getIdentifierProperty().isComposite() && propiedad.equals(clazz.getIdentifierProperty())){
					//<#-- Primaria compuesta por 1 único campo -->		
					  Iterator<Column> itColumns = propiedad.getColumnIterator();
					  while (itColumns.hasNext()){
						  Column aux2	= 	itColumns.next();		
						  result.add("t1."+ aux2.getName()+ " " + ControllerUtils.findHibernateName(propiedad.getName()).toUpperCase());
						 
					  }// while (itColumns.hasNext()){
			

				}else{
					Iterator<Column> prop =  propiedad.getColumnIterator();
					while(prop.hasNext()){
						Column aux2 = prop.next();
						result.add("t1."+ aux2.getName()+" "+ ControllerUtils.findHibernateName(propiedad.getName()).toUpperCase());
					}
				}//fin else final
					


	}
		}
		if (resultSelectMany.size()>0){
			Iterator<String> auxIterador= resultSelectMany.iterator();
			while (auxIterador.hasNext()){
				result.add(auxIterador.next());
			}
		}
		if (resultSelectManyNiv2.size()>0){
			Iterator<String> auxIterador= resultSelectManyNiv2.iterator();
			while (auxIterador.hasNext()){
				result.add(auxIterador.next());
			}
		}
	
	return result;
	}catch(Exception e){
		log.error("Error:" + e.getCause() + e.getMessage());
		return null;
		
	}
}
public List<String> insertMNFields (POJOClass pojo, Configuration cfg,Property propiedad){

	try{
		List<String> result = new ArrayList<String>();
		//<#-- Obtenemos el nombre de la tabla hijo -->
		Collection collection = (Collection)propiedad.getValue();
		ManyToOne manyToOne = (ManyToOne)collection.getElement();
		PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
		Iterator<Column> campoPadre =collection.getKey().getColumnIterator() ;
		String sentenciaInsert="";	
		while (campoPadre.hasNext()){
			Column camposPadre=campoPadre.next();
			result.add(camposPadre.getName());
		 }
	//	<#-- Cuando la tabla hija tiene una primaria compuesta, siempre partiendo que estamos en una relacion m:n , no recupera bien los campos relacionados con el hijo. 
	//	 Por ello, vamos a mirar la primaria de la tabla intermedia, y deshacernos de aquellos parametros que previamente hemos metido -->

		int contador=0 ;	
		int contadoraux2=0 ;
		Iterator<Column> primariasMN = collection.getCollectionTable().getPrimaryKey().getColumnIterator();
		while (primariasMN.hasNext()){
			contador=0;
			Column  camposPrimInter  = primariasMN.next();
			Iterator<Column> columnasExist=collection.getKey().getColumnIterator();
			while (columnasExist.hasNext()){
				Column auxiliarCol=columnasExist.next();
				if (auxiliarCol.getName() == camposPrimInter.getName()){
					contador=1; 				   
				}
				
			}
			if (contador==0){
				result.add(camposPrimInter.getName());
			}
		}
		
		
	
		return result;
	}catch(Exception e){
		log.error("Error:" + e.getCause() + e.getMessage());
		return null;
		
	}
}
public List<String> getMNObject(POJOClass pojo, Configuration cfg, Property propiedad){
	try{
		List<String> result = new ArrayList<String>();
		//<#-- Obtenemos el nombre de la tabla hijo -->
		String vsRecupera= "";
		PersistentClass clazz=(PersistentClass) pojo.getDecoratedObject();
		
		if(clazz.getIdentifierProperty().isComposite()){//clave compuesta
			Iterator<Property> propPrim = c2h.getProperties((Component)clazz.getIdentifier()); 
			while (propPrim.hasNext()){
				Property key= propPrim.next();
				result.add(pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(key.getName()) +"()");
			}
		}else{//clave simple
			result.add(pojo.getDeclarationName().toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(clazz.getIdentifierProperty().getName()) +"()");
		}
		
		Collection collection = (Collection)propiedad.getValue();
		ManyToOne manyToOne = (ManyToOne)collection.getElement();
		PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
		String nombreSubclass = ControllerUtils.findNameFromEntity(subclass.getEntityName());
	    if (c2j.isComponent(subclass.getIdentifier())){
	    	Iterator<Property> primRelacionada = c2h.getProperties((Component)subclass.getIdentifier());
	    	while (primRelacionada.hasNext()){
	    		Property camposPrim= primRelacionada.next();
	    		result.add( nombreSubclass.toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(camposPrim.getName()) + "()") ;
	    	}
	    }else{
	    	result.add( nombreSubclass.toLowerCase()+".get"+ BasicPOJOClass.beanCapitalize(subclass.getIdentifierProperty().getName()) + "()") ;
	    }
			
		return result;
	}catch(Exception e){
		log.error("Error:" + e.getCause() + e.getMessage());
		return null;
		
	}
}
public List<String> deleteMNWhere (POJOClass pojo, Configuration cfg,Property propiedad){

	try{
		List<String> result = new ArrayList<String>();
		//<#-- Obtenemos el nombre de la tabla hijo -->
		Collection collection = (Collection)propiedad.getValue();
		ManyToOne manyToOne = (ManyToOne)collection.getElement();
		PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
		Iterator<Column> campoPadre =collection.getKey().getColumnIterator() ;
		while (campoPadre.hasNext()){
			Column camposPadre=campoPadre.next();
			result.add(camposPadre.getName());
		 }
	//	<#-- Cuando la tabla hija tiene una primaria compuesta, siempre partiendo que estamos en una relacion m:n , no recupera bien los campos relacionados con el hijo. 
	//	 Por ello, vamos a mirar la primaria de la tabla intermedia, y deshacernos de aquellos parametros que previamente hemos metido -->

		int contador=0 ;	
		int contadoraux2=0 ;
		Iterator<Column> primariasMN = collection.getCollectionTable().getPrimaryKey().getColumnIterator();
		while (primariasMN.hasNext()){
			contador=0;
			Column  camposPrimInter  = primariasMN.next();
			Iterator<Column> columnasExist=collection.getKey().getColumnIterator();
			while (columnasExist.hasNext()){
				Column auxiliarCol=columnasExist.next();
				if (auxiliarCol.getName() == camposPrimInter.getName()){
					contador=1; 				   
				}
				
			}
			if (contador==0){
				result.add(camposPrimInter.getName());
			}
		}
		
		return result;
	}catch(Exception e){
		log.error("Error:" + e.getCause() + e.getMessage());
		return null;
		
	}
}
public List<String> wherefindMN(POJOClass pojo, Configuration cfg, Property propiedad){
	try{
		List<String> result = new ArrayList<String>();
		//<#-- Obtenemos el nombre de la tabla hijo -->
		String join= "";
		PersistentClass clazz=(PersistentClass) pojo.getDecoratedObject();
		
				Collection collection = (Collection)propiedad.getValue();
		ManyToOne manyToOne = (ManyToOne)collection.getElement();
		PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
		int contador=0 ;	
		int contadoraux=0 ;	
		int contadoraux2=0 ;
		Iterator<Column> primariasMN = collection.getCollectionTable().getPrimaryKey().getColumnIterator();
		while (primariasMN.hasNext()){
			contador=0;
			Column  camposPrimInter  = primariasMN.next();
			Iterator<Column> columnasExist=collection.getKey().getColumnIterator();
			while (columnasExist.hasNext()){
				Column auxiliarCol=columnasExist.next();
				if (auxiliarCol.getName() == camposPrimInter.getName()){
					contador=1; 				   
				}
			}
			if (contador==0){
				 contadoraux = contadoraux + 1;
				 contadoraux2 = contadoraux ;
			
				Iterator<Column> primSubclass= subclass.getIdentifier().getColumnIterator();
				while (primSubclass.hasNext()){
					Column aux = primSubclass.next();
					if (contadoraux2 == 1){
						result.add("t1."+ camposPrimInter.getName() +" = t2." +  aux.getName());
						contadoraux2=0;
					}else{
						contadoraux2=contadoraux2-1;
					}
					
				}
			  
			}
		}
		
		Iterator<Column> campoPadre =collection.getKey().getColumnIterator() ;
		String sentenciaInsert="";	
		while (campoPadre.hasNext()){
			Column camposPadre=campoPadre.next();
			result.add("t1."+camposPadre.getName()+"=?");
		 }
		
			
		return result;
	}catch(Exception e){
		log.error("Error:" + e.getCause() + e.getMessage());
		return null;
		
	}
}
public List<String> selectFieldsMN(POJOClass pojo, Configuration cfg, Property propiedad){
	try{
		List<String> result = new ArrayList<String>();
		//<#-- Obtenemos el nombre de la tabla hijo -->
		String join= "";
		PersistentClass clazz=(PersistentClass) pojo.getDecoratedObject();
		
		Collection collection = (Collection)propiedad.getValue();

		ManyToOne manyToOne = (ManyToOne)collection.getElement();
		PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
		String camposRelacionados="";


		Iterator<Column> primHijo =subclass.getIdentifierProperty().getValue().getColumnIterator();
		while (primHijo.hasNext()){
			Column camposPrim= primHijo.next();
			String propiedadName = ControllerUtils.findHibernateName(camposPrim.getName());
			result.add("t2."+ BasicPOJOClass.beanCapitalize(camposPrim.getName().toLowerCase()) + " " +  propiedadName);
		}
	 		
		Iterator<Property> columnasHijos = subclass.getPropertyIterator();		
		while (columnasHijos.hasNext()){
			Property columnas = columnasHijos.next();
			if (!c2h.isCollection(columnas)  && !c2h.isManyToOne(columnas)){
				String propiedadName = ControllerUtils.findHibernateName(columnas.getName());
				String valor = ControllerUtils.findDataBaseName(columnas.getName());
				result.add( "t2."+ valor+" "+ propiedadName.toUpperCase());
			}
		}
		return result;
	}catch(Exception e){
		log.error("Error:" + e.getCause() + e.getMessage());
		return null;
		
	}
}
public String findMNChild(POJOClass pojo, Configuration cfg, Property propiedad){
	try{
		String recuperarHijo = "";
		PersistentClass clazz=(PersistentClass) pojo.getDecoratedObject();
		Collection collection = (Collection)propiedad.getValue();
		ManyToOne manyToOne = (ManyToOne)collection.getElement();
		PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
		if(subclass.getIdentifierProperty().isComposite()){
			//subclase clave compuesta
			Iterator<Property> primHijo =c2h.getProperties((Component)subclass.getIdentifier());
			while (primHijo.hasNext()){
				Property auxPrim = primHijo.next() ;
				recuperarHijo = recuperarHijo + "resultSet.get"+WarningSupressorJdbc.typeConverter(warSupresor.getJavaTypeName(auxPrim, true, true),false)+"(\""+ auxPrim.getName().toUpperCase()+"\"), ";
			}
		}else{
			//subclase clave simple
			recuperarHijo = recuperarHijo + "resultSet.get"+WarningSupressorJdbc.typeConverter(warSupresor.getJavaTypeName(subclass.getIdentifierProperty(), true, true),false)+"(\""+ subclass.getIdentifierProperty().getName().toUpperCase()+"\"), ";
		}		
		
		String recuperarHijoaux = "" ;
		String recuperarHijoMany = "" ;
		Iterator<Property> columnaHijos = subclass.getPropertyIterator() ;
		while (columnaHijos.hasNext()){
			Property columnas= columnaHijos.next();
			String typejoSimpleHijoaux="" ;
			String TypeSimpHijoAux="";
			if (!c2h.isCollection(columnas)  && !c2h.isManyToOne(columnas)){
			    recuperarHijoaux = recuperarHijoaux + "resultSet.get"+WarningSupressorJdbc.typeConverter(warSupresor.getJavaTypeName(columnas, true, true),false)+"(\""+ columnas.getName().toUpperCase()+"\"), ";
			}else{
			   // <#-- Si es un ManyToOne rellenamos la clase. Si es un List, en cambio, no lo recuperamos   -->
				  recuperarHijoMany = recuperarHijoMany + ", null";
				  if (!recuperarHijoaux.trim().equals("") && recuperarHijoaux.trim().lastIndexOf(",")+1== recuperarHijoaux.trim().length()){
					//<#-- si es coleccion borramos la ultima coma -->
					 recuperarHijoaux=recuperarHijoaux.substring(0, recuperarHijoaux.length()-2);
					 
				  }
			}
		}
	  recuperarHijo = recuperarHijo + recuperarHijoaux + recuperarHijoMany;
	  return recuperarHijo;
	}catch(Exception e){
		log.error("Error:" + e.getCause() + e.getMessage());
		return null;
		
	}
}
public String getMNEntityName(POJOClass pojo, Configuration cfg, Property propiedad){
	try{
		String result = "";
		//<#-- Obtenemos el nombre de la tabla hijo -->
		String join= "";
		
		PersistentClass clazz=(PersistentClass) pojo.getDecoratedObject();
		
		Collection collection = (Collection)propiedad.getValue();

		ManyToOne manyToOne = (ManyToOne)collection.getElement();
		Table table = collection.getCollectionTable();
		PersistentClass subclass = cfg.getClassMapping(manyToOne.getReferencedEntityName());
		String resultado=getManyToManyMappedBy(cfg,collection);
		result=resultado;
		return  result ;
		
	}catch(Exception e){
		log.error("Error:" + e.getCause() + e.getMessage());
		return null;
		
	}

}


private String getManyToManyMappedBy(Configuration cfg, Collection collection)
  {
     Iterator joinColumnsIt = collection.getKey().getColumnIterator();
    Set joinColumns = new HashSet();
     while (joinColumnsIt.hasNext()) {
      joinColumns.add(joinColumnsIt.next());
    }
     ManyToOne manyToOne = (ManyToOne)collection.getElement();
    PersistentClass pc = cfg.getClassMapping(manyToOne.getReferencedEntityName());
    Iterator properties = pc.getPropertyClosureIterator();
 
    boolean isOtherSide = false;
    String mappedBy = "unresolved";
     while ((!(isOtherSide)) && (properties.hasNext())) {
       Property collectionProperty = (Property)properties.next();
       Value collectionValue = collectionProperty.getValue();
       if ((collectionValue != null) && (collectionValue instanceof Collection)) {
         Collection realCollectionValue = (Collection)collectionValue;
         if ((!(realCollectionValue.isOneToMany())) && 
           (joinColumns.size() == realCollectionValue.getElement().getColumnSpan())) {
           isOtherSide = true;
           Iterator it = realCollectionValue.getElement().getColumnIterator();
           while (it.hasNext()) {
             Object column = it.next();
            if (!(joinColumns.contains(column))) {
              isOtherSide = false;
              break;
           }
          }
           if (isOtherSide) {
            mappedBy = collectionProperty.getName();
          }
        }
      }
    }

    return mappedBy;
   }

}
