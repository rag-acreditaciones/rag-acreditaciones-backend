package org.simarro.rag_daw.valoraciones.srv;

import java.util.List;

import org.simarro.rag_daw.valoraciones.model.dto.CalidadEvolucionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadResumenDTO;
import org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO;

public interface CalidadService {

    /**
     * Obtiene resumen global de métricas de calidad
     * @return Resumen global
     */
    CalidadResumenDTO getResumen();

    /**
     * Obtiene métricas de calidad agrupadas por sección
     * @return Lista de métricas por sección
     */
    List<CalidadPorSeccionDTO> getCalidadPorSeccion();

    /**
     * Obtiene ranking de respuestas mejor/peor valoradas
     * @param tipo "MEJOR" o "PEOR"
     * @param limit Número máximo de resultados
     * @return Lista de respuestas del ranking
     */
    List<TopRespuestaDTO> getTopRespuestas(String tipo, int limit);

    /**
     * Obtiene evolución temporal de calidad
     * @param fechaDesde Fecha inicial (YYYY-MM-DD)
     * @param fechaHasta Fecha final (YYYY-MM-DD)
     * @param agrupacion DIA, SEMANA o MES
     * @return Lista de datos evolutivos
     */
    List<CalidadEvolucionDTO> getEvolucion(String fechaDesde, String fechaHasta, String agrupacion);
}