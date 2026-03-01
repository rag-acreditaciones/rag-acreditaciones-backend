-- Primero, quitar el estado por defecto de la opción 2
UPDATE rag_configuraciones 
SET por_defecto = FALSE 
WHERE id = 2;

-- Luego, establecer la opción 1 como la opción por defecto
UPDATE rag_configuraciones 
SET por_defecto = TRUE 
WHERE id = 1;
