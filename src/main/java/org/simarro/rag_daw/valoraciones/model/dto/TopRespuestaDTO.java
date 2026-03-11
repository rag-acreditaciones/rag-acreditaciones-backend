package org.simarro.rag_daw.valoraciones.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopRespuestaDTO {

    private Long mensajeId;
    private String textoResumido;
    private Long valoracionesPositivas;
    private Long valoracionesNegativas;
    private Long conversacionId;
}