-- ============================================================
-- DROP ALL — Eliminar todas las tablas
-- ============================================================

DROP TABLE IF EXISTS mensajes CASCADE;
DROP TABLE IF EXISTS conversaciones CASCADE;
DROP TABLE IF EXISTS usuarios_roles CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS documentos CASCADE;
DROP TABLE IF EXISTS secciones_tematicas CASCADE;
DROP TABLE IF EXISTS vector_store_384 CASCADE;
DROP TABLE IF EXISTS vector_store_768 CASCADE;
DROP TABLE IF EXISTS vector_store_1024 CASCADE;
DROP TABLE IF EXISTS rag_configuraciones CASCADE;
DROP TABLE IF EXISTS modelos_embedding CASCADE;
DROP TABLE IF EXISTS modelos_llm CASCADE;

-- Extensiones (opcional, comenta si no quieres eliminarlas)
DROP EXTENSION IF EXISTS vector CASCADE;
DROP EXTENSION IF EXISTS hstore CASCADE;
DROP EXTENSION IF EXISTS "uuid-ossp" CASCADE;