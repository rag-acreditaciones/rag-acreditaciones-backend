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
import org.simarro.rag_daw.valoraciones.srv.mapper.ReporteMapper;
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
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteMapper reporteMapper;

    @Override
    @Transactional
    public ReporteDTO crearReporte(ReporteCreateDTO dto, String emailUsuario) {
        log.debug("Creando reporte para mensaje ID: {} por usuario: {}", dto.getMensajeId(), emailUsuario);

        UsuarioDb usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + emailUsuario));

        ReporteRespuestaDb.MotivoReporte motivo;
        try {
            motivo = ReporteRespuestaDb.MotivoReporte.valueOf(dto.getMotivo().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Motivo de reporte inválido: {}", dto.getMotivo());
            throw new IllegalArgumentException("Motivo inválido. Valores permitidos: INCORRECTA, INCOMPLETA, IRRELEVANTE, OFENSIVA, OTRA");
        }

        ReporteRespuestaDb reporte = new ReporteRespuestaDb();
        reporte.setMensajeId(dto.getMensajeId());
        reporte.setUsuarioId(usuario.getId());
        reporte.setMotivo(motivo);
        reporte.setDescripcion(dto.getDescripcion());
        reporte.setEstado(ReporteRespuestaDb.EstadoReporte.PENDIENTE);

        ReporteRespuestaDb reporteGuardado = reporteRepository.save(reporte);
        log.info("Reporte creado con ID: {} para mensaje: {}", reporteGuardado.getId(), dto.getMensajeId());

        ReporteDTO reporteDTO = reporteMapper.toDTO(reporteGuardado);
        reporteDTO.setUsuarioEmail(usuario.getEmail());
        
        return reporteDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<ReporteDTO> listarReportes(
            String estado,
            String motivo,
            int page,
            int size,
            String[] sort) throws FiltroException {
        
        log.debug("Listando reportes - estado: {}, motivo: {}, page: {}, size: {}", estado, motivo, page, size);

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
        
        log.debug("Encontrados {} reportes (total: {})", pageResult.getNumberOfElements(), pageResult.getTotalElements());

        List<ReporteDTO> content = pageResult.getContent()
                .stream()
                .map(reporte -> {
                    ReporteDTO dto = reporteMapper.toDTO(reporte);
                    usuarioRepository.findById(reporte.getUsuarioId())
                            .ifPresent(usuario -> dto.setUsuarioEmail(usuario.getEmail()));
                    return dto;
                })
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
    @Transactional
    public ReporteDTO cambiarEstado(Long id, ReporteEstadoDTO dto) {
        log.debug("Cambiando estado de reporte ID: {} a: {}", id, dto.getEstado());

        ReporteRespuestaDb reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id));

        ReporteRespuestaDb.EstadoReporte nuevoEstado;
        try {
            nuevoEstado = ReporteRespuestaDb.EstadoReporte.valueOf(dto.getEstado().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Estado de reporte inválido: {}", dto.getEstado());
            throw new IllegalArgumentException("Estado inválido. Valores permitidos: PENDIENTE, REVISADO, DESCARTADO");
        }

        reporte.setEstado(nuevoEstado);
        ReporteRespuestaDb reporteActualizado = reporteRepository.save(reporte);
        
        log.info("Reporte ID: {} actualizado a estado: {}", id, nuevoEstado);

        ReporteDTO reporteDTO = reporteMapper.toDTO(reporteActualizado);
        usuarioRepository.findById(reporteActualizado.getUsuarioId())
                .ifPresent(usuario -> reporteDTO.setUsuarioEmail(usuario.getEmail()));
        
        return reporteDTO;
    }

    @Override
    @Transactional
    public void eliminarReporte(Long id) {
        log.debug("Eliminando reporte ID: {}", id);

        ReporteRespuestaDb reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id));

        reporteRepository.delete(reporte);
        log.info("Reporte eliminado con ID: {}", id);
    }
}