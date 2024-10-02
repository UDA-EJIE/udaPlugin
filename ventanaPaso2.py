import menuPrincipal as m

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
import threading
from plugin.utils import *

base_path = os.path.dirname(os.path.abspath(__file__))
d = os.path.join(base_path, 'instantclient_21_12')


ruta_classes = utl.readConfig("RUTA", "ruta_classes")
ruta_war = utl.readConfig("RUTA", "ruta_war")
tables_original = None
CADENA_COLUMN = "TTTABLA"
PRIMARY_COLUMN = "PPPRIMARY"
class PaginaUno(CTkFrame):
    
    def __init__(self, master, main_menu, tables=None, tables_ori=None, columns=None,estado_tables=None, *args, **kwargs):
        super().__init__(master, *args, **kwargs)

        self.configure(corner_radius=10, fg_color="#FFFFFF", border_color="#84bfc4", border_width=4)

        self.main_menu = main_menu
        # Configura el contenedor principal para que las columnas se expandan
        self.grid_columnconfigure(0, weight=1)  # Esto hace que la columna se expanda
        
        configuration_frame = CTkFrame(self)
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")

        configuration_label = CTkLabel(configuration_frame,  text="Generar código para una aplicación UDA", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        self.configuration_warning = CTkLabel(configuration_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.grid(row=0, column=3, columnspan=3, pady=(20, 5), padx=10, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard genera el código fuente para desplegar una aplicación UDA")
        description_label.grid(row=1, column=0, columnspan=3, pady=(10, 5), padx=20, sticky="w")

        # Formulario
        labels = ["Service name:", "SID:", "Host:", "Puerto:", "Usuario:", "Contraseña:", "Esquema Catálogo:", "URL:"]
        valores = ["serviceName", "sid", "host", "puerto", "usuario", "password", "esquema", "url"]
        self.entries = []
        
        for i, label_text in enumerate(labels):
            
            sv = StringVar(self)
            sv.trace_add("write", lambda name, index, mode, sv=lambda:sv: self.urlModify())
            label = CTkLabel(self, text=label_text, fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
            label.grid(row=i+1, column=0, sticky="w", padx=(20, 10), pady=(20, 2))
            entry = CTkEntry(self,textvariable=sv, fg_color='#84bfc4', border_color='#84bfc4', height=2.5, 
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
        self.test_button = CTkButton(self, text="Probar conexión", command=self.probar_conexion, fg_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        self.test_button.grid(row=len(labels) + 1, column=0, columnspan=2, pady=20, padx=20, sticky="ew")

        next_button = CTkButton(self, text="Siguiente", command=lambda:self.master.mostrarSpinner("avanzarPaso2"), fg_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        next_button.grid(row=len(labels) + 2, column=1, pady=10, padx=20, sticky="e")

        back_button = CTkButton(self, text="Atrás", command=lambda: self.cancelar(), fg_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        back_button.grid(row=len(labels) + 2, column=1, pady=10, padx=(50, 130), sticky="e")
                 
    
    def urlModify(self):
        if(len(self.entries) > 7):
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
    

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        self.master.quit()
        self.main_menu.MainMenuLoop()
        
class PaginaDos(CTkFrame):
    def __init__(self, master, main_menu, tables, cursor, tables_ori=None,  estado_tables=None, *args, **kwargs):
        super().__init__(master, *args, **kwargs)
        self.configure(corner_radius=10, fg_color="#FFFFFF")

        self.original_tables = copy.deepcopy(tables)
        self.tables = []
        self.tables_ori = tables_ori
        global tables_original
        self.cursor = cursor
        self.main_menu = main_menu
        tables_original = tables

        # Header frame using grid
        self.header_frame = CTkFrame(self, fg_color="black")
        self.header_frame.grid(row=0, column=0, sticky="ew")
        self.grid_columnconfigure(0, weight=1)  # Ensure this column can expand
        self.grid_rowconfigure(1, weight=1)     # Central row where the scrollable frame will go

        header_label = CTkLabel(self.header_frame, text="Seleccione las tablas y sus columnas para la generación de código", font=("Arial", 14, "bold"))
        header_label.pack(pady=10, padx=10)
        self.estado_tables = estado_tables

        # Scrollable frame in the middle using pack inside a grid row
        self.middle_frame = CTkFrame(self)
        self.middle_frame.grid(row=1, column=0, sticky="nsew")
        self.scrollable_frame = CTkScrollableFrame(self.middle_frame, fg_color="#E0E0E0", scrollbar_fg_color="#E0E0E0")
        self.scrollable_frame.pack(fill="both", expand=True, padx=10, pady=10)

        self.populate_scrollable_frame(self.scrollable_frame, tables_original)
        self.master.update_progress(1.0)
        # Footer frame using grid for buttons
        self.footer_frame = CTkFrame(self, fg_color="#FFFFFF")
        self.footer_frame.grid(row=2, column=0, pady=(5, 30) ,sticky="ew")
        self.setup_footer_buttons()
        self.master.close_loading_frame()
        #self.master.ocultarSpinner()
        
        if estado_tables != None:
            self.estado_tables_checkbox(estado_tables)
    
    listaColumnas = {}    
    var_list = []
    
    def choose(self,table,index):
     
     for column in table.columns:
        if (column.primaryKey == 'P'):
            columna = self.listaColumnas[table.name+CADENA_COLUMN+PRIMARY_COLUMN+column.name]
        else:  
            columna = self.listaColumnas[table.name+CADENA_COLUMN+column.name]  
        if(self.var_list[index].get() == 1):
            columna.select()
        else:
            columna.deselect()   

    #Guarda las columnas seleccionadas anterioremente
    def estado_tables_checkbox(self, estado_checkboxes):
        for tables in estado_checkboxes:
            for column in tables['columns']:
                
                if (column.primaryKey == 'P'):
                    columna = self.listaColumnas[tables['name']+CADENA_COLUMN+PRIMARY_COLUMN+column.name]
                else:    
                    columna = self.listaColumnas[tables['name']+CADENA_COLUMN+column.name]
                columna.select()                
   
    def populate_scrollable_frame(self, frame, tables_original):
        self.var_list = []
        total_pasos = len(tables_original) + 1
        pasos_por_parte = total_pasos // 8
        for index, table in enumerate(tables_original):
            self.var_list.append(IntVar(value=0))
            table_frame = CTkFrame(frame, fg_color="#FFFFFF", corner_radius=10)
            table_frame.pack(fill="x", padx=10, pady=2, expand=True)
            #check con la lista de tablas
            table_checkbox = CTkCheckBox(table_frame, text=table.name,command=partial(self.choose,table,index), variable=self.var_list[index], 
                                            text_color="black", font=("Arial", 10, "bold"),
                                            checkbox_height=15, checkbox_width=15, border_color='#84bfc4', fg_color='#84bfc4')
            table_checkbox.pack(side="left", padx=5)
            if self.estado_tables != None and len([x for x in self.estado_tables if x['name'] == table.name]) == 1:
                table_checkbox.select()
            
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
                nombreColumna = column.name
                if (column.primaryKey == 'P'):
                    nombreColumna = nombreColumna + " - PK"
                column_checkbox = CTkCheckBox(columns_frame, text=nombreColumna, variable=tk.BooleanVar(value=False), 
                                                text_color="black", font=("Arial", 10, "bold"),
                                                command=partial(self.selectTableFromColumn,table_checkbox,column.name),
                                                checkbox_height=15, checkbox_width=15, border_color='#84bfc4', fg_color='#84bfc4')
                column_checkbox.pack(anchor="w", padx=20)
                column_checkbox.primaryKey = column.primaryKey
                if (column.primaryKey == 'P'):
                    self.listaColumnas[table.name+CADENA_COLUMN+PRIMARY_COLUMN+column.name] = column_checkbox
                else:
                    self.listaColumnas[table.name+CADENA_COLUMN+column.name] = column_checkbox
            self.tables.append(table_frame)
            if index % pasos_por_parte == 0:  
                porcentaje = (index / total_pasos) 
                if(porcentaje < 0.2): 
                    porcentaje = 0.2 
                self.master.update_progress(porcentaje)     
        self.master.update_progress(1.0)    

    def toggle_columns(self, table_frame):
    # Asegúrate de referirte al columns_frame para expandir/contraer
        if table_frame.columns_frame.winfo_viewable():
            table_frame.columns_frame.pack_forget()
            table_frame.winfo_children()[1].configure(text="▼")  # Icono cambia a 'expandir'
        else:
            table_frame.columns_frame.pack(fill="x", expand=True, padx=20, pady=2)
            table_frame.winfo_children()[1].configure(text="▲")  # Icono cambia a 'contraer'
    def selectTableFromColumn(self,tableCheck,columnName):
        if (tableCheck._text+CADENA_COLUMN+columnName in self.listaColumnas):
            columnCheckbox = self.listaColumnas[tableCheck._text+CADENA_COLUMN+columnName]
        else : #Si no lo encuentra , es por que es PK
            columnCheckbox = self.listaColumnas[tableCheck._text+CADENA_COLUMN+PRIMARY_COLUMN+columnName]
        #si es campo clave siempre tiene que estar activado
        if (columnCheckbox.primaryKey == 'P'):
            columnCheckbox.select()
        #comprueba si, alguna de las columnas esta activa para dejar marcada la tabla padre
        if (len(self.listaColumnas) != 0 and 
            columnCheckbox.get() == 1):
            tableCheck.select()
            #Comprobar que la PK tambien esta seleccionada SIEMPRE
            #Buscar primaries y activarlas todas
            cadenaPrimaria = tableCheck._text+CADENA_COLUMN+PRIMARY_COLUMN
            listaClaves = [v for k,v in self.listaColumnas.items() if k.startswith(cadenaPrimaria)]
            for clave in listaClaves:
              clave.select()
                
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
        select_all_button = CTkButton(self.footer_frame, text="Seleccionar Todas",bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda:self.master.mostrarSpinner("selectAll"))
        select_all_button.pack(side="left", padx=5)

        deselect_all_button = CTkButton(self.footer_frame, text="Deseleccionar Todas",bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda:self.master.mostrarSpinner("deselectAll"))
        deselect_all_button.pack(side="left", padx=5)

        configuration_warning = CTkLabel(self.footer_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        configuration_warning.pack(side="left", padx=5)
        self.master.configuration_warning = configuration_warning


        next_button = CTkButton(self.footer_frame, text="Siguiente", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25,  command=lambda: self.master.mostrar_pagina_tres(self.main_menu, self.obtener_seleccion_checkbox(),  self.cursor,self.tables_ori))
        next_button.pack(side="right", padx=5)

        back_button = CTkButton(self.footer_frame, text="Atras",bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda : self.master.mostrar_pagina_uno(self.main_menu))
        back_button.pack(side="right", padx=5)
     
        cancel_button = CTkButton(self.footer_frame, text="Cancelar", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command= lambda: self.cancelar())
        cancel_button.pack(side="right", padx=5)

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        self.master.quit()
        self.main_menu.MainMenuLoop()
            
class PaginaTres(CTkFrame):
    def __init__(self, master, main_menu, tables,  cursor, tables_ori, estado_tables=None, *args, **kwargs):
        super().__init__(master, *args, **kwargs)
        self.tables_original = tables_ori
        self.cursor = cursor
        # Variables para controlar los checkboxes
        self.modelo_datos_var = tk.BooleanVar(value=False)
        self.daos_var = tk.BooleanVar(value=False)
        self.servicios_var = tk.BooleanVar(value=False)
        self.controladores_var = tk.BooleanVar(value=False)
        
        self.main_menu = main_menu
        self.configure(corner_radius=10)
        
        # Header frame usando grid
        self.header_frame = CTkFrame(self, fg_color="black")
        self.header_frame.grid(row=0, column=0, sticky="new")
        self.grid_columnconfigure(0, weight=1)
        self.grid_rowconfigure(1, weight=1)  # Asegura que el contenedor principal se expanda

        header_label = CTkLabel(self.header_frame, text="Seleccione las opciones para los distintos componentes", font=("Arial", 14, "bold"))
        header_label.pack(pady=10, padx=10)

        # Contenedor principal
        main_container = CTkFrame(self, fg_color="#FFFFFF")
        main_container.grid(row=1, column=0, sticky="nsew")
        main_container.grid_columnconfigure(0, weight=1)
        main_container.grid_rowconfigure(0, weight=1)
        main_container.grid_rowconfigure(1, weight=1)
        self.main_container = main_container

        # Contenedor de Componentes de Negocio
        negocio_container = CTkFrame(main_container, fg_color="#FFFFFF", border_width=3, border_color="#84bfc4")
        negocio_container.grid(row=0, column=0, sticky="nsew")
        negocio_container.grid_columnconfigure(0, weight=1)

        # Título "Componentes de Negocio"
        CTkLabel(negocio_container, text="Componentes de Negocio", text_color="black", font=("Arial", 13, "bold")).grid(row=0, column=0, sticky="w", pady=(10, 20), padx=(20, 20))

        # Componentes individuales
        for index, (component, var) in enumerate([("Modelo de Datos", self.modelo_datos_var), 
                                                  ("DAOs", self.daos_var), 
                                                  ("Servicios", self.servicios_var)]):
            component_container = CTkFrame(negocio_container, fg_color="#FFFFFF")
            component_container.grid(row=index+1, column=0, sticky="ew", pady=5, padx=(20, 20))
            CTkCheckBox(component_container, variable=var, onvalue=True, offvalue=False, text=component,command=self.update_search_state, border_color='#84bfc4', fg_color='#84bfc4', text_color="black", font=("Arial", 11, "bold")).grid(row=0, column=0, padx=(20, 0), sticky="w")
        
        # Entry y Botón de Buscar para Componentes de Negocio
        rutaActual = utl.rutaActual(__file__)
        textRutaNegocio = rutaActual
        textRutaControlador = rutaActual
        ruta_classes = utl.readConfig("RUTA", "ruta_classes")
        ruta_war = utl.readConfig("RUTA", "ruta_war")
        if(ruta_classes != None and ruta_classes != ""):
           textRutaNegocio = ruta_classes 
        if(ruta_war != None and ruta_war != ""):
           textRutaControlador = ruta_war 
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
        self.search_entry_negocio = CTkEntry(negocio_container, width=600, fg_color='#84bfc4')
        self.search_entry_negocio.insert(0, textRutaNegocio)
        self.search_entry_negocio.configure(text_color="grey")
        self.search_entry_negocio.configure(state="disabled")
        self.search_entry_negocio.grid(row=4, column=0, padx=(0,230), pady=(10,0))
        search_button_negocio = CTkButton(negocio_container, text="Buscar",bg_color='#FFFFFF',fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command= lambda: self.buscar_archivos("negocio",self.selectDirectory(self.search_entry_negocio.get())))
        search_button_negocio.grid(row=4, column=0, padx=(670, 0), pady=(10,0))

        # Contenedor de Componentes de Presentacións
        presentacion_container = CTkFrame(main_container, fg_color="#FFFFFF", bg_color="#FFFFFF", border_width=3, border_color="#84bfc4")
        presentacion_container.grid(row=1, column=0, sticky="nsew")
        presentacion_container.grid_columnconfigure(0, weight=1)

        # Título "Componentes de Presentación"
        CTkLabel(presentacion_container, text="Componentes de Presentación",text_color="black", font=("Arial", 13, "bold")).grid(row=0, column=0, sticky="w", pady=(10, 20), padx=(20, 20))

        contenedor_controlador = CTkFrame(presentacion_container, fg_color="#FFFFFF")
        contenedor_controlador.grid(row=1, column=0, sticky="ew", pady=5, padx=(20, 20))
        # Checkbox para "Controladores"
        controladores_checkbox = CTkCheckBox(contenedor_controlador, onvalue=True, offvalue=False, text="Controladores", text_color="black",border_color='#84bfc4', fg_color='#84bfc4', font=("Arial", 12, "bold"), variable=self.controladores_var, command=self.update_search_state)
        controladores_checkbox.grid(row=1, column=0, padx=(20, 0), sticky="w")

        # Entry y Botón de Buscar para Componentes de Presentación
        self.search_entry_presentacion = CTkEntry(presentacion_container, width=600, fg_color='#84bfc4')
        self.search_entry_presentacion.insert(0, textRutaControlador)
        self.search_entry_presentacion.configure(text_color="grey")
        self.search_entry_presentacion.configure(state="disabled")
        self.search_entry_presentacion.grid(row=2, column=0, padx=(0,230), pady=(10,0))
        search_button_presentacion = CTkButton(presentacion_container, text="Buscar", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda : self.buscar_archivos("presentacion",self.selectDirectory(self.search_entry_presentacion.get())))
        search_button_presentacion.grid(row=2, column=0, padx=(670, 0), pady=(10,0))

        # Botones finales en el pie de página
        buttons_container = CTkFrame(self, fg_color="#FFFFFF", bg_color="#FFFFFF")
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
        CTkButton(buttons_container, text="Finalizar", command=lambda: self.master.mostrarSpinner("finalizar"), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',  hover_color='#41848a',text_color="black", font=("Arial", 12, "bold"), width= 100, height=25).pack(side="right", padx=5, pady=20)
        CTkButton(buttons_container, text="Atras",bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda : self.master.mostrar_pagina_dos(self.main_menu, tables_original, estado_tables=tables)).pack(side="right", padx=5, pady=20)
        CTkButton(buttons_container, text="Cancelar", command=lambda: self.cancelar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25).pack(side="right", padx=5, pady=20)
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
            if project_name == '':
                project_name = archivoWar.replace(war_name+"War","")
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



    def mostrar_resultados_scrollbar(self, files, boton_pulsado, ruta):
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
        desc_label = ctk.CTkLabel(scrollbar_resumen, text="Seleccione un WAR", text_color="black")
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
            texto = "Esta ruta no contiene ningún War"
            if boton_pulsado == "negocio" :
                texto = "Esta ruta no contiene ningún EarClasses"  
            desc_label3 = CTkLabel(scrollbar_container, text=texto,text_color="red")
            desc_label3.pack(fill="x", pady=(0, 2), padx=30, anchor="w")

        # Botones de acción en el pie de página
        button_frame = ctk.CTkFrame(resultados_window, fg_color="#FFFFFF", border_color="#84bfc4")
        button_frame.grid(row=2, column=0, columnspan=3, sticky="ew", pady=20)
        
        buscar_button = ctk.CTkButton(button_frame, text="Buscar", command=lambda: self.open_file_explorer(resultados_window, boton_pulsado), fg_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        buscar_button.pack(side="left", padx=10, expand=True)
        
        cancel_button = ctk.CTkButton(button_frame, text="Cancelar", command=resultados_window.destroy, fg_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        cancel_button.pack(side="right", padx=10, expand=True)
        
        accept_button = ctk.CTkButton(button_frame, text="Aceptar", command=lambda: self.aceptar(resultados_window, selected_file.get(),boton_pulsado, ruta), fg_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        accept_button.pack(side="right", padx=10, expand=True)

    def mostrar_resultados(self, files, boton_pulsado,ruta):



        if files != None and len(files) > 6:
            self.mostrar_resultados_scrollbar(files, boton_pulsado, ruta)

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
            desc_label = CTkLabel(file_frame, text="Seleccione un WAR ", text_color= "black")
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
                texto = "Esta ruta no contiene ningún War"
                if boton_pulsado == "negocio" :
                    texto = "Esta ruta no contiene ningún EarClasses"  
                desc_label3 = CTkLabel(file_frame, text=texto,text_color="red")
                desc_label3.grid(row=3, column=0, columnspan=3, pady=(0,2), padx=30, sticky="w")
            # Botones de acción en el pie de página
            button_frame = ctk.CTkFrame(resultados_window, fg_color="#FFFFFF", border_color="#84bfc4")
            button_frame.pack(fill="x", pady=20)
                
            buscar_button = ctk.CTkButton(button_frame, text="Buscar", command= lambda: self.open_file_explorer(resultados_window, boton_pulsado), fg_color='#84bfc4',  hover_color='#41848a', text_color= "black", font=("Arial", 12, "bold")) 
            buscar_button.pack(side="left", padx=10, expand=True)
                
            cancel_button = ctk.CTkButton(button_frame, text="Cancelar", command=resultados_window.destroy, fg_color='#84bfc4',  hover_color='#41848a', text_color= "black", font=("Arial", 12, "bold"))
            cancel_button.pack(side="right", padx=10, expand=True)
            accept_button = ctk.CTkButton(button_frame, text="Aceptar", command=lambda: self.aceptar(resultados_window, selected_file.get(), boton_pulsado,ruta), fg_color='#84bfc4',  hover_color='#41848a', text_color= "black", font=("Arial", 12, "bold"))
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

    
    def toggle_columns(self, table_frame):
        # Asegúrate de referirte al columns_frame para expandir/contraer
            if table_frame.columns_frame.winfo_viewable():
                table_frame.columns_frame.pack_forget()
                table_frame.winfo_children()[1].configure(text="▼")  # Icono cambia a 'expandir'
            else:
                table_frame.columns_frame.pack(fill="x", expand=True, padx=20, pady=2)
                table_frame.winfo_children()[1].configure(text="▲")  # Icono cambia a 'contraer'

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        self.master.quit()
        self.main_menu.MainMenuLoop()

    def cancelar_cerrar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        sys.exit(0)

    def comprobar_relaciones(self, tablas_originales, tablasSeleccionadas):
        
        ##Esta query saca las relaciones entre las tablas
        query_relations = """WITH fk_tables AS (
                            -- Seleccionamos las tablas que tienen claves foráneas
                            SELECT acc.table_name, acc.column_name, ac.constraint_name AS fk_constraint_name,
                                acc.position, ac.constraint_type, acc2.table_name AS referenced_table, acc2.column_name AS referenced_column
                            FROM all_cons_columns acc
                            JOIN all_constraints ac 
                                ON acc.constraint_name = ac.constraint_name
                            JOIN all_cons_columns acc2 
                                ON ac.r_constraint_name = acc2.constraint_name
                            WHERE ac.constraint_type = 'R' -- Clave foránea (Foreign Key)
                        ),
                        pk_tables AS (
                            -- Seleccionamos las tablas que tienen claves primarias
                            SELECT acc.table_name, acc.column_name
                            FROM all_cons_columns acc
                            JOIN all_constraints ac 
                                ON acc.constraint_name = ac.constraint_name
                            WHERE ac.constraint_type = 'P' -- Primary Key
                        ),
                        unique_constraints AS (
                            -- Seleccionamos las columnas que tienen restricciones de unicidad (UNIQUE)
                            SELECT acc.table_name, acc.column_name
                            FROM all_cons_columns acc
                            JOIN all_constraints ac 
                                ON acc.constraint_name = ac.constraint_name
                            WHERE ac.constraint_type = 'U' -- Unique Key
                        )
                        SELECT fk.table_name AS child_table, 
                            fk.column_name AS child_column, 
                            fk.referenced_table AS parent_table, 
                            fk.referenced_column AS parent_column,
                            CASE 
                                -- Relación Many-to-Many: Si la tabla intermedia contiene únicamente claves PF (Primary/Foreign Keys)
                                WHEN fk.table_name IN (
                                        SELECT pf.table_name FROM (
                                            SELECT fk.table_name
                                            FROM fk_tables fk
                                            JOIN pk_tables pk 
                                                ON fk.table_name = pk.table_name 
                                                AND fk.column_name = pk.column_name
                                            GROUP BY fk.table_name
                                            HAVING COUNT(DISTINCT fk.column_name) = (
                                                SELECT COUNT(*) FROM user_tab_columns utc WHERE utc.table_name = fk.table_name)
                                        ) pf
                                )
                                THEN 'Many to Many'

                                -- Relación One-to-One: Si la clave foránea es también la clave primaria de la tabla hija
                                WHEN fk.column_name IN (
                                        SELECT column_name FROM pk_tables WHERE table_name = fk.table_name
                                ) 
                                THEN 'One to One'

                                -- Relación One-to-Many: Si la clave foránea **no es** la clave primaria en la tabla hija
                                WHEN fk.column_name NOT IN (
                                        SELECT column_name FROM pk_tables WHERE table_name = fk.table_name
                                )
                                THEN 'One to Many'

                                ELSE 'Unknown'
                            END AS relationship_type
                        FROM fk_tables fk
                        LEFT JOIN pk_tables pk 
                            ON fk.referenced_table = pk.table_name 
                            AND fk.referenced_column = pk.column_name
                        ORDER BY fk.table_name, relationship_type
                        """
        
        self.cursor.execute(query_relations)

        resultados = self.cursor.fetchall()
        for table in tablasSeleccionadas:
            for table_ori in tablas_originales:
                if table['name'] == table_ori[5]:
                    table['original_table'] = table_ori[0]
                    
                    break
                    
                else:
                    if table_ori[5] is None:
                        table['original_table'] = table['name']
                        break

                    
            
        tablas_seleccionadas_nombres = [tabla['original_table'] for tabla in tablasSeleccionadas]
        relaciones_encontradas = []
    
        relaciones_combinadas = []
        
        relaciones_temp = {}

        relaciones_a_eliminar = []  # Para almacenar las relaciones que serán eliminadas
        
        #En este buble se eliminan la redundancia de relaciones entre las tablas que son intermedias
        # Y se unifican unicamente en las tablas que realmente estan relacionadas
        for relacion in resultados:
            tabla_intermedia, columna_intermedia, tabla_1, columna_1, tipo_relacion = relacion
            
            # Solo procesamos las relaciones de tipo "Many to Many"
            if tipo_relacion == 'Many to Many':
       
                if tabla_intermedia in relaciones_temp:

                    relacion_previa = relaciones_temp[tabla_intermedia]
                    nueva_relacion = (relacion_previa[2], relacion_previa[3], tabla_1, columna_1, tipo_relacion)
                    
                    relaciones_combinadas.append(nueva_relacion)
                    
                    relaciones_a_eliminar.append(relacion_previa)
                    relaciones_a_eliminar.append(relacion)
                    
                    del relaciones_temp[tabla_intermedia]
                else:

                    relaciones_temp[tabla_intermedia] = relacion

        # Se eliminan de las relaciones originales las que han sido combinadas
        for relacion in relaciones_a_eliminar:
            resultados.remove(relacion)
        resultados.extend(relaciones_combinadas)

        # Recorrer las relaciones de resultados  con las modificaciones y comprueba si
        # dos tablas que han sido seleccionadas estan relacionadas entre si 
        for relacion in resultados:
            # Desempaquetar los valores de la relación
            tabla_1, columna_1, tabla_2, columna_2, tipo_relacion = relacion
            
            if tabla_1 in tablas_seleccionadas_nombres and tabla_2 in tablas_seleccionadas_nombres:
                relaciones_encontradas.append(relacion)
                continue

            # Si es una relación many-to-many con una tabla intermedia
            if tipo_relacion == 'Many to Many':

                tabla_intermedia, columna_intermedia = tabla_1, columna_1
                
                if tabla_intermedia in relaciones_temp:

                    relacion_previa = relaciones_temp[tabla_intermedia]
                    nueva_relacion = (relacion_previa[2], relacion_previa[3], tabla_2, columna_2, tipo_relacion)
                    relaciones_combinadas.append(nueva_relacion)

                    del relaciones_temp[tabla_intermedia]
                else:
                    relaciones_temp[tabla_intermedia] = relacion


        return relaciones_encontradas, tablasSeleccionadas


    # Añadir relaciones a las tablas seleccionadas
    def agregar_relaciones_a_tablas(self, tablas_seleccionadas, relaciones):
        for relacion in relaciones:
            tabla_1, columna_1, tabla_2, columna_2, tipo_relacion = relacion
            
            # Encontrar ambas tablas en la lista de tablas seleccionadas
            tabla_obj_1 = self.encontrar_tabla(tabla_1, tablas_seleccionadas)
            tabla_obj_2 = self.encontrar_tabla(tabla_2, tablas_seleccionadas)
            tName = snakeToCamel(tabla_1)
            tName2 = snakeToCamel(tabla_2)

            if not tabla_obj_1 or not tabla_obj_2:
                continue  # Si no encontramos las tablas, pasamos a la siguiente relación
            
            #Se añaden metodos para el dao
            
            

            if tipo_relacion == 'One to One':

                #En los casos One to One solo guardo en la tabla "Padre"
                nueva_columna_2 = {
                    'name': tName, 
                    'type': tName.capitalize(),
                    'dataPrecision': None,
                    'datoImport': None,
                    'datoType': None,
                    'nullable': 'N',
                    'primaryKey': ' ',
                    'tableName': tabla_1
                }
                tabla_obj_2['columns'].append(nueva_columna_2)

                tabla_obj_1['dao'] =  None
                tabla_obj_2['dao'] =  None

            elif tipo_relacion == 'One to Many':
                # Para One to Many, agregamos una lista en la segunda tabla (la que tiene "Many")
                nueva_lista_2 = {
                    'name': tabla_1.lower(), 
                    'type': 'LIST',  
                    'entidad': tabla_1.capitalize(),  # Aqui guardo la entidad para cuando se genera la variable de tipo List
                    'dataPrecision': None,
                    'datoImport': None,
                    'datoType': None,
                    'nullable': 'N',
                    'primaryKey': ' ',
                    'tableName': tabla_1
                }
                tabla_obj_2['columns'].append(nueva_lista_2)

                #Extraer primary key del padre
                numero_columna = 0
                for index, columns in enumerate(tabla_obj_2['columns']):

                    if columns['primaryKey']  == 'P':
                        numero_columna = index
                        break
            
                #Se pasan los datos necesarios para las plantillas del dao 
                tabla_obj_1['dao'] =  {
                        'entidadPadre': tabla_obj_2['name'].capitalize(), 
                        'primaryKey': tabla_obj_2['columns'][numero_columna]['name'].capitalize(),
                        'columns' : tabla_obj_2['columns']
                        
                    }
                
                tabla_obj_2['dao'] =  None

                # En la primera tabla, agregamos una referencia a la segunda tabla como entidad
                nueva_columna_1 = {
                    'name': tName2,
                    'type': tName2.capitalize(),
                    'dataPrecision': None,
                    'datoImport': None,
                    'datoType': None,
                    'nullable': 'N',
                    'primaryKey': ' ',
                    'tableName': tabla_2
                }
                tabla_obj_1['columns'].append(nueva_columna_1)

            elif tipo_relacion == 'Many to Many':
                # Para Many to Many, agregamos una lista en ambas tablas
                nueva_lista_1 = {
                    'name': tName2,
                    'type': 'LIST',
                    'entidad': tName2.capitalize(),
                    'dataPrecision': None,
                    'datoImport': None,
                    'datoType': None,
                    'nullable': 'N',
                    'primaryKey': ' ',
                    'tableName': tabla_2
                }
                nueva_lista_2 = {
                    'name': tName,
                    'type': 'LIST',
                    'entidad': tName.capitalize(),
                    'dataPrecision': None,
                    'datoImport': None,
                    'datoType': None,
                    'nullable': 'N',
                    'primaryKey': ' ',
                    'tableName': tabla_1
                }
                tabla_obj_1['columns'].append(nueva_lista_1)
                tabla_obj_2['columns'].append(nueva_lista_2)
                
                
                
                #Extraer primary key del padre
                numero_columna_tab1 = 0
                for index, columns in enumerate(tabla_obj_1['columns']):

                    if columns['primaryKey']  == 'P':
                        numero_columna_tab1 = index
                        break

            #Extraer primary key del padre
                numero_columna_tab2 = 0
                for index, columns in enumerate(tabla_obj_2['columns']):

                    if columns['primaryKey']  == 'P':
                        numero_columna_tab2 = index
                        break
            
                #Se guardan los datos necesarios para modificar las plantillas del controler 
                tabla_obj_1['controller'] =  {
                        'entidadRelacion': tabla_obj_2['name'].capitalize(), 
                        'primaryKeyCol': [tabla_obj_1['columns'][numero_columna_tab1]],
                        'columns' : tabla_obj_1['columns'],
                        'colPrimaryRelacion': [tabla_obj_2['columns'][numero_columna_tab2]]
    

                        
                    }
                
                 #Se guardan los datos necesarios para modificar las plantillas del controler 
                tabla_obj_2['controller'] =  {
                        'entidadRelacion': tabla_obj_2['name'].capitalize(), 
                        'primaryKeyCol': [tabla_obj_2['columns'][numero_columna_tab2]],
                        'columns' : tabla_obj_2['columns'],
                        'colPrimaryRelacion': [tabla_obj_1['columns'][numero_columna_tab1]],
                        
                    }
                tabla_obj_1['dao'] =  None
                tabla_obj_2['dao'] =  None

        return tablas_seleccionadas

     # Función para encontrar una tabla seleccionada
    def encontrar_tabla(self, tabla_nombre, tablas_seleccionadas):
        for tabla in tablas_seleccionadas:
            if tabla['name'] == tabla_nombre:
                return tabla
        return None


class VentanaPrincipal(CTk):
   
    def __init__(self, main_menu):
        super().__init__()
        self.title("Generar código de negocio y control")
        # width = self.winfo_screenwidth() - 100
        # height = self.winfo_screenheight() - 100
        # self.geometry(str(width)+"x"+str(height)) # Puedes ajustar las dimensiones según tus necesidades
        # padx = 0 # the padding you need.
        # pady = 0
        # toplevel_offsetx = 50
        # toplevel_offsety = 50
        # self.geometry(f"+{toplevel_offsetx + padx}+{toplevel_offsety + pady}")
        self.geometry("900x700") 
        self.resizable(width=False, height=False)
        self.stop_event = threading.Event()

        self.main_menu = main_menu

        self.grid_rowconfigure(0, weight=1)
        self.columnconfigure(0, weight=1)

        self.pagina_actual = None
        self.mostrar_pagina(PaginaUno, self.main_menu)

    def mostrar_pagina(self, pagina, main_menu=None, tables=None, cursor = None, tables_ori=None, estado_tables=None):      
        if self.pagina_actual is not None:
            self.pagina_actual.destroy()

        self.pagina_actual = pagina(self, main_menu=main_menu,tables= tables, cursor=cursor, tables_ori=tables_ori, estado_tables=estado_tables)
        self.pagina_actual.grid(row=0, column=0, sticky="nsew")

    def mostrar_pagina_dos(self, main_menu=None, tables=None, cursor = None, tables_ori=None,  estado_tables=None):
        self.mostrar_pagina(PaginaDos, main_menu, tables, cursor, tables_ori, estado_tables)

    def mostrar_pagina_tres(self, main_menu= None ,tables=None, cursor=None, tables_ori=None,  estado_tables=None):
        if(len(tables) == 0):
          self.configuration_warning.configure(text="Debe seleccionar al menos una tabla y una columna")
          return False
        self.close_loading_frame()
        self.mostrar_pagina(PaginaTres, main_menu, tables, cursor, tables_ori, estado_tables)

    def mostrar_pagina_uno(self, main_menu):
        self.mostrar_pagina(PaginaUno, main_menu)

    def update_progress(self,value):
        self.progressbar.set(value)
        self.loading_frame.update_idletasks()
        self.update()
    
    def mostrarSpinner(self,caso):
        # Crear y configurar el Frame de carga
        self.loading_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color='#FFFFFF', border_color='#84bfc4', border_width=3)
        self.loading_frame.place(relx=0, rely=0, relwidth=1, relheight=1)

        l = CTkLabel(self.loading_frame, text="Cargando...", bg_color="#FFFFFF", fg_color="#FFFFFF", text_color="black", font=("Arial", 50, "bold"))
        l.place(relx=0.5, rely=0.5, anchor='center')
        self.progress_var = tk.IntVar()
        self.progressbar = CTkProgressBar(self.loading_frame, variable=self.progress_var)
        self.progressbar.place(relx=0.5, rely=0.5, anchor='center')
      #  self.progressbar.start()
        l.pack()
        self.update()

        if(caso == "avanzarPaso2"):
            # Iniciar el proceso en segundo plano
            #self.actualizar_progress_bar()
            threading.Thread(target=self.avanzar_paso2()).start()
           
            #resultados_window2.after(710, self.avanzar_paso2)
        elif caso == "selectAll": 
            threading.Thread(target=self.select_all()).start()
            #resultados_window2.after(710, self.select_all) 
        elif caso == "deselectAll": 
            threading.Thread(target=self.deselect_all()).start()
            #resultados_window2.after(710, self.deselect_all) 
        elif caso == "finalizar":
            threading.Thread(target=self.validarPaso2()).start()
            #resultados_window2.after(710, self.validarPaso2) 
        elif caso == "paso2To3":
            threading.Thread(target=self.mostrar_pagina_tres(self.main_menu, self.pagina_actual.obtener_seleccion_checkbox())).start()
            #resultados_window2.after(710,self.mostrar_pagina_tres(self.main_menu, self.pagina_actual.obtener_seleccion_checkbox())) 
        elif caso == "paso2To1":
            threading.Thread(target=self.mostrar_pagina_uno(self.main_menu)).start()
            #resultados_window2.after(710,self.mostrar_pagina_uno(self.main_menu))               

    # def ocultarSpinner(self):
    #     self.resultados_window2.destroy()

    def close_loading_frame(self):
        self.stop_event.set()
        if self.loading_frame:
            self.loading_frame.place_forget()  # Oculta el frame
            # Si no planeas reutilizar el frame, puedes destruirlo en su lugar
            self.loading_frame.destroy()
            self.loading_frame = None 

    def actualizar_progress_bar(self):
        if  self.stop_event.is_set():
            # Aquí puedes actualizar el estado del progress bar
            self.progressbar.step(1)  # Actualiza el progress bar
            self.after(100, self.actualizar_progress_bar)  

    def avanzar_paso2(self):         

        # Puedes agregar aquí la lógica para probar la conexión a la base de datos
        print("Conexión probada")
        logging.info("Conexión probada")
       
        un = self.pagina_actual.entries[4].get()
        pw = self.pagina_actual.entries[5].get()
        sid = self.pagina_actual.entries[1].get()
        serviceName = self.pagina_actual.entries[0].get()
        host = self.pagina_actual.entries[2].get()
        port = self.pagina_actual.entries[3].get()
        esquema = self.pagina_actual.entries[6].get()
        url = self.pagina_actual.entries[7].get()
        
        self.tables = [] 
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
        


        self.update_progress(0.1)
        oracledb.init_oracle_client(lib_dir=d)
        try:
            if(sid == ''):
             cs = host + ":" + port + "/" + serviceName
             connection =  oracledb.connect(user=un, password=pw, dsn=cs)
            else:#con SID
             connection =  oracledb.connect(user=un, password=pw, sid=sid,host=host,port=port)
            utl.writeConfig("BBDD", 
            {"servicename":serviceName,"sid":sid,"host":host,"puerto":port,
            "usuario":un,"password":pw,"esquema":esquema,"url":url})
        except Exception as e: 
            logging.exception("An exception occurred BBDD:  " )  
            self.pagina_actual.configuration_warning.configure(text="An exception occurred: " + str(e))
            self.pagina_actual.configuration_warning.configure(text_color ="red")
            self.close_loading_frame()
            return False
        
        with connection.cursor() as cursor:
                cursor.execute(query, esquema=esquema.upper())
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
                            self.tables.append(Table(tableName,columns)) 
                        contPrimaryKey = 0    
                        if row[4]  == 'P': #primarykey
                            contPrimaryKey = contPrimaryKey + 1    
                        columns = []
                        #se crea la columna
                        column = Column(tableNameBBDD,row[1],row[2],row[3],row[4],None,None,row[6])
                        columns.append(column)  
                    
                    if cont == len(rows) and contPrimaryKey < len(columns): #si es la última se mete a la tabla
                        self.tables.append(Table(tableName,columns))   
                    tableName = tableNameBBDD   

        if(len(self.tables) == 0): 
           self.pagina_actual.configuration_warning.configure(text="Ninguna tabla encontrada en esta BBDD")
           self.pagina_actual.configuration_warning.configure(text_color ="red")
           self.close_loading_frame()    
           return False  
  
        self.update_progress(0.2)               
        self.mostrar_pagina_dos(self.main_menu, self.tables, connection.cursor(), rows)  

    def select_all(self):
        total_pasos = len(self.pagina_actual.tables) + 1
        pasos_por_parte = total_pasos // 10
        for cont,table_frame in enumerate(self.pagina_actual.tables, start = 1):
            # Assuming _state is an attribute that holds the checkbox state
            table_frame.winfo_children()[0].select()  # Checkbox de la tabla
            for checkbox in table_frame.columns_frame.winfo_children():
                checkbox.select()
            if cont % pasos_por_parte == 0:  
                porcentaje = (cont / total_pasos)  
                self.update_progress(porcentaje)        
        self.close_loading_frame()
    def deselect_all(self):
        total_pasos = len(self.pagina_actual.tables) + 1
        pasos_por_parte = total_pasos // 10
        for cont,table_frame in enumerate(self.pagina_actual.tables, start = 1):
            # Assuming _state is an attribute that holds the checkbox state
            table_frame.winfo_children()[0].deselect()  # Checkbox de la tabla
            for checkbox in table_frame.columns_frame.winfo_children():
                checkbox.deselect() # Checkbox de las columnas  
            if cont % pasos_por_parte == 0:  
                porcentaje = (cont / total_pasos)  
                self.update_progress(porcentaje)      
        self.close_loading_frame() 

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
            this.close_loading_frame()
            #this.ocultarSpinner()
            return False   
        if(self.search_entry_negocio.get() == '' and negocioActivado):
            self.configuration_warning.configure(text="Ninguna carpeta EarClasses seleccionada")
            this.close_loading_frame()
           #this.ocultarSpinner()
            return False
        if(self.controladores_var.get() and self.search_entry_presentacion.get() == ''):
            self.configuration_warning.configure(text="Ninguna carpeta War seleccionada")
            this.close_loading_frame()
            #this.ocultarSpinner()
            return False
        if len(tabla_resultados) > 1:
            relaciones_encontradas, tablas_seleccionadas = self.comprobar_relaciones(self.tables_original, tabla_resultados) 
            tablas_seleccionadas_modificadas = self.agregar_relaciones_a_tablas(tablas_seleccionadas, relaciones_encontradas )
        else:    
            tablas_seleccionadas_modificadas = tabla_resultados


        p2.initPaso2(tablas_seleccionadas_modificadas, self.getDatos(rutaActual,archivoClases,archivoWar),self)
        this.close_loading_frame()
        #this.ocultarSpinner()
        this.mostrarResumenFinal(tabla_resultados)

     
    def mostrarResumenFinal(self,tablas):
        self = self.pagina_actual
        self.header_frame.destroy()
        self.main_container.destroy()
        self.buttons_container.destroy()
        
        modelos = " Modelos" if self.modelo_datos_var.get() == True else ""
        daos = " DAOs" if self.daos_var .get() == True else ""
        servicios = " Servicios" if self.servicios_var.get() == True else ""
        controladores = " Controladores" if self.controladores_var.get() == True else ""
        
        cabecera_container = CTkFrame(self, fg_color="#FFFFFF", bg_color="#FFFFFF")
        cabecera_container.grid(row=0, column=0, columnspan=3, sticky="nsew")

         # Checkbox for the table
        cabecera_label = ctk.CTkLabel(cabecera_container, text="Se han creado los" + modelos + daos + servicios + controladores+ " de las siguientes tablas y columnas",
                                            text_color="black", font=("Arial", 10, "bold"))
        cabecera_label.grid(row=0, column= 1, padx=230, pady = (30, 0), sticky = "we")



        scrollbar_container = CTkFrame(self, fg_color="#FFFFFF", bg_color="#FFFFFF")
        scrollbar_container.grid(row=1, column=0, columnspan=3, sticky="nsew")
        scrollbar_container.grid_columnconfigure(0, weight=1)
        scrollbar_container.grid_rowconfigure(0, weight=1)
        
        self.scrollbar_resumen = CTkScrollableFrame(scrollbar_container, fg_color="#E0E0E0", scrollbar_fg_color="#E0E0E0")
        self.scrollbar_resumen.pack(fill="both", expand=True, padx=10, pady=10)
        
        self.var_list = []
        self.tables = []
        
        for index, table in enumerate(tablas):
            self.var_list.append(ctk.IntVar(value=0))
            table_frame = ctk.CTkFrame(self.scrollbar_resumen, fg_color="#FFFFFF", corner_radius=10)
            table_frame.pack(fill="x", padx=10, pady=2, expand=True)

            # Checkbox for the table
            table_checkbox = ctk.CTkLabel(table_frame, text=table['name'],
                                            text_color="black", font=("Arial", 10, "bold"))
            table_checkbox.pack(side="left", padx=5)

            expand_icon = ctk.CTkLabel(table_frame, text="▼", fg_color="#FFFFFF", cursor="hand2",
                                       text_color="black", font=("Arial", 10, "bold"))
            expand_icon.pack(side="left", padx=5)
            expand_icon.bind("<Button-1>", lambda event, f=table_frame: self.toggle_columns(f))

            columns_frame = ctk.CTkFrame(table_frame, fg_color="#F0F0F0", corner_radius=10)
            table_frame.columns_frame = columns_frame
            columns_frame.pack(fill="x", expand=True, padx=20, pady=2)
            columns_frame.pack_forget()  # Start with columns hidden

            # Placement of column labels inside the columns_frame
            for column in table['columns']:
                column_label = ctk.CTkLabel(columns_frame, text=f"Column: {column['name']} Type: {column['type']}",
                                            text_color="black", font=("Arial", 10, "bold"))
                column_label.pack(anchor="w", padx=20)
            self.tables.append(table_frame)
     
        frame_boton = CTkFrame(self, fg_color="#FFFFFF", bg_color="#FFFFFF")
        frame_boton.grid(row = 2, column= 0, columnspan=3, sticky="nsew")

        button_cerrar = CTkButton(frame_boton, text="Volver al menu",command=lambda: self.cancelar() , bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25) 
        button_cerrar.grid(row=0, column=0, pady=(100, 30), padx=(350,0), sticky="w")

        button_menu = CTkButton(frame_boton,text="Cerrar" ,command=lambda: self.cancelar_cerrar() , bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25) 
        button_menu.grid(row=0, column=0, pady=(100, 30), padx=(470,0), sticky="w")
    


if __name__ == "__main__":

    menu = m.MainMenu()
    app = VentanaPrincipal(menu)
    app.mainloop()