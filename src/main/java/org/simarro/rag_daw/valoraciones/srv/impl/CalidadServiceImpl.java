package org.simarro.rag_daw.valoraciones.srv.impl;

import java.util.List;

import org.simarro.rag_daw.valoraciones.model.db.ReporteRespuestaDb;
import org.simarro.rag_daw.valoraciones.model.db.ValoracionDb;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadEvolucionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadResumenDTO;
import org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO;
import org.simarro.rag_daw.valoraciones.repository.ReporteRepository;
import org.simarro.rag_daw.valoraciones.repository.ValoracionRepository;
import org.simarro.rag_daw.valoraciones.srv.CalidadService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalidadServiceImpl implements CalidadService {

    private final ValoracionRepository valoracionRepository;
    private final ReporteRepository reporteRepository;

    @Override
    public CalidadResumenDTO getResumen() {

        long totalValoraciones = valoracionRepository.count();

        long positivas = valoracionRepository.countByValoracion(
                ValoracionDb.TipoValoracion.POSITIVA);

        long negativas = valoracionRepository.countByValoracion(
                ValoracionDb.TipoValoracion.NEGATIVA);

        double ratioCalidad = totalValoraciones == 0
                ? 0.0
                : (double) positivas / totalValoraciones;

        long totalReportes = reporteRepository.count();

        long reportesPendientes = reporteRepository.countByEstado(
                ReporteRespuestaDb.EstadoReporte.PENDIENTE);

        return new CalidadResumenDTO(
                totalValoraciones,
                positivas,
                negativas,
                ratioCalidad,
                totalReportes,
                reportesPendientes
        );
    }

    @Override
    public List<CalidadPorSeccionDTO> getCalidadPorSeccion() {
        return valoracionRepository.getCalidadPorSeccion();
    }

    @Override
    public List<TopRespuestaDTO> getTopRespuestas(String tipo, int limit) {

        if ("PEOR".equalsIgnoreCase(tipo)) {
            return valoracionRepository.getTopPeoresRespuestas(limit);
        }

        return valoracionRepository.getTopMejoresRespuestas(limit);
    }

    @Override
    public List<CalidadEvolucionDTO> getEvolucion(
            String fechaDesde,
            String fechaHasta,
            String agrupacion) {

        return valoracionRepository.getEvolucionCalidad(
                fechaDesde,
                fechaHasta,
                agrupacion
        );
    }
}