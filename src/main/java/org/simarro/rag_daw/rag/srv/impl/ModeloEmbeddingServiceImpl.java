package org.simarro.rag_daw.rag.srv.impl;

import java.util.List;

import org.simarro.rag_daw.rag.model.dto.ModeloEmbeddingDTO;
import org.simarro.rag_daw.rag.repository.ModeloEmbeddingRepository;
import org.simarro.rag_daw.rag.srv.ModeloEmbeddingService;
import org.simarro.rag_daw.rag.srv.mapper.ModeloEmbeddingMapper;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ModeloEmbeddingServiceImpl implements ModeloEmbeddingService {

    private final ModeloEmbeddingRepository repository;
    private final ModeloEmbeddingMapper mapper;

    public ModeloEmbeddingServiceImpl(ModeloEmbeddingRepository repository,
                                       ModeloEmbeddingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ModeloEmbeddingDTO> listarActivos() {
        return mapper.modeloEmbeddingDbListToModeloEmbeddingDTOList(
                repository.findByActivoTrue());
    }

    @Override
    public ModeloEmbeddingDTO obtenerPorId(Long id) {
        return repository.findById(id)
            .map(mapper::modeloEmbeddingDbToModeloEmbeddingDTO)
            .orElseThrow(() -> new EntityNotFoundException(
                "Modelo de embedding no encontrado: " + id));
    }
}
