# Manual de uso del asistente de generación de código

## Introducción

En el presente documento se van a detallar los pasos a seguir para generar una aplicación UDA mediante un asistente de generación de código.

El asistente se divide en los siguientes tres pasos:

1. #### Crear una nueva aplicación
	Permite generar una aplicación básica a partir de la cuál en los siguientes pasos se podrá ir alimentando de negocio, control y mantenimientos.
	
	Se puede configurar el código de aplicación, la ubicación, el nombre del WAR, los idiomas a incluir, el idioma por defecto e incluso si se delegará en XLNetS la seguridad de la aplicación.

2. #### Generar código de negocio y control	
	Lo primero de todo es rellenar el formulario para que el asistente sea capaz de conectarse a la BBDD que se usará para obtener los esquemas a partir de los cuales se genera el código.
	
	Después, se deben seleccionar los esquemas sobre los que se quieren generar el código.
	
	Por último, el asistente permite seleccionar si se desean generar las siguientes partes del negocio o presentación:
	- Modelo de datos
	- DAOs
	- Servicios
	- Controladores

3. #### Generar mantenimiento	
	Al igual que en el anterior paso, lo primero de todo será rellenar el formulario para que el asistente sea capaz de conectarse a la BBDD.
	
	A continuación, se permite elegir el nombre del mantenimiento al igual que el título a mostrar en la JSP. También se permiten el siguiente grupo de opciones:
	- Mantenimiento
	- Tipo de mantenimiento (si es edición en formulario permite configurar la obtención de los datos además de la tipología de los botones)
	- Botonera
	- Menú contextual
	- Filtrado de datos
	- Búsqueda
	- Validaciones en cliente
	- Multiselección
	
	Entre las distintas opciones, se ofrece la posibilidad de elegir si la tabla será o no un mantenimiento, pudiendo ser una tabla básica de visualización de datos. En los casos de tablas básicas no es posible definir el tipo de mantenimiento ni las validaciones. 

	Después, se deben seleccionar los esquemas sobre los que se quieren generar el código, aunque además, es posible realizar la configuración de la URL a la que apuntará el mantenimiento, el nombre del JS a generar, la carga al inicio de la ventana y la ordenación.

	Es importante remarcar que cuando una tabla sea básica y no tenga el filtro habilitado, no se podrá desmarcar la opción de carga al inicio de la ventana.
	
	Finalmente, es posible desmarcar columnas (únicamente las permitidas) que no quieran ser mostradas.
