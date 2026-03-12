package org.simarro.rag_daw.chunks.model.DTO;

import java.util.List;

import org.simarro.rag_daw.chunks.model.ENUMS.EstadoChunk;
import org.simarro.rag_daw.documentos.model.dto.DocumentoFilterDTO;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//RESULTAT DE LA RECERCA
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChunkBusquedaDTO {
    private Long id;
    private DocumentoFilterDTO documento;
    private Integer orden;
    private String texto;
    private Integer numTokens;
    @Enumerated(EnumType.STRING)
    private EstadoChunk estado;
    private Double similitud;
    private List<Integer> posicionesBusquedaTextual;

}
