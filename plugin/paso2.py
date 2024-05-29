import json
from copier import Worker
import os
from plugin.utils import getColumnsDates
from datetime import datetime
from plugin.utils import snakeToCamel
from plugin.utils import toCamelCase
from plugin.utils import modifyJackson
import operator

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
    rutaJackson = destinoWarViews+"jackson-config.xml"

    # si no existe crear la carpeta, raiz control - config java
    if os.path.isdir(destinoWarControl) == False:
        os.makedirs(destinoWarControl)
    if os.path.isdir(destinoEarService) == False:
        os.makedirs(destinoEarService)
    if os.path.isdir(destinoEarDao) == False:
        os.makedirs(destinoEarDao)
    if os.path.isdir(destinoEarModel) == False:
        os.makedirs(destinoEarModel)            
    data["packageName"] = "com.ejie."+proyectName  
    lastTable = False
    for x, table in enumerate(tables):
        #añadir funciones
        columnsDates = getColumnsDates(table["columns"]) 
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
        #Fecha creación controllers
        now = datetime.now()        
        data["date"] = now.strftime('%d-%b-%Y %H:%M:%S')    

        #controller java 
        if(ventanaPaso2.controladores_var.get()):
            with Worker(src_path=dirController, dst_path=destinoWarControl, data=data, exclude=["Mvc*","*RelationsImpl"],overwrite=True) as worker:
             worker.jinja_env.filters["snakeToCamel"] = snakeToCamel
             worker.jinja_env.filters["toCamelCase"] = toCamelCase
             worker.run_copy() 

        #Fecha creación services
        now = datetime.now()        
        data["date"] = now.strftime('%d-%b-%Y %H:%M:%S') 
        data["project_name"] = proyectName 
        #service java 
        if(ventanaPaso2.servicios_var.get()):
            with Worker(src_path=dirService, dst_path=destinoEarService, data=data, exclude=["*Rel*"],overwrite=True) as worker:
                worker.run_copy()   

        #Fecha creación Daos
        now = datetime.now()        
        data["date"] = now.strftime('%d-%b-%Y %H:%M:%S')  
        #Daos java 
        if(ventanaPaso2.daos_var.get()):
         with Worker(src_path=dirDao, dst_path=destinoEarDao, data=data, exclude=["*Rel*"],overwrite=True) as worker:
             worker.jinja_env.filters["toCamelCase"] = toCamelCase
             worker.run_copy()  
        
        #Fecha creación Models
        now = datetime.now()        
        data["date"] = now.strftime('%d-%b-%Y %H:%M:%S')  
        #Models java 
        if(ventanaPaso2.modelo_datos_var.get()):
            with Worker(src_path=dirModel, dst_path=destinoEarModel, data=data, exclude=["*model*"],overwrite=True) as worker:
                worker.jinja_env.filters["toCamelCase"] = toCamelCase
                worker.jinja_env.filters["snakeToCamel"] = snakeToCamel
                worker.run_copy()
                if(x == len(tables) - 1):
                    lastTable = True
                if os.path.isdir(rutaJackson) == True:    
                    modifyJackson(rutaJackson,tName,lastTable,data["packageName"])                   
        
#FIN función principal