package org.simarro.rag_daw.chunks.model.DTO;

import org.simarro.rag_daw.chunks.model.ENUMS.EstadoChunk;
import org.simarro.rag_daw.documentos.model.dto.DocumentoFilterDTO;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//DETALL D'UN CHUNK
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChunkDetailDTO {

    private Long id;
    private Integer orden;
    private String textoCompleto;
    @Enumerated(EnumType.STRING)
    private EstadoChunk estado;
    private DocumentoFilterDTO documento;

}
