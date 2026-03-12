-- ============================================================
-- DATA.SQL — Datos iniciales (seed)
-- ============================================================

-- Modelos de embedding
INSERT INTO modelos_embedding (id, nombre, proveedor, modelo_id, dimensiones, descripcion) VALUES
(1, 'all-minilm',        'OLLAMA', 'all-minilm',        384,
   'Modelo ligero de embeddings. Ideal para equipos con 8GB RAM. Solo 79MB en disco.'),
(2, 'nomic-embed-text',  'OLLAMA', 'nomic-embed-text',  768,
   'Buen balance calidad/velocidad. 274MB en disco. Contexto 8192 tokens.'),
(3, 'mxbai-embed-large', 'OLLAMA', 'mxbai-embed-large', 1024,
   'Alta calidad de embeddings. 670MB en disco. Recomendado para equipos con GPU.');

-- Modelos LLM
INSERT INTO modelos_llm (id, nombre, proveedor, modelo_id, descripcion) VALUES
(1, 'llama3.2:1b',  'OLLAMA', 'llama3.2:1b',
   'Modelo mínimo de Meta. 1.3GB. Respuestas básicas. Para equipos con 8GB RAM.'),
(2, 'llama3.2:3b',  'OLLAMA', 'llama3.2:3b',
   'Modelo compacto de Meta con soporte español nativo. ~2GB. Recomendado 16GB RAM.'),
(3, 'mistral:7b',   'OLLAMA', 'mistral:7b',
   'Modelo de Mistral AI. ~4.1GB. Buena calidad en español. Requiere 16GB+ o GPU.'),
(4, 'llama3.1:8b',  'OLLAMA', 'llama3.1:8b',
   'Modelo potente de Meta. ~4.7GB. Excelente calidad. Ideal para servidor con GPU.');

-- Configuraciones RAG
INSERT INTO rag_configuraciones
    (id, nombre, descripcion, modelo_embedding_id, modelo_llm_id,
     host_embedding, puerto_embedding, host_llm, puerto_llm,
     esquema_vector_store, por_defecto, requiere_gpu) VALUES
(1, 'RAG Local Mínimo',
    'Configuración ligera para equipos con 8GB RAM.',
    1, 1, 'localhost', 11434, 'localhost', 11434,
    'vector_store_384', TRUE, FALSE),
(2, 'RAG Local',
    'Configuración estándar para equipos con 16GB RAM.',
    2, 2, 'localhost', 11434, 'localhost', 11434,
    'vector_store_768', FALSE, FALSE),
(3, 'RAG Morfeo',
    'Servidor remoto con GPU NVIDIA 24GB.',
    3, 4, '10.100.22.109', 11434, '10.100.22.109', 11434,
    'vector_store_1024', FALSE, TRUE);

-- Resetear secuencias
SELECT setval('modelos_embedding_id_seq', (SELECT MAX(id) FROM modelos_embedding));
SELECT setval('modelos_llm_id_seq', (SELECT MAX(id) FROM modelos_llm));
SELECT setval('rag_configuraciones_id_seq', (SELECT MAX(id) FROM rag_configuraciones));

/********************************************************************************
*        Script de creación de tablas relacionadas con usuarios y permisos:     *
*        Tablas usuarios, roles, permisos, usuarios_roles y roles_permisos      *
*********************************************************************************/

/* En una aplicación real al crear el usuario se quedaría PENDIENTE  de confirmar el usuario por email, 
simplificando podeís darlo de alta con ACTIVO PORQUE no da tiempo a realizar la confirmación por email.
 

  Insertando usuarios de ejemplo para cada rol. Todos con la misma contraseña.
  La contraseña 'NoTeLoDigo@1' encriptada utilizandola secret-key=firmaSeguridadSimarro1DesarrolloWebEntornoServidor 
  almacenada en  application.properties es '$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJe'*/

INSERT INTO usuarios (nombre, email, password, estado) VALUES
('Candidato Apellido1 Apellido2', 'candidato@example.com', '$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJe', 'ACTIVO'),
('Asesor Apellido1 Apellido2', 'asesor@example.com', '$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJe', 'ACTIVO'),
('Evaluador Apellido1 Apellido2', 'evaluador@example.com', '$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJe', 'ACTIVO'),
('Supervisor Apellido1 Apellido2', 'supervisor@example.com', '$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJe', 'ACTIVO'),
('Profesor Apellido1 Apellido2', 'profesor@example.com', '$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJe', 'ACTIVO'),
('JefeDpto Apellido1 Apellido2', 'jefedpto@example.com', '$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJe', 'ACTIVO'),
('JefeEstudios Apellido1 Apellido2', 'jefeestudios@example.com', 'encrypted_passwo$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJerd', 'ACTIVO'),
('Administrativo Apellido1 Apellido2', 'administrativo@example.com', '$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJe', 'ACTIVO'),
('Administrador Apellido1 Apellido2', 'administrador@example.com', '$2a$10$sYSmlH1l3VgSrcAOGlBst.HZor33pUHZYkawAIjVdpjXcmPkyvqJe', 'ACTIVO');

-- Comprobar usuarios creados:
-- SELECT * FROM usuarios;

-- Añadimos los roles de usuario de la aplicación con descripciones
INSERT INTO roles (nombre, descripcion) VALUES 
('ROLE_CANDIDATO', 'Usuario que solicita una acreditación'),
('ROLE_ASESOR', 'Provee orientación y asesoramiento en áreas específicas'),
('ROLE_EVALUADOR', 'Responsable de la evaluación de candidatos'),
('ROLE_SUPERVISOR', 'Supervisa tareas d asesores y evaluadores en la acreditación'),
('ROLE_PROFESOR', 'Impartir clases y educar a los estudiantes'),
('ROLE_JEFEDPTO', 'Dirige un departamento dentro del IES'),
('ROLE_JEFEESTUDIOS', 'Gestiona y supervisa programas educativos'),
('ROLE_ADMINISTRATIVO', 'Realiza tareas administrativas y de soporte'),
('ROLE_ADMINISTRADOR', 'Gestiona el sistema y tiene acceso a funcionalidades especiales de la aplicación'),
('ROLE_USER', 'Rol por defecto para usuarios no autenticados');

/* Si ya estuvieran creados los roles y queremos modificarlos, podemos hacerlo con la siguiente sentencia SQL:

UPDATE roles SET descripcion = 'Usuario que solicita una acreditación' WHERE nombre = 'ROLE_CANDIDATO';
UPDATE roles SET descripcion = 'Provee orientación y asesoramiento en áreas específicas' WHERE nombre = 'ROLE_ASESOR';
UPDATE roles SET descripcion = 'Responsable de la evaluación de candidatos' WHERE nombre = 'ROLE_EVALUADOR';
UPDATE roles SET descripcion = 'Supervisa tareas de asesores y evaluadores en la acreditación' WHERE nombre = 'ROLE_SUPERVISOR';
UPDATE roles SET descripcion = 'Impartir clases y educar a los estudiantes' WHERE nombre = 'ROLE_PROFESOR';
UPDATE roles SET descripcion = 'Dirige un departamento dentro del IES' WHERE nombre = 'ROLE_JEFEDPTO';
UPDATE roles SET descripcion = 'Gestiona y supervisa programas educativos' WHERE nombre = 'ROLE_JEFEESTUDIOS';
UPDATE roles SET descripcion = 'Realiza tareas administrativas y de soporte' WHERE nombre = 'ROLE_ADMINISTRATIVO';
UPDATE roles SET descripcion = 'Gestiona el sistema y tiene acceso a funcionalidades especiales de la aplicación' WHERE nombre = 'ROLE_ADMINISTRADOR';
UPDATE roles SET descripcion = 'Rol por defecto para usuarios no autenticados' WHERE nombre = 'ROLE_USER';
*/

-- Comprobar roles creados:
--SELECT * FROM roles;

-- Si queremos dejar vacia la relación entre usuarios y roles, podemos hacerlo con la siguiente sentencia SQL:
-- DELETE FROM usuarios_roles;

-- Asignación de roles a usuarios utilizando el email del usuario y el nombre del role que queremos darle
INSERT INTO usuarios_roles (idUsuario, idRol)
VALUES
  ((SELECT id FROM usuarios WHERE email = 'candidato@example.com'), 
   (SELECT id FROM roles WHERE nombre = 'ROLE_CANDIDATO')),

  ((SELECT id FROM usuarios WHERE email = 'asesor@example.com'), 
   (SELECT id FROM roles WHERE nombre = 'ROLE_ASESOR')),

  ((SELECT id FROM usuarios WHERE email = 'evaluador@example.com'), 
   (SELECT id FROM roles WHERE nombre = 'ROLE_EVALUADOR')),

  ((SELECT id FROM usuarios WHERE email = 'supervisor@example.com'), 
   (SELECT id FROM roles WHERE nombre = 'ROLE_SUPERVISOR')),

  ((SELECT id FROM usuarios WHERE email = 'profesor@example.com'), 
   (SELECT id FROM roles WHERE nombre = 'ROLE_PROFESOR')),

  ((SELECT id FROM usuarios WHERE email = 'jefedpto@example.com'), 
   (SELECT id FROM roles WHERE nombre = 'ROLE_JEFEDPTO')),

  ((SELECT id FROM usuarios WHERE email = 'jefeestudios@example.com'), 
   (SELECT id FROM roles WHERE nombre = 'ROLE_JEFEESTUDIOS')),

  ((SELECT id FROM usuarios WHERE email = 'administrativo@example.com'), 
   (SELECT id FROM roles WHERE nombre = 'ROLE_ADMINISTRATIVO')),

  ((SELECT id FROM usuarios WHERE email = 'administrador@example.com'), 
   (SELECT id FROM roles WHERE nombre = 'ROLE_ADMINISTRADOR'));

-- ============================================
-- LIMPIAR TABLAS COMPLETAMENTE
-- ============================================
TRUNCATE TABLE valoraciones CASCADE;
TRUNCATE TABLE reportes_respuesta CASCADE;

-- ============================================
-- DATOS PARA VALORACIONES (tests 41-45)
-- ============================================
INSERT INTO valoraciones (mensaje_id, usuario_id, valoracion, comentario, fecha_creacion)
VALUES 
-- Para test 43 (mensaje_id=2 debe tener valoraciones)
(2, 1, 'POSITIVA', 'Muy buena respuesta', '2025-01-15 10:30:00'),
(2, 2, 'POSITIVA', 'Me ayudó mucho', '2025-01-16 11:45:00'),
(2, 3, 'NEGATIVA', 'No era lo que buscaba', '2025-01-17 09:15:00'),

-- Para test 44 (conversacion_id=1 necesita valoraciones)
(1, 1, 'POSITIVA', 'Excelente', '2025-02-01 10:00:00'),
(3, 1, 'POSITIVA', 'Muy claro', '2025-02-02 11:00:00'),
(5, 1, 'NEGATIVA', 'Regular', '2025-02-03 12:00:00'),

-- Para test 45 (eliminar valoración con id específico)
(120, 1, 'POSITIVA', 'Esta se eliminará', '2025-03-01 10:00:00'),

-- Datos adicionales para calidad
(4, 1, 'POSITIVA', 'Bien', '2025-03-15 14:30:00'),
(4, 2, 'NEGATIVA', 'Mal', '2025-03-16 15:45:00'),
(6, 1, 'POSITIVA', 'Perfecto', '2025-04-01 09:00:00'),
(6, 2, 'POSITIVA', 'Genial', '2025-04-02 10:30:00'),
(7, 1, 'NEGATIVA', 'No sirve', '2025-04-03 11:45:00'),
(8, 1, 'POSITIVA', 'Buen trabajo', '2025-05-01 12:00:00'),
(8, 2, 'POSITIVA', 'Gracias', '2025-05-02 13:15:00'),
(8, 3, 'NEGATIVA', 'Mejorable', '2025-05-03 14:30:00');

-- ============================================
-- DATOS PARA REPORTES (tests 46-50)
-- ============================================
INSERT INTO reportes_respuesta (mensaje_id, usuario_id, motivo, estado, fecha_creacion)
VALUES 
-- Para test 46 (reporte de mensaje_id=4)
(4, 1, 'INCORRECTA', 'PENDIENTE', '2025-03-20 10:00:00'),

-- Para test 47 (reporte de mensaje_id=6)
(6, 2, 'INCOMPLETA', 'PENDIENTE', '2025-04-05 11:30:00'),

-- Datos adicionales para tests de listado y filtros
(2, 3, 'INCORRECTA', 'REVISADO', '2025-01-18 12:00:00'),
(3, 2, 'INCOMPLETA', 'PENDIENTE', '2025-02-10 09:00:00'),
(5, 1, 'IRRELEVANTE', 'PENDIENTE', '2025-03-05 14:00:00'),
(7, 3, 'OFENSIVA', 'REVISADO', '2025-04-10 15:30:00'),
(1, 2, 'INCORRECTA', 'PENDIENTE', '2025-05-15 16:45:00');

-- ============================================
-- ACTUALIZAR SECUENCIAS (importante para que los IDs sigan el orden)
-- ============================================
SELECT setval('valoraciones_id_seq', (SELECT MAX(id) FROM valoraciones));
SELECT setval('reportes_respuesta_id_seq', (SELECT MAX(id) FROM reportes_respuesta));

-- ============================================
-- VERIFICAR DATOS (opcional)
-- ============================================
-- Ver valoraciones
SELECT 'VALORACIONES' as tabla, COUNT(*) as total FROM valoraciones
UNION ALL
SELECT 'REPORTES', COUNT(*) FROM reportes_respuesta;

-- Ver datos específicos para cada test
SELECT 'Test 43 - Valoraciones mensaje 2' as test, COUNT(*) as total 
FROM valoraciones WHERE mensaje_id = 2;

SELECT 'Test 44 - Valoraciones conversacion 1' as test, COUNT(*) as total 
FROM valoraciones WHERE mensaje_id IN (1,3,5);

SELECT 'Test 46-48 - Reportes PENDIENTE' as test, COUNT(*) as total 
FROM reportes_respuesta WHERE estado = 'PENDIENTE';

SELECT 'Test 49 - Reportes INCORRECTA' as test, COUNT(*) as total 
FROM reportes_respuesta WHERE motivo = 'INCORRECTA';

SELECT 'Test 50 - Reporte ID 6' as test, estado 
FROM reportes_respuesta WHERE id = 6;
--  DOCUMENTOS (Equipo 1) SECCIONES TEMATICAS (datos precargados)
INSERT INTO secciones_tematicas (nombre, descripcion, codigo_color) VALUES
('GENERAL',       'Documentacion general del proceso de acreditaciones', '#6B7280'),
('BD',            'Bases de datos relacionales y no relacionales',       '#3B82F6'),
('PROGRAMACION',  'Programacion orientada a objetos y estructurada',    '#10B981'),
('WEB',           'Desarrollo web frontend y backend',                  '#F59E0B');
