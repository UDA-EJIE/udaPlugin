from customtkinter import *
import interfazTkinter as paso1
import ventanaPaso2Buena as paso2
import ventanaPaso3 as paso3

class MainMenu(CTk):
    def __init__(self):
        super().__init__()

        self.title("Menú Principal")
        self.geometry("300x200")  # Configura el tamaño de la ventana
        self.configure(bg_color="#2E3B55")

        # Botón para Paso 1
        self.button_paso_1 = CTkButton(self, text="Paso 1", command=self.abrir_paso1)
        self.button_paso_1.pack(pady=10, padx=20, fill="x")

        # Botón para Paso 2
        self.button_paso_2 = CTkButton(self, text="Paso 2", command=self.abrir_paso2)
        self.button_paso_2.pack(pady=10, padx=20, fill="x")

        # Botón para Paso 3
        self.button_paso_3 = CTkButton(self, text="Paso 3", command=self.abrir_paso3)
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