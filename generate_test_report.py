#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Generador de Dashboard HTML para Reportes de Pruebas Maven Surefire
Lee los archivos XML de target/surefire-reports/ y genera un dashboard HTML visual
"""

import os
import xml.etree.ElementTree as ET
from datetime import datetime
from pathlib import Path
import json

def parse_surefire_xml(xml_file):
    """Parsea un archivo XML de Surefire y extrae la información de las pruebas"""
    try:
        tree = ET.parse(xml_file)
        root = tree.getroot()
        
        suite_name = root.get('name', 'Unknown')
        total_tests = int(root.get('tests', 0))
        errors = int(root.get('errors', 0))
        failures = int(root.get('failures', 0))
        skipped = int(root.get('skipped', 0))
        time = float(root.get('time', 0))
        
        passed = total_tests - errors - failures - skipped
        
        test_cases = []
        for testcase in root.findall('testcase'):
            test_name = testcase.get('name', 'Unknown')
            classname = testcase.get('classname', 'Unknown')
            test_time = float(testcase.get('time', 0))
            
            status = 'passed'
            error_message = ''
            
            if testcase.find('failure') is not None:
                status = 'failed'
                failure = testcase.find('failure')
                error_message = failure.get('message', '') or failure.text or ''
            elif testcase.find('error') is not None:
                status = 'error'
                error = testcase.find('error')
                error_message = error.get('message', '') or error.text or ''
            elif testcase.find('skipped') is not None:
                status = 'skipped'
            
            test_cases.append({
                'name': test_name,
                'classname': classname,
                'status': status,
                'time': test_time,
                'error': error_message
            })
        
        return {
            'suite_name': suite_name,
            'total_tests': total_tests,
            'passed': passed,
            'failed': failures,
            'errors': errors,
            'skipped': skipped,
            'time': time,
            'test_cases': test_cases
        }
    except Exception as e:
        print(f"Error parsing {xml_file}: {e}")
        return None

def generate_html_dashboard(suites_data, output_file='test-report.html'):
    """Genera un dashboard HTML visual con los datos de las pruebas"""
    
    # Calcular estadísticas totales
    total_tests = sum(s['total_tests'] for s in suites_data)
    total_passed = sum(s['passed'] for s in suites_data)
    total_failed = sum(s['failed'] for s in suites_data)
    total_errors = sum(s['errors'] for s in suites_data)
    total_skipped = sum(s['skipped'] for s in suites_data)
    total_time = sum(s['time'] for s in suites_data)
    
    success_rate = (total_passed / total_tests * 100) if total_tests > 0 else 0
    
    # Generar HTML
    html = f"""<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard de Pruebas - Reporte de Tests</title>
    <style>
        * {{
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }}
        
        body {{
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }}
        
        .container {{
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            overflow: hidden;
        }}
        
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }}
        
        .header h1 {{
            font-size: 2.5em;
            margin-bottom: 10px;
        }}
        
        .header .timestamp {{
            opacity: 0.9;
            font-size: 1.1em;
        }}
        
        .stats-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            padding: 30px;
            background: #f8f9fa;
        }}
        
        .stat-card {{
            background: white;
            padding: 25px;
            border-radius: 15px;
            text-align: center;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }}
        
        .stat-card:hover {{
            transform: translateY(-5px);
            box-shadow: 0 8px 12px rgba(0,0,0,0.15);
        }}
        
        .stat-card.passed {{
            border-top: 4px solid #28a745;
        }}
        
        .stat-card.failed {{
            border-top: 4px solid #dc3545;
        }}
        
        .stat-card.errors {{
            border-top: 4px solid #ffc107;
        }}
        
        .stat-card.skipped {{
            border-top: 4px solid #6c757d;
        }}
        
        .stat-card.total {{
            border-top: 4px solid #007bff;
        }}
        
        .stat-card.time {{
            border-top: 4px solid #17a2b8;
        }}
        
        .stat-value {{
            font-size: 2.5em;
            font-weight: bold;
            margin: 10px 0;
        }}
        
        .stat-label {{
            color: #6c757d;
            font-size: 0.9em;
            text-transform: uppercase;
            letter-spacing: 1px;
        }}
        
        .progress-bar {{
            width: 100%;
            height: 30px;
            background: #e9ecef;
            border-radius: 15px;
            overflow: hidden;
            margin: 20px 0;
        }}
        
        .progress-fill {{
            height: 100%;
            background: linear-gradient(90deg, #28a745, #20c997);
            transition: width 0.5s;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
        }}
        
        .suites-section {{
            padding: 30px;
        }}
        
        .suites-section h2 {{
            margin-bottom: 20px;
            color: #333;
        }}
        
        .suite-card {{
            background: #f8f9fa;
            border-radius: 15px;
            margin-bottom: 20px;
            overflow: hidden;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }}
        
        .suite-header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }}
        
        .suite-header:hover {{
            opacity: 0.9;
        }}
        
        .suite-header h3 {{
            font-size: 1.3em;
        }}
        
        .suite-stats {{
            display: flex;
            gap: 15px;
            font-size: 0.9em;
        }}
        
        .suite-content {{
            padding: 20px;
            display: none;
        }}
        
        .suite-content.active {{
            display: block;
        }}
        
        .test-list {{
            list-style: none;
        }}
        
        .test-item {{
            padding: 15px;
            margin: 10px 0;
            background: white;
            border-radius: 10px;
            border-left: 4px solid #ddd;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }}
        
        .test-item.passed {{
            border-left-color: #28a745;
        }}
        
        .test-item.failed {{
            border-left-color: #dc3545;
        }}
        
        .test-item.error {{
            border-left-color: #ffc107;
        }}
        
        .test-item.skipped {{
            border-left-color: #6c757d;
        }}
        
        .test-name {{
            font-weight: 500;
            color: #333;
        }}
        
        .test-time {{
            color: #6c757d;
            font-size: 0.9em;
        }}
        
        .test-status {{
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: bold;
            text-transform: uppercase;
        }}
        
        .test-status.passed {{
            background: #d4edda;
            color: #155724;
        }}
        
        .test-status.failed {{
            background: #f8d7da;
            color: #721c24;
        }}
        
        .test-status.error {{
            background: #fff3cd;
            color: #856404;
        }}
        
        .test-status.skipped {{
            background: #e2e3e5;
            color: #383d41;
        }}
        
        .error-details {{
            margin-top: 10px;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 5px;
            font-family: 'Courier New', monospace;
            font-size: 0.85em;
            color: #721c24;
            white-space: pre-wrap;
            word-break: break-word;
        }}
        
        .footer {{
            text-align: center;
            padding: 20px;
            color: #6c757d;
            background: #f8f9fa;
        }}
        
        @media (max-width: 768px) {{
            .stats-grid {{
                grid-template-columns: repeat(2, 1fr);
            }}
            
            .header h1 {{
                font-size: 1.8em;
            }}
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📊 Dashboard de Pruebas</h1>
            <div class="timestamp">Generado el {datetime.now().strftime('%d/%m/%Y %H:%M:%S')}</div>
        </div>
        
        <div class="stats-grid">
            <div class="stat-card total">
                <div class="stat-label">Total de Pruebas</div>
                <div class="stat-value">{total_tests}</div>
            </div>
            <div class="stat-card passed">
                <div class="stat-label">Exitosas</div>
                <div class="stat-value" style="color: #28a745;">{total_passed}</div>
            </div>
            <div class="stat-card failed">
                <div class="stat-label">Fallidas</div>
                <div class="stat-value" style="color: #dc3545;">{total_failed}</div>
            </div>
            <div class="stat-card errors">
                <div class="stat-label">Errores</div>
                <div class="stat-value" style="color: #ffc107;">{total_errors}</div>
            </div>
            <div class="stat-card skipped">
                <div class="stat-label">Omitidas</div>
                <div class="stat-value" style="color: #6c757d;">{total_skipped}</div>
            </div>
            <div class="stat-card time">
                <div class="stat-label">Tiempo Total</div>
                <div class="stat-value" style="color: #17a2b8;">{total_time:.2f}s</div>
            </div>
        </div>
        
        <div style="padding: 0 30px;">
            <div class="progress-bar">
                <div class="progress-fill" style="width: {success_rate}%;">
                    {success_rate:.1f}% Éxito
                </div>
            </div>
        </div>
        
        <div class="suites-section">
            <h2>📋 Suites de Pruebas ({len(suites_data)})</h2>
"""
    
    # Agregar cada suite
    for suite in suites_data:
        suite_success_rate = (suite['passed'] / suite['total_tests'] * 100) if suite['total_tests'] > 0 else 0
        suite_id = suite['suite_name'].replace(' ', '_').replace('.', '_')
        
        html += f"""
            <div class="suite-card">
                <div class="suite-header" onclick="toggleSuite('{suite_id}')">
                    <div>
                        <h3>{suite['suite_name']}</h3>
                        <div class="suite-stats">
                            <span>✅ {suite['passed']}</span>
                            <span>❌ {suite['failed']}</span>
                            <span>⚠️ {suite['errors']}</span>
                            <span>⏭️ {suite['skipped']}</span>
                            <span>⏱️ {suite['time']:.2f}s</span>
                        </div>
                    </div>
                    <div style="font-size: 1.5em;">▼</div>
                </div>
                <div class="suite-content" id="{suite_id}">
                    <ul class="test-list">
"""
        
        for test in suite['test_cases']:
            html += f"""
                        <li class="test-item {test['status']}">
                            <div>
                                <div class="test-name">{test['name']}</div>
                                <div class="test-time">{test['time']:.3f}s</div>
                                {f'<div class="error-details">{test["error"]}</div>' if test['error'] else ''}
                            </div>
                            <span class="test-status {test['status']}">{test['status']}</span>
                        </li>
"""
        
        html += """
                    </ul>
                </div>
            </div>
"""
    
    html += """
        </div>
        
        <div class="footer">
            <p>Generado automáticamente por generate_test_report.py</p>
        </div>
    </div>
    
    <script>
        function toggleSuite(suiteId) {
            const content = document.getElementById(suiteId);
            content.classList.toggle('active');
        }
        
        // Expandir automáticamente suites con fallos
        document.addEventListener('DOMContentLoaded', function() {
            const suites = document.querySelectorAll('.suite-card');
            suites.forEach(suite => {
                const failed = suite.querySelector('.test-item.failed, .test-item.error');
                if (failed) {
                    const content = suite.querySelector('.suite-content');
                    if (content) {
                        content.classList.add('active');
                    }
                }
            });
        });
    </script>
</body>
</html>
"""
    
    # Escribir el archivo HTML
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(html)
    
    print(f"[OK] Dashboard HTML generado exitosamente: {output_file}")
    return output_file

def main():
    """Función principal"""
    # Directorio de reportes de Maven
    reports_dir = Path('target/surefire-reports')
    
    if not reports_dir.exists():
        print(f"[ERROR] No se encontro el directorio {reports_dir}")
        print("   Ejecuta primero: mvn test")
        return 1
    
    # Buscar todos los archivos XML de pruebas
    xml_files = list(reports_dir.glob('TEST-*.xml'))
    
    if not xml_files:
        print(f"[ERROR] No se encontraron archivos XML de pruebas en {reports_dir}")
        print("   Ejecuta primero: mvn test")
        return 1
    
    print(f"[INFO] Procesando {len(xml_files)} archivo(s) de reporte...")
    
    # Parsear todos los archivos XML
    suites_data = []
    for xml_file in xml_files:
        suite_data = parse_surefire_xml(xml_file)
        if suite_data:
            suites_data.append(suite_data)
            print(f"   [OK] {suite_data['suite_name']}: {suite_data['total_tests']} pruebas")
    
    if not suites_data:
        print("[ERROR] No se pudieron parsear los archivos XML")
        return 1
    
    # Generar el dashboard HTML
    output_file = generate_html_dashboard(suites_data, 'test-report.html')
    
    # Calcular estadísticas finales
    total_tests = sum(s['total_tests'] for s in suites_data)
    total_passed = sum(s['passed'] for s in suites_data)
    total_failed = sum(s['failed'] for s in suites_data)
    
    print(f"\n[RESUMEN]")
    print(f"   Total de pruebas: {total_tests}")
    print(f"   Exitosas: {total_passed}")
    print(f"   Fallidas: {total_failed}")
    print(f"   Tasa de exito: {(total_passed/total_tests*100):.1f}%")
    print(f"\n[INFO] Abre {output_file} en tu navegador para ver el dashboard")
    
    return 0

if __name__ == '__main__':
    exit(main())

