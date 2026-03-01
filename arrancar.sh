#!/bin/bash
# ============================================================
# arrancar.sh — Arranque SIN borrar datos
# ============================================================
# Levanta los contenedores tal como están.
# NO borra volúmenes: conserva tanto los datos de PostgreSQL
# como los modelos de Ollama.
#
# Si no hay modelos de Ollama descargados, ejecuta
# automáticamente init-ollama-local-minimo.sh para garantizar
# un RAG funcional mínimo.
#
# Usar cuando:
#   - Retomas el trabajo donde lo dejaste
#   - Quieres conservar datos insertados en sesiones anteriores
#
# Si necesitas empezar desde cero (seed fresco):
#   ./limpiar_y_arrancar.sh
# ============================================================

set -e

PROJECT=$(basename "$(pwd)")
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=== Entorno: ${PROJECT} (arranque sin borrar datos) ==="
echo ""

# ── 0. Comprobaciones previas ────────────────────────────────
# 0a. ¿Docker está corriendo?
if ! docker info > /dev/null 2>&1; then
    echo "ERROR: Docker no está corriendo o no tienes permisos."
    echo "  - Arranca Docker Desktop o el servicio dockerd"
    echo "  - Si es un problema de permisos: sudo usermod -aG docker $USER"
    exit 1
fi

# 0b. ¿Existe docker-compose.yml en el directorio actual?
if [ ! -f "${SCRIPT_DIR}/docker-compose.yml" ] && [ ! -f "${SCRIPT_DIR}/compose.yml" ]; then
    echo "ERROR: No se encuentra docker-compose.yml ni compose.yml en:"
    echo "  ${SCRIPT_DIR}"
    echo ""
    echo "Asegúrate de ejecutar este script desde la raíz del proyecto"
    echo "o de que el fichero docker-compose.yml está en el mismo directorio."
    exit 1
fi

# 0c. ¿Existen ya los contenedores del proyecto?
CONTENEDORES_EXISTENTES=$(docker compose ps -a --format '{{.Name}}' 2>/dev/null | grep -c "." || echo "0")
if [ "$CONTENEDORES_EXISTENTES" -eq 0 ]; then
    echo ">>> Primera ejecución: no existen contenedores previos."
    echo "    Se crearán a partir de docker-compose.yml..."
else
    echo ">>> Contenedores existentes detectados (${CONTENEDORES_EXISTENTES})."
    echo "    Se reiniciarán limpiamente..."
fi
echo ""

# ── 1. Limpiar contenedores previos (pueden venir de otro proyecto) ──
echo ">>> Limpiando contenedores previos..."
docker rm -f ragdaw-ollama ragdaw-postgres 2>/dev/null || true
docker compose down 2>/dev/null || true

# ── 2. Levantar contenedores ────────────────────────────────
echo ">>> Levantando contenedores..."
docker compose up -d

# ── 3. Esperar a que PostgreSQL esté listo ───────────────────
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

# ── 4. Esperar a que Ollama esté listo ───────────────────────
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

# ── 5. Verificar modelos de Ollama ───────────────────────────
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
    echo "    │  Modelos de Ollama disponibles               │"
    echo "    ├──────────────────────────────────────────────┤"
    echo "$MODELOS_RAW" | tail -n +2 | while IFS= read -r line; do
        if [ -n "$line" ]; then
            NOMBRE=$(echo "$line" | awk '{print $1}')
            TAMANO=$(echo "$line" | awk '{print $3, $4}')
            printf "    │  ✔ %-25s %12s   │\n" "$NOMBRE" "$TAMANO"
        fi
    done
    echo "    └──────────────────────────────────────────────┘"
    echo ""
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
            echo "    ┌─────────────────────────────────────────────┐"
            echo "    │  ✔ Modelos descargados correctamente        │"
            echo "    ├─────────────────────────────────────────────┤"
            echo "$MODELOS_POST" | tail -n +2 | while IFS= read -r line; do
                if [ -n "$line" ]; then
                    NOMBRE=$(echo "$line" | awk '{print $1}')
                    TAMANO=$(echo "$line" | awk '{print $3, $4}')
                    printf "    │  ✔ %-26s %13s  │\n" "$NOMBRE" "$TAMANO"
                fi
            done
            echo "    └─────────────────────────────────────────────┘"
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

# ── 6. Mostrar resumen final ─────────────────────────────────
echo ""
echo "============================================================"
echo " ENTORNO LISTO"
echo "============================================================"
echo ""
echo " PostgreSQL: localhost:5432  (BD: ragdaw, user: postgres)"
echo " Ollama:     localhost:11434"
echo ""
echo " Tablas en la BD:"
docker compose exec postgres psql -U postgres -d ragdaw -t \
    -c "SELECT count(*) || ' tablas creadas' FROM information_schema.tables WHERE table_schema = 'public';"
echo ""
echo " Para modelos más potentes (si tienes recursos):"
echo "   ./init-ollama-local.sh     (nomic-embed-text + llama3.2:3b ≈ 2.3GB)"
echo ""
echo "============================================================"
