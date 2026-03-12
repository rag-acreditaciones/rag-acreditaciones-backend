package org.simarro.rag_daw.chunks.model.DTO;

import org.simarro.rag_daw.chunks.model.ENUMS.EstadoChunk;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//LLISTAT PAGINAT
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChunkResponseDTO {
    private Long id;
    private Integer orden;
    private String truncado100chars;
    private Integer numTokens;
    @Enumerated(EnumType.STRING)
    private EstadoChunk estado;

}
