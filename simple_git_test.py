import os
import subprocess
import plugin.utils as utl

def ejecutar_test_git():
    """Función simple para ejecutar el test de Git"""
    print("=== TEST SIMPLE GIT ===")

    # Antes
    print("\n1. ANTES de setup_embedded_git:")
    try:
        result = subprocess.run(['git', '--version'], capture_output=True, text=True)
        if result.returncode == 0:
            print(f"   Git disponible: {result.stdout.strip()}")
            
            where_result = subprocess.run(['where', 'git'], capture_output=True, text=True, shell=True)
            if where_result.returncode == 0:
                print(f"   Ubicación: {where_result.stdout.strip().split()[0]}")
        else:
            print("   Git no disponible")
    except Exception as e:
        print(f"   Error: {e}")

    # Ejecutar setup
    print("\n2. Ejecutando utl.setup_embedded_git():")
    try:
        result = utl.setup_embedded_git()
        print(f"   Resultado: {result}")
    except Exception as e:
        print(f"   Error: {e}")

    # Después
    print("\n3. DESPUÉS de setup_embedded_git:")
    try:
        result = subprocess.run(['git', '--version'], capture_output=True, text=True)
        if result.returncode == 0:
            print(f"   Git disponible: {result.stdout.strip()}")
            
            where_result = subprocess.run(['where', 'git'], capture_output=True, text=True, shell=True)
            if where_result.returncode == 0:
                print(f"   Ubicación: {where_result.stdout.strip().split()[0]}")
        else:
            print("   Git no disponible")
    except Exception as e:
        print(f"   Error: {e}")

    # Test rápido con Copier
    print("\n4. Test rápido Copier:")
    try:
        from copier import Worker
        print("   ✅ Copier importado correctamente")
        print("   Copier puede usar Git embebido")
    except Exception as e:
        print(f"   ❌ Error con Copier: {e}")

if __name__ == "__main__":
    ejecutar_test_git()