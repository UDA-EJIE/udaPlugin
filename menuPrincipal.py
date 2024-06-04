from customtkinter import *
import interfazTkinter as paso1
import ventanaPaso2Buena as paso2
import ventanaPaso3 as paso3
from tkinter import *
from PIL import Image, ImageTk
import logging

base_path = os.path.dirname(os.path.abspath(__file__))
logsPath = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'logs\\log.log')
sys.stderr = open(logsPath, 'a')
logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG,encoding='utf-8',filename='logs/info.log')

class MainMenu(CTkToplevel):
    def __init__(self):
        super().__init__()

        self.title("Menú Principal")
        self.geometry("600x600")  # Configura el tamaño de la ventana
        self.config(bg="#E0E0E0")

        # Load the image
        image_path = base_path+'\\plugin\\images\\logo_uda.png'
        logo_ejie = CTkImage(light_image=Image.open(image_path))
        label_logo = CTkLabel(self, text="Plugin UDA", image=logo_ejie, bg_color='#E0E0E0')
        label_logo.pack(pady=(20, 10), padx=20, fill="x")

        
        # Botón para Paso 1
        self.button_paso_1 = CTkButton(self, text="Paso 1", command=self.abrir_paso1, bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        self.button_paso_1.pack(pady=(20, 10), padx=20, fill="x")

        # Botón para Paso 2
        self.button_paso_2 = CTkButton(self, text="Paso 2", command=self.abrir_paso2, bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        self.button_paso_2.pack(pady=10, padx=20, fill="x")

        # Botón para Paso 3
        self.button_paso_3 = CTkButton(self, text="Paso 3", command=self.abrir_paso3, bg_color='#E0E0E0', fg_color='#69a3d6', border_color='#69a3d6', text_color="black", font=("Arial", 12, "bold"), width= 100, height=25)
        self.button_paso_3.pack(pady=10, padx=20, fill="x")

    def on_close(self,ventana):
        ventana.destroy()  # Destruye la ventana del paso
        self.deiconify()   # Muestra el menú principal

    def abrir_paso1(self):
        self.withdraw()  # Oculta el menú principal
        ventana = paso1.Paso1()
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))  # Configura el callback
        ventana.mainloop()

    def abrir_paso2(self):
        self.withdraw()
        ventana = paso2.VentanaPrincipal()
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))
        ventana.mainloop()

    def abrir_paso3(self):
        self.withdraw()
        ventana = paso3.VentanaPrincipal()
        ventana.protocol("WM_DELETE_WINDOW", lambda: self.on_close(ventana))
        ventana.ocultarSpinner()
        ventana.mainloop()



def MainMenuLoop(self):   
    app = MainMenu()
    self.destroy()
    app.mainloop()
    

if __name__ == "__main__":
    MainMenu().mainloop()