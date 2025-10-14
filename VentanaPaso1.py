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
import threading
import subprocess
import shutil
import re

self = CTk()

base_path = os.path.dirname(os.path.abspath(__file__))
logsPath = os.path.join(base_path, 'logs\\log.log')

ansi_escape = re.compile(r'\x1b\[[0-9;]*[A-Za-z]')
class CustomStderr:
    
    def __init__(self, func,  master):
        self.func = func
        self.log_file = open(logsPath, 'a')
        self.cont = 0
        self.total_pasos = 1858 #actualizar con los templates para que sea exacto
        self.pasos_por_parte = 10 # 10
        self.master = master

    def write(self, message):
        if message.strip() != '' and len(ansi_escape.findall(message)) == 0:  # Evitar imprimir líneas vacías
            if not message.endswith('\n'):
              message += '\n'
            self.func(message)
            self.log_file.write(message) 
            self.cont = self.cont + 1 
            if self.cont % self.pasos_por_parte == 0:  
                porcentaje = (self.cont / self.total_pasos)  
                self.master.update_progress(porcentaje)     

    def flush(self):
        self.log_file.flush()
        pass  # No se necesita hacer nada especial para flush en este caso
        

stderr_lambda = lambda message: print(message)


class Paso1(CTk):
    def __init__(self, main_menu):

        self.cont = 0
        sys.stderr = CustomStderr(stderr_lambda,self)        
        #paso 1 se muestra los logs en 2 sitios
        super().__init__()

        self.title("Crear nueva aplicación")
        self.geometry("900x700")
        self.resizable(width=False, height=False)
        
        self.main_menu = main_menu
        # Configurar el color de fondo de la ventana
        self.config(bg="#FFFFFF")

        self.columnconfigure(1, weight=1)

        configuration_frame = CTkFrame(self, bg_color="black")
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")

        configuration_label = CTkLabel(configuration_frame,  text="Crear nueva aplicación", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        self.configuration_warning = CTkLabel(configuration_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.grid(row=0, column=3, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar")
        description_label.grid(row=1, column=0, columnspan=3, pady=(10, 5), padx=20, sticky="w")

        code_label = CTkLabel(self, text="Código de aplicación:", bg_color='#FFFFFF', text_color="black", font=("Arial", 12, "bold"))
        code_label.grid(row=2, column=0, sticky="w", padx=(20, 10), pady=(20, 2))
        self.entry_code = CTkEntry(self, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black" )
        self.entry_code.grid(row=2, column=1, padx=(30, 400), pady=(20, 2), sticky="ew")

        self.use_default_location = tk.BooleanVar()
        location_checkbox = CTkCheckBox(self,hover=True, text="Usar localización por defecto", checkbox_height=20, checkbox_width=20, border_color='#84bfc4',fg_color='#84bfc4', variable=self.use_default_location, command=self.toggle_textbox, bg_color='#FFFFFF', text_color="black", font=("Arial", 12, "bold"))
        location_checkbox.grid(row=3, column=0, columnspan=2, pady=(5, 2), padx=20, sticky="w")
        location_checkbox.select()

        localizacion_label = CTkLabel(self, text="Localización:",  bg_color='#FFFFFF', text_color="black", font=("Arial", 12, "bold"))
        localizacion_label.grid(row=4, column=0, sticky="w", padx=(20, 10), pady=(5, 2))
        self.entry_location = CTkEntry(self, state="normal", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3)
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

        self.location_button = CTkButton(self,state="disabled", text="Explorar", command=self.browse_location, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        self.location_button.grid(row=4, column=1, pady=(5, 2), padx= (0, 20), sticky="e")

        war_label = CTkLabel(self, text="Nombre del WAR:", bg_color='#FFFFFF', text_color="black", font=("Arial", 12, "bold"))
        war_label.grid(row=5, column=0, sticky="w", padx=(20, 10), pady=(5, 30))
        self.entry_war = CTkEntry(self, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        self.entry_war.grid(row=5, column=1, padx=(30, 20), pady=(5, 30), sticky="ew")

        languages_frame = CTkFrame(self,  bg_color='#FFFFFF', fg_color='#FFFFFF', border_color='#84bfc4', border_width=3)
        languages_frame.grid(row=6, column=0, columnspan=2, pady=(5, 30), padx=20, sticky="ew")

        # Crear un marco interno para organizar los widgets dentro del contenedor "Idiomas"
        self.idiomas_inner_frame = CTkFrame(languages_frame, fg_color='#FFFFFF', bg_color='#FFFFFF', border_color='#84bfc4')
        self.idiomas_inner_frame.grid(row=0, column=0, padx=10, pady=10, sticky="nsew")

        # Crear un widget Label encima del borde del marco
        label_on_border = CTkLabel(self, text="Idiomas", bg_color="#FFFFFF", fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
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
            CTkCheckBox(self.idiomas_inner_frame,state=stateCheck, text=lang_option, variable=lang_var, checkbox_height=20, checkbox_width=20, border_color='#84bfc4', bg_color='#FFFFFF', fg_color='#84bfc4', text_color="black", font=("Arial", 12, "bold")).grid(row=0, column=i, padx=5, pady=(10, 2), sticky="w")

        default_language_label = CTkLabel(self.idiomas_inner_frame, text="Idioma por defecto:", text_color="black", font=("Arial", 12, "bold"))
        default_language_label.grid(row=7, column=0, sticky="w", padx=(10, 10), pady=(25, 2))
        self.default_language_var = tk.StringVar()

        self.default_language_combobox = self.update_default_language_options()
        security_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color='#FFFFFF', border_color='#84bfc4', border_width=3)
        security_frame.grid(row=8, column=0, columnspan=2, pady=(5, 10), padx=20, sticky="ew")

        # Crear un widget Label encima del borde del marco
        labelSecurityFrame = CTkLabel(self, text="Seguridad con XLNetS", bg_color="#FFFFFF", fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        labelSecurityFrame.place(in_=security_frame, anchor="sw" )

        self.security_var = tk.StringVar(value="Si")
        self.security_yes_radio = CTkRadioButton(security_frame, text="Sí", value="Si", variable=self.security_var, text_color="black", fg_color='#84bfc4', radiobutton_height= 18 , radiobutton_width= 18)
        self.security_yes_radio.grid(row=0, column=0, padx=(20, 0), pady=(20, 10), sticky="nsew")
        security_no_radio = CTkRadioButton(security_frame, text="No", value="No", variable=self.security_var, text_color="black", fg_color='#84bfc4', radiobutton_height= 18 , radiobutton_width= 18)
        security_no_radio.grid(row=0, column=1, padx=5, pady=(20, 10), sticky="nsew")

        # Plantillas Tiles o Thymeleaf
        plantillar_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color='#FFFFFF', border_color='#84bfc4', border_width=3)
        plantillar_frame.grid(row=9, column=0, columnspan=2, pady=(20, 5), padx=20, sticky="ew")
        labelPlantillarFrame = CTkLabel(self, text="Plantillar con Tiles o Thymeleaf", bg_color="#FFFFFF", fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        labelPlantillarFrame.place(in_=plantillar_frame, anchor="sw" )

        self.plantillar_var = tk.StringVar(value="tiles")
        self.plantillar_yes_radio = CTkRadioButton(plantillar_frame, text="Tiles", value="tiles", variable=self.plantillar_var, text_color="black", fg_color='#84bfc4', radiobutton_height= 18 , radiobutton_width= 18)
        self.plantillar_yes_radio.grid(row=0, column=0, padx=(20, 0), pady=(20, 10), sticky="nsew")
        plantillar_no_radio = CTkRadioButton(plantillar_frame, text="Thymeleaf", value="thymeleaf", variable=self.plantillar_var, text_color="black", fg_color='#84bfc4', radiobutton_height= 18 , radiobutton_width= 18)
        plantillar_no_radio.grid(row=0, column=1, padx=5, pady=(20, 10), sticky="nsew")

        # botones terminar y cancelar
        finish_button = CTkButton(self, text="Terminar", command=lambda:self.mostrarSpinner(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        finish_button.grid(row=12, column=1, pady=(60, 0), padx=(560, 30), sticky = "se")

        cancel_button = CTkButton(self, text="Cancelar", command= lambda: self.cancelar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        cancel_button.grid(row=12, column=1, pady=(60, 0), padx=(300,150), sticky = "se")

    def update_progress(self,value):
        if self.progressbar.winfo_exists():
            self.progressbar.set(value)
            valor = value*100
            if(valor > 100):
                valor = 100
            self.percentage_label.configure(text=f"Cargando... {int(valor)}%")
            self.loading_frame.update_idletasks()
            self.update()

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.withdraw()
        self.quit()
        self.main_menu.MainMenuLoop()

    def cerrar(self):
        # Cancela todos los eventos pendientes
        self.withdraw()
        sys.exit(0)





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

        self.default_language_combobox = CTkComboBox(self.idiomas_inner_frame, values= idiomas_seleccionados, command=self.validarIdioma, fg_color='#84bfc4', text_color="black",state="readonly", font=("Arial", 12, "bold"))
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
            yaml_data["xlnets"] = True
        else:
            yaml_data["xlnets"] = False     

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
        filesExcludes.append("*gitkeep")
        defaultLanguage = self.default_language_combobox.get() 
        
        if  defaultLanguage == "Castellano":      
            yaml_data["defaultLanguage"] = "es"
        if  defaultLanguage == "Euskera":      
            yaml_data["defaultLanguage"] = "eu"
        elif  defaultLanguage == "Inglés":      
            yaml_data["defaultLanguage"] = "en"
        elif  defaultLanguage == "Francés":      
            yaml_data["defaultLanguage"] = "fr"
        else:
            yaml_data["defaultLanguage"] = "es"    

        yaml_data["availableLangs"] = availableLangs
        destinoPath = self.entry_location.get()
        if(destinoPath == ''):
            destinoPath = rutaPath

        if self.plantillar_var.get() == "tiles":
            filesExcludes.append("*.html")
            filesExcludes.append("HomeController.java")
            filesExcludes.append("GlobalModelAttributes.java")
        elif self.plantillar_var.get() == "thymeleaf": 
            filesExcludes.append("*.jsp")
            filesExcludes.append("tiles.xml")
        yaml_data["typeTemplate"]  =  self.plantillar_var.get()

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
           
        self.close_loading_frame()
        print('Fin: proyecto Creado: ' + yaml_data["project_name"]+yaml_data["war_project_name"])
        fin = datetime.now()
        logging.info('Tiempo: proyecto Creado en: ' + str((fin-inicio).total_seconds()) + " segundos")
        now = datetime.now()
        dates = now.strftime('%d-%b-%Y %H:%M:%S')
        print(F"Final: paso 1 creado ::: "+dates,file=sys.stderr)
        sys.stderr.flush()
    
        self.ventana_final_popup()
        
    def ejecutarBuild(self,fichBuild):
        if os.path.exists(fichBuild):
            print(f"El archivo {fichBuild} existe, ejecutando tarea maven...")  
            if shutil.which("mvn") is not None:
                print("El comando maven está disponible.")
            else:
                print("El comando de maven(mvn), no está en el PATH o no está instalado." )     
            try:
                result = subprocess.run(["mvn", "-s", fichBuild, "compile"], check=True)
                print("Salida estándar:", result.stdout)  # Salida de mvn
                print("Error estándar:", result.stderr)   # Cualquier error generado por mvn
            except subprocess.CalledProcessError as e:
                print(f"Error al ejecutar Maven: {e.stderr}")
            except Exception as e:
                # Captura cualquier otro error no específico
                print(f"Error inesperado: {str(e)}")    
            
    def close_loading_frame(self):
        if self.loading_frame:
            self.loading_frame.place_forget()  # Oculta el frame
            # Si no planeas reutilizar el frame, puedes destruirlo en su lugar
            self.loading_frame.destroy()
            self.loading_frame = None
        

        
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
        if(self.entry_code.get().isalnum() == False):
            self.configuration_warning.configure(text="Campo 'Código de aplicación' es solo alfanumérico")
            return FALSE
        if(self.entry_war.get().isalnum() == False):
            self.configuration_warning.configure(text="Campo 'Nombre del WAR' es solo alfanumérico")
            return FALSE
        if os.path.isdir(self.entry_location.get()) == False:
            self.configuration_warning.configure(text="La localización no existe")
            return FALSE
       
        # resultados_window2 = ctk.CTkToplevel(self)
        # resultados_window2.title("")
        # resultados_window2.attributes('-topmost', True)
        
        # self.title("Cargando...")
       
        # toplevel_offsetx, toplevel_offsety = self.winfo_x(), self.winfo_y()
        # padx = -20 # the padding you need.
        # pady = 50
        # self.update_idletasks()

        # monitors = get_monitors() 
        # width = self.winfo_width()
        # height = self.winfo_height()
        # x = self.winfo_x()
        # y = self.winfo_y()
        # # Ajustar tamaño y posición de la ventana Toplevel
        # #resultados_window2.geometry(f'{width}x{height}+{x}+{y}')
        # resultados_window2.geometry(f"+{toplevel_offsetx + padx}+{toplevel_offsety + pady}")
        # width = self.winfo_screenwidth() - 50
        # height = self.winfo_screenheight() -50
        # resultados_window2.geometry(str(width)+"x"+str(height))
        # #resultados_window2.overrideredirect(True)
        # resultados_window2.wm_attributes('-alpha',0.8)
        # self.resultados_window2 = resultados_window2  

        # Crear y configurar el Frame de carga
        self.loading_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color='#FFFFFF', border_color='#84bfc4', border_width=3)
        self.loading_frame.place(relx=0, rely=0, relwidth=1, relheight=1)

        self.percentage_label = CTkLabel(self.loading_frame, text="Cargando...", bg_color="#FFFFFF", fg_color="#FFFFFF", text_color="black", font=("Arial", 50, "bold"))
        self.percentage_label.place(relx=0.5, rely=0.5, anchor='center')
        
        self.progressbar = CTkProgressBar(self.loading_frame, orientation="horizontal")
        self.progressbar.place(relx=0.5, rely=0.5, anchor='center')
        #self.progressbar.start()
        self.percentage_label.pack()
        self.update()

        # Iniciar el proceso en segundo plano
        threading.Thread(target=self.save_to_yaml).start()


    def ocultarSpinner(self):
        self.resultados_window2.destroy()

    def close_toplevel(self):
        if self.resultados_window2:
            self.resultados_window2.destroy()
        self.destroy()


    
    def ventana_final_popup(self):
        # Guardar los valores de los widgets de entrada
        entry_code_value = self.entry_code.get()
        entry_war_value = self.entry_war.get()
        entry_location_value = self.entry_location.get()

        # Destruir todos los widgets hijos del frame actual
        try:
            for widget in self.winfo_children():
                widget.grid_forget()
        except Exception as e:
            logging.error('An exception occurred: destroy widget:')   

        # Crear un nuevo frame que ocupe toda la ventana
        frame_final = CTkFrame(self, bg_color="#FFFFFF", fg_color="#FFFFFF")
        frame_final.pack(fill="both", expand=True)

        # Frame interno centrado
        frame_center = CTkFrame(frame_final, bg_color="#FFFFFF", fg_color="#FFFFFF")
        frame_center.pack(expand=True)


        nombre_label = CTkLabel(frame_center, text="Has creado el siguiente proyecto: " , fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        nombre_label.pack(pady=(0, 0), padx=30)
        
        nombre_proyecto_label = CTkLabel(frame_center, text=entry_code_value, fg_color="#FFFFFF", text_color="black", font=("Arial", 14, "bold"))
        nombre_proyecto_label.pack(pady=(0, 0), padx=30)

        war_label = CTkLabel(frame_center, text="El war del proyecto es: " , fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        war_label.pack(pady=(0, 0), padx=30)

        nombre_war_label = CTkLabel(frame_center, text= entry_war_value, fg_color="#FFFFFF", text_color="black", font=("Arial", 14, "bold"))
        nombre_war_label.pack(pady=10, padx=30)

        ruta_label = CTkLabel(frame_center, text="Has guardado el proyecto en la ruta " + entry_location_value, fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ruta_label.pack(pady=10, padx=30)

        ruta = base_path + "/logs"
        logs_label = CTkLabel(frame_center, text="Para más información consultar los logs en la ruta " + ruta, fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        logs_label.pack(pady=10, padx=40)

        frame_boton = CTkFrame(frame_center, bg_color="#FFFFFF", fg_color="#FFFFFF")
        frame_boton.pack(pady=10)

        menu_button = ctk.CTkButton(frame_boton, text="Volver al menú", command=lambda: self.cancelar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width=100, height=25)
        menu_button.pack(side="right", padx=(6, 5), pady=(40, 10))

        close_button = ctk.CTkButton(frame_boton, text="Cerrar", command=lambda: self.cerrar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width=100, height=25)
        close_button.pack(side="left", padx=(5, 5), pady=(40, 10))

        # Mostrar el nuevo frame
        frame_final.pack(fill="both", expand=True)

if __name__ == "__main__":

    app = Paso1()
    app.mainloop()

