package org.simarro.rag_daw.model.dto;

import java.time.LocalDateTime;

import org.simarro.rag_daw.model.enums.EstadoConversacion;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConversacionCreateDTO {
    private Long id;
    private Long usuarioId;
    private String titulo = "Nueva conversación";
    private String seccionTematica;
    @Enumerated(EnumType.STRING)
    private EstadoConversacion estado = EstadoConversacion.ACTIVA;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
