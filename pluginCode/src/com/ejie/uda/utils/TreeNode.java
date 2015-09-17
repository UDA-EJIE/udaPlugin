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

public class TreeNode implements Comparable<TreeNode> {

	private String name;
	private String nameBBDD;
	private List<TreeNode> children = new ArrayList<TreeNode>();
	private TreeNode parent;
	private String type;
	private boolean isChecked;
	private boolean isComposite;
	private boolean isPrimaryKey;
	private String referenceClass;
	
	private boolean isMN;

	public TreeNode(String name) {
		this.name = name;
	}
	
	public TreeNode(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public TreeNode(String name, String type,String nameBBDD) {
		this.name = name;
		this.type = type;
		this.nameBBDD = nameBBDD;
	}
	
	public TreeNode(String name, String type,String nameBBDD, boolean isMN) {
		this.name = name;
		this.type = type;
		this.nameBBDD = nameBBDD;
		this.isMN  = isMN;
	}

	public TreeNode getParent() {
		return parent;
	}

	public TreeNode addChild(TreeNode child) {
		children.add(child);
		child.parent = this;
		return this;
	}

	public List<TreeNode> getChildren() {
		return children;
	}
	
	public String getName() {
		
		String nameFormat = "";
		if (!Utilities.isBlank(this.toString())){
			if (this.toString().contains(":")){
				String[] nameSplit = this.toString().split(":");
				
				if (nameSplit!= null && nameSplit.length > 0){
					nameFormat = nameSplit[0].trim();
				}
			}else{
				nameFormat = this.toString().trim();	
			}
		}
		
		return nameFormat;
	}

	public String toString() {
		return this.name;
	}
	
	public String toStringBBDD() {
		return this.nameBBDD;
	}
	
	public String getNameBBDD() {
		
		String nameFormat = "";
		if (!Utilities.isBlank(this.toStringBBDD())){
			if (this.toStringBBDD().contains(":")){
				String[] nameSplit = this.toStringBBDD().split(":");
				
				if (nameSplit!= null && nameSplit.length > 0){
					nameFormat = nameSplit[0].trim();
				}
			}else{
				nameFormat = this.toStringBBDD().trim();	
			}
		}
		
		return nameFormat;
	}
	
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		 this.type = type;
	}

	public boolean isMN() {
		return isMN;
	}
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	public boolean isComposite() {
		return isComposite;
	}

	public void setComposite(boolean isComposite) {
		this.isComposite = isComposite;
	}
	
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	
	public String getReferenceClass() {
		return referenceClass;
	}

	public void setReferenceClass(String referenceClass) {
		this.referenceClass = referenceClass;
	}

	public boolean isCheckedChildren() {
		boolean checked = true;
		
		List<TreeNode> childrens = this.getChildren();
		if (childrens != null && !childrens.isEmpty()){
			
			for (TreeNode treeNode : childrens) {
				if (!treeNode.isChecked()){
					checked = false;
					break;
				}
			}
		}
		return checked;
	}
	
	public boolean isNotAllCheckedChildren() {
		boolean notChecked = true;
		
		List<TreeNode> childrens = this.getChildren();
		if (childrens != null && !childrens.isEmpty()){
			
			for (TreeNode treeNode : childrens) {
				if (treeNode.isChecked()){
					notChecked = false;
					break;
				}
			}
		}
		return notChecked;
	}

	@Override
	public int compareTo(TreeNode o) {
		return name.compareTo(o.getName());
	}
}
