package org.simarro.rag_daw.valoraciones.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ── Imports que necesitaréis cuando implementéis ──
// import org.simarro.rag_daw.valoraciones.srv.CalidadService;
// import org.simarro.rag_daw.valoraciones.model.dto.CalidadResumenDTO;
// import org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO;
// import org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO;
// import org.simarro.rag_daw.valoraciones.model.dto.CalidadEvolucionDTO;
// import java.util.List;

/**
 * Controlador REST para métricas de calidad — EQUIPO 4
 *
 * Endpoints:
 *   GET /api/v1/calidad/resumen          → Resumen global de calidad
 *   GET /api/v1/calidad/por-seccion      → Calidad desglosada por sección
 *   GET /api/v1/calidad/top-respuestas   → Top respuestas mejor/peor valoradas
 *   GET /api/v1/calidad/evolucion        → Evolución temporal de calidad
 *
 * Estos endpoints agregan datos de valoraciones y reportes para ofrecer
 * una visión de la calidad de las respuestas del RAG.
 */
@RestController
@RequestMapping("/api/v1/calidad")
public class CalidadController {

    // TODO ALUMNO: Descomentar cuando creéis CalidadService
    // private final CalidadService calidadService;
    //
    // public CalidadController(CalidadService calidadService) {
    //     this.calidadService = calidadService;
    // }

    /**
     * Resumen global de calidad del RAG.
     * Response: { totalValoraciones, positivas, negativas, ratio,
     *             totalReportes, reportesPendientes }
     */
    @GetMapping("/resumen")
    public ResponseEntity<?> getResumen() {
        // TODO ALUMNO: calidadService.getResumenGlobal()
        throw new UnsupportedOperationException(
            "GET /api/v1/calidad/resumen — No implementado (Equipo 4)");
    }

    /**
     * Calidad desglosada por sección temática.
     * Response: [{ seccion, positivas, negativas, ratio, totalPreguntas }]
     */
    @GetMapping("/por-seccion")
    public ResponseEntity<?> getCalidadPorSeccion() {
        // TODO ALUMNO: calidadService.getCalidadPorSeccion()
        throw new UnsupportedOperationException(
            "GET /api/v1/calidad/por-seccion — No implementado (Equipo 4)");
    }

    /**
     * Top respuestas mejor o peor valoradas.
     * Query: tipo=MEJOR|PEOR, limit=10
     * Response: [{ mensajeId, textoResumido, valoracionesPositivas,
     *              valoracionesNegativas, conversacionId }]
     */
    @GetMapping("/top-respuestas")
    public ResponseEntity<?> getTopRespuestas(
            @RequestParam(defaultValue = "MEJOR") String tipo,
            @RequestParam(defaultValue = "10") int limit) {
        // TODO ALUMNO: calidadService.getTopRespuestas(tipo, limit)
        throw new UnsupportedOperationException(
            "GET /api/v1/calidad/top-respuestas — No implementado (Equipo 4)");
    }

    /**
     * Evolución temporal de calidad (para gráficos de línea en Angular).
     * Query: fechaDesde, fechaHasta, agrupacion=DIA|SEMANA|MES
     * Response: [{ periodo, positivas, negativas, ratio }]
     */
    @GetMapping("/evolucion")
    public ResponseEntity<?> getEvolucion(
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            @RequestParam(defaultValue = "MES") String agrupacion) {
        // TODO ALUMNO: calidadService.getEvolucion(fechaDesde, fechaHasta, agrupacion)
        throw new UnsupportedOperationException(
            "GET /api/v1/calidad/evolucion — No implementado (Equipo 4)");
    }
}
