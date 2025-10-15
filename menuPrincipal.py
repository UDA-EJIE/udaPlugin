import plugin.utils as utl
from plugin.git_patch import patch_git_for_copier
from customtkinter import *
import VentanaPaso1 as paso1
import ventanaPaso2 as paso2
import ventanaPaso3 as paso3
import VentanaPaso4 as paso4
import VentanaPaso5 as paso5
import VentanaPaso6 as paso6
import VentanaPaso7 as paso7
from tkinter import *
from PIL import Image, ImageTk
import logging
import sys
import os
from plugin.utils import VERSION_STR 
#from simple_git_test import ejecutar_test_git


base_path = os.path.dirname(os.path.abspath(__file__))
logsPath = os.path.join(base_path, 'logs\\log.log')
logsInfoPath = os.path.join(base_path, 'logs\\info.log')

logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG,encoding='utf-8',filename=logsInfoPath)

sys.stderr = open(logsPath, 'a')

class MainMenu(CTkToplevel):
    def __init__(self):
        super().__init__()
        utl.setup_embedded_git()
        #ejecutar_test_git()
        patch_git_for_copier()
       
        self.title("Menú Principal")
        self.geometry("900x700")   # Configura el tamaño de la ventana
        self.config(bg="#FFFFFF")
        self.resizable(width=False, height=False)
    
        # Configurar el grid de la ventana principal (self)
        # Se crean 2 filas: fila 0 para el contenido (que se expande) y fila 1 para el footer.
        self.grid_rowconfigure(0, weight=1)
        self.grid_rowconfigure(1, weight=0)
        self.grid_columnconfigure(0, weight=1)
        
        # =======================================
        # Frame de Contenido (logo y botones)
        # =======================================
        content_frame = CTkFrame(self, fg_color="#FFFFFF", bg_color="#FFFFFF")
        content_frame.grid(row=0, column=0, sticky="nsew", padx=20, pady=20)
        
        # Opcional: configurar columnas internas del content_frame (si es necesario)
        content_frame.grid_columnconfigure(0, weight=1)
        
        # Cargar la imagen del logo
        image_path = base_path + '\\plugin\\images\\logo_uda.png'
        logo_ejie = CTkImage(light_image=Image.open(image_path), size=(200, 200))
        label_logo = CTkLabel(content_frame, text="", image=logo_ejie, bg_color='#E0E0E0')
        label_logo.grid(row=0, column=0, pady=(20, 10), padx=20)
        
        
        self.button_paso_1 = CTkButton(content_frame,
            text="1. Crear una nueva aplicación",
            command=lambda: self.abrir_paso1(),
            bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', text_color="black",
            hover_color='#41848a', font=("Arial", 12, "bold"), width=400, height=25)
        self.button_paso_1.grid(row=1, column=0, pady=(20, 10), padx=20)

        # Botón para Paso 2
        self.button_paso_2 = CTkButton(content_frame,
            text="2. Generar código de negocio y control",
            command=lambda: self.abrir_paso2(),
            bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',
            hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"),
            width=400, height=25)
        self.button_paso_2.grid(row=2, column=0, pady=10, padx=20)

        # Botón para Paso 3
        self.button_paso_3 = CTkButton(content_frame,
            text="3. Generar mantenimiento",
            command=lambda: self.abrir_paso3(),
            bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',
            hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"),
            width=400, height=25)
        self.button_paso_3.grid(row=3, column=0, pady=10, padx=20)

        # Botón para Paso 4
        self.button_paso_4 = CTkButton(content_frame,
            text="4. Añadir proyecto WAR",
            command=lambda: self.abrir_paso4(),
            bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',
            hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"),
            width=400, height=25)
        self.button_paso_4.grid(row=4, column=0, pady=10, padx=20)

        # Botón para Paso 5
        self.button_paso_5 = CTkButton(content_frame,
            text="5. Añadir proyecto EJB",
            command=lambda: self.abrir_paso5(),
            bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',
            hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"),
            width=400, height=25)
        self.button_paso_5.grid(row=5, column=0, pady=10, padx=20)

        # Botón para Paso 6
        self.button_paso_6 = CTkButton(content_frame,
            text="6. Generar código para EJB cliente",
            command=lambda: self.abrir_paso6(),
            bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',
            hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"),
            width=400, height=25)
        self.button_paso_6.grid(row=6, column=0, pady=10, padx=20)

        # Botón para Paso 7
        self.button_paso_7 = CTkButton(content_frame,
            text="7. Generar código para EJB servidor",
            command=lambda: self.abrir_paso7(),
            bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4',
            hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"),
            width=400, height=25)
        self.button_paso_7.grid(row=7, column=0, pady=10, padx=20)
        
        #Frame para pie de pagina
        footer_frame = CTkFrame(self,fg_color="#FFFFFF", bg_color="#FFFFFF")
        footer_frame.grid(row=1, column=0, sticky="ew", padx=10, pady=(0,10))
        footer_frame.grid_columnconfigure(0, weight=1)
        footer_frame.grid_columnconfigure(1, weight=0)

        # Botón deshabilitado para mostrar la versión en el pie de página
        version_button = CTkButton(footer_frame, text=VERSION_STR,
            bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', text_color="black", font=("Arial", 12, "bold"),  hover_color='#84bfc4')
        version_button.grid(row=0, column=1, sticky="e")
    def on_close(self,ventana):
        ventana.destroy()  # Destruye la ventana del paso
        self.deiconify()   # Muestra el menú principal

    def abrir_paso1(self):
        self.withdraw()  # Oculta el menú principal
        ventana = paso1.Paso1(self)
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))  # Configura el callback
        ventana.mainloop()

    def abrir_paso2(self):
        self.withdraw()
        print("Abriendo Paso 2")
        sys.stderr = open(logsPath, 'a')
        ventana = paso2.VentanaPrincipal(self)
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))
        ventana.mainloop()

    def abrir_paso3(self):
        self.withdraw()
        sys.stderr = open(logsPath, 'a')
        ventana = paso3.VentanaPrincipal(self)
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))
        ventana.mainloop()

    def abrir_paso4(self):
        self.withdraw()
        sys.stderr = open(logsPath, 'a')
        ventana = paso4.Paso4(self)
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))
        ventana.mainloop()


    def abrir_paso5(self):
        self.withdraw()
        sys.stderr = open(logsPath, 'a')
        ventana = paso5.Paso5(self)
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))
        ventana.mainloop()

    def abrir_paso6(self):
        self.withdraw()
        sys.stderr = open(logsPath, 'a')
        ventana = paso6.VentanaPaso6(self)
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))
        ventana.mainloop()   

    def abrir_paso7(self):
        self.withdraw()
        sys.stderr = open(logsPath, 'a')
        ventana = paso7.VentanaPaso7(self)
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))
        ventana.mainloop()     

    def MainMenuLoop(self):   
       self.deiconify()



def close_win():
    sys.exit(0)    

if __name__ == "__main__":
    MainMenu().mainloop()     