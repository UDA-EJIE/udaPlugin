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

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.cfg.reveng.JDBCToHibernateTypeHelper;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.BasicPOJOClass;
import org.hibernate.tool.hbm2x.pojo.ImportContext;
import org.hibernate.tool.hbm2x.pojo.NoopImportContext;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.hbm2x.visitor.JavaTypeFromValueVisitor;
import org.hibernate.util.StringHelper;
/**
 * 
 * Clase la cual contiene funciones genéricas de todas las capas de la persistencia JDBC
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class WarningSupressorJdbc {
	private final static Logger log = Logger.getLogger(PojoUtils.class);
	private static Cfg2JavaTool c2j = new Cfg2JavaTool();

	public WarningSupressorJdbc() {}
	

	public String getGetterSignature(Property p,POJOClass pojo) {
	     String prefix = (typeConverter(getJavaTypeName(p, false,false),false).equals("boolean")) ? "is" : "get";
	     return prefix + BasicPOJOClass.beanCapitalize(p.getName());
	}
	
	public static String typeConverter(String type,boolean generateImports) {

		try {
			String typeAux = "";
			if (generateImports) {
				if (type.toUpperCase().endsWith("BYTE")
						|| type.toUpperCase().endsWith("LONG")
						|| type.toUpperCase().endsWith("SHORT")) {
					typeAux = "Long";
				} else if (type.toUpperCase().endsWith("CHARACTER")
						|| type.toUpperCase().endsWith("STRING")) {
					typeAux = "String";
				} else if (type.toUpperCase().endsWith("DATE") || type.toUpperCase().endsWith("TIMESTAMP")) {
					typeAux = "java.util.Date";
				} else if (type.toUpperCase().endsWith("BOOLEAN")) {
					typeAux = "Boolean";
				} else if (type.toUpperCase().endsWith("INT")) {
					typeAux = "Integer";
				} else if (type.toUpperCase().endsWith("FLOAT")) {
					typeAux = "Float";
				} else if (type.toUpperCase().endsWith("JAVA.MATH.BIGDECIMAL")) {
					typeAux = "java.math.BigDecimal";					
				} else if (type.toUpperCase().endsWith("JAVA.UTIL.SET")) {
					typeAux = "java.util.List";
				} else if (type.toUpperCase().endsWith("JAVA.IO.SERIALIZABLE")) {
					typeAux = "Object";
				} else {
					typeAux = type;
				}
			} else {
				if (type.toUpperCase().endsWith("BYTE")
						|| type.toUpperCase().endsWith("LONG")
						|| type.toUpperCase().endsWith("SHORT")) {
					typeAux = "Long";
				} else if (type.toUpperCase().endsWith("CHARACTER")
						|| type.toUpperCase().endsWith("STRING")) {
					typeAux = "String";
				} else if (type.toUpperCase().endsWith("DATE") || type.toUpperCase().endsWith("TIMESTAMP")) {
					typeAux = "Date";
				} else if (type.toUpperCase().endsWith("BOOLEAN")) {
					typeAux = "Boolean";
				} else if (type.toUpperCase().endsWith("INT")) {
					typeAux = "Integer";
				} else if (type.toUpperCase().endsWith("FLOAT")) {
					typeAux = "Float";
				} else if (type.toUpperCase().contains("JAVA.MATH.BIGDECIMAL")) {
					typeAux = "BigDecimal";
				} else if (type.toUpperCase().contains("CLOB")) {
					typeAux = "Clob";
				} else if (type.toUpperCase().contains("BLOB")) {
					typeAux = "Blob";
				} else if (type.toUpperCase().contains("JAVA.UTIL.SET")) {
					typeAux = "List";
				} else if (type.toUpperCase().endsWith("SERIALIZABLE")) {
					typeAux = "Object";
				} else {
					typeAux = type;
				}
			}
			return typeAux;
		} catch (Exception e) {
			log.error("Error:" + e.getMessage());
			return null;
		}
	}

	public static String getIntegerGetter(String type){
		try {

			String typeAux = "";
				if (type.toUpperCase().endsWith("INTEGER")){
					typeAux = "Int";
				} else {
					typeAux = type;
				}
			return typeAux;
		} catch (Exception e) {
			log.error("Error:" + e.getMessage());
			return null;
		}
	}

	public String getJavaTypeName(Property p, boolean useGenerics,boolean generateImports) {
		return getJavaTypeName(p, useGenerics, new NoopImportContext(),generateImports);
	}

	public String getGenericCollectionDeclaration(Collection collection,
			boolean preferRawTypeNames, ImportContext importContext,boolean generateImports) {

		Value element = collection.getElement();
		String elementType =null;
		if (generateImports){
		 elementType = importContext
				.importType(typeConverter(getJavaTypeName(element,
						preferRawTypeNames,generateImports),generateImports));
		} else{
			 elementType = typeConverter(getJavaTypeName(element,
						preferRawTypeNames,generateImports),generateImports);
		}
		String genericDecl = elementType;
		if (collection.isIndexed()) {
			IndexedCollection idxCol = (IndexedCollection) collection;
			if (!(idxCol.isList())) {
				Value idxElement = idxCol.getIndex();
				String indexType = null;
				if (generateImports){
					indexType = importContext
						.importType(typeConverter(getJavaTypeName(idxElement,
								preferRawTypeNames,generateImports),generateImports));
				}else{
					indexType = typeConverter(getJavaTypeName(idxElement,
							preferRawTypeNames,generateImports),generateImports);
				}
				genericDecl = indexType + "," + elementType;
			}
		}
		String decl = "<" + genericDecl + ">";
		return decl;
	}

	public String getJavaTypeName(Property p, boolean useGenerics,
			ImportContext importContext,boolean generateImports) {
		String overrideType = c2j.getMetaAsString(p, "property-type");
		if (!(StringHelper.isEmpty(overrideType))) {
			String importType = null;
			if (generateImports){
				 importType = importContext
						.importType(typeConverter(overrideType,generateImports));
			}else{
				 importType = typeConverter(overrideType,generateImports);
			}
			if ((useGenerics) && (importType.indexOf("<") < 0)
					&& (p.getValue() instanceof Collection)) {
				String decl = getGenericCollectionDeclaration(
						(Collection) p.getValue(), true, importContext,generateImports);
				return importType + decl;
			}
			if (importType.toUpperCase().endsWith("BOOLEAN")){
				Iterator<Column> col = p.getColumnIterator();
				while (col.hasNext()){
					Column columna= col.next();
					String valor = JDBCToHibernateTypeHelper.getJDBCTypeName(columna.getSqlTypeCode().intValue());
					if (valor.equals("BIT")){
						importType="Integer";
					}
				}
			}	
			return importType;
		}

		String rawType = getRawTypeName(p, useGenerics, true, importContext,generateImports);		
		if (rawType == null) {
			throw new IllegalStateException(
					"getJavaTypeName *must* return a value");
		}
		String tipoImport=typeConverter(rawType,generateImports);
		if (tipoImport.toUpperCase().endsWith("BOOLEAN")){
			Iterator<Column> kaka = p.getColumnIterator();
			while (kaka.hasNext()){
				Column columna= kaka.next();
				String valor = JDBCToHibernateTypeHelper.getJDBCTypeName(columna.getSqlTypeCode().intValue());
				if (valor.equals("BIT")){
					tipoImport="Integer";
				}
			}
		}	
		return importContext.importType(tipoImport);
	}

	private String getRawTypeName(Property p, boolean useGenerics,
			boolean preferRawTypeNames, ImportContext importContext,boolean generateImports) {
		Value value = p.getValue();
		try {
			if (value instanceof Array) {
				Array a = (Array) value;

				if (a.isPrimitiveArray()) {
					return typeConverter(toName(value.getType()
							.getReturnedClass()),generateImports);
				}
				if (a.getElementClassName() != null) {
					return a.getElementClassName() + "[]";
				}
				String tipo =getJavaTypeName(a.getElement(),
						preferRawTypeNames,generateImports);
				
				return typeConverter(tipo,generateImports) + "[]";
			}

			if (value instanceof Component) {
				Component component = (Component) value;
				if (component.isDynamic())
					return "java.util.Map";
				return typeConverter(component.getComponentClassName(),generateImports);
			}

			if ((useGenerics) && (value instanceof Collection)) {
				String decl = getGenericCollectionDeclaration(
						(Collection) value, preferRawTypeNames, importContext,generateImports);
				return typeConverter(getJavaTypeName(value, preferRawTypeNames,generateImports),generateImports)
						+ decl;
			}
			
			return typeConverter(getJavaTypeName(value, preferRawTypeNames,generateImports),generateImports);
		} catch (Exception e) {
			String msg = "Could not resolve type without exception for " + p
					+ " Value: " + value;
			if ((value != null) && (value.isSimpleValue())) {
				String typename = ((SimpleValue) value).getTypeName();
				log.warn(msg + ". Falling back to typename: " + typename);
				return typeConverter(typename,generateImports);
			}
			return null;
		}
	}

	private String getJavaTypeName(Value value, boolean preferRawTypeNames,boolean generateImports) {
		if ( value.accept(new JavaTypeFromValueVisitor()).toString().toUpperCase().endsWith("SERIALIZABLE")){
			return ((String) value.accept(new JavaTypeFromValueVisitor()));	
		}
		return typeConverter(((String) value.accept(new JavaTypeFromValueVisitor())),generateImports);
	}

	private String toName(Class c) {
		if (c.isArray()) {
			Class a = c.getComponentType();
			return a.getName() + "[]";
		}
		return c.getName();
	}
	
}