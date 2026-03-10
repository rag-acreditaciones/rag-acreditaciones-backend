package org.simarro.rag_daw.valoraciones.srv.impl;

import java.util.ArrayList;
import java.util.List;

import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.simarro.rag_daw.helper.PaginationHelper;
import org.simarro.rag_daw.model.db.UsuarioDb;
import org.simarro.rag_daw.model.dto.FiltroBusqueda;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.model.enums.TipoOperacionBusqueda;
import org.simarro.rag_daw.repository.UsuarioRepository;
import org.simarro.rag_daw.srv.specification.FiltroBusquedaSpecification;
import org.simarro.rag_daw.valoraciones.model.db.ReporteRespuestaDb;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteEstadoDTO;
import org.simarro.rag_daw.valoraciones.repository.ReporteRepository;
import org.simarro.rag_daw.valoraciones.srv.ReporteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public ReporteDTO crearReporte(ReporteCreateDTO dto, String emailUsuario) {
        UsuarioDb usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        ReporteRespuestaDb.MotivoReporte motivo;
        try {
            motivo = ReporteRespuestaDb.MotivoReporte.valueOf(dto.getMotivo().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "El motivo debe ser: INCORRECTA, INCOMPLETA, IRRELEVANTE, OFENSIVA u OTRA");
        }

        ReporteRespuestaDb reporte = new ReporteRespuestaDb();
        reporte.setMensajeId(dto.getMensajeId());
        reporte.setUsuarioId(usuario.getId());
        reporte.setMotivo(motivo);
        reporte.setDescripcion(dto.getDescripcion());
        reporte.setEstado(ReporteRespuestaDb.EstadoReporte.PENDIENTE);

        return mapToDTO(reporteRepository.save(reporte));
    }

    @Override
    public PaginaResponse<ReporteDTO> listarReportes(String estado, String motivo, int page, int size, String[] sort)
            throws FiltroException {

        List<FiltroBusqueda> filtros = new ArrayList<>();

        if (estado != null && !estado.isBlank()) {
            filtros.add(new FiltroBusqueda("estado", TipoOperacionBusqueda.IGUAL, estado.toUpperCase()));
        }

        if (motivo != null && !motivo.isBlank()) {
            filtros.add(new FiltroBusqueda("motivo", TipoOperacionBusqueda.IGUAL, motivo.toUpperCase()));
        }

        Pageable pageable = PaginationHelper.createPageable(page, size, sort);

        Specification<ReporteRespuestaDb> spec = filtros.isEmpty()
                ? Specification.where(null)
                : new FiltroBusquedaSpecification<>(filtros);

        Page<ReporteRespuestaDb> pageResult = reporteRepository.findAll(spec, pageable);

        List<ReporteDTO> content = pageResult.getContent().stream()
                .map(this::mapToDTO)
                .toList();

        return new PaginaResponse<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                content,
                filtros,
                List.of(sort)
        );
    }

    @Override
    public ReporteDTO cambiarEstado(Long id, ReporteEstadoDTO dto) {
        ReporteRespuestaDb reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));

        ReporteRespuestaDb.EstadoReporte nuevoEstado;
        try {
            nuevoEstado = ReporteRespuestaDb.EstadoReporte.valueOf(dto.getEstado().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("El estado debe ser: PENDIENTE, REVISADO o DESCARTADO");
        }

        reporte.setEstado(nuevoEstado);

        return mapToDTO(reporteRepository.save(reporte));
    }

    private ReporteDTO mapToDTO(ReporteRespuestaDb entity) {
        ReporteDTO dto = new ReporteDTO();
        dto.setId(entity.getId());
        dto.setMensajeId(entity.getMensajeId());
        dto.setUsuarioId(entity.getUsuarioId());
        dto.setMotivo(entity.getMotivo().name());
        dto.setDescripcion(entity.getDescripcion());
        dto.setEstado(entity.getEstado().name());
        dto.setFechaCreacion(entity.getFechaCreacion());

        usuarioRepository.findById(entity.getUsuarioId())
                .ifPresent(usuario -> dto.setUsuarioEmail(usuario.getEmail()));

        return dto;
    }
}