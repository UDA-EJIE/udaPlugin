import unittest
from unittest.mock import MagicMock, patch, call,Mock
from plugin.paso2 import initPaso2
from datetime import datetime
import logging
from copier import Worker

class TestInitPaso2(unittest.TestCase):

    @patch("plugin.paso2.Worker")
    @patch("logging.info")
    @patch("plugin.paso2.getColumnsDates")
    @patch("plugin.paso2.snakeToCamel")
    def test_initPaso2_basic_execution(self, mock_snakeToCamel, mock_getColumnsDates, mock_logging, mock_worker):
        """Test initPaso2 basic functionality and file generation process."""
        
        # Setup sample data
        tables = getTableDatosPadreHijo()
        yaml_data = {"project_name": "uuu",
                     "security_app":"",
            "PACKAGE_NAME":"",         
            "war_project_name": "ttt",
            "directorio_actual": "C:/aplic/copier/udaPlugin/templates/generateCode/",
            "destinoApp": "C:/aplic/copier/test/uuuEARClasses",
            "destinoWar": "C:/aplic/copier/test/uuutttWar"
                     }


        self.ventanaPaso2 = Mock()
        self.ventanaPaso2.master.update_progress = Mock()
        self.ventanaPaso2.archivoClases = 'uuuEARClasses'
        self.ventanaPaso2.archivoWar = 'uuutttWar'
        
        # Mock the tkinter variables that are used in the ventanaPaso2
        self.ventanaPaso2.controladores_var = MagicMock()
        self.ventanaPaso2.servicios_var = MagicMock()
        self.ventanaPaso2.daos_var = MagicMock()
        self.ventanaPaso2.modelo_datos_var = MagicMock()

        # Execute initPaso2
        initPaso2(tables, yaml_data, self.ventanaPaso2)

        # Assertions to verify directory paths and data handling
        #modelo
        mock_worker.assert_called_with(
            src_path='C:/aplic/copier/udaPlugin/templates/generateCode/model/',
            dst_path='C:/aplic/copier/test/uuuEARClasses/src/com/ejie/uuu/model',
            data=unittest.mock.ANY,  # `ANY` acts as a wildcard, allowing any value for `data`
            exclude=['*model*'],
            overwrite=True
        )
               

        # Check if columns were processed correctly
       
        self.assertEqual(mock_getColumnsDates.call_count, 7)
        self.assertEqual(mock_snakeToCamel.call_count, 4)# 2 por 2 tablas

    @patch("plugin.paso2.writeConfig")
    def test_config_written_once(self, mock_writeConfig):
        """Test that writeConfig is called with expected arguments."""
        
        # Sample input data
        tables = getTableDatosPadreHijo()
        yaml_data = {"project_name": "uuu",
                     "security_app":"",
            "PACKAGE_NAME":"",         
            "war_project_name": "ttt",
            "directorio_actual": "C:/aplic/copier/udaPlugin/templates/generateCode/",
            "destinoApp": "C:/aplic/copier/test/uuuEARClasses",
            "destinoWar": "C:/aplic/copier/test/uuutttWar"
                     }

        self.ventanaPaso2 = Mock()
        self.ventanaPaso2.master.update_progress = Mock()
        self.ventanaPaso2.archivoClases = 'uuuEARClasses'
        self.ventanaPaso2.archivoWar = 'uuutttWar'
        
        # Mock the tkinter variables that are used in the ventanaPaso2
        self.ventanaPaso2.controladores_var = MagicMock()
        self.ventanaPaso2.servicios_var = MagicMock()
        self.ventanaPaso2.daos_var = MagicMock()
        self.ventanaPaso2.modelo_datos_var = MagicMock()

        # Run function
        initPaso2(tables, yaml_data,self.ventanaPaso2 )
        
        # Assert writeConfig was called twice
        self.assertEqual(mock_writeConfig.call_count, 6)# 2 por cada tabla(2) y 2 finales
        mock_writeConfig.assert_any_call('RUTA', {'ruta_war': 'C:/aplic/copier/test'})
        mock_writeConfig.assert_any_call('RUTA', {'ruta_ultimo_proyecto': 'C:/aplic/copier/test'})
        mock_writeConfig.assert_any_call('RUTA', {'ruta_classes': 'C:/aplic/copier/test'})

    @patch("plugin.paso2.logging")
    def test_logging_output(self, mock_logging):
        """Test that logging outputs expected messages."""
        
        # Sample input data
        tables = getTableDatosPadreHijo()
        yaml_data = {"project_name": "uuu",
                     "security_app":"",
            "PACKAGE_NAME":"",         
            "war_project_name": "ttt",
            "directorio_actual": "C:/aplic/copier/udaPlugin/templates/generateCode/",
            "destinoApp": "C:/aplic/copier/test/uuuEARClasses",
            "destinoWar": "C:/aplic/copier/test/uuutttWar"
                     }
        
        self.ventanaPaso2 = Mock()
        self.ventanaPaso2.master.update_progress = Mock()
        self.ventanaPaso2.archivoClases = 'uuuEARClasses'
        self.ventanaPaso2.archivoWar = 'uuutttWar'
        
        # Mock the tkinter variables that are used in the ventanaPaso2
        self.ventanaPaso2.controladores_var = MagicMock()
        self.ventanaPaso2.servicios_var = MagicMock()
        self.ventanaPaso2.daos_var = MagicMock()
        self.ventanaPaso2.modelo_datos_var = MagicMock()

        # Run function
        initPaso2(tables, yaml_data, self.ventanaPaso2)
        
        # Verify logging calls
        mock_logging.info.assert_any_call('Final: paso 2 creado')

def getTableDatosPadreHijo():
    tables = entities = [
    {
        "name": "COMARCA",
        "columns": [
            {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "COMARCA"},
            {"name": "CODE_AYUNTAMIENTO", "type": "NUMBER", "dataPrecision": 30, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "CODE_PROVINCIA", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "R", "tableName": "COMARCA"},
            {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "PROVINCIA", "type": "PROVINCIA", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": " ", "tableName": "PROVINCIA"}
        ],
        "original_table": "COMARCA",
        "dao": [
            {
                "entidadPadre": "Provincia",
                "primaryKey": "Code",
                "entidadPadreCol": [
                    {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "PROVINCIA"},
                    {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
                    {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
                    {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"}
                ],
                "foreingkey": "CODE_PROVINCIA",
                "primaryPadre": "CODE"
            }
        ],
        "rowMapper": [
            {
                "entidadPadre": "Provincia",
                "primaryKey": "Code",
                "entidadPadreCol": [
                    {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "PROVINCIA"},
                    {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
                    {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
                    {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"}
                ],
                "foreingkey": "CODE_PROVINCIA",
                "primaryPadre": "CODE"
            }
        ],
        "originalCol": [
            {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "COMARCA"},
            {"name": "CODE_AYUNTAMIENTO", "type": "NUMBER", "dataPrecision": 30, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"}
        ],
        "columnasOriNoForeing": [
            {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "COMARCA"},
            {"name": "CODE_AYUNTAMIENTO", "type": "NUMBER", "dataPrecision": 30, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"}
        ],
        "columnasDao": [
            {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "COMARCA"},
            {"name": "CODE_AYUNTAMIENTO", "type": "NUMBER", "dataPrecision": 30, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "CODE_PROVINCIA", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "R", "tableName": "COMARCA"},
            {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"},
            {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "COMARCA"}
        ],
        "controller": None
    },
    {
        "name": "PROVINCIA",
        "columns": [
            {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "PROVINCIA"},
            {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
            {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
            {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
            {"name": "comarca", "type": "LIST", "entidad": "Comarca", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": " ", "tableName": "COMARCA"}
        ],
        "original_table": "PROVINCIA",
        "columnasDao": [
            {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "PROVINCIA"},
            {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
            {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
            {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"}
        ],
        "originalCol": [
            {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "PROVINCIA"},
            {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
            {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
            {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"}
        ],
        "columnasOriNoForeing": [
            {"name": "CODE", "type": "NUMBER", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "N", "primaryKey": "P", "tableName": "PROVINCIA"},
            {"name": "CSS", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
            {"name": "DESC_ES", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"},
            {"name": "DESC_EU", "type": "VARCHAR2", "dataPrecision": None, "datoImport": None, "datoType": None, "nullable": "Y", "primaryKey": "", "tableName": "PROVINCIA"}
        ],
        "dao": None,
        "controller": None
    }
]

    return tables
if __name__ == "__main__":
    unittest.main()
