package org.simarro.rag_daw.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.simarro.rag_daw.model.enums.TipoMensaje;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {
    private Long id;
    private Long conversacionId;
    @Enumerated(EnumType.STRING)
    private TipoMensaje tipo;
    private String contenido;
    private List<Long> chunksUtilizados;
    private LocalDateTime fecha;
}
