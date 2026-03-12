package org.simarro.rag_daw.chunks.model.interfaces;

public interface DocumentoChunkStatsInterface {
    
    Long getDocumentoId();
    Long getNumeroChunks();
    Long getTotalTokens();
    Long getEstadoPendiente();
    Long getEstadoRevisado();
    Long getEstadoDescartado();
    Long getLongitudMax();
    Long getLongitudMin();
    Double getLongitudMedia();

}
