package org.simarro.rag_daw.valoraciones.srv;

import java.util.List;

import org.simarro.rag_daw.valoraciones.model.dto.CalidadEvolucionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadResumenDTO;
import org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO;

public interface CalidadService {

    CalidadResumenDTO getResumen();

    List<CalidadPorSeccionDTO> getCalidadPorSeccion();

    List<TopRespuestaDTO> getTopRespuestas(String tipo, int limit);

    List<CalidadEvolucionDTO> getEvolucion(String fechaDesde, String fechaHasta, String agrupacion);

}