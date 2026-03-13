-- ============================================================
-- configuraLocal.sql
-- ============================================================
-- Configura la BD para usar ÚNICAMENTE el RAG Local.
--
-- Acciones:
--   1. Desactiva TODAS las configuraciones RAG
--   2. Quita el flag "por_defecto" de todas
--   3. Activa solo la configuración "RAG Local" (id=2)
--   4. La marca como configuración por defecto
--   5. Activa solo los modelos que usa RAG Local:
--      - nomic-embed-text (embedding, 768d, ~274MB)
--      - llama3.2:3b (LLM, ~2GB)
--
-- Requisitos:
--   - Ollama local con los modelos descargados:
--       docker compose exec ollama ollama pull nomic-embed-text
--       docker compose exec ollama ollama pull llama3.2:3b
--     O ejecutar: ./init-ollama-local.sh
--   - Equipo con al menos 16GB de RAM recomendado
--
-- Uso:
--   docker compose exec postgres psql -U postgres -d ragdaw -f /tmp/configuraLocal.sql
--
-- Para cambiar a Morfeo:  psql -f configuraMorfeo.sql
-- Para cambiar a Mínimo:  psql -f configuraLocalMinimo.sql
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
-- 2. ACTIVAR solo los modelos de RAG Local
-- ─────────────────────────────────────────────────────────────

-- Activar nomic-embed-text (id=2, 768 dimensiones)
UPDATE modelos_embedding
   SET activo = TRUE
 WHERE modelo_id = 'nomic-embed-text';

-- Activar llama3.2:3b (id=2)
UPDATE modelos_llm
   SET activo = TRUE
 WHERE modelo_id = 'llama3.2:3b';

-- ─────────────────────────────────────────────────────────────
-- 3. ACTIVAR y marcar como defecto la configuración RAG Local
-- ─────────────────────────────────────────────────────────────

UPDATE rag_configuraciones
   SET activo = TRUE,
       por_defecto = TRUE
 WHERE nombre = 'RAG Local';

COMMIT;

-- ─────────────────────────────────────────────────────────────
-- VERIFICACIÓN: mostrar el estado resultante
-- ─────────────────────────────────────────────────────────────

\echo ''
\echo '═══════════════════════════════════════════════════════════'
\echo ' CONFIGURACIÓN APLICADA — RAG Local'
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
\echo ' con nomic-embed-text + llama3.2:3b'
\echo ' Tabla vectorial: vector_store_768'
\echo '═══════════════════════════════════════════════════════════'
\echo ''
\echo ' ⚠  RECUERDA: Si antes usabas otro modelo de embedding,'
\echo '    debes re-ingestar los PDFs para generar embeddings'
\echo '    de 768 dimensiones en vector_store_768.'
\echo '    Los chunks de vector_store_384 / vector_store_1024'
\echo '    NO son compatibles con este modelo.'
\echo ''
\echo ' Asegúrate de tener los modelos descargados:'
\echo '   docker compose exec ollama ollama list'
\echo ' Si faltan, ejecuta: ./init-ollama-local.sh'
\echo ''
