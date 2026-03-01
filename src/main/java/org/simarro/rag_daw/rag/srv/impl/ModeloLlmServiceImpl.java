package org.simarro.rag_daw.rag.srv.impl;

import java.util.List;

import org.simarro.rag_daw.rag.model.dto.ModeloLlmDTO;
import org.simarro.rag_daw.rag.repository.ModeloLlmRepository;
import org.simarro.rag_daw.rag.srv.ModeloLlmService;
import org.simarro.rag_daw.rag.srv.mapper.ModeloLlmMapper;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ModeloLlmServiceImpl implements ModeloLlmService {

    private final ModeloLlmRepository repository;
    private final ModeloLlmMapper mapper;

    public ModeloLlmServiceImpl(ModeloLlmRepository repository,
                                 ModeloLlmMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ModeloLlmDTO> listarActivos() {
        return mapper.modeloLlmDbListToModeloLlmDTOList(
                repository.findByActivoTrue());
    }

    @Override
    public ModeloLlmDTO obtenerPorId(Long id) {
        return repository.findById(id)
            .map(mapper::modeloLlmDbToModeloLlmDTO)
            .orElseThrow(() -> new EntityNotFoundException(
                "Modelo LLM no encontrado: " + id));
    }
}
