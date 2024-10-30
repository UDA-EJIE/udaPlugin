import xml.etree.ElementTree as ET
from lxml import etree
from io import StringIO, BytesIO
from lxml.etree import Element
import fileinput
import logging
import configparser
import os
from pathlib import Path
from plumbum import local
import subprocess
import sys

def getColumnsDates(columns):
    newColumns = []
    columnsPks = []
    entidadesRelacionadas = []
    for columnOld in columns:   
        newColumn = columnOld
        name = columnOld["name"]
        type = columnOld["type"]
        newColumn["editable"] = "true"
        if columnOld["primaryKey"] == "P": 
            newColumn["editable"] = "false"
        newColumn["hidden"] = "false"
        newColumn["requiredEditRules"] = "false"
        
        if type == "FLOAT":
               newColumn["DATO_TYPE"] = "BigDecimal"
               newColumn["DATA_IMPORT"] = "java.math.BigDecimal"
               newColumn["DATA_IMPORT2"] = ""
        if type == "NUMBER":
               if columnOld["dataPrecision"] != None and columnOld["dataPrecision"] > 1 and columnOld["dataPrecision"] < 5:
                newColumn["DATO_TYPE"] = "Long"
                newColumn["DATA_IMPORT"] = ""
                newColumn["DATA_IMPORT2"] = ""
               elif columnOld["dataPrecision"] != None and columnOld["dataPrecision"] >= 5:
                newColumn["DATO_TYPE"] = "BigDecimal"
                newColumn["DATA_IMPORT"] = "java.math.BigDecimal"
                newColumn["DATA_IMPORT2"] = ""
               else :
                newColumn["DATO_TYPE"] = "Integer"
                newColumn["DATA_IMPORT"] = ""
                newColumn["DATA_IMPORT2"] = ""
        elif type == "LONG":
               newColumn["DATO_TYPE"] = "Long"
               newColumn["DATA_IMPORT"] = ""
               newColumn["DATA_IMPORT2"] = ""
        elif type == "CLOB":
               newColumn["DATO_TYPE"] = "Clob"
               newColumn["DATA_IMPORT"] = "java.sql.Clob"
               newColumn["DATA_IMPORT2"] = ""
        elif type == "BLOB":
              newColumn["DATO_TYPE"] = "Blob"
              newColumn["DATA_IMPORT"] = "java.sql.Blob"
              newColumn["DATA_IMPORT2"] = ""
        elif type == "DATE":
              newColumn["DATO_TYPE"] = "Date"
              newColumn["DATA_IMPORT"] = "java.util.Date"
              newColumn["DATA_IMPORT2"] = ""
        elif type == "TIMESTAMP":
              newColumn["DATO_TYPE"] = "Date"
              newColumn["DATA_IMPORT"] = "java.util.Date"
              newColumn["DATA_IMPORT2"] = ""
        elif type == "LIST":
            newColumn["name"] = name+"s"
            newColumn["DATO_TYPE"] = "List"
            newColumn["DATA_IMPORT"] = "java.util.List"
            newColumn["DATA_IMPORT2"] = "java.util.ArrayList"
        elif name == type:
            newColumn["DATO_TYPE"] = toCamelCase(type)
            newColumn["DATA_IMPORT"] = ""
            newColumn["DATA_IMPORT2"] = ""
            entidadesRelacionadas.append(newColumn)
        else :
              newColumn["DATO_TYPE"] = "String"
              newColumn["DATA_IMPORT"] = ""
              newColumn["DATA_IMPORT2"] = ""
        #si el import ya esta, no repetimos
        if contains(newColumns, lambda x: x["DATA_IMPORT"] == newColumn["DATA_IMPORT"]): 
             newColumn["DATA_IMPORT"] = ""
        if contains(newColumns, lambda x: x["DATA_IMPORT2"] == newColumn["DATA_IMPORT2"]): 
             newColumn["DATA_IMPORT2"] = "" 
        if contains(newColumns, lambda x: x["name"] == newColumn["name"]):  
            newColumn["name"] = newColumn["name"] + "Ext"#en caso raro de tener el mismo nombre la variable cambia.
            newColumn["priority"] = True               
        newColumns.append(newColumn) 
        if columnOld["primaryKey"] == "P":
            columnsPks.append(newColumn)       
    return [newColumns,columnsPks, entidadesRelacionadas]

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
    res = ""
    try:
    # split underscore using split
        str = str.lower()
        temp = str.split('_')
    
    # joining result 
        res = temp[0] + ''.join(ele.title() for ele in temp[1:])
    except Exception as e:
        logging.error('An exception occurred: snakeToCamel:')

    return res

def toRestUrlNaming(str):
    res = ""
    try:
        res = str.lower().replace("_", "-")
    except Exception as e:
        logging.error('An exception occurred: toRestUrlNaming:')

    return res

def fistLetterMin(cadena):
    if cadena:
        return cadena[0].lower() + cadena[1:]
    return cadena

