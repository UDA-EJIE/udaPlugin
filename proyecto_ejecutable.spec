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
        ('ventanaPaso2Buena.py', '.'),
        ('ventanaPaso3.py', '.'),
        ('generateCode/controller/*', 'generateCode/controller'),
        ('generateCode/dao/*', 'generateCode/dao'),
        ('generateCode/maint/', 'generateCode/maint'),
        ('generateCode/maint/includes/*', 'generateCode/maint/includes'),
        ('generateCode/model/*', 'generateCode/model'),
        ('generateCode/service/*', 'generateCode/service'),
		('instantclient_21_12/*', 'instantclient_21_12'),
        ('plugin/*', 'plugin'),
        ('plugin/images/*', 'plugin/images'),
        ('proyecto/', 'proyecto'),
        ('proyecto/{{project_name}}Config/', 'proyecto/{{project_name}}Config'),
        ('proyecto/{{project_name}}EAR/', 'proyecto/{{project_name}}EAR'),
        ('proyecto/{{project_name}}EARClasses/', 'proyecto/{{project_name}}EARClasses'),
        ('proyecto/{{project_name}}Statics/', 'proyecto/{{project_name}}Statics'),
        ('proyecto/{{project_name}}{{ejb_project_name}}EJB/', 'proyecto/{{project_name}}{{ejb_project_name}}EJB'),
        ('proyecto/{{project_name}}{{war_project_name}}War/', 'proyecto/{{project_name}}{{war_project_name}}War'),
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
    name='mi_proyecto',
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
