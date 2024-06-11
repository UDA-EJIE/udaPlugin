from customtkinter import *
import yaml
from pathlib import Path
import yaml
from copier import Worker
import tkinter as tk
import logging
import plugin.utils as utl
import menuPrincipal as m
import customtkinter as ctk
from datetime import datetime

self = CTk()

base_path = os.path.dirname(os.path.abspath(__file__))


class Paso1(CTk):
    def __init__(self):
        super().__init__()

        self.title("Crear nueva aplicación")
        self.geometry("900x700")

        # Configurar el color de fondo de la ventana
        self.config(bg="#E0E0E0")

        self.columnconfigure(1, weight=1)

        configuration_frame = CTkFrame(self, bg_color="black")
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")

        configuration_label = CTkLabel(configuration_frame,  text="Crear nueva aplicación", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        self.configuration_warning = CTkLabel(configuration_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.grid(row=0, column=3, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar")
        description_label.grid(row=1, column=0, columnspan=3, pady=(10, 5), padx=20, sticky="w")

        code_label = CTkLabel(self, text="Código de aplicación:", bg_color='#E0E0E0', text_color="black", font=("Arial", 12, "bold"))
        code_label.grid(row=2, column=0, sticky="w", padx=(20, 10), pady=(20, 2))
        self.entry_code = CTkEntry(self, bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', height=2.5, border_width=3, text_color="black" )
        self.entry_code.grid(row=2, column=1, padx=(30, 400), pady=(20, 2), sticky="ew")

        self.use_default_location = tk.BooleanVar()
        location_checkbox = CTkCheckBox(self,hover=True, text="Usar localización por defecto", checkbox_height=20, checkbox_width=20, border_color='#337ab7', variable=self.use_default_location, command=self.toggle_textbox, bg_color='#E0E0E0', text_color="black", font=("Arial", 12, "bold"))
        location_checkbox.grid(row=3, column=0, columnspan=2, pady=(5, 2), padx=20, sticky="w")
        location_checkbox.select()

        localizacion_label = CTkLabel(self, text="Localización:",  bg_color='#E0E0E0', text_color="black", font=("Arial", 12, "bold"))
        localizacion_label.grid(row=4, column=0, sticky="w", padx=(20, 10), pady=(5, 2))
        self.entry_location = CTkEntry(self, state="normal", bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', height=2.5, border_width=3)
        self.entry_location.grid(row=4, column=1, padx=(30,180), pady=(5, 2), sticky="ew")
        self.entry_location.configure(placeholder_text=os.getcwd())
        self.entry_location.configure(placeholder_text_color="grey")
        self.entry_location.configure(text_color="grey")
        self.entry_location.delete(0, "end")
        rutaUltimoProyecto = utl.readConfig("RUTA", "ruta_ultimo_proyecto")
        if rutaUltimoProyecto == '':
            self.entry_location.insert(0, os.getcwd())
        else:
            self.entry_location.insert(0, rutaUltimoProyecto)    
        self.entry_location.configure(state="disabled")

        self.location_button = CTkButton(self,state="disabled", text="Explorar", command=self.browse_location, bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        self.location_button.grid(row=4, column=1, pady=(5, 2), padx= (0, 20), sticky="e")

        war_label = CTkLabel(self, text="Nombre del WAR:", bg_color='#E0E0E0', text_color="black", font=("Arial", 12, "bold"))
        war_label.grid(row=5, column=0, sticky="w", padx=(20, 10), pady=(5, 30))
        self.entry_war = CTkEntry(self, bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', height=2.5, border_width=3, text_color="black")
        self.entry_war.grid(row=5, column=1, padx=(30, 20), pady=(5, 30), sticky="ew")

        languages_frame = CTkFrame(self,  bg_color='#E0E0E0', fg_color='#E0E0E0', border_color='#69a3d6', border_width=3)
        languages_frame.grid(row=6, column=0, columnspan=2, pady=(5, 30), padx=20, sticky="ew")

        # Crear un marco interno para organizar los widgets dentro del contenedor "Idiomas"
        self.idiomas_inner_frame = CTkFrame(languages_frame, fg_color='#E0E0E0', bg_color='#E0E0E0', border_color='#69a3d6')
        self.idiomas_inner_frame.grid(row=0, column=0, padx=10, pady=10, sticky="nsew")

        # Crear un widget Label encima del borde del marco
        label_on_border = CTkLabel(self, text="Idiomas", bg_color="#E0E0E0", fg_color="#E0E0E0", text_color="black", font=("Arial", 12, "bold"))
        label_on_border.place(in_=languages_frame, anchor="sw" )

        # obligatoria Castellano y Euskera
        self.language_options = ["Castellano", "Euskera", "Inglés", "Francés"]
        self.language_vars = []
        self.language_vars.append(tk.BooleanVar(name="Castellano",value=TRUE))
        self.language_vars.append(tk.BooleanVar(name="Euskera",value=TRUE))
        self.language_vars.append(tk.BooleanVar(name="Inglés",value=False))
        self.language_vars.append(tk.BooleanVar(name="Francés",value=False))
        stateCheck = "disabled"

        for i, (lang_option, lang_var) in enumerate(zip(self.language_options, self.language_vars)):
            if(lang_option != 'Castellano' and lang_option != 'Euskera'):
                stateCheck = "normal"
            CTkCheckBox(self.idiomas_inner_frame,state=stateCheck, text=lang_option, variable=lang_var, checkbox_height=20, checkbox_width=20, border_color='#337ab7', bg_color='#E0E0E0', text_color="black", font=("Arial", 12, "bold")).grid(row=0, column=i, padx=5, pady=(10, 2), sticky="w")

        default_language_label = CTkLabel(self.idiomas_inner_frame, text="Idioma por defecto:", text_color="black", font=("Arial", 12, "bold"))
        default_language_label.grid(row=7, column=0, sticky="w", padx=(10, 10), pady=(25, 2))
        self.default_language_var = tk.StringVar()

        self.default_language_combobox = self.update_default_language_options()
        security_frame = CTkFrame(self, bg_color='#E0E0E0', fg_color='#E0E0E0', border_color='#69a3d6', border_width=3)
        security_frame.grid(row=8, column=0, columnspan=2, pady=(30, 20), padx=20, sticky="ew")

        # Crear un widget Label encima del borde del marco
        labelSecurityFrame = CTkLabel(self, text="Seguridad con XLNets", bg_color="#E0E0E0", fg_color="#E0E0E0", text_color="black", font=("Arial", 12, "bold"))
        labelSecurityFrame.place(in_=security_frame, anchor="sw" )

        self.security_var = tk.StringVar(value="Si")
        self.security_yes_radio = CTkRadioButton(security_frame, text="Sí", value="Si", variable=self.security_var, text_color="black", radiobutton_height= 18 , radiobutton_width= 18)
        self.security_yes_radio.grid(row=0, column=0, padx=(20, 0), pady=(20, 10), sticky="nsew")
        security_no_radio = CTkRadioButton(security_frame, text="No", value="No", variable=self.security_var, text_color="black", radiobutton_height= 18 , radiobutton_width= 18)
        security_no_radio.grid(row=0, column=1, padx=5, pady=(20, 10), sticky="nsew")

        finish_button = CTkButton(self, text="Finish", command=lambda:self.mostrarSpinner(), bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        finish_button.grid(row=12, column=1, pady=(60, 0), padx=(560, 30), sticky = "se")

        cancel_button = CTkButton(self, text="Cancelar", command= lambda: m.MainMenuLoop(self), bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        cancel_button.grid(row=12, column=1, pady=(60, 0), padx=(300,150), sticky = "se")




    def toggle_textbox(self):# check localización por defecto
        if self.use_default_location.get():
            self.entry_location.configure(text_color="grey")
            self.entry_location.delete(0, "end")
            self.entry_location.insert(0, os.getcwd())
            self.entry_location.configure(state="disabled")
            self.location_button.configure(state="disabled")
        else:
            self.entry_location.configure(state="normal")
            self.location_button.configure(state="normal")
            self.entry_location.configure(text_color="black")

    def update_default_language_options(self):
        idiomas_seleccionados = []

            # Iterar sobre los checkbox y agregar los idiomas seleccionados a la lista
        for lang_option, lang_var in zip(self.language_options, self.language_vars):
            idiomas_seleccionados.append(lang_option)

        self.default_language_combobox = CTkComboBox(self.idiomas_inner_frame, values= idiomas_seleccionados, command=self.validarIdioma)
        self.default_language_combobox.grid(row=7, column=1, padx=(0, 20), pady=(25, 2), sticky="ew")
        self.default_language_combobox.set(idiomas_seleccionados[0] if idiomas_seleccionados else "")
        self.default_language_combobox._values = idiomas_seleccionados

        return self.default_language_combobox   
    
    def validarIdioma(self, idiomaSeleccionado):

        for lang_option, lang_var in zip(self.language_options, self.language_vars):
            if lang_var.get() == FALSE:
                if lang_option == idiomaSeleccionado:
                    self.configuration_warning.configure(text="El idioma por defecto('"+idiomaSeleccionado+"'), no esta seleccionado")
                    return FALSE
        self.configuration_warning.configure(text="")    

    def save_to_yaml(self):
        inicio = datetime.now()
        yaml_data = {
            "i18n_app": [lang_option for lang_option, lang_var in zip(self.language_options, self.language_vars) if lang_var.get()],
            "i18n_default_app": self.default_language_var.get(),
            "project_name": self.entry_code.get(),
            "security_app": self.security_var.get(),
            "war_project_name": self.entry_war.get()
        }
        if self.security_yes_radio._check_state:
            yaml_data["xlnets"] = TRUE
        else:
            yaml_data["xlnets"] = FALSE     

        rutaPath = utl.rutaActual(__file__)
        directorio_actual = rutaPath + "\\templates\\proyecto"
        filesExcludes = []
        availableLangs = "es, eu"
        
        for lang_option, lang_var in zip(self.language_options, self.language_vars):
            if lang_var.get() == FALSE:
                if lang_option == "Inglés":
                    filesExcludes.append("*i18n_en*")
                if lang_option == "Francés":
                    filesExcludes.append("*i18n_fr*") 
            else:  
                if lang_option == "Inglés":
                    availableLangs = availableLangs + " ,en"
                if lang_option == "Francés":
                    availableLangs = availableLangs + " ,fr"   
        filesExcludes.append("*EJB")         
        yaml_data["defaultLanguage"] = self.default_language_combobox.get()
        yaml_data["availableLangs"] = availableLangs
        destinoPath = self.entry_location.get()
        if(destinoPath == ''):
            destinoPath = rutaPath
        now = datetime.now()
        dates = now.strftime('%d-%b-%Y %H:%M:%S') 
        print('Inicio: proyecto Creando... ' + yaml_data["project_name"]+yaml_data["war_project_name"])    
        with Worker(src_path=directorio_actual,overwrite=True, dst_path=destinoPath, data=yaml_data,exclude=filesExcludes) as worker:
            logging.info('Inicio: Crear proyecto: ' + yaml_data["project_name"]+yaml_data["war_project_name"])
            worker.template.version = ": 1.0 Paso 1 ::: "+dates
            worker.run_copy()
            logging.info('Fin: Crear proyecto: ' + yaml_data["project_name"]+yaml_data["war_project_name"])
            #guardar ultima ruta creada
            utl.writeConfig(
                "RUTA", {"ruta_classes":destinoPath,"ruta_war":destinoPath,"ruta_ultimo_proyecto":destinoPath})
        self.ocultarSpinner()
        print('Fin: proyecto Creado: ' + yaml_data["project_name"]+yaml_data["war_project_name"])
        fin = datetime.now()
        logging.info('Tiempo: proyecto Creado en: ' + str((fin-inicio).total_seconds()) + " segundos")
        now = datetime.now()
        dates = now.strftime('%d-%b-%Y %H:%M:%S')
        print(F"Final: paso 1 creado ::: "+dates,file=sys.stderr)
        sys.stderr.flush()
        self.ventana_final_popup()

            
        

        
    def browse_location(self):
        folder_selected = filedialog.askdirectory()
        if not folder_selected == '':
            self.entry_location.delete(0, "end")
            self.entry_location.insert(0, folder_selected)

    def mostrarSpinner(self):
        if(self.entry_code.get() == ''):
            self.configuration_warning.configure(text="Campo 'Código de aplicación' obligatorio")
            return FALSE
        if(self.entry_war.get() == ''):
            self.configuration_warning.configure(text="Campo 'Nombre del WAR' obligatorio")
            return FALSE 
        if os.path.isdir(self.entry_location.get()) == False:
            self.configuration_warning.configure(text="La localización no existe")
            return FALSE
        self.wm_attributes('-alpha',0.4)
        resultados_window2 = ctk.CTkToplevel(self)
        resultados_window2.title("")
        resultados_window2.attributes('-topmost', True)
        resultados_window2.wm_attributes('-alpha',0.8)
        self.title("Cargando...")
        resultados_window2.overrideredirect(True)
        toplevel_offsetx, toplevel_offsety = self.winfo_x(), self.winfo_y()
        padx = -10 # the padding you need.
        pady = -10
        resultados_window2.geometry(f"+{toplevel_offsetx + padx}+{toplevel_offsety + pady}")
        width = self.winfo_screenwidth() - 80
        height = self.winfo_screenheight() - 80
        resultados_window2.geometry(str(width)+"x"+str(height))
        self.resultados_window2 = resultados_window2  

        l_frame = CTkFrame(resultados_window2, bg_color='#E0E0E0', fg_color='#E0E0E0', border_color='#69a3d6', border_width=3)
        l_frame.grid(row=8, column=4, columnspan=4, pady=(200, 20), padx=100, sticky="ew")
        l = CTkLabel(l_frame, text="Cargando...", bg_color="#E0E0E0", fg_color="#E0E0E0", text_color="black", font=("Arial", 50, "bold"))
        l.grid(row=3, column=6, columnspan=6, pady=(200, 5), padx=200, sticky="w")
        progressbar = CTkProgressBar(resultados_window2, orientation="horizontal")
        progressbar.grid(row=10, column=6, pady=10, padx=20, sticky="n")
        progressbar.start()
        l.pack()
        self.update()
        resultados_window2.after(710, self.save_to_yaml())


    def ocultarSpinner(self):
        self.resultados_window2.destroy()


    # Function to create and show the popup
    def ventana_final_popup(self):
        popup_final = ctk.CTkToplevel(self)
        popup_final.geometry("800x300")
        popup_final.attributes('-topmost', True)

        popup_final.config(bg="#E0E0E0")
        popup_final.grid_columnconfigure(0, weight=1)
        popup_final.grid_rowconfigure(0, weight=1)

        frame_labels = CTkFrame(popup_final, bg_color="#E0E0E0", fg_color="#E0E0E0")
        frame_labels.grid(row= 0, column = 0, columnspan = 3)

        nombre_label = CTkLabel(frame_labels, text="Has creado el siguiente proyecto " + self.entry_code.get(), fg_color="#E0E0E0", text_color="black", font=("Arial", 10, "bold"))
        nombre_label.grid(row=0, column=2,  pady=(0, 0))

        war_label = CTkLabel(frame_labels, text="El war del proyecto es " + self.entry_war.get(), fg_color="#E0E0E0", text_color="black", font=("Arial", 10, "bold"))
        war_label.grid(row=1, column=2, pady=(10, 5))

        ruta_label = CTkLabel(frame_labels, text="Has guardado el proyecto en la ruta " + self.entry_location.get(),  fg_color="#E0E0E0", text_color="black", font=("Arial", 10, "bold"))
        ruta_label.grid(row=2, column=2,  pady=(10, 5))


        ruta = base_path + "\logs"
        ruta_label = CTkLabel(frame_labels, text="Para mas informacion consultar los logs en la ruta " + ruta ,  fg_color="#E0E0E0", text_color="black", font=("Arial", 10, "bold"))
        ruta_label.grid(row=3, column=2,  pady=(10, 5))

        frame_boton = CTkFrame(popup_final, bg_color="#E0E0E0", fg_color="#E0E0E0")
        frame_boton.grid(row = 1, column= 0, columnspan = 3)

        close_button = ctk.CTkButton(frame_boton, text="Volver al menu", command= lambda : m.MainMenuLoop(self), bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        close_button.grid(row = 0, column = 1, pady = (30,30), sticky= 's')



if __name__ == "__main__":

    app = Paso1()
    app.mainloop()

