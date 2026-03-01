package org.simarro.rag_daw.rag.model.enums;

public enum Proveedor {
    OLLAMA("Servidor Ollama — API propia + compatible OpenAI. "
            + "Sencillo de desplegar, ideal para desarrollo local y servidores con GPU."),
    VLLM("Motor de inferencia vLLM — API compatible OpenAI. "
            + "Alto rendimiento con PagedAttention, ideal para producción con GPU."),
    OPENAI("API de OpenAI — Requiere API key y conexión a internet. "
            + "Modelos propietarios (GPT-4, GPT-3.5) con alta calidad.");

    private final String descripcion;

    Proveedor(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
