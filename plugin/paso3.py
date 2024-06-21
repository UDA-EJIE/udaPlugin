import json
from copier import Worker
import os
from plugin.utils import getColumnsDates
from datetime import datetime
from plugin.utils import snakeToCamel
from plugin.utils import toCamelCase
from plugin.utils import modifyTiles
from plugin.utils import modifyMenu
import operator
import logging
from customtkinter import *
from datetime import datetime

#INICIO función principal
def initPaso3(tables,yaml_data, data_mantenimiento, columnsOriginal):
    # work only controller
    proyectName = yaml_data["project_name"]
    proyectWar = yaml_data["war_project_name"]
    directorio_actual = yaml_data["directorio_actual"] 
    dirController = directorio_actual+"controller/" 
    rutaWar = "src/com/ejie/"+proyectName+"/control" 
    data = {}
    destinoWar = yaml_data["destinoApp"]+"/" 
    destinoWarViews = destinoWar+"WebContent/WEB-INF/views/"
    dirMaintJsp = directorio_actual+"maint/"
    data["proyectName"] = proyectName
    
    dirMaintJspIncludes = dirMaintJsp + "includes/"
   
    dest = yaml_data["destinoApp"].replace(proyectWar,"")
    proyectWar = proyectWar.replace("War","")
    data["proyectWar"] = proyectWar
    destinoStaticsJs = dest+proyectName+"Statics/WebContent/"+proyectName+"/scripts/"+proyectWar+"/" 
    rutaTiles = destinoWarViews+"tiles.xml"
    rutaMenu = destinoWar+"WebContent/WEB-INF/layouts/menuMantenimientos.jsp"

    data["packageName"] = "com.ejie."+proyectName  
    lastTable = False

    print("Inicio paso 3")
    for x, table in enumerate(tables):
        #añadir funciones
        columnsDates = getColumnsDates(table["columns"]) 
        data["listPks"] = columnsDates[1]  
        columnas = columnsDates[0]
        allColumnsNoPk = [x for x in columnas if x['primaryKey'] != 'P']
        allColumns = columnsDates[1] + allColumnsNoPk
        sord = calcularOrden(data_mantenimiento[14][1],columnsOriginal,allColumnsNoPk)
        data["columnsDates"] = columnsDates[0]
        data["allColumns"] = allColumns
        data["allColumnsNoPk"] = allColumnsNoPk
        tNameOriginal = table["name"]
        alias = data_mantenimiento[12][1].strip().lower() 
        if(alias == ''):
          alias = snakeToCamel(tNameOriginal)
        else: 
          alias =  snakeToCamel(alias)
        data["alias"] = alias
        tName = snakeToCamel(tNameOriginal) 
        data["tableNameOriginal"] = tNameOriginal
        data["tableName"] = tName[0].capitalize() + tName[1:] 
        data["tableNameDecapitalize"] = tName
        data["titleMaint"]  = data_mantenimiento[1][1]
        data["nameMaint"]  = data_mantenimiento[00][1].strip()
        
        # Opciones mantenimiento
        data["maint"] = {}
        data["maint"]["name"]  = data_mantenimiento[00][1].strip()
        data["maint"]["title"]  = data_mantenimiento[1][1]
        data["maint"]["isMaint"] = data_mantenimiento[2][1]
        data["maint"]["type"] = 'INLINE' if data_mantenimiento[3][1] == 'Edición en línea' else "DETAIL"
        data["maint"]["detail"]  = {}
        data["maint"]["detail"]["requestData"]  = data_mantenimiento[4][1]
        data["maint"]["detail"]["saveButton"]  = data_mantenimiento[5][1]
        data["maint"]["buttons"]  = data_mantenimiento[6][1]
        data["maint"]["contextMenu"]  = data_mantenimiento[7][1]
        data["maint"]["filter"]  = data_mantenimiento[8][1]
        data["maint"]["search"]  = data_mantenimiento[9][1]
        data["maint"]["clientValidation"]  = data_mantenimiento[10][1]
        data["maint"]["multiselection"]  = data_mantenimiento[11][1]
        data["maint"]["loadOnStartUp"]  = data_mantenimiento[13][1]
        data["maint"]["order"]  = {}
        data["maint"]["order"]["sidx"]  = data_mantenimiento[14][1]
        data["maint"]["order"]["sord"]  = data_mantenimiento[15][1]
        data["maint"]["primaryKey"] = snakeToCamel(data["listPks"][0]["name"])
        
        data["urlBase"]  = "../"+toRestUrlNaming(tNameOriginal)
        data["urlStatics"]  = "../"+tNameOriginal
        destinoWarViewsFinal = destinoWarViews + alias +"/"
        destinoWarViewsFinalIncludes = destinoWarViewsFinal +"includes/"

        logging.info("SRC MAINT Jsp:: " +dirMaintJsp)
        logging.info("DEST MAINT Jsp:: " +destinoWarViewsFinal)
        now = datetime.now()
        data["date"] = now.strftime('%d-%b-%Y %H:%M:%S')
        #Generando jsp MAINT 
        with Worker(src_path=dirMaintJsp, dst_path=destinoWarViewsFinal, data=data, exclude=["*.js","includes"],overwrite=True) as worker:
         worker.jinja_env.filters["toCamelCase"] = toCamelCase
         worker.jinja_env.filters["snakeToCamel"] = snakeToCamel
         worker.template.version = ":  1.0 Paso 3 Jsps ::: "+data["date"]
         worker.run_copy() 
        if data["maint"]["isMaint"]: 
            #Generando jsp Includes MAINT 
            with Worker(src_path=dirMaintJspIncludes, dst_path=destinoWarViewsFinalIncludes, data=data,overwrite=True) as worker:
                worker.jinja_env.filters["toCamelCase"] = toCamelCase
                worker.jinja_env.filters["snakeToCamel"] = snakeToCamel
                worker.template.version = ": 1.0 Paso 3 Includes ::: "+data["date"]
                worker.run_copy()
        #Generando js MAINT 
        with Worker(src_path=dirMaintJsp, dst_path=destinoStaticsJs, data=data, exclude=["*.jsp","includes"],overwrite=True) as worker:
         worker.jinja_env.filters["toCamelCase"] = toCamelCase
         worker.jinja_env.filters["snakeToCamel"] = snakeToCamel
         worker.template.version = ":  1.0 Paso 3 Js ::: "+data["date"]
         worker.run_copy() 
         if(x == len(tables) - 1):
           lastTable = True
        modifyTiles(rutaTiles,alias,lastTable)
        modifyMenu(rutaMenu,alias,lastTable)
        logging.info("Fin mantenimento: "+data["tableName"])  
    
    print("Fin paso 3")
    logging.info("Final: paso 3 creado")
    print("Final: paso 3 creado ::: "+data["date"],file=sys.stderr)
    sys.stderr.flush()
        
#FIN función principal
def calcularOrden(pos,columns, columsSelected):
  orden = 0
  columna = columns[pos]
  
  for i, col in enumerate(columsSelected):
    if (columna == col['name']):
     orden = i
     break
  return orden