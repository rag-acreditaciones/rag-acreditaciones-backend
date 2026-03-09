package org.simarro.rag_daw.informes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EstadisticaDocumentosDTO {
    private String etiqueta;    // nombre seccion o estado
    private Long cantidad;
}

