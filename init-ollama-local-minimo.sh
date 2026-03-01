#!/bin/bash
# ============================================================
# init-ollama-local-minimo.sh
# Descarga modelos para RAG Local Mínimo (8GB RAM)
# Disco necesario: ~1.4 GB
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
echo "=== Descargando modelo de embedding: all-minilm (384 dims, ~79MB) ==="
docker exec "$CONTAINER" ollama pull all-minilm

echo ""
echo "=== Descargando modelo LLM: llama3.2:1b (~1.3GB) ==="
docker exec "$CONTAINER" ollama pull llama3.2:1b

echo ""
echo "=== Modelos descargados ==="
docker exec "$CONTAINER" ollama list
echo ""
echo "=== RAG Local Mínimo listo ==="
