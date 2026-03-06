package org.simarro.rag_daw.valoraciones.srv;

import java.util.List;

import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteEstadoDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionDTO;

public interface ReporteService {
    ReporteDTO crearReporte(ReporteCreateDTO dto, String emailUsuario);
    PaginaResponse<ReporteDTO> listarReportes(String estado, String motivo, int page, int size, String[] sort) throws FiltroException;
    ReporteDTO cambiarEstado(Long id, ReporteEstadoDTO dto);
}