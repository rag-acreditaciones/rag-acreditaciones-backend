package org.simarro.rag_daw.model.dto;

import java.time.LocalDateTime;

import org.simarro.rag_daw.model.db.MensajeDb;
import org.simarro.rag_daw.model.enums.EstadoConversacion;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConversacionResponseDTO {
    private Long id;
    private Long usuarioId;
    private String titulo;
    private String seccionTematica;
    @Enumerated(EnumType.STRING)
    private EstadoConversacion estado;
    private LocalDateTime fechaCreacion;

    private MensajeDb ultimoMensaje;
}
