package org.simarro.rag_daw.rag.srv;

import org.simarro.rag_daw.rag.model.dto.ChatResponseDTO;

import reactor.core.publisher.Flux;

public interface ChatRagService {
    ChatResponseDTO preguntar(String pregunta, String seccion);
    ChatResponseDTO preguntar(String pregunta, String seccion, Long ragConfiguracionId);
    Flux<String> preguntarStream(String pregunta, String seccion, Long ragConfiguracionId);
}