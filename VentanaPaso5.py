import customtkinter as ctk
import tkinter as tk

# Initialize the customtkinter library
ctk.set_appearance_mode("System")  # Modes: "System" (default), "Dark", "Light"
ctk.set_default_color_theme("blue")  # Themes: "blue" (default), "green", "dark-blue"

# Create the main window
root1 = ctk.CTk()
root1.title("Añadir un módulo EJB a la aplicación")
root1.geometry("700x400")

def create_widgets_1():
    # Title label
    title_label = ctk.CTkLabel(root1, text="Añadir un módulo EJB a la aplicación", font=ctk.CTkFont(size=16, weight="bold"))
    title_label.grid(row=0, column=0, columnspan=2, pady=10)

    # Subtitle label
    subtitle_label = ctk.CTkLabel(root1, text="Este Wizard genera un nuevo módulo EJB y lo añade a un EAR existente")
    subtitle_label.grid(row=1, column=0, columnspan=2)

    # EAR to bind
    ear_label = ctk.CTkLabel(root1, text="EAR a vincular:")
    ear_label.grid(row=2, column=0, sticky="w", pady=5)
    ear_entry = ctk.CTkEntry(root1, width=250)
    ear_entry.grid(row=2, column=1, pady=5, padx=10, sticky="w")
    ear_button = ctk.CTkButton(root1, text="Buscar Proyecto")
    ear_button.grid(row=2, column=1, sticky="e", padx=10)

    # EJB project name
    ejb_name_label = ctk.CTkLabel(root1, text="Nombre del proyecto EJB:")
    ejb_name_label.grid(row=3, column=0, sticky="w", pady=5)
    ejb_name_entry = ctk.CTkEntry(root1, width=250)
    ejb_name_entry.grid(row=3, column=1, pady=5, padx=10, sticky="w")

    # Full EJB module name
    full_ejb_name_label = ctk.CTkLabel(root1, text="Nombre Completo del módulo EJB:")
    full_ejb_name_label.grid(row=4, column=0, sticky="w", pady=5)
    full_ejb_name_entry = ctk.CTkEntry(root1, width=250)
    full_ejb_name_entry.grid(row=4, column=1, pady=5, padx=10, sticky="w")

    # Buttons
    buttons_frame = ctk.CTkFrame(root1)
    buttons_frame.grid(row=5, column=0, columnspan=2, pady=10)
    
    back_button = ctk.CTkButton(buttons_frame, text="Back")
    back_button.grid(row=0, column=0, padx=5)
    next_button = ctk.CTkButton(buttons_frame, text="Next", state="disabled")
    next_button.grid(row=0, column=1, padx=5)
    finish_button = ctk.CTkButton(buttons_frame, text="Finish")
    finish_button.grid(row=0, column=2, padx=5)
    cancel_button = ctk.CTkButton(buttons_frame, text="Cancel")
    cancel_button.grid(row=0, column=3, padx=5)

create_widgets_1()
root1.mainloop()
