package com.ejie.uda.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class TableLabelProvider extends LabelProvider{ // implements ITableLabelProvider{

	private ResourceManager resourceManager;

	public ImageDescriptor getImageDescriptorHibernateTools(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.hibernate.eclipse.console", path);
	}

	public TableLabelProvider() {
		this.resourceManager = new LocalResourceManager(JFaceResources.getResources());
	}

	public final Image getImage(Object element) {

		ImageDescriptor descriptor = null;

		if (element.getClass() == TreeNode.class) {

			TreeNode node = (TreeNode) element;

			if ("table".equals(node.getType())) {
				descriptor = getImageDescriptorHibernateTools("icons/images/table.gif");
			} else if ("column".equals(node.getType())) {
				descriptor = getImageDescriptorHibernateTools("icons/images/columns.gif");
			}
		}

		return this.resourceManager.createImage(descriptor);
	}
	
}