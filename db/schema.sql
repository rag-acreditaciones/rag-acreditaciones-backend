-- ============================================================
-- SCHEMA.SQL — Estructura de tablas
-- ============================================================

-- Extensiones necesarias para pgvector
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- TABLAS DE CONFIGURACIÓN MULTI-RAG
-- ============================================================

CREATE TABLE modelos_embedding (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(100)  NOT NULL UNIQUE,
    proveedor       VARCHAR(20)   NOT NULL CHECK (proveedor IN ('OLLAMA','VLLM','OPENAI')),
    modelo_id       VARCHAR(200)  NOT NULL,
    dimensiones     INTEGER       NOT NULL,
    descripcion     TEXT,
    activo          BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE modelos_llm (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(100)  NOT NULL UNIQUE,
    proveedor       VARCHAR(20)   NOT NULL CHECK (proveedor IN ('OLLAMA','VLLM','OPENAI')),
    modelo_id       VARCHAR(200)  NOT NULL,
    descripcion     TEXT,
    activo          BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE rag_configuraciones (
    id                    BIGSERIAL PRIMARY KEY,
    nombre                VARCHAR(100)  NOT NULL UNIQUE,
    descripcion           TEXT,
    modelo_embedding_id   BIGINT        NOT NULL REFERENCES modelos_embedding(id),
    modelo_llm_id         BIGINT        NOT NULL REFERENCES modelos_llm(id),
    host_embedding        VARCHAR(255)  NOT NULL,
    puerto_embedding      INTEGER       NOT NULL,
    host_llm              VARCHAR(255)  NOT NULL,
    puerto_llm            INTEGER       NOT NULL,
    esquema_vector_store  VARCHAR(100)  NOT NULL,
    activo                BOOLEAN       NOT NULL DEFAULT TRUE,
    por_defecto           BOOLEAN       NOT NULL DEFAULT FALSE,
    requiere_gpu          BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- ============================================================
-- TABLAS VECTORIALES (una por dimensión de embedding)
-- ============================================================

CREATE TABLE IF NOT EXISTS vector_store_384 (
    id        UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    content   TEXT,
    metadata  JSON,
    embedding vector(384)
);
CREATE INDEX IF NOT EXISTS idx_vs384_embedding
    ON vector_store_384 USING HNSW (embedding vector_cosine_ops);

CREATE TABLE IF NOT EXISTS vector_store_768 (
    id        UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    content   TEXT,
    metadata  JSON,
    embedding vector(768)
);
CREATE INDEX IF NOT EXISTS idx_vs768_embedding
    ON vector_store_768 USING HNSW (embedding vector_cosine_ops);

CREATE TABLE IF NOT EXISTS vector_store_1024 (
    id        UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    content   TEXT,
    metadata  JSON,
    embedding vector(1024)
);
CREATE INDEX IF NOT EXISTS idx_vs1024_embedding
    ON vector_store_1024 USING HNSW (embedding vector_cosine_ops);

-- ============================================================
-- TABLAS SEGURIDAD JWT
-- ============================================================

-- Usuarios del sistema
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,		-- Identificador único para login
  password VARCHAR(255) NOT NULL,         	-- Almacenado con bcrypt
  estado VARCHAR(50) NOT NULL  DEFAULT 'PENDIENTE',
  CONSTRAINT usuarios_estado_check CHECK (estado IN (
    'PENDIENTE','ACTIVO','DESACTIVADO', 'BAJA')),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE usuarios IS 'Almacena todos los usuarios del sistema con sus credenciales';
-- Para visualizar los comentarios sobre la tabla sesiones en PostgreSQL, puedes usar la siguiente consulta:
-- SELECT obj_description('usuarios'::regclass, 'pg_class');

/* Si ya teneís la tabla creada y queréis adaptarla podéis hacerlo con las siguientes sentencias SQL:
ALTER TABLE usuarios
ADD COLUMN estado VARCHAR(50) NOT NULL CHECK (estado IN ('PENDIENTE', 'ACTIVO', 'DESACTIVADO', 'BAJA')) DEFAULT 'PENDIENTE';

ALTER TABLE usuarios ALTER COLUMN nickname DROP NOT NULL;

ALTER TABLE usuarios
ADD COLUMN fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
*/

-- Roles del sistema
CREATE TABLE IF NOT EXISTS roles (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE,
  descripcion VARCHAR(255) NOT NULL DEFAULT ' ',
  CONSTRAINT roles_nombre_check CHECK (nombre IN (
    'ROLE_CANDIDATO',
    'ROLE_ASESOR',
    'ROLE_EVALUADOR',
    'ROLE_SUPERVISOR',
    'ROLE_PROFESOR',
    'ROLE_JEFEDPTO',
    'ROLE_JEFEESTUDIOS',
    'ROLE_ADMINISTRATIVO',
    'ROLE_ADMINISTRADOR',
    'ROLE_USER'
  ))
);

COMMENT ON TABLE roles IS 'Roles del sistema para control de acceso';
COMMENT ON COLUMN roles.nombre IS 'Nombre único del role según normativa del sistema';

/* Si lo hicieramos fuera de la creación de la tabla:
ALTER TABLE roles
ADD CONSTRAINT roles_nombre_check CHECK (nombre IN (
    'ROLE_CANDIDATO',
    'ROLE_ASESOR',
    'ROLE_EVALUADOR',
    'ROLE_SUPERVISOR',
    'ROLE_PROFESOR',
    'ROLE_JEFEDPTO',
    'ROLE_JEFEESTUDIOS',
    'ROLE_ADMINISTRATIVO',
    'ROLE_ADMINISTRADOR',
    'ROLE_USER'
));*/

/* 
Si la constraint ya existe y queréis modificarla, podéis hacerlo con las siguientes sentencias SQL:

Primero consultamos que exista:
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conrelid = 'roles'::regclass AND conname = 'roles_nombre_check';

Si existe la podemos borrar:
ALTER TABLE roles
DROP CONSTRAINT roles_nombre_check;

Si queremos borrar roles que no queremos en la tabla roles, podemos hacerlo con la siguiente sentencia SQL:
DELETE FROM roles where nombre IN(
  'ROLE_ADMIN','ROLE_ENTRENADOR','ROLE_JUGADOR','ROLE_ARBITRO');

Y podemos añadir de nuevo la constraint con los roles de nuestra aplicación:
ALTER TABLE roles
ADD CONSTRAINT roles_nombre_check CHECK (nombre IN (
    'ROLE_CANDIDATO', 
    'ROLE_ASESOR',
    'ROLE_EVALUADOR',
    'ROLE_SUPERVISOR',
    'ROLE_PROFESOR',
    'ROLE_JEFEDPTO',
    'ROLE_JEFEESTUDIOS',
    'ROLE_ADMINISTRATIVO',
    'ROLE_ADMINISTRADOR',
    'ROLE_USER'
));

Y también el nuevo atributo:
ALTER TABLE roles 
ADD COLUMN descripcion VARCHAR(255) NOT NULL DEFAULT ' ';
 */

-- Relación entre usuarios y roles
CREATE TABLE IF NOT EXISTS usuarios_roles (
  idUsuario BIGINT NOT NULL,
  idRol INTEGER NOT NULL,
  PRIMARY KEY (idUsuario, idRol),
  CONSTRAINT usuarios_roles_fk_usuarios FOREIGN KEY (idUsuario)
    REFERENCES usuarios (id) ON DELETE CASCADE,
  CONSTRAINT usuarios_roles_fk_roles FOREIGN KEY (idRol)
    REFERENCES roles (id) ON DELETE CASCADE
);

