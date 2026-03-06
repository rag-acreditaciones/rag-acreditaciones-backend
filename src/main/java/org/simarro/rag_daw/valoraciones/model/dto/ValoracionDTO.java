package org.simarro.rag_daw.valoraciones.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class ValoracionDTO {
    private Long id;
    private Long mensajeId;
    private Long usuarioId;
    private String valoracion;
    private String comentario;
    private LocalDateTime fechaCreacion;
}