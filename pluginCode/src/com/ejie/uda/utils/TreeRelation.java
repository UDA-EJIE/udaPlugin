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
