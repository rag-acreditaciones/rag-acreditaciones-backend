package org.simarro.rag_daw.valoraciones.srv.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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

        if (limit <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor que 0");
        }

        List<TopRespuestaDTO> resultados;

        if ("PEOR".equalsIgnoreCase(tipo)) {
            resultados = valoracionRepository.getTopPeoresRespuestas();
        } else {
            resultados = valoracionRepository.getTopMejoresRespuestas();
        }

        return resultados.stream()
                .limit(limit)
                .toList();
    }

   @Override
public List<CalidadEvolucionDTO> getEvolucion(String fechaDesde, String fechaHasta, String agrupacion) {
    // Parsear fechas
    LocalDate desde = LocalDate.parse(fechaDesde);
    LocalDate hasta = LocalDate.parse(fechaHasta);
    
    LocalDateTime fechaHoraDesde = desde.atStartOfDay();
    LocalDateTime fechaHoraHasta = hasta.atTime(LocalTime.MAX);
    
    // Obtener valoraciones
    List<ValoracionDb> valoraciones = valoracionRepository
            .findEnRangoFechas(fechaHoraDesde, fechaHoraHasta);
    
    // Si no hay valoraciones, devolver lista vacía
    if (valoraciones.isEmpty()) {
        return new ArrayList<>();
    }
    
    // Agrupar por fecha según agrupacion
    Map<LocalDate, List<ValoracionDb>> agrupadas = new LinkedHashMap<>();
    
    for (ValoracionDb v : valoraciones) {
        if (v.getFechaCreacion() == null) continue;
        
        LocalDate fecha = v.getFechaCreacion().toLocalDate();
        LocalDate clave;
        
        if ("MES".equalsIgnoreCase(agrupacion)) {
            clave = fecha.withDayOfMonth(1);
        } else if ("SEMANA".equalsIgnoreCase(agrupacion)) {
            clave = fecha.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        } else {
            clave = fecha; // DIA
        }
        
        agrupadas.computeIfAbsent(clave, k -> new ArrayList<>()).add(v);
    }
    
    // Crear DTOs
    List<CalidadEvolucionDTO> resultado = new ArrayList<>();
    
    for (Map.Entry<LocalDate, List<ValoracionDb>> entry : agrupadas.entrySet()) {
        LocalDate fecha = entry.getKey();
        List<ValoracionDb> lista = entry.getValue();
        
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
        
        resultado.add(new CalidadEvolucionDTO(fecha, positivas, negativas, ratio, 0L));
    }
    
    // Ordenar por fecha
    resultado.sort(Comparator.comparing(CalidadEvolucionDTO::getFecha));
    
    return resultado;
}

    private LocalDate obtenerClaveFecha(LocalDate fecha, String agrupacion) {
        return switch (agrupacion) {
            case "SEMANA" -> fecha.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
            case "MES" -> fecha.withDayOfMonth(1);
            default -> fecha;
        };
    }
}