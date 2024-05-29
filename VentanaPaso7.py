import customtkinter as ctk
import tkinter as tk


# Create the main window
root3 = ctk.CTk()
root3.title("Generar EJB")
root3.geometry("500x400")

def create_widgets_3():
    # Title label
    title_label = ctk.CTkLabel(root3, text="Generar EJB", font=ctk.CTkFont(size=16, weight="bold"))
    title_label.grid(row=0, column=0, columnspan=2, pady=10)

    # Subtitle label
    subtitle_label = ctk.CTkLabel(root3, text="Este Wizard el EJB de un servicio existente")
    subtitle_label.grid(row=1, column=0, columnspan=2)

    # EJB project container
    ejb_container_label = ctk.CTkLabel(root3, text="Proyecto EJB contenedor:")
    ejb_container_label.grid(row=2, column=0, sticky="w", pady=5)
    ejb_container_entry = ctk.CTkEntry(root3, width=250)
    ejb_container_entry.grid(row=2, column=1, pady=5, padx=10, sticky="w")
    ejb_container_button = ctk.CTkButton(root3, text="Buscar Proyecto")
    ejb_container_button.grid(row=2, column=1, sticky="e", padx=10)

    # Service
    service_label = ctk.CTkLabel(root3, text="Servicio:")
    service_label.grid(row=3, column=0, sticky="w", pady=5)
    service_entry = ctk.CTkEntry(root3, width=250)
    service_entry.grid(row=3, column=1, pady=5, padx=10, sticky="w")
    service_button = ctk.CTkButton(root3, text="Buscar Servicio")
    service_button.grid(row=3, column=1, sticky="e", padx=10)

    # JNDI Name
    jndi_label = ctk.CTkLabel(root3, text="Nombre JNDI:")
    jndi_label.grid(row=4, column=0, sticky="w", pady=5)
    jndi_entry = ctk.CTkEntry(root3, width=250)
    jndi_entry.grid(row=4, column=1, pady=5, padx=10, sticky="w")

    # EJB Name
    ejb_name_label = ctk.CTkLabel(root3, text="Nombre del EJB:")
    ejb_name_label.grid(row=5, column=0, sticky="w", pady=5)
    ejb_name_entry = ctk.CTkEntry(root3, width=250)
    ejb_name_entry.grid(row=5, column=1, pady=5, padx=10, sticky="w")

    # Buttons
    buttons_frame = ctk.CTkFrame(root3)
    buttons_frame.grid(row=6, column=0, columnspan=2, pady=10)
    
    back_button = ctk.CTkButton(buttons_frame, text="Back")
    back_button.grid(row=0, column=0, padx=5)
    next_button = ctk.CTkButton(buttons_frame, text="Next", state="disabled")
    next_button.grid(row=0, column=1, padx=5)
    finish_button = ctk.CTkButton(buttons_frame, text="Finish")
    finish_button.grid(row=0, column=2, padx=5)
    cancel_button = ctk.CTkButton(buttons_frame, text="Cancel")
    cancel_button.grid(row=0, column=3, padx=5)

create_widgets_3()
root3.mainloop()
