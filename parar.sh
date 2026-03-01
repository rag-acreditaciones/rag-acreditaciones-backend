#!/bin/bash
# ============================================================
# parar.sh — Script de parada de contenedores
# ============================================================
# Ejecutar al TERMINAR la sesión de trabajo.
# Para los contenedores pero NO borra volúmenes
# (los modelos de Ollama se conservan).
# ============================================================

echo ">>> Parando contenedores..."
docker compose down

echo ""
echo "Contenedores parados. Los modelos de Ollama se conservan"
echo "para la siguiente sesión."
echo ""
echo "Si quieres liberar TODO el espacio (incluidos modelos):"
echo "  docker compose down -v"
