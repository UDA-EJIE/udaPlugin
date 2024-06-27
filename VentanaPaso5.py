import customtkinter as ctk
import tkinter as tk
from customtkinter import *
import plugin.utils as utl
from pathlib import Path
self = CTk()

class Paso5(CTk):
    def __init__(self):
        super().__init__()
    # Create the main window
        self.title("Crear nueva aplicación")
        self.geometry("900x700")
        self.resizable(width=False, height=False)
        
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

        rutaActual = utl.rutaActual(__file__)
        textRutaNegocio = rutaActual
        textRutaControlador = rutaActual
        ruta_classes = utl.readConfig("RUTA", "ruta_classes")
        ruta_war = utl.readConfig("RUTA", "ruta_war")
        if(ruta_classes != None and ruta_classes != ""):
           textRutaNegocio = ruta_classes 
        archivoClases = utl.buscarArchivo(textRutaNegocio,"EARClasses") 
        archivoWar = utl.buscarArchivo(textRutaControlador,"War") 
        if(archivoClases != '' ):
           textRutaNegocio = textRutaNegocio+"\\"+archivoClases 
        else:
            textRutaNegocio = ""


        # EAR to bind
        ear_label = CTkLabel(self, text="EAR a vincular:", bg_color='#E0E0E0', text_color="black", font=("Arial", 12, "bold"))
        ear_label.grid(row=2, column=0, sticky="w", padx= (20,20), pady=5)
        self.ear_entry = CTkEntry(self, bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', height=2.5, border_width=3, text_color="black" )
        self.ear_entry.grid(row=2, column=1, padx=(30,180), pady=(5, 2), sticky="ew")
        self.ear_entry.insert(0, textRutaNegocio)
        ear_button = CTkButton(self, text="Buscar Proyecto", command= lambda : self.buscar_archivos(self.selectDirectory(self.ear_entry.get())), bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        ear_button.grid(row=2, column=1, sticky="e", padx=(100, 20))

        # WAR name
        war_name_label = CTkLabel(self, text="Nombre del WAR:", bg_color='#E0E0E0', text_color="black", font=("Arial", 12, "bold"))
        war_name_label.grid(row=3, column=0, sticky="w", padx= (20,20), pady=5)
        war_name_entry = CTkEntry(self, bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', height=2.5, border_width=3, text_color="black" )
        war_name_entry.grid(row=3, column=1, padx=(30,180), pady=(5, 2), sticky="ew")

        # Full WAR name
        full_war_name_label = CTkLabel(self, text="Nombre Completo del WAR:", bg_color='#E0E0E0', text_color="black", font=("Arial", 12, "bold"))
        full_war_name_label.grid(row=4, column=0, sticky="w", padx= (20,20), pady=5)
        full_war_name_entry = CTkEntry(self, bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', height=2.5, border_width=3, text_color="black" )
        full_war_name_entry.grid(row=4, column=1, padx=(30,180), pady=(5, 2), sticky="ew")

        # Buttons
        buttons_frame = ctk.CTkFrame(self, fg_color="#E0E0E0", bg_color="#E0E0E0")
        buttons_frame.grid(row=5, column=0, columnspan=2, pady=10)
        
        back_button = CTkButton(buttons_frame, text="Back", bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        back_button.grid(row=0, column=0, padx=5)
        next_button = CTkButton(buttons_frame, text="Next", state="disabled", bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        next_button.grid(row=0, column=1, padx=5)
        finish_button = CTkButton(buttons_frame, text="Finish", bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        finish_button.grid(row=0, column=2, padx=5)
        cancel_button = CTkButton(buttons_frame, text="Cancel", bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        cancel_button.grid(row=0, column=3, padx=5)


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

    def buscar_archivos(self, ruta_personalizada = None):
        files = None
        """Busca archivos con terminación 'Classes' en la misma ruta del script Python."""
        if ruta_personalizada == None:
            try:
                files = [file for file in os.listdir(ruta_classes) if file.endswith("Classes")]
            except:
                print("No encontro la ruta: " + ruta_classes)
            self.mostrar_resultados(files, ruta_classes)
        else:
            try:
                files = [file for file in os.listdir(ruta_personalizada) if file.endswith("Classes")]
            except:
                print("No encontro la ruta: " + ruta_personalizada)    
            self.mostrar_resultados(files,ruta_personalizada)

    def selectDirectory(self,directory):
        if(directory == ""):
            return directory
        else:
            par = Path(directory)
            return str(par.parent)
        
    def mostrar_resultados(self, files,ruta):
        """Muestra los archivos encontrados en una nueva ventana con radiobuttons."""

        resultados_window = ctk.CTkToplevel(self)
        resultados_window.title("Resultados de Búsqueda")
        resultados_window.geometry("600x300")
        resultados_window.attributes('-topmost', True)  # Asegura que la ventana emergente se muestre al frente

        # Variable para almacenar el archivo seleccionado
        selected_file = tk.StringVar(value=None)

        # Frame para contener los radiobuttons
        file_frame = ctk.CTkFrame(resultados_window)
        file_frame.pack(fill="both", expand=True)

        desc_label = CTkLabel(file_frame, text="Seleccione un Archivo ")
        desc_label.grid(row=0, column=0, columnspan=3, pady=(5, 1), padx=20, sticky="w")
        if (ruta != ''):
            desc_label2 = CTkLabel(file_frame, text="(" + ruta +")")
            desc_label2.grid(row=1, column=0, columnspan=3, pady=(0,2), padx=30, sticky="w")

        # Añadir radiobuttons para cada archivo
        if(files != None and len(files) > 0):
            for index, file in enumerate(files):
                radiobutton = ctk.CTkRadioButton(file_frame, text=file, variable=selected_file, value=file)
                radiobutton.grid(row=index + 3, column=0, sticky="w", padx=60, pady=3)

        # Botones de acción en el pie de página
        button_frame = ctk.CTkFrame(resultados_window)
        button_frame.pack(fill="x", pady=20)
            
        buscar_button = ctk.CTkButton(button_frame, text="Buscar", command= lambda: self.open_file_explorer(resultados_window)) 
        buscar_button.pack(side="left", padx=10, expand=True)
            
        cancel_button = ctk.CTkButton(button_frame, text="Cancelar", command=resultados_window.destroy)
        cancel_button.pack(side="right", padx=10, expand=True)
        accept_button = ctk.CTkButton(button_frame, text="Aceptar", command=lambda: self.aceptar(resultados_window, selected_file.get(), ruta))
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


    
if __name__ == "__main__":

    app = Paso5()
    app.mainloop()


