package org.simarro.rag_daw.valoraciones.srv.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simarro.rag_daw.valoraciones.model.db.ValoracionDb;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadEvolucionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadResumenDTO;
import org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO;
import org.simarro.rag_daw.valoraciones.repository.CalidadRepository;
import org.simarro.rag_daw.valoraciones.repository.ReporteRepository;
import org.simarro.rag_daw.valoraciones.repository.ValoracionRepository;
import org.simarro.rag_daw.valoraciones.srv.CalidadService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalidadServiceImpl implements CalidadService {

    private final CalidadRepository calidadRepository;
    private final ValoracionRepository valoracionRepository;
    private final ReporteRepository reporteRepository;

    @Override
    public CalidadResumenDTO getResumen() {
        long totalValoraciones = calidadRepository.countTotalValoraciones();
        long positivas = calidadRepository.countPositivas();
        long negativas = calidadRepository.countNegativas();
        double ratioCalidad = totalValoraciones == 0 ? 0.0 : (double) positivas / totalValoraciones;
        long totalReportes = calidadRepository.countTotalReportes();
        long reportesPendientes = calidadRepository.countReportesPendientes();

        return new CalidadResumenDTO(
            totalValoraciones, positivas, negativas, 
            ratioCalidad, totalReportes, reportesPendientes
        );
    }

    @Override
    public List<CalidadPorSeccionDTO> getCalidadPorSeccion() {
        return valoracionRepository.getCalidadPorSeccion();
    }

    @Override
    public List<TopRespuestaDTO> getTopRespuestas(String tipo, int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor que 0");
        }

        List<TopRespuestaDTO> resultados;
        if ("PEOR".equalsIgnoreCase(tipo)) {
            resultados = valoracionRepository.getTopPeoresRespuestas();
        } else {
            resultados = valoracionRepository.getTopMejoresRespuestas();
        }

        return resultados.stream().limit(limit).toList();
    }

    @Override
public List<CalidadEvolucionDTO> getEvolucion(String fechaDesde, String fechaHasta, String agrupacion) {
    List<CalidadEvolucionDTO> resultado = new ArrayList<>();
    
    try {
        // Parsear fechas
        LocalDate desde = LocalDate.parse(fechaDesde);
        LocalDate hasta = LocalDate.parse(fechaHasta);
        
        LocalDateTime fechaHoraDesde = desde.atStartOfDay();
        LocalDateTime fechaHoraHasta = hasta.atTime(LocalTime.MAX);
        
        // Consultar repositorio
        List<ValoracionDb> valoraciones = calidadRepository.findValoracionesEnRango(fechaHoraDesde, fechaHoraHasta);
        
        if (valoraciones == null || valoraciones.isEmpty()) {
            return resultado;
        }
        
        // Agrupar por fecha según agrupacion
        Map<String, List<ValoracionDb>> grupos = new HashMap<>();
        
        for (ValoracionDb v : valoraciones) {
            if (v.getFechaCreacion() == null) {
                continue;
            }
            
            LocalDate fechaValoracion = v.getFechaCreacion().toLocalDate();
            String clave = obtenerClaveAgrupacion(fechaValoracion, agrupacion);
            
            if (!grupos.containsKey(clave)) {
                grupos.put(clave, new ArrayList<>());
            }
            grupos.get(clave).add(v);
        }
        
        // Crear DTOs
        for (Map.Entry<String, List<ValoracionDb>> entry : grupos.entrySet()) {
            String clave = entry.getKey();
            List<ValoracionDb> lista = entry.getValue();
            
            LocalDate fechaGrupo = LocalDate.parse(clave);
            long positivas = 0;
            long negativas = 0;
            
            for (ValoracionDb v : lista) {
                if (v.getValoracion() == ValoracionDb.TipoValoracion.POSITIVA) {
                    positivas++;
                } else if (v.getValoracion() == ValoracionDb.TipoValoracion.NEGATIVA) {
                    negativas++;
                }
            }
            
            double ratio = (positivas + negativas) == 0 ? 0.0 : (double) positivas / (positivas + negativas);
            
            resultado.add(new CalidadEvolucionDTO(fechaGrupo, positivas, negativas, ratio, 0L));
        }
        
        // Ordenar por fecha
        resultado.sort(Comparator.comparing(CalidadEvolucionDTO::getFecha));
        
    } catch (Exception e) {
        // Puedes loguear el error si quieres, pero sin System.out
        // logger.error("Error en getEvolucion", e);
    }
    
    return resultado;
}

private String obtenerClaveAgrupacion(LocalDate fecha, String agrupacion) {
    if ("MES".equalsIgnoreCase(agrupacion)) {
        return fecha.withDayOfMonth(1).toString();
    } else if ("SEMANA".equalsIgnoreCase(agrupacion)) {
        // Calcular el lunes de la semana
        int diaSemana = fecha.getDayOfWeek().getValue();
        LocalDate lunes = fecha.minusDays(diaSemana - 1);
        return lunes.toString();
    } else {
        return fecha.toString(); // DIA
    }
}
}