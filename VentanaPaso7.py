import customtkinter as ctk
from customtkinter import *
import tkinter as tk
import plugin.utils as utl
from pathlib import Path
import re
import logging
from datetime import datetime
from copier import *
import plugin.utilsPaso6 as utlPaso6


ruta_classes = utl.readConfig("RUTA", "ruta_classes")

class VentanaPaso7(CTk):
    def __init__(self, main_menu):
        super().__init__()
        # Configurar la ventana principal
        self.title("Generar EJB")
        self.geometry("900x700")
        self.resizable(width=False, height=False)
        self.config(bg="#FFFFFF")

        self.columnconfigure(1, weight=1)
        self.rowconfigure(2, weight= 1)

        self.main_menu = main_menu

        configuration_frame = CTkFrame(self, bg_color="#FFFFFF")
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")

        configuration_label = CTkLabel(configuration_frame, text="Generar EJB", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(10, 5), padx=10, sticky="w")

        self.configuration_warning = CTkLabel(configuration_frame,  text="", font=("Arial", 13, "bold"),text_color="red")
        self.configuration_warning.grid(row=0, column=4, columnspan=3, pady=(20, 5), padx=20, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard genera el EJB de un servicio existente")
        description_label.grid(row=1, column=0, columnspan=3, pady=(5, 5), padx=10, sticky="w")


        frame_ejb = CTkFrame(self, fg_color="#FFFFFF")
        frame_ejb.grid(row=1, column=0, columnspan=3, sticky="ew", pady =(20, 20), padx = (15, 15))


        rutaActual = utl.rutaActual(__file__)
        textRutaNegocio = rutaActual
        textRutaControlador = rutaActual
        ruta_EJB = utl.readConfig("RUTA", "ruta_classes")
 
        if(ruta_EJB != None and ruta_EJB != ""):
           textRutaNegocio = ruta_EJB 
        archivoEJB = utl.buscarArchivo(textRutaNegocio,"EJB") 
  
        if(archivoEJB != '' ):
           textRutaNegocio = textRutaNegocio+"\\"+archivoEJB 
        else:
            textRutaNegocio = ""


        ejb_container_label = CTkLabel(frame_ejb, text="Proyecto EJB contenedor:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_container_label.grid(row=0, column=0, sticky="w", pady=5, padx=10)
        self.ejb_container_entry = CTkEntry(frame_ejb, bg_color='#599398', fg_color='#599398', border_color='#599398',width= 550, height=25, border_width=3, text_color="black")
        self.ejb_container_entry.grid(row=0, column=1, pady=5, padx=10, sticky="w")
        self.ejb_container_entry.configure(state="disabled")

        self.ejb_container_entry.insert(0, textRutaNegocio)
        ejb_container_button = CTkButton(frame_ejb, text="Buscar Proyecto",  command= lambda : self.buscar_archivos(self.selectDirectory(self.ejb_container_entry.get())), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold") , width= 100, height=23)
        ejb_container_button.grid(row=0, column=2, sticky="e", padx=10)

        service_label = CTkLabel(frame_ejb, text="Servicio:", bg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        service_label.grid(row=1, column=0, sticky="w", pady=5, padx=10)
        self.service_entry = CTkEntry(frame_ejb, bg_color='#599398', fg_color='#599398', border_color='#599398', width= 550, height=25, border_width=3, text_color="black")
        self.service_entry.grid(row=1, column=1, pady=5, padx=10, sticky="w")
        self.service_entry.configure(state="disabled")
        service_button = CTkButton(frame_ejb, text="Buscar Servicio", command= lambda :  self.findServices(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"), width= 100, height=23)
        service_button.grid(row=1, column=2, sticky="e", padx=10)

        jndi_label = CTkLabel(frame_ejb, text="Nombre JNDI:", bg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        jndi_label.grid(row=2, column=0, sticky="w", pady=5, padx=10)
        self.jndi_entry = CTkEntry(frame_ejb, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',width= 550, height=25, border_width=3, text_color="black")
        self.jndi_entry.grid(row=2, column=1, pady=5, padx=10, sticky="w")

        ejb_name_label = CTkLabel(frame_ejb, text="Nombre del EJB:", bg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_name_label.grid(row=3, column=0, sticky="w", pady=5, padx=10)
        self.ejb_name_entry = CTkEntry(frame_ejb, bg_color='#599398', fg_color='#599398', border_color='#599398', width= 550, height=25, border_width=3, text_color="black")
        self.ejb_name_entry.grid(row=3, column=1, pady=5, padx=10, sticky="w")
        self.ejb_name_entry.configure(state="disabled")

        buttons_frame = CTkFrame(self, bg_color="#FFFFFF", fg_color="#FFFFFF")
        buttons_frame.grid(row=2, column=0, columnspan=3, pady= (300, 0))

        back_button = CTkButton(buttons_frame, text="Atrás", command= lambda : self.cancelar(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        back_button.grid(row=0, column=0, padx=(280, 5))
        finish_button = CTkButton(buttons_frame, text="Finalizar", command= lambda : self.save_to_yaml(), bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        finish_button.grid(row=0, column=1, padx=5)
      

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
                files = [file for file in os.listdir(ruta_classes) if file.endswith("EJB")]
            except:
                print("No encontro la ruta: " + ruta_classes)
            self.mostrar_resultados(files, ruta_classes)
        else:
            try:
                files = [file for file in os.listdir(ruta_personalizada) if file.endswith("EJB")]
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
        desc_label = ctk.CTkLabel(scrollbar_resumen, text="Seleccione un EJB", text_color="black")
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
            texto = "Esta ruta no contiene ningún EJB"  
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
            desc_label = CTkLabel(file_frame, text="Seleccione un EJB ", text_color= "black")
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
    
                texto = "Esta ruta no contiene ningún EJB"  
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
            self.ejb_container_entry.configure(state="normal")
            self.ejb_container_entry.delete(0, "end")
            self.ejb_container_entry.insert(0, ruta+"/"+selected_file)
            self.ejb_container_entry.configure(state="disabled")
            self.ejbEntryRoute = ruta
            self.archivoClases = selected_file
            frame.destroy()

        else:
            print("No se seleccionó ningún archivo.")


    def findServices(self):
        if (self.ejb_container_entry.get() == None or self.ejb_container_entry.get() == ""):
            self.configuration_warning.configure(text="El nombre del contenedor EJB es obligatorio")
            self.configuration_warning.configure(text_color ="red")
            return FALSE
        # Crear la ventana secundaria
        resultados_services = ctk.CTkToplevel(self)
        resultados_services.title("Resultados de Búsqueda")
        resultados_services.geometry("600x400")
        resultados_services.configure(corner_radius=10, fg_color="#FFFFFF", border_color="#84bfc4", border_width=4)

        resultados_services.attributes('-topmost', True)

        # Configurar las columnas y filas
        resultados_services.grid_columnconfigure(0, weight=1)
        resultados_services.grid_columnconfigure(1, weight=1)
        resultados_services.grid_rowconfigure(2, weight=1)

        # Label para el título de la ventana
        title_label = ctk.CTkLabel(resultados_services, text="Service que se desea vincular al proyecto EJB", font=("Arial", 12, "bold"), bg_color="#FFFFFF")
        title_label.grid(row=0, column=0, columnspan=2, pady=20, padx=10, sticky="n")

        # Entry para búsqueda de servicios
        search_entry = ctk.CTkEntry(resultados_services, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=25, border_width=3, text_color="black", width=400)
        search_entry.grid(row=1, column=0, columnspan=2, pady=10, padx=20, sticky="ew")

        # Crear un CTkScrollableFrame para contener la lista de servicios
        self.scrollable_frame = ctk.CTkScrollableFrame(resultados_services, width=560, height=200, bg_color="#FFFFFF", fg_color="#FFFFFF")
        self.scrollable_frame.grid(row=2, column=0, columnspan=2, pady=10, padx=20, sticky="nsew")

        # Insertar elementos en el CTkScrollableFrame y permitir selección
        self.selected_label = None

        services = self.get_patch_services(self.ejb_container_entry.get())

        for service in services:
            self.selected_button = ctk.CTkButton(self.scrollable_frame, text= service, font=("Arial", 12), bg_color="#FFFFFF", fg_color="#FFFFFF", hover_color="#E0E0E0", text_color="black", command=lambda b=service: self.on_button_click(resultados_services, b))
            self.selected_button.grid(sticky="ew", padx=10, pady=5)


    def on_button_click(self, resultados_services,  service):
        self.service_seleccionado = service.split("\\")[len(service.split("\\")) -1 ]
        print(f"Selected Service: {service}")
        # Aquí puedes realizar cualquier acción adicional cuando se selecciona un servicio
        # Por ejemplo, almacenar el servicio seleccionado o actualizar la interfaz
        self.service_entry.configure(state="normal")
        self.ejb_name_entry.configure(state="normal")
        self.service_entry.insert(0 ,service)  # Guardar el servicio seleccionado
        nombre_EJB = service.split("\\")[len(service.split("\\")) -1].split(".")[0] + "Skeleton"
        self.ejb_name_entry.insert(0, nombre_EJB)
        resultados_services.withdraw()
        self.service_entry.configure(state="disabled")
        self.ejb_name_entry.configure(state="disabled")

        return self.service_seleccionado 


    def get_patch_services(self, ruta_personalizada):

        nameEar = utlPaso6.obtener_con_pathEar(ruta_personalizada+"/.classpath")  
        self.parte  =  nameEar.replace("EAR","") 
        if self.ejbEntryRoute == None or self.ejbEntryRoute == "":
            indice = self.ejb_container_entry.get().rfind('/')
            self.ejbEntryRoute = self.ejb_container_entry.get()[:indice]

        self.ruta_services = self.ejbEntryRoute +"\\"+ self.parte +"EARClasses\\src\\com\\ejie\\" + self.parte +"\\service\\"

        java_files = []

        for root, dirs, files in os.walk(self.ruta_services):
            for file in files:
                if file.endswith(".java") and not file.endswith("Impl.java"):
                    java_files.append("com\\ejie\\" + self.parte +"\\service\\"+file)
        return java_files


    def find_impl_file(self, selected_file, directory):
        # Asumimos que el archivo "Impl" tiene el mismo nombre base con "Impl" añadido antes de ".java"
        base_name = selected_file.replace(".java", "")
        impl_file_name = f"{base_name}.java"

        for root, dirs, files in os.walk(directory):
            if impl_file_name in files:
                return os.path.join(root, impl_file_name)
        
        return None

    def extract_methods_from_impl(self, impl_file_path):
        methods = []
        # Regex para capturar el tipo de retorno, nombre del método y parámetros
        method_pattern = re.compile(r'^\s*([^\s]+)\s+([^\s(]+)\s*\(([^)]*)\)\s*;')

        with open(impl_file_path, 'r',  encoding='cp65001') as file:
            while True:
                line = file.readline()

                match = method_pattern.search(line)
                if match:
                    return_type = match.group(1)
                    method_name = match.group(2)
                    parameters = match.group(3).strip()

                    # Formatear los parámetros para la salida final
                    if parameters:
                        param_list = [param.strip().rsplit(' ', 1)[0] for param in parameters.split(',')]
                        param_types = ', '.join(param_list)
                    else:
                        param_types = ''
                    
                    # Construir la firma completa del método
                    method_signature = f'{return_type} {method_name}({param_types});'
                    
                    # Agregar el resultado a la lista
                    methods.append(method_signature)
                if not line:
                    break
        
        return methods
    
    def save_to_yaml(self):   

        if (self.ejb_container_entry.get() == None or self.ejb_container_entry.get() == ""):
            self.configuration_warning.configure(text="El nombre del contenedor EJB es obligatorio")
            self.configuration_warning.configure(text_color ="red")
            return FALSE  
        if (self.service_entry.get() == None or self.service_entry.get() == ""):
            self.configuration_warning.configure(text="El nombre del servicio es obligatorio")
            self.configuration_warning.configure(text_color ="red")
            return FALSE
        if (self.jndi_entry.get() == None or self.jndi_entry.get() == ""):
            self.configuration_warning.configure(text="El nombre del JNDI es obligatorio")
            self.configuration_warning.configure(text_color ="red")
            return FALSE
        if (self.ejb_name_entry.get() == None or self.ejb_name_entry.get() == ""):
            self.configuration_warning.configure(text="El nombre del EJB es obligatorio")
            self.configuration_warning.configure(text_color ="red")
            return FALSE

        prueba = self.find_impl_file(self.service_seleccionado, self.ruta_services)

        metodos = self.extract_methods_from_impl(prueba)

        inicio = datetime.now()

        self.ruta_destino = self.ejb_container_entry.get() + "\\\ejbModule\\com\\ejie\\" + self.parte + "\\remoting"
        self.valorEjb = self.ejb_name_entry.get()
        serviceName = self.ejb_name_entry.get().replace("Skeleton","")
        yaml_data = {
            "jndiName": self.ejb_name_entry.get(),
            "serviceName": serviceName,
            "metodos" : metodos
        }

        rutaPath = utl.rutaActual(__file__)
        directorio_actual = rutaPath + "\\templates\\proyectoPaso7"
        filesExcludes = []

        #destinoPath = self.entry_location.get()
        #if(destinoPath == ''):
        destinoPath = rutaPath
        now = datetime.now()
        dates = now.strftime('%d-%b-%Y %H:%M:%S') 
        print('Inicio: Creando servicio... ' +yaml_data["serviceName"])    
        with Worker(src_path=directorio_actual,overwrite=True, dst_path=self.ruta_destino, data=yaml_data,exclude=filesExcludes) as worker:

            logging.info('Inicio: Crear servicio: ' + yaml_data["serviceName"])
            worker.template.version = ": 1.0 Paso 1 ::: "+dates
            worker.jinja_env.globals['extract_java_types'] = utlPaso6.extract_java_types
            worker.jinja_env.filters["fistLetterMin"] = utl.fistLetterMin
            worker.run_copy()
            logging.info('Fin: Crear sercivio: ' + yaml_data["serviceName"])
            #guardar ultima ruta creada5
            utl.writeConfig(
                "RUTA", {"ruta_classes":destinoPath,"ruta_war":destinoPath,"ruta_ultimo_proyecto":destinoPath})
        print('Fin: proyecto servicio: ' + yaml_data["serviceName"])
        fin = datetime.now()
        logging.info('Tiempo: proyecto Creado en: ' + str((fin-inicio).total_seconds()) + " segundos")
        now = datetime.now()
        dates = now.strftime('%d-%b-%Y %H:%M:%S')
        print(F"Final: paso 1 creado ::: "+dates,file=sys.stderr)
        sys.stderr.flush()
    
        self.ventana_final_popup() 
    

    def ventana_final_popup(self):
        # Guardar los valores de los widgets de entrada

        # Destruir todos los widgets hijos del frame actual
        for widget in self.winfo_children():
            widget.destroy()

        # Crear un nuevo frame que ocupe toda la ventana
        frame_final = CTkFrame(self, bg_color="#FFFFFF", fg_color="#FFFFFF")
        frame_final.pack(fill="both", expand=True)

        # Frame interno centrado
        frame_center = CTkFrame(frame_final, bg_color="#FFFFFF", fg_color="#FFFFFF")
        frame_center.pack(expand=True)


        nombre_label = CTkLabel(frame_center, text="Has creado el siguiente servicio EJB: " , fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        nombre_label.pack(pady=(0, 0), padx=30)
        
        nombre_proyecto_label = CTkLabel(frame_center, text= self.valorEjb, fg_color="#FFFFFF", text_color="black", font=("Arial", 14, "bold"))
        nombre_proyecto_label.pack(pady=(0, 0), padx=30)

        ruta_label = CTkLabel(frame_center, text="Has guardado el proyecto en la ruta ", fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ruta_label.pack(pady=10, padx=30)

        ruta_label = CTkLabel(frame_center, text=self.ruta_destino, fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
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


    def cerrar_ventana(self):
        # Cancela todos los eventos pendientes, y cierrar la ventana
        self.withdraw()
        sys.exit(0)

    def cancelar(self):
        # Cancela todos los eventos pendientes
        self.withdraw()
        self.quit()
        self.main_menu.MainMenuLoop()

if __name__ == '__main__':
    app = VentanaPaso7()
    app.mainloop()
