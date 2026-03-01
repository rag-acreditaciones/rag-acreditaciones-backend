package org.simarro.rag_daw.rag.srv;

import java.util.List;

import org.simarro.rag_daw.rag.model.dto.ModeloLlmDTO;

public interface ModeloLlmService {
    List<ModeloLlmDTO> listarActivos();
    ModeloLlmDTO obtenerPorId(Long id);
}
