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