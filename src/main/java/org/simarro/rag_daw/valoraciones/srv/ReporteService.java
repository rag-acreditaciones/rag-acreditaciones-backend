package org.simarro.rag_daw.valoraciones.srv;

import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteEstadoDTO;

public interface ReporteService {

    /**
     * Crea un nuevo reporte
     * @param dto Datos del reporte
     * @param emailUsuario Email del usuario autenticado
     * @return Reporte creado
     */
    ReporteDTO crearReporte(ReporteCreateDTO dto, String emailUsuario);

    /**
     * Lista reportes con filtros y paginación
     * @param estado Filtro por estado
     * @param motivo Filtro por motivo
     * @param page Número de página
     * @param size Tamaño de página
     * @param sort Criterios de ordenación
     * @return Página de reportes
     * @throws FiltroException Si hay error en los filtros
     */
    PaginaResponse<ReporteDTO> listarReportes(
            String estado,
            String motivo,
            int page,
            int size,
            String[] sort
    ) throws FiltroException;

    /**
     * Cambia el estado de un reporte
     * @param id ID del reporte
     * @param dto Nuevo estado
     * @return Reporte actualizado
     */
    ReporteDTO cambiarEstado(Long id, ReporteEstadoDTO dto);

    /**
     * Elimina un reporte
     * @param id ID del reporte
     */
    void eliminarReporte(Long id);
}