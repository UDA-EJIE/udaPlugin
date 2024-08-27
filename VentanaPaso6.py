import customtkinter as ctk
import tkinter as tk
from customtkinter import *
import plugin.utils as utl
from pathlib import Path
import re
import zipfile
import subprocess
import os
from datetime import datetime
import logging
from copier import Worker

self = CTk()
ruta_classes = utl.readConfig("RUTA", "ruta_classes")
class VentanaPaso6(CTk):
    def __init__(self, main_menu ):
        super().__init__()
        # Configurar la ventana principal
        self.title("Crear nueva aplicación")
        self.geometry("900x700")
        self.resizable(width=False, height=False)
        self.config(bg="#FFFFFF")

        self.main_menu = main_menu

        self.columnconfigure(1, weight=1)
        self.rowconfigure(4, weight=1 )

        configuration_frame = CTkFrame(self, bg_color="#FFFFFF")
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")

        configuration_label = CTkLabel(configuration_frame, text="Generar EJB Cliente", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(10, 5), padx=10, sticky="w")

        self.configuration_warning = CTkLabel(configuration_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.grid(row=0, column=3, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard genera el EJB Cliente de un servicio existente")
        description_label.grid(row=1, column=0, columnspan=3, pady=(5, 5), padx=10, sticky="w")

        ejb_frame = CTkFrame(self, bg_color="#FFFFFF", fg_color="#FFFFFF", border_color='#84bfc4', border_width=3)
        ejb_frame.grid(row=1, column=0, columnspan=3, sticky="ew", padx= (10,10), pady= (30,30))


        ejb_container_label = CTkLabel(ejb_frame, text="Proyecto EJB contenedor:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_container_label.grid(row=0, column=0, sticky="w", padx=(20, 5), pady=(10, 2))
        self.ejb_container_entry = CTkEntry(ejb_frame, bg_color='#599398', fg_color='#599398', border_color='#599398',width= 550, height=2.5, border_width=3, text_color="black")
        self.ejb_container_entry.grid(row=0, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")
        self.ejb_container_entry.configure(state="disabled")
        ejb_container_button = CTkButton(ejb_frame, text="Buscar Proyecto",command= lambda : self.buscar_archivos(self.selectDirectory(self.ejb_container_entry.get())), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=23)
        ejb_container_button.grid(row=0, column=2, sticky="e", padx=(5, 10), pady=(10, 2))

        ejb_remote_type_label = CTkLabel(ejb_frame, text="Tipo de EJB Remoto:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_remote_type_label.grid(row=1, column=0, sticky="w", padx=(20, 5), pady=(10, 2))

        ejb_remote_type_frame = CTkFrame(ejb_frame, bg_color='#FFFFFF', fg_color="#FFFFFF")
        ejb_remote_type_frame.grid(row=1, column=1, padx=(10, 5), pady=(10,10), sticky="w")

        ejb_remote_type_var = tk.StringVar(value="EJB 3.0")
        ejb3_rb = CTkRadioButton(ejb_remote_type_frame, text="EJB 3.0", variable=ejb_remote_type_var, value="EJB 3.0", text_color="black", fg_color='#84bfc4', radiobutton_height=18, radiobutton_width=18, bg_color='#FFFFFF')
        ejb3_rb.grid(row=0, column=0, padx=5)
        ejb2_rb = CTkRadioButton(ejb_remote_type_frame, text="EJB 2.0", variable=ejb_remote_type_var, value="EJB 2.0", text_color="black", fg_color='#84bfc4', radiobutton_height=18, radiobutton_width=18, bg_color='#FFFFFF')
        ejb2_rb.grid(row=0, column=1, padx=5)

        ejb_interface_label = CTkLabel(ejb_frame, text="Interface del EJB Remoto:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_interface_label.grid(row=2, column=0, sticky="w", padx=(10, 5), pady=(10, 10))
        self.ejb_interface_entry = CTkEntry(ejb_frame, bg_color='#599398', fg_color='#599398', border_color='#599398', width= 550, height=2.5, border_width=3, text_color="black")
        self.ejb_interface_entry.grid(row=2, column=1, padx=(10, 5), pady=(10, 10), sticky="ew")
        self.ejb_interface_entry.configure(state="disabled")
        self.ejb_interface_button = CTkButton(ejb_frame, text="Buscar Interface",command= lambda : self.buscar_archivos_interfaz(self.ejb_container_entry.get()), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold") , width= 100, height=23)
        self.ejb_interface_button.grid(row=2, column=2, sticky="e", padx=(5, 10), pady=(10, 10))
        self.ejb_interface_button.configure(state="disabled")


        servidor_despliegue_frame =  CTkFrame(self, bg_color="#FFFFFF", fg_color="#FFFFFF", border_color='#84bfc4', border_width=3)
        servidor_despliegue_frame.grid(row=3, column=0, columnspan=3, sticky="ew", pady = (30,30), padx= (10,10))
   
        # Crear un widget Label encima del borde del marco
        label_on_border = CTkLabel(self, text="Parámetros Servidor Despliegue", bg_color="#FFFFFF", fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        label_on_border.place(in_=servidor_despliegue_frame, anchor="sw")

        ip_label = CTkLabel(servidor_despliegue_frame, text="IP Servidor:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ip_label.grid(row=0, column=0, sticky="w", padx=(10, 5), pady=(10, 10))
        self.ip_entry = CTkEntry(servidor_despliegue_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', width=550, height=2.5, border_width=3, text_color="black")
        self.ip_entry.grid(row=0, column=1, padx=(10, 5), pady=(10, 10), sticky="ew")
        self.ip_entry.insert(0, "127.0.0.1")

        port_label = CTkLabel(servidor_despliegue_frame, text="Puerto:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        port_label.grid(row=0, column=2, sticky="w", padx=(10, 5), pady=(10, 10))
        self.port_entry = CTkEntry(servidor_despliegue_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        self.port_entry.grid(row=0, column=3, padx=(10, 5), pady=(10, 10), sticky="ew")
        self.port_entry.insert(0, "7001")

        remote_ejb_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color="#FFFFFF", border_color='#84bfc4', border_width=3)
        remote_ejb_frame.grid(row=4, column=0, columnspan=3, pady=5, padx=(10, 5), sticky="ew")

        # Crear un widget Label encima del borde del marco
        label_on_border_remoto = CTkLabel(self, text="Parámetros EJB Remoto", bg_color="#FFFFFF", fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        label_on_border_remoto.place(in_=remote_ejb_frame, anchor="sw" )

        remote_ejb_name_label = CTkLabel(remote_ejb_frame, text="Nombre Servidor EJB:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        remote_ejb_name_label.grid(row=0, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        self.remote_ejb_name_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', width=300, height=2.5, border_width=3, text_color="black")
        self.remote_ejb_name_entry.grid(row=0, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")

        remote_ip_label = CTkLabel(remote_ejb_frame, text="Dirección IP:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        remote_ip_label.grid(row=1, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        self.remote_ip_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', width=300,  height=2.5, border_width=3, text_color="black")
        self.remote_ip_entry.grid(row=1, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")

        remote_port_label = CTkLabel(remote_ejb_frame, text="Puerto:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        remote_port_label.grid(row=1, column=2, sticky="w", padx=(10, 5), pady=(10, 2))
        self.remote_port_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        self.remote_port_entry.grid(row=1, column=3, padx=(10, 5), pady=(10, 2), sticky="ew")

        user_label = CTkLabel(remote_ejb_frame, text="Usuario:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        user_label.grid(row=2, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        self.user_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',  width=300, height=2.5, border_width=3, text_color="black")
        self.user_entry.grid(row=2, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")

        password_label = CTkLabel(remote_ejb_frame, text="Password:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        password_label.grid(row=2, column=2, sticky="w", padx=(10, 5), pady=(10, 2))
        self.password_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, show="*", text_color="black")
        self.password_entry.grid(row=2, column=3, padx=(10, 5), pady=(10, 2), sticky="ew")

        jndi_label = CTkLabel(remote_ejb_frame, text="Nombre JNDI:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        jndi_label.grid(row=3, column=0, sticky="w", padx=(10, 5), pady=(10, 20))
        self.jndi_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',  width=400, height=2.5, border_width=3, text_color="black")
        self.jndi_entry.grid(row=3, column=1, padx=(10, 5), pady=(10, 20), sticky="ew")

        buttons_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color="#FFFFFF")
        buttons_frame.grid(row=5, column=0, columnspan=3, pady=10)

        back_button = CTkButton(buttons_frame, text="Atrás", command=lambda: self.cancelar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        back_button.grid(row=0, column=0, padx=(300, 0))

        finish_button = CTkButton(buttons_frame, text="Finalizar",command= lambda : self.save_to_yaml(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        finish_button.grid(row=0, column=2, padx=5)


    def cancelar(self):
       # Cancela todos los eventos pendientes
       self.withdraw()
       self.quit()
       self.main_menu.MainMenuLoop()

    def buscar_archivos_interfaz(self, ruta_personalizada = None):
        files = None

        fileName = ruta_personalizada.split("/")[len(ruta_personalizada.split("/")) - 1] 
        parte = re.split(r'(?=[A-Z])', fileName)[0]   
        rutaBusqueda = self.ejbEntryRoute + "/" + parte + "EAR/EarContent/APP-INF/lib"
        files = []
        try:
            for file in os.listdir(rutaBusqueda): #buscar jar acabdos en Remoting , 
               if file.endswith(".jar") and "Remoting" in file:#dentro buscar los serives a usar
                   # Extraer el archivo JAR 
                    jar_path  = rutaBusqueda+"/"+file
                    class_files = self.listar_clases_de_jar(jar_path)
                    print("Archivos .class encontrados:", class_files)
                    files.append(class_files)
        except:
            print("No encontro la ruta: " + rutaBusqueda)    
        self.mostrar_resultados_interfaz(files,rutaBusqueda)

    def listar_clases_de_jar(self,jar_path):
        """
        Lista las clases contenidas en un archivo JAR.

        :param jar_path: Ruta al archivo JAR.
        :return: Lista de nombres de clases completamente calificados.
        """
        clases = []

        # Abrir el archivo JAR como un archivo ZIP
        with zipfile.ZipFile(jar_path, 'r') as jar:
            # Iterar sobre todos los archivos en el JAR
            for archivo in jar.namelist():
                # Filtrar solo archivos .class
                if archivo.endswith('SkeletonRemote.class') and not archivo.startswith('META-INF/'):
                    # Convertir la ruta del archivo en un nombre de clase
                    # Reemplazar '/' o '\' por '.' y eliminar la extensión '.class'
                    clase = archivo.replace('/', '.').replace('\\', '.').rstrip('.class')
                    clases.append(clase)

        return clases

    def buscar_archivos(self, ruta_personalizada = None):
        files = None
        """Busca archivos con jst.ejb."""
        rutaBusqueda = ruta_personalizada
        if ruta_personalizada == None:
            rutaBusqueda = ruta_classes
        files = []
        try:
            for file in os.listdir(rutaBusqueda):
                rutaSettings = rutaBusqueda+"/"+file+"/.settings/org.eclipse.wst.common.project.facet.core.xml"
                
                if len(file) > 3 and file.endswith("EJB") and utl.buscarPropiedadInXml(rutaSettings,"facet","jst.ejb"):
                   files.append(file)
        except:
            print("No encontro la ruta: " + rutaBusqueda)    
        self.mostrar_resultados(files,rutaBusqueda)    

    def selectDirectory(self,directory):
        if(directory == ""):
            return directory
        else:
            par = Path(directory)
            return str(par.parent)  

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
            #Comprobar la configuración
            print(f"Archivo seleccionado: {selected_file}")
            self.ejb_container_entry.configure(state="normal")
            self.ejb_container_entry.delete(0, "end")
            self.ejb_container_entry.insert(0, ruta+"/"+selected_file)
            self.ejb_container_entry.configure(state="disabled")
            self.ejb_interface_button.configure(state="normal")
            self.ejbEntryRoute = ruta
            self.archivoClases = selected_file
            frame.destroy()

        else:
            print("No se seleccionó ningún archivo.") 

    def aceptar_interfaz(self, frame, selected_file, ruta):
        if selected_file:
            #Comprobar la configuración
            print(f"Archivo seleccionado: {selected_file}")
            self.ejb_interface_entry.configure(state="normal")
            self.ejb_interface_entry.delete(0, "end")
            self.ejb_interface_entry.insert(0, selected_file)
            self.ejb_interface_entry.configure(state="disabled")

            frame.destroy()

        else:
            print("No se seleccionó ningún archivo.")             

    def mostrar_resultados_interfaz(self, files,ruta):
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
            desc_label = CTkLabel(file_frame, text="Seleccione un proyecto EJB ", text_color= "black")
            desc_label.grid(row=0, column=0, columnspan=3, pady=(5, 1), padx=20, sticky="w")

            # Botones de acción en el pie de página
            button_frame = ctk.CTkFrame(resultados_window, fg_color="#FFFFFF", border_color="#84bfc4")
            button_frame.pack(fill="x", pady=20)

            if (ruta != ''):
                desc_label2 = CTkLabel(file_frame, text="(" + ruta +")" , text_color= "black")
                desc_label2.grid(row=1, column=0, columnspan=3, pady=(0,2), padx=30, sticky="w")

            # Añadir radiobuttons para cada archivo
            if(files != None and len(files) > 0):
                for index, file in enumerate(files):
                    radiobutton = ctk.CTkRadioButton(file_frame, text=file, variable=selected_file, value=file[index], border_color='#84bfc4', fg_color='#84bfc4', text_color= "black", font=("Arial", 12, "bold"))
                    radiobutton.grid(row=index + 3, column=0, sticky="w", padx=60, pady=3)

                accept_button = ctk.CTkButton(button_frame, text="Aceptar", command=lambda: self.aceptar_interfaz(resultados_window, selected_file.get(),ruta), fg_color='#84bfc4',  hover_color='#41848a', text_color= "black", font=("Arial", 12, "bold"))
                accept_button.pack(side="right", padx=10, expand=True)    
            else:    
    
                texto = "No se ha encontrado ninguna interfaz de EJB remoto"  
                desc_label3 = CTkLabel(file_frame, text=texto,text_color="red")
                desc_label3.grid(row=3, column=0, columnspan=3, pady=(0,2), padx=30, sticky="w")
                ok_button = ctk.CTkButton(button_frame, text="Ok", command=resultados_window.destroy, fg_color='#84bfc4',  hover_color='#41848a', text_color= "black", font=("Arial", 12, "bold"))
                ok_button.pack(side="right", padx=10, expand=True)           
       
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
            desc_label = CTkLabel(file_frame, text="Seleccione un proyecto EJB ", text_color= "black")
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
    
                texto = "Esta ruta no contiene ningún proyecto EJB valido"  
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

        frame_boton = CTkFrame(frame_center, bg_color="#FFFFFF", fg_color="#FFFFFF")
        frame_boton.pack(pady=10)

        menu_button = ctk.CTkButton(frame_boton, text="Volver al menú", command=lambda: self.cancelar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width=100, height=25)
        menu_button.pack(side="right", padx=(6, 5), pady=(40, 10))

        close_button = ctk.CTkButton(frame_boton, text="Cerrar", command=lambda: self.cerrar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width=100, height=25)
        close_button.pack(side="left", padx=(5, 5), pady=(40, 10))

        # Mostrar el nuevo frame
        frame_final.pack(fill="both", expand=True)  

    def save_to_yaml(self):      
        if self.ejb_container_entry.get() == '' or self.ejb_interface_entry.get() == '':
            self.configuration_warning.configure(text="El proyecto y el interfaz del EJB son obligatorios")
            self.configuration_warning.configure(text_color ="red")
            return FALSE
        

        if (self.ip_entry.get() == '' or self.ejb_port_entry.get() == ''  or self.remote_ejb_name_entry.get() == ''  or self.remote_ip_entry.get() == '' or 
            self.remote_port_entry.get() == '' or self.user_entry.get() == ''  or self.password_entry.get() == ''  or self.jndi_entry.get() == ''):
            self.configuration_warning.configure(text="Los parámetros de servidor y remotos EJB son obligatorios")
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
        print('Fin: proyecto Creado: ' + yaml_data["project_name"]+ yaml_data["ejb_project_name"]+  "EJB")
        fin = datetime.now()
        logging.info('Tiempo: proyecto Creado en: ' + str((fin-inicio).total_seconds()) + " segundos")
        now = datetime.now()
        dates = now.strftime('%d-%b-%Y %H:%M:%S')
        print(F"Final: paso 1 creado ::: "+dates,file=sys.stderr)
        sys.stderr.flush()
    
        self.ventana_final_popup()        

if __name__ == '__main__':
    app = VentanaPaso6()
    app.mainloop()
