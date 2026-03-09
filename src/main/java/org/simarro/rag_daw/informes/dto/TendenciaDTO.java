package org.simarro.rag_daw.informes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TendenciaDTO {
    private String fecha;       // formato: "2025-02-14"
    private Long valor;
}

