@echo off
set templates_dir=%cd%/templates
set QT_DIR=D:\AppServ\Qt\Qt5.14.2\5.14.2\android\src\android\templates

if not exist "%QT_DIR%" (
    mkdir "%QT_DIR%"
)

xcopy "%templates_dir%\*" "%QT_DIR%\" /f /s /e /y
