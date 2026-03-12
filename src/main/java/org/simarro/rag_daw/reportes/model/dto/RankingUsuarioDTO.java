package org.simarro.rag_daw.reportes.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingUsuarioDTO {
    private String email;
    private String nombre;
    private Long docsSubidos;
    private Long conversaciones;
    private Long total;
}