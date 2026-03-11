package org.simarro.rag_daw.valoraciones.srv.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalidadServiceImpl implements CalidadService {

    private final CalidadRepository calidadRepository;
    private final ValoracionRepository valoracionRepository;
    private final ReporteRepository reporteRepository;

    @Override
    @Transactional(readOnly = true)
    public CalidadResumenDTO getResumen() {
        log.debug("Obteniendo resumen global de calidad");

        long totalValoraciones = calidadRepository.countTotalValoraciones();
        long positivas = calidadRepository.countPositivas();
        long negativas = calidadRepository.countNegativas();
        double ratioCalidad = totalValoraciones == 0 ? 0.0 : (double) positivas / totalValoraciones;
        long totalReportes = calidadRepository.countTotalReportes();
        long reportesPendientes = calidadRepository.countReportesPendientes();

        log.debug("Resumen: totalValoraciones={}, positivas={}, negativas={}, ratio={}, reportes={}, pendientes={}",
                totalValoraciones, positivas, negativas, ratioCalidad, totalReportes, reportesPendientes);

        return new CalidadResumenDTO(
            totalValoraciones, positivas, negativas, 
            ratioCalidad, totalReportes, reportesPendientes
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalidadPorSeccionDTO> getCalidadPorSeccion() {
        log.debug("Obteniendo métricas de calidad por sección");
        
        List<CalidadPorSeccionDTO> resultados = valoracionRepository.getCalidadPorSeccion();
        
        log.debug("Encontradas {} secciones con métricas", resultados.size());
        
        return resultados;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopRespuestaDTO> getTopRespuestas(String tipo, int limit) {
        log.debug("Obteniendo top respuestas - tipo: {}, limit: {}", tipo, limit);
        
        if (limit <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor que 0");
        }

        List<TopRespuestaDTO> resultados;
        if ("PEOR".equalsIgnoreCase(tipo)) {
            resultados = valoracionRepository.getTopPeoresRespuestas();
            log.debug("Obtenidas {} peores respuestas", resultados.size());
        } else {
            resultados = valoracionRepository.getTopMejoresRespuestas();
            log.debug("Obtenidas {} mejores respuestas", resultados.size());
        }

        List<TopRespuestaDTO> limitados = resultados.stream().limit(limit).toList();
        log.debug("Devolviendo {} respuestas (limit={})", limitados.size(), limit);
        
        return limitados;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalidadEvolucionDTO> getEvolucion(String fechaDesde, String fechaHasta, String agrupacion) {
        log.debug("Obteniendo evolución de calidad - desde: {}, hasta: {}, agrupacion: {}", 
                fechaDesde, fechaHasta, agrupacion);
        
        List<CalidadEvolucionDTO> resultado = new ArrayList<>();
        
        try {
            LocalDate desde = LocalDate.parse(fechaDesde);
            LocalDate hasta = LocalDate.parse(fechaHasta);
            
            LocalDateTime fechaHoraDesde = desde.atStartOfDay();
            LocalDateTime fechaHoraHasta = hasta.atTime(LocalTime.MAX);
            
            log.debug("Rango de fechas convertido: {} - {}", fechaHoraDesde, fechaHoraHasta);
            
            List<ValoracionDb> valoraciones = calidadRepository.findValoracionesEnRango(fechaHoraDesde, fechaHoraHasta);
            
            if (valoraciones == null || valoraciones.isEmpty()) {
                log.debug("No se encontraron valoraciones en el rango especificado");
                return resultado;
            }
            
            log.debug("Encontradas {} valoraciones en el rango", valoraciones.size());
            
            Map<String, List<ValoracionDb>> grupos = new HashMap<>();
            
            for (ValoracionDb v : valoraciones) {
                if (v.getFechaCreacion() == null) {
                    continue;
                }
                
                LocalDate fechaValoracion = v.getFechaCreacion().toLocalDate();
                String clave = obtenerClaveAgrupacion(fechaValoracion, agrupacion);
                
                grupos.computeIfAbsent(clave, k -> new ArrayList<>()).add(v);
            }
            
            log.debug("Agrupadas en {} grupos según agrupación '{}'", grupos.size(), agrupacion);
            
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
            
            resultado.sort(Comparator.comparing(CalidadEvolucionDTO::getFecha));
            log.debug("Evolución calculada con {} puntos de datos", resultado.size());
            
        } catch (DateTimeParseException e) {
            log.error("Error al parsear fechas: desde={}, hasta={}", fechaDesde, fechaHasta);
            throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD");
        } catch (Exception e) {
            log.error("Error inesperado al calcular evolución: {}", e.getMessage(), e);
            throw new RuntimeException("Error al calcular evolución de calidad", e);
        }
        
        return resultado;
    }

    private String obtenerClaveAgrupacion(LocalDate fecha, String agrupacion) {
        if ("MES".equalsIgnoreCase(agrupacion)) {
            return fecha.withDayOfMonth(1).toString();
        } else if ("SEMANA".equalsIgnoreCase(agrupacion)) {
            int diaSemana = fecha.getDayOfWeek().getValue();
            LocalDate lunes = fecha.minusDays(diaSemana - 1);
            return lunes.toString();
        } else {
            return fecha.toString();
        }
    }
}