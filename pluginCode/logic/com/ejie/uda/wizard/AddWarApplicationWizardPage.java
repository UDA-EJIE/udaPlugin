package com.ejie.uda.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jst.j2ee.project.EarUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.ejie.uda.Activator;
import com.ejie.uda.utils.Constants;
import com.ejie.uda.utils.Utilities;

/**
 *  Clase la cual define la pantalla del asistente "Añadir Proyecto WAR".
 */
public class AddWarApplicationWizardPage extends WizardPage {
	
	// Propiedades/Objecto utilizados en la pantalla
	private Text warNameText;
	private Text warFullNameText;
	private Text earNameText;
	private IProject projectEAR;
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
	public AddWarApplicationWizardPage(ISelection selection) {
		super("wizardPage");

		setTitle("Añadir un WAR a la aplicación");
		setDescription("Este Wizard genera un nuevo WAR y lo añade a un EAR existente");
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
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		gd3.horizontalSpan = 3;
		
		// Campo texto de nombre del EAR
		Label labelProjectEAR = new Label(container, SWT.NULL);
		labelProjectEAR.setText("EAR a vincular:");
		earNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		earNameText.setEnabled(false);
		earNameText.setTextLimit(25);
		
		Button button = new Button(container, SWT.NONE);
		button.setText("&Buscar Proyecto");
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					setProjectEAR(handleBrowseEARProject());
					if (getProjectEAR() != null) {
						earNameText.setText(getProjectEAR().getName());
						warFullNameText.setText(buildWarFullName(earNameText.getText(), warNameText.getText()));
					}
					break;
				}
			}
		});
		
		Label labelLine = new Label(container, SWT.NULL);
		labelLine.setLayoutData(gd3);
		
		// Campo texto de nombre del WAR
		Label labelWARName = new Label(container, SWT.NULL);
		labelWARName.setText("&Nombre del WAR:");
		warNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		warNameText.setTextLimit(25);
		warNameText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e) {
				/*
				 * Valida el contenido del campo y monta el nombre del WAR completo 
				 */
				if (warNameText.getText().length() > 0 && !Utilities.validateWARText(warNameText.getText())) {
					setMessage("Caracteres no válidos para en campo 'Nombre del WAR'", IMessageProvider.ERROR);
				}else{
					setMessage("Este Wizard genera un nuevo WAR y lo añade a un EAR existente");
					warFullNameText.setText(buildWarFullName(earNameText.getText(), warNameText.getText()));
				}
			}
		});
		warNameText.addListener(SWT.FocusOut, new Listener() {
			/*
			 * Al salir del campo se capitaliza (camelCase) los caracteres 
			 */
			public void handleEvent(Event e) {
				warNameText.setText(Utilities.camelCase(warNameText.getText()));
			}
		});

		Label labelHide = new Label(container, SWT.NULL);
		labelHide.setVisible(false);
		
		// Campo texto de nombre del WAR
		Label labelFullNameWAR = new Label(container, SWT.NULL);
		labelFullNameWAR.setText("Nombre Completo del WAR:");
		warFullNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		warFullNameText.setEnabled(false);

		//Aplica estilo
		earNameText.setLayoutData(gd);
		warNameText.setLayoutData(gd);
		warFullNameText.setLayoutData(gd);
		
		Label labelHide2 = new Label(container, SWT.NULL);
		labelHide2.setVisible(false);

		// Check box para generar ejemplos de código
