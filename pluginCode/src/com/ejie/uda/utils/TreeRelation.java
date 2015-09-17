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
package com.ejie.uda.utils;

import java.util.ArrayList;
import java.util.List;

public class TreeRelation {
	private String name;
	private String nameBBDD;
	private List<TreeRelation> children = new ArrayList<TreeRelation>();
	private TreeRelation parent;
	private String type;
	
	public TreeRelation(String name) {
		this.name = name;
	}
	
	public TreeRelation(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public TreeRelation(String name, String type, String nameBBDD) {
		this.name = name;
		this.type = type;
		this.nameBBDD = nameBBDD;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public TreeRelation getParent() {
		return parent;
	}
	public TreeRelation addChild(TreeRelation child) {
		children.add(child);
		child.parent = this;
		return this;
	}
	public TreeRelation updateChild(TreeRelation child) {
		children.remove(child);
		children.add(child);
		child.parent = this;
		return this;
	}
	
	public List<TreeRelation> getChildren() {
		return children;
	}

	public String getNameBBDD() {
		return nameBBDD;
	}

	public void setNameBBDD(String nameBBDD) {
		this.nameBBDD = nameBBDD;
	}

}
