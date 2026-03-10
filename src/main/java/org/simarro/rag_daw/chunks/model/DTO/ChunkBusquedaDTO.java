package org.simarro.rag_daw.chunks.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//RESULTAT DE LA RECERCA
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChunkBusquedaDTO {
    private String metadata;
    private String textoCompleto;
    private Integer similitud;
    
}
