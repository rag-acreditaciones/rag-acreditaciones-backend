package org.simarro.rag_daw.documentos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ── Imports que necesitaréis cuando implementéis ──
// import org.simarro.rag_daw.documentos.srv.SeccionTematicaService;
// import org.simarro.rag_daw.documentos.model.dto.SeccionTematicaDTO;
// import java.util.List;

/**
 * Controlador REST para secciones temáticas — EQUIPO 1
 *
 * Endpoint:
 *   GET /api/v1/secciones-tematicas → Listado de todas las secciones (para combos/filtros)
 *
 * Las secciones temáticas son: GENERAL, BD, PROGRAMACION, WEB
 * Se usan como filtro en documentos, chunks y conversaciones.
 */
@RestController
@RequestMapping("/api/v1/secciones-tematicas")
public class SeccionTematicaController {

    // TODO ALUMNO: Descomentar cuando creéis SeccionTematicaService
    // private final SeccionTematicaService seccionService;
    //
    // public SeccionTematicaController(SeccionTematicaService seccionService) {
    //     this.seccionService = seccionService;
    // }

    /**
     * Listado completo de secciones temáticas (sin paginación, son pocas).
     * Se usa para poblar combos y filtros en el frontend Angular.
     */
    @GetMapping
    public ResponseEntity<?> listarSecciones() {
        // TODO ALUMNO: return ResponseEntity.ok(seccionService.findAll());
        throw new UnsupportedOperationException("GET /api/v1/secciones-tematicas — No implementado (Equipo 1)");
    }
}
