import xml.etree.ElementTree as ET
from lxml import etree
from io import StringIO, BytesIO
from lxml.etree import Element
import fileinput
import logging
import configparser
import os
from pathlib import Path

def getColumnsDates(columns):
    newColumns = []
    columnsPks = []
    for columnOld in columns:   
        newColumn = columnOld
        type = columnOld["type"] 
        
        if type == "FLOAT":
               newColumn["DATO_TYPE"] = "BigDecimal"
               newColumn["DATA_IMPORT"] = "java.math.BigDecimal"
        if type == "NUMBER":
               if columnOld["dataPrecision"] != None and columnOld["dataPrecision"] > 1 and columnOld["dataPrecision"] < 5:
                newColumn["DATO_TYPE"] = "Long"
                newColumn["DATA_IMPORT"] = ""
               elif columnOld["dataPrecision"] != None and columnOld["dataPrecision"] >= 5:
                newColumn["DATO_TYPE"] = "BigDecimal"
                newColumn["DATA_IMPORT"] = "java.math.BigDecimal"
               else :
                newColumn["DATO_TYPE"] = "Integer"
                newColumn["DATA_IMPORT"] = ""
        elif type == "LONG":
               newColumn["DATO_TYPE"] = "Long"
               newColumn["DATA_IMPORT"] = ""
        elif type == "CLOB":
               newColumn["DATO_TYPE"] = "Clob"
               newColumn["DATA_IMPORT"] = "java.sql.Clob"
        elif type == "BLOB":
              newColumn["DATO_TYPE"] = "Blob"
              newColumn["DATA_IMPORT"] = "java.sql.Clob"
        elif type == "DATE":
              newColumn["DATO_TYPE"] = "Date"
              newColumn["DATA_IMPORT"] = "java.util.Date"
        elif type == "TIMESTAMP":
              newColumn["DATO_TYPE"] = "Date"
              newColumn["DATA_IMPORT"] = "java.util.Date"
        else :
              newColumn["DATO_TYPE"] = "String"
              newColumn["DATA_IMPORT"] = ""
        #si el import ya esta, no repetimos
        if contains(newColumns, lambda x: x["DATA_IMPORT"] == newColumn["DATA_IMPORT"]): 
             newColumn["DATA_IMPORT"] = ""          
        newColumns.append(newColumn) 
        if columnOld["primaryKey"] == "P":
            columnsPks.append(newColumn)       
    return [newColumns,columnsPks]

def toCamelCase(text):
    s = text.replace("-", " ").replace("_", " ")
    s = s.split()
    if len(text) == 0:
        return text.capitalize()
    return s[0].capitalize() + ''.join(i.capitalize() for i in s[1:]) 

def contains(list, filter):
    for x in list:
        if filter(x):
            return True
    return False

# Function to convert the string
# from snake case to camel case
def snakeToCamel(str):
    # split underscore using split
    str = str.lower()
    temp = str.split('_')
    
    # joining result 
    res = temp[0] + ''.join(ele.title() for ele in temp[1:])
    return res

def modifyTiles(ruta,entityName, final):
    tree = ET.parse(ruta)
    root = tree.getroot()   
    diag = root.find('definition[@name="'+entityName+'"]') 
    if (diag == None): 
         padre = ET.Element("definition")
         padre.set('extends','template')
         padre.set('name',entityName)
         content = ET.Element("put-attribute")
         content.set('name','content')
         content.set('value',"/WEB-INF/views/"+entityName+"/"+entityName+".jsp")
         includes = ET.Element("put-attribute")
         includes.set('name','includes')
         includes.set('value',"/WEB-INF/views/"+entityName+"/"+entityName+"-includes.jsp")
         ET.indent(tree, '  ')
         padre.append(content)
         padre.append(includes)
         root.append(padre)
         tree.write(ruta)
    if(final):
        ET.indent(tree, '  ') 
        tree.write(ruta) 

