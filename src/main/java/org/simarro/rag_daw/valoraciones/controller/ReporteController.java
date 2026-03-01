package org.simarro.rag_daw.valoraciones.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ── Imports que necesitaréis cuando implementéis ──
// import org.simarro.rag_daw.valoraciones.srv.ReporteService;
// import org.simarro.rag_daw.valoraciones.model.dto.ReporteDTO;
// import org.simarro.rag_daw.valoraciones.model.dto.ReporteCreateDTO;
// import org.simarro.rag_daw.exception.FiltroException;
// import org.simarro.rag_daw.model.dto.PaginaResponse;
// import java.security.Principal;

/**
 * Controlador REST para reportes de respuestas — EQUIPO 4
 *
 * Endpoints:
 *   POST  /api/v1/reportes               → Reportar una respuesta
 *   GET   /api/v1/reportes               → Listar reportes (paginado, filtros)
 *   PATCH /api/v1/reportes/{id}/estado   → Cambiar estado (REVISADO/DESCARTADO)
 *
 * Los motivos de reporte son: INCORRECTA, INCOMPLETA, IRRELEVANTE, OFENSIVA, OTRA
 * Los estados del reporte son: PENDIENTE, REVISADO, DESCARTADO
 */
@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    // TODO ALUMNO: Descomentar cuando creéis ReporteService
    // private final ReporteService reporteService;
    //
    // public ReporteController(ReporteService reporteService) {
    //     this.reporteService = reporteService;
    // }

    /**
     * Reportar una respuesta del RAG como problemática.
     * Body: { "mensajeId": 5, "motivo": "INCORRECTA", "descripcion": "Info desactualizada" }
     */
    @PostMapping
    public ResponseEntity<?> crearReporte(@RequestBody Map<String, Object> body
            /* , Principal principal */) {
        // TODO ALUMNO: reporteService.crear(body, principal.getName())
        throw new UnsupportedOperationException("POST /api/v1/reportes — No implementado (Equipo 4)");
    }

    /**
     * Listar reportes (paginado con filtros por estado y motivo).
     * Solo accesible por roles ADMIN/PROFESOR.
     */
    @GetMapping
    public ResponseEntity<?> listarReportes(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String motivo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion,desc") String[] sort) {
        // TODO ALUMNO: reporteService.findAll(estado, motivo, page, size, sort)
        throw new UnsupportedOperationException("GET /api/v1/reportes — No implementado (Equipo 4)");
    }

    /**
     * Cambiar estado de un reporte (solo ADMIN/PROFESOR).
     * Body: { "estado": "REVISADO" }
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        // String nuevoEstado = body.get("estado");
        // TODO ALUMNO: reporteService.cambiarEstado(id, nuevoEstado)
        throw new UnsupportedOperationException(
            "PATCH /api/v1/reportes/{id}/estado — No implementado (Equipo 4)");
    }
}
