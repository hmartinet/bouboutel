@echo off
setlocal

cd /d "%~dp0"

set "JAVA_PATH=java"
set "JAR=Bouboutel.jar"
set "UPDATE=Bouboutel-UPDATE.jar"

:loop
if exist "%UPDATE%" (
    move /y "%UPDATE%" "%JAR%" >nul
    if errorlevel 1 (
        echo Failed to install update.
        exit /b 1
    )
)

"%JAVA_PATH%" -jar "%JAR%"
set "RC=%ERRORLEVEL%"

if "%RC%"=="10" goto loop
exit /b %RC%
