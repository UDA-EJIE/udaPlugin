# mi_proyecto.spec

# -*- mode: python ; coding: utf-8 -*-

block_cipher = None

a = Analysis(
    ['menuPrincipal.py'],
    pathex=['.'],
    binaries=[],
    datas=[
        ('Column.py', '.'),
        ('interfazTkinter.py', '.'),
        ('Table.py', '.'),
        ('ventanaPaso2.py', '.'),
        ('ventanaPaso3.py', '.'),
        ('templates/generateCode/controller/*', 'templates/generateCode/controller'),
        ('templates/generateCode/dao/*', 'templates/generateCode/dao'),
        ('templates/generateCode/maint/', 'templates/generateCode/maint'),
        ('templates/generateCode/maint/includes/*', 'templates/generateCode/maint/includes'),
        ('templates/generateCode/model/*', 'templates/generateCode/model'),
        ('templates/generateCode/service/*', 'templates/generateCode/service'),
		('instantclient_21_12/*', 'instantclient_21_12'),
        ('logs/*', 'logs'),
        ('plugin/*', 'plugin'),
        ('plugin/images/*', 'plugin/images'),
        ('templates/proyecto/', 'templates/proyecto'),
        ('templates/proyecto/{{project_name}}Config/', 'templates/proyecto/{{project_name}}Config'),
        ('templates/proyecto/{{project_name}}EAR/', 'templates/proyecto/{{project_name}}EAR'),
        ('templates/proyecto/{{project_name}}EARClasses/', 'templates/proyecto/{{project_name}}EARClasses'),
        ('templates/proyecto/{{project_name}}Statics/', 'templates/proyecto/{{project_name}}Statics'),
        ('templates/proyecto/{{project_name}}{{ejb_project_name}}EJB/', 'templates/proyecto/{{project_name}}{{ejb_project_name}}EJB'),
        ('templates/proyecto/{{project_name}}{{war_project_name}}War/', 'templates/proyecto/{{project_name}}{{war_project_name}}War'),
        # Añade más rutas si es necesario
    ],
    hiddenimports=[
        'jinja2_ansible_filters',
        # Añade otras dependencias necesarias aquí
    ],
    hookspath=[],
    runtime_hooks=[],
    excludes=[],
    win_no_prefer_redirects=False,
    win_private_assemblies=False,
    cipher=block_cipher,
    noarchive=False,
)

pyz = PYZ(a.pure, a.zipped_data, cipher=block_cipher)

exe = EXE(
    pyz,
    a.scripts,
    [],
    exclude_binaries=True,
    name='UDA_PLUGIN',
    debug=False,
    bootloader_ignore_signals=False,
    strip=False,
    upx=True,
    upx_exclude=[],
    runtime_tmpdir=None,
    console=True,
)

coll = COLLECT(
    exe,
    a.binaries,
    a.zipfiles,
    a.datas,
    strip=False,
    upx=True,
    upx_exclude=[],
    name='copierUDA',
)
