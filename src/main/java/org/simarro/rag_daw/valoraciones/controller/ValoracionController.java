package org.simarro.rag_daw.valoraciones.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ── Imports que necesitaréis cuando implementéis ──
// import org.simarro.rag_daw.valoraciones.srv.ValoracionService;
// import org.simarro.rag_daw.valoraciones.model.dto.ValoracionDTO;
// import org.simarro.rag_daw.valoraciones.model.dto.ValoracionCreateDTO;
// import org.simarro.rag_daw.valoraciones.model.dto.ValoracionResumenDTO;
// import java.security.Principal;

/**
 * Controlador REST para valoraciones de respuestas — EQUIPO 4
 *
 * Endpoints:
 *   POST   /api/v1/valoraciones                        → Crear/actualizar valoración (👍/👎)
 *   GET    /api/v1/valoraciones/mensaje/{msgId}         → Valoraciones de un mensaje
 *   GET    /api/v1/valoraciones/conversacion/{convId}   → Resumen valoraciones de conversación
 *   DELETE /api/v1/valoraciones/{id}                    → Eliminar mi valoración
 *
 * Las valoraciones pueden ser:
 *   - POSITIVA (👍 mano arriba): el usuario considera útil la respuesta
 *   - NEGATIVA (👎 mano abajo): el usuario no está satisfecho
 * Opcionalmente incluyen un comentario textual.
 * Un usuario solo puede tener UNA valoración por mensaje (upsert).
 */
@RestController
@RequestMapping("/api/v1/valoraciones")
public class ValoracionController {

    // TODO ALUMNO: Descomentar cuando creéis ValoracionService
    // private final ValoracionService valoracionService;
    //
    // public ValoracionController(ValoracionService valoracionService) {
    //     this.valoracionService = valoracionService;
    // }

    /**
     * Crear o actualizar valoración.
     * Si el usuario ya valoró ese mensaje, se actualiza (upsert).
     * Body: { "mensajeId": 5, "valoracion": "POSITIVA", "comentario": "Muy útil" }
     */
    @PostMapping
    public ResponseEntity<?> crearValoracion(@RequestBody Map<String, Object> body
            /* , Principal principal */) {
        // TODO ALUMNO: valoracionService.crearOActualizar(body, principal.getName())
        throw new UnsupportedOperationException(
            "POST /api/v1/valoraciones — No implementado (Equipo 4)");
    }

    /**
     * Valoraciones de un mensaje concreto.
     * Devuelve la lista de valoraciones con usuario, tipo y comentario.
     */
    @GetMapping("/mensaje/{msgId}")
    public ResponseEntity<?> getValoracionesMensaje(@PathVariable Long msgId) {
        // TODO ALUMNO: valoracionService.findByMensajeId(msgId)
        throw new UnsupportedOperationException(
            "GET /api/v1/valoraciones/mensaje/{msgId} — No implementado (Equipo 4)");
    }

    /**
     * Resumen de valoraciones de una conversación completa.
     * Response: { totalPositivas, totalNegativas, ratio, detalles[] }
     */
    @GetMapping("/conversacion/{convId}")
    public ResponseEntity<?> getResumenConversacion(@PathVariable Long convId) {
        // TODO ALUMNO: valoracionService.getResumenConversacion(convId)
        throw new UnsupportedOperationException(
            "GET /api/v1/valoraciones/conversacion/{convId} — No implementado (Equipo 4)");
    }

    /**
     * Eliminar mi propia valoración.
     * Solo el autor de la valoración puede borrarla.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarValoracion(@PathVariable Long id
            /* , Principal principal */) {
        // TODO ALUMNO: valoracionService.eliminar(id, principal.getName())
        throw new UnsupportedOperationException(
            "DELETE /api/v1/valoraciones/{id} — No implementado (Equipo 4)");
    }
}
