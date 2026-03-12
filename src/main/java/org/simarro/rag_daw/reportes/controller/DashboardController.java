package org.simarro.rag_daw.reportes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ── Imports que necesitaréis cuando implementéis ──
import org.simarro.rag_daw.reportes.srv.DashboardService;
// import org.simarro.rag_daw.reportes.model.dto.DashboardResumenDTO;
// import org.simarro.rag_daw.reportes.model.dto.DistribucionDTO;
// import org.simarro.rag_daw.reportes.model.dto.EvolucionDTO;
// import org.simarro.rag_daw.reportes.model.dto.ActividadDiariaDTO;
// import org.simarro.rag_daw.reportes.model.dto.HorasPuntaDTO;
// import org.simarro.rag_daw.reportes.model.dto.RankingUsuarioDTO;
// import org.simarro.rag_daw.reportes.model.dto.ActividadRecienteDTO;
import java.util.List;

/**
 * Controlador REST para dashboard e informes — EQUIPO 5
 *
 * Endpoints:
 *   GET /api/v1/dashboard/resumen                    → KPIs globales (contadores)
 *   GET /api/v1/dashboard/documentos/por-seccion     → Distribución docs por sección
 *   GET /api/v1/dashboard/documentos/por-estado      → Distribución docs por estado
 *   GET /api/v1/dashboard/documentos/evolucion       → Subidas en el tiempo
 *   GET /api/v1/dashboard/chats/por-seccion          → Conversaciones por sección
 *   GET /api/v1/dashboard/chats/actividad-diaria     → Preguntas por día (line chart)
 *   GET /api/v1/dashboard/chats/horas-punta          → Distribución por hora del día
 *   GET /api/v1/dashboard/usuarios/ranking            → Top usuarios más activos
 *   GET /api/v1/dashboard/usuarios/por-rol           → Distribución usuarios por rol
 *   GET /api/v1/dashboard/actividad-reciente         → Últimas 20 acciones
 *
 * Todos estos endpoints son consultas de agregación (COUNT, GROUP BY, etc.)
 * sobre las tablas de documentos, conversaciones, mensajes, valoraciones y usuarios.
 * La mayoría devolverán datos para gráficos de Angular (Chart.js, ng2-charts, etc.)
 * 
 * CONSEJO: Usar @Query nativas en los Repository con GROUP BY para los agregados.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    
    public DashboardController(DashboardService dashboardService) {
         this.dashboardService = dashboardService;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  RESUMEN GLOBAL (KPIs)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * KPIs globales de la plataforma.
     * Response: { totalDocs, totalChunks, totalConversaciones,
     *             totalPreguntas, totalUsuarios, ratioCalidad }
     */
    @GetMapping("/resumen")
    public ResponseEntity<?> getResumen() {
        return ResponseEntity.ok(dashboardService.getResumenGlobal());
    }

    // ═══════════════════════════════════════════════════════════════════
    //  DOCUMENTOS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Distribución de documentos por sección temática (para pie chart).
     * Response: [{ seccion: "BD", count: 15 }, { seccion: "WEB", count: 8 }, ...]
     */
    @GetMapping("/documentos/por-seccion")
    public ResponseEntity<?> getDocsPorSeccion() {
        return ResponseEntity.ok(dashboardService.getDocumentosPorSeccion());
    }

    /**
     * Distribución de documentos por estado (para pie/bar chart).
     * Response: [{ estado: "PROCESADO", count: 20 }, { estado: "ERROR", count: 2 }, ...]
     */
    @GetMapping("/documentos/por-estado")
    public ResponseEntity<?> getDocsPorEstado() {
        return ResponseEntity.ok(dashboardService.getDocumentosPorEstado());
    }

    /**
     * Evolución de subidas de documentos en el tiempo (para line chart).
     * Query: fechaDesde, fechaHasta, agrupacion=DIA|SEMANA
     * Response: [{ periodo: "2025-01-15", count: 3 }, ...]
     */
    @GetMapping("/documentos/evolucion")
    public ResponseEntity<?> getDocsEvolucion(
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta,
            @RequestParam(defaultValue = "SEMANA") String agrupacion) {
        return ResponseEntity.ok(dashboardService.getDocumentosEvolucion(fechaDesde, fechaHasta, agrupacion));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CHATS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Conversaciones por sección temática (para bar chart).
     * Response: [{ seccion: "BD", count: 45 }, ...]
     */
    @GetMapping("/chats/por-seccion")
    public ResponseEntity<?> getChatsPorSeccion() {
        return ResponseEntity.ok(dashboardService.getChatsPorSeccion());
    }

    /**
     * Preguntas por día (para line chart de actividad).
     * Query: fechaDesde, fechaHasta
     * Response: [{ fecha: "2025-03-15", preguntas: 12 }, ...]
     */
    @GetMapping("/chats/actividad-diaria")
    public ResponseEntity<?> getActividadDiaria(
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta) {
        return ResponseEntity.ok(dashboardService.getActividadDiaria(fechaDesde, fechaHasta));
    }

    /**
     * Distribución de preguntas por hora del día (para identificar horas punta).
     * Response: [{ hora: 9, preguntas: 45 }, { hora: 10, preguntas: 62 }, ...]
     * Las horas van de 0 a 23.
     */
    @GetMapping("/chats/horas-punta")
    public ResponseEntity<?> getHorasPunta() {
        return ResponseEntity.ok(dashboardService.getHorasPunta());
    }

    // ═══════════════════════════════════════════════════════════════════
    //  USUARIOS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Ranking de usuarios más activos.
     * Query: limit=10, criterio=DOCS|CHATS|TOTAL
     * Response: [{ email, nombre, docsSubidos, conversaciones, total }]
     */
    @GetMapping("/usuarios/ranking")
    public ResponseEntity<?> getRankingUsuarios(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "TOTAL") String criterio) {
        return ResponseEntity.ok(dashboardService.getRankingUsuarios(limit, criterio));
    }

    /**
     * Distribución de usuarios por rol (para pie chart).
     * Response: [{ rol: "CANDIDATO", count: 50 }, { rol: "ASESOR", count: 10 }, ...]
     */
    @GetMapping("/usuarios/por-rol")
    public ResponseEntity<?> getUsuariosPorRol() {
        return ResponseEntity.ok(dashboardService.getUsuariosPorRol());
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ACTIVIDAD RECIENTE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Últimas 20 acciones realizadas en la plataforma (feed de actividad).
     * Response: [{ usuario, accion, recurso, fecha }]
     * 
     * Acciones posibles: SUBIDA_DOCUMENTO, PREGUNTA_RAG, VALORACION, REPORTE, etc.
     * 
     * CONSEJO: Usar UNION ALL con las tablas de documentos, mensajes, valoraciones
     * y reportes, ordenado por fecha descendente, LIMIT 20.
     */
    @GetMapping("/actividad-reciente")
    public ResponseEntity<?> getActividadReciente() {
        return ResponseEntity.ok(dashboardService.getActividadReciente());
    }
}
