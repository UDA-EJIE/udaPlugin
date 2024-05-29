import customtkinter as ctk
import tkinter as tk



# Create the main window
root2 = ctk.CTk()
root2.title("Generar EJB Cliente")
root2.geometry("500x600")

def create_widgets_2():
    # Title label
    title_label = ctk.CTkLabel(root2, text="Generar EJB Cliente", font=ctk.CTkFont(size=16, weight="bold"))
    title_label.grid(row=0, column=0, columnspan=2, pady=10)

    # Subtitle label
    subtitle_label = ctk.CTkLabel(root2, text="Este Wizard el EJB Cliente de un servicio existente")
    subtitle_label.grid(row=1, column=0, columnspan=2)

    # EJB project container
    ejb_container_label = ctk.CTkLabel(root2, text="Proyecto EJB contenedor:")
    ejb_container_label.grid(row=2, column=0, sticky="w", pady=5)
    ejb_container_entry = ctk.CTkEntry(root2, width=250)
    ejb_container_entry.grid(row=2, column=1, pady=5, padx=10, sticky="w")
    ejb_container_button = ctk.CTkButton(root2, text="Buscar Proyecto")
    ejb_container_button.grid(row=2, column=1, sticky="e", padx=10)

    # EJB Remote Type
    ejb_remote_type_label = ctk.CTkLabel(root2, text="Tipo de EJB Remoto:")
    ejb_remote_type_label.grid(row=3, column=0, sticky="w", pady=5)
    
    ejb_remote_type_frame = ctk.CTkFrame(root2)
    ejb_remote_type_frame.grid(row=3, column=1, pady=5, padx=10, sticky="w")

    ejb_remote_type_var = tk.StringVar(value="EJB 3.0")
    ejb3_rb = ctk.CTkRadioButton(ejb_remote_type_frame, text="EJB 3.0", variable=ejb_remote_type_var, value="EJB 3.0")
    ejb3_rb.grid(row=0, column=0, padx=5)
    ejb2_rb = ctk.CTkRadioButton(ejb_remote_type_frame, text="EJB 2.0", variable=ejb_remote_type_var, value="EJB 2.0")
    ejb2_rb.grid(row=0, column=1, padx=5)

    # EJB Remote Interface
    ejb_interface_label = ctk.CTkLabel(root2, text="Interface del EJB Remoto:")
    ejb_interface_label.grid(row=4, column=0, sticky="w", pady=5)
    ejb_interface_entry = ctk.CTkEntry(root2, width=250)
    ejb_interface_entry.grid(row=4, column=1, pady=5, padx=10, sticky="w")
    ejb_interface_button = ctk.CTkButton(root2, text="Buscar Interface")
    ejb_interface_button.grid(row=4, column=1, sticky="e", padx=10)

    # Deployment Server Parameters
    deployment_server_label = ctk.CTkLabel(root2, text="Parámetros Servidor Despliegue")
    deployment_server_label.grid(row=5, column=0, columnspan=2, pady=10)

    ip_label = ctk.CTkLabel(root2, text="IP Servidor:")
    ip_label.grid(row=6, column=0, sticky="w", pady=5)
    ip_entry = ctk.CTkEntry(root2, width=250)
    ip_entry.grid(row=6, column=1, pady=5, padx=10, sticky="w")

    port_label = ctk.CTkLabel(root2, text="Puerto:")
    port_label.grid(row=7, column=0, sticky="w", pady=5)
    port_entry = ctk.CTkEntry(root2, width=250)
    port_entry.grid(row=7, column=1, pady=5, padx=10, sticky="w")

    # Remote EJB Parameters
    remote_ejb_label = ctk.CTkLabel(root2, text="Parámetros EJB remoto")
    remote_ejb_label.grid(row=8, column=0, columnspan=2, pady=10)

    remote_ejb_name_label = ctk.CTkLabel(root2, text="Nombre Servidor EJB:")
    remote_ejb_name_label.grid(row=9, column=0, sticky="w", pady=5)
    remote_ejb_name_entry = ctk.CTkEntry(root2, width=250)
    remote_ejb_name_entry.grid(row=9, column=1, pady=5, padx=10, sticky="w")

    remote_ip_label = ctk.CTkLabel(root2, text="Dirección IP:")
    remote_ip_label.grid(row=10, column=0, sticky="w", pady=5)
    remote_ip_entry = ctk.CTkEntry(root2, width=250)
    remote_ip_entry.grid(row=10, column=1, pady=5, padx=10, sticky="w")

    remote_port_label = ctk.CTkLabel(root2, text="Puerto:")
    remote_port_label.grid(row=11, column=0, sticky="w", pady=5)
    remote_port_entry = ctk.CTkEntry(root2, width=250)
    remote_port_entry.grid(row=11, column=1, pady=5, padx=10, sticky="w")

    user_label = ctk.CTkLabel(root2, text="Usuario:")
    user_label.grid(row=12, column=0, sticky="w", pady=5)
    user_entry = ctk.CTkEntry(root2, width=250)
    user_entry.grid(row=12, column=1, pady=5, padx=10, sticky="w")

    password_label = ctk.CTkLabel(root2, text="Password:")
    password_label.grid(row=13, column=0, sticky="w", pady=5)
    password_entry = ctk.CTkEntry(root2, width=250, show="*")
    password_entry.grid(row=13, column=1, pady=5, padx=10, sticky="w")

    jndi_label = ctk.CTkLabel(root2, text="Nombre JNDI:")
    jndi_label.grid(row=14, column=0, sticky="w", pady=5)
    jndi_entry = ctk.CTkEntry(root2, width=250)
    jndi_entry.grid(row=14, column=1, pady=5, padx=10, sticky="w")

    # Buttons
    buttons_frame = ctk.CTkFrame(root2)
    buttons_frame.grid(row=15, column=0, columnspan=2, pady=10)
    
    back_button = ctk.CTkButton(buttons_frame, text="Back")
    back_button.grid(row=0, column=0, padx=5)
    next_button = ctk.CTkButton(buttons_frame, text="Next", state="disabled")
    next_button.grid(row=0, column=1, padx=5)
    finish_button = ctk.CTkButton(buttons_frame, text="Finish")
    finish_button.grid(row=0, column=2, padx=5)
    cancel_button = ctk.CTkButton(buttons_frame, text="Cancel")
    cancel_button.grid(row=0, column=3, padx=5)

create_widgets_2()
root2.mainloop()
