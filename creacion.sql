-- ============================================
-- TABLA VALORACIONES
-- ============================================

CREATE TABLE valoraciones (
    id SERIAL PRIMARY KEY,
    mensaje_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    valoracion VARCHAR(10) NOT NULL,
    comentario TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_valoraciones_mensaje
ON valoraciones(mensaje_id);

CREATE UNIQUE INDEX idx_valoracion_usuario_mensaje
ON valoraciones(mensaje_id, usuario_id);


-- ============================================
-- TABLA REPORTES RESPUESTA
-- ============================================

CREATE TABLE reportes_respuesta (
    id SERIAL PRIMARY KEY,
    mensaje_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    motivo TEXT NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP
);

CREATE INDEX idx_reportes_mensaje
ON reportes_respuesta(mensaje_id);

CREATE INDEX idx_reportes_estado
ON reportes_respuesta(estado);