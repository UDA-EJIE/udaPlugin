import customtkinter as ctk
import tkinter as tk
from customtkinter import *
import plugin.utils as utl
from pathlib import Path

class VentanaPaso6(CTk):
    def __init__(self):
        super().__init__()
        # Configurar la ventana principal
        self.title("Crear nueva aplicación")
        self.geometry("900x700")
        self.resizable(width=False, height=False)
        self.config(bg="#FFFFFF")

        self.columnconfigure(1, weight=1)

        configuration_frame = CTkFrame(self, bg_color="#FFFFFF")
        configuration_frame.grid(row=0, column=0, columnspan=3, sticky="ew")

        configuration_label = CTkLabel(configuration_frame, text="Generar EJB Cliente", font=("Arial", 14, "bold"))
        configuration_label.grid(row=0, column=0, columnspan=3, pady=(10, 5), padx=10, sticky="w")

        description_label = CTkLabel(configuration_frame, text="Este Wizard el EJB Cliente de un servicio existente")
        description_label.grid(row=1, column=0, columnspan=3, pady=(5, 5), padx=10, sticky="w")

        ejb_container_label = CTkLabel(self, text="Proyecto EJB contenedor:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_container_label.grid(row=2, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        ejb_container_entry = CTkEntry(self, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        ejb_container_entry.grid(row=2, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")
        ejb_container_button = CTkButton(self, text="Buscar Proyecto", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        ejb_container_button.grid(row=2, column=2, sticky="e", padx=(5, 10))

        ejb_remote_type_label = CTkLabel(self, text="Tipo de EJB Remoto:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_remote_type_label.grid(row=3, column=0, sticky="w", padx=(10, 5), pady=(10, 2))

        ejb_remote_type_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color="#FFFFFF")
        ejb_remote_type_frame.grid(row=3, column=1, pady=5, padx=(10, 5), sticky="w")

        ejb_remote_type_var = tk.StringVar(value="EJB 3.0")
        ejb3_rb = CTkRadioButton(ejb_remote_type_frame, text="EJB 3.0", variable=ejb_remote_type_var, value="EJB 3.0", text_color="black", fg_color='#84bfc4', radiobutton_height=18, radiobutton_width=18, bg_color='#FFFFFF')
        ejb3_rb.grid(row=0, column=0, padx=5)
        ejb2_rb = CTkRadioButton(ejb_remote_type_frame, text="EJB 2.0", variable=ejb_remote_type_var, value="EJB 2.0", text_color="black", fg_color='#84bfc4', radiobutton_height=18, radiobutton_width=18, bg_color='#FFFFFF')
        ejb2_rb.grid(row=0, column=1, padx=5)

        ejb_interface_label = CTkLabel(self, text="Interface del EJB Remoto:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ejb_interface_label.grid(row=4, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        ejb_interface_entry = CTkEntry(self, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        ejb_interface_entry.grid(row=4, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")
        ejb_interface_button = CTkButton(self, text="Buscar Interface", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        ejb_interface_button.grid(row=4, column=2, sticky="e", padx=(5, 10))

        deployment_server_label = CTkLabel(self, text="Parámetros Servidor Despliegue", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        deployment_server_label.grid(row=5, column=0, columnspan=3, pady=5)

        deployment_server_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color="#FFFFFF")
        deployment_server_frame.grid(row=6, column=0, columnspan=3, pady=5, padx=(10, 5), sticky="ew")

        ip_label = CTkLabel(deployment_server_frame, text="IP Servidor:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        ip_label.grid(row=0, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        ip_entry = CTkEntry(deployment_server_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        ip_entry.grid(row=0, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")

        port_label = CTkLabel(deployment_server_frame, text="Puerto:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        port_label.grid(row=0, column=2, sticky="w", padx=(10, 5), pady=(10, 2))
        port_entry = CTkEntry(deployment_server_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        port_entry.grid(row=0, column=3, padx=(10, 5), pady=(10, 2), sticky="ew")

        remote_ejb_label = CTkLabel(self, text="Parámetros EJB remoto", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        remote_ejb_label.grid(row=7, column=0, columnspan=3, pady=5)

        remote_ejb_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color="#FFFFFF")
        remote_ejb_frame.grid(row=8, column=0, columnspan=3, pady=5, padx=(10, 5), sticky="ew")

        remote_ejb_name_label = CTkLabel(remote_ejb_frame, text="Nombre Servidor EJB:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        remote_ejb_name_label.grid(row=0, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        remote_ejb_name_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        remote_ejb_name_entry.grid(row=0, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")

        remote_ip_label = CTkLabel(remote_ejb_frame, text="Dirección IP:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        remote_ip_label.grid(row=1, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        remote_ip_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        remote_ip_entry.grid(row=1, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")

        remote_port_label = CTkLabel(remote_ejb_frame, text="Puerto:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        remote_port_label.grid(row=1, column=2, sticky="w", padx=(10, 5), pady=(10, 2))
        remote_port_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        remote_port_entry.grid(row=1, column=3, padx=(10, 5), pady=(10, 2), sticky="ew")

        user_label = CTkLabel(remote_ejb_frame, text="Usuario:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        user_label.grid(row=2, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        user_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        user_entry.grid(row=2, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")

        password_label = CTkLabel(remote_ejb_frame, text="Password:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        password_label.grid(row=2, column=2, sticky="w", padx=(10, 5), pady=(10, 2))
        password_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, show="*", text_color="black")
        password_entry.grid(row=2, column=3, padx=(10, 5), pady=(10, 2), sticky="ew")

        jndi_label = CTkLabel(remote_ejb_frame, text="Nombre JNDI:", bg_color='#FFFFFF', fg_color="#FFFFFF", text_color="black", font=("Arial", 12, "bold"))
        jndi_label.grid(row=3, column=0, sticky="w", padx=(10, 5), pady=(10, 2))
        jndi_entry = CTkEntry(remote_ejb_frame, bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', height=2.5, border_width=3, text_color="black")
        jndi_entry.grid(row=3, column=1, padx=(10, 5), pady=(10, 2), sticky="ew")

        buttons_frame = CTkFrame(self, bg_color='#FFFFFF', fg_color="#FFFFFF")
        buttons_frame.grid(row=15, column=0, columnspan=3, pady=10)

        back_button = CTkButton(buttons_frame, text="Back", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        back_button.grid(row=0, column=0, padx=5)
        next_button = CTkButton(buttons_frame, text="Next", state="disabled", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        next_button.grid(row=0, column=1, padx=5)
        finish_button = CTkButton(buttons_frame, text="Finish", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        finish_button.grid(row=0, column=2, padx=5)
        cancel_button = CTkButton(buttons_frame, text="Cancel", bg_color='#FFFFFF', fg_color='#84bfc4', border_color='#84bfc4', hover_color='#41848a', text_color="black", font=("Arial", 12, "bold"))
        cancel_button.grid(row=0, column=3, padx=5)

if __name__ == '__main__':
    app = VentanaPaso6()
    app.mainloop()