def modifyTiles(ruta,entityName, final):
    tree = etree.parse(ruta)
    root = tree.getroot()   
    diag = root.find('definition[@name="'+entityName+'"]') 
    if (diag == None): 
         padre = Element("definition")
         padre.set('extends','template')
         padre.set('name',entityName)
         content = Element("put-attribute")
         content.set('name','content')
         content.set('value',"/WEB-INF/views/"+entityName+"/"+entityName+".jsp")
         includes = Element("put-attribute")
         includes.set('name','includes')
         includes.set('value',"/WEB-INF/views/"+entityName+"/"+entityName+"-includes.jsp")
         padre.append(content)
         padre.append(includes)
         etree.indent(padre, space="")
         root.append(padre)
         tree.write(ruta, encoding='utf-8', xml_declaration=True)
    if(final):
        tree.write(ruta, encoding='utf-8', xml_declaration=True) 

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
         utilMap = Element("{http://www.springframework.org/schema/util}map")
         serial.append(utilMap)
         diag.append(serial) 
    else:  
        utilMap = serial.find("./")           
    if (utilMap != None and utilMap.find("./*[@key='#{T("+packageName+")}']") == None):
        entry = Element("entry")
        entry.set('key','#{T('+packageName+')}')
        entry.set('value-ref',"customSerializer")
        etree.indent(diag, space="    ")
        utilMap.append(entry)
        tree.write(ruta, encoding='utf-8', xml_declaration=True)
    if(final):
       #etree.indent(root, space="    ") 
       tree.write(ruta, encoding='utf-8', xml_declaration=True)    

def modifyMenu(ruta,tableRequestMapping,entityName, final):
 linea1 = "	<spring:url value=\"/"+tableRequestMapping+"/maint\" var=\""+entityName+"Maint\" htmlEscape=\"true\"/>"
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
          print (linea, end='')
      else:   
        if entityName+"/maint" in (linea):#encontrado
            encontrado = True
            logging.warning('Mantenimiento ya definido en el menu.jsp')
        print (linea, end='')

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

#escriba las propiedades en un archivo properties
def writeProperties(path,nameFile,section,key):

    configfile_name = os.path.join(path, nameFile)
    config = configparser.ConfigParser()
    
    try:
        # Leer el archivo
        config.read(configfile_name)
    except configparser.MissingSectionHeaderError:
        # Si falta una sección, añadirla automáticamente
        with open(configfile_name, 'r') as f:
            content = f.read()

        # Añadir una sección predeterminada y guardar el archivo corregido
        with open(configfile_name, 'w') as f:
            f.write('[CONFIG]\n' + content)

        # Reintentar leer el archivo
        config.read(configfile_name)    
    try:
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
        paths = sorted(Path(ruta).iterdir() , key=os.path.getmtime, reverse = True)
        files = [file for file in paths if str(file).endswith(tipo)]
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

def obtenerNombreProyectoByEar(ruta):
    path = ""
    try:
        tree = etree.parse(ruta+"/EarContent/META-INF/application.xml")
        root = tree.getroot()
        diag = root.find(".//{http://java.sun.com/xml/ns/javaee}context-root")
        nombreWar = diag.text
        return nombreWar
    except ValueError:
        print("An exception occurred: obtenerNombreProyecto: ", ValueError)
    return path

def buscarPropiedadInXml(ruta,prop,valor):
    encontrado = False
    try:
        tree = etree.parse(ruta)
        root = tree.getroot()
        diag = root.find("./*[@"+prop+"='"+valor+"']")
        if(diag != None):
            return True
    except Exception as e:
        print("An exception occurred: obtenerNombreProyectoWar",e)
    return encontrado


def setup_embedded_git():
    if hasattr(sys, '_MEIPASS'):
        # Cuando la aplicación es ejecutada por PyInstaller
        print("Paso por aqui")
        git_base_path = os.path.join(sys._MEIPASS, "embedded_git")
    else:
        # Cuando ejecutas en local
        git_base_path = os.path.join(os.path.dirname(__file__), "embedded_git")
    

    git_bin_path = os.path.join(git_base_path, "bin")
    git_executable_path = os.path.join(git_bin_path, "git.exe")
    os.environ["PATH"] = git_bin_path + os.pathsep + git_executable_path + os.pathsep + os.environ["PATH"]
    

def check_git_path():
    git_path = os.path.join(os.path.dirname(__file__), "embedded_git", "bin", "git.exe")
    if os.path.exists(git_path):
        print(f"Git found at: {git_path}")
    else:
        print("Git not found in the expected location")


def run_git_directly():
    git_path = os.path.join(os.path.dirname(__file__), "embedded_git", "bin", "git.exe")
    result = subprocess.run([git_path, "--version"], capture_output=True, text=True)
    print(result.stdout)

def run_plumbum_git():
    git_path = os.path.join(os.path.dirname(__file__), "embedded_git", "bin", "git.exe")
    git = local[git_path]  # Usar la ruta completa de Git embebido
    result = git("--version")
    print(result)