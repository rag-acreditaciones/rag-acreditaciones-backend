#!/bin/bash
# ============================================================
# arrancar.sh — Script de arranque limpiando datos anteriores
# ============================================================
# Ejecutar SIEMPRE al comenzar una sesión de trabajo.
# Garantiza un entorno limpio sin afectar a los modelos
# de Ollama ya descargados.
# ============================================================

set -e

# Nombre del proyecto Docker Compose (inferido del directorio)
PROJECT=$(basename "$(pwd)")

echo "=== Entorno: ${PROJECT} ==="
echo ""

# ── 1. Parar contenedores previos (si los hay) ──────────────
echo ">>> Parando contenedores previos..."
docker compose down 2>/dev/null || true

# ── 2. Borrar SOLO el volumen de PostgreSQL ──────────────────
# Los scripts de /docker-entrypoint-initdb.d/ solo se ejecutan
# cuando el volumen está vacío. Si lo dejamos, el siguiente
# alumno hereda datos del anterior y el seed no se aplica.
PG_VOLUME="${PROJECT}_postgres_data"
if docker volume ls -q | grep -q "^${PG_VOLUME}$"; then
    echo ">>> Eliminando volumen PostgreSQL anterior: ${PG_VOLUME}"
    docker volume rm "${PG_VOLUME}"
else
    echo ">>> No hay volumen PostgreSQL previo (primera ejecución)"
fi

# ── 3. Comprobar si el volumen de Ollama tiene modelos ───────
# NO lo borramos. Los modelos (2-9GB) se reutilizan entre sesiones.
OLLAMA_VOLUME="${PROJECT}_ollama_data"
if docker volume ls -q | grep -q "^${OLLAMA_VOLUME}$"; then
    echo ">>> Volumen Ollama encontrado: ${OLLAMA_VOLUME} (modelos conservados)"
    echo ""
    echo ">>> Verificando modelos de Ollama..."
    # Obtener la lista de modelos (sin la cabecera)
    MODELOS_RAW=$(docker compose exec ollama ollama list 2>/dev/null || echo "")
    NUM_MODELOS=0

    if [ -n "$MODELOS_RAW" ] && echo "$MODELOS_RAW" | grep -q "NAME"; then
    	# Contar líneas excluyendo la cabecera
    	NUM_MODELOS=$(echo "$MODELOS_RAW" | tail -n +2 | grep -c "." 2>/dev/null || echo "0")
    fi
    if [ "$NUM_MODELOS" -gt 0 ]; then
        echo ""
        echo "    ┌──────────────────────────────────────────────┐"
        echo "    │  Modelos de Ollama disponibles                │"
        echo "    ├──────────────────────────────────────────────┤"
        echo "$MODELOS_RAW" | tail -n +2 | while IFS= read -r line; do
            if [ -n "$line" ]; then
                NOMBRE=$(echo "$line" | awk '{print $1}')
                TAMANO=$(echo "$line" | awk '{print $3, $4}')
                printf "    │  ✔ %-25s %12s  │\n" "$NOMBRE" "$TAMANO"
            fi
        done
        echo "    └──────────────────────────────────────────────┘"
        echo ""
    fi
