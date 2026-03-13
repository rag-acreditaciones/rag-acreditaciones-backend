-- ============================================================
-- configuraMorfeo.sql
-- ============================================================
-- Configura la BD para usar ÚNICAMENTE el RAG de Morfeo.
--
-- Acciones:
--   1. Desactiva TODAS las configuraciones RAG
--   2. Quita el flag "por_defecto" de todas
--   3. Activa solo la configuración "RAG Morfeo" (id=3)
--   4. La marca como configuración por defecto
--   5. Activa solo los modelos que usa Morfeo:
--      - mxbai-embed-large (embedding, 1024d)
--      - llama3.1:8b (LLM)
--
-- Uso:
--   Desde terminal:
--     docker compose exec postgres psql -U postgres -d ragdaw -f /ruta/configuraMorfeo.sql
--
--   O copiar el fichero al contenedor primero:
--     docker cp configuraMorfeo.sql ragdaw-postgres:/tmp/
--     docker compose exec postgres psql -U postgres -d ragdaw -f /tmp/configuraMorfeo.sql
--
--   O conectar con cualquier cliente SQL (DBeaver, DataGrip, psql local):
--     psql -h localhost -U postgres -d ragdaw -f configuraMorfeo.sql
--
-- Para volver a la configuración local:
--   Ejecutar configuraLocal.sql o revertir manualmente.
-- ============================================================

BEGIN;

-- ─────────────────────────────────────────────────────────────
-- 1. DESACTIVAR todo
-- ─────────────────────────────────────────────────────────────

-- Desactivar todas las configuraciones RAG
UPDATE rag_configuraciones
   SET activo = FALSE,
       por_defecto = FALSE;

-- Desactivar todos los modelos de embedding
UPDATE modelos_embedding
   SET activo = FALSE;

-- Desactivar todos los modelos LLM
UPDATE modelos_llm
   SET activo = FALSE;

-- ─────────────────────────────────────────────────────────────
-- 2. ACTIVAR solo los modelos de Morfeo
-- ─────────────────────────────────────────────────────────────

-- Activar mxbai-embed-large (id=3, 1024 dimensiones)
UPDATE modelos_embedding
   SET activo = TRUE
 WHERE modelo_id = 'mxbai-embed-large';

-- Activar llama3.1:8b (id=4)
UPDATE modelos_llm
   SET activo = TRUE
 WHERE modelo_id = 'llama3.1:8b';

-- ─────────────────────────────────────────────────────────────
-- 3. ACTIVAR y marcar como defecto la configuración RAG Morfeo
-- ─────────────────────────────────────────────────────────────

-- Activar la config RAG Morfeo (id=3) y hacerla por defecto
UPDATE rag_configuraciones
   SET activo = TRUE,
       por_defecto = TRUE
 WHERE nombre = 'RAG Morfeo';

COMMIT;

-- ─────────────────────────────────────────────────────────────
-- VERIFICACIÓN: mostrar el estado resultante
-- ─────────────────────────────────────────────────────────────

\echo ''
\echo '═══════════════════════════════════════════════════════════'
\echo ' CONFIGURACIÓN APLICADA — RAG Morfeo'
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
\echo ' Ahora la app usará Ollama en 10.100.22.109:11434'
\echo ' con mxbai-embed-large + llama3.1:8b'
\echo ' Tabla vectorial: vector_store_1024'
\echo '═══════════════════════════════════════════════════════════'
\echo ''
\echo ' ⚠  RECUERDA: Debes re-ingestar los PDFs para generar'
\echo '    embeddings de 1024 dimensiones en vector_store_1024.'
\echo '    Los chunks de vector_store_384 / vector_store_768'
\echo '    NO son compatibles con este modelo de embedding.'
\echo ''
