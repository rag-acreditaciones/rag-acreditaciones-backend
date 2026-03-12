package org.simarro.rag_daw.reportes.srv.impl;

import org.simarro.rag_daw.reportes.repository.DashboardDocumentoRepository;
import org.simarro.rag_daw.reportes.repository.DashboardUsuarioRepository;
import org.simarro.rag_daw.reportes.model.dto.*;
import org.simarro.rag_daw.reportes.srv.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardDocumentoRepository documentoRepo;

    @Autowired
    private DashboardUsuarioRepository usuarioRepo;

    // ── RESUMEN GLOBAL ───────────────────────────────────────────────
    @Override
    public DashboardResumenDTO getResumenGlobal() {
        DashboardResumenDTO dto = new DashboardResumenDTO();
        dto.setTotalDocumentos(documentoRepo.countTotal());
        dto.setTotalUsuarios(usuarioRepo.countTotal());
        // Demo hasta que otros equipos entreguen sus entidades
        dto.setTotalChunks(0L);
        dto.setTotalConversaciones(0L);
        dto.setTotalPreguntas(0L);
        dto.setRatioCalidad(0.0);
        return dto;
    }

    // ── DOCUMENTOS ───────────────────────────────────────────────────
    @Override
    public List<DistribucionDTO> getDocumentosPorSeccion() {
        List<DistribucionDTO> resultado = new ArrayList<>();
        for (Object[] fila : documentoRepo.countPorSeccion()) {
            resultado.add(new DistribucionDTO((String) fila[0], (Long) fila[1]));
        }
        return resultado;
    }

    @Override
    public List<DistribucionDTO> getDocumentosPorEstado() {
        List<DistribucionDTO> resultado = new ArrayList<>();
        for (Object[] fila : documentoRepo.countPorEstado()) {
            resultado.add(new DistribucionDTO(fila[0].toString(), (Long) fila[1]));
        }
        return resultado;
    }

    @Override
    public List<EvolucionDTO> getDocumentosEvolucion(String fechaDesde, String fechaHasta, String agrupacion) {
        LocalDateTime desde = LocalDateTime.parse(fechaDesde + "T00:00:00");
        LocalDateTime hasta = LocalDateTime.parse(fechaHasta + "T23:59:59");
        List<EvolucionDTO> resultado = new ArrayList<>();
        for (Object[] fila : documentoRepo.evolucionPorFecha(desde, hasta)) {
            resultado.add(new EvolucionDTO(fila[0].toString(), (Long) fila[1]));
        }
        return resultado;
    }

    // ── USUARIOS ─────────────────────────────────────────────────────
    @Override
    public List<DistribucionDTO> getUsuariosPorRol() {
        List<DistribucionDTO> resultado = new ArrayList<>();
        for (Object[] fila : usuarioRepo.countPorRol()) {
            resultado.add(new DistribucionDTO(fila[0].toString(), (Long) fila[1]));
        }
        return resultado;
    }

    // ── DEMO hasta que otros equipos entreguen ────────────────────────
    @Override
    public List<DistribucionDTO> getChatsPorSeccion() {
        return List.of(
            new DistribucionDTO("BD", 45L),
            new DistribucionDTO("Web", 30L),
            new DistribucionDTO("Programación", 25L),
            new DistribucionDTO("General", 10L)
        );
    }

    @Override
    public List<ActividadDiariaDTO> getActividadDiaria(String fechaDesde, String fechaHasta) {
        return List.of(
            new ActividadDiariaDTO("2026-03-04", 20L),
            new ActividadDiariaDTO("2026-03-05", 35L),
            new ActividadDiariaDTO("2026-03-06", 28L),
            new ActividadDiariaDTO("2026-03-07", 42L),
            new ActividadDiariaDTO("2026-03-08", 31L),
            new ActividadDiariaDTO("2026-03-09", 15L),
            new ActividadDiariaDTO("2026-03-10", 12L)
        );
    }

    @Override
    public List<HorasPuntaDTO> getHorasPunta() {
        return List.of(
            new HorasPuntaDTO(8,  35L), new HorasPuntaDTO(9,  52L),
            new HorasPuntaDTO(10, 48L), new HorasPuntaDTO(11, 43L),
            new HorasPuntaDTO(12, 30L), new HorasPuntaDTO(15, 55L),
            new HorasPuntaDTO(16, 62L), new HorasPuntaDTO(17, 45L)
        );
    }

    @Override
    public List<RankingUsuarioDTO> getRankingUsuarios(int limit, String criterio) {
        return List.of(
            new RankingUsuarioDTO("candidato@example.com", "Candidato", 12L, 8L,  20L),
            new RankingUsuarioDTO("profesor@example.com",  "Profesor",  15L, 2L,  17L),
            new RankingUsuarioDTO("asesor@example.com",    "Asesor",    10L, 12L, 22L)
        );
    }

    @Override
    public List<ActividadRecienteDTO> getActividadReciente() {
        return List.of(
            new ActividadRecienteDTO("Candidato", "SUBIDA_DOCUMENTO", "Manual_PEAC.pdf",  "2026-03-10T10:00:00"),
            new ActividadRecienteDTO("Asesor",    "PREGUNTA_RAG",     "¿Qué es el PEAC?", "2026-03-10T09:45:00"),
            new ActividadRecienteDTO("Candidato", "VALORACION",       "Respuesta #12",    "2026-03-10T09:30:00")
        );
    }
}