package com.ejie.uda.wizard;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ejie.uda.utils.Utilities;

/**
 * Clase la cual define la cuarta pantalla del asistente "Generar código de negocio y control"
 */
public class GenerateCodeWizardPageFour extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private Group securityGroup;
	private Group xlnetsParamsGroup;
	private Button radNoSec;
	private Button radXLNets;
	//private Button radAnotControl;
	//private Button radXmlControl;
	private Text idSecurityText;
	private Text idSecurityTextAux;
	
	
	/**
	 * Primera ventana del Wizard de Plugin, donde se selecciona
	 * la opción de generar una aplicación
	 * @param selection
	 */
	public GenerateCodeWizardPageFour(ISelection selection) {
		super("wizardPage");

		setTitle("Generar código para una aplicación UDA");
		setDescription("Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar");
		setPageComplete(true);
	}

	/**
	 * Creación de controles de la ventana
	 * @param parent - controlador padre
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		//layout.numColumns = 4;
	
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 2;
		GridData gd3 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd3.horizontalSpan = 3;
		GridData gd4 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd4.horizontalSpan = 4;
		
		
		
		Label descLabelSeg= new Label(container, SWT.NULL);
		descLabelSeg.setText("Seleccione el tipo de seguridad que desea utilizar");
		descLabelSeg.setLayoutData(gd2);
		// Salto de línea
		Label hiddenLabel= new Label(container, SWT.NULL);
		hiddenLabel.setLayoutData(gd2);
		
		
			
		
		securityGroup = new Group(container, SWT.NONE);
		securityGroup.setText("Seguridad");
	    securityGroup.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, true));
		
	    securityGroup.setLayout(new GridLayout(2, false));		
		
	   
	
		// Radio de seguridad
		Label labelSecurity = new Label(securityGroup, SWT.NULL);
		labelSecurity.setText("XLNets:");
		labelSecurity.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,1,1));

		//XLNets
		radXLNets = new Button(securityGroup, SWT.RADIO);
		radXLNets.setText("Sí");
		radXLNets.setSelection(true);
		radXLNets.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radXLNets.getSelection()){
                	setMessage("Este Wizard genera la estructura necesaria para el backend necesario");
                	idSecurityTextAux.setText("");
                	idSecurityText.setText("");
                	xlnetsParamsGroup.setVisible(true);
                
                }
            }
        });
		radXLNets.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,1,1));
		
		// Salto de línea
		Label hiddenLabel2= new Label(securityGroup, SWT.NULL);
		hiddenLabel2.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,1,1));
		
		//No XLNets
		radNoSec = new Button(securityGroup, SWT.RADIO);
		radNoSec.setText("No");
		radNoSec.setSelection(false);
		radNoSec.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radNoSec.getSelection()){
                	setMessage("Este Wizard genera la estructura necesaria para el backend necesario");
                	xlnetsParamsGroup.setVisible(false);
                }
            }
        });
		radNoSec.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false,1,1));	
		
		xlnetsParamsGroup = new Group(container, SWT.NONE);
		xlnetsParamsGroup.setText("Parámetros XLNets");
		xlnetsParamsGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true));
		
		xlnetsParamsGroup.setLayout(new GridLayout(3, false));		
		
	
			// Campo texto de Código de aplicación
		Label labelIdSecurity = new Label(xlnetsParamsGroup, SWT.NULL);
		labelIdSecurity.setText("Instancia XLNets:");
		labelIdSecurity.setVisible(true);
		labelIdSecurity.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));
		idSecurityText = new Text(xlnetsParamsGroup, SWT.BORDER | SWT.SINGLE);
		idSecurityText.setToolTipText("Instancia correspondiente a la instancia XLNets. Para introducir más de un valor, separar por el carácter ';'. NOTA: El primer Rol de la lista será el que se aplique a los métodos de los servicios de negocio.");
		idSecurityText.setVisible(true);
		idSecurityText.addListener(SWT.KeyUp, new Listener() {
			/*
			 * Valida el contenido del campo 
			 */
			public void handleEvent(Event e) {
				if (!Utilities.isBlank(getIdSecurity())) {
					//setMessage("Caracteres no numéricos para el campo 'Id Seguridad'", IMessageProvider.ERROR);
					idSecurityTextAux.setText(getTextIsSeguridad(getIdSecurity()));
				}else{
					setMessage("Este Wizard genera la estructura necesaria para desarollar una aplicación estándar");
				}
			}
		});
		idSecurityText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,2,1));
		
		// Campo texto de Código de aplicación
		Label labelIdSecurityAux = new Label(xlnetsParamsGroup, SWT.NULL);
		labelIdSecurityAux.setText("Rol Spring Security:");
		labelIdSecurityAux.setVisible(true);
		labelIdSecurityAux.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,1,1));
		idSecurityTextAux = new Text(xlnetsParamsGroup, SWT.BORDER | SWT.SINGLE);
		idSecurityTextAux.setVisible(true);
		idSecurityTextAux.setEnabled(false);
		idSecurityTextAux.addListener(SWT.KeyUp, new Listener() {
			/*
			 * Valida el contenido del campo 
			 */
			public void handleEvent(Event e) {
					setMessage("Este Wizard genera la estructura necesaria para desarollar una aplicación estándar");
				
			}
		});
		idSecurityTextAux.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,2,1));
		setControl(container);
		
	}
	
	
	/*************/
	/*  Getters  */
	/*************/
	/**
	 * Recupera el valor del radio de seguridad
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getRadNoSec() {
		return radNoSec.getSelection();
	}
	/**
	 * Recupera el valor del radio de seguridad
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getRadXLNets() {
		return radXLNets.getSelection();
	}

	public String getIdSecurity() {
		if (idSecurityText != null) {
			return idSecurityText.getText();
		} else {
			return "";
		}
	}
	public String getIdSecurityAux() {
		if (idSecurityTextAux != null) {
			return idSecurityTextAux.getText();
		} else {
			return "";
		}
	}
	/**********************/
	/*  Métodos privados  */
	/**********************/
	
	private String getTextIsSeguridad(String idSeguridad){
		//String resultado="";
		if (!idSeguridad.equals("")){
			String chkIdXlNetsAux =idSeguridad;
			if (idSeguridad.substring(0, 1).equals(";")){
				 chkIdXlNetsAux = chkIdXlNetsAux.substring(1,chkIdXlNetsAux.length());
			}
			if (idSeguridad.substring(idSeguridad.length()-1, idSeguridad.length()).equals(";")){
				chkIdXlNetsAux=chkIdXlNetsAux.substring(0, chkIdXlNetsAux.length()-1);
			}
			
			String rolesAnt= chkIdXlNetsAux.replace(";", "','ROLE_");
			return "'ROLE_"+rolesAnt+"'";
		}	else return "";
		
	}
	
}