import customtkinter as ctk
from customtkinter import *
import tkinter as tk

class VentanaPaso7(CTk):
    def __init__(self):
        super().__init__()
        # Configurar la ventana principal
        self.title("Generar EJB")
        self.geometry("900x700")
        self.resizable(width=False, height=False)
        self.config(bg="#FFFFFF")

        self.columnconfigure(1, weight=1)

        configuration_frame = CTkFrame(self, bg_color="#FFFFFF", fg_color="#FFFFFF")
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")

        configuration_label = CTkLabel(configuration_frame, text="Generar EJB", font=("Arial", 14, "bold"), bg_color="#FFFFFF", fg_color="#FFFFFF")
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(10, 5), padx=10, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard el EJB de un servicio existente", bg_color="#FFFFFF", fg_color="#FFFFFF")
        description_label.grid(row=1, column=0, columnspan=3, pady=(5, 5), padx=10, sticky="w")

        ejb_container_label = CTkLabel(self, text="Proyecto EJB contenedor:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_container_label.grid(row=2, column=0, sticky="w", pady=5, padx=10)
        ejb_container_entry = CTkEntry(self, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=25, border_width=3, text_color="black")
        ejb_container_entry.grid(row=2, column=1, pady=5, padx=10, sticky="w")
        ejb_container_button = CTkButton(self, text="Buscar Proyecto", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        ejb_container_button.grid(row=2, column=2, sticky="e", padx=10)

        service_label = CTkLabel(self, text="Servicio:", bg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        service_label.grid(row=3, column=0, sticky="w", pady=5, padx=10)
        service_entry = CTkEntry(self, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=25, border_width=3, text_color="black")
        service_entry.grid(row=3, column=1, pady=5, padx=10, sticky="w")
        service_button = CTkButton(self, text="Buscar Servicio", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        service_button.grid(row=3, column=2, sticky="e", padx=10)

        jndi_label = CTkLabel(self, text="Nombre JNDI:", bg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        jndi_label.grid(row=4, column=0, sticky="w", pady=5, padx=10)
        jndi_entry = CTkEntry(self, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=25, border_width=3, text_color="black")
        jndi_entry.grid(row=4, column=1, pady=5, padx=10, sticky="w")

        ejb_name_label = CTkLabel(self, text="Nombre del EJB:", bg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_name_label.grid(row=5, column=0, sticky="w", pady=5, padx=10)
        ejb_name_entry = CTkEntry(self, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=25, border_width=3, text_color="black")
        ejb_name_entry.grid(row=5, column=1, pady=5, padx=10, sticky="w")

        buttons_frame = CTkFrame(self, bg_color="#FFFFFF", fg_color="#FFFFFF")
        buttons_frame.grid(row=6, column=0, columnspan=3, pady=10)

        back_button = CTkButton(buttons_frame, text="Back", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        back_button.grid(row=0, column=0, padx=5)
        next_button = CTkButton(buttons_frame, text="Next", state="disabled", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        next_button.grid(row=0, column=1, padx=5)
        finish_button = CTkButton(buttons_frame, text="Finish", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        finish_button.grid(row=0, column=2, padx=5)
        cancel_button = CTkButton(buttons_frame, text="Cancel", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        cancel_button.grid(row=0, column=3, padx=5)

if __name__ == '__main__':
    app = VentanaPaso7()
    app.mainloop()
