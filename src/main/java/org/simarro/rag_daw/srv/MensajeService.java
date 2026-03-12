package org.simarro.rag_daw.srv;

import org.simarro.rag_daw.model.dto.MensajeDTO;
import org.simarro.rag_daw.model.dto.PreguntaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MensajeService {
    MensajeDTO enviarPregunta(PreguntaDTO preguntaDTO);
    Page<MensajeDTO> findByConversacionId(Long conversacionId, Pageable pageable);
}
