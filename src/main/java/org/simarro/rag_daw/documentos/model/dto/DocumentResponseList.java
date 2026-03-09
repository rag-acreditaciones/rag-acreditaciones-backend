package org.simarro.rag_daw.documentos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTO para recoger una lista de todos los documentos,o lista de documentos con filtros,resumen: lista de documentos

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseList {
    private Long id;
    private String nombre;
    private String contentType;
    private Long sizeBytes;
    private String descripcion;
}
