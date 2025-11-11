#!/bin/bash
# Script para generar reporte HTML de pruebas en Linux/Mac
# Ejecuta las pruebas de Maven y genera un dashboard HTML

echo "========================================"
echo "Generador de Reporte de Pruebas"
echo "========================================"
echo ""

# Verificar si Python está instalado
if ! command -v python3 &> /dev/null; then
    if ! command -v python &> /dev/null; then
        echo "[ERROR] Python no está instalado o no está en el PATH"
        echo "Por favor instala Python 3.6 o superior"
        exit 1
    else
        PYTHON_CMD="python"
    fi
else
    PYTHON_CMD="python3"
fi

echo "[1/3] Ejecutando pruebas de Maven..."
./mvnw test
MAVEN_EXIT_CODE=$?

if [ $MAVEN_EXIT_CODE -ne 0 ]; then
    echo "[ADVERTENCIA] Algunas pruebas fallaron, pero continuando con la generación del reporte..."
fi

echo ""
echo "[2/3] Generando dashboard HTML..."
$PYTHON_CMD generate_test_report.py
PYTHON_EXIT_CODE=$?

if [ $PYTHON_EXIT_CODE -ne 0 ]; then
    echo "[ERROR] No se pudo generar el reporte"
    exit 1
fi

echo ""
echo "[3/3] Abriendo reporte en el navegador..."

if [ -f "test-report.html" ]; then
    # Intentar abrir en el navegador según el sistema operativo
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        if command -v xdg-open &> /dev/null; then
            xdg-open test-report.html
        elif command -v gnome-open &> /dev/null; then
            gnome-open test-report.html
        else
            echo "Por favor abre test-report.html manualmente en tu navegador"
        fi
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        open test-report.html
    else
        echo "Por favor abre test-report.html manualmente en tu navegador"
    fi
    
    echo ""
    echo "========================================"
    echo "Reporte generado exitosamente!"
    echo "Archivo: test-report.html"
    echo "========================================"
else
    echo "[ERROR] El archivo test-report.html no se generó"
    exit 1
fi

