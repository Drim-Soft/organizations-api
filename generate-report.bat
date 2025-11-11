@echo off
REM Script para generar reporte HTML de pruebas en Windows
REM Ejecuta las pruebas de Maven y genera un dashboard HTML

echo ========================================
echo Generador de Reporte de Pruebas
echo ========================================
echo.

REM Verificar si Python está instalado
python --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Python no está instalado o no está en el PATH
    echo Por favor instala Python 3.6 o superior
    pause
    exit /b 1
)

echo [1/3] Ejecutando pruebas de Maven...
call mvnw.cmd test
if errorlevel 1 (
    echo [ADVERTENCIA] Algunas pruebas fallaron, pero continuando con la generacion del reporte...
)

echo.
echo [2/3] Generando dashboard HTML...
python generate_test_report.py
if errorlevel 1 (
    echo [ERROR] No se pudo generar el reporte
    pause
    exit /b 1
)

echo.
echo [3/3] Abriendo reporte en el navegador...
if exist test-report.html (
    start test-report.html
    echo.
    echo ========================================
    echo Reporte generado exitosamente!
    echo Archivo: test-report.html
    echo ========================================
) else (
    echo [ERROR] El archivo test-report.html no se genero
    pause
    exit /b 1
)

pause

