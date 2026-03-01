#!/bin/bash
# ============================================================
# init-ollama-local.sh
# Descarga modelos para RAG Local (16GB RAM) — CONFIGURACIÓN POR DEFECTO
# Disco necesario: ~2.3 GB
# ============================================================

set -e

CONTAINER="ragdaw-ollama"
OLLAMA_URL="http://localhost:11434"

echo "=== Verificando que Ollama está arrancado ==="
until curl -s "$OLLAMA_URL/api/version" > /dev/null 2>&1; do
    echo "Esperando a Ollama..."
    sleep 2
done
echo "Ollama OK: $(curl -s $OLLAMA_URL/api/version)"

echo ""
echo "=== Descargando modelo de embedding: nomic-embed-text (768 dims, ~274MB) ==="
docker exec "$CONTAINER" ollama pull nomic-embed-text

echo ""
echo "=== Descargando modelo LLM: llama3.2:3b (~2GB) ==="
docker exec "$CONTAINER" ollama pull llama3.2:3b

echo ""
echo "=== Modelos descargados ==="
docker exec "$CONTAINER" ollama list
echo ""
echo "=== RAG Local listo (configuración por defecto) ==="
