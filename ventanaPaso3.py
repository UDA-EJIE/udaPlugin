import tkinter as tk
from tkinter import ttk
import yaml
import oracledb
from Column import Column
from Table import Table
import os
from tkinter import filedialog
import json
from customtkinter import *
import plugin.paso3 as p3
import customtkinter as ctk
import plugin.utils as utl
import menuPrincipal as m
from pathlib import Path
import time
import logging

d = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'instantclient_21_12')
#sys.stderr = open('logs/log.log', 'a')
ruta_war = utl.readConfig("RUTA", "ruta_war")

class PaginaUno(CTkFrame):
    def __init__(self, master, main_menu, tables=None, data_mantenimiento=None, indexSeleccionado=None, *args, **kwargs):
        super().__init__(master, *args, **kwargs)
        self.configure(corner_radius=10, fg_color="#E0E0E0", border_color="#69a3d6", border_width=4)
         # Configura el contenedor principal para que las columnas se expandan
        self.grid_columnconfigure(0, weight=1)  # Esto hace que la columna se expanda
        self.grid_columnconfigure(1, weight=1) 

        self.main_menu = main_menu

        configuration_frame = CTkFrame(self)
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")
        configuration_frame.grid_columnconfigure(0, weight=1)
        configuration_frame.grid_rowconfigure(0, weight=1)


        configuration_label = CTkLabel(configuration_frame,  text="Generar nuevo mantenimiento para una aplicación", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(5, 5), padx=20, sticky="w")

        self.configuration_warning = CTkLabel(configuration_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.grid(row=0, column=2, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard genera un nuevo mantenimineto para una aplicación UDA")
        description_label.grid(row=1, column=0, columnspan=3, pady=(5, 5), padx=20, sticky="w")

        desc_label = CTkLabel(configuration_frame, text="Seleccione el WAR al que se quiere añadir el mantenimiento y configure una conexión a la base de datos")
        desc_label.grid(row=2, column=0, columnspan=3, pady=(5, 5), padx=20, sticky="w")

        war_frame = CTkFrame(configuration_frame, fg_color="#E0E0E0", border_color="#707070", border_width=3, height=2.5)
        war_frame.grid(row=3, column=0, sticky="ew", columnspan=3)
        war_frame.grid_columnconfigure(0, weight=1)
        war_frame.grid_rowconfigure(0, weight=1, minsize=100)

        # Crear un widget Label encima del borde del marco
        labelSecurityFrame = CTkLabel(self, text="Selección del proyecto WAR", bg_color="#E0E0E0", fg_color="#E0E0E0", text_color="black", font=("Arial", 12, "bold"))
        labelSecurityFrame.place(in_=war_frame, anchor="sw", x=10, y=35 )
        # Entry y Botón de Buscar para Componentes de Negocio
        rutaActual = utl.rutaActual(__file__)
        ruta_war = utl.readConfig("RUTA", "ruta_war")
        if(ruta_war != None and ruta_war != ""):
           rutaActual = ruta_war 
        archivoWar = utl.buscarArchivo(rutaActual,"War") 
        if(archivoWar != ''):
           rutaActual = rutaActual+"/"+archivoWar
           nombreProyecto = utl.obtenerNombreProyectoWar(rutaActual) 
           self.master.nombreProyecto = nombreProyecto
           self.master.archivoWar = rutaActual 
        else:
            rutaActual = ""  
        self.war_entry = CTkEntry(war_frame, fg_color='#69a3d6', border_color='#69a3d6', height=2.5, width=600, text_color="black")
        self.war_entry.grid(row=0, column=0 , padx=(220,60), pady=(30, 2), sticky="ew")
        self.war_entry.insert(0, rutaActual)
        

        # Botones
        buscar_button = CTkButton(war_frame, text="Buscar...", fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25, command=lambda : self.buscar_archivos(self.selectDirectory(self.war_entry.get())))
        buscar_button.grid(row=0, column=2, pady=(30, 2), padx=20, sticky="ew")
      

        # Formulario
        labels = ["Service name:", "SID:", "Host:", "Puerto:", "Usuario:", "Contraseña:", "Esquema Catálogo:", "URL:"]
        valores = ["serviceName", "sid", "host", "puerto", "usuario", "password", "esquema", "url"]
        self.entries = []


        for i, label_text in enumerate(labels):
            sv = StringVar(self)
            sv.trace_add("write", lambda name, index, mode, sv=lambda:sv: self.urlModify())
            label = CTkLabel(self, text=label_text, fg_color="#E0E0E0", text_color="black", font=("Arial", 12, "bold"))
            label.grid(row=i+1, column=0, sticky="w", padx=(20, 10), pady=(15, 2))
            entry = CTkEntry(self,textvariable=sv, fg_color='#69a3d6', border_color='#69a3d6', height=2.5,
                              width=400, text_color="grey" if label_text == 'URL:' else 'black', show='*' if label_text == 'Contraseña:' else None,state='disabled' if label_text == 'URL:' else 'normal')
            entry.grid(row=i+1, column=1, padx=(0, 200), pady=(15, 2), sticky="ew")
            if (valores[i] != None):
               try:
                 entry.insert(0, utl.readConfig("BBDD", valores[i]))  
               except ValueError:    
                 logging.exception("Error al obtener el valor:" )
            self.entries.append(entry)
        self.urlModify()
        # Botones
        self.test_button = CTkButton(self, text="Probar conexión", command=self.probar_conexion, fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"))
        self.test_button.grid(row=len(labels) + 1, column=0, columnspan=2, pady=10, padx=20, sticky="ew")

        next_button = CTkButton(self, text="Siguiente", command=lambda:self.master.mostrarSpinner("avanzarPaso2"), fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"))
        next_button.grid(row=len(labels) + 2, column=1, pady=0, padx=20, sticky="e")
        
        back_button = CTkButton(self, text="Atrás", command=lambda: self.cancelar(), fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"))
        back_button.grid(row=len(labels) + 2, column=1, pady=0, padx=(0, 180), sticky="e")
        
    def selectDirectory(self,directory):
        if(directory == ""):
            return directory
        else:
            par = Path(directory)
            return str(par.parent)
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
        # Obtener datos de los cuadros de texto
        
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
            logging.exception("Error connecting to Oracle Database: ")
            self.update_button_color('#FF0000')  # Red color on error
            self.configuration_warning.configure(text="Error connecting to Oracle Database")
            self.configuration_warning.configure(text_color ="#FF0000")
            
    def update_button_color(self, color):
        self.test_button.configure(fg_color=color)    
        
        
    def avanzar_paso2(self): 
        
        # Puedes agregar aquí la lógica para probar la conexión a la base de datos
        print("Conexión probada")
        logging.info("Conexión probada")
        
        if self.war_entry.get() == "":
           self.configuration_warning.configure(text="Seleccione un proyecto WAR")
           self.configuration_warning.configure(text_color ="red")
           self.master.ocultarSpinner()
           return False
       
        un = self.entries[4].get()
        pw = self.entries[5].get()
        sid = self.entries[1].get()
        serviceName = self.entries[0].get()
        host = self.entries[2].get()
        port = self.entries[3].get()
        esquema = self.entries[6].get()
        url = self.entries[7].get()
        
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
            if(sid == ''):
                cs = host + ":" + port + "/" + serviceName
                connection =  oracledb.connect(user=un, password=pw, dsn=cs)
            else:#con SID
                connection =  oracledb.connect(user=un, password=pw, sid=sid,host=host,port=port)
            utl.writeConfig("BBDD", 
            {"servicename":serviceName,"sid":sid,"host":host,"puerto":port,
            "usuario":un,"password":pw,"esquema":esquema,"url":url})
        except Exception as e: 
            logging.exception("An exception occurred BBDD:  ")  
            self.configuration_warning.configure(text="An exception occurred: " + str(e))
            self.configuration_warning.configure(text_color ="red")
            self.master.ocultarSpinner()
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
        self.master.mostrar_pagina_dos(self.main_menu, tables)           


    def buscar_archivos(self, ruta_personalizada = None):
            """Busca archivos con terminación 'war' en la misma ruta del script Python."""
            files = None
            if ruta_personalizada == None:
                files = [file for file in os.listdir(ruta_war) if file.endswith("War")]
                self.mostrar_resultados(files,ruta_war)
            else:
                try:
                    files = [file for file in os.listdir(ruta_personalizada) if file.endswith("War")]
                except:
                    logging.exception("No encontro la ruta: " + ruta_personalizada)    
                self.mostrar_resultados(files,ruta_personalizada)



    def mostrar_resultados(self, files, ruta):
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
        desc_label = CTkLabel(file_frame, text="Seleccione un WAR ")
        desc_label.grid(row=0, column=0, columnspan=3, pady=(5, 1), padx=20, sticky="w")
        if (ruta != ''):
            desc_label2 = CTkLabel(file_frame, text="(" + ruta +")")
            desc_label2.grid(row=1, column=0, columnspan=3, pady=(0,2), padx=30, sticky="w")

        # Añadir radiobuttons para cada archivo
        if(files != None and len(files) > 0):
            for index, file in enumerate(files):
                radiobutton = ctk.CTkRadioButton(file_frame, text=file, variable=selected_file, value=file)
                radiobutton.grid(row=index + 3, column=0, sticky="w", padx=60, pady=3)
        else:    
            desc_label3 = CTkLabel(file_frame, text="Esta ruta no contiene ningún War",text_color="red")
            desc_label3.grid(row=3, column=0, columnspan=3, pady=(0,2), padx=30, sticky="w")   

        # Botones de acción en el pie de página
        button_frame = ctk.CTkFrame(resultados_window)
        button_frame.pack(fill="x", pady=20)
            
        buscar_button = ctk.CTkButton(button_frame, text="Buscar", command= lambda: self.open_file_explorer(resultados_window)) 
        buscar_button.pack(side="left", padx=10, expand=True)
            
        cancel_button = ctk.CTkButton(button_frame, text="Cancelar", command=resultados_window.destroy)
        cancel_button.pack(side="right", padx=10, expand=True)
        accept_button = ctk.CTkButton(button_frame, text="Aceptar", command=lambda: self.aceptar(resultados_window, selected_file.get(),ruta))
        accept_button.pack(side="right", padx=10, expand=True)        


    def aceptar(self, frame, selected_file,ruta):
        if selected_file :
            selected_file = ruta+"/"+selected_file
            nombreProyecto = utl.obtenerNombreProyectoWar(selected_file)
            if nombreProyecto != '':
                logging.info(f"Archivo seleccionado: {selected_file}")
                self.war_entry.delete(0, "end")
                self.war_entry.insert(0, selected_file)
                self.master.nombreProyecto = nombreProyecto
                self.master.archivoWar = selected_file
                frame.destroy()
            else:    
                logging.info("Este war no contiene un web.xml.") 
        else:
            logging.info("No se seleccionó ningún archivo.")       

    def open_file_explorer(self, frame):
        # Esta función se llama cuando el usuario hace clic en "Buscar"
        # Abre un diálogo para seleccionar un directorio
        frame.destroy()
        directory = filedialog.askdirectory(parent=self)      
        if directory:  # Si se selecciona un directorio
            selected_directory = directory  # Guardar la ruta del directorio seleccionado
            self.buscar_archivos(selected_directory)
            logging.info(f"Directorio seleccionado: {selected_directory}")
        else:
            logging.info("No se seleccionó ningún directorio.")

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        self.master.quit()
        self.main_menu.MainMenuLoop()

class ventanaPaso2(CTkFrame):
    def __init__(self, master, main_menu, tables, data_mantenimiento=None, indexSeleccionado=None, *args, **kwargs):
        super().__init__(master, *args, **kwargs)
        self.configure(corner_radius=10, fg_color="#E0E0E0", border_color="#69a3d6", border_width=2)
        self.grid_columnconfigure(1, weight=1)


        self.tables = tables
        self.data_mantenimiento = data_mantenimiento
        configuration_frame = CTkFrame(self)
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")
        configuration_frame.grid_columnconfigure(0, weight=1) 

        self.main_menu = main_menu
        
        configuration_label = CTkLabel(configuration_frame,  text="Generar nuevo mantenimiento para una aplicación", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(5, 5), padx=20, sticky="w")

        self.configuration_warning = CTkLabel(configuration_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.grid(row=0, column=2, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar")
        description_label.grid(row=1, column=0, columnspan=3, pady=(5, 5), padx=20, sticky="w")

        desc_label = CTkLabel(configuration_frame, text="Seleccione el WAR al que se quiere añadir el mantenimiento y configure una conexión a la base de datos")
        desc_label.grid(row=2, column=0, columnspan=3, pady=(5, 5), padx=20, sticky="w")

        contenedor_opciones = CTkFrame(self, corner_radius=10, fg_color="#E0E0E0", border_color="#69a3d6", border_width=4)
        contenedor_opciones.grid(row=1, column=0, columnspan= 2,  sticky="nswe", padx=10, pady=10)
        contenedor_opciones.grid_columnconfigure(0, weight=1)
        contenedor_opciones.grid_rowconfigure(1, weight=1)

        # Nombre del mantenimiento
        nombre_label = CTkLabel(contenedor_opciones, text="Nombre del mantenimiento:", text_color="black")
        nombre_label.grid(row=0, column=0, sticky="w", padx=(20, 0), pady=(10,5))
        self.nombre_entry = CTkEntry(contenedor_opciones, fg_color='#69a3d6', border_color='#69a3d6', text_color="black", height=2.5, width=500)
        self.nombre_entry.grid(row=0, column=0, padx=(200, 100),  pady=(10,5), sticky="ew")

        # Título del mantenimiento
        titulo_label = CTkLabel(contenedor_opciones, text="Título del mantenimiento:", text_color="black")
        titulo_label.grid(row=1, column=0, sticky="w", padx=(20, 0), pady=(5,5))
        self.titulo_entry = CTkEntry(contenedor_opciones,fg_color='#69a3d6', border_color='#69a3d6', text_color="black", height=2.5, width=500)
        self.titulo_entry.grid(row=1, column=0, padx=(200, 100),  pady=(5,5), sticky="ew")


        self.checkbox_var = BooleanVar(value=True)
        # Checkbox para estado de mantenimiento
        self.mantenimiento_checkbox = CTkCheckBox(contenedor_opciones, text="Mantenimiento", text_color="black", variable=self.checkbox_var, command= lambda : self.mantenimiento_activo(data_mantenimiento))
        self.mantenimiento_checkbox.grid(row=2, column=0, padx=20, pady=5, sticky="w")

        # Radiobuttons para tipo de mantenimiento
        tipo_label = CTkLabel(contenedor_opciones, text="Tipo de Mantenimiento:", text_color="black")
        tipo_label.grid(row=3, column=0, sticky="w", padx=(20, 0))
        

        self.tipo_var = tk.StringVar(value="Edición en línea")
        self.tipo_radio1 = CTkRadioButton(contenedor_opciones, text="Edición en línea", variable=self.tipo_var, value="Edición en línea", text_color="black", command=lambda: self.edicion_linea() )
        self.tipo_radio1.grid(row=3, column=0, sticky="w", padx=(200, 0))
        self.tipo_radio2 = CTkRadioButton(contenedor_opciones, text="Formulario de detalle", variable=self.tipo_var, value="Formulario de detalle", text_color="black", command=lambda: self.edicion_formulario_detalle())
        self.tipo_radio2.grid(row=4, column=0, sticky="w", padx=(200, 0))

        # Radiobuttons para tipo de mantenimiento / se inicializan antes para poder deshabilitarlos
        self.recuperar_checkbox = CTkCheckBox(contenedor_opciones, text="Recuperar datos de detalle desde servidor", text_color="black")
        self.recuperar_checkbox.grid(row=5, column=0, sticky="w", padx=(200, 0) ,pady=(10,10))


        self.label_tipologia = CTkLabel(contenedor_opciones, text="Tipología de botones:", text_color="black")
        self.label_tipologia.grid(row=6, column=0, sticky="w", padx=(200, 0))


        self.tipologia_label_combobox = CTkComboBox(contenedor_opciones, values=["SAVE", "SAVE_REPEAT"], dropdown_text_color="black",dropdown_fg_color='#69a3d6',border_color="black", fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width=200)
        self.tipologia_label_combobox.grid(row=6, column=0, sticky="w", padx=(350, 20), pady=10)


        # Opciones adicionales
        self.botonera_checkbox = CTkCheckBox(contenedor_opciones, text="Botonera", text_color="black")
        self.botonera_checkbox.grid(row=7, column=0, padx=20, pady=5, sticky="w")

        self.menu_contextual_checkbox = CTkCheckBox(contenedor_opciones, text="Menú contextual", text_color="black")
        self.menu_contextual_checkbox.grid(row=8, column=0, padx=20, pady=5, sticky="w")

        self.filtrado_datos_checkbox = CTkCheckBox(contenedor_opciones, text="Filtrado de datos", text_color="black")
        self.filtrado_datos_checkbox.grid(row=9, column=0, padx=20, pady=5, sticky="w")

        self.busqueda_checkbox = CTkCheckBox(contenedor_opciones, text="Búsqueda", text_color="black")
        self.busqueda_checkbox.grid(row=10, column=0, padx=20, pady=5, sticky="w")

        self.validaciones_cliente_checkbox = CTkCheckBox(contenedor_opciones, text="Validaciones cliente", text_color="black")
        self.validaciones_cliente_checkbox.grid(row=11, column=0, padx=20, pady=5, sticky="w")

        self.multiseleccion_checkbox = CTkCheckBox(contenedor_opciones, text="Multiselección", text_color="black")
        self.multiseleccion_checkbox.grid(row=12, column=0, padx=20, pady=(5, 20), sticky="w")

        # Footer con botones de navegación
        footer_frame = CTkFrame(self, fg_color="#E0E0E0")
        footer_frame.grid(row=3, column=0, columnspan=2, padx=20, sticky="se")
        btn_back = CTkButton(footer_frame, text="Back", command=lambda :master.mostrar_pagina_uno(self.main_menu),  fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"))
        btn_back.pack(side="left", padx=10)
        btn_next = CTkButton(footer_frame, text="Next", command=lambda: self.validarPaso3() ,fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"))
        btn_next.pack(side="left", padx=10)
        btn_cancel = CTkButton(footer_frame, text="Cancel", command=lambda : self.cancelar() ,fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"))
        btn_cancel.pack(side="left", padx=10)

        if data_mantenimiento ==None:
            self.checkbox_var = BooleanVar(value=True)
        else: 
           self.checkbox_var = BooleanVar(value=True if data_mantenimiento[2][1] == 0  else False)

           self.mantenimiento_activo(data_mantenimiento)

        if data_mantenimiento is not None:
            self.nombre_entry.insert(0, data_mantenimiento[00][1])
            self.titulo_entry.insert(0, data_mantenimiento[1][1])
            self.tipo_var.set(data_mantenimiento[3][1])
            self.configurar_checkbox(self.recuperar_checkbox, data_mantenimiento[4][1])  
            self.tipologia_label_combobox.set(data_mantenimiento[5][1])
            self.configurar_checkbox(self.botonera_checkbox, data_mantenimiento[6][1])   
            self.configurar_checkbox(self.menu_contextual_checkbox, data_mantenimiento[7][1])
            self.configurar_checkbox(self.filtrado_datos_checkbox, data_mantenimiento[8][1])
            self.configurar_checkbox(self.busqueda_checkbox, data_mantenimiento[9][1])
            self.configurar_checkbox(self.validaciones_cliente_checkbox, data_mantenimiento[10][1])
            self.configurar_checkbox(self.multiseleccion_checkbox, data_mantenimiento[11][1])




        self.edicion_linea()
        self.master.ocultarSpinner()

    # Otras configuraciones de CheckBox
       
    def validarPaso3(self):
        if(self.titulo_entry.get() == '' or self.nombre_entry.get() == ''):
            self.configuration_warning.configure(text="El nombre y el título del mantenimiento es obligatorio")
            self.configuration_warning.configure(text_color ="red")
            return FALSE
        self.master.mostrarSpinner("paso3To4")

    def configurar_checkbox(self, checkbox, valor):
        """Configura el estado de un CTkCheckBox basado en un valor entero."""
        if valor == 1:
            checkbox.select()
        else:
            checkbox.deselect()

    def mantenimiento_activo(self, data_mantenimiento): 
        #Funcion para deshabilitar botones ne funcion de mantenimiento
        if self.checkbox_var.get() == False:    

            self.tipo_var.set("Edición en línea")
            self.tipo_radio1.configure(state="normal")
            self.tipo_radio2.configure(state="normal")
            
            self.checkbox_var = BooleanVar(value=True)

        elif self.checkbox_var.get() == True: 
            self.tipo_radio1.configure(state="disabled")
            self.tipo_radio2.configure(state="disabled")
            
            self.checkbox_var = BooleanVar(value=False)

            

        self.recuperar_checkbox.configure(state="disabled")
        self.tipologia_label_combobox.configure(state="disabled")
        self.label_tipologia.configure(text_color="grey")

    def edicion_linea(self):
        #funcion para deshabilitar los botones cuando es edicion en linea 
        self.recuperar_checkbox.configure(state="disabled")
        self.tipologia_label_combobox.configure(state="disabled")
        self.label_tipologia.configure(text_color="grey")

    def edicion_formulario_detalle(self):
        self.recuperar_checkbox.configure(state="normal")
        self.tipologia_label_combobox.configure(state="normal")
        self.label_tipologia.configure(text_color="black")



    def obtener_datos(self):
        """Función para recopilar todos los datos de la interfaz en formato de lista de tuplas."""
       
        if self.mantenimiento_checkbox.get() == 0:
            self.tipo_var = None
            datos_servidor = 0


        else: 
            self.tipo_var = self.tipo_var.get()
            datos_servidor = self.recuperar_checkbox.get()
       
        datos = [

            ("nombre_mantenimiento", self.nombre_entry.get()),
            ("titulo_mantenimiento", self.titulo_entry.get()),
            ("mantenimineto", self.mantenimiento_checkbox.get()),  
            ("tipo_mantenimineto", self.tipo_var),
            ("datos_servidor", datos_servidor),
            ("tipologia_botones", self.tipologia_label_combobox.get()),
            ("botonera", self.botonera_checkbox.get()), 
            ("menu_contextual", self.menu_contextual_checkbox.get()),
            ("filtrado_datos", self.filtrado_datos_checkbox.get()),
            ("busqueda", self.busqueda_checkbox.get()),
            ("validaciones_cliente", self.validaciones_cliente_checkbox.get()),
            ("multiseleccion", self.multiseleccion_checkbox.get())
        ]
        return datos
    
    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        self.master.quit()
        self.main_menu.MainMenuLoop()



class VentanaPaso3(CTkFrame):
    def __init__(self, master, main_menu, tables, data_mantenimiento, indexSeleccionado=None, *args, **kwargs):
        super().__init__(master, *args, **kwargs)
        self.tables = tables
        self.data_mantenimiento = data_mantenimiento
        self.grid_columnconfigure(0, weight=1)
        self.grid_rowconfigure(0, weight=1)
        self.configure(corner_radius=10, fg_color="#E0E0E0", border_color="#69a3d6", border_width=2)

        self.main_menu = main_menu
        # Izquierda: Contenedor para la lista de entidades con radio buttons
        left_container = CTkFrame(self, corner_radius=3, bg_color="#E0E0E0", border_color="#69a3d6", )
        left_container.grid(row=0, column=0, sticky="nswe", padx=10, pady=10)
        left_container.grid_rowconfigure(0, weight=1)
        left_container.grid_columnconfigure(0, weight=1)

        # Scrollbar para los radio buttons
        self.scrollbar = CTkScrollableFrame(left_container, fg_color="#E0E0E0")
        self.scrollbar.pack(fill="both", expand=True, padx=10, pady=10)
        
        self.radio_var = tk.StringVar(value=tables[0].name if tables else None)  # Valor predeterminado
        
        # Creamos los radio buttons dentro del scrollbar
        for i, table in enumerate(tables):
            radio_button = CTkRadioButton(self.scrollbar , text=table.name, variable=self.radio_var, value=table.name, text_color="black", command=lambda table=table, i=i: self.actualizar_indice(i, table.name, table))
            radio_button.grid(row=i, column=0, sticky="w", padx=10, pady=2)

        # Si no se proporciona un índice seleccionado, usamos 0 por defecto
        if indexSeleccionado is None:
            indexSeleccionado = 0
        self.tabla_seleccionada_index = indexSeleccionado



        # Derecha: Contenedor para los campos de entrada y opciones
        right_container = CTkFrame(self, corner_radius=5, fg_color="#E0E0E0", border_color="#69a3d6")
        right_container.grid(row=0, column=1, sticky="nswe", padx=10, pady=10)
        right_container.grid_columnconfigure(1, weight=1)

        # Campos de entrada y otros widgets en el contenedor derecho
        url_label = CTkLabel(right_container, text="URL(*):", text_color="black")
        url_label.grid(row=0, column=0, sticky="w", padx=10, pady=10)
        self.url_entry = CTkEntry(right_container, fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"))
        self.url_entry.grid(row=0, column=1, sticky="we", padx=10, pady=10)

        alias_label = CTkLabel(right_container, text="Alias(*):", text_color="black")
        alias_label.grid(row=1, column=0, sticky="w", padx=10, pady=10)
        self.alias_entry = CTkEntry(right_container, fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"))
        self.alias_entry.grid(row=1, column=1, sticky="we", padx=10, pady=10)

        var_cargar = ctk.BooleanVar(value=True)    
        self.cargar_check = CTkCheckBox(right_container, text="Cargar al inicio de la ventana", text_color="black", variable=var_cargar)
        self.cargar_check.grid(row=2, column=0, columnspan=2, sticky="w", padx=10, pady=10)

        orden_label = CTkLabel(right_container, text="Ordenación:", text_color="black")
        orden_label.grid(row=3, column=0, sticky="w", padx=10, pady=10)
        self.orden_combobox = CTkComboBox(right_container, values=["asc", "desc"], fg_color='#69a3d6', text_color="black",state="readonly", font=("Arial", 12, "bold"))
        self.orden_combobox.grid(row=3, column=1, sticky="we", padx=10, pady=10)
        self.orden_combobox.set("asc")

        orden_nombre_label = CTkLabel(right_container, text="Ordenación por:", text_color="black")
        orden_nombre_label.grid(row=4, column=0, sticky="w", padx=10, pady=10)
        self.orden_nombre_combobox = CTkComboBox(right_container, fg_color='#69a3d6', text_color="black", state="readonly", font=("Arial", 10, "bold"))
        self.orden_nombre_combobox.grid(row=4, column=1, sticky="we", padx=10, pady=10)

        # Asegurarse de que el índice predeterminado se maneja desde el inicio
        self.actualizar_indice(indexSeleccionado, tables[indexSeleccionado].name, tables[indexSeleccionado])

        # Footer con botones
        footer_frame = CTkFrame(self, fg_color="#E0E0E0")
        footer_frame.grid(row=1, column=0, columnspan=2, sticky="se", padx=10, pady=(20, 20))
        
        btn_back = CTkButton(footer_frame, text="Back", fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), command= lambda: master.mostrar_pagina_dos(self.main_menu, data_mantenimiento=data_mantenimiento, tables=tables))
        btn_back.pack(side="left", padx=10, pady=5)
        btn_next = CTkButton(footer_frame, text="Next", fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), command=lambda : [self.anaydir_data_mantenimineto() , self.master.mostrarSpinner("paso4To5")])
        btn_next.pack(side="left", padx=10, pady=5)
        btn_cancel = CTkButton(footer_frame, text="Cancel" ,fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), command= lambda: self.cancelar())
        btn_cancel.pack(side="left", padx=10, pady=5)
        self.master.ocultarSpinner()

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        self.master.quit()
        self.main_menu.MainMenuLoop()

    def anaydir_data_mantenimineto(self):
        
        if  len(self.data_mantenimiento) > 12: 
            self.data_mantenimiento =  self.data_mantenimiento[:len(self.data_mantenimiento)- 3]
        self.data_mantenimiento.append(("alias", self.alias_entry.get()))
        self.data_mantenimiento.append(("cargar_check", self.cargar_check.get()))
        self.data_mantenimiento.append(("ordenacion_por", self.obtener_posicion(self.orden_nombre_combobox.get())))
        self.data_mantenimiento.append(("ordenacion", (self.orden_combobox.get())))



    def actualizar_indice(self, index, name, table):
        """Actualizar el índice de la tabla seleccionada."""
        self.tabla_seleccionada_index = index
        # Borrar contenido existente
        self.url_entry.delete(0, "end") 
        self.alias_entry.delete(0, "end")  
        self.url_entry.insert(0, "../" + name)
        self.alias_entry.insert(0, name)
        
        logging.info("Índice seleccionado:" + str(index))

        self.orden_nombre_combobox.set("")

        # crea el array con los nombres de las columnas y una lista con nombre y posicion
        columnas = [column.name for column in table.columns]
        self.column_dict = {name: pos for pos, name in enumerate(columnas)}
        
        # Actualizar los valores del combobox
        self.orden_nombre_combobox.configure(values=columnas)
        self.orden_nombre_combobox.set(columnas[0])

        
    def obtener_posicion(self, nombre):
        """Obtener la posición del nombre dado en el combobox."""
        return self.column_dict.get(nombre, None)
    
    def abrir_ventana_columnas(self):
        """Usar el índice seleccionado para abrir otra ventana o realizar alguna acción."""
        index_seleccionado = self.tabla_seleccionada_index
        logging.info("Índice seleccionado: " + str(index_seleccionado))
        return index_seleccionado
           
class VentanaColumnas(CTkFrame):
    def __init__(self, master, main_menu, tables, data_mantenimiento, index_seleccionado, *args, **kwargs):
        super().__init__(master, *args, **kwargs)
        self.tables = tables
        self.data_mantenimiento = data_mantenimiento
        self.index_seleccionado = index_seleccionado
        self.configure(corner_radius=10, fg_color="#E0E0E0", border_color="#69a3d6", border_width=4)
         # Configura el contenedor principal para que las columnas se expandan
        self.grid_columnconfigure(0, weight=1)  # Esto hace que la columna se expanda
        self.grid_rowconfigure(3, weight=1)  # Row para contenedor_principal se expande
        
        self.main_menu = main_menu

        self.configuration_frame = CTkFrame(self)
        self.configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")
        self.configuration_frame.grid_columnconfigure(0, weight=1) 


        configuration_label = CTkLabel(self.configuration_frame,  text="Generar nuevo mantenimiento para una aplicación", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(5, 5), padx=20, sticky="w")

        description_label = CTkLabel(self.configuration_frame, text="Este Wizard genera la estructura necesaria para desarrollar una aplicación estándar")
        description_label.grid(row=1, column=0, columnspan=3, pady=(5, 5), padx=20, sticky="w")

        desc_label = CTkLabel(self.configuration_frame, text="Seleccione el WAR al que se quiere añadir el mantenimiento y configure una conexión a la base de datos")
        desc_label.grid(row=2, column=0, columnspan=3, pady=(5, 5), padx=20, sticky="w")
        # Contenedor principal
        self.contenedor_principal = ctk.CTkFrame(self)
        self.contenedor_principal.grid(row=3, column=0, sticky="nsew", padx=10, pady=10)
        self.contenedor_principal.grid_columnconfigure(0, weight=1)
        self.contenedor_principal.grid_rowconfigure(0, weight=1)

        # Contenedor Scrollable para los Checkbuttons
        self.scrollable_container = ctk.CTkScrollableFrame(self.contenedor_principal, fg_color="#E0E0E0", width=400, height=300)
        self.scrollable_container.grid(row=0, column=0, sticky="nsew", padx=10, pady=10)


        # Checkbuttons para cada columna
        self.column_checkboxes = []
        for i, columna in enumerate(tables[index_seleccionado].columns):
            text = ""
            if columna.nullable:
                text += " Nullable"
            if columna.primaryKey:
                text += " PK"
            var = ctk.BooleanVar(value=True)
            checkbox = ctk.CTkCheckBox(self.scrollable_container, text=f"{columna.name}: {columna.type}{text}", variable=var, text_color="black", font=("Arial", 12, "bold"))
            checkbox.grid(row=i, column=0, sticky="w")
            self.column_checkboxes.append(var)

        # Botones
        self.contenedor_botones = ctk.CTkFrame(self, fg_color="#E0E0E0")
        self.contenedor_botones.grid(row=4, column=0, sticky="se", padx=10, pady=10)

        back_button = ctk.CTkButton(self.contenedor_botones, text="Back", fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), command=lambda : master.mostrar_pagina_tres(self.main_menu, data_mantenimiento, tables,  index_seleccionado))
        back_button.grid(row=0, column=0, padx=5, sticky="e")
        
        finish_button = ctk.CTkButton(self.contenedor_botones, text="Finish",  fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), command=lambda:self.master.mostrarSpinner("finalizar") )
        finish_button.grid(row=0, column=1, padx=5, sticky="e")

        cancel_button = ctk.CTkButton(self.contenedor_botones, text="Cancel", fg_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), command= lambda: self.cancelar())
        cancel_button.grid(row=0, column=2, padx=5, sticky="e")
        self.master.ocultarSpinner()

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        self.master.quit()
        self.main_menu.MainMenuLoop()

    def getTablaResultados(self,tb):
        tabla_resultados = []
        
        tabla = {}
        tabla['name'] = tb.name
        tabla['columns'] = []
        i = 0
        for column in tb.columns:
            if(self.column_checkboxes[i].get()):
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
            i = i +1    
        tabla_resultados.append(tabla)
        return tabla_resultados 
    
    def paso3(self,tables, index_seleccionado, datosCargados, data_mantenimiento): 
        this = self.master
        tablaResultados = self.getTablaResultados(tables[index_seleccionado])
        p3.initPaso3(tablaResultados, datosCargados, data_mantenimiento)
        this.mostrarResumenFinal(tablaResultados)  

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        self.master.quit()
        self.main_menu.MainMenuLoop()  
    
    def cancelar_cerrar(self):
        # Cancela todos los eventos pendientes
        self.master.withdraw()
        self.master.quit()

class VentanaPrincipal(CTk):
    def __init__(self, main_menu):
        super().__init__()
        self.title("Generar código para una aplicación UDA")
        self.geometry("900x700") # Puedes ajustar las dimensiones según tus necesidades
        self.resizable(width=True, height=True)
        self.main_menu = main_menu
        self.grid_rowconfigure(0, weight=1)
        self.columnconfigure(0, weight=1)

        self.pagina_actual = None

        self.mostrar_pagina(PaginaUno, main_menu=self.main_menu, tables=None, data_mantenimineto=None, indexSeleccionado=None)

    def mostrar_pagina(self, pagina, main_menu, tables, data_mantenimineto, indexSeleccionado):
        
        if self.pagina_actual:
            self.pagina_actual.destroy()
        nueva_pagina = pagina(self, main_menu, tables, data_mantenimineto, indexSeleccionado) 
        self.mostrarSpinner("") 
        self.update()  
        nueva_pagina.grid(row=0, column=0, sticky="nsew")
        self.pagina_actual = nueva_pagina
        self.ocultarSpinner()

    def mostrar_pagina_dos(self, main_menu ,tables, data_mantenimiento=None , indexSeleccionado=None):
        self.mostrar_pagina(ventanaPaso2,  main_menu, tables, data_mantenimiento, indexSeleccionado), 

    def mostrar_pagina_tres(self, main_menu, data_mantenimiento, tables, indexSeleccionado=None):
        self.mostrar_pagina(VentanaPaso3, main_menu, tables, data_mantenimiento, indexSeleccionado)

    def mostrar_pagina_cuatro(self, main_menu, tables, data_mantenimineto, indexSeleccionado):
        self.mostrar_pagina(VentanaColumnas, main_menu,  tables, data_mantenimineto, indexSeleccionado)   

    def mostrar_pagina_uno(self, main_menu, tables = None, data_mantenimiento=None , indexSeleccionado=None):
        self.mostrar_pagina(PaginaUno, main_menu, tables, data_mantenimiento, indexSeleccionado )

    def getDatos(self,rutaActual):
        project_name = self.nombreProyecto
        splits = self.archivoWar.split('/')
        war_name = splits[len(splits) - 1]

        data = { "project_name": project_name,
        "security_app": "",
        "war_project_name": war_name,
        "PACKAGE_NAME": "com.ejie."+project_name+".control",
        "directorio_actual" : rutaActual+"/templates/generateCode/",
        "destinoApp" : self.archivoWar
       }
        logging.info(data)
        return data  

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
            resultados_window2.after(710, self.pagina_actual.avanzar_paso2)
        elif caso == "paso3To4":#ir a las columnas
            self.update()
            resultados_window2.after(710,self.mostrar_pagina_tres(self.main_menu, self.pagina_actual.obtener_datos(),self.pagina_actual.tables)) 
        elif caso == "paso4To5":
            resultados_window2.after(710,self.mostrar_pagina_cuatro(self.main_menu, self.pagina_actual.tables, self.pagina_actual.data_mantenimiento, self.pagina_actual.abrir_ventana_columnas()))
        elif caso == "finalizar":
            self.update()
            pfinal = self.pagina_actual
            rutaActual = utl.rutaActual(__file__)
            resultados_window2.after(710, pfinal.paso3(pfinal.tables, pfinal.index_seleccionado, self.getDatos(rutaActual), pfinal.data_mantenimiento))                   

    def ocultarSpinner(self):
        self.resultados_window2.destroy()  

    def mostrarResumenFinal(self,tablas):
        self = self.pagina_actual
        self.configuration_frame.destroy()
        self.contenedor_botones.destroy()
        self.scrollable_container.destroy()
        self.contenedor_principal.destroy()
        
        
        self.master.ocultarSpinner()
        # Create the main container frame
        main_container = CTkFrame(self, fg_color="#E0E0E0")
        main_container.grid(row=0, column=0, sticky="nsew")
        main_container.grid_rowconfigure(0, weight=1)
        main_container.grid_rowconfigure(1, weight=1)
        main_container.grid_columnconfigure(0, weight=1)
        main_container.grid_columnconfigure(1, weight=1)
        main_container.grid_columnconfigure(2, weight=1)

        # Create the label container frame
        label_container = CTkFrame(main_container, fg_color="#E0E0E0")
        label_container.grid(row=0, column=1, sticky="n")
        label_container.grid_rowconfigure(0, weight=1)
        label_container.grid_columnconfigure(0, weight=1)

        # Create the information label
        label_info = CTkLabel(label_container, text="Has creado el mantenimiento de la siguiente tabla " + tablas[0]['name'], 
                            bg_color="#E0E0E0", text_color="black", font=("Arial", 15, "bold"))
        label_info.grid(row=0, column=0, pady=20, padx=20, sticky="n")

        # Create a button container frame for buttons at the bottom
        button_container = CTkFrame(main_container, fg_color="#E0E0E0")
        button_container.grid(row=1, column=0, columnspan=3, pady=20, sticky="s")
        button_container.grid_columnconfigure(0, weight=1)
        button_container.grid_columnconfigure(1, weight=1)

        # Create the 'Volver al menu' button
        boton_menu = ctk.CTkButton(button_container, text="Volver al menú", fg_color='#69a3d6', text_color="black", 
                                font=("Arial", 12, "bold"), command=lambda: self.cancelar())
        boton_menu.grid(row=0, column=0, padx=5, pady=(200,0), sticky="se")

        # Create the 'cerrar' button
        boton_cerrar = ctk.CTkButton(button_container, text="cerrar", fg_color='#69a3d6', text_color="black", 
                                    font=("Arial", 12, "bold"), command=lambda: self.cancelar_cerrar())
        boton_cerrar.grid(row=0, column=1, padx=5, pady=(200,0), sticky="sw")

if __name__ == "__main__":
    app = VentanaPrincipal()
    app.mainloop()