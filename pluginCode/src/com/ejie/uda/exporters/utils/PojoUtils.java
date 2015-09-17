package com.ejie.uda.exporters.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.MetaAttributable;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.ExporterException;
import org.hibernate.tool.hbm2x.MetaAttributeHelper;
import org.hibernate.tool.hbm2x.pojo.BasicPOJOClass;
import org.hibernate.tool.hbm2x.pojo.ImportContext;
import org.hibernate.tool.hbm2x.pojo.NoopImportContext;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.hibernate.tool.hbm2x.visitor.JavaTypeFromValueVisitor;
import org.hibernate.util.StringHelper;
/**
 * 
 * Clase principalmente utilizada en la generación de la capa de modelo, la cual contiene las funciones genéricas independientemente de la persistencia utilizada
 *
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class PojoUtils {
	private final static Logger log = Logger.getLogger(PojoUtils.class);
	WarningSupressorJPA warjpa = new WarningSupressorJPA();
	WarningSupressorJdbc warjdbc = new WarningSupressorJdbc();

	public PojoUtils() {

	}

	public List<String[]> getListFromCommaSeparatedString(String campos) {
		List<String[]> result = new ArrayList<String[]>();
		try {
			StringTokenizer stCampos = new StringTokenizer(campos, ",");
			while (stCampos.hasMoreTokens()){
				result.add(stCampos.nextToken().trim().split(" "));
			}
			return result;
		} catch (Exception e) {
			log.error("Error:" + e.getMessage() + e.getCause());
			return null;
		}
	}

	public String generateJsonIgnoreAnnotation(Property propiedad,
			POJOClass pojo) {
		String result = "";
		if (pojo.getIdentifierProperty() != null
				&& !pojo.getIdentifierProperty().equals(propiedad)) {
			String tipe = WarningSupressorJPA.typeConverter(
					warjpa.getJavaTypeName(propiedad, true, false), false);
			if (!tipe.toUpperCase().endsWith("TIMESTAMP")
					&& !tipe.toUpperCase().endsWith("STRING")
					&& !tipe.toUpperCase().endsWith("CHARACTER")
					&& !tipe.toUpperCase().endsWith("DATE")
					&& !tipe.toUpperCase().endsWith("BOOLEAN")
					&& !tipe.toUpperCase().endsWith("BIGDECIMAL")
					&& !tipe.toUpperCase().endsWith("LIST")
					&& !tipe.toUpperCase().endsWith("LONG")
					&& !tipe.toUpperCase().endsWith("BYTE")
					&& !tipe.toUpperCase().endsWith("INT")
					&& !tipe.toUpperCase().endsWith("DOUBLE")
					&& !tipe.toUpperCase().endsWith("FLOAT")
					&& !tipe.toUpperCase().endsWith("INTEGER")) {
				result = "@JsonIgnore";
				pojo.importType("org.codehaus.jackson.annotate.JsonIgnore");

			}
		}

		return result;

	}

	public String generateJsonIgnoreAnnotationJdbc(Property propiedad,
			POJOClass pojo) {
		String result = "";
		if (pojo.getIdentifierProperty() != null
				&& !pojo.getIdentifierProperty().equals(propiedad)) {
			String tipe = WarningSupressorJdbc.typeConverter(
					warjdbc.getJavaTypeName(propiedad, true, false), false);
			if (!tipe.toUpperCase().endsWith("TIMESTAMP")
					&& !tipe.toUpperCase().endsWith("STRING")
					&& !tipe.toUpperCase().endsWith("CHARACTER")
					&& !tipe.toUpperCase().endsWith("DATE")
					&& !tipe.toUpperCase().endsWith("BOOLEAN")
					&& !tipe.toUpperCase().endsWith("BIGDECIMAL")
					&& !tipe.toUpperCase().endsWith("LIST")
					&& !tipe.toUpperCase().endsWith("LONG")
					&& !tipe.toUpperCase().endsWith("BYTE")
					&& !tipe.toUpperCase().endsWith("INT")
					&& !tipe.toUpperCase().endsWith("DOUBLE")
					&& !tipe.toUpperCase().endsWith("FLOAT")
					&& !tipe.toUpperCase().endsWith("INTEGER")) {
				result = "@JsonIgnore";
				pojo.importType("org.codehaus.jackson.annotate.JsonIgnore");

			}
		}

		return result;

	}

	public String generateTimeStampAnnotationGetter(String tipe, POJOClass pojo) {
		String result = "";
		if (tipe.toUpperCase().endsWith("TIMESTAMP")
				|| tipe.toUpperCase().endsWith("DATE")) {
			result = "@JsonSerialize(using = JsonDateSerializer.class)";
			pojo.importType("org.codehaus.jackson.map.annotate.JsonSerialize");
			pojo.importType("com.ejie.x38.control.JsonDateSerializer");
		}
		return result;

	}

	public String generateTimeStampAnnotationSetter(String tipe, POJOClass pojo) {
		String result = "";
		if (tipe.toUpperCase().endsWith("TIMESTAMP")
				|| tipe.toUpperCase().endsWith("DATE")) {
			result = "@JsonDeserialize(using = JsonDateDeserializer.class)";
			pojo.importType("org.codehaus.jackson.map.annotate.JsonDeserialize");
			pojo.importType("com.ejie.x38.control.JsonDateDeserializer");
		}
		return result;

	}

	/*
	 * Sobreescribimos el metodo pojo.generateAnnColumnAnnotation(Property) de
	 * Hibernate, a fin de eliminar el texto 'unique = true' de las anotaciones
	 * Column que se crean debajo de la anotación @Id
	 */
	public static String generateAnnColumnAnnotation(Property field,
			POJOClass element) {
		try {
			String auxiliar = element.generateAnnColumnAnnotation(field);
			Property identificador = element.getIdentifierProperty();
			if (field.equals(identificador)) {
				String reemplazo = auxiliar.replace("unique=true,", "");
				return reemplazo;
			} else {
				return auxiliar;
			}
		} catch (Exception e) {
			log.error("Error:" + e.getMessage());
			return null;
		}
	}

	/*
	 * Sobreescribimos el metodo pojo.generateAnnIdGenerator() de Hibernate, a
	 * fin de eliminar el texto '@GenericGenerator(name = "generator", strategy
	 * = "foreign", parameters = @Parameter(name = "property", value =
	 * "ordered"))
	 * 
	 * @Id
	 * 
	 * @GeneratedValue(generator = "generator")' , sustituyendolo por @Id
	 * únicamente
	 */
	public static String generateAnnIdGenerator(POJOClass element) {
		try {
			String auxiliar = element.generateAnnIdGenerator();
			String reemplazo = auxiliar;
			if (auxiliar.contains("@Id")) {
				reemplazo = "@Id";
			}

			return reemplazo;
		} catch (Exception e) {
			log.error("Error:" + e.getMessage());
			return null;
		}
	}

	/*
	 * Cuando el pojo que creamos disponga de una clave primaria compuesta por
	 * más de un campo, es necesario generar la anotacion
	 * @IdClass(<nombreClaseDeseada>) antes de la declaración de la clase para
	 * un correcto funcionamiento de JPA 2.0. Para ello generamos este método
	 */
	public static String generateIdClassAnnotation(Property fieldPrimary,
			POJOClass element) {
		try {
			String tipoAux = "";
			if (fieldPrimary.isComposite()) {
				tipoAux = "@IdClass (" + element.getPackageName() + ".model."
						+ element.getDeclarationName() + "Id.class)";
			}
			return tipoAux;
		} catch (Exception e) {
			log.error("Error:" + e.getMessage());
			return null;
		}
	}

	public static String getAppName(String ruta) {
		try {
			String typeAux = "";
			if (ruta.contains("Classes")) {
				typeAux = ruta.substring(ruta.lastIndexOf("\\") + 1,
						ruta.lastIndexOf("EARClasses"));
			} else {
				typeAux = ruta.substring(ruta.lastIndexOf("\\") + 1,
						ruta.length());
			}
			return typeAux;
		} catch (Exception e) {
			log.error("Error:" + e.getMessage());
			return null;
		}
	}

	public String getGetterSignature(Property p, POJOClass pojo) {

		String prefix = (WarningSupressorJPA.typeConverter(
				warjpa.getJavaTypeName(p, false, false), false)
				.equals("boolean")) ? "is" : "get";
		return prefix + BasicPOJOClass.beanCapitalize(p.getName());
	}

	public static String getJavaTypeNameHibernate(Property p, boolean useGenerics)
	{
		
		return getJavaTypeNameHibernate(p, useGenerics, new NoopImportContext());
		}

	public static String getJavaTypeNameHibernate(Property p, boolean useGenerics,
			ImportContext importContext) {
		String overrideType = getMetaAsString(p, "property-type");
		if (!(StringHelper.isEmpty(overrideType))) {
			String importType = overrideType;
			if ((useGenerics) && (importType.indexOf("<") < 0)
					&& (p.getValue() instanceof Collection)) {
				String decl = getGenericCollectionDeclaration(
						(Collection) p.getValue(), true, importContext);
				return importType + decl;
			}

			return importType;
		}

		String rawType = getRawTypeName(p, useGenerics, true, importContext);
		if (rawType == null) {
			throw new IllegalStateException(
					"getJavaTypeName *must* return a value");
		}
		return rawType;
	}

	private static String getRawTypeName(Property p, boolean useGenerics,
			boolean preferRawTypeNames, ImportContext importContext) {
		Value value = p.getValue();
		try {
			if (value instanceof Array) {
				Array a = (Array) value;

				if (a.isPrimitiveArray()) {
					return toName(value.getType().getReturnedClass());
				}
				if (a.getElementClassName() != null) {
					return a.getElementClassName() + "[]";
				}
				return getJavaTypeNameHibernate(a.getElement(), preferRawTypeNames)
						+ "[]";
			}

			if (value instanceof Component) {
				Component component = (Component) value;
				if (component.isDynamic())
					return "java.util.Map";
				return component.getComponentClassName();
			}

			if ((useGenerics) && (value instanceof Collection)) {
				String decl = getGenericCollectionDeclaration(
						(Collection) value, preferRawTypeNames, importContext);
				return getJavaTypeNameHibernate(value, preferRawTypeNames) + decl;
			}

			return getJavaTypeNameHibernate(value, preferRawTypeNames);
		} catch (Exception e) {
			String msg = "Could not resolve type without exception for " + p
					+ " Value: " + value;
			if ((value != null) && (value.isSimpleValue())) {
				String typename = ((SimpleValue) value).getTypeName();
				log.warn(msg + ". Falling back to typename: " + typename);
				return typename;
			}

			throw new ExporterException(msg, e);
		}
	}

	public static String getGenericCollectionDeclaration(Collection collection,
			boolean preferRawTypeNames, ImportContext importContext) {
		Value element = collection.getElement();
		String elementType = getJavaTypeNameHibernate(element,
				preferRawTypeNames);
		String genericDecl = elementType;
		if (collection.isIndexed()) {
			IndexedCollection idxCol = (IndexedCollection) collection;
			if (!(idxCol.isList())) {
				Value idxElement = idxCol.getIndex();
				String indexType = getJavaTypeNameHibernate(
						idxElement, preferRawTypeNames);
				genericDecl = indexType + "," + elementType;
			}
		}
		String decl = "<" + genericDecl + ">";
		return decl;
	}

	private static String getJavaTypeNameHibernate(Value value, boolean preferRawTypeNames) {
		return ((String) value.accept(new JavaTypeFromValueVisitor()));
	}

	private static String toName(Class c) {
		if (c.isArray()) {
			Class a = c.getComponentType();

			return a.getName() + "[]";
		}

		return c.getName();
	}

	public static String getMetaAsString(MetaAttributable pc, String attribute) {
		MetaAttribute c = pc.getMetaAttribute(attribute);

		return MetaAttributeHelper.getMetaAsString(c);
	}

}
