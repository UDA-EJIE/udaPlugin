package com.ejie.uda.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.ejie.uda.operations.DataBaseWorker;
import com.ejie.uda.utils.ConnectionData;
import com.ejie.uda.utils.ContentProvider;
import com.ejie.uda.utils.TableLabelProvider;
import com.ejie.uda.utils.TreeNode;
import com.ejie.uda.utils.TreeRelation;
import com.ejie.uda.utils.Utilities;

/**
 *  Clase la cual define la segunda pantalla del asistente "Generar código de negocio y control"
 */
public class GenerateCodeWizardPageTwo extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private CheckboxTreeViewer schemaCheckboxTree;
	private TreeRelation schemaMNTree;
	

	

	/**
	 * Primera ventana del Wizard de Plugin, donde se selecciona
	 * la opción de generar una aplicación
	 * @param selection
	 */
	public GenerateCodeWizardPageTwo(ISelection selection) {
		super("wizardPage");

		setTitle("Generar código para una aplicación UDA");
		setDescription("Este Wizard genera el código fuente para desplegar una aplicación UDA");
	}

	/**
	 * Creación de controles de la ventana
	 * @param parent - controlador padre
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 2;
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 3;
		GridData gdTreeViewer = new GridData(GridData.FILL_BOTH);
		gdTreeViewer.horizontalSpan = 3;
		
		// Descripción de la operación
		Label descLabel= new Label(container, SWT.NULL);
		descLabel.setText("Seleccione las tablas y sus columnas para la generación de código");
		descLabel.setLayoutData(gd3);
		
		// Salto de línea
		Label hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(gd3);
		
		// Tree con el contenido del esquema de BBDD
		// Los datos serán insertados desde el método público llamado
		// por la página Two al dar al botón siguiente
		schemaCheckboxTree = new CheckboxTreeViewer(container);
		schemaCheckboxTree.setContentProvider(new ContentProvider());
		schemaCheckboxTree.setLabelProvider(new TableLabelProvider());
		schemaCheckboxTree.getTree().setLayoutData(gdTreeViewer);

	    // Cuando chequea/descheckea un checkbox en un tree, checkea/descheckea a todos sus hijos
		schemaCheckboxTree.addCheckStateListener(new ICheckStateListener() {
	      public void checkStateChanged(CheckStateChangedEvent event) {
	    	  TreeNode node = (TreeNode)event.getElement();
	        if (event.getChecked()) {
	          // checkea a todos los hijos
	        	schemaCheckboxTree.setSubtreeChecked(node, true);
	        	node.setChecked(true);
	        }else{
	        	if (node.isPrimaryKey()){
	        		// Si es primary key no se puede deseleccionar
	        		schemaCheckboxTree.setChecked(node, true);
	        		node.setChecked(true);
	        	}else{
		        	// descheckea a todos los hijos
		        	schemaCheckboxTree.setSubtreeChecked(node, false);
		        	node.setChecked(false);
	        	}
	        }
	        // Actualiza el estado del checks-padres
	        updateParentItems((TreeNode)((TreeNode)event.getElement()).getParent());
	      }
	    });
		
		// Botón de seleccionar todo
		final Button selectAllButton = new Button(container, SWT.NONE);
		selectAllButton.setText("Seleccionar Todas");
		selectAllButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				setSelection(true);
			}
		});

		// Botón de deseleccionar todo
		final Button deselectAllButton = new Button(container, SWT.NONE);
		deselectAllButton.setText("Deseleccionar Todas");
		deselectAllButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				setSelection(false);
			}
		});
		
		setControl(container);
	}
	
	public boolean canFlipToNextPage() {
		List<TreeNode> treeNodes = generateFilterReveng();
		
		if (treeNodes == null || treeNodes.isEmpty()){
			return false;
		}else{
			return true;	
		}
	}
	
	public IWizardPage getNextPage() {
		
		List<TreeNode> treeNodes = generateFilterReveng();
		
		if (treeNodes == null || treeNodes.isEmpty()){
			setErrorMessage("No se ha seleccionado ningún elemento");
//			setPageComplete(false);
			return getWizard().getContainer().getCurrentPage();
		}else{
			setPageComplete(true);
			return super.getNextPage();	
		}
		
	}
	
	/*************/
	/*  Getters  */
	/*************/
	
	/**
	 * Recupera los Objetos checkeados
	 * 
	 * @return lista de objetos (TreeNode)
	 */
	public List<TreeNode> generateFilterReveng() {
		
		List<TreeNode> treeNodesColumns = null;
		List<TreeNode> filterTreeNodes = new ArrayList<TreeNode>(0);
		List<TreeNode> treeNodes = new ArrayList<TreeNode>(0);
		TreeNode rootTreeNodes = (TreeNode)schemaCheckboxTree.getInput();
		List<TreeRelation> treeRelation = new ArrayList<TreeRelation>(0);
		TreeRelation rootTreeRelation = getSchemaMNTree();
		//List<TreeRelation> listTreeRelationList = null;
		List<String> auxiliarTabla = new ArrayList<String>(0);
		
		//miramos en las tablas seleccionadas si tienen alguna m:n relacionada
		// En caso afirmativo incluimos todos sus M:N hijas
		
		
		if (rootTreeNodes != null){
			treeNodes = rootTreeNodes.getChildren();
			
			for (TreeNode treeNodeTable : treeNodes) {
				
				if (schemaCheckboxTree.getChecked(treeNodeTable)){
					treeNodeTable.setChecked(true);
					treeNodesColumns = treeNodeTable.getChildren();
					
					for (TreeNode treeNodeColumn : treeNodesColumns) {
						if (schemaCheckboxTree.getChecked(treeNodeColumn)){
							treeNodeColumn.setChecked(true);
							// En las claves compuestas, se checkearán todas o ninguna
							List<TreeNode> treeNodesComposite = treeNodeColumn.getChildren();
							for (TreeNode treeNodeComposite : treeNodesComposite) {
								treeNodeComposite.setChecked(true);
							}
							
						}else{
							treeNodeColumn.setChecked(false);
						}
					}
					auxiliarTabla.add(treeNodeTable.getNameBBDD());
					filterTreeNodes.add(treeNodeTable);
					
				}
				
			}
		}
		//recorremos la treeRelation del m:n para ver si existe. En caso afirmativo metremos a los hijos
		if (rootTreeRelation!=null){
			treeRelation = rootTreeRelation.getChildren();
			List<TreeRelation> treeRelationChildren = null;
			//String nombreMN=null;
			for (TreeRelation treeRelationTable : treeRelation ) { 	
					boolean encontrado=true;
					//nombreMN = treeRelationTable.getName();
					treeRelationChildren = treeRelationTable.getChildren() ;
					for (TreeRelation treeRelationChild : treeRelationChildren ) { 
						//para cada hijo miramos si esta seleccionado. si no estan todas las tablas seleccionadas, no la incluimos
					    Iterator<String> iteradorseleccionadas= auxiliarTabla.iterator();
					    boolean encontradoAux=false;
					    while (iteradorseleccionadas.hasNext()){
					    	String nombreSeleccionado = iteradorseleccionadas.next();
					    	if(nombreSeleccionado.equals(treeRelationChild.getName())){
					    		encontradoAux=true;
					    		break;
					    	}
					    }
					    if (!encontradoAux){
					    	encontrado=false;
					    	break;
					    }

					}
					if (encontrado){
						TreeNode tablaMN =  new TreeNode(Utilities.getRelationName (treeRelationTable.getName()), "table", treeRelationTable.getNameBBDD(), true);
						filterTreeNodes.add(tablaMN);
					}	
				}	
			}
			
		
		
		return filterTreeNodes;
	}
	
	// Pone el estado inical al checked
	public void setInitialCheckedState(){
		setSelection(true);
	}
	
	/**********************/
	/*  Métodos privados  */
	/**********************/
	
	private void setSelection(boolean state) {
		if (schemaCheckboxTree != null){
			TreeNode input = (TreeNode) schemaCheckboxTree.getInput();
			select(input, state);	
		}
	}

	/**
	 * Recursivo seleccionado y deseleccionado de nodos
	 */
	private void select(TreeNode root, boolean state) {
		if (root != null){
			List<TreeNode> children = root.getChildren();
			if (children!=null && !children.isEmpty()){
				for(TreeNode node : children){
					schemaCheckboxTree.setChecked(node, state);

					select(node, state);
				}
			}	
		}		
	}

	public void setSchemaCheckboxTree(ConnectionData conData){
		TreeNode schemaAux=DataBaseWorker.getSchemaTree(conData);
		schemaCheckboxTree.setInput(schemaAux);
		//Necesario para los métodos de gestión de tablas de relaciones M:N
		setSchemaMNTree(DataBaseWorker.getSchemaTreeWithMN(conData));
	}
	
	/**
	* Actualiza los padres del check seleccionado
	*/
	private void updateParentItems(TreeNode item) {
		if (item != null) {
			List<TreeNode> children = item.getChildren();
			boolean containsChecked = false;
			boolean containsUnchecked = false;
			
			for (TreeNode treeNode : children) {
				containsChecked |= schemaCheckboxTree.getChecked(treeNode);
				containsUnchecked |= (!schemaCheckboxTree.getChecked(treeNode));
			}
			
			item.setChecked(containsChecked);
			schemaCheckboxTree.setChecked(item, containsChecked);
			TreeNode parent = item.getParent();
			
			// Check las claves primarias obligatorias.
			if (containsChecked){
				checkPrimaryKeys(item);
			}
			// Actualiza los padres
			updateParentItems(parent);
		}
	}
	
	/**
	* Checkea las columnas Primary Key del nodo tabla
	*/
	private void checkPrimaryKeys(TreeNode item) {
		
    	if (item != null) {
			List<TreeNode> children = item.getChildren();
			
			if (children != null && !children.isEmpty()){
				for (TreeNode treeNode : children) {
					
					if (treeNode.isComposite() && treeNode.isPrimaryKey()){
						
						// Checkea las claves primarias
						schemaCheckboxTree.setChecked(treeNode, true);
						treeNode.setChecked(true);
						
						List<TreeNode> childrenComposite = treeNode.getChildren();
						if (childrenComposite != null && !childrenComposite.isEmpty()){
							for (TreeNode treeNodeComposite : childrenComposite) {
								if (treeNodeComposite.isPrimaryKey()){
									// Checkea las claves primarias
									schemaCheckboxTree.setChecked(treeNodeComposite, true);
									treeNodeComposite.setChecked(true);
								}
							}
						}
					}else if (treeNode.isPrimaryKey()){
						// Checkea las claves primarias
						schemaCheckboxTree.setChecked(treeNode, true);
						treeNode.setChecked(true);
					}
				}
			}
			
		}
	}
	public TreeRelation getSchemaMNTree() {
		return schemaMNTree;
	}

	public void setSchemaMNTree(TreeRelation schemaMNTree) {
		this.schemaMNTree = schemaMNTree;
	}	

}