package org.simarro.rag_daw.reportes.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorasPuntaDTO {
    private Integer hora;
    private Long preguntas;
}