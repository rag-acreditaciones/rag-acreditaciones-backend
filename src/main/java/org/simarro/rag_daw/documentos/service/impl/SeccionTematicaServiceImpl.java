package org.simarro.rag_daw.documentos.service.impl;

import java.util.List;

import org.simarro.rag_daw.documentos.model.dto.SeccionTematicaDTO;
import org.simarro.rag_daw.documentos.repository.SeccionTematicaRepository;
import org.simarro.rag_daw.documentos.service.SeccionTematicaService;
import org.simarro.rag_daw.documentos.service.mapper.DocumentoMapper;
import org.springframework.stereotype.Service;

@Service
public class SeccionTematicaServiceImpl implements SeccionTematicaService {

    private final SeccionTematicaRepository repository;
    private final DocumentoMapper mapper;

    public SeccionTematicaServiceImpl(SeccionTematicaRepository repository,
            DocumentoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<SeccionTematicaDTO> findAll() {
        return mapper.toSeccionDTOList(repository.findAll());
    }
}
