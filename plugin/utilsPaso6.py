import subprocess
import os
import zipfile
import re

def list_classes_in_jar(jar_path):
    """
    Lista todas las clases en un archivo .jar
    """
    classes = []
    with zipfile.ZipFile(jar_path, 'r') as jar:
        for file in jar.namelist():
            if file.endswith('.class'):
                classes.append(file.replace('/', '.').replace('.class', ''))
    return classes

def get_methods_from_class(jar_path, class_name):
    """
    Obtiene los métodos de una clase específica dentro del .jar usando `javap`.
    """
    try:
        # Comando de `javap` para obtener la información de los métodos
        result = subprocess.run(['javap', '-classpath', jar_path, class_name], 
                                capture_output=True, text=True)

        if result.returncode != 0:
            print(f"Error al ejecutar javap: {result.stderr}")
            return []

        # Procesar la salida para obtener solo los métodos
        methods = []
        lines = result.stdout.splitlines()
        for line in lines:
            line = line.strip()
            if line.endswith(';') and not line.startswith('Compiled'):
                methods.append(line)

        return methods

    except Exception as e:
        print(f"Error al obtener los métodos: {e}")
        return []

def already_exists(resultado, subst):
    return subst in resultado

def get_parameters_skeleton(cadena, imports, is_jpa):
    resultado = []
    try:
        if cadena and cadena.strip():  # Verificar que la cadena no esté vacía
            auxiliar = cadena
            while ";" in cadena and cadena:
                parameter = auxiliar[:auxiliar.index(";")]
                parameter_remp = parameter.replace("class ", "")

                # FIX
                if imports:
                    parameter_remp = parameter_remp.replace("[]", "").strip()
                    if is_avoidable(parameter_remp):
                        # Actualizar cadena y auxiliar para el siguiente ciclo
                        cadena = auxiliar[auxiliar.index(";") + 1:]
                        auxiliar = cadena
                        continue

                parametro_cambio = parameter_remp
                object_type = parametro_cambio.split(".")[-1]

                if is_jpa and parameter_remp.strip().startswith("com.ejie") and not parameter_remp.strip().startswith("com.ejie.x38"):
                    parametro_cambio = (
                        f"{parameter_remp[:parameter_remp.rindex('.')]}."
                        f"dto.{parameter_remp.split('.')[-1]}Dto"
                    )
                    object_type = parametro_cambio.split(".")[-1]

                if imports:
                    resultado.append(parametro_cambio)
                else:
                    resultado.append(object_type)

                # Actualizar cadena y auxiliar para el siguiente ciclo
                cadena = auxiliar[auxiliar.index(";") + 1:]
                auxiliar = cadena

    except Exception as e:
        print(f"Error: {e}")  # Imprimir el error en lugar de ignorarlo

    return resultado

def is_avoidable(retorno):
    # Lógica que define si el retorno es evitable, por ahora un placeholder.
    return retorno.lower() == 'void'

def generate_parameter_imports(lista_metodos, is_jpa):
    resultado = []

    try:
        # Iterar sobre listaMetodos
        for cadena_met_arr in lista_metodos:
            # Convertir a cadena y dividir
            cadena_met = ','.join(cadena_met_arr)
            cadena_met_aux = cadena_met.split(',')

            # Param
            clase = cadena_met_aux[3].strip()
            if clase.endswith(']'):
                clase = clase[:-1]

            # Excep
            clase += cadena_met_aux[5].strip()[:-1]
            lista_aux = get_parameters_skeleton(clase, True, is_jpa)

            # Parámetros del método
            for subst in lista_aux:
                # Condición comentada relacionada con isJpa
                # if not is_jpa and ".dto." in subst:
                #     subst = StubClassUtils.replaceDto(subst)

                if not already_exists(resultado, subst):
                    resultado.append(subst)

            # Parámetros del retorno
            retorno = cadena_met_aux[1].strip()
            if is_avoidable(retorno):
                continue

            retorno_final = retorno
            if is_jpa and retorno.startswith("com.ejie") and not retorno.startswith("com.ejie.x38"):
                retorno_final = retorno + "Dto"

            # Condición comentada relacionada con isJpa
            # if not is_jpa and ".dto." in retorno_final:
            #     retorno_final = StubClassUtils.replaceDto(retorno_final)

            if '.' in retorno_final and not retorno_final.upper().startswith("JAVA.LANG.") and retorno_final != 'void':
                if not already_exists(resultado, retorno_final):
                    resultado.append(retorno)
    except Exception as e:
        print(e)  # Imprimir el error en lugar de solo obtener el stacktrace
    finally:
        return resultado
    
def replace_dto(field):
    resultado = ""
    # Verifica si la cadena termina con 'Dto'
    if field.endswith("Dto"):
        # Reemplaza '.dto.' por '.'
        res = field.replace(".dto.", ".")
        # Elimina la última ocurrencia de 'Dto'
        resultado = res[:res.rfind("Dto")]
    else:
        resultado = field

    return resultado  

def extract_java_types(method_declaration):
    # Expresión regular para capturar el tipo de retorno, nombre del método y los tipos de parámetros
    pattern = r"(\w+(?:<[\w\s,<>]+>)?)\s+(\w+)\((.*?)\)"
    match = re.search(pattern, method_declaration)

    if match:
        return_type = match.group(1)
        method_name = match.group(2)
        params = match.group(3)

        # Separar los tipos de parámetros si hay varios
        param_types = [p.split()[0] for p in params.split(",") if p]

        return method_name,return_type, param_types
    else:
        return None, None  