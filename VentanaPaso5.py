import customtkinter as ctk
import tkinter as tk
from customtkinter import *
import plugin.utils as utl
from pathlib import Path
from copier import Worker
import logging
from datetime import datetime
self = CTk()

ruta_classes = utl.readConfig("RUTA", "ruta_classes")

class Paso5(CTk):
    def __init__(self, main_menu):
        super().__init__()


        self.main_menu = main_menu
    # Create the main window
        self.title("Crear nueva aplicación")
        self.geometry("900x700")
        self.resizable(width=False, height=False)
        self.rowconfigure(5, weight=1)
        
        # Configurar el color de fondo de la ventana
        self.config(bg="#FFFFFF")
        self.columnconfigure(1, weight=1)

        configuration_frame = CTkFrame(self, bg_color="black")
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")

        configuration_label = CTkLabel(configuration_frame,  text="Añadir un módulo EJB a la aplicación", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        self.configuration_warning = CTkLabel(configuration_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.grid(row=0, column=3, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard genera un nuevo modulo EJB y lo añade a un EAR existente")
        description_label.grid(row=1, column=0, columnspan=3, pady=(10, 5), padx=20, sticky="w")

        rutaActual = utl.rutaActual(__file__)
        textRutaNegocio = rutaActual
        textRutaControlador = rutaActual
        ruta_classes = utl.readConfig("RUTA", "ruta_classes")
        ruta_war = utl.readConfig("RUTA", "ruta_war")
        if(ruta_classes != None and ruta_classes != ""):
           textRutaNegocio = ruta_classes 
        archivoClases = utl.buscarArchivo(textRutaNegocio,"EAR") 
        archivoWar = utl.buscarArchivo(textRutaControlador,"War") 
        if(archivoClases != '' ):
           textRutaNegocio = textRutaNegocio+"\\"+archivoClases 
        else:
            textRutaNegocio = ""


        # EAR to bind
        ear_label = CTkLabel(self, text="EAR a vincular:", bg_color='#FFFFFF', text_color="black", font=("Arial", 12, "bold"))
        ear_label.grid(row=2, column=0, sticky="w", padx= (20,20), pady=(30, 0))
        self.ear_entry = CTkEntry(self, bg_color='#599398', fg_color='#599398', border_color='#599398', height=2.5, border_width=3, text_color="black" )
        self.ear_entry.grid(row=2, column=1, padx=(30,180), pady=(5, 2), sticky="ew")
        self.ear_entry.insert(0, textRutaNegocio)
        ear_button = CTkButton(self, text="Buscar Proyecto", command= lambda : self.buscar_archivos(self.selectDirectory(self.ear_entry.get())), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', text_color="black", font=("Arial", 12, "bold"), width= 100, height=23)
        ear_button.grid(row=2, column=1, sticky="e", padx=(100, 20))
        self.ear_entry.configure(state="disabled")

        # EJB proyect
        sv = StringVar(self)
        sv.trace_add("write", lambda name, index, mode, sv=lambda:sv: self.update_entry())
        ejb_name_label = CTkLabel(self, text="Nombre del proyecto EJB:", bg_color='#FFFFFF', text_color="black", font=("Arial", 12, "bold"))
        ejb_name_label.grid(row=3, column=0, sticky="w", padx= (20,20), pady=5)
        self.ejb_name_entry = CTkEntry(self,textvariable=sv, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black" )
        self.ejb_name_entry.grid(row=3, column=1, padx=(30,180), pady=(5, 2), sticky="ew")
        
        # Full EJB module
        full_ejb_name_label = CTkLabel(self, text="Nombre Completo del modulo EJB:", bg_color='#FFFFFF', text_color="black", font=("Arial", 12, "bold"))
        full_ejb_name_label.grid(row=4, column=0, sticky="w", padx= (20,20), pady=5)
        self.full_ejb_name_entry = CTkEntry(self, bg_color='#599398', fg_color='#599398', border_color='#599398', height=2.5, border_width=3, text_color="black" )
        self.full_ejb_name_entry.grid(row=4, column=1, padx=(30,180), pady=(5, 2), sticky="ew")
        self.full_ejb_name_entry.configure(state="disabled")

        # Buttons
        buttons_frame = ctk.CTkFrame(self, fg_color="#FFFFFF", bg_color="#FFFFFF")
        buttons_frame.grid(row=5, column=0, columnspan=2, pady=10, sticky = "ew")
        
        back_button = CTkButton(buttons_frame, text="Atrás", command=lambda: self.cancelar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        back_button.grid(row=0, column=0, padx=(650, 5), pady = (300, 0))

        finish_button = CTkButton(buttons_frame, text="Terminar", command= lambda: self.save_to_yaml(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        finish_button.grid(row=0, column=2, padx=(5, 5), pady = (300, 0))

        # --- FRAME 4: FOOTER DE VERSIÓN ---
        version_frame = CTkFrame(self, fg_color="#FFFFFF", bg_color="#FFFFFF")
        version_frame.grid(row=6, column=0, columnspan=2, sticky="ew", padx=10, pady=(0,10))
        version_frame.grid_columnconfigure(0, weight=1)
        version_frame.grid_columnconfigure(1, weight=0)
        version_button = CTkButton(version_frame,
                                text="Versión 6.2.0",
                                bg_color="#FFFFFF",
                                fg_color="#84bfc4",
                                border_color="#84bfc4",
                                text_color="black",
                                font=("Arial", 12, "bold"),
                                hover_color="#84bfc4")
        version_button.grid(row=0, column=1, sticky="e")
    def update_entry(self):
        if self.ear_entry.get() != '' and self.ejb_name_entry.get() != '':
            content = self.ejb_name_entry.get()
            array_proyect = self.ear_entry.get().split("/")
            proyect_name = array_proyect[len(array_proyect)-1].replace("EAR","")            
            self.full_ejb_name_entry.configure(state="normal")
            self.full_ejb_name_entry.delete(0, tk.END)
            self.full_ejb_name_entry.insert(0, proyect_name+content+"EJB")
            self.full_ejb_name_entry.configure(state="disabled")

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.withdraw()
        self.quit()
        self.main_menu.MainMenuLoop()


    def buscar_archivos(self, ruta_personalizada = None):
        files = None
        """Busca archivos con terminación 'Classes' en la misma ruta del script Python."""
        if ruta_personalizada == None:
            try:
                files = [file for file in os.listdir(ruta_classes) if file.endswith("EAR")]
            except:
                print("No encontro la ruta: " + ruta_classes)
            self.mostrar_resultados(files, ruta_classes)
        else:
            try:
                files = [file for file in os.listdir(ruta_personalizada) if file.endswith("EAR")]
            except:
                print("No encontro la ruta: " + ruta_personalizada)    
            self.mostrar_resultados(files,ruta_personalizada)

    def selectDirectory(self,directory):
        if(directory == ""):
            return directory
        else:
            par = Path(directory)
            return str(par.parent)
        
    def mostrar_resultados_scrollbar(self, files, ruta):
        resultados_window = ctk.CTkToplevel(self)
        resultados_window.title("Resultados de Búsqueda")
        resultados_window.geometry("600x300")
        resultados_window.configure(corner_radius=10, fg_color="#FFFFFF", border_color="#84bfc4", border_width=4)
        resultados_window.attributes('-topmost', True)  # Asegura que la ventana emergente se muestre al frente

        # Variable para almacenar el archivo seleccionado
        selected_file = tk.StringVar(value=None)

        # Frame para contener los radiobuttons con scrollbar
        scrollbar_container = ctk.CTkFrame(resultados_window, fg_color="#FFFFFF", bg_color="#FFFFFF")
        scrollbar_container.grid(row=1, column=0, columnspan=3, sticky="nsew")
        scrollbar_container.grid_columnconfigure(0, weight=1)
        scrollbar_container.grid_rowconfigure(0, weight=1)
        
        scrollbar_resumen = ctk.CTkScrollableFrame(scrollbar_container, fg_color="#E0E0E0", scrollbar_fg_color="#E0E0E0")
        scrollbar_resumen.pack(fill="both", expand=True, padx=10, pady=10)

        # Descripción
        desc_label = ctk.CTkLabel(scrollbar_resumen, text="Seleccione un EAR", text_color="black")
        desc_label.pack(fill="x", pady=(5, 1), padx=20, anchor="w")
        
        if ruta != '':
            desc_label2 = ctk.CTkLabel(scrollbar_resumen, text="(" + ruta + ")", text_color="black")
            desc_label2.pack(fill="x", pady=(0, 2), padx=30, anchor="w")

        # Añadir radiobuttons para cada archivo
        if files and len(files) > 0:
            for file in files:
                radiobutton = ctk.CTkRadioButton(scrollbar_resumen, text=file, variable=selected_file, value=file, border_color='#84bfc4', fg_color='#84bfc4', text_color="black", font=("Arial", 12, "bold"))
                radiobutton.pack(fill="x", padx=60, pady=3, anchor="w")
        else:
            texto = "Esta ruta no contiene ningún EAR"  
            desc_label3 = CTkLabel(scrollbar_container, text=texto,text_color="red")
            desc_label3.pack(fill="x", pady=(0, 2), padx=30, anchor="w")

        # Botones de acción en el pie de página
        button_frame = ctk.CTkFrame(resultados_window, fg_color="#FFFFFF", border_color="#84bfc4")
        button_frame.grid(row=2, column=0, columnspan=3, sticky="ew", pady=20)
        
        buscar_button = ctk.CTkButton(button_frame, text="Buscar", command=lambda: self.open_file_explorer(resultados_window), fg_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        buscar_button.pack(side="left", padx=10, expand=True)
        
        cancel_button = ctk.CTkButton(button_frame, text="Cancelar", command=resultados_window.destroy, fg_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        cancel_button.pack(side="right", padx=10, expand=True)
        
        accept_button = ctk.CTkButton(button_frame, text="Aceptar", command=lambda: self.aceptar(resultados_window, selected_file.get(), ruta), fg_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        accept_button.pack(side="right", padx=10, expand=True)

    def mostrar_resultados(self, files,ruta):



        if files != None and len(files) > 6:
            self.mostrar_resultados_scrollbar(files, ruta)

        else:

            """Muestra los archivos encontrados en una nueva ventana con radiobuttons."""

            resultados_window = ctk.CTkToplevel(self)
            resultados_window.title("Resultados de Búsqueda")
            resultados_window.geometry("600x300")
            resultados_window.configure(corner_radius=10, fg_color="#FFFFFF", border_color="#84bfc4", border_width=4)
            
            resultados_window.attributes('-topmost', True)  # Asegura que la ventana emergente se muestre al frente

            # Variable para almacenar el archivo seleccionado
            selected_file = tk.StringVar(value=None)

            # Frame para contener los radiobuttons
            file_frame = ctk.CTkFrame(resultados_window, fg_color="#FFFFFF", border_color="#84bfc4")
            file_frame.pack(fill="both", expand=True)
            desc_label = CTkLabel(file_frame, text="Seleccione un EAR ", text_color= "black")
            desc_label.grid(row=0, column=0, columnspan=3, pady=(5, 1), padx=20, sticky="w")
            if (ruta != ''):
                desc_label2 = CTkLabel(file_frame, text="(" + ruta +")" , text_color= "black")
                desc_label2.grid(row=1, column=0, columnspan=3, pady=(0,2), padx=30, sticky="w")

            # Añadir radiobuttons para cada archivo
            if(files != None and len(files) > 0):
                for index, file in enumerate(files):
                    radiobutton = ctk.CTkRadioButton(file_frame, text=file, variable=selected_file, value=file, border_color='#84bfc4', fg_color='#84bfc4', text_color= "black", font=("Arial", 12, "bold"))
                    radiobutton.grid(row=index + 3, column=0, sticky="w", padx=60, pady=3)
            else:    
    
                texto = "Esta ruta no contiene ningún EAR"  
                desc_label3 = CTkLabel(file_frame, text=texto,text_color="red")
                desc_label3.grid(row=3, column=0, columnspan=3, pady=(0,2), padx=30, sticky="w")
            # Botones de acción en el pie de página
            button_frame = ctk.CTkFrame(resultados_window, fg_color="#FFFFFF", border_color="#84bfc4")
            button_frame.pack(fill="x", pady=20)
                
            buscar_button = ctk.CTkButton(button_frame, text="Buscar", command= lambda: self.open_file_explorer(resultados_window), fg_color='#84bfc4',  hover_color='#41848a', text_color= "black", font=("Arial", 12, "bold")) 
            buscar_button.pack(side="left", padx=10, expand=True)
                
            cancel_button = ctk.CTkButton(button_frame, text="Cancelar", command=resultados_window.destroy, fg_color='#84bfc4',  hover_color='#41848a', text_color= "black", font=("Arial", 12, "bold"))
            cancel_button.pack(side="right", padx=10, expand=True)
            accept_button = ctk.CTkButton(button_frame, text="Aceptar", command=lambda: self.aceptar(resultados_window, selected_file.get(),ruta), fg_color='#84bfc4',  hover_color='#41848a', text_color= "black", font=("Arial", 12, "bold"))
            accept_button.pack(side="right", padx=10, expand=True) 


    def open_file_explorer(self, frame):
        # Esta función se llama cuando el usuario hace clic en "Buscar"
        # Abre un diálogo para seleccionar un directorio
        frame.destroy()
        directory = filedialog.askdirectory(parent=self)      
        if directory:  # Si se selecciona un directorio
            selected_directory = directory  # Guardar la ruta del directorio seleccionado
            self.buscar_archivos(selected_directory)
            print(f"Directorio seleccionado: {selected_directory}")
        else:
            print("No se seleccionó ningún directorio.")

    def aceptar(self, frame, selected_file, ruta):
        if selected_file:
            print(f"Archivo seleccionado: {selected_file}")
            self.ear_entry.configure(state="normal")
            self.ear_entry.delete(0, "end")
            self.ear_entry.insert(0, ruta+"/"+selected_file)
            self.ear_entry.configure(state="disabled")
            self.archivoClases = selected_file
            frame.destroy()

        else:
            print("No se seleccionó ningún archivo.")


    def save_to_yaml(self):      
        if self.ear_entry.get() == '' or self.ejb_name_entry.get() == '':
            self.configuration_warning.configure(text="El nombre del proyecto y el ear son obligatorios")
            self.configuration_warning.configure(text_color ="red")
            return FALSE

        inicio = datetime.now()
        array_proyect = self.ear_entry.get().split("/")
        self.proyect_name = array_proyect[len(array_proyect)-1].replace("EAR","")
        self.rutaDest = self.ear_entry.get().replace("/"+self.proyect_name+"EAR","")
        yaml_data = {
            "project_name": self.proyect_name,
            "ejb_project_name": self.ejb_name_entry.get(),
        }

        rutaPath = utl.rutaActual(__file__)
        directorio_actual = rutaPath + "\\templates\\proyectoPaso5"
        filesExcludes = []

        #destinoPath = self.entry_location.get()
        #if(destinoPath == ''):
        destinoPath = rutaPath
        now = datetime.now()
        dates = now.strftime('%d-%b-%Y %H:%M:%S') 
        print('Inicio: proyecto Creando... ' +yaml_data["project_name"]+ yaml_data["ejb_project_name"]+  "EJB")    
        with Worker(src_path=directorio_actual,overwrite=True, dst_path=self.rutaDest, data=yaml_data,exclude=filesExcludes) as worker:
            logging.info('Inicio: Crear proyecto: ' + yaml_data["ejb_project_name"])
            worker.template.version = ": 1.0 Paso 1 ::: "+dates
            worker.run_copy()
            logging.info('Fin: Crear proyecto: ' + yaml_data["project_name"]+ yaml_data["ejb_project_name"]+  "EJB")
            #guardar ultima ruta creada5
            utl.writeConfig(
                "RUTA", {"ruta_classes":destinoPath,"ruta_war":destinoPath,"ruta_ultimo_proyecto":destinoPath})
        #self.ocultarSpinner()
        #self.close_loading_frame()
        print('Fin: proyecto Creado: ' + yaml_data["project_name"]+ yaml_data["ejb_project_name"]+  "EJB")
        fin = datetime.now()
        logging.info('Tiempo: proyecto Creado en: ' + str((fin-inicio).total_seconds()) + " segundos")
        now = datetime.now()
        dates = now.strftime('%d-%b-%Y %H:%M:%S')
        print(F"Final: paso 1 creado ::: "+dates,file=sys.stderr)
        sys.stderr.flush()
    
        self.ventana_final_popup()

    def cerrar_ventana(self):
        # Cancela todos los eventos pendientes, y cierrar la ventana
        self.withdraw()
        sys.exit(0)        


    def ventana_final_popup(self):
        # Guardar los valores de los widgets de entrada
        
        ejb_full_value = self.full_ejb_name_entry.get()
   

        # Destruir todos los widgets hijos del frame actual
        for widget in self.winfo_children():
            widget.destroy()

        # Crear un nuevo frame que ocupe toda la ventana
        frame_final = CTkFrame(self, bg_color="#FFFFFF", fg_color="#FFFFFF")
        frame_final.pack(fill="both", expand=True)

        # Frame interno centrado
        frame_center = CTkFrame(frame_final, bg_color="#FFFFFF", fg_color="#FFFFFF")
        frame_center.pack(expand=True)


        nombre_label = CTkLabel(frame_center, text="Has creado el siguiente proyecto  EJB: " , fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        nombre_label.pack(pady=(0, 0), padx=30)
        
        nombre_proyecto_label = CTkLabel(frame_center, text= ejb_full_value, fg_color="#FFFFFF", text_color="black", font=("Arial", 14, "bold"))
        nombre_proyecto_label.pack(pady=(0, 0), padx=30)

        war_label = CTkLabel(frame_center, text="El proyecto EAR es" , fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        war_label.pack(pady=(0, 0), padx=30)

        nombre_war_label = CTkLabel(frame_center, text= self.proyect_name, fg_color="#FFFFFF", text_color="black", font=("Arial", 14, "bold"))
        nombre_war_label.pack(pady=10, padx=30) 

        ruta_label = CTkLabel(frame_center, text="Has guardado el proyecto en la ruta ", fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ruta_label.pack(pady=10, padx=30)

        ruta_label = CTkLabel(frame_center, text=self.rutaDest , fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ruta_label.pack(pady=10, padx=30)

        #ruta = base_path + "/logs"
        # logs_label = CTkLabel(frame_center, text="Para más información consultar los logs en la ruta " + ruta, fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        # logs_label.pack(pady=10, padx=40)

        frame_boton = CTkFrame(frame_center, bg_color="#FFFFFF", fg_color="#FFFFFF")
        frame_boton.pack(pady=10)

        menu_button = ctk.CTkButton(frame_boton, text="Volver al menú", command=lambda: self.cancelar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width=100, height=25)
        menu_button.pack(side="right", padx=(6, 5), pady=(40, 10))

        close_button = ctk.CTkButton(frame_boton, text="Cerrar", command=lambda: self.cerrar_ventana(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width=100, height=25)
        close_button.pack(side="left", padx=(5, 5), pady=(40, 10))

        # Mostrar el nuevo frame
        frame_final.pack(fill="both", expand=True)


    
if __name__ == "__main__":

    app = Paso5()
    app.mainloop()
