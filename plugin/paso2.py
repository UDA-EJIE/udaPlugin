import json
from copier import Worker
import os
from plugin.utils import getColumnsDates
from datetime import datetime
from plugin.utils import snakeToCamel
from plugin.utils import toCamelCase
from plugin.utils import toRestUrlNaming
from plugin.utils import modifyJackson
import operator
import logging
from customtkinter import *
from plugin.utils import writeConfig
from plugin.utils import obtenerNombreProyectoByEar

#INICIO función principal
def initPaso2(tables,yaml_data,ventanaPaso2):
    # work only controller
    data = {}
    proyectName = yaml_data["project_name"]
    proyectWar = yaml_data["war_project_name"]
    directorio_actual = yaml_data["directorio_actual"] 
    dirController = directorio_actual+"controller/" 
    rutaWar = "src/com/ejie/"+proyectName+"/control" 
    war = proyectName+proyectWar+"War";
    destinoWar = yaml_data["destinoWar"]+"/" 
    destinoWarViews = destinoWar+"WebContent/WEB-INF/spring/"
    destinoWarControl = destinoWar+rutaWar
    dirService = directorio_actual+"service/" 
    destinoEarService = yaml_data["destinoApp"]+"/src/com/ejie/"+proyectName+"/service"
    dirDao = directorio_actual+"dao/" 
    destinoEarDao = yaml_data["destinoApp"]+"/src/com/ejie/"+proyectName+"/dao"
    dirModel = directorio_actual+"model/" 
    destinoEarModel = yaml_data["destinoApp"]+"/src/com/ejie/"+proyectName+"/model"

    destinoSrc = ""
    destinoSrcWar = ""
    
    if(yaml_data["destinoApp"] != ""):
     destinoApp = yaml_data["destinoApp"]
     destinoApp = destinoApp.replace("//"+ventanaPaso2.archivoClases,"")
     destinoApp = destinoApp.replace("\\"+ventanaPaso2.archivoClases,"")
     destinoSrc = destinoApp.replace("/"+ventanaPaso2.archivoClases,"")
    if(yaml_data["destinoWar"] != ""):
     destinoWar = yaml_data["destinoWar"]
     destinoWar = destinoWar.replace("//"+ventanaPaso2.archivoWar,"")
     destinoWar = destinoWar.replace("\\"+ventanaPaso2.archivoWar,"")
     destinoSrcWar = destinoWar.replace("/"+ventanaPaso2.archivoWar,"") 

    # si no existe crear la carpeta, raiz control - config java
    if ventanaPaso2.controladores_var.get() and os.path.isdir(destinoWarControl) == False:
        os.makedirs(destinoWarControl)
    if ventanaPaso2.servicios_var.get() and os.path.isdir(destinoEarService) == False:
        os.makedirs(destinoEarService)
    if ventanaPaso2.daos_var.get() and os.path.isdir(destinoEarDao) == False:
        os.makedirs(destinoEarDao)
    if ventanaPaso2.modelo_datos_var.get() and os.path.isdir(destinoEarModel) == False:
        os.makedirs(destinoEarModel)            
    data["packageName"] = "com.ejie."+proyectName  
    lastTable = False

    for x, table in enumerate(tables):
        #añadir funciones
        columnsDates = getColumnsDates(table["columns"])
        #columnDao = getColumnsDates(table["columnasDao"])
        if not table["controller"] is None:
            colControllerPk= getColumnsDates(table["controller"]['primaryKeyCol'])
            colControllerAll = getColumnsDates(table["controller"]['columns'])
            colControllerPkRel = getColumnsDates(table["controller"]['colPrimaryRelacion'])
            data["colControllerPk"] = colControllerPk
            data["colControllerAll"] = colControllerAll
            data["colControllerPkRel"] = colControllerPkRel
            data["entidadRelacion"] = table["controller"]["entidadRelacion"]
            data["controller"] = "value"
        else:
           data["controller"] = None
        data["listPks"] = columnsDates[1]  
        columnas = columnsDates[0]
        allColumns = columnsDates[1] + [x for x in columnas if x['primaryKey'] != 'P']
        data["columnsDates"] = columnsDates[0]
        data["allColumns"] = allColumns
        tNameOriginal = table["name"]
        tName = snakeToCamel(tNameOriginal) 
        data["tableNameOriginal"] = tNameOriginal
        data["tableName"] = tName[0].capitalize() + tName[1:] 
        data["tableNameDecapitalize"] = tName
        if not table["dao"] is None:
            data["entidadPadre"] = table["dao"]['entidadPadre']
            data["primaryKeyPadre"] = table["dao"]['primaryKey']
            data["columnasDaos"] = table["columnasDao"]
            data["padreOriginalCol"] = table["dao"]['entidadPadreCol']
            data['tableFKey'] = table["dao"]["foreingkey"]
            data['primaryKPadre'] = table["dao"]["primaryPadre"]
            data["dao"] = "value"
        else:
           data["dao"] = None
           data["columnasDaos"] = columnsDates[0]
        #Fecha creación controllers
        now = datetime.now()        
        data["date"] = now.strftime('%d-%b-%Y %H:%M:%S')    
        print("Inicio paso 2")
        generoEar = False
        #controller java 
        if(ventanaPaso2.controladores_var.get()):            
            logging.info("Inicio: crear controllers...")
            with Worker(src_path=dirController, dst_path=destinoWarControl, data=data, exclude=["Mvc*","*RelationsImpl"],overwrite=True) as worker:
             worker.jinja_env.filters["snakeToCamel"] = snakeToCamel
             worker.jinja_env.filters["toCamelCase"] = toCamelCase
             worker.jinja_env.filters["toRestUrlNaming"] = toRestUrlNaming
             worker.template.version = ":  1.0 Paso 2 Controllers ::: "+data["date"]
             worker.run_copy() 
             writeConfig("RUTA", {"ruta_war":destinoSrcWar})
             writeConfig("RUTA", {"ruta_ultimo_proyecto":destinoSrcWar})

        #Fecha creación services
        now = datetime.now()        
        data["date"] = now.strftime('%d-%b-%Y %H:%M:%S') 
        data["project_name"] = proyectName 
        #service java 
        if(ventanaPaso2.servicios_var.get()):
            logging.info("Inicio: crear services...")
            with Worker(src_path=dirService, dst_path=destinoEarService, data=data, exclude=["*Rel*"],overwrite=True) as worker:
                worker.template.version = ":  1.0 Paso 2 servicios ::: "+data["date"]
                worker.run_copy() 
                generoEar = True  

        #Fecha creación Daos
        now = datetime.now()        
        data["date"] = now.strftime('%d-%b-%Y %H:%M:%S')  
        #Daos java 
        if(ventanaPaso2.daos_var.get()):
         logging.info("Inicio: crear daos...")
         with Worker(src_path=dirDao, dst_path=destinoEarDao, data=data, exclude=["*Rel*"],overwrite=True) as worker:
             worker.jinja_env.filters["toCamelCase"] = toCamelCase
             worker.jinja_env.filters["snakeToCamel"] = snakeToCamel
             worker.jinja_env.filters["toRestUrlNaming"] = toRestUrlNaming
             worker.template.version = ": 1.0 Paso 2 daos ::: "+data["date"]
             worker.run_copy()  
             generoEar = True
        
        #Fecha creación Models
        now = datetime.now()        
        data["date"] = now.strftime('%d-%b-%Y %H:%M:%S')  
        #Models java 
        if(ventanaPaso2.modelo_datos_var.get()):
            logging.info("Inicio: crear models...")
            with Worker(src_path=dirModel, dst_path=destinoEarModel, data=data, exclude=["*model*"],overwrite=True) as worker:
                worker.jinja_env.filters["toCamelCase"] = toCamelCase
                worker.jinja_env.filters["snakeToCamel"] = snakeToCamel
                worker.jinja_env.filters["toRestUrlNaming"] = toRestUrlNaming
                worker.template.version = ": 1.0 Paso 2 modelos ::: "+data["date"]
                worker.run_copy()
                if(x == len(tables) - 1):
                    lastTable = True
                #Obtener war desde el Ear seleccionado
                rutaClasses = destinoSrc + "/" + ventanaPaso2.archivoClases.replace("Classes","") 
                nombreWar = obtenerNombreProyectoByEar(rutaClasses)
                if nombreWar != '':
                    destinoWarViews = destinoSrc+"/"+nombreWar+"/WebContent/WEB-INF/spring/"
                    rutaJackson = destinoWarViews+"jackson-config.xml"    
                    if os.path.isfile(rutaJackson) == True:    
                        modifyJackson(rutaJackson,data["tableName"],lastTable,data["packageName"])  
                generoEar = True                     
    if(generoEar):
        writeConfig("RUTA", {"ruta_classes":destinoSrc})
        writeConfig("RUTA", {"ruta_ultimo_proyecto":destinoSrc})
    print("Fin paso 2") 
    logging.info("Final: paso 2 creado") 
    print("Final: paso 2 creado ::: "+data["date"],file=sys.stderr)  
    sys.stderr.flush()
#FIN función principal