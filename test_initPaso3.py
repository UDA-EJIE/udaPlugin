import unittest
from unittest.mock import MagicMock, patch, call,Mock
from plugin.paso3 import initPaso3, calcularOrden
from datetime import datetime
import logging
from copier import Worker

class TestInitPaso3(unittest.TestCase):

    @patch("plugin.paso3.Worker")
    @patch("logging.info")
    @patch("plugin.paso3.getColumnsDates")
    @patch("plugin.paso3.snakeToCamel")
    def test_initPaso3_basic_execution(self, mock_snakeToCamel, mock_getColumnsDates, mock_logging, mock_worker):
        """Test initPaso3 basic functionality and file generation process."""
        
        # Setup sample data
        tables = [{"name": "sample_table",
                    "columns" : [
                    {"tableName":"users", "name":"id", "type":"INT", "nullable":False, "primaryKey":"P", 
                        "datoType":"INTEGER", "datoImport":"User ID", "dataPrecision":None},
                        {
            "tableName": "users",
            "name": "username",
            "type": "VARCHAR",
            "nullable": False,
            "primaryKey": "",
            "datoType": "STRING",
            "datoImport": "User Name",
            "dataPrecision": 255
        },
        {
            "tableName": "users",
            "name": "email",
            "type": "VARCHAR",
            "nullable": True,
            "primaryKey": "",
            "datoType": "STRING",
            "datoImport": "User Email",
            "dataPrecision": 255
        },
        {
            "tableName": "users",
            "name": "order_id",
            "type": "INT",
            "nullable": False,
            "primaryKey": "P",
            "datoType": "INTEGER",
            "datoImport": "Order ID",
            "dataPrecision": None
        },
        {
            "tableName": "users",
            "name": "user_id",
            "type": "INT",
            "nullable": False,
            "primaryKey": "",
            "datoType": "INTEGER",
            "datoImport": "User ID",
            "dataPrecision": None
        }
                ]
                    }]
        yaml_data = {
            "project_name": "uuu",
            "war_project_name": "uuutttWar",
            "directorio_actual": "C:/aplic/copier/udaPlugin/templates/generateCode/",
            "destinoApp": "C:/aplic/copier/test/uuutttWar"
        }
        data_mantenimiento = {
            "isMaint": True,
            "tipoMantenimiento": "Edición en línea",
            "requestData": True,
            "saveButton": True,
            "buttons": True,
            "contextMenu": True,
            "filter": True,
            "search": True,
            "clientValidation": True,
            "multiselection": True,
            "loadOnStartUp": True,
            "sord": "asc",
            "sidx": 0,
            "titulo_mantenimiento": "Titulo Test",
            "nombre_mantenimiento": "Nombre Test",
            "alias": "sample_alias",
            "urlBase": "/url/base"
        }
        columnsOriginal = ["id", "username","email","order_id","user_id"]

        # Mock return values
        #mock_getColumnsDates.return_value = ([{"name": "col1"}, {"name": "col2"}], [{"name": "col1"}])
        #mock_snakeToCamel.side_effect = lambda x: x.capitalize()  # Convert first letter to uppercase

        # Execute initPaso3
        self.ventanaPaso3 = Mock()
        initPaso3(tables, yaml_data, data_mantenimiento, columnsOriginal,self.ventanaPaso3)

        # Assertions to verify directory paths and data handling
        mock_worker.assert_called_with(
            src_path='C:/aplic/copier/udaPlugin/templates/generateCode/maint/',
            dst_path='C:/aplic/copier/test/uuuStatics/WebContent/uuu/scripts/uuuttt/',
            data=unittest.mock.ANY,  # `ANY` acts as a wildcard, allowing any value for `data`
            exclude=['*.jsp', 'includes'],
            overwrite=True
        )
               

        # Check if columns were processed correctly
        mock_getColumnsDates.assert_called_once_with(tables[0]["columns"])
        self.assertEqual(mock_snakeToCamel.call_count, 3)

    def test_calcularOrden(self):
        """Test calcularOrden with specific columns."""
        columns = ["col1", "col2", "col3"]
        columsSelected = [{"name": "col1"}, {"name": "col2"}, {"name": "col3"}]
        
        pos = 1  # Position to find
        orden = calcularOrden(pos, columns, columsSelected)
        
        # Verifies correct order index is returned
        self.assertEqual(orden, 1)  # Should match the position index

    @patch("plugin.paso3.writeConfig")
    def test_config_written_once(self, mock_writeConfig):
        """Test that writeConfig is called with expected arguments."""
        
        # Sample input data
        tables = [{"name": "table1", # Creating instances of the Column class                
                "columns" : [
                    {"tableName":"users", "name":"id", "type":"INT", "nullable":False, "primaryKey":"P", 
                        "datoType":"INTEGER", "datoImport":"User ID", "dataPrecision":None},
                        {
            "tableName": "users",
            "name": "username",
            "type": "VARCHAR",
            "nullable": False,
            "primaryKey": "",
            "datoType": "STRING",
            "datoImport": "User Name",
            "dataPrecision": 255
        },
        {
            "tableName": "users",
            "name": "email",
            "type": "VARCHAR",
            "nullable": True,
            "primaryKey": "",
            "datoType": "STRING",
            "datoImport": "User Email",
            "dataPrecision": 255
        },
        {
            "tableName": "users",
            "name": "order_id",
            "type": "INT",
            "nullable": False,
            "primaryKey": "P",
            "datoType": "INTEGER",
            "datoImport": "Order ID",
            "dataPrecision": None
        },
        {
            "tableName": "users",
            "name": "user_id",
            "type": "INT",
            "nullable": False,
            "primaryKey": "",
            "datoType": "INTEGER",
            "datoImport": "User ID",
            "dataPrecision": None
        }
                ]}]
        yaml_data = {
            "project_name": "uuu",
            "war_project_name": "uuutttWar",
            "directorio_actual": "C:/aplic/copier/udaPlugin/templates/generateCode/",
            "destinoApp": "C:/aplic/copier/test/uuutttWar"
        }
        data_mantenimiento = {
            "isMaint": False, "tipoMantenimiento": "Edición en línea", "requestData": True,
            "saveButton": True, "buttons": True, "contextMenu": True, "filter": True,
            "search": True, "clientValidation": True, "multiselection": True,
            "loadOnStartUp": True, "sord": "asc", "sidx": 0,
            "titulo_mantenimiento": "Test Title", "nombre_mantenimiento": "Test Name", "alias": "test_alias", "urlBase": "/base/url"
        }
        columnsOriginal = ["id", "username","email","order_id","user_id"]

        # Run function
        self.ventanaPaso3 = Mock()
        initPaso3(tables, yaml_data, data_mantenimiento, columnsOriginal,self.ventanaPaso3)
        
        # Assert writeConfig was called twice
        self.assertEqual(mock_writeConfig.call_count, 2)
        mock_writeConfig.assert_any_call('RUTA', {'ruta_war': 'C:/aplic/copier/test/'})
        mock_writeConfig.assert_any_call('RUTA', {'ruta_ultimo_proyecto': 'C:/aplic/copier/test/'})

    @patch("plugin.paso3.logging")
    def test_logging_output(self, mock_logging):
        """Test that logging outputs expected messages."""
        
        # Sample input data
        tables = [{"name": "log_table", "columns" : [
                    {"tableName":"users", "name":"id", "type":"INT", "nullable":False, "primaryKey":"P", 
                        "datoType":"INTEGER", "datoImport":"User ID", "dataPrecision":None},
                        {
            "tableName": "users",
            "name": "username",
            "type": "VARCHAR",
            "nullable": False,
            "primaryKey": "",
            "datoType": "STRING",
            "datoImport": "User Name",
            "dataPrecision": 255
        },
        {
            "tableName": "users",
            "name": "email",
            "type": "VARCHAR",
            "nullable": True,
            "primaryKey": "",
            "datoType": "STRING",
            "datoImport": "User Email",
            "dataPrecision": 255
        },
        {
            "tableName": "orders",
            "name": "order_id",
            "type": "INT",
            "nullable": False,
            "primaryKey": "P",
            "datoType": "INTEGER",
            "datoImport": "Order ID",
            "dataPrecision": None
        },
        {
            "tableName": "orders",
            "name": "user_id",
            "type": "INT",
            "nullable": False,
            "primaryKey": "",
            "datoType": "INTEGER",
            "datoImport": "User ID",
            "dataPrecision": None
        }
                ]}]
        yaml_data = {"project_name": "uuu",
            "war_project_name": "uuutttWar",
            "directorio_actual": "C:/aplic/copier/udaPlugin/templates/generateCode/",
            "destinoApp": "C:/aplic/copier/test/uuutttWar"
                     }
        data_mantenimiento = {"isMaint": True, "tipoMantenimiento": "Edición en línea", "requestData": True,
                              "saveButton": True, "buttons": True, "contextMenu": True, "filter": True,
                              "search": True, "clientValidation": True, "multiselection": True,
                              "loadOnStartUp": True, "sord": "asc", "sidx": 0,
                              "titulo_mantenimiento": "Log Title", "nombre_mantenimiento": "Log Name", "alias": "log_alias", "urlBase": "/log/base"}
        columnsOriginal = ["id", "data"]

        # Run function
        self.ventanaPaso3 = Mock()
        initPaso3(tables, yaml_data, data_mantenimiento, columnsOriginal,self.ventanaPaso3)
        
        # Verify logging calls
        mock_logging.info.assert_any_call('Fin mantenimento: LogTable')
        mock_logging.info.assert_any_call('Final: paso 3 creado')

if __name__ == "__main__":
    unittest.main()