def modifyJackson(ruta,entityName, final, packageName):
    packageName = packageName + ".model."+entityName
    tree = etree.parse(ruta)
    root = tree.getroot()   
    diag = root.find("./*[@id='udaModule']") #Debe existir el bean
    serial = diag.find("./*[@name='serializers']")
    if (serial == None): #buscar el serializers
         serial = Element("property")
         serial.set('name',"serializers")
         # crear ulti map
         utilMap = Element("util")
         serial.append(utilMap)
         diag.append(serial) 
    else:  
        utilMap = serial.find("./")           
    if (utilMap.find("./*[@key='#{T("+packageName+")}']") == None):
        entry = Element("entry")
        entry.set('key','#{T('+packageName+')}')
        entry.set('value-ref',"customSerializer")
        etree.indent(root, space="    ")
        utilMap.append(entry)
        tree.write(ruta)
    if(final):
       etree.indent(root, space="    ") 
       tree.write(ruta)    

def modifyMenu(ruta,entityName, final):
 linea1 = "	<spring:url value=\"/"+entityName+"/maint\" var=\""+entityName+"Maint\" htmlEscape=\"true\"/>"
 linea2 = "	<a class=\"dropdown-item\" href=\"${"+entityName+"Maint}\">"
 linea3 = "		<spring:message code=\""+entityName+"Maint\" />"
 linea4 = "	</a>"
 encontrado = False
 with fileinput.input(ruta, inplace=True) as f:
   for linea in f:
      if linea == "</div>": #ultima linea 
          if not encontrado:
            print (linea1)
            print (linea2)
            print (linea3)
            print (linea4)
          print (linea)
      else:   
        if linea in (entityName+"/maint"):#encontrado
            encontrado = True
            logging.warning('Mantenimiento ya definido en el menu.jsp')
        print (linea)
#section String padre, keyArray array de llaves
def writeConfig(section,key):
    try:
        base_path = os.path.dirname(os.path.abspath(__file__))
        configfile_name = os.path.join(base_path, 'config.ini')
        config = configparser.ConfigParser()
        config.read(configfile_name)
        if(len(key) == 1):
          for k in key:
            config.set(section,k,key[k])
        else:      
          config[section] = key
    
        with open(configfile_name, 'w') as configfile:
            config.write(configfile)
    except ValueError:
        print("An exception occurred al escribir el config: " + ValueError)

def readConfig(valor,key):

    try:
        base_path = os.path.dirname(os.path.abspath(__file__))
        configfile_name = os.path.join(base_path, 'config.ini')
        config = configparser.ConfigParser()
        config.read(configfile_name) 
        if key == None:
            return config[valor]
        return config[valor][key]
    except Exception as e:
        print("An exception occurred: leer config: " , e)
    return ""

def rutaActual(ruta_archivo_actual):
    rutaPath = ""
    try:
        rutaPath = os.path.dirname(ruta_archivo_actual)
    except Exception as e:
        print("An exception occurred: rutaActual", e)    
    return rutaPath

def buscarArchivo(ruta,tipo):
    path = ""
    try:
        files = [file for file in os.listdir(ruta) if file.endswith(tipo)]
        if len(files) != 0:
            return Path(files[0]).stem
    except Exception as e:
        print("An exception occurred: buscarArchivo", e)
    return path

def obtenerNombreProyecto(ruta,nombreWar):
    path = ""
    try:
        tree = etree.parse(ruta+"/.classpath")
        root = tree.getroot()
        diag = root.xpath(".//classpathentry[contains(@path, '%s')]" % "EARClasses")
        path = diag[0].attrib["path"]
        path = path.replace("/","")
        path = path.replace("EARClassesbuildclasses","")
        nombreWar = nombreWar.replace("War","")
        nombreWar = nombreWar.replace(path,"")
        return nombreWar
    except ValueError:
        print("An exception occurred: obtenerNombreProyecto: ", ValueError)
    return path

def obtenerNombreProyectoWar(ruta):
    path = ""
    try:
        tree = etree.parse(ruta+"/WebContent/WEB-INF/web.xml")
        root = tree.getroot()
        diag = root.find("./*[{http://java.sun.com/xml/ns/javaee}param-name='webAppName']")
        nombreWar = diag[1].text
        return nombreWar
    except Exception as e:
        print("An exception occurred: obtenerNombreProyectoWar",e)
    return path