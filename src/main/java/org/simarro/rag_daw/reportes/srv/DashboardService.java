package org.simarro.rag_daw.reportes.srv;

import java.util.List;

import org.simarro.rag_daw.reportes.model.dto.*;

public interface DashboardService {

    DashboardResumenDTO getResumenGlobal();

    List<DistribucionDTO> getDocumentosPorSeccion();

    List<DistribucionDTO> getDocumentosPorEstado();

    List<EvolucionDTO> getDocumentosEvolucion(String fechaDesde, String fechaHasta, String agrupacion);

    List<DistribucionDTO> getUsuariosPorRol();

    List<DistribucionDTO> getChatsPorSeccion();

    List<ActividadDiariaDTO> getActividadDiaria(String fechaDesde, String fechaHasta);

    List<HorasPuntaDTO> getHorasPunta();

    List<RankingUsuarioDTO> getRankingUsuarios(int limit, String criterio);

    List<ActividadRecienteDTO> getActividadReciente();
    
}
