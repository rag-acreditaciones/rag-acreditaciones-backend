-- ============================================================
-- configuraLocalMinimo.sql
-- ============================================================
-- Configura la BD para usar ÚNICAMENTE el RAG Local Mínimo.
--
-- Acciones:
--   1. Desactiva TODAS las configuraciones RAG
--   2. Quita el flag "por_defecto" de todas
--   3. Activa solo la configuración "RAG Local Mínimo" (id=1)
--   4. La marca como configuración por defecto
--   5. Activa solo los modelos que usa RAG Local Mínimo:
--      - all-minilm (embedding, 384d, ~79MB)
--      - llama3.2:1b (LLM, ~1.3GB)
--
-- Requisitos:
--   - Ollama local con los modelos descargados:
--       docker compose exec ollama ollama pull all-minilm
--       docker compose exec ollama ollama pull llama3.2:1b
--     O ejecutar: ./init-ollama-local-minimo.sh
--   - Funciona en equipos con 8GB de RAM
--
-- Uso:
--   docker compose exec postgres psql -U postgres -d ragdaw -f /tmp/configuraLocalMinimo.sql
--
-- Para cambiar a Local:   psql -f configuraLocal.sql
-- Para cambiar a Morfeo:  psql -f configuraMorfeo.sql
-- ============================================================

BEGIN;

-- ─────────────────────────────────────────────────────────────
-- 1. DESACTIVAR todo
-- ─────────────────────────────────────────────────────────────

UPDATE rag_configuraciones
   SET activo = FALSE,
       por_defecto = FALSE;

UPDATE modelos_embedding
   SET activo = FALSE;

UPDATE modelos_llm
   SET activo = FALSE;

-- ─────────────────────────────────────────────────────────────
-- 2. ACTIVAR solo los modelos de RAG Local Mínimo
-- ─────────────────────────────────────────────────────────────

-- Activar all-minilm (id=1, 384 dimensiones)
UPDATE modelos_embedding
   SET activo = TRUE
 WHERE modelo_id = 'all-minilm';

-- Activar llama3.2:1b (id=1)
UPDATE modelos_llm
   SET activo = TRUE
 WHERE modelo_id = 'llama3.2:1b';

-- ─────────────────────────────────────────────────────────────
-- 3. ACTIVAR y marcar como defecto RAG Local Mínimo
-- ─────────────────────────────────────────────────────────────

UPDATE rag_configuraciones
   SET activo = TRUE,
       por_defecto = TRUE
 WHERE nombre = 'RAG Local Mínimo';

COMMIT;

-- ─────────────────────────────────────────────────────────────
-- VERIFICACIÓN: mostrar el estado resultante
-- ─────────────────────────────────────────────────────────────

\echo ''
\echo '═══════════════════════════════════════════════════════════'
\echo ' CONFIGURACIÓN APLICADA — RAG Local Mínimo'
\echo '═══════════════════════════════════════════════════════════'
\echo ''

\echo '── Configuraciones RAG ──'
SELECT id, nombre, activo, por_defecto,
       host_embedding || ':' || puerto_embedding AS endpoint_embedding,
       host_llm || ':' || puerto_llm AS endpoint_llm,
       esquema_vector_store
  FROM rag_configuraciones
 ORDER BY id;

\echo ''
\echo '── Modelos Embedding (activos) ──'
SELECT id, nombre, modelo_id, dimensiones, activo
  FROM modelos_embedding
 ORDER BY id;

\echo ''
\echo '── Modelos LLM (activos) ──'
SELECT id, nombre, modelo_id, activo
  FROM modelos_llm
 ORDER BY id;

\echo ''
\echo '═══════════════════════════════════════════════════════════'
\echo ' Ahora la app usará Ollama en localhost:11434'
\echo ' con all-minilm + llama3.2:1b'
\echo ' Tabla vectorial: vector_store_384'
\echo '═══════════════════════════════════════════════════════════'
\echo ''
\echo ' ⚠  RECUERDA: Si antes usabas otro modelo de embedding,'
\echo '    debes re-ingestar los PDFs para generar embeddings'
\echo '    de 384 dimensiones en vector_store_384.'
\echo '    Los chunks de vector_store_768 / vector_store_1024'
\echo '    NO son compatibles con este modelo.'
\echo ''
\echo ' Asegúrate de tener los modelos descargados:'
\echo '   docker compose exec ollama ollama list'
\echo ' Si faltan, ejecuta: ./init-ollama-local-minimo.sh'
\echo ''
