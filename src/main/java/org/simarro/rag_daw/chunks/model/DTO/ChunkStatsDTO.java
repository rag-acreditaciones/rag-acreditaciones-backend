package org.simarro.rag_daw.chunks.model.DTO;

import org.simarro.rag_daw.documentos.model.dto.DocumentoResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChunkStatsDTO {

    private DocumentoResponseDTO documento;
    private Long numeroChunks;
    private Long numeroChunksPendiente;
    private Long numeroChunksRevisado;
    private Long numeroChunksDescartado;
    private Long longitudMedia;
    private Long longitudMax;
    private Long longitudMin;
    private Long totalTokens;

}    //private HashMap<String, Integer> numeroChunksEstat;



