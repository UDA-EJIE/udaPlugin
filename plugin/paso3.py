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

#INICIO función principal
def initPaso3(tables,yaml_data, data_mantenimiento):
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
    data["maint"] = {"detailSaveButton":True, "filterMaint":True}
    dest = yaml_data["destinoApp"].replace(proyectWar,"")
    proyectWar = proyectWar.replace("War","")
    data["proyectWar"] = proyectWar
    destinoStaticsJs = dest+proyectName+"Statics/WebContent/"+proyectName+"/scripts/"+proyectWar+"/" 
    rutaTiles = destinoWarViews+"tiles.xml"
    rutaMenu = destinoWar+"WebContent/WEB-INF/layouts/menuMantenimientos.jsp"

    data["packageName"] = "com.ejie."+proyectName  
    lastTable = False
    for x, table in enumerate(tables):
        #añadir funciones
        columnsDates = getColumnsDates(table["columns"]) 
        data["listPks"] = columnsDates[1]  
        columnas = columnsDates[0]
        allColumnsNoPk = [x for x in columnas if x['primaryKey'] != 'P']
        allColumns = columnsDates[1] + allColumnsNoPk
        data["columnsDates"] = columnsDates[0]
        data["allColumns"] = allColumns
        data["allColumnsNoPk"] = allColumnsNoPk
        tNameOriginal = table["name"]
        tName = snakeToCamel(tNameOriginal) 
        data["tableNameOriginal"] = tNameOriginal
        data["tableName"] = tName[0].capitalize() + tName[1:] 
        data["tableNameDecapitalize"] = tName  
        data["titleMaint"]  = data_mantenimiento[1][1]
        data["nameMaint"]  = data_mantenimiento[00][1] 
        data["urlBase"]  = "../"+table["name"]
        data["filterMaint"]  = True
        data["typeMaint"] = "DETAIL"
        data["urlStatics"]  = "../"+table["name"]
        destinoWarViewsFinal = destinoWarViews + tName.lower() +"/"
        destinoWarViewsFinalIncludes = destinoWarViewsFinal +"includes/"  
        data["maint"]["primaryKey"] = data["listPks"][0]      
        data["maint"]["isMaint"] = True
        data["maint"]["typeMaint"] = 'INLINE' if data_mantenimiento[3][1] == 'Edición en línea' else "DETAIL"
        data["maint"]["clientValidationMaint"] = True

        print("SRC MAINT Jsp:: " +dirMaintJsp)
        print("DEST MAINT Jsp:: " +destinoWarViewsFinal)
        #Generando jsp MAINT 
        with Worker(src_path=dirMaintJsp, dst_path=destinoWarViewsFinal, data=data, exclude=["*.js"],overwrite=True) as worker:
         worker.jinja_env.filters["toCamelCase"] = toCamelCase
         worker.run_copy() 
        #Generando jsp Includes MAINT 
        with Worker(src_path=dirMaintJspIncludes, dst_path=destinoWarViewsFinalIncludes, data=data,overwrite=True) as worker:
         worker.jinja_env.filters["toCamelCase"] = toCamelCase
         worker.run_copy()
        #Generando js MAINT 
        with Worker(src_path=dirMaintJsp, dst_path=destinoStaticsJs, data=data, exclude=["*.jsp"],overwrite=True) as worker:
         worker.jinja_env.filters["toCamelCase"] = toCamelCase
         worker.run_copy() 
         if(x == len(tables) - 1):
           lastTable = True
        modifyTiles(rutaTiles,table["name"].lower(),lastTable)
        modifyMenu(rutaMenu,table["name"].lower(),lastTable)
        print("Fin mantenimento: "+data["tableName"])  
  
        
#FIN función principal