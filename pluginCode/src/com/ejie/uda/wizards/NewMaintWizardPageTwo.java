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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Maint;
import com.ejie.uda.utils.Utilities;

/**
 * Clase que define la segunda pantalla del asistente "Generar mantenimiento"
 */
public class NewMaintWizardPageTwo extends WizardPage {

	// Propiedades utilizadas en la pantalla
	private Text nameMaintText;
	private Text titleMaintText;
	private Button btnMaintButton;
	private Button btnEdicinEnLnea;
	private Button btnFormularioDetalle;
	private Button btnRecuperarDetalleDesde;
	private Combo detailMaintButtonsCombo;
	private Label detailMaintButtonsComboLabel;
	private Button feedbackMaintCollapsibleCheck;
	private Button toolbarMaintCheck;
	private Button btnFiltrado;
	private Button btnMenuContextual;
	private Button btnBusqueda;
	private Button btnValidacionesCliente;
	private Button btnMultiseleccion;
	private Button btnJerarquia;

	/**
	 * Segunda ventana del Wizard de Plugin, donde se configura las propiedades
	 * genéricas del mantenimiento
	 * 
	 * @param selection
	 */
	public NewMaintWizardPageTwo(ISelection selection) {
		super("wizardPage");

		setTitle("Generar nuevo mantenimiento para una aplicación");
		setDescription("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
	}

	/**
	 * Creación de controles de la ventana
	 * 
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
		nameMaintLabel.setText("Nombre del mantenimiento:");

		nameMaintText = new Text(container, SWT.BORDER | SWT.SINGLE);
		nameMaintText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		nameMaintText.setToolTipText("Nombre del mantenimiento");
		nameMaintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		// Campo title del mantenimiento
		Label titleMaintLabel = new Label(container, SWT.NONE);
		titleMaintLabel.setText("Título del mantenimiento:");

		titleMaintText = new Text(container, SWT.BORDER | SWT.SINGLE);
		titleMaintText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		});
		titleMaintText.setToolTipText("Título del mantenimiento que se enseñará en pantalla");
		titleMaintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		// Salto de línea
		hiddenLabel= new Label(container, SWT.NONE);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		// Mantenimiento o tabla de datos
		Label detailMaintCheckLabel = new Label(container, SWT.NONE);
		detailMaintCheckLabel.setText("Mantenimiento:     ");
		
		btnMaintButton = new Button(container, SWT.CHECK);
		btnMaintButton.setToolTipText("Se creará un mantenimiento completo, o una tabla de visualización de datos si no se marca esta opción");
		GridData gd_btnCheckButton = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_btnCheckButton.heightHint = 18;
		btnMaintButton.setLayoutData(gd_btnCheckButton);
		btnMaintButton.setSelection(true);
		
		btnMaintButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnMaintButton.getSelection()) {
					btnEdicinEnLnea.setEnabled(true);
					btnFormularioDetalle.setEnabled(true);

					btnEdicinEnLnea.setSelection(true);
				} else {
					btnEdicinEnLnea.setEnabled(false);
					btnFormularioDetalle.setEnabled(false);
					btnRecuperarDetalleDesde.setEnabled(false);
					detailMaintButtonsComboLabel.setEnabled(false);
					detailMaintButtonsCombo.setEnabled(false);
					
					btnEdicinEnLnea.setSelection(false);
					btnFormularioDetalle.setSelection(false);
				}
			}
		});
		
		// Tipo de Mantenimiento
		// Edición en línea
		Label maintTypeLabel = new Label(container, SWT.NONE);
		maintTypeLabel.setText("Tipo de Mantenimiento:     ");

		btnEdicinEnLnea = new Button(container, SWT.RADIO);
		btnEdicinEnLnea.setToolTipText("El tipo de mantenimiento será de 'Edición en línea'");
		btnEdicinEnLnea.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnEdicinEnLnea.setText("Edición en línea");
		btnEdicinEnLnea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnMaintButton.getSelection()) {
					btnRecuperarDetalleDesde.setEnabled(false);
					detailMaintButtonsComboLabel.setEnabled(false);
					detailMaintButtonsCombo.setEnabled(false);
				}
			}
		});

		// Formulario de detalle
		hiddenLabel= new Label(container, SWT.NONE);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		btnFormularioDetalle = new Button(container, SWT.RADIO);
		btnFormularioDetalle.setToolTipText("El tipo de mantenimiento será con 'Formulario de detalle'");
		btnFormularioDetalle.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnFormularioDetalle.setText("Formulario de detalle");
		btnFormularioDetalle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnMaintButton.getSelection()) {
					btnRecuperarDetalleDesde.setEnabled(true);
					detailMaintButtonsComboLabel.setEnabled(true);
					detailMaintButtonsCombo.setEnabled(true);
				}
			}
		});
		btnFormularioDetalle.setSelection(true);
		
		// Recuperar datos de detalle desde el servidor
		hiddenLabel= new Label(container, SWT.NONE);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		btnRecuperarDetalleDesde = new Button(container, SWT.CHECK);
		btnRecuperarDetalleDesde.setToolTipText("Recuperar datos de detalle desde el servidor");
		GridData gd_btnRecuperarDetalleDesde = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_btnRecuperarDetalleDesde.horizontalIndent = 20;
		btnRecuperarDetalleDesde.setLayoutData(gd_btnRecuperarDetalleDesde);
		btnRecuperarDetalleDesde.setText("Recuperar datos de detalle desde servidor");
		btnRecuperarDetalleDesde.setEnabled(false);
		btnRecuperarDetalleDesde.setSelection(true);
		
		// Botones a incluir en el formulario de detalle
		hiddenLabel= new Label(container, SWT.NONE);
		hiddenLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		detailMaintButtonsComboLabel = new Label(container, SWT.NULL);
		detailMaintButtonsComboLabel.setToolTipText("Botones a incluir en el formulario de detalle");
		GridData gd_detailMaintButtonsComboLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_detailMaintButtonsComboLabel.horizontalIndent = 20;
		detailMaintButtonsComboLabel.setLayoutData(gd_detailMaintButtonsComboLabel);
		detailMaintButtonsComboLabel.setText("Tipología de botones:");
		detailMaintButtonsComboLabel.setEnabled(false);

		detailMaintButtonsCombo = new Combo(container, SWT.READ_ONLY);
		//gd_detailMaintButtonsCombo.heightHint = 21;
		detailMaintButtonsCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		detailMaintButtonsCombo.add(Constants.SAVE);
		detailMaintButtonsCombo.add(Constants.SAVE_REPEAT);
		detailMaintButtonsCombo.select(0);
		detailMaintButtonsCombo
				.setToolTipText("Propiedad que indica la tipología de botones que se crearan en el formulario de detalle."
						+ "\nSAVE: Se crearán dos botones: Guardar; Cancelar."
						+ "\nSAVE_REPEAT: Se crearán tres botones: Guardar; Guardar y repetir; Cancelar.");
		detailMaintButtonsCombo.setEnabled(true);
		btnRecuperarDetalleDesde.setEnabled(true);
		detailMaintButtonsComboLabel.setEnabled(true);
		detailMaintButtonsCombo.setEnabled(true);
		
		
		// Check box para mostrar todos los mensajes en formato plegable
//		Label feedbackMaintCollapsibleCheckLabel = new Label(container, SWT.NONE);
//		feedbackMaintCollapsibleCheckLabel.setText("Feedback plegable:     ");
//
//		feedbackMaintCollapsibleCheck = new Button(container, SWT.CHECK);
//		GridData gd_feedbackMaintCollapsibleCheck = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
//		gd_feedbackMaintCollapsibleCheck.heightHint = 18;
//		feedbackMaintCollapsibleCheck.setLayoutData(gd_feedbackMaintCollapsibleCheck);
//		feedbackMaintCollapsibleCheck.setSelection(true);
//		feedbackMaintCollapsibleCheck
//				.setToolTipText("Los mensajes de feedback para las acciones de añadir, modificar y borrar desaparecerán de manera automática");
		
		// Check box de Generación de la toolbar del mantenimiento
		Label toolbarMaintCheckLabel = new Label(container, SWT.NONE);
		toolbarMaintCheckLabel.setText("Botonera:    ");

		toolbarMaintCheck = new Button(container, SWT.CHECK);
		GridData gd_toolbarMaintCheck = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_toolbarMaintCheck.heightHint = 18;
		toolbarMaintCheck.setLayoutData(gd_toolbarMaintCheck);
		toolbarMaintCheck.setSelection(true);
		toolbarMaintCheck.setToolTipText("Se generará la botonera asociada a la tabla");

		
		// Se generará un menú contextual que muestra las posibles acciones a realizar sobre los registros
		Label btnMenuContextualLabel = new Label(container, SWT.NONE);
		btnMenuContextualLabel.setText("Menú contextual:    ");

		btnMenuContextual = new Button(container, SWT.CHECK);
		btnMenuContextual.setToolTipText("Se generará un menú contextual que muestra las posibles acciones a realizar sobre los registros");
		GridData gd_btnMenuContextual = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_btnMenuContextual.heightHint = 18;
		btnMenuContextual.setLayoutData(gd_btnMenuContextual);
		btnMenuContextual.setSelection(true);
		
		// Se permitirá el filtrado de datos
		Label btnFiltradoLabel = new Label(container, SWT.NONE);
		btnFiltradoLabel.setText("Filtrado de datos:    ");

		btnFiltrado = new Button(container, SWT.CHECK);
		btnFiltrado.setToolTipText("Se permitirá el filtrado de datos");
		GridData gd_btnFiltrado = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_btnFiltrado.heightHint = 18;
		btnFiltrado.setLayoutData(gd_btnFiltrado);
		btnFiltrado.setSelection(true);

		// Se incluirá formulario de búsqueda
		Label btnBusquedaLabel = new Label(container, SWT.NONE);
		btnBusquedaLabel.setText("Búsqueda:    ");

		btnBusqueda = new Button(container, SWT.CHECK);
		btnBusqueda.setToolTipText("Se incluirá formulario de búsqueda");
		GridData gd_btnBsqueda = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_btnBsqueda.heightHint = 18;
		btnBusqueda.setLayoutData(gd_btnBsqueda);
		btnBusqueda.setSelection(true);

		// Se configurarán validaciones en cliente
		Label btnValidacionesClienteLabel = new Label(container, SWT.NONE);
		btnValidacionesClienteLabel.setText("Validaciones cliente:    ");

		btnValidacionesCliente = new Button(container, SWT.CHECK);
		btnValidacionesCliente.setToolTipText("Se configurarán validaciones en cliente");
		GridData gd_btnValidacionesCliente = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_btnValidacionesCliente.heightHint = 18;
		btnValidacionesCliente.setLayoutData(gd_btnValidacionesCliente);
		btnValidacionesCliente.setSelection(true);

		// Se permitirá selección de múltiples filas
		Label btnMultiseleccionLabel = new Label(container, SWT.NONE);
		btnMultiseleccionLabel.setText("Multiselección:    ");

		btnMultiseleccion = new Button(container, SWT.CHECK);
		btnMultiseleccion.setToolTipText("Se permitirá selección de múltiples filas");
		GridData gd_btnMultiseleccin = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_btnMultiseleccin.heightHint = 18;
		btnMultiseleccion.setLayoutData(gd_btnMultiseleccin);

		// La tabla podrá presentar datos con formato jerárquico (padre-hijo)
//		Label btnJerarquiaLabel = new Label(container, SWT.NONE);
//		btnJerarquiaLabel.setText("Jerarquía:    ");
//
//		btnJerarquia = new Button(container, SWT.CHECK);
//		btnJerarquia.setToolTipText("La tabla podrá presentar datos con formato jerárquico (padre-hijo)");
//		GridData gd_btnJerarqua = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
//		gd_btnJerarqua.heightHint = 18;
//		btnJerarquia.setLayoutData(gd_btnJerarqua);

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
	 * Verifica si al pinchar en el botón Next puede pasar a la siguiente
	 * pantalla
	 * 
	 * @return página a direccionar
	 */
	public IWizardPage getNextPage() {

		if (Utilities.isBlank(getNameMaintText())) {
			setErrorMessage("El campo 'Nombre del mantenimiento' es obligatorio");
			return getWizard().getContainer().getCurrentPage();
		}

		if (Utilities.isBlank(getTitleMaintText())) {
			setErrorMessage("El campo 'Título del mantenimiento' es obligatorio");
			return getWizard().getContainer().getCurrentPage();
		}

		return super.getNextPage();
	}

	/*************/
	/* Getters */
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
	 * Recupera el valor del check Mantenimiento
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getMaintButtonCheck() {
		return btnMaintButton.getSelection();
	}

	/**
	 * Recupera el valor del tipo de Mantenimiento
	 * 
	 * @return INLINE o DETAIL
	 */
	public String getTypeMaint() {

		if (btnEdicinEnLnea.getSelection()){
			return Constants.INLINE;
		}
		if (btnFormularioDetalle.getSelection()){
			return Constants.DETAIL;
		}
		return Constants.DATA_TABLE;
	}

	/**
	 * Recupera el valor del check Recuperar detalle dede servidor
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getBtnRecuperarDetalleDesdeCheck() {
		return btnRecuperarDetalleDesde.getSelection();
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
	 * Recupera el valor del check de la toolbar
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getToolbarMaintCheck() {
		return toolbarMaintCheck.getSelection();
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
	 * Recupera el valor del check Menú contexual
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getBtnMenuContextualCheck() {
		return btnMenuContextual.getSelection();
	}
	
	/**
	 * Recupera el valor del check Filtrado de datos
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getBtnFiltradoCheck() {
		return btnFiltrado.getSelection();
	}


	/**
	 * Recupera el valor del check Búsqueda
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getBtnBusquedaCheck() {
		return btnBusqueda.getSelection();
	}

	/**
	 * Recupera el valor del check Validaciones Cliente
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getBtnValidacionesClienteCheck() {
		return btnValidacionesCliente.getSelection();
	}

	/**
	 * Recupera el valor del check Multiselección
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getBtnMultiseleccionCheck() {
		return btnMultiseleccion.getSelection();
	}
	/**
	 * Recupera el valor del check Jerarquía
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getBtnJerarquiaCheck() {
		return btnJerarquia.getSelection();
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
		maint.setIsMaint(getMaintButtonCheck());
		maint.setTypeMaint(getTypeMaint());
		maint.setDetailServerMaint(getBtnRecuperarDetalleDesdeCheck());
		maint.setDetailMaintButtons(getDetailMaintButtonsCombo());
//		maint.setFeedbackMaintCollapsible(getFeedbackMaintCollapsibleCheck());
		maint.setToolBarButtonsMaint(getToolbarMaintCheck());
		maint.setContextMenuMaint(getBtnMenuContextualCheck());
		maint.setFilterMaint(getBtnFiltradoCheck());
		maint.setSearchMaint(getBtnBusquedaCheck());
		maint.setClientValidationMaint(getBtnValidacionesClienteCheck());
		maint.setMultiSelectMaint(getBtnMultiseleccionCheck());
//		maint.setHierarchyMaint(getBtnJerarquiaCheck());
		
		return maint;
	}

	/**********************/
	/* Métodos privados */
	/**********************/

	/**
	 * Valida los campos de la pantalla
	 * 
	 * @return true si todos los controles están validados, false si algún campo
	 *         no es válido.
	 */
	protected boolean validatePage() {

		setErrorMessage(null);

		if (!Utilities.isBlank(getNameMaintText())
				&& !Utilities.validateText(getNameMaintText())) {
			setErrorMessage("Caracteres no válidos para en campo 'Nombre del mantenimiento'");
			return false;
		}

		setErrorMessage(null);
		setMessage("Este Wizard genera un nuevo mantenimiento para una aplicación UDA");
		return true;
	}
}