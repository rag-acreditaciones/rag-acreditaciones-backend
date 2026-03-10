package org.simarro.rag_daw.documentos.service;

import java.util.List;

import org.simarro.rag_daw.documentos.model.dto.SeccionTematicaDTO;

public interface SeccionTematicaService {

    List<SeccionTematicaDTO> findAll();
}
