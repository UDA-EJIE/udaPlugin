import tkinter as tk
from tkinter import ttk
import yaml
import oracledb
from Column import Column
from Table import Table
import os
from tkinter import filedialog
import plugin.paso2 as p2
import plugin.utils as utl
import copy
import json
from customtkinter import *
import customtkinter as ctk
from functools import partial
from plugin.GIFLabel import *
import menuPrincipal as m
from pathlib import Path
import logging

base_path = os.path.dirname(os.path.abspath(__file__))
d = os.path.join(base_path, 'instantclient_21_12')

ruta_classes = utl.readConfig("RUTA", "ruta_classes")
ruta_war = utl.readConfig("RUTA", "ruta_war")
tables_original = None


#sys.stderr = open('logs/log.log', 'a')
class PaginaUno(CTkFrame):
    
    def __init__(self, master, tables=None, columns=None, *args, **kwargs):
        super().__init__(master, *args, **kwargs)
        self.configure(corner_radius=10, fg_color="#E0E0E0", border_color="#69a3d6", border_width=4)


        # Configura el contenedor principal para que las columnas se expandan
        self.grid_columnconfigure(0, weight=1)  # Esto hace que la columna se expanda
        
        configuration_frame = CTkFrame(self)
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")

        configuration_label = CTkLabel(configuration_frame,  text="Crear nueva aplicación", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        self.configuration_warning = CTkLabel(configuration_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.grid(row=0, column=3, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar")
        description_label.grid(row=1, column=0, columnspan=3, pady=(10, 5), padx=20, sticky="w")

        # Formulario
        labels = ["Service name:", "SID:", "Host:", "Puerto:", "Usuario:", "Contraseña:", "Esquema Catálogo:", "URL:"]
        valores = ["serviceName", "sid", "host", "puerto", "usuario", "password", "esquema", "url"]
        self.entries = []
        
        for i, label_text in enumerate(labels):
            sv = StringVar()
            sv.trace_add("write", lambda name, index, mode, sv=lambda:sv: self.urlModify())
            label = CTkLabel(self, text=label_text, fg_color="#E0E0E0", text_color="black", font=("Arial", 12, "bold"))
            label.grid(row=i+1, column=0, sticky="w", padx=(20, 10), pady=(20, 2))
            entry = CTkEntry(self,textvariable=sv, fg_color='#69a3d6', border_color='#69a3d6', height=2.5, 
                             width=500, text_color="grey" if label_text == 'URL:' else 'black', show='*' if label_text == 'Contraseña:' else None,state='disabled' if label_text == 'URL:' else 'normal')
            entry.grid(row=i+1, column=1, padx=(0, 200), pady=(20, 2), sticky="ew")
            if (valores[i] != None):
               try:
                entry.insert(0, utl.readConfig("BBDD", valores[i]))  
               except ValueError:    
                   logging.exception("Error al obtener el valor:" + ValueError)
            self.entries.append(entry)
        self.urlModify()
        # Botones
        self.test_button = CTkButton(self, text="Probar conexión", command=self.probar_conexion, fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        self.test_button.grid(row=len(labels) + 1, column=0, columnspan=2, pady=20, padx=20, sticky="ew")

        next_button = CTkButton(self, text="Siguiente", command=lambda:self.master.mostrarSpinner("avanzarPaso2"), fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        next_button.grid(row=len(labels) + 2, column=1, pady=10, padx=20, sticky="e")

        back_button = CTkButton(self, text="Atrás", command=lambda: m.MainMenuLoop(self), fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        back_button.grid(row=len(labels) + 2, column=1, pady=10, padx=(50, 130), sticky="e")
                 
    
    def urlModify(self):
        if(len(self.entries) > 6):
            entryUrl = self.entries[7]
            entryService = self.entries[0].get()
            entrySid = self.entries[1].get()
            entryHost = self.entries[2].get()
            entryPuerto = self.entries[3].get()
            cadena = "jdbc:oracle:thin:@"
            if(entrySid == ''):
                cadena = cadena +"//"+ entryHost + ":" + entryPuerto + "/" + entryService
            else:
                cadena = cadena + entryHost + ":" + entryPuerto + ":" + entrySid    
            entryUrl.configure(state="normal")
            entryUrl.delete(0, "end")
            entryUrl.insert(0, cadena)
            entryUrl.configure(state="disabled")  

    def probar_conexion(self):
        
        un = self.entries[4].get()
        pw = self.entries[5].get()
        
        try:
            oracledb.init_oracle_client(lib_dir=d)
            if(self.entries[1].get() == ''):
                cs = self.entries[2].get() + ":" + self.entries[3].get() + "/" + self.entries[0].get()
                oracledb.connect(user=un, password=pw, dsn=cs)
            else:#con SID
                oracledb.connect(user=un, password=pw, sid=self.entries[1].get(),host=self.entries[2].get(),port=self.entries[3].get())    
            logging.info("Connection successful!")
            self.update_button_color('#4CAF50')  # Green color on successful connection
            self.configuration_warning.configure(text="Connection successful!")
            self.configuration_warning.configure(text_color ="#4CAF50")
        except oracledb.Error as e:
            logging.exception("Error connecting to Oracle Database: " )
            self.update_button_color('#FF0000')  # Red color on error
            self.configuration_warning.configure(text="Error connecting to Oracle Database")
            self.configuration_warning.configure(text_color ="#FF0000")
            
    def update_button_color(self, color):
        self.test_button.configure(fg_color=color)    
        
        
class PaginaDos(CTkFrame):
    def __init__(self, master, tables, estado_tables=None,  *args, **kwargs):
        super().__init__(master, *args, **kwargs)
        self.configure(corner_radius=10, fg_color="#E0E0E0")

        self.original_tables = copy.deepcopy(tables)
        self.tables = []

        global tables_original
        
        tables_original = tables

        # Header frame using grid
        self.header_frame = CTkFrame(self, fg_color="black")
        self.header_frame.grid(row=0, column=0, sticky="ew")
        self.grid_columnconfigure(0, weight=1)  # Ensure this column can expand
        self.grid_rowconfigure(1, weight=1)     # Central row where the scrollable frame will go

        header_label = CTkLabel(self.header_frame, text="Seleccione las tablas y sus columnas para la generación de código", font=("Arial", 14, "bold"))
        header_label.pack(pady=10, padx=10)

        # Scrollable frame in the middle using pack inside a grid row
        self.middle_frame = CTkFrame(self)
        self.middle_frame.grid(row=1, column=0, sticky="nsew")
        self.scrollable_frame = CTkScrollableFrame(self.middle_frame, fg_color="#E0E0E0", scrollbar_fg_color="#E0E0E0")
        self.scrollable_frame.pack(fill="both", expand=True, padx=10, pady=10)

        self.populate_scrollable_frame(self.scrollable_frame, tables_original)

        # Footer frame using grid for buttons
        self.footer_frame = CTkFrame(self, fg_color="#E0E0E0")
        self.footer_frame.grid(row=2, column=0, pady=(5, 30) ,sticky="ew")
        self.setup_footer_buttons()
        self.master.ocultarSpinner()

        if estado_tables != None:
            self.estado_tables_checkbox(estado_tables)
    
    listaColumnas = {}    
    var_list = []
    
    def choose(self,table,index):
     
     for column in table.columns:
        columna = self.listaColumnas[table.name+column.name]
        if(self.var_list[index].get() == 1):
            columna.select()
        else:
            columna.deselect()   

    def estado_tables_checkbox(self, estado_checkboxes):
        for tables in estado_checkboxes:
            for column in tables['columns']:
                columna = self.listaColumnas[tables['name']+column.name]
                columna.select()
   
    def populate_scrollable_frame(self, frame, tables_original):
        self.var_list = []
        for index, table in enumerate(tables_original):
            self.var_list.append(IntVar(value=0))
            table_frame = CTkFrame(frame, fg_color="#FFFFFF", corner_radius=10)
            table_frame.pack(fill="x", padx=10, pady=2, expand=True)
            #check con la lista de tablas
            table_checkbox = CTkCheckBox(table_frame, text=table.name,command=partial(self.choose,table,index), variable=self.var_list[index], 
                                            text_color="black", font=("Arial", 10, "bold"),
                                            checkbox_height=15, checkbox_width=15, border_color='#337ab7')
            table_checkbox.pack(side="left", padx=5)
            
            expand_icon = CTkLabel(table_frame, text="▼", fg_color="#FFFFFF", cursor="hand2", 
                                    text_color="black", font=("Arial", 10, "bold"))
            expand_icon.pack(side="left", padx=5)
            expand_icon.bind("<Button-1>", lambda event, f=table_frame: self.toggle_columns(f))

            columns_frame = CTkFrame(table_frame, fg_color="#F0F0F0", corner_radius=10)
            table_frame.columns_frame = columns_frame
            columns_frame.pack(fill="x", expand=True, padx=20, pady=2)
            columns_frame.pack_forget()  # Start with columns hidden

            # Correct placement of column checkboxes inside the columns_frame
            for column in table.columns:
                column_checkbox = CTkCheckBox(columns_frame, text=column.name, variable=tk.BooleanVar(value=False), 
                                                text_color="black", font=("Arial", 10, "bold"),
                                                command=partial(self.selectTableFromColumn,table_checkbox,column.name),
                                                checkbox_height=15, checkbox_width=15, border_color='#337ab7')
                column_checkbox.pack(anchor="w", padx=20)
                self.listaColumnas[table.name+column.name] = column_checkbox
            self.tables.append(table_frame)

    def toggle_columns(self, table_frame):
    # Asegúrate de referirte al columns_frame para expandir/contraer
        if table_frame.columns_frame.winfo_viewable():
            table_frame.columns_frame.pack_forget()
            table_frame.winfo_children()[1].configure(text="▼")  # Icono cambia a 'expandir'
        else:
            table_frame.columns_frame.pack(fill="x", expand=True, padx=20, pady=2)
            table_frame.winfo_children()[1].configure(text="▲")  # Icono cambia a 'contraer'
    def selectTableFromColumn(self,tableCheck,columnName):
        if (len(self.listaColumnas) != 0 and 
            self.listaColumnas[tableCheck._text+columnName].get() == 1):
            tableCheck.select()
    def obtener_seleccion_checkbox(self):
        seleccion_checkbox = []
        
        
        for table_frame, original_table in zip(self.tables, self.original_tables):

            table_name = original_table.name  # Nombre de la tabla
            selected_columns = []


            for child, original_column in zip(table_frame.columns_frame.winfo_children(), original_table.columns):
                if child.get() == 1:
                    selected_columns.append(original_column)

            if selected_columns:
                seleccion_checkbox.append({"name": table_name, "columns": selected_columns})

        logging.info("Esto es la selección de checkboxes: " + str(seleccion_checkbox))
        return seleccion_checkbox
    
    def setup_footer_buttons(self):
        select_all_button = CTkButton(self.footer_frame, text="Seleccionar Todas",bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda:self.master.mostrarSpinner("selectAll"))
        select_all_button.pack(side="left", padx=5)

        deselect_all_button = CTkButton(self.footer_frame, text="Deseleccionar Todas",bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda:self.master.mostrarSpinner("deselectAll"))
        deselect_all_button.pack(side="left", padx=5)

        configuration_warning = CTkLabel(self.footer_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        configuration_warning.pack(side="left", padx=5)
        self.master.configuration_warning = configuration_warning


        next_button = CTkButton(self.footer_frame, text="Siguiente", bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25,  command=lambda: self.master.mostrar_pagina_tres(self.obtener_seleccion_checkbox()))
        next_button.pack(side="right", padx=5)

        back_button = CTkButton(self.footer_frame, text="Atras",bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda : self.master.mostrar_pagina_uno())
        back_button.pack(side="right", padx=5)
     
        cancel_button = CTkButton(self.footer_frame, text="Cancelar", bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command= lambda: m.MainMenuLoop(self.master))
        cancel_button.pack(side="right", padx=5)
            
class PaginaTres(CTkFrame):
    def __init__(self, master, tables, estado_tables=None, *args, **kwargs):
        super().__init__(master, *args, **kwargs)
    

        # Variables para controlar los checkboxes
        self.modelo_datos_var = tk.BooleanVar(value=False)
        self.daos_var = tk.BooleanVar(value=False)
        self.servicios_var = tk.BooleanVar(value=False)
        self.controladores_var = tk.BooleanVar(value=False)
        
        self.configure(corner_radius=10)
        
        # Header frame usando grid
        self.header_frame = CTkFrame(self, fg_color="black")
        self.header_frame.grid(row=0, column=0, sticky="new")
        self.grid_columnconfigure(0, weight=1)
        self.grid_rowconfigure(1, weight=1)  # Asegura que el contenedor principal se expanda

        header_label = CTkLabel(self.header_frame, text="Seleccione las opciones para los distintos componentes", font=("Arial", 14, "bold"))
        header_label.pack(pady=10, padx=10)

        # Contenedor principal
        main_container = CTkFrame(self, fg_color="#E0E0E0")
        main_container.grid(row=1, column=0, sticky="nsew")
        main_container.grid_columnconfigure(0, weight=1)
        main_container.grid_rowconfigure(0, weight=1)
        main_container.grid_rowconfigure(1, weight=1)
        self.main_container = main_container

        # Contenedor de Componentes de Negocio
        negocio_container = CTkFrame(main_container, fg_color="#E0E0E0", border_width=3, border_color="#69a3d6")
        negocio_container.grid(row=0, column=0, sticky="nsew")
        negocio_container.grid_columnconfigure(0, weight=1)

        # Título "Componentes de Negocio"
        CTkLabel(negocio_container, text="Componentes de Negocio", text_color="black", font=("Arial", 13, "bold")).grid(row=0, column=0, sticky="w", pady=(10, 20), padx=(20, 20))

        # Componentes individuales
        for index, (component, var) in enumerate([("Modelo de Datos", self.modelo_datos_var), 
                                                  ("DAOs", self.daos_var), 
                                                  ("Servicios", self.servicios_var)]):
            component_container = CTkFrame(negocio_container, fg_color="#E0E0E0")
            component_container.grid(row=index+1, column=0, sticky="ew", pady=5, padx=(20, 20))
            CTkCheckBox(component_container, variable=var, onvalue=True, offvalue=False, text=component,command=self.update_search_state, text_color="black", font=("Arial", 11, "bold")).grid(row=0, column=0, padx=(20, 0), sticky="w")
        
        # Entry y Botón de Buscar para Componentes de Negocio
        rutaActual = utl.rutaActual(__file__)
        textRutaNegocio = rutaActual
        textRutaControlador = rutaActual
        ruta_classes = utl.readConfig("RUTA", "ruta_classes")
        ruta_war = utl.readConfig("RUTA", "ruta_war")
        if(ruta_classes != None and ruta_classes != ""):
           textRutaNegocio = ruta_classes 
        if(ruta_war != None and ruta_war != ""):
           textRutaControlador = ruta_classes 
        archivoClases = utl.buscarArchivo(textRutaNegocio,"EARClasses") 
        archivoWar = utl.buscarArchivo(textRutaControlador,"War") 
        if(archivoClases != '' ):
           textRutaNegocio = textRutaNegocio+"\\"+archivoClases 
        else:
            textRutaNegocio = ""
        if(archivoWar != ''):
           textRutaControlador = textRutaControlador+"\\"+archivoWar  
        else:
            textRutaControlador = ""
        self.search_entry_negocio = CTkEntry(negocio_container, width=600, fg_color='#69a3d6')
        self.search_entry_negocio.insert(0, textRutaNegocio)
        self.search_entry_negocio.configure(text_color="grey")
        self.search_entry_negocio.configure(state="disabled")
        self.search_entry_negocio.grid(row=4, column=0, padx=(0,230), pady=(10,0))
        search_button_negocio = CTkButton(negocio_container, text="Buscar",bg_color='#E0E0E0',fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command= lambda: self.buscar_archivos("negocio",self.selectDirectory(self.search_entry_negocio.get())))
        search_button_negocio.grid(row=4, column=0, padx=(670, 0), pady=(10,0))

        # Contenedor de Componentes de Presentacións
        presentacion_container = CTkFrame(main_container, fg_color="#E0E0E0", bg_color="#E0E0E0", border_width=3, border_color="#69a3d6")
        presentacion_container.grid(row=1, column=0, sticky="nsew")
        presentacion_container.grid_columnconfigure(0, weight=1)

        # Título "Componentes de Presentación"
        CTkLabel(presentacion_container, text="Componentes de Presentación",text_color="black", font=("Arial", 13, "bold")).grid(row=0, column=0, sticky="w", pady=(10, 20), padx=(20, 20))

        contenedor_controlador = CTkFrame(presentacion_container, fg_color="#E0E0E0")
        contenedor_controlador.grid(row=1, column=0, sticky="ew", pady=5, padx=(20, 20))
        # Checkbox para "Controladores"
        controladores_checkbox = CTkCheckBox(contenedor_controlador, onvalue=True, offvalue=False, text="Controladores", text_color="black", font=("Arial", 12, "bold"), variable=self.controladores_var, command=self.update_search_state)
        controladores_checkbox.grid(row=1, column=0, padx=(20, 0), sticky="w")

        # Entry y Botón de Buscar para Componentes de Presentación
        self.search_entry_presentacion = CTkEntry(presentacion_container, width=600, fg_color='#69a3d6')
        self.search_entry_presentacion.insert(0, textRutaControlador)
        self.search_entry_presentacion.configure(text_color="grey")
        self.search_entry_presentacion.configure(state="disabled")
        self.search_entry_presentacion.grid(row=2, column=0, padx=(0,230), pady=(10,0))
        search_button_presentacion = CTkButton(presentacion_container, text="Buscar", bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda : self.buscar_archivos("presentacion",self.selectDirectory(self.search_entry_presentacion.get())))
        search_button_presentacion.grid(row=2, column=0, padx=(670, 0), pady=(10,0))

        # Botones finales en el pie de página
        buttons_container = CTkFrame(self, fg_color="#E0E0E0", bg_color="#E0E0E0")
        buttons_container.grid(row=2, column=0, sticky="sew")
        buttons_container.grid_columnconfigure(0, weight=1)  # Distribuir espacio uniformemente
        buttons_container.grid_columnconfigure(1, weight=1)
        self.buttons_container = buttons_container

        tabla_resultados = []
        for tb in tables:
            tabla = {}
            tabla['name'] = tb['name']
            tabla['columns'] = []
            for column in tb['columns']:
                columna_dict = {
                'name': column.name,
                'type': column.type,
                'dataPrecision': column.dataPrecision,
                'datoImport': column.datoImport,
                'datoType': column.datoType,
                'nullable': column.nullable,
                'primaryKey': column.primaryKey,
                'tableName': column.tableName
            }

                tabla['columns'].append(columna_dict)
            tabla_resultados.append(tabla)
            self.tabla_resultados = tabla_resultados
        self.rutaActual = rutaActual
        self.archivoClases = archivoClases
        self.archivoWar = archivoWar
        CTkButton(buttons_container, text="Finalizar", command=lambda: self.master.mostrarSpinner("finalizar"), bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25).pack(side="right", padx=5, pady=20)
        CTkButton(buttons_container, text="Atras",bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda : self.master.mostrar_pagina_dos(tables_original, estado_tables=tables)).pack(side="right", padx=5, pady=20)
        CTkButton(buttons_container, text="Cancelar", command=lambda: m.MainMenuLoop(master), bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25).pack(side="right", padx=5, pady=20)
        self.configuration_warning = CTkLabel(buttons_container,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.pack(side="left", padx=5, pady=20)

        self.grid_rowconfigure(2, weight=0)  # Botones no expanden

    def selectDirectory(self,directory):
        if(directory == ""):
            return directory
        else:
            par = Path(directory)
            return str(par.parent)
        
    def getDatos(self,rutaActual,archivoClases,archivoWar):
        project_name = archivoClases.replace("EARClasses","")
        #war tiene que tener un classpath valido
        war_name = ""
        if(self.controladores_var.get()):
            war_name = utl.obtenerNombreProyecto(self.search_entry_presentacion.get(),archivoWar)
        data = { "project_name": project_name,
        "security_app": "",
        "war_project_name": war_name,
        "PACKAGE_NAME": "com.ejie."+project_name+".control",
        "directorio_actual" : rutaActual+"/templates/generateCode/",
        "destinoApp" : self.search_entry_negocio.get(),
        "destinoWar" : self.search_entry_presentacion.get()
       }
        return data

    def update_search_state(self):
        """Actualiza el estado de los contenedores de búsqueda según el estado de los checkboxes."""
        if any([self.modelo_datos_var.get(), self.daos_var.get(), self.servicios_var.get()]):
            self.search_entry_negocio.configure(state="normal")
            self.search_entry_negocio.configure(text_color="black")
            self.search_entry_negocio.configure(state="disabled")
        else:
            self.search_entry_negocio.configure(state="normal")
            self.search_entry_negocio.configure(text_color="grey")
            self.search_entry_negocio.configure(state="disabled")

        if self.controladores_var.get():
            self.search_entry_presentacion.configure(state="normal")
            self.search_entry_presentacion.configure(text_color="black")
            self.search_entry_presentacion.configure(state="disabled")
        else:
            self.search_entry_presentacion.configure(state="normal")
            self.search_entry_presentacion.configure(text_color="grey")
            self.search_entry_presentacion.configure(state="disabled")

    def buscar_archivos(self, boton_pulsado, ruta_personalizada = None):
        files = None
        if boton_pulsado == "negocio":
            """Busca archivos con terminación 'Classes' en la misma ruta del script Python."""
            if ruta_personalizada == None:
                try:
                    files = [file for file in os.listdir(ruta_classes) if file.endswith("Classes")]
                except:
                    logging.exception("No encontro la ruta: " + ruta_classes)
                self.mostrar_resultados(files, boton_pulsado,ruta_classes)
            else:
                try:
                    files = [file for file in os.listdir(ruta_personalizada) if file.endswith("Classes")]
                except:
                    logging.exception("No encontro la ruta: " + ruta_personalizada)    
                self.mostrar_resultados(files, boton_pulsado,ruta_personalizada)
        else:
            """Busca archivos con terminación 'war' en la misma ruta del script Python."""
            if ruta_personalizada == None:
                try:
                    files = [file for file in os.listdir(ruta_war) if file.endswith("War")]
                except:
                    logging.exception("No encontro la ruta: " + ruta_war)    
                self.mostrar_resultados(files, boton_pulsado,ruta_war)
            else:
                try:
                    files = [file for file in os.listdir(ruta_personalizada) if file.endswith("War")]
                except:
                    logging.exception("No encontro la ruta: " + ruta_personalizada)    
                self.mostrar_resultados(files, boton_pulsado,ruta_personalizada)



    def mostrar_resultados(self, files, boton_pulsado,ruta):
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
            
        buscar_button = ctk.CTkButton(button_frame, text="Buscar", command= lambda: self.open_file_explorer(resultados_window, boton_pulsado=boton_pulsado)) 
        buscar_button.pack(side="left", padx=10, expand=True)
            
        cancel_button = ctk.CTkButton(button_frame, text="Cancelar", command=resultados_window.destroy)
        cancel_button.pack(side="right", padx=10, expand=True)
        accept_button = ctk.CTkButton(button_frame, text="Aceptar", command=lambda: self.aceptar(resultados_window, selected_file.get(), boton_pulsado,ruta))
        accept_button.pack(side="right", padx=10, expand=True)


    def aceptar(self, frame, selected_file, boton_pulsado, ruta):
        if selected_file and boton_pulsado == "negocio":
            logging.info(f"Archivo seleccionado: {selected_file}")
            self.search_entry_negocio.configure(state="normal")
            self.search_entry_negocio.delete(0, "end")
            self.search_entry_negocio.insert(0, ruta+"/"+selected_file)
            self.search_entry_negocio.configure(state="disabled")
            self.archivoClases = selected_file
            frame.destroy()
        elif(selected_file and boton_pulsado == "presentacion"):
            logging.info(f"Archivo seleccionado: {selected_file}")
            self.search_entry_presentacion.configure(state="normal")
            self.search_entry_presentacion.delete(0, "end")
            self.search_entry_presentacion.insert(0, ruta+"/"+selected_file)
            self.search_entry_presentacion.configure(state="disabled")
            self.archivoWar = selected_file
            frame.destroy()

        else:
            logging.error("No se seleccionó ningún archivo.")       

    def open_file_explorer(self, frame, boton_pulsado):
        # Esta función se llama cuando el usuario hace clic en "Buscar"
        # Abre un diálogo para seleccionar un directorio
        frame.destroy()
        directory = filedialog.askdirectory(parent=self)      
        if directory:  # Si se selecciona un directorio
            selected_directory = directory  # Guardar la ruta del directorio seleccionado
            self.buscar_archivos(boton_pulsado, selected_directory)
            logging.info(f"Directorio seleccionado: {selected_directory}")
        else:
            logging.error("No se seleccionó ningún directorio.")


class VentanaPrincipal(CTk):
    def __init__(self):
        super().__init__()
        self.title("Generar código de negocio y control")
        width = self.winfo_screenwidth() - 100
        height = self.winfo_screenheight() - 100
        self.geometry(str(width)+"x"+str(height)) # Puedes ajustar las dimensiones según tus necesidades
        padx = 0 # the padding you need.
        pady = 0
        toplevel_offsetx = 50
        toplevel_offsety = 50
        self.geometry(f"+{toplevel_offsetx + padx}+{toplevel_offsety + pady}")
        self.resizable(width=True, height=True)

        self.grid_rowconfigure(0, weight=1)
        self.columnconfigure(0, weight=1)

        self.pagina_actual = None
        self.mostrar_pagina(PaginaUno)

    def mostrar_pagina(self, pagina, tables=None, estado_tables=None):      
        if self.pagina_actual is not None:
            self.pagina_actual.destroy()
        self.pagina_actual = pagina(self, tables, estado_tables)
        self.pagina_actual.grid(row=0, column=0, sticky="nsew")

    def mostrar_pagina_dos(self, tables=None, estado_tables=None):
        self.mostrar_pagina(PaginaDos, tables, estado_tables)

    def mostrar_pagina_tres(self, tables=None, estado_tables=None):
        if(len(tables) == 0):
          self.configuration_warning.configure(text="Debe seleccionar al menos una tabla y una columna")
          return False
        self.mostrar_pagina(PaginaTres, tables, estado_tables)

    def mostrar_pagina_uno(self):
        self.mostrar_pagina(PaginaUno)
    
    def mostrarSpinner(self,caso):
        resultados_window2 = ctk.CTkToplevel(self)
        resultados_window2.title("")
        resultados_window2.attributes('-topmost', True)
        resultados_window2.wm_attributes('-alpha',0.8)
        #resultados_window2.resizable(width=None, height=None)
        #resultados_window2.transient()
        resultados_window2.overrideredirect(True)
        toplevel_offsetx, toplevel_offsety = self.winfo_x(), self.winfo_y()
        padx = -10 # the padding you need.
        pady = -10
        resultados_window2.geometry(f"+{toplevel_offsetx + padx}+{toplevel_offsety + pady}")
        width = self.winfo_screenwidth() - 80
        height = self.winfo_screenheight() - 80
        resultados_window2.geometry(str(width)+"x"+str(height))
        # label2 = GIFLabel(resultados_window2, "./plugin/images/spinner.gif")
        # label2.grid(row=11, column=11, columnspan=10, pady=(50, 5), padx=50, sticky="w")
        l_frame = CTkFrame(resultados_window2, bg_color='#E0E0E0', fg_color='#E0E0E0', border_color='#69a3d6', border_width=3)
        l_frame.grid(row=8, column=4, columnspan=4, pady=(200, 20), padx=100, sticky="ew")
        l = CTkLabel(l_frame, text="Cargando...", bg_color="#E0E0E0", fg_color="#E0E0E0", text_color="black", font=("Arial", 50, "bold"))
        l.grid(row=3, column=6, columnspan=6, pady=(200, 5), padx=200, sticky="w")
        progressbar = CTkProgressBar(resultados_window2, orientation="horizontal")
        progressbar.grid(row=10, column=6, pady=10, padx=20, sticky="n")
        progressbar.start()
        l.pack()

        label = CTkLabel(resultados_window2, text="Cargando...", fg_color="#E0E0E0", text_color="black", font=("Arial", 12, "bold"))
        label.grid(row=0, column=0, columnspan=3, pady=(20, 5), padx=20, sticky="w")
        self.resultados_window2 = resultados_window2   
        if(caso == "avanzarPaso2"):
            resultados_window2.after(710, self.avanzar_paso2)
        elif caso == "selectAll": 
            resultados_window2.after(710, self.select_all) 
        elif caso == "deselectAll": 
            resultados_window2.after(710, self.deselect_all) 
        elif caso == "finalizar":
            resultados_window2.after(710, self.validarPaso2) 
        elif caso == "paso2To3":
            resultados_window2.after(710,self.mostrar_pagina_tres(self.pagina_actual.obtener_seleccion_checkbox())) 
        elif caso == "paso2To1":
            resultados_window2.after(710,self.mostrar_pagina_uno())               

    def ocultarSpinner(self):
        self.resultados_window2.destroy()  

    def avanzar_paso2(self):         

        # Puedes agregar aquí la lógica para probar la conexión a la base de datos
        print("Conexión probada")
        logging.info("Conexión probada")
       
        un = self.pagina_actual.entries[4].get()
        pw = self.pagina_actual.entries[5].get()
        
        tables = [] 
        columns = [] 
        query = """select tb1.table_name, tb1.column_name,tb1.DATA_TYPE,tb1.NULLABLE,tb2.constraint_type, tb1.SYNONYM_NAME, tb1.DATA_PRECISION
         FROM  
            (SELECT ta.table_name,sy.SYNONYM_NAME, utc.COLUMN_NAME, utc.data_type,utc.nullable,utc.DATA_PRECISION
             FROM user_tables ta
             LEFT JOIN user_synonyms sy
             ON ta.TABLE_NAME = sy.TABLE_NAME
             INNER JOIN USER_TAB_COLUMNS utc
             ON ta.TABLE_NAME = utc.TABLE_NAME 
             order by sy.SYNONYM_NAME,ta.table_name,utc.column_name) tb1 
        LEFT JOIN 
            (select all_cons_columns.owner , all_cons_columns.table_name, all_cons_columns.column_name, all_constraints.constraint_type
            from all_constraints, all_cons_columns 
            where 
                all_constraints.constraint_type = 'P' AND all_constraints.owner = :esquema
                and all_constraints.constraint_name = all_cons_columns.constraint_name
                and all_constraints.owner = all_cons_columns.owner 
            order by all_cons_columns.owner,all_cons_columns.table_name) tb2
        ON tb1.table_name = tb2.table_name AND tb1.column_name = tb2.column_name"""
        
        oracledb.init_oracle_client(lib_dir=d)
        try:
            if(self.pagina_actual.entries[1].get() == ''):
             cs = self.pagina_actual.entries[2].get() + ":" + self.pagina_actual.entries[3].get() + "/" + self.pagina_actual.entries[0].get()
             connection =  oracledb.connect(user=un, password=pw, dsn=cs)
            else:#con SID
             connection =  oracledb.connect(user=un, password=pw, sid=self.pagina_actual.entries[1].get(),host=self.pagina_actual.entries[2].get(),port=self.pagina_actual.entries[3].get())
        except Exception as e: 
            logging.exception("An exception occurred BBDD:  " )  
            self.pagina_actual.configuration_warning.configure(text="An exception occurred: " + str(e))
            self.pagina_actual.configuration_warning.configure(text_color ="red")
            self.ocultarSpinner()
            return False
        
        with connection.cursor() as cursor:
                cursor.execute(query, esquema=pw.upper())
                rows = cursor.fetchall()
                tableName = ''
                cont = 0
                contPrimaryKey = 0
                for row in rows:
                    cont = cont + 1
                    tableNameBBDD = row[0]
                    if row[5] != None: #sinonimos
                      tableNameBBDD = row[5]  
                    #snakeCamelCase)   
                    if tableName == tableNameBBDD:
                        #se crea la columna
                        column = Column(tableNameBBDD,row[1],row[2],row[3],row[4],None,None,row[6])
                        columns.append(column)
                        if row[4]  == 'P': #primarykey
                            contPrimaryKey = contPrimaryKey + 1
                    else:
                        if cont > 1 and contPrimaryKey < len(columns):
                            tables.append(Table(tableName,columns)) 
                        contPrimaryKey = 0    
                        if row[4]  == 'P': #primarykey
                            contPrimaryKey = contPrimaryKey + 1    
                        columns = []
                        #se crea la columna
                        column = Column(tableNameBBDD,row[1],row[2],row[3],row[4],None,None,row[6])
                        columns.append(column)  
                    
                    if cont == len(rows) and contPrimaryKey < len(columns): #si es la última se mete a la tabla
                        tables.append(Table(tableName,columns))   
                    tableName = tableNameBBDD   
     
        self.mostrar_pagina_dos(tables)  

    def select_all(self):
        for table_frame in self.pagina_actual.tables:
            # Assuming _state is an attribute that holds the checkbox state
            table_frame.winfo_children()[0].select()  # Checkbox de la tabla
            for checkbox in table_frame.columns_frame.winfo_children():
                checkbox.select()
        self.ocultarSpinner()        

    def deselect_all(self):
        for table_frame in self.pagina_actual.tables:
            # Assuming _state is an attribute that holds the checkbox state
            table_frame.winfo_children()[0].deselect()  # Checkbox de la tabla
            for checkbox in table_frame.columns_frame.winfo_children():
                checkbox.deselect() # Checkbox de las columnas   
        self.ocultarSpinner() 

    def validarPaso2(self):
        this = self
        self = self.pagina_actual
        tabla_resultados = self.tabla_resultados
        rutaActual = self.rutaActual
        archivoClases = self.archivoClases
        archivoWar = self.archivoWar
        negocioActivado = False
        if(self.modelo_datos_var.get() == True or self.daos_var.get() == True or self.servicios_var.get() == True):
            negocioActivado = True
        if (self.controladores_var.get() == False and negocioActivado == False): 
            self.configuration_warning.configure(text="Debe seleccionar al menos un componente")
            this.ocultarSpinner()
            return False   
        if(self.search_entry_negocio.get() == '' and negocioActivado):
            self.configuration_warning.configure(text="Ninguna carpeta EarClasses seleccionada")
            this.ocultarSpinner()
            return False
        if(self.controladores_var.get() and self.search_entry_presentacion.get() == ''):
            self.configuration_warning.configure(text="Ninguna carpeta War seleccionada")
            this.ocultarSpinner()
            return False
        
        p2.initPaso2(tabla_resultados, self.getDatos(rutaActual,archivoClases,archivoWar),self)
        this.ocultarSpinner()
        this.mostrarResumenFinal(tabla_resultados)

    def mostrarResumenFinal(self,tablas):
        self = self.pagina_actual
        self.header_frame.destroy()
        self.main_container.destroy()
        self.buttons_container.destroy()
        main_container = CTkFrame(self, fg_color="#E0E0E0")
        main_container.grid(row=1, column=0, columnspan=3, sticky="nsew")
        main_container.grid_columnconfigure(0, weight=1)
        main_container.grid_rowconfigure(0, weight=1)
        configuration_warning = CTkLabel(main_container,  text="Se han creado "+str(len(tablas))+" tablas ", font=("Arial", 13, "bold"),text_color="black")
        configuration_warning.grid(row=0, column=0, pady=(20, 5), padx=(500,0), sticky="w")  
        button = CTkButton(main_container, text="Cerrar", command=lambda: m.MainMenuLoop(self.master), bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25) 
        button.grid(row=0, column=0, pady=(100, 5), padx=(500,0), sticky="w") 

if __name__ == "__main__":
    app = VentanaPrincipal()
    app.mainloop()