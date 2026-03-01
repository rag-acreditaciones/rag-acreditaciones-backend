package org.simarro.rag_daw.rag.srv;

import java.util.List;

import org.simarro.rag_daw.rag.model.dto.ModeloEmbeddingDTO;

public interface ModeloEmbeddingService {
    List<ModeloEmbeddingDTO> listarActivos();
    ModeloEmbeddingDTO obtenerPorId(Long id);
}