else
    echo ""
    echo "    ┌──────────────────────────────────────────────┐"
    echo "    │  ⚠  No hay modelos de Ollama descargados     │"
    echo "    └──────────────────────────────────────────────┘"
    echo ""
    echo "    Se necesitan modelos para que el RAG funcione."
    echo "    Procediendo a descargar modelos para RAG mínimo..."
    echo ""

    # Buscar y ejecutar init-ollama-local-minimo.sh
    INIT_SCRIPT="${SCRIPT_DIR}/init-ollama-local-minimo.sh"
    if [ -f "$INIT_SCRIPT" ]; then
        echo "    Ejecutando: init-ollama-local-minimo.sh"
        echo "    (all-minilm ~23MB + llama3.2:1b ~1.3GB ≈ 1.4GB total)"
        echo ""
        bash "$INIT_SCRIPT"
        echo ""

        # Verificar que se descargaron correctamente
        MODELOS_POST=$(docker compose exec ollama ollama list 2>/dev/null || echo "")
        NUM_POST=$(echo "$MODELOS_POST" | tail -n +2 | grep -c "." 2>/dev/null || echo "0")

        if [ "$NUM_POST" -gt 0 ]; then
            echo ""
            echo "    ┌──────────────────────────────────────────────┐"
            echo "    │  ✔ Modelos descargados correctamente         │"
            echo "    ├──────────────────────────────────────────────┤"
            echo "$MODELOS_POST" | tail -n +2 | while IFS= read -r line; do
                if [ -n "$line" ]; then
                    NOMBRE=$(echo "$line" | awk '{print $1}')
                    TAMANO=$(echo "$line" | awk '{print $3, $4}')
                    printf "    │  ✔ %-25s %12s  │\n" "$NOMBRE" "$TAMANO"
                fi
            done
            echo "    └──────────────────────────────────────────────┘"
        else
            echo ""
            echo "    ⚠  ERROR: La descarga de modelos pudo fallar."
            echo "    Ejecuta manualmente: ./init-ollama-local-minimo.sh"
            echo "    O para modelos más potentes: ./init-ollama-local.sh"
        fi
    else
        echo "    ⚠  No se encuentra init-ollama-local-minimo.sh"
        echo "    Descarga los modelos manualmente:"
        echo ""
        echo "      docker compose exec ollama ollama pull all-minilm"
        echo "      docker compose exec ollama ollama pull llama3.2:1b"
        echo ""
        echo "    O para modelos más potentes:"
        echo "      docker compose exec ollama ollama pull nomic-embed-text"
        echo "      docker compose exec ollama ollama pull llama3.2:3b"
    fi
    echo ""
fi

# ── 4. Levantar contenedores ────────────────────────────────
echo ""
echo ">>> Levantando contenedores..."
docker compose up -d

# ── 5. Esperar a que PostgreSQL esté listo ───────────────────
echo ">>> Esperando a PostgreSQL..."
INTENTOS=0
MAX_INTENTOS=30
until docker compose exec postgres pg_isready -U postgres > /dev/null 2>&1; do
    INTENTOS=$((INTENTOS + 1))
    if [ $INTENTOS -ge $MAX_INTENTOS ]; then
        echo ""
        echo "ERROR: PostgreSQL no responde después de ${MAX_INTENTOS} intentos."
        echo ""
        echo "Revisa los logs con:"
        echo "  docker compose logs postgres"
        echo ""
        echo "Si hay errores en schema.sql o data.sql, corrígelos y ejecuta:"
        echo "  ./limpiar_y_arrancar.sh"
        exit 1
    fi
    sleep 2
done
echo ">>> PostgreSQL OK"

# ── 6. Esperar a que Ollama esté listo ───────────────────────
echo ">>> Esperando a Ollama..."
INTENTOS=0
until curl -s http://localhost:11434/api/version > /dev/null 2>&1; do
    INTENTOS=$((INTENTOS + 1))
    if [ $INTENTOS -ge $MAX_INTENTOS ]; then
        echo ""
        echo "ERROR: Ollama no responde después de ${MAX_INTENTOS} intentos."
        echo "Revisa los logs con: docker compose logs ollama"
        exit 1
    fi
    sleep 2
done
echo ">>> Ollama OK: $(curl -s http://localhost:11434/api/version)"

# ── 7. Mostrar resumen ──────────────────────────────────────
echo ""
echo "============================================================"
echo " ENTORNO LISTO"
echo "============================================================"
echo ""
echo " PostgreSQL: localhost:5432  (BD: ragdaw, user: postgres)"
echo " Ollama:     localhost:11434"
echo ""

# Mostrar modelos de Ollama disponibles
MODELOS=$(docker compose exec ollama ollama list 2>/dev/null || echo "")
if [ -n "$MODELOS" ] && echo "$MODELOS" | grep -q "NAME"; then
    echo " Modelos de Ollama ya descargados:"
    echo "$MODELOS" | tail -n +2 | while read -r line; do
        echo "   • $line"
    done
else
    echo " ⚠  No hay modelos de Ollama descargados."
    echo "    Ejecuta uno de los scripts init-ollama-*.sh"
fi

echo ""
echo " Tablas en la BD:"
docker compose exec postgres psql -U postgres -d ragdaw -t \
    -c "SELECT count(*) || ' tablas creadas' FROM information_schema.tables WHERE table_schema = 'public';"
echo ""
echo "============================================================"
