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

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ejie.uda.Activator;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

/**
 * Clase la cual define la pantalla del asistente "Crear nueva aplicación"
 */
public class NewApplicationWizardPage extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private Text appCodeText;
	private Text warNameText;
	private Text ejbNameText;
	private Text ejbFullNameText;
	private Text warFullNameText;
	private Button radSpringJDBC;
	private Button radJPA;
	private Button locationCheck;
	private Text locationText;
	private Button locationButton;
	private Text idSecurityText;
	private Button ejbCheck;
	private Label labelEJBName;
	private Label labelEJBNameFull;
	private Combo appTypeCombo;
	private Button horizontalRadio;
	private Button verticalRadio;
	private Button mixtoRadio;
	private Combo categoryCombo;
//	private Button examplesCheck;
	private Combo languageCombo;
	private Button esLanguageCheck; 
	private Button euLanguageCheck;
	private Button enLanguageCheck;
	private Button frLanguageCheck;

	/**
	 * Primera ventana del Wizard de Plugin, donde se selecciona
	 * la opción de generar una aplicación
	 * @param selection
	 */
	public NewApplicationWizardPage(ISelection selection) {
		super("wizardPage");

		setTitle("Crear nueva aplicación");
		setDescription("Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar");
	}

	/**
	 * Creación de controles de la ventana
	 * @param parent - controlador padre
	 */
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 5;
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 2;
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 3;
		GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);
		gd4.horizontalSpan = 4;
		GridData gd5 = new GridData(GridData.FILL_HORIZONTAL);
		gd5.horizontalSpan = 5;

		// Campo texto de Código de aplicación
		Label labelCodapp = new Label(container, SWT.NULL);
		labelCodapp.setText("Código de aplicación:");
		appCodeText = new Text(container, SWT.BORDER | SWT.SINGLE);
		appCodeText.setTextLimit(10);
		appCodeText.addListener(SWT.KeyUp, new Listener() {
			/*
			 * Valida el contenido del campo 
			 */
			public void handleEvent(Event e) {
				if (!Utilities.isBlank(getAppCode()) && !Utilities.validateText(appCodeText.getText())) {
					setMessage("Caracteres no válidos para en campo 'Código de aplicación'", IMessageProvider.ERROR);
				}else{
					setMessage("Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar");
					if (locationCheck.getSelection() && !Utilities.isBlank(getAppCode())){
						locationText.setText(getPathWorkspace() + "\\" + getAppCode());
					}
					if (!Utilities.isBlank(getAppCode())){
						warFullNameText.setText(buildWarFullName(getAppCode(), warNameText.getText()));
					}
					if (!Utilities.isBlank(getAppCode()) && getEjbCheck()){
						ejbFullNameText.setText(buildWarFullName(getAppCode(), ejbNameText.getText()));
					}
				}
			}
		});
		appCodeText.addListener(SWT.FocusOut, new Listener() {
			/*
			 * Al salir del campo pone los caracteres a minúsculas 
			 */
			public void handleEvent(Event e) {
				appCodeText.setText(appCodeText.getText().toLowerCase());
			}
		});
		appCodeText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
		
		// Check box de localizacion de la aplicación
		locationCheck = new Button(container, SWT.CHECK);
		locationCheck.setText("Usar localización por defecto");
		locationCheck.setSelection(true);
		locationCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (locationCheck.getSelection()) {
                	locationText.setEnabled(false);
                	locationText.setText(getPathWorkspace());
   		        	locationButton.setEnabled(false);
   		        	if (!Utilities.isBlank(getAppCode())){
						locationText.setText(getPathWorkspace() + "\\" + getAppCode());
					}
                } else {
                	locationText.setEnabled(true);
                	locationText.setText("");
   		        	locationButton.setEnabled(true);
                }
            }
        });
		locationCheck.setLayoutData(gd5);
		
		// Campo texto de la ruta de localización
		Label labelLocation = new Label(container, SWT.NULL);
		labelLocation.setText("Localización:");
		labelLocation.setLayoutData(gd);
		locationText = new Text(container, SWT.BORDER | SWT.SINGLE);
		locationText.setEnabled(false);
		locationText.setText(getPathWorkspace());
		locationText.setSize(50, 10);
		locationText.setLayoutData(gd2);
		
		locationButton = new Button(container, SWT.PUSH);
		locationButton.setText("Buscar...");
		locationButton.setEnabled(false);
		locationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				// Ingresa la rut apor defecto
				dlg.setFilterPath(locationText.getText());
				// Título de la ventana
				dlg.setText("Buscar carpeta");
				// Mensaje de título
				dlg.setMessage("Seleccione una carpeta para la aplicación");
				// Abre el diagolo de carpetas
				String dir = dlg.open();
				// Devolverá null si no ha seleccionada ninguna carpeta
				// en caso contrario recupera la ruta seleccionada
				if (dir != null) {
					// Pinta la ruta en la caja de texto
					locationText.setText(dir);
				}
			}
		});
		
		Label labelLine = new Label(container, SWT.NULL);
		labelLine.setLayoutData(gd5);
		
		// Campo texto de nombre del WAR
		Label labelWARName = new Label(container, SWT.NULL);
		labelWARName.setText("&Nombre del WAR:");
		labelWARName.setLayoutData(gd);
		warNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		warNameText.setTextLimit(25);
		warNameText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
				/*
				 * Valida el contenido del campo 
				 */
				if (warNameText.getText().length() > 0 && !Utilities.validateWARText(warNameText.getText())) {
					setMessage("Caracteres no válidos para en campo 'Nombre del WAR'", IMessageProvider.ERROR);
				}else{
					setMessage("Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar");
					warFullNameText.setText(buildWarFullName(getAppCode(), warNameText.getText()));
				}
			}
		});
		warNameText.addListener(SWT.FocusOut, new Listener() {
			/*
			 * Al salir del campo se capitaliza(camelCase) los caracteres
			 */
			public void handleEvent(Event e) {
				warNameText.setText(Utilities.camelCase(warNameText.getText()));
			}
		});
		warNameText.setLayoutData(gd4);
		
		// Campo texto de nombre del WAR
		Label labelFullNameWAR = new Label(container, SWT.NULL);
		labelFullNameWAR.setText("Nombre Completo del WAR:");
		warFullNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		warFullNameText.setEnabled(false);
		warFullNameText.setLayoutData(gd4);
		
		// Salto de línea
		labelLine = new Label(container, SWT.NULL);
		labelLine.setLayoutData(gd5);
		
		// Grupo Layout
	    Group persistenceGroup = new Group(container, SWT.NONE);
	    persistenceGroup.setLayout(new GridLayout(3, false));
	    persistenceGroup.setText("Persistencia");
	    persistenceGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 5, 1));
		
	    Label labelRadioEmpty = new Label(persistenceGroup, SWT.NULL);
		labelRadioEmpty.setText("                   ");
		labelRadioEmpty.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	    
		// Radio button para Spring-JDBC
		radSpringJDBC = new Button(persistenceGroup, SWT.RADIO);
		radSpringJDBC.setText("Spring JDBC");
		radSpringJDBC.setSelection(true);
		radSpringJDBC.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		// Radio button para JPA 2.0
		radJPA = new Button(persistenceGroup, SWT.RADIO);
		radJPA.setText("JPA 2.0");
		radJPA.setSelection(false);
		radJPA.setLayoutData(new GridData (SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		// Grupo Layout
	    Group layoutGroup = new Group(container, SWT.NONE);
	    layoutGroup.setText("Layout");
	    layoutGroup.setLayout(new GridLayout(4, false));
	    layoutGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 5, 1));
	    
	    // Disposición
	    Label dispositionLabel = new Label(layoutGroup, SWT.NULL);
	    dispositionLabel.setText("Disposición:");
	    dispositionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	    
	    horizontalRadio = new Button(layoutGroup, SWT.RADIO);
	    horizontalRadio.setText("Horizontal");
	    horizontalRadio.setSelection(true);
	    horizontalRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
	    
	    if ("true".equalsIgnoreCase(Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE))){
		     // Tipo aplicación
		    Label appTypeLabel = new Label(layoutGroup, SWT.NULL);
		    appTypeLabel.setText("Tipo aplicación:");
		    appTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		    
		    // Combo Tipo Apliciación
		    appTypeCombo = new Combo(layoutGroup, SWT.READ_ONLY);
		    appTypeCombo.add("Intranet/Extranet/JASO");
		    appTypeCombo.add("Internet");
		    appTypeCombo.select(0);
		    appTypeCombo.setToolTipText("Tipo de aplicación a generar");
		    appTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			appTypeCombo.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (categoryCombo != null) {
						if (Constants.APP_TYPE_INTERNET.equalsIgnoreCase(appTypeCombo.getText())) {
							categoryCombo.select(0);
							categoryCombo.setEnabled(false);
						} else {
							categoryCombo.setEnabled(true);
						}
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
	    }else{
	    	horizontalRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
	    }

		labelRadioEmpty = new Label(layoutGroup, SWT.NULL);
		labelRadioEmpty.setText("");
		labelRadioEmpty.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	    
	    verticalRadio = new Button(layoutGroup, SWT.RADIO);
	    verticalRadio.setText("Vertical");
	    verticalRadio.setSelection(false);
	    verticalRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 3, 1));

		labelRadioEmpty = new Label(layoutGroup, SWT.NULL);
		labelRadioEmpty.setText("");
		labelRadioEmpty.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	    
	    mixtoRadio = new Button(layoutGroup, SWT.RADIO);
	    mixtoRadio.setText("Mixto");
	    mixtoRadio.setSelection(false);
	    mixtoRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 3, 1));
	   
	    if ("true".equalsIgnoreCase(Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE))){
	    	
	    	mixtoRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
	    	
	    	// Categoría
		    Label categoryLabel = new Label(layoutGroup, SWT.NULL);
		    categoryLabel.setText("Categoría:");
		    categoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		    // Combo Categoría
		    categoryCombo = new Combo(layoutGroup, SWT.READ_ONLY);
		    categoryCombo.add("Horizontal");
		    categoryCombo.add("Departamental");
		    categoryCombo.select(0);
		    categoryCombo.setToolTipText("Categoróa de la aplicación");
		    categoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));	    
	    }
	    
		// Grupo Idioma
	    Group languageGroup = new Group(container, SWT.NONE);
	    languageGroup.setText("Idiomas");
	    languageGroup.setLayout(new GridLayout(4, true));
	    languageGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 5, 1));
	    
		// Grupo Idioma Base
	    Group baseLanguageGroup = new Group(languageGroup, SWT.NONE);
	    baseLanguageGroup.setText("Base");
	    baseLanguageGroup.setLayout(new GridLayout(2, false));
	    baseLanguageGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 2, 1));
	    
		// Check de Castellano
	    esLanguageCheck = new Button(baseLanguageGroup, SWT.CHECK);
	    esLanguageCheck.setText("Castellano");
	    esLanguageCheck.setSelection(true);
	    esLanguageCheck.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 1, 1));
	    esLanguageCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (languageCombo != null){
            		if (esLanguageCheck.getSelection()) {
        				if (languageCombo.indexOf("Castellano")==-1){
        					languageCombo.add("Castellano");
        				}
                    } else {
                    	if (languageCombo.indexOf("Castellano")!=-1){
        					languageCombo.remove("Castellano");
        				}
                    }	
            	}
            }
        });
	    
		// Check de Euskera
		euLanguageCheck = new Button(baseLanguageGroup, SWT.CHECK);
		euLanguageCheck.setText("Euskera");
		euLanguageCheck.setSelection(true);
		euLanguageCheck.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 1, 1));
		euLanguageCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (languageCombo != null){
            		if (euLanguageCheck.getSelection()) {
        				if (languageCombo.indexOf("Euskera")==-1){
        					languageCombo.add("Euskera");
        				}
                    } else {
                    	if (languageCombo.indexOf("Euskera")!=-1){
        					languageCombo.remove("Euskera");
        				}
                    }	
            	}
            }
        });   
		
		// Grupo Idioma Otros
	    Group othersLanguageGroup = new Group(languageGroup, SWT.NONE);
	    othersLanguageGroup.setText("Otros");
	    othersLanguageGroup.setLayout(new GridLayout(2, false));
	    othersLanguageGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 2, 1));
	    
		// Check de Inglés
		enLanguageCheck = new Button(othersLanguageGroup, SWT.CHECK);
		enLanguageCheck.setText("Inglés");
		enLanguageCheck.setSelection(false);
		enLanguageCheck.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 1, 1));
		enLanguageCheck.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent e) {
            	if (languageCombo != null){
            		if (enLanguageCheck.getSelection()) {
        				if (languageCombo.indexOf("Inglés")==-1){
        					languageCombo.add("Inglés");
        				}
                    } else {
                    	if (languageCombo.indexOf("Inglés")!=-1){
        					languageCombo.remove("Inglés");
        				}
                    }	
            	}
            }
        });
		
		// Check de Francés
		frLanguageCheck = new Button(othersLanguageGroup, SWT.CHECK);
		frLanguageCheck.setText("Francés");
		frLanguageCheck.setSelection(false);
		frLanguageCheck.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 1, 1));
		frLanguageCheck.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent e) {
            	if (languageCombo != null){
            		if (frLanguageCheck.getSelection()) {
        				if (languageCombo.indexOf("Francés")==-1){
        					languageCombo.add("Francés");
        				}
                    } else {
                    	if (languageCombo.indexOf("Francés")!=-1){
        					languageCombo.remove("Francés");
        				}
                    }	
            	}
            }
        });
		
		//Deshabilita los idiomas bases, pues no pueden ser deseleccionados
		if (Activator.getDefault().getPreferenceStore().getString(Constants.PREF_EJIE).equals("true")){
			esLanguageCheck.setEnabled(false);
			euLanguageCheck.setEnabled(false);
		}
		
		// Idioma
	    Label languageLabel = new Label(languageGroup, SWT.NULL);
	    languageLabel.setText("Idioma por defecto:");
	    languageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		
		// Combo de Idioma
	    languageCombo = new Combo(languageGroup, SWT.READ_ONLY);
	    languageCombo.add("Castellano");
	    languageCombo.add("Euskera");
	    languageCombo.select(0);
	    languageCombo.setToolTipText("Idioma por defecto de la aplicación");
	    languageCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 3, 1));

		labelLine = new Label(container, SWT.NULL);
		labelLine.setLayoutData(gd5);

		// Check box para crear Módulo EJB para la aplicación
		ejbCheck = new Button(container, SWT.CHECK);
		ejbCheck.setText("Módulo EJB para Remoting");
		ejbCheck.setSelection(false);
		ejbCheck.setLayoutData(gd5);
		ejbCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if (getEjbCheck()){
            		labelEJBName.setVisible(true);
            		ejbNameText.setVisible(true);
            		labelEJBNameFull.setVisible(true);
            		ejbFullNameText.setVisible(true);
            	}	else{
            		ejbNameText.setText("");
            		ejbFullNameText.setText("");
            		labelEJBName.setVisible(false);
            		ejbNameText.setVisible(false);
            		labelEJBNameFull.setVisible(false);
            		ejbFullNameText.setVisible(false);
            	}
            }
        });

		labelEJBName = new Label(container, SWT.NULL);
		labelEJBName.setText("Nombre del proyecto de EJBs:");
		labelEJBName.setVisible(false);
		labelEJBName.setLayoutData(gd);
		ejbNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		ejbNameText.setTextLimit(25);
		ejbNameText.setVisible(false);
		ejbNameText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
				/*
				 * Valida el contenido del campo 
				 */
				if (ejbNameText.getText().length() > 0 && !Utilities.validateWARText(ejbNameText.getText())) {
					setMessage("Caracteres no válidos para en campo 'Nombre del EJB'", IMessageProvider.ERROR);
				}else{
					setMessage("Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar");
					ejbFullNameText.setText(buildEjbFullName(getAppCode(), ejbNameText.getText()));
				}
			}
		});
		ejbNameText.setLayoutData(gd4);
		
		labelEJBNameFull = new Label(container, SWT.NULL);
		labelEJBNameFull.setText("Nombre completo del proyecto de EJBs:");
		labelEJBNameFull.setVisible(false);
		labelEJBNameFull.setLayoutData(gd);
		
		ejbFullNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		ejbFullNameText.setEnabled(false);
		ejbFullNameText.setVisible(false);
		ejbFullNameText.setLayoutData(gd4);
		// Pone el foco en el primer campo
		appCodeText.forceFocus();

		setControl(container);
	}

	/*************/
	/*  Getters  */
	/*************/
	
	/**
	 * Recupera el código de la aplicación
	 * 
	 * @return código de la aplicación
	 */
	public String getAppCode() {
		if (appCodeText != null) {
			return appCodeText.getText();
		} else {
			return "";
		}
	}

	/**
	 * Recupera el nombre del WAR
	 * 
	 * @return nombre del WAR
	 */
	public String getWarName() {
		if (warNameText != null) {
			return warNameText.getText();
		} else {
			return "";
		}
	}

	/**
	 * Recupera el valor del radio de Spring-JDBC
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getRadSpringJDBC() {
		return radSpringJDBC.getSelection();
	}
	
	/**
	 * Recupera el valor del radio de JPA 2.0
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getRadJPA() {
		return radJPA.getSelection();
	}

	/**
	 * Recupera el valor del check de localización
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getLocationCheck() {
		return locationCheck.getSelection();
	}
	
	/**
	 * Recupera el nombre del WAR
	 * 
	 * @return nombre del WAR
	 */
	public String getLocationText() {
		if (locationText != null) {
			return locationText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el valor del check de crear módulo EJB para la aplicación
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getEjbCheck() {
		return ejbCheck.getSelection();
	}
	
	/**
	 * Recupera el id de seguridad de la aplicación
	 * 
	 * @return id de seguridad de la aplicación
	 */
	public String getIdSecurity() {
		if (idSecurityText != null) {
			return idSecurityText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera el nombre del proyecto EJJB
	 * 
	 * @return nombre proyecto EJB
	 */
	public String getEjbFullNameText() {
		if (ejbFullNameText != null) {
			return ejbFullNameText.getText();
		} else {
			return "";
		}
	}
	
	/**
	 * Recupera la diposición de los elementos del layout
	 * 
	 * @return nombre de la diposición de los elementos del layout
	 */
	public String getDispositionCombo() {
		
		String disposition = "";
		
		if (horizontalRadio != null && verticalRadio != null && mixtoRadio != null) {
			if (horizontalRadio.getSelection()){
				disposition = horizontalRadio.getText(); 
			}else if (verticalRadio.getSelection()){
				disposition = verticalRadio.getText(); 
			}else if (mixtoRadio.getSelection()){
				disposition = mixtoRadio.getText(); 
			} 
		}
		return disposition;
	}
	
	/**
	 * Recupera el tipo de aplicación para el layout
	 * 
	 * @return tipo de aplicación para el layout
	 */
	public String getAppTypeCombo() {
		if (appTypeCombo != null) {
			return appTypeCombo.getText();
		} else {
			return "";
		}
	}

	/**
	 * Recupera categoría de la aplicación para el layout
	 * 
	 * @return categoría de la aplicación para el layout
	 */
	public String getCategoryCombo() {
		if (categoryCombo != null) {
			return categoryCombo.getText();
		} else {
			return "";
		}
	}

	/**
	 * Recupera una cadena de valores de idiomas de la aplicación
	 * 
	 * @return cadena de idiomas.
	 */
	public String getLanguages(){
		String languages = "";
		
		if (esLanguageCheck.getSelection()){
			languages += "\"es\", ";
		}
		if (euLanguageCheck.getSelection()){
			languages += "\"eu\", ";
		}
		if (enLanguageCheck.getSelection()){
			languages += "\"en\", ";
		}
		if (frLanguageCheck.getSelection()){
			languages += "\"fr\", ";
		}
		
		if (!Utilities.isBlank(languages)){
			// Quita la última coma
			languages = languages.substring(0, languages.length() - 2);
		}
		
		return languages;
	}

	/**
	 * Recupera una cadena de valores de idiomas sin comillas de la aplicación
	 * 
	 * @return cadena de idiomas.
	 */
	public String getLanguagesWithoutQuotes(){
		String languages = "";
		
		if (esLanguageCheck.getSelection()){
			languages += "es, ";
		}
		if (euLanguageCheck.getSelection()){
			languages += "eu, ";
		}
		if (enLanguageCheck.getSelection()){
			languages += "en, ";
		}
		if (frLanguageCheck.getSelection()){
			languages += "fr, ";
		}
		
		if (!Utilities.isBlank(languages)){
			// Quita la última coma
			languages = languages.substring(0, languages.length() - 2);
		}
		
		return languages;
	}
	
	/**
	 * Recupera idioma de la aplicación
	 * 
	 * @return idioma de la aplicación
	 */
	public String getLanguageCombo() {
		String language = "";
		
		if (languageCombo != null) {
			
			if ("Castellano".equalsIgnoreCase(languageCombo.getText())){
				language = "es";
			}
			if ("Euskera".equalsIgnoreCase(languageCombo.getText())){
				language = "eu";
			}
			if ("Inglés".equalsIgnoreCase(languageCombo.getText())){
				language = "en";
			}
			if ("Francés".equalsIgnoreCase(languageCombo.getText())){
				language = "fr";
			}
		}
		return language;
	}

	public void setFocusAppCode(){
		appCodeText.setFocus();
	}
	
	private String getPathWorkspace(){
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getLocation().toString().replace("/", "\\");
	}
	
	private String buildWarFullName(String appCode, String warName){
		String fullName = "";
		
		if (!Utilities.isBlank(appCode) && !Utilities.isBlank(warName)){
			fullName = appCode.toLowerCase() + Utilities.camelCase(warName) + Constants.WAR_NAME;	
		}
		
		return fullName;
	}
	private String buildEjbFullName(String appCode, String warName){
		String fullName = "";
		
		if (!Utilities.isBlank(appCode) && !Utilities.isBlank(warName)){
			fullName = appCode.toLowerCase() + Utilities.camelCase(warName) + Constants.EJB_NAME;	
		}
		
		return fullName;
	}
	
}