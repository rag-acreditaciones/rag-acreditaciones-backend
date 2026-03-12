package org.simarro.rag_daw.reportes.srv.impl;

import org.simarro.rag_daw.reportes.repository.DashboardChunkRepository;
import org.simarro.rag_daw.reportes.repository.DashboardConversacionRepository;
import org.simarro.rag_daw.reportes.repository.DashboardDocumentoRepository;
import org.simarro.rag_daw.reportes.repository.DashboardMensajeRepository;
import org.simarro.rag_daw.reportes.repository.DashboardUsuarioRepository;
import org.simarro.rag_daw.reportes.repository.DashboardValoracionesRepository;
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

    @Autowired
    private DashboardValoracionesRepository valoracionRepo;

    @Autowired
    private DashboardChunkRepository chunkRepo;

    @Autowired
    private DashboardConversacionRepository conversacionRepo;

    @Autowired
    private DashboardMensajeRepository mensajeRepo;

    // ── RESUMEN GLOBAL ───────────────────────────────────────────────
    @Override
    public DashboardResumenDTO getResumenGlobal() {
        DashboardResumenDTO dto = new DashboardResumenDTO();
        dto.setTotalDocumentos(documentoRepo.countTotal());
        dto.setTotalUsuarios(usuarioRepo.countTotal());
        dto.setTotalChunks(chunkRepo.countTotal());
        dto.setTotalConversaciones(conversacionRepo.countTotal());
        dto.setTotalPreguntas(mensajeRepo.countPreguntas());
        dto.setRatioCalidad(Math.round(valoracionRepo.getRatioCalidad() * 100.0) / 100.0);
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

    @Override
    public List<RankingUsuarioDTO> getRankingUsuarios(int limit, String criterio) {
    List<RankingUsuarioDTO> resultado = new ArrayList<>();
        for (Object[] fila : usuarioRepo.getRankingUsuarios(limit, criterio)) {
            resultado.add(new RankingUsuarioDTO(
                fila[0].toString(),
                fila[1].toString(),
                ((Number) fila[2]).longValue(),
                ((Number) fila[3]).longValue(),
                ((Number) fila[4]).longValue()
            ));
        }
        return resultado;
    }

    // ── CHATS ─────────────────────────────────────────────────────────
    public List<DistribucionDTO> getChatsPorSeccion() {
        List<DistribucionDTO> resultado = new ArrayList<>();
        for (Object[] fila : conversacionRepo.countPorSeccion()) {
            resultado.add(new DistribucionDTO(
                fila[0] != null ? fila[0].toString() : "Sin sección",
                (Long) fila[1]
            ));
        }
        return resultado;
    }

    @Override
    public List<ActividadDiariaDTO> getActividadDiaria(String fechaDesde, String fechaHasta) {
        LocalDateTime desde = LocalDateTime.parse(fechaDesde + "T00:00:00");
        LocalDateTime hasta = LocalDateTime.parse(fechaHasta + "T23:59:59");
        List<ActividadDiariaDTO> resultado = new ArrayList<>();
        for (Object[] fila : conversacionRepo.actividadPorFecha(desde, hasta)) {
            resultado.add(new ActividadDiariaDTO(fila[0].toString(), (Long) fila[1]));
        }
        return resultado;
    }

    @Override
    public List<HorasPuntaDTO> getHorasPunta() {
        List<HorasPuntaDTO> resultado = new ArrayList<>();
        for (Object[] fila : mensajeRepo.countPorHora()) {
            resultado.add(new HorasPuntaDTO(((Number) fila[0]).intValue(), (Long) fila[1]));
        }
        return resultado;
    }

    // ── ACTIVIDAD RECIENTE ──────────────────────────────────────────────

    @Override
    public List<ActividadRecienteDTO> getActividadReciente() {
        List<ActividadRecienteDTO> resultado = new ArrayList<>();
        for (Object[] fila : documentoRepo.getActividadReciente()) {
            resultado.add(new ActividadRecienteDTO(
                fila[0].toString(),
                fila[1].toString(),
                fila[2].toString(),
                fila[3].toString()
            ));
        }
        return resultado;
    }
}