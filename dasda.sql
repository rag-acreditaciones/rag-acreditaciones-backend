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
INSERT INTO reportes_respuesta (mensaje_id, usuario_id, motivo, estado, descripcion, fecha_creacion)
VALUES 
-- Para test 46 (reporte de mensaje_id=4)
(4, 1, 'INCORRECTA', 'PENDIENTE', 'La información sobre plazos está desactualizada, los plazos cambiaron en 2024', '2025-03-20 10:00:00'),

-- Para test 47 (reporte de mensaje_id=6)
(6, 2, 'INCOMPLETA', 'PENDIENTE', 'Falta mencionar la documentación necesaria del módulo de BD', '2025-04-05 11:30:00'),

-- Datos adicionales para tests de listado y filtros
(2, 3, 'INCORRECTA', 'REVISADO', 'Error en los datos', '2025-01-18 12:00:00'),
(3, 2, 'INCOMPLETA', 'PENDIENTE', 'Faltan ejemplos', '2025-02-10 09:00:00'),
(5, 1, 'IRRELEVANTE', 'PENDIENTE', 'No responde a la pregunta', '2025-03-05 14:00:00'),
(7, 3, 'OFENSIVA', 'REVISADO', 'Lenguaje inapropiado', '2025-04-10 15:30:00'),
(1, 2, 'INCORRECTA', 'PENDIENTE', 'Datos incorrectos', '2025-05-15 16:45:00');

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