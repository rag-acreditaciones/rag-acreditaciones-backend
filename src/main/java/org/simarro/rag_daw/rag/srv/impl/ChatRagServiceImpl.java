package org.simarro.rag_daw.rag.srv.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.simarro.rag_daw.config.RagClientBundle;
import org.simarro.rag_daw.config.RagDynamicConfig;
import org.simarro.rag_daw.rag.model.db.RagConfiguracionDb;
import org.simarro.rag_daw.rag.model.dto.ChatResponseDTO;
import org.simarro.rag_daw.rag.srv.ChatRagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class ChatRagServiceImpl implements ChatRagService {

        private final RagDynamicConfig ragDynamicConfig;

        private static final String SYSTEM_PROMPT = """
                        Eres un asistente experto en acreditaciones de competencias profesionales
                        de informática en España (PEAC). Responde SIEMPRE en español.
                        Sé conciso y preciso. Cita la fuente cuando sea posible.
                        """;

        private static final String ADVISOR_PROMPT = """
                        A continuación se proporciona información de contexto:
                        ---------------------
                        {contexto}
                        ---------------------
                        Usando ÚNICAMENTE la información del contexto anterior,
                        responde en español de forma clara y detallada la siguiente pregunta:

                        {pregunta}
                        """;

        public ChatRagServiceImpl(RagDynamicConfig ragDynamicConfig) {
                this.ragDynamicConfig = ragDynamicConfig;
        }

        // --- MÉTODOS DE LA INTERFAZ ---

        @Override
        public ChatResponseDTO preguntar(String pregunta, String seccion) {
                // Llamada segura: si ragConfiguracionId es null,
                // RagDynamicConfig gestionará la configuración por defecto.
                return preguntar(pregunta, seccion, null);
        }

        /**
         * ANTES:
         * public ChatResponseDTO preguntar(String pregunta, String seccion, Long
         * ragConfiguracionId) {
         * long inicio = System.currentTimeMillis();
         * 
         * RagClientBundle bundle =
         * ragDynamicConfig.crearClienteRag(ragConfiguracionId);
         * SearchRequest baseRequest = buildSearchRequest(seccion, null);
         * 
         * ChatClient chatClient = buildChatClient(bundle, baseRequest);
         * 
         * // 1. Obtener respuesta del LLM
         * String respuesta = chatClient.prompt()
         * .user(pregunta)
         * .call()
         * .content();
         * 
         * long tiempoMs = System.currentTimeMillis() - inicio;
         * 
         * // 2. Obtener los IDs de los chunks para trazabilidad
         * List<String> chunkIds = obtenerIdsDeSimilitud(bundle, pregunta, seccion);
         * 
         * // 3. Mapear al DTO
         * RagConfiguracionDb config = bundle.configuracion();
         * return new ChatResponseDTO(
         * respuesta,
         * chunkIds,
         * tiempoMs,
         * config.getId(),
         * config.getNombre(),
         * config.getModeloLlm().getModeloId()
         * );
         * }
         * 
         */
        @Override
        public ChatResponseDTO preguntar(String pregunta, String seccion, Long ragConfiguracionId) {
                long inicio = System.currentTimeMillis();

                RagClientBundle bundle = ragDynamicConfig.crearClienteRag(ragConfiguracionId);

                // 1. Buscar chunks relevantes
                SearchRequest searchRequest = buildSearchRequest(seccion, pregunta);
                List<Document> chunks = bundle.vectorStore().similaritySearch(searchRequest);
                List<String> chunkIds = chunks.stream().map(Document::getId).collect(Collectors.toList());

                // 2. Construir contexto a partir de los chunks
                String contexto = chunks.stream()
                                .map(Document::getText)
                                .collect(Collectors.joining("\n\n---\n\n"));

                // 3. Construir el prompt completo con contexto inyectado
                String promptConContexto = ADVISOR_PROMPT
                                .replace("{contexto}", contexto)
                                .replace("{pregunta}", pregunta);

                // 4. Llamar al LLM (sin Advisor, control total del prompt)
                ChatClient chatClient = buildChatClient(bundle);
                String respuesta = chatClient.prompt()
                                .user(promptConContexto)
                                .call()
                                .content();

                long tiempoMs = System.currentTimeMillis() - inicio;

                RagConfiguracionDb config = bundle.configuracion();
                return new ChatResponseDTO(
                                respuesta, chunkIds, tiempoMs,
                                config.getId(), config.getNombre(),
                                config.getModeloLlm().getModeloId());
        }

        /**
         * ANTES:
         * public Flux<String> preguntarStream(String pregunta, String seccion, Long
         * ragConfiguracionId) {
         * RagClientBundle bundle =
         * ragDynamicConfig.crearClienteRag(ragConfiguracionId);
         * SearchRequest baseRequest = buildSearchRequest(seccion, null);
         * 
         * return buildChatClient(bundle, baseRequest)
         * .prompt()
         * .user(pregunta)
         * .stream()
         * .content();
         * }
         */

        @Override
        public Flux<String> preguntarStream(String pregunta, String seccion, Long ragConfiguracionId) {
                RagClientBundle bundle = ragDynamicConfig.crearClienteRag(ragConfiguracionId);

                SearchRequest searchRequest = buildSearchRequest(seccion, pregunta);
                List<Document> chunks = bundle.vectorStore().similaritySearch(searchRequest);

                String contexto = chunks.stream()
                                .map(Document::getText)
                                .collect(Collectors.joining("\n\n---\n\n"));

                String promptConContexto = ADVISOR_PROMPT
                                .replace("{contexto}", contexto)
                                .replace("{pregunta}", pregunta);

                return buildChatClient(bundle)
                                .prompt()
                                .user(promptConContexto)
                                .stream()
                                .content();
        }

        // --- MÉTODOS PRIVADOS AUXILIARES (Refactorizados) ---

        /**
         * Centraliza la creación del ChatClient con sus Advisors.
         * ANTES:
         * private ChatClient buildChatClient(RagClientBundle bundle, SearchRequest
         * searchRequest) {
         * return ChatClient.builder(bundle.llmModel())
         * .defaultAdvisors(
         * QuestionAnswerAdvisor.builder(bundle.vectorStore())
         * .searchRequest(searchRequest)
         * .build()
         * )
         * .defaultSystem(SYSTEM_PROMPT)
         * .build();
         * }
         */
        private ChatClient buildChatClient(RagClientBundle bundle) {
                return ChatClient.builder(bundle.llmModel())
                                .defaultSystem(SYSTEM_PROMPT)
                                .build();
        }

        /**
         * Centraliza la lógica de filtrado y búsqueda vectorial.
         */
        private SearchRequest buildSearchRequest(String seccion, String pregunta) {
                SearchRequest.Builder builder = SearchRequest.builder()
                                .topK(4)
                                .similarityThreshold(0.3);

                // Filtrado por metadatos (opcional)
                if (seccion != null && !seccion.isBlank()) {
                        builder.filterExpression("seccionTematica == '" + seccion + "'");
                }

                // Añadir la query solo si se proporciona (para búsqueda manual de IDs)
                if (pregunta != null) {
                        builder.query(pregunta);
                }

                return builder.build();
        }

        /**
         * Recupera manualmente los IDs de los documentos más similares para incluirlos
         * en el DTO.
         */
        private List<String> obtenerIdsDeSimilitud(RagClientBundle bundle, String pregunta, String seccion) {
                SearchRequest requestConQuery = buildSearchRequest(seccion, pregunta);
                return bundle.vectorStore()
                                .similaritySearch(requestConQuery)
                                .stream()
                                .map(Document::getId)
                                .collect(Collectors.toList());
        }
}