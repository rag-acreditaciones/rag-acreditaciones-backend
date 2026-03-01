package org.simarro.rag_daw.config;

// Spring
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

// JPA
import jakarta.persistence.EntityNotFoundException;

// Spring AI — Interfaces comunes
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.vectorstore.VectorStore;

// Spring AI — Ollama
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;

// Spring AI — OpenAI (también usado para vLLM con API compatible)
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

// Spring AI — PgVector
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;

// Proyecto
import org.simarro.rag_daw.rag.repository.RagConfiguracionRepository;
import org.simarro.rag_daw.rag.model.db.RagConfiguracionDb;

@Configuration
public class RagDynamicConfig {

    private final RagConfiguracionRepository ragConfigRepository;
    private final JdbcTemplate jdbcTemplate;

    public RagDynamicConfig(RagConfiguracionRepository ragConfigRepository,
            JdbcTemplate jdbcTemplate) {
        this.ragConfigRepository = ragConfigRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public RagClientBundle crearClienteRag(Long ragConfigId) {
        RagConfiguracionDb config = (ragConfigId != null)
                ? ragConfigRepository.findById(ragConfigId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Configuración RAG no encontrada: " + ragConfigId))
                : ragConfigRepository.findByPorDefectoTrue()
                        .orElseThrow(() -> new IllegalStateException(
                                "No hay configuración RAG por defecto"));

        EmbeddingModel embeddingModel = crearEmbeddingModel(config);
        ChatModel llmModel = crearLlmModel(config);
        VectorStore vectorStore = crearVectorStore(config, embeddingModel);

        return new RagClientBundle(config, embeddingModel, llmModel, vectorStore);
    }

    private EmbeddingModel crearEmbeddingModel(RagConfiguracionDb config) {
        String baseUrl = "http://" + config.getHostEmbedding()
                + ":" + config.getPuertoEmbedding();
        return switch (config.getModeloEmbedding().getProveedor()) {
            case OLLAMA -> OllamaEmbeddingModel.builder()
                    .ollamaApi(OllamaApi.builder().baseUrl(baseUrl).build())
                    .defaultOptions(OllamaOptions.builder()
                            .model(config.getModeloEmbedding().getModeloId())
                            .build())
                    .build();
            case VLLM, OPENAI -> new OpenAiEmbeddingModel(
                    OpenAiApi.builder()
                            .baseUrl(baseUrl)
                            .apiKey("no-key-needed")
                            .build(),
                    MetadataMode.EMBED,
                    OpenAiEmbeddingOptions.builder()
                            .model(config.getModeloEmbedding().getModeloId())
                            .build());
        };
    }

    private ChatModel crearLlmModel(RagConfiguracionDb config) {
        String baseUrl = "http://" + config.getHostLlm()
                + ":" + config.getPuertoLlm();
        return switch (config.getModeloLlm().getProveedor()) {
            case OLLAMA -> OllamaChatModel.builder()
                    .ollamaApi(OllamaApi.builder().baseUrl(baseUrl).build())
                    .defaultOptions(OllamaOptions.builder()
                            .model(config.getModeloLlm().getModeloId())
                            .temperature(0.7)
                            .build())
                    .build();
            case VLLM -> OpenAiChatModel.builder()
                    .openAiApi(OpenAiApi.builder()
                            .baseUrl(baseUrl + "/v1")
                            .apiKey("no-key-needed")
                            .build())
                    .defaultOptions(OpenAiChatOptions.builder()
                            .model(config.getModeloLlm().getModeloId())
                            .temperature(0.7)
                            .build())
                    .build();
            case OPENAI -> OpenAiChatModel.builder()
                    .openAiApi(OpenAiApi.builder()
                            .baseUrl(baseUrl)
                            .apiKey(System.getenv("OPENAI_API_KEY"))
                            .build())
                    .defaultOptions(OpenAiChatOptions.builder()
                            .model(config.getModeloLlm().getModeloId())
                            .temperature(0.7)
                            .build())
                    .build();
        };
    }

    private VectorStore crearVectorStore(RagConfiguracionDb config,
            EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .schemaName("public")
                .vectorTableName(config.getEsquemaVectorStore())
                .dimensions(config.getModeloEmbedding().getDimensiones())
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .initializeSchema(false)
                .build();
    }
}