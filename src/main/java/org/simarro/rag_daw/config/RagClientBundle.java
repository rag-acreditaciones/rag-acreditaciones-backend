package org.simarro.rag_daw.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.simarro.rag_daw.rag.model.db.RagConfiguracionDb;

public record RagClientBundle(
    RagConfiguracionDb configuracion,
    EmbeddingModel embeddingModel,
    ChatModel llmModel,
    VectorStore vectorStore
) {}