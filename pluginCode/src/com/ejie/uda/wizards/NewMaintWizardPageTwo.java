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
package com.ejie.uda.wizards;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.ejie.uda.utils.Maint;
import com.ejie.uda.utils.Utilities;

/**
 * Clase que define la segunda pantalla del asistente "Generar mantenimiento"
 */
public class NewMaintWizardPageTwo extends WizardPage {
	
	// Pestañas
	TabFolder tabFolder;
	TabItem generalFeaturesTab;
	TabItem eventsTab;
	
	// Propiedades utilizadas en la pantalla
	private Text nameMaintText;
	private Text titleMaintText;
	private Button detailMaintCheck;
	private Text detailMaintText;
	private Combo detailMaintButtonsCombo;
	private Button searchMaintCheck;
	private Text searchMaintText;
	private Button toolbarMaintCheck;
	private Text toolbarMaintText;
	private Button toolbarMaintAutoSizeCheck;
	private Button toolbarMaintButtonsDefaultCheck;
	private Button feedbackMaintCheck;
	private Text feedbackMaintText;
	private Button feedbackMaintShowAllCheck;
	private Button feedbackMaintCollapsibleCheck;
	private Text imgPathMaintText;
	private Button validationModeMaintCheck;
	private Button detailServerMaintCheck;
	// Eventos del mantenimiento
	private Text eventOnbeforeDetailShowText;
	private Text eventOnafterDetailShowText;
	
	/**
	 * Segunda ventana del Wizard de Plugin, donde se configura las propiedades genéricas del mantenimiento
	 * @param selection
	 */
	public NewMaintWizardPageTwo(ISelection selection) {
		super("wizardPage");

		setTitle("Generar nuevo mantenimiento para una aplicación");
		setDescription("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
	}

	/**
	 * Creación de controles de la ventana
	 * @param parent - controlador padre
	 */
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(4, true));
		container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		// Descripción de la operación
		Label descLabel= new Label(container, SWT.NONE);
		descLabel.setText("Rellene los campos con las propiedades del mantenimiento");
		descLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		// Salto de línea
		Label hiddenLabel= new Label(container, SWT.NONE);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		// Campo nombre del mantenimiento
		Label nameMaintLabel = new Label(container, SWT.NONE);
		nameMaintLabel.setText("&Nombre del mantenimiento:");
		nameMaintLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		nameMaintText = new Text(container, SWT.BORDER | SWT.SINGLE);
		nameMaintText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		nameMaintText.setToolTipText("Nombre del mantenimiento");
		nameMaintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		// Label Oculto
		hiddenLabel= new Label(container, SWT.NONE);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// Campo title del mantenimiento
		Label titleMaintLabel = new Label(container, SWT.NONE);
		titleMaintLabel.setText("Título del mantenimiento:");
		titleMaintLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		titleMaintText = new Text(container, SWT.BORDER | SWT.SINGLE);
		titleMaintText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		titleMaintText.setToolTipText("Título del mantenimiento que se enseñará en pantalla");
		titleMaintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		// Label Oculto
		hiddenLabel= new Label(container, SWT.NONE);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// Salto de línea
		hiddenLabel= new Label(container, SWT.NONE);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		// Pestañas de Características generales y eventos
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));
		
		TabItem generalFeaturesTab = new TabItem(tabFolder, SWT.NONE);
		generalFeaturesTab.setText("Características generales");
		generalFeaturesTab.setToolTipText("Características generales del mantenimiento");
		
		TabItem eventsTab = new TabItem(tabFolder, SWT.NONE);
		eventsTab.setText("Eventos");
		eventsTab.setToolTipText("Eventos del mantenimiento");
		
		// Contenedor del tab de Características generales
		Composite containerGeneralFeaturesTab = new Composite(tabFolder, SWT.NONE);
		containerGeneralFeaturesTab.setLayout(new GridLayout(5, false));
		containerGeneralFeaturesTab.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		/////////////////////////////////////
		// Grupo de detalle del mantenimiento
		/////////////////////////////////////
		Group detailMaintGroup = new Group(containerGeneralFeaturesTab, SWT.NONE);
		detailMaintGroup.setText("Formulario de Detalle");
		detailMaintGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5 ,1));
		detailMaintGroup.setLayout(new GridLayout(5, false));
		
		Label detailMaintCheckLabel = new Label(detailMaintGroup, SWT.NONE);
		detailMaintCheckLabel.setText("Formulario automático de detalle:     ");
		detailMaintCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));
		
		// Check box de Generación de detalle para el mantenimiento
		detailMaintCheck = new Button(detailMaintGroup, SWT.CHECK);
		detailMaintCheck.setSelection(true);
		detailMaintCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	// Limpia el campo y cuando checkeado se visualiza el campo de texto
            	detailMaintText.setText("");
				if (detailMaintCheck.getSelection()) {
					detailMaintText.setVisible(false);
				} else {
					detailMaintText.setVisible(true);
				}
            }
        });
		detailMaintCheck.setToolTipText("Activa o no la Generación automática del formulario de detalle");
		detailMaintCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));

		// Campo texto del nombre de la propiedad del detalle del mantenimiento
		detailMaintText = new Text(detailMaintGroup, SWT.BORDER | SWT.SINGLE);
		detailMaintText.setEnabled(true);
		detailMaintText.setToolTipText("Id del formulario de detalle del mantenimiento cuando su Generación no sea automática");
		detailMaintText.setVisible(false);
		detailMaintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));
		detailMaintText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		
		Label detailMaintButtonsComboLabel = new Label(detailMaintGroup, SWT.NULL);
		detailMaintButtonsComboLabel.setText("Tipología de botones:");
		detailMaintButtonsComboLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));
		
		detailMaintButtonsCombo = new Combo(detailMaintGroup, SWT.READ_ONLY);
		detailMaintButtonsCombo.add("SAVE");
		detailMaintButtonsCombo.add("SAVE_REPEAT");
		detailMaintButtonsCombo.select(0);
		detailMaintButtonsCombo.setToolTipText("Propiedad que indica la tipología de botones que se crearan en el formulario de detalle." +
				"\nSAVE: Se crearán dos botones: guardar; cancelar." + 
				"\nSAVE_REPEAT: Se crearán tres botones: guardar; guardar y repetir; cancelar.");
		detailMaintButtonsCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2 ,1));
		
		///////////////////////////////////////
		// Grupo de Búsqueda del mantenimiento
		///////////////////////////////////////	
		Group searchMaintGroup = new Group(containerGeneralFeaturesTab, SWT.NONE);
		searchMaintGroup.setText("Formulario de Búsqueda");
		searchMaintGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5 ,1));
		searchMaintGroup.setLayout(new GridLayout(5, false));

		Label searchMaintCheckLabel = new Label(searchMaintGroup, SWT.NULL);
		searchMaintCheckLabel.setText("Formulario automático de búsqueda:");
		searchMaintCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));
		
		// Check box de Generación del formulario de automático de búsqueda del mantenimiento
		searchMaintCheck = new Button(searchMaintGroup, SWT.CHECK);
		searchMaintCheck.setSelection(true);
		searchMaintCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	searchMaintText.setText("");
				if (searchMaintCheck.getSelection()) {
					searchMaintText.setVisible(false);
				} else {
					searchMaintText.setVisible(true);
				}
            }
        });
		searchMaintCheck.setToolTipText("Activa o no la Generación del formulario automático de búsquedas");
		searchMaintCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));
		
		// Campo texto del nombre de la propiedad del formulario de búsqueda del mantenimiento
		searchMaintText = new Text(searchMaintGroup, SWT.BORDER | SWT.SINGLE);
		searchMaintText.setEnabled(true);
		searchMaintText.setToolTipText("Id del formulario de búsquedas");
		searchMaintText.setVisible(false);
		searchMaintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));
		searchMaintText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		
		///////////////////////////////////////
		// Grupo de Toolbar del mantenimiento
		///////////////////////////////////////		
		Group toolbarMaintGroup = new Group(containerGeneralFeaturesTab, SWT.NONE);
		toolbarMaintGroup.setText("Botonera");
		toolbarMaintGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5 ,1));
		toolbarMaintGroup.setLayout(new GridLayout(5, false));
		
		Label toolbarMaintCheckLabel = new Label(toolbarMaintGroup, SWT.NULL);
		toolbarMaintCheckLabel.setText("Botonera automática:                        ");
		toolbarMaintCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));

		// Check box de Generación de la toolbar del mantenimiento
		toolbarMaintCheck = new Button(toolbarMaintGroup, SWT.CHECK);
		toolbarMaintCheck.setSelection(true);
		toolbarMaintCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	toolbarMaintText.setText("");
				if (toolbarMaintCheck.getSelection()) {
					toolbarMaintText.setVisible(false);
					toolbarMaintButtonsDefaultCheck.setEnabled(false);
					toolbarMaintButtonsDefaultCheck.setSelection(true);
				} else {
					toolbarMaintText.setVisible(true);
					toolbarMaintButtonsDefaultCheck.setEnabled(true);
				}
            }
        });
		toolbarMaintCheck.setToolTipText("Activa o no la Generación automática de la botonera");
		toolbarMaintCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));

		// Campo texto del nombre de la propiedad de toolbar del mantenimiento
		toolbarMaintText = new Text(toolbarMaintGroup, SWT.BORDER | SWT.SINGLE);
		toolbarMaintText.setEnabled(true);
		toolbarMaintText.setToolTipText("Id de la capa que contendrá la toolbar en el mantenimiento");
		toolbarMaintText.setVisible(false);
		toolbarMaintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));
		toolbarMaintText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		
		Label toolbarMaintButtonsDefaultCheckLabel = new Label(toolbarMaintGroup, SWT.NULL);
		toolbarMaintButtonsDefaultCheckLabel.setText("Botones por defecto:");
		toolbarMaintButtonsDefaultCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));
		
		// Check box para insertar los botones por defecto de la botonera
		toolbarMaintButtonsDefaultCheck = new Button(toolbarMaintGroup, SWT.CHECK);
		toolbarMaintButtonsDefaultCheck.setSelection(true);
		toolbarMaintButtonsDefaultCheck.setEnabled(false);
		toolbarMaintButtonsDefaultCheck.setToolTipText("Indica si se van a crear los botones por defecto en la toolbar, ya bien sea la creada por el desarrollador o bien las creada automáticamente por el patrón");
		toolbarMaintButtonsDefaultCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2 ,1));
		
		Label toolbarMaintAutoSizeCheckLabel = new Label(toolbarMaintGroup, SWT.NULL);
		toolbarMaintAutoSizeCheckLabel.setText("Autoajuste:");
		toolbarMaintAutoSizeCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));
		
		// Check box para autoajuste de la botonera
		toolbarMaintAutoSizeCheck = new Button(toolbarMaintGroup, SWT.CHECK);
		toolbarMaintAutoSizeCheck.setSelection(true);
		toolbarMaintAutoSizeCheck.setToolTipText("Indica si se va a ajustar el tamaño de la toolbar al tamaño del grid");
		toolbarMaintAutoSizeCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2 ,1));
		
		///////////////////////////////////////
		// Grupo de FeedBack del mantenimiento
		///////////////////////////////////////
		Group feedbackMaintGroup = new Group(containerGeneralFeaturesTab, SWT.NONE);
		feedbackMaintGroup.setText("Mensajes");
		feedbackMaintGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5 ,1));
		feedbackMaintGroup.setLayout(new GridLayout(5, false));
		
		Label feedbackMaintCheckLabel = new Label(feedbackMaintGroup, SWT.NULL);
		feedbackMaintCheckLabel.setText("FeedBack por defecto:                      ");
		feedbackMaintCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));

		// Check box de Generación del feedback del mantenimiento
		feedbackMaintCheck = new Button(feedbackMaintGroup, SWT.CHECK);
		feedbackMaintCheck.setSelection(true);
		feedbackMaintCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	feedbackMaintText.setText("");
				if (feedbackMaintCheck.getSelection()) {
					feedbackMaintText.setVisible(false);
				} else {
					feedbackMaintText.setVisible(true);
				}
            }
        });
		feedbackMaintCheck.setToolTipText("Activa o no la Generación automática de la mensajería");
		feedbackMaintCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1 ,1));

		// Campo texto del nombre de la propiedad de feedback del mantenimiento
		feedbackMaintText = new Text(feedbackMaintGroup, SWT.BORDER | SWT.SINGLE);
		feedbackMaintText.setEnabled(true);
		feedbackMaintText.setToolTipText("Id de la capa donde se mostrará los mensajes generados por el mantenimiento");
		feedbackMaintText.setVisible(false);
		feedbackMaintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));
		feedbackMaintText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		
		Label feedbackMaintShowAllCheckLabel = new Label(feedbackMaintGroup, SWT.NULL);
		feedbackMaintShowAllCheckLabel.setText("Mostrar todos:");
		feedbackMaintShowAllCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));

		// Check box para mostrar todos los mensajes
		feedbackMaintShowAllCheck = new Button(feedbackMaintGroup, SWT.CHECK);
		feedbackMaintShowAllCheck.setSelection(true);
		feedbackMaintShowAllCheck.setToolTipText("Indica si se mostraran las advertencias");
		feedbackMaintShowAllCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2 ,1));
		
		Label feedbackMaintCollapsibleCheckLabel = new Label(feedbackMaintGroup, SWT.NULL);
		feedbackMaintCollapsibleCheckLabel.setText("Feedback plegable:");
		feedbackMaintCollapsibleCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));

		// Check box para mostrar todos los mensajes en formato plegable
		feedbackMaintCollapsibleCheck = new Button(feedbackMaintGroup, SWT.CHECK);
		feedbackMaintCollapsibleCheck.setSelection(false);
		feedbackMaintCollapsibleCheck.setToolTipText("Indica si se van a mostrar los mensajes de OK si las acciones de Añadir, modificar o borrar se han realizado con éxito");
		feedbackMaintCollapsibleCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2 ,1));
		
		Label imgPathMaintTextLabel = new Label(containerGeneralFeaturesTab, SWT.NULL);
		imgPathMaintTextLabel.setText("Ruta de imágenes:                               ");
		imgPathMaintTextLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));

		// Campo texto con la ruta de la imagen del mantenimiento
		imgPathMaintText = new Text(containerGeneralFeaturesTab, SWT.BORDER | SWT.SINGLE);
		imgPathMaintText.setText("/rup/basic-theme/images");
		imgPathMaintText.setToolTipText("Ruta donde están las imágenes del grid");
		imgPathMaintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1 ,1));
		imgPathMaintText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		
		Label validationModeMaintCheckLabel = new Label(containerGeneralFeaturesTab, SWT.NULL);
		validationModeMaintCheckLabel.setText("Validación por campo:");
		validationModeMaintCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));

		// Check box para la ruta de imagenes
		validationModeMaintCheck = new Button(containerGeneralFeaturesTab, SWT.CHECK);
		validationModeMaintCheck.setSelection(true);
		validationModeMaintCheck.setToolTipText("Validación de los campos del formulario de forma individual a la hora de perder el foco");
		validationModeMaintCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2 ,1));

		Label detailServerMaintCheckLabel = new Label(containerGeneralFeaturesTab, SWT.NULL);
		detailServerMaintCheckLabel.setText("Recupera detalle desde servidor:");
		detailServerMaintCheckLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3 ,1));

		// Check box para la ruta de imágenes
		detailServerMaintCheck = new Button(containerGeneralFeaturesTab, SWT.CHECK);
		detailServerMaintCheck.setSelection(true);
		detailServerMaintCheck.setToolTipText("Indica si se mostrarán en el detalle todos los datos de la entidad");
		detailServerMaintCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2 ,1));
		
		//Añade el contenedor a la pestaña de Características generales
		generalFeaturesTab.setControl(containerGeneralFeaturesTab);
		
		////////////////////////////////
		// Contenedor del tab de eventos
		////////////////////////////////
		Composite containerEventsTab = new Composite(tabFolder, SWT.NONE);

		containerEventsTab.setLayout(new GridLayout(5, false));
		containerEventsTab.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		containerEventsTab.setSize(50, 50);
		
		// Generación del evento onbeforeDetailShow
		Label eventOnbeforeDetailShowLabel = new Label(containerEventsTab, SWT.NULL);
		eventOnbeforeDetailShowLabel.setText("onbeforeDetailShow:");
		eventOnbeforeDetailShowLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2 ,1));
		
		// Campo texto del nombre de la funcion del evento onbeforeDetailShow
		eventOnbeforeDetailShowText = new Text(containerEventsTab, SWT.BORDER | SWT.SINGLE);
		eventOnbeforeDetailShowText.setEnabled(true);
		eventOnbeforeDetailShowText.setToolTipText("Nombre de la Función que se ejecuta antes de enseóar el formulario de edición");
		eventOnbeforeDetailShowText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3 ,1));
		eventOnbeforeDetailShowText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		
		// Generación del evento onafterDetailShow
		Label eventOnafterDetailShowLabel = new Label(containerEventsTab, SWT.NULL);
		eventOnafterDetailShowLabel.setText("onafterDetailShow:");
		eventOnafterDetailShowLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2 ,1));
		
		// Campo texto del nombre de la funcion del evento onafterDetailShow
		eventOnafterDetailShowText = new Text(containerEventsTab, SWT.BORDER | SWT.SINGLE);
		eventOnafterDetailShowText.setEnabled(true);
		eventOnafterDetailShowText.setToolTipText("Nombre de la Función que se ejecuta después de mostrar el formulario de edición y de que el patrón haya realizado todas sus acciones");
		eventOnafterDetailShowText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3 ,1));
		eventOnafterDetailShowText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		
		//Añade el contenedor a la pestaña de eventos
		eventsTab.setControl(containerEventsTab);
        setControl(container);
	}
	
	/**
	 * Habilita el botón de Next si cumple la Validación
	 * 
	 * return true para habilitar el botón, false ecc
	 */
	public boolean canFlipToNextPage() {
		return validatePage();
	}
	
	/**
	 * Verifica si al pinchar en el botón Next puede pasar a la siguiente pantalla
	 * 
	 * @return página a direccionar
	 */
	public IWizardPage getNextPage() {
		
		if (Utilities.isBlank(getNameMaintText())){
			setErrorMessage("El campo 'Nombre del mantenimiento' obligatorio");
			return getWizard().getContainer().getCurrentPage();
		}
		
		if (Utilities.isBlank(getTitleMaintText())){
			setErrorMessage("El campo 'Título del mantenimiento' obligatorio");
			return getWizard().getContainer().getCurrentPage();
		}
		
		if (!getDetailMaintCheck() && Utilities.isBlank(getDetailMaintText())){
			setErrorMessage("El campo 'Formulario automático de detalle' obligatorio si su check está deseleccionado");
			return getWizard().getContainer().getCurrentPage();
		}

		if (!getSearchMaintCheck() && Utilities.isBlank(getSearchMaintText())){
			setErrorMessage("El campo 'Formulario de búsqueda automático' obligatorio si su check está seleccionado");
			return getWizard().getContainer().getCurrentPage();
		}

		if (!getToolbarMaintCheck() && Utilities.isBlank(getToolbarMaintText())){
			setErrorMessage("El campo 'Botonera automática' obligatorio si su check está deseleccionado");
			return getWizard().getContainer().getCurrentPage();
		}
		
		if (!getFeedbackMaintCheck() && Utilities.isBlank(getFeedbackMaintText())){
			setErrorMessage("El campo 'FeedBack por defecto' obligatorio si su check está deseleccionado");
			return getWizard().getContainer().getCurrentPage();
		}
    	
    	return super.getNextPage();	
	}
	
	/*************/
	/*  Getters  */
	/*************/
	
	/**
	 * Recupera el nombre del mantenimiento
	 * 
	 * @return nombre del mantenimiento
	 */
	public String getNameMaintText() {
		if (nameMaintText != null) {
			return nameMaintText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el Título del mantenimiento
	 * 
	 * @return Título del mantenimiento
	 */
	public String getTitleMaintText() {
		if (titleMaintText != null) {
			return titleMaintText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el valor del check de formulario de detalle automático
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getDetailMaintCheck() {
		return detailMaintCheck.getSelection();
	}
	
	/**
	 * Recupera el nombre del formulario de detalle
	 * 
	 * @return nombre del formulario de detalle
	 */
	public String getDetailMaintText() {
		if (detailMaintText != null) {
			return detailMaintText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el valor seleccionado en la combo de tipologia de botones
	 * 
	 * @return valor de la combo
	 */
	public String getDetailMaintButtonsCombo() {
		if (detailMaintButtonsCombo != null) {
			return detailMaintButtonsCombo.getText();
		} else {
			return "";
		}
	}

	/**
	 * Recupera el valor del check del formulario automatico de búsqueda
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getSearchMaintCheck() {
		return searchMaintCheck.getSelection();
	}
	
	/**
	 * Recupera el nombre del formulario de búsqueda
	 * 
	 * @return nombre del formulario de búsqueda
	 */
	public String getSearchMaintText() {
		if (searchMaintText != null) {
			return searchMaintText.getText();
		} else {
			return "";
		}
	}

	/**
	 * Recupera el valor del check de la toolbar
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getToolbarMaintCheck() {
		return toolbarMaintCheck.getSelection();
	}
	
	/**
	 * Recupera el nombre de la toolbar
	 * 
	 * @return nombre de la toolbar
	 */
	public String getToolbarMaintText() {
		if (toolbarMaintText != null) {
			return toolbarMaintText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el valor del check de autoajuste de la toolbar
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getToolbarMaintAutoSizeCheck() {
		return toolbarMaintAutoSizeCheck.getSelection();
	}
	
	/**
	 * Recupera el valor del check de botones por defecto de la toolbar
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getToolbarMaintButtonsDefaultCheck() {
		return toolbarMaintButtonsDefaultCheck.getSelection();
	}
	
	/**
	 * Recupera el valor del check de mesajería (feed back)
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getFeedbackMaintCheck() {
		return feedbackMaintCheck.getSelection();
	}
	
	/**
	 * Recupera el nombre del feedback
	 * 
	 * @return nombre del feedback
	 */
	public String getFeedbackMaintText() {
		if (feedbackMaintText != null) {
			return feedbackMaintText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el valor del check Mostrar todos los mensajes de mesajería (feed back)
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getFeedbackMaintShowAllCheck() {
		return feedbackMaintShowAllCheck.getSelection();
	}
	
	/**
	 * Recupera el valor del check Feedback plegable
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getFeedbackMaintCollapsibleCheck() {
		return feedbackMaintCollapsibleCheck.getSelection();
	}
	
	
	/**
	 * Recupera el nombre del campo de imgPath
	 * 
	 * @return imgPathMaintText
	 */
	public String getImgPathMaintText() {
		if (imgPathMaintText != null) {
			return imgPathMaintText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el valor del check Validation Mode
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getValidationModeMaintCheck() {
		return validationModeMaintCheck.getSelection();
	}
	
	/**
	 * Recupera el valor del check detailServer
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getDetailServerMaintCheck() {
		return detailServerMaintCheck.getSelection();
	}
	
	/**
	 * Recupera el nombre del evento de onbeforeDetailShow
	 * 
	 * @return nombre del evento de onbeforeDetailShow
	 */
	public String getEventOnbeforeDetailShowText() {
		if (eventOnbeforeDetailShowText != null) {
			return eventOnbeforeDetailShowText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre del evento de onafterDetailShow
	 * 
	 * @return nombre del evento de onafterDetailShow
	 */
	public String getEventOnafterDetailShowText() {
		if (eventOnafterDetailShowText != null) {
			return eventOnafterDetailShowText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera los datos del mantenimiento
	 * 
	 * @return objecto de mantenimiento
	 */
	public Maint getMaint() {
		
		Maint maint = new Maint();
		
		// Recupera las propiedades
		maint.setNameMaint(getNameMaintText());
		maint.setTitleMaint(getTitleMaintText());
		maint.setDetailMaint(getDetailMaintText());
		maint.setDetailMaintButtons(getDetailMaintButtonsCombo());
		maint.setSearchMaint(getSearchMaintText(), getSearchMaintCheck());
		maint.setToolbarMaint(getToolbarMaintText());
		maint.setToolbarMaintAutoSize(getToolbarMaintAutoSizeCheck());
		maint.setToolbarMaintButtonsDefault(getToolbarMaintButtonsDefaultCheck());
		maint.setFeedbackMaint(getFeedbackMaintText());
		maint.setFeedbackMaintShowAll(getFeedbackMaintShowAllCheck());
		maint.setFeedbackMaintCollapsible(getFeedbackMaintCollapsibleCheck());
		maint.setImgPathMaint(getImgPathMaintText());
		maint.setValidationModeMaint(getValidationModeMaintCheck());
		maint.setDetailServerMaint(getDetailServerMaintCheck());

		// Recupera los eventos
		maint.setEventOnafterDetailShow(getEventOnafterDetailShowText());
		maint.setEventOnbeforeDetailShow(getEventOnbeforeDetailShowText());
		
		return maint;
	}
	
	/**********************/
	/*  Métodos privados  */
	/**********************/
	
    /**
     * Valida los campos de la pantalla
     * 
     * @return true si todos los controles están validados, false si algún campo no es válido.
     */
    protected boolean validatePage() {
    	
    	setErrorMessage(null);
    	
    	if (!Utilities.isBlank(getNameMaintText()) && !Utilities.validateText(getNameMaintText())) {
			setErrorMessage("Caracteres no válidos para en campo 'Nombre del mantenimiento'");
			return false;
		}
    	
    	if (!getDetailMaintCheck() && !Utilities.isBlank(getDetailMaintText()) && !Utilities.validateText(getDetailMaintText())){
    		setErrorMessage("Caracteres no válidos para en campo 'Formulario automático de detalle'");
			return false;
    	}
    	
    	if (getSearchMaintCheck() && !Utilities.isBlank(getSearchMaintText()) && !Utilities.validateText(getSearchMaintText())){
    		setErrorMessage("Caracteres no válidos para en campo 'Formulario de búsqueda'");
			return false;
    	}
    	
    	if (!getToolbarMaintCheck() && !Utilities.isBlank(getToolbarMaintText()) && !Utilities.validateText(getToolbarMaintText())){
    		setErrorMessage("Caracteres no válidos para en campo 'Botonera automática'");
			return false;
    	}
    	
    	if (!getFeedbackMaintCheck() && !Utilities.isBlank(getFeedbackMaintText()) && !Utilities.validateText(getFeedbackMaintText())){
    		setErrorMessage("Caracteres no válidos para en campo 'FeedBack por defecto'");
			return false;
    	}
    	
    	if (!Utilities.isBlank(getEventOnafterDetailShowText()) && !Utilities.validateText(getEventOnafterDetailShowText())) {
			setErrorMessage("Caracteres no válidos para en campo 'onafterDetailShow'");
			return false;
		}
    	
    	if (!Utilities.isBlank(getEventOnbeforeDetailShowText()) && !Utilities.validateText(getEventOnbeforeDetailShowText())) {
			setErrorMessage("Caracteres no válidos para en campo 'onbeforeDetailShow'");
			return false;
		}
    	
        setErrorMessage(null);
        setMessage("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
        return true;
    }
    
}