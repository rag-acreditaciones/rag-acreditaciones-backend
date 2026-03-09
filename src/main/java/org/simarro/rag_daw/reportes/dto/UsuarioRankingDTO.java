package org.simarro.rag_daw.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsuarioRankingDTO {
    private Long usuarioId;
    private String nombre;
    private Long numDocumentos;
    private Long numConversaciones;
}

