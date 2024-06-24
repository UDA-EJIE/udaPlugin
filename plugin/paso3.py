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
        sord = calcularOrden(data_mantenimiento["sord"],columnsOriginal,allColumnsNoPk)
        data["columnsDates"] = columnsDates[0]
        data["allColumns"] = allColumns
        data["allColumnsNoPk"] = allColumnsNoPk
        tNameOriginal = table["name"]
        alias = data_mantenimiento["alias"].strip().lower() 
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
        data["maint"]["isMaint"] = data_mantenimiento["isMaint"]
        data["maint"]["type"] = 'INLINE' if data_mantenimiento["tipoMantenimiento"] == 'Edición en línea' else "DETAIL"
        data["maint"]["detail"]  = {}
        data["maint"]["detail"]["requestData"]  = data_mantenimiento["requestData"]
        data["maint"]["detail"]["saveButton"]  = data_mantenimiento["saveButton"]
        data["maint"]["buttons"]  = data_mantenimiento["buttons"]
        data["maint"]["contextMenu"]  = data_mantenimiento["contextMenu"]
        data["maint"]["filter"]  = data_mantenimiento["filter"]
        data["maint"]["search"]  = data_mantenimiento["search"]
        data["maint"]["clientValidation"]  = data_mantenimiento["clientValidation"]
        data["maint"]["multiselection"]  = data_mantenimiento["multiselection"]
        data["maint"]["loadOnStartUp"]  = data_mantenimiento["loadOnStartUp"]
        data["maint"]["order"]  = {}
        data["maint"]["order"]["sord"]  = sord
        data["maint"]["order"]["sidx"]  = data_mantenimiento["sidx"]
        data["maint"]["title"]  = data_mantenimiento["titulo_mantenimiento"]
        data["maint"]["name"]  = data_mantenimiento["nombre_mantenimiento"].strip() 
        data["urlBase"]  = data_mantenimiento["urlBase"]
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