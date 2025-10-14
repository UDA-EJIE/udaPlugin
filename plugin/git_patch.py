"""
Parche para forzar a Copier/plumbum a usar Git embebido
"""
import os
import sys

def patch_git_for_copier():
    """Parchea las librerías para usar Git embebido"""
    
    # Detectar ruta del Git embebido
    if getattr(sys, 'frozen', False):
        base_path = sys._MEIPASS
    else:
        base_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    
    git_exe_path = os.path.join(base_path, 'plugin', 'embedded_git', 'bin', 'git.exe')
    
    if not os.path.exists(git_exe_path):
        print(f"   ❌ Git embebido no encontrado: {git_exe_path}")
        return False
    
    try:
        # Importar plumbum
        from plumbum import local
        from plumbum.machines import local as local_machine
        
        # Método 1: Parchear el método which de la clase LocalMachine
        original_which = local_machine.LocalMachine.which
        
        def patched_which(self, progname):
            """Método parcheado que devuelve nuestro git"""
            if progname == 'git':
                return git_exe_path
            return original_which(self, progname)
        
        # Aplicar el parche
        local_machine.LocalMachine.which = patched_which
        
        # Método 2: También parchear el __getitem__ del objeto local
        original_getitem = local.__class__.__getitem__
        
        def patched_getitem(self, name):
            if name == 'git':
                from plumbum.commands import local as local_cmd
                return local_cmd[git_exe_path]
            return original_getitem(self, name)
        
        local.__class__.__getitem__ = patched_getitem
        
        # Limpiar cache si existe
        if hasattr(local, '_path_cache'):
            local._path_cache.clear()
        if hasattr(local, '_which_cache'):
            local._which_cache.clear()
            
        # Verificar que funciona
        test_git = local['git']
        print(f"   ✅ Plumbum parcheado exitosamente: {test_git}")
        return True
        
    except Exception as e:
        print(f"   ❌ Error parcheando plumbum: {e}")
        
        # PLAN B: Forzar mediante variables de entorno
        os.environ['GIT_EXECUTABLE'] = git_exe_path
        os.environ['GIT_PYTHON_GIT_EXECUTABLE'] = git_exe_path
        
        # También intentar con copier específicamente
        try:
            import copier.vcs
            # Parchear directamente la función get_git de copier
            original_get_git = copier.vcs.get_git
            
            def patched_get_git():
                from plumbum.commands import local
                return local[git_exe_path]
            
            copier.vcs.get_git = patched_get_git
            print(f"   ✅ Copier.vcs parcheado directamente")
            return True
            
        except Exception as e2:
            print(f"   ❌ Error parcheando copier: {e2}")
            return False
