package com.ejie.uda.exporters.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.cfg.reveng.JDBCToHibernateTypeHelper;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.AnnotationBuilder;
import org.hibernate.tool.hbm2x.pojo.BasicPOJOClass;
import org.hibernate.tool.hbm2x.pojo.ImportContext;
import org.hibernate.tool.hbm2x.pojo.NoopImportContext;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.hbm2x.visitor.JavaTypeFromValueVisitor;
import org.hibernate.util.StringHelper;
/**
 * 
 * Clase la cual contiene funciones genéricas de todas las capas de la persistencia JPA
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class WarningSupressorJPA {
	private final static Logger log = Logger.getLogger(PojoUtils.class);
	private static Cfg2JavaTool c2j = new Cfg2JavaTool();
//	private PojoUtils pojoutil= new PojoUtils();

	public WarningSupressorJPA() {

	}

	public String getGetterSignature(Property p) {
		String prefix = (typeConverter(getJavaTypeName(p, false, false), false)
				.equals("boolean")) ? "is" : "get";
		return prefix + BasicPOJOClass.beanCapitalize(p.getName());
	}

	public String generateLobAnnotation(Property field, POJOClass element) {
		try {
			String tipo = PojoUtils.getJavaTypeNameHibernate(field, false);

			String tipoAux = "";
			if ((tipo.toUpperCase()).contains("BLOB")
					|| (tipo.toUpperCase()).contains("CLOB")
					|| (tipo.toUpperCase()).contains("LOB")) {
				tipoAux = "@Lob";
				tipoAux= tipoAux + " \t@Basic(fetch = FetchType.LAZY)";
				element.importType("javax.persistence.Lob");
				element.importType("javax.persistence.Basic");
				element.importType("javax.persistence.FetchType");
				

			}

			return tipoAux;
		} catch (Exception e) {
			log.error("Error:" + e.getMessage());
			return "";
		}
	}

	public static String typeConverter(String type, boolean generateImports) {
		try {
			String typeAux = "";
			if (generateImports) {// miramos si queremos generar los imports o
									// no. En los daos no queremos, por ejemplo
				if (type.toUpperCase().contains("JAVA.IO.SERIALIZABLE")) {
					typeAux = "byte[]";
				}else
					if (type.toUpperCase().endsWith("DATE")) {
						typeAux = "java.sql.Date";	
				} else if (type.toUpperCase().endsWith("CHARACTER")
							|| type.toUpperCase().endsWith("STRING")) {
						typeAux = "String";
				} else if (type.toUpperCase().contains("JAVA.MATH.BIGDECIMAL")) {
					typeAux = "java.math.BigDecimal";
				} else if (type.toUpperCase().contains("JAVA.UTIL.SET")) {
					typeAux = "java.util.List";
				} else if (type.toUpperCase().endsWith("BOOLEAN")) {
					typeAux = "Boolean";
				} else if (type.toUpperCase().endsWith("LONG")
						|| type.toUpperCase().endsWith("BYTE")
						|| type.toUpperCase().endsWith("SHORT")) {
					typeAux = "Long";
				} else if (type.toUpperCase().endsWith("INT")) {
					typeAux = "Integer";
				} else if (type.toUpperCase().endsWith("FLOAT")) {
					typeAux = "Float";
				} else if (type.toUpperCase().endsWith("DOUBLE")) {
					typeAux = "Double";
				} else if (type.toUpperCase().endsWith("BLOB")) {
					typeAux = "byte[]";
				} else if (type.toUpperCase().endsWith("CLOB")) {
					typeAux = "String";

				} else {
					typeAux = type;
				}
			} else {
				if (type.toUpperCase().contains("JAVA.IO.SERIALIZABLE")) {
					typeAux = "byte[]";
				} else 
				if (type.toUpperCase().endsWith("DATE")) {
						typeAux = "Date";
				} else if (type.toUpperCase().endsWith("CHARACTER")
						|| type.toUpperCase().endsWith("STRING")) {
					typeAux = "String";						
				} else if (type.toUpperCase().contains("JAVA.MATH.BIGDECIMAL")) {
					typeAux = "BigDecimal";
				} else if (type.toUpperCase().contains("JAVA.UTIL.SET")) {
					typeAux = "List";
				} else if (type.toUpperCase().endsWith("BOOLEAN")) {
					typeAux = "Boolean";
				} else if (type.toUpperCase().endsWith("LONG")
						|| type.toUpperCase().endsWith("BYTE")
						|| type.toUpperCase().endsWith("SHORT")) {
					typeAux = "Long";
				} else if (type.toUpperCase().endsWith("INT")) {
					typeAux = "Integer";
				} else if (type.toUpperCase().endsWith("FLOAT")) {
					typeAux = "Float";
				} else if (type.toUpperCase().endsWith("DOUBLE")) {
					typeAux = "Double";
				} else if (type.toUpperCase().endsWith("BLOB")) {
					typeAux = "byte[]";
				} else if (type.toUpperCase().endsWith("CLOB")) {
					typeAux = "String";

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

	public String getJavaTypeName(Property p, boolean useGenerics,
			boolean generateImports) {
		return 
		   getJavaTypeName(p, useGenerics, new NoopImportContext(),
				generateImports);
		
	}

	public String getGenericCollectionDeclaration(Collection collection,
			boolean preferRawTypeNames, ImportContext importContext,
			boolean generateImports) {

		Value element = collection.getElement();
		String elementType = null;
		if (generateImports) {
			elementType = importContext.importType(typeConverter(
					getJavaTypeName(element, preferRawTypeNames,
							generateImports), generateImports));
		} else {
			elementType = typeConverter(
					getJavaTypeName(element, preferRawTypeNames,
							generateImports), generateImports);
		}
		String genericDecl = elementType;
		if (collection.isIndexed()) {
			IndexedCollection idxCol = (IndexedCollection) collection;
			if (!(idxCol.isList())) {
				Value idxElement = idxCol.getIndex();
				String indexType = null;
				if (generateImports) {
					indexType = importContext.importType(typeConverter(
							getJavaTypeName(idxElement, preferRawTypeNames,
									generateImports), generateImports));
				} else {
					indexType = typeConverter(
							getJavaTypeName(idxElement, preferRawTypeNames,
									generateImports), generateImports);
				}
				genericDecl = indexType + "," + elementType;
			}
		}
		String decl = "<" + genericDecl + ">";
		return decl;
	}

	public String getJavaTypeName(Property p, boolean useGenerics,
			ImportContext importContext, boolean generateImports) {
		String overrideType = c2j.getMetaAsString(p, "property-type");
		if (!(StringHelper.isEmpty(overrideType))) {
			String importType = null;
			if (generateImports) {
				importType = importContext.importType(typeConverter(
						overrideType, true));
			} else {
				importType = typeConverter(overrideType, false);
			}
			if ((useGenerics) && (importType.indexOf("<") < 0)
					&& (p.getValue() instanceof Collection)) {
				String decl = getGenericCollectionDeclaration(
						(Collection) p.getValue(), true, importContext,
						generateImports);
				return importType + decl;
			}
			
			if (importType.toUpperCase().endsWith("BOOLEAN")){
				Iterator<Column> cols = p.getColumnIterator();
				while (cols.hasNext()){
					Column columna= cols.next();
					String valor = JDBCToHibernateTypeHelper.getJDBCTypeName(columna.getSqlTypeCode().intValue());
					if (valor.equals("BIT")){
						importType="Integer";
					}
					
					
				}
			}	
			return importType;
		}

		String rawType = getRawTypeName(p, useGenerics, true, importContext,
				generateImports);
		if (rawType == null) {
			throw new IllegalStateException(
					"getJavaTypeName *must* return a value");
		}
		if (generateImports) {
			String importa=typeConverter(rawType, true);
			if (importa.toUpperCase().endsWith("BOOLEAN")){
				Iterator<Column> cols = p.getColumnIterator();
				while (cols.hasNext()){
					Column columna= cols.next();
					String valor = JDBCToHibernateTypeHelper.getJDBCTypeName(columna.getSqlTypeCode().intValue());
					if (valor.equals("BIT")){
						importa="Integer";
					}
					
					
				}
			}	
			return importContext.importType(importa);
		} else {
			return typeConverter(rawType, false);
		}
	}

	private String getRawTypeName(Property p, boolean useGenerics,
			boolean preferRawTypeNames, ImportContext importContext,
			boolean generateImports) {
		Value value = p.getValue();
		try {
			if (value instanceof Array) {
				Array a = (Array) value;

				if (a.isPrimitiveArray()) {
					return typeConverter(toName(value.getType()
							.getReturnedClass()), generateImports);
				}
				if (a.getElementClassName() != null) {
					return a.getElementClassName() + "[]";
				}
				return typeConverter(
						getJavaTypeName(a.getElement(), preferRawTypeNames,
								generateImports), generateImports)
						+ "[]";
			}

			if (value instanceof Component) {
				Component component = (Component) value;
				if (component.isDynamic())
					return "java.util.Map";
				return typeConverter(component.getComponentClassName(),
						generateImports);
			}

			if ((useGenerics) && (value instanceof Collection)) {
				String decl = getGenericCollectionDeclaration(
						(Collection) value, preferRawTypeNames, importContext,
						generateImports);
				return typeConverter(
						getJavaTypeName(value, preferRawTypeNames,
								generateImports), generateImports)
						+ decl;
			}

			return typeConverter(
					getJavaTypeName(value, preferRawTypeNames, generateImports),
					generateImports);
		} catch (Exception e) {
			String msg = "Could not resolve type without exception for " + p
					+ " Value: " + value;
			if ((value != null) && (value.isSimpleValue())) {
				String typename = ((SimpleValue) value).getTypeName();
				log.warn(msg + ". Falling back to typename: " + typename);
				return typeConverter(typename, generateImports);
			}

			return null;
		}
	}

	private String getJavaTypeName(Value value, boolean preferRawTypeNames,
			boolean generateImports) { 
		return typeConverter(
				((String) value.accept(new JavaTypeFromValueVisitor())),
				generateImports);
		
	}

	private String toName(Class c) {
		if (c.isArray()) {
			Class a = c.getComponentType();

			return a.getName() + "[]";
		}

		return c.getName();
	}

	public String generateAnnIdGenerator(POJOClass pojo) {
		PersistentClass clazz = (PersistentClass) pojo.getDecoratedObject();
		KeyValue identifier = clazz.getIdentifier();
		StringBuffer wholeString = new StringBuffer("    ");
		if (identifier instanceof Component) {
			wholeString.append(AnnotationBuilder.createAnnotation(
					pojo.importType("javax.persistence.EmbeddedId"))
					.getResult());
		} else if (identifier instanceof SimpleValue) {
			StringBuffer idResult = new StringBuffer();
			AnnotationBuilder builder = AnnotationBuilder.createAnnotation(pojo
					.importType("javax.persistence.Id"));
			idResult.append(builder.getResult());
			idResult.append(" ");
			wholeString.append(idResult);
		}
		return wholeString.toString();
	}

	public String generateHashCode(Property property, String result,
			String thisName, boolean jdk5) {
		StringBuffer buf = new StringBuffer();
		if (c2j.getMetaAsBool(property, "use-in-equals")) {
			String javaTypeName = typeConverter(
					this.getJavaTypeName(property, jdk5, false), false);
			boolean isPrimitive = c2j.isPrimitive(javaTypeName);
			if (isPrimitive || "Long".equals(javaTypeName)
					|| "Float".equals(javaTypeName)
					|| "Double".equals(javaTypeName)
					|| "BigDecimal".equals(javaTypeName)) {
				buf.append(result).append(" = 37 * ").append(result)
						.append(" + ");

				String thisValue = thisName + "."
						+ getGetterSignature(property) + "()";
				if (("char".equals(javaTypeName))
						|| ("int".equals(javaTypeName))
						|| ("short".equals(javaTypeName))
						|| ("byte".equals(javaTypeName))) {
					buf.append(thisValue);
				} else if ("boolean".equals(javaTypeName)
						|| "Boolean".equals(javaTypeName)) {
					buf.append("(" + thisValue + "?1:0)");
				} else if ("Long".equals(javaTypeName)
						|| "Float".equals(javaTypeName)
						|| "Double".equals(javaTypeName)
						|| "BigDecimal".equals(javaTypeName)) {

					buf.append(thisValue + ".intValue()");
				} else {
					buf.append("(int) ");
					buf.append(thisValue);
				}
				buf.append(";");
			} else if (javaTypeName.endsWith("[]")) {
				if (jdk5) {
					buf.append(result).append(" = 37 * ").append(result)
							.append(" + ");

					buf.append("(this.")
							.append(getGetterSignature(property))
							.append("() == null ? 0 : " + "Arrays"
									+ ".hashCode(").append(thisName)
							.append(".").append(getGetterSignature(property))
							.append("())").append(")").append(";");
				} else {
					buf.append(internalGenerateArrayHashcode(property,
							javaTypeName, result, thisName));
				}
			} else {
				buf.append(result).append(" = 37 * ").append(result)
						.append(" + ");

				buf.append("(this.").append(getGetterSignature(property))
						.append("() == null ? 0 : ").append(thisName)
						.append(".").append(getGetterSignature(property))
						.append("()").append(".hashCode()").append(")")
						.append(";");
			}

		}

		return buf.toString();
	}

	private String internalGenerateArrayHashcode(Property property,
			String javaTypeName, String result, String thisName) {
		StringBuffer buf = new StringBuffer();

		String propertyHashVarName = property.getName() + "Hashcode";
		String propertyArrayName = property.getName() + "Property";

		buf.append("int ").append(propertyHashVarName).append(" = 0;\n");

		buf.append("         ").append(javaTypeName).append(" ")
				.append(propertyArrayName).append(" = ").append(thisName)
				.append(".").append(getGetterSignature(property))
				.append("();\n");

		buf.append("         if(").append(propertyArrayName)
				.append(" != null) {\n");

		buf.append("             ").append(propertyHashVarName)
				.append(" = 1;\n");

		String elementType = javaTypeName.replaceAll("\\[\\]", "");
		buf.append("             for (int i=0; i<").append(propertyArrayName)
				.append(".length; i++) {\n");

		if (javaTypeName.startsWith("long")) {
			buf.append("                 int elementHash = (int)(")
					.append(propertyArrayName).append("[i] ^ (")
					.append(propertyArrayName).append("[i] >>> 32));\n");

			buf.append("                 ").append(propertyHashVarName)
					.append(" = 37 * ").append(propertyHashVarName)
					.append(" + elementHash;\n");
		} else if (javaTypeName.startsWith("boolean")) {
			buf.append("                 ").append(propertyHashVarName)
					.append(" = 37 * ").append(propertyHashVarName)
					.append(" + (").append(propertyArrayName)
					.append("[i] ? 1231 : 1237);\n");
		} else if (javaTypeName.startsWith("float")
				|| javaTypeName.contains("Float")) {
			buf.append("                 ").append(propertyHashVarName)
					.append(" = 37 * ").append(propertyHashVarName)
					.append(" + Float.floatToIntBits(")
					.append(propertyArrayName).append("[i]);\n");
		} else if (javaTypeName.startsWith("double")
				|| javaTypeName.contains("Double")) {
			buf.append("                 long bits = Double.doubleToLongBits(")
					.append(propertyArrayName).append("[i]);\n");

			buf.append("                 ").append(propertyHashVarName)
					.append(" = 37 * ").append(propertyHashVarName)
					.append(" + (int)(bits ^ (bits >>> 32));\n");
		} else if ((javaTypeName.startsWith("int"))
				|| (javaTypeName.startsWith("short"))
				|| (javaTypeName.startsWith("char"))
				|| (javaTypeName.startsWith("byte"))) {
			buf.append("                 ").append(propertyHashVarName)
					.append(" = 37 * ").append(propertyHashVarName)
					.append(" + ").append(propertyArrayName).append("[i];\n");
		} else if (javaTypeName.contains("Long")
				|| javaTypeName.contains("Integer")
				|| javaTypeName.contains("BigDecimal")) {
			buf.append("                 int elementHash = (int)(")
					.append(propertyArrayName).append("[i] ^ (")
					.append(propertyArrayName).append("[i] >>> 32));\n");

			buf.append("                 ").append(propertyHashVarName)
					.append(" = 37 * ").append(propertyHashVarName)
					.append(" + elementHash;\n");
		} else {
			buf.append("                 ").append(propertyHashVarName)
					.append(" = 37 * ").append(propertyHashVarName)
					.append(" + ").append(propertyArrayName)
					.append("[i].hashCode();\n");
		}

		buf.append("             }\n");
		buf.append("         }\n\n");

		buf.append("         ").append(result).append(" = 37 * ")
				.append(result).append(" + ").append(propertyHashVarName)
				.append(";\n");

		return buf.toString();
	}

	public String generateEquals(String thisName, String otherName,
			boolean useGenerics, POJOClass pojo) {
		Iterator allPropertiesIterator = getEqualsHashCodePropertiesIterator(pojo);
		return generateEquals(thisName, otherName, allPropertiesIterator,
				useGenerics);
	}

	public Iterator getEqualsHashCodePropertiesIterator(POJOClass pojo) {
		Iterator iter = pojo.getAllPropertiesIterator();
		return getEqualsHashCodePropertiesIterator(iter);
	}

	private Iterator getEqualsHashCodePropertiesIterator(Iterator iter) {
		List<Property> properties = new ArrayList();
		while (iter.hasNext()) {
			Property element = (Property) iter.next();
			if (usePropertyInEquals(element)) {
				properties.add(element);
			}
		}

		return properties.iterator();
	}

	private boolean usePropertyInEquals(Property property) {
		boolean hasEqualsMetaAttribute = c2j.hasMetaAttribute(property,
				"use-in-equals");
		boolean useInEquals = c2j.getMetaAsBool(property, "use-in-equals");

		if (property.isNaturalIdentifier()) {
			return ((!(hasEqualsMetaAttribute)) || (useInEquals));
		}

		return useInEquals;
	}

	protected String generateEquals(String thisName, String otherName,
			Iterator allPropertiesIterator, boolean useGenerics) {
		StringBuffer buf = new StringBuffer();
		while (allPropertiesIterator.hasNext()) {
			Property property = (Property) allPropertiesIterator.next();
			if (buf.length() > 0)
				buf.append("\n && ");
			String javaTypeName = typeConverter(
					getJavaTypeName(property, false, false), false);
			buf.append(internalgenerateEquals(javaTypeName, thisName + "."
					+ getGetterSignature(property) + "()", otherName + "."
					+ getGetterSignature(property) + "()"));
		}

		if (buf.length() == 0) {
			return "false";
		}

		return buf.toString();
	}

	private String internalgenerateEquals(String typeName, String lh, String rh) {
		if (c2j.isPrimitive(typeName)) {
			return "(" + lh + " == " + rh + ")";
		}

		if (useCompareTo(typeName)) {
			return "((" + lh + " == " + rh + ") || (" + lh + " != null && " + rh
					+ " != null && " + lh + ".compareTo(" + rh + ") == 0))";
		}
		if (typeName.endsWith("[]")) {
			return "((" + lh + " == " + rh + ") || (" + lh + " != null && " + rh
					+ "!=null && " + "Arrays" + ".equals(" + lh + ", " + rh
					+ ")))";
		}
		if (typeName.contains("Long")) {
			return "(" + lh + ".intValue() == " + rh + ".intValue())";
		}
		return "((" + lh + " == " + rh + ") || (" + lh + " != null && " + rh
				+ " != null && " + lh + ".equals(" + rh + ")))";
	}

	private boolean useCompareTo(String javaTypeName) {
		return ("java.math.BigDecimal".equals(javaTypeName));
	}
}
