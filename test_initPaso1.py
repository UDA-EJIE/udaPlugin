import unittest
from unittest.mock import MagicMock, patch, Mock
import VentanaPaso1 as paso1
import os

class TestSaveToYAML(unittest.TestCase):
    @patch('VentanaPaso1.Worker', wraps=paso1.Worker)  
    @patch('plugin.utils.writeConfig')
    @patch("VentanaPaso1.logging")
    @patch.object(paso1.Paso1, 'close_loading_frame', new=Mock())
    @patch.object(paso1.Paso1, 'update_progress', return_value=None)  # Mockear update_progress
    def test_save_to_yaml(self,mock_update_progress, mock_logging, mock_write_config, mock_worker):
        # Mock de Worker y su método run_copy
        mock_worker_instance = mock_worker.return_value
        mock_worker_instance.run_copy = MagicMock()

        # Crear una instancia de la clase a probar y configurar las variables necesarias
        instancia = paso1.Paso1(self)
        instancia.language_options = ["Castellano", "Euskera", "Inglés", "Francés"]
        instancia.language_vars = [MagicMock(get=lambda: True), MagicMock(get=lambda: True),
                                   MagicMock(get=lambda: False), MagicMock(get=lambda: False)]
        instancia.default_language_var = MagicMock(get=lambda: "Castellano")
        instancia.entry_code = MagicMock(get=lambda: "proyecto_test")
        instancia.security_var = MagicMock(get=lambda: "Si")
        instancia.entry_war = MagicMock(get=lambda: "war_test")
        instancia.security_yes_radio = MagicMock(get=lambda: True)
        instancia.default_language_combobox = MagicMock(get=lambda: "Castellano")
        instancia.entry_location = MagicMock(get=lambda: "C:/aplic/copier/test")
        
        # Ejecutar el método save_to_yaml
        instancia.save_to_yaml()

        mock_update_progress.assert_called()

        # Datos esperados para la verificación
        esperado_yaml_data = {
            "i18n_app": ["Castellano", "Euskera"],
            "i18n_default_app": "Castellano",
            "project_name": "proyecto_test",
            "security_app": "Si",
            "war_project_name": "war_test",
            "xlnets": True,
            "defaultLanguage": "es",
            "availableLangs": "es, eu"
        }
       
        # Verificar que Worker fue llamado con los parámetros correctos
        mock_worker.assert_called_once_with(
            src_path="c:\\aplic\\copier\\udaPlugin\\templates\\proyecto",
            overwrite=True,
            dst_path="C:/aplic/copier/test",
            data=esperado_yaml_data,
            exclude=['*i18n_en*', '*i18n_fr*', '*EJB']
        )

        # Verificar que run_copy no fue llamado por el mock
        self.assertEqual(mock_worker_instance.run_copy.called, False)

        # Verificar que writeConfig fue llamado con las rutas especificadas
        mock_write_config.assert_called_once_with(
            "RUTA", {
                "ruta_classes": "C:/aplic/copier/test",
                "ruta_war": "C:/aplic/copier/test",
                "ruta_ultimo_proyecto": "C:/aplic/copier/test"
            }
        )

        # Verificar los registros de logging
        mock_logging.info.assert_any_call('Inicio: Crear proyecto: proyecto_testwar_test')
        mock_logging.info.assert_any_call('Fin: Crear proyecto: proyecto_testwar_test')

if __name__ == "__main__":
    unittest.main(exit=False)
