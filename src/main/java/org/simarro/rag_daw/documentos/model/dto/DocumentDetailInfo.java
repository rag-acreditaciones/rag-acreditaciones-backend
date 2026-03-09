package org.simarro.rag_daw.documentos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTO para ver la informacion de un solo documento y visualizar el documento

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailInfo {
    private Long id;
    private String nombre;
    private String contentType;
    private Long sizeBytes;
    private String descripcion;

    // Contenido en base64
    private String base64;
}
