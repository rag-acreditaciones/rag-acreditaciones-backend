package org.simarro.rag_daw.valoraciones.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionResumenDTO {
    private long totalPositivas;
    private long totalNegativas;
    private double ratio;
    private List<ValoracionDTO> detalles;
}