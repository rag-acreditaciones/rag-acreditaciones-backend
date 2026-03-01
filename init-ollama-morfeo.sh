#!/bin/bash
# ============================================================
# init-ollama-morfeo.sh
# Descarga modelos para RAG Morfeo (servidor con GPU NVIDIA 24GB)
# Disco necesario: ~5.4 GB
# Ejecutar EN EL SERVIDOR MORFEO, no en local
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
echo "=== Descargando modelo de embedding: mxbai-embed-large (1024 dims, ~670MB) ==="
docker exec "$CONTAINER" ollama pull mxbai-embed-large

echo ""
echo "=== Descargando modelo LLM: llama3.1:8b (~4.7GB) ==="
docker exec "$CONTAINER" ollama pull llama3.1:8b

echo ""
echo "=== Modelos descargados ==="
docker exec "$CONTAINER" ollama list
echo ""
echo "=== RAG Morfeo listo ==="
