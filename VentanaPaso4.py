import customtkinter as ctk
import tkinter as tk

# Initialize the customtkinter library
ctk.set_appearance_mode("System")  # Modes: "System" (default), "Dark", "Light"
ctk.set_default_color_theme("blue")  # Themes: "blue" (default), "green", "dark-blue"

# Create the main window
root = ctk.CTk()
root.title("Añadir un WAR a la aplicación")
root.geometry("700x500")

# Create and place widgets
def create_widgets():
    # Title label
    title_label = ctk.CTkLabel(root, text="Añadir un WAR a la aplicación", font=ctk.CTkFont(size=16, weight="bold"))
    title_label.grid(row=0, column=0, columnspan=2, pady=10)

    # Subtitle label
    subtitle_label = ctk.CTkLabel(root, text="Este Wizard genera un nuevo WAR y lo añade a un EAR existente")
    subtitle_label.grid(row=1, column=0, columnspan=2)

    # EAR to bind
    ear_label = ctk.CTkLabel(root, text="EAR a vincular:")
    ear_label.grid(row=2, column=0, sticky="w", pady=5)
    ear_entry = ctk.CTkEntry(root, width=250)
    ear_entry.grid(row=2, column=1, pady=5, padx=10, sticky="w")
    ear_button = ctk.CTkButton(root, text="Buscar Proyecto")
    ear_button.grid(row=2, column=1, sticky="e", padx=10)

    # WAR name
    war_name_label = ctk.CTkLabel(root, text="Nombre del WAR:")
    war_name_label.grid(row=3, column=0, sticky="w", pady=5)
    war_name_entry = ctk.CTkEntry(root, width=250)
    war_name_entry.grid(row=3, column=1, pady=5, padx=10, sticky="w")

    # Full WAR name
    full_war_name_label = ctk.CTkLabel(root, text="Nombre Completo del WAR:")
    full_war_name_label.grid(row=4, column=0, sticky="w", pady=5)
    full_war_name_entry = ctk.CTkEntry(root, width=250)
    full_war_name_entry.grid(row=4, column=1, pady=5, padx=10, sticky="w")

    # Layout
    layout_label = ctk.CTkLabel(root, text="Layout")
    layout_label.grid(row=5, column=0, columnspan=2, pady=10)

    # Idiomas
    idiomas_label = ctk.CTkLabel(root, text="Idiomas")
    idiomas_label.grid(row=6, column=0, sticky="w", pady=5)
    
    base_frame = ctk.CTkFrame(root)
    base_frame.grid(row=7, column=0, columnspan=2, pady=5, padx=10, sticky="w")
    
    castellano_cb = ctk.CTkCheckBox(base_frame, text="Castellano")
    castellano_cb.grid(row=0, column=0, padx=5)
    euskera_cb = ctk.CTkCheckBox(base_frame, text="Euskera")
    euskera_cb.grid(row=0, column=1, padx=5)

    otros_frame = ctk.CTkFrame(root)
    otros_frame.grid(row=8, column=0, columnspan=2, pady=5, padx=10, sticky="w")

    ingles_cb = ctk.CTkCheckBox(otros_frame, text="Inglés")
    ingles_cb.grid(row=0, column=0, padx=5)
    frances_cb = ctk.CTkCheckBox(otros_frame, text="Francés")
    frances_cb.grid(row=0, column=1, padx=5)

    # Default language
    default_lang_label = ctk.CTkLabel(root, text="Idioma por defecto:")
    default_lang_label.grid(row=9, column=0, sticky="w", pady=5)
    default_lang_option = ctk.CTkOptionMenu(root, values=["Castellano", "Euskera", "Inglés", "Francés"])
    default_lang_option.grid(row=9, column=1, pady=5, padx=10, sticky="w")

    # Security with XLNets
    security_label = ctk.CTkLabel(root, text="Seguridad con XLNets:")
    security_label.grid(row=10, column=0, sticky="w", pady=5)
    
    security_frame = ctk.CTkFrame(root)
    security_frame.grid(row=11, column=0, columnspan=2, pady=5, padx=10, sticky="w")

    security_var = tk.StringVar(value="Sí")  # Using StringVar for the radio buttons

    security_yes_rb = ctk.CTkRadioButton(security_frame, text="Sí", variable=security_var, value="Sí")
    security_yes_rb.grid(row=0, column=0, padx=5)
    security_no_rb = ctk.CTkRadioButton(security_frame, text="No", variable=security_var, value="No")
    security_no_rb.grid(row=0, column=1, padx=5)

    # Buttons
    buttons_frame = ctk.CTkFrame(root)
    buttons_frame.grid(row=12, column=0, columnspan=2, pady=10)
    
    back_button = ctk.CTkButton(buttons_frame, text="Back")
    back_button.grid(row=0, column=0, padx=5)
    next_button = ctk.CTkButton(buttons_frame, text="Next", state="disabled")
    next_button.grid(row=0, column=1, padx=5)
    finish_button = ctk.CTkButton(buttons_frame, text="Finish")
    finish_button.grid(row=0, column=2, padx=5)
    cancel_button = ctk.CTkButton(buttons_frame, text="Cancel")
    cancel_button.grid(row=0, column=3, padx=5)

create_widgets()

# Run the application
root.mainloop()