//		examplesCheck = new Button(container, SWT.CHECK);
//		examplesCheck.setText("Generar ejemplos de código");
//		examplesCheck.setSelection(false);
//		examplesCheck.setLayoutData(gd3);
		
		// Salto de línea
		labelLine = new Label(container, SWT.NULL);
		labelLine.setLayoutData(gd3);

		// Grupo Layout
	    Group layoutGroup = new Group(container, SWT.NONE);
	    layoutGroup.setText("Layout");
	    layoutGroup.setLayout(new GridLayout(4, false));
	    layoutGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 3, 1));
	    
	    // Disposición
	    Label dispositionLabel = new Label(layoutGroup, SWT.NULL);
	    dispositionLabel.setText("Disposición:");
	    dispositionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	    
	    horizontalRadio = new Button(layoutGroup, SWT.RADIO);
	    horizontalRadio.setText("Horizontal");
	    horizontalRadio.setSelection(true);
	    horizontalRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
	    horizontalRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (horizontalRadio.getSelection()){
                	setMessage("Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar");
                }
            }
        });
	    
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

		Label labelRadioEmpty = new Label(layoutGroup, SWT.NULL);
		labelRadioEmpty.setText("");
		labelRadioEmpty.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	    
	    verticalRadio = new Button(layoutGroup, SWT.RADIO);
	    verticalRadio.setText("Vertical");
	    verticalRadio.setSelection(false);
	    verticalRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 3, 1));
	    verticalRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (verticalRadio.getSelection()){
                	setMessage("Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar");
                }
            }
        });

		labelRadioEmpty = new Label(layoutGroup, SWT.NULL);
		labelRadioEmpty.setText("");
		labelRadioEmpty.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	    
	    mixtoRadio = new Button(layoutGroup, SWT.RADIO);
	    mixtoRadio.setText("Mixto");
	    mixtoRadio.setSelection(false);
	    mixtoRadio.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 3, 1));
	    mixtoRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (mixtoRadio.getSelection()){
                	setMessage("Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar");
                }
            }
        });

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
		    categoryCombo.setToolTipText("Categoría de la aplicación");
		    categoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));	    
	    }
	    
		// Grupo Idioma
	    Group languageGroup = new Group(container, SWT.NONE);
	    languageGroup.setText("Idiomas");
	    languageGroup.setLayout(new GridLayout(4, true));
	    languageGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false, 3, 1));
	    
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

		setControl(container);
	}
	
	/*************/
	/*  Getters  */
	/*************/
	
	/**
	 * Recupera el nombre del WAR
	 * 
	 * @return nombre del WAR
	 */
	public String getWarNameText() {
		if (warNameText != null) {
			return warNameText.getText();
		} else {
			return "";
		}
	}
	
	public String getWarFullNameText() {
		if (warFullNameText != null) {
			return warFullNameText.getText();
		} else {
			return "";
		}
	}
	
	public String getEarNameText() {
		if (earNameText != null) {
			return earNameText.getText();
		} else {
			return "";
		}
	}
	
	public IProject getProjectEAR(){
		return this.projectEAR;
	}
	
	public void setProjectEAR(IProject project){
		this.projectEAR = project;
	}
	
	public String getWarCodName(){
		String codApp = getWarFullNameText();
		
		if (!Utilities.isBlank(codApp) && codApp.endsWith(Constants.WAR_NAME)){
			codApp = codApp.substring(0, codApp.length() - Constants.WAR_NAME.length());
		}
		
		return codApp;
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
	 * Recupera el valor del check de ejemplos de pantallas
	 * 
	 * @return true si seleccionado, false ecc.
	 */
	public boolean getExamplesCheck() {
		// De momento no se quiere generar ejemplos desde la nueva aplicacion
//		return examplesCheck.getSelection();
		return false;
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
	
	/**********************/
	/*  Métodos privados  */
	/**********************/
	
	private IProject handleBrowseEARProject() {

		IProject[] projectsEAR;

		projectsEAR = EarUtilities.getAllProjectsInWorkspaceOfType("jst.ear");

		ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Proyectos EAR");
		dialog.setMessage("Proyectos EAR al que se desea vincular");
		dialog.setElements(projectsEAR);

		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				IProject project = ((IProject) result[0]);
				if (isProjectEAREjie(project)){
					
					return project;	
				}
			}
		}
		return null;
	}
	
	private boolean isProjectEAREjie(IProject project){
		
		if (project != null){
			String nameProject = project.getName();
			
			if (!Utilities.isBlank(nameProject) && nameProject.endsWith(Constants.EAR_NAME)){
				return true;
			}
		}
		
		return false;
	}
	
	private String buildWarFullName(String earName, String warName){
		String fullName = "";
		String codApp = "";
		
		if (!Utilities.isBlank(earName) && !Utilities.isBlank(warName)){
			if (earName.endsWith(Constants.EAR_NAME)){
				codApp = earName.substring(0, earName.length() - Constants.EAR_NAME.length());
				if (!Utilities.isBlank(codApp)){
					fullName = codApp.toLowerCase() + Utilities.camelCase(warName) + Constants.WAR_NAME;	
				}
			}
		}
		
		return fullName;
	}
	
}