package org.simarro.rag_daw.chat.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ── Imports que necesitaréis cuando implementéis ──
// import org.simarro.rag_daw.chat.srv.ConversacionService;
// import org.simarro.rag_daw.chat.srv.MensajeService;
// import org.simarro.rag_daw.chat.model.dto.ConversacionDTO;
// import org.simarro.rag_daw.chat.model.dto.ConversacionCreateDTO;
// import org.simarro.rag_daw.chat.model.dto.MensajeDTO;
// import org.simarro.rag_daw.chat.model.dto.PreguntaRequestDTO;
// import org.simarro.rag_daw.chat.model.dto.PreguntaResponseDTO;
// import org.simarro.rag_daw.exception.FiltroException;
// import org.simarro.rag_daw.model.dto.PaginaResponse;
// import java.security.Principal;

/**
 * Controlador REST para gestión de conversaciones y chat — EQUIPO 3
 *
 * Endpoints:
 *   POST   /api/v1/conversaciones                     → Crear conversación
 *   GET    /api/v1/conversaciones                     → Listar conversaciones del usuario
 *   GET    /api/v1/conversaciones/{id}                → Detalle con todos los mensajes
 *   DELETE /api/v1/conversaciones/{id}                → Eliminar conversación
 *   PATCH  /api/v1/conversaciones/{id}/archivar       → Archivar conversación
 *   POST   /api/v1/conversaciones/{id}/preguntas      → Enviar pregunta al RAG
 *   GET    /api/v1/conversaciones/{id}/mensajes       → Mensajes paginados
 *
 * Flujo de chat:
 *   1. El usuario crea una conversación indicando la sección temática
 *   2. Envía preguntas con POST /{id}/preguntas
 *   3. El MensajeService guarda pregunta y respuesta, invocando ChatRagService (caja negra)
 *   4. La respuesta incluye los chunks del VectorStore que se usaron como contexto
 */
@RestController
@RequestMapping("/api/v1/conversaciones")
public class ConversacionController {

    // TODO ALUMNO: Descomentar cuando creéis ConversacionService y MensajeService
    // private final ConversacionService conversacionService;
    // private final MensajeService mensajeService;
    //
    // public ConversacionController(ConversacionService conversacionService,
    //                                MensajeService mensajeService) {
    //     this.conversacionService = conversacionService;
    //     this.mensajeService = mensajeService;
    // }

    /**
     * Crear una nueva conversación.
     * Body: { "seccionTematica": "BD" }
     * Las secciones válidas son: GENERAL, BD, PROGRAMACION, WEB
     * La conversación se crea con estado ACTIVA y asociada al usuario autenticado.
     */
    @PostMapping
    public ResponseEntity<?> crearConversacion(@RequestBody Map<String, String> body
            /* , Principal principal */) {
        // String seccion = body.get("seccionTematica");
        // TODO ALUMNO: conversacionService.crear(seccion, principal.getName())
        throw new UnsupportedOperationException(
            "POST /api/v1/conversaciones — No implementado (Equipo 3)");
    }

    /**
     * Listar conversaciones del usuario autenticado (paginado con filtros).
     * Solo devuelve las conversaciones del usuario logueado.
     */
    @GetMapping
    public ResponseEntity<?> listarConversaciones(
            @RequestParam(required = false) String seccion,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion,desc") String[] sort
            /* , Principal principal */) {
        // TODO ALUMNO: conversacionService.findByUsuario(principal.getName(), seccion, estado, page, size, sort)
        throw new UnsupportedOperationException(
            "GET /api/v1/conversaciones — No implementado (Equipo 3)");
    }

    /**
     * Detalle de una conversación incluyendo todos sus mensajes.
     * Verifica que la conversación pertenece al usuario autenticado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getConversacion(@PathVariable Long id
            /* , Principal principal */) {
        // TODO ALUMNO: conversacionService.findByIdConMensajes(id, principal.getName())
        throw new UnsupportedOperationException(
            "GET /api/v1/conversaciones/{id} — No implementado (Equipo 3)");
    }

    /**
     * Eliminar una conversación (borrado lógico o físico, decidid vosotros).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarConversacion(@PathVariable Long id
            /* , Principal principal */) {
        // TODO ALUMNO: conversacionService.eliminar(id, principal.getName())
        throw new UnsupportedOperationException(
            "DELETE /api/v1/conversaciones/{id} — No implementado (Equipo 3)");
    }

    /**
     * Archivar una conversación (cambia estado a ARCHIVADA).
     * Una conversación archivada no admite nuevas preguntas.
     */
    @PatchMapping("/{id}/archivar")
    public ResponseEntity<?> archivarConversacion(@PathVariable Long id
            /* , Principal principal */) {
        // TODO ALUMNO: conversacionService.archivar(id, principal.getName())
        throw new UnsupportedOperationException(
            "PATCH /api/v1/conversaciones/{id}/archivar — No implementado (Equipo 3)");
    }

    /**
     * Enviar una pregunta al RAG dentro de una conversación existente.
     * Body: { "texto": "¿Qué es...?" }
     * 
     * Internamente el MensajeService:
     *   1. Guarda la pregunta como Mensaje(PREGUNTA)
     *   2. Llama a ChatRagService.preguntar() (CAJA NEGRA del profesor)
     *   3. Guarda la respuesta como Mensaje(RESPUESTA) con chunks usados
     *   4. Devuelve { pregunta, respuesta, chunksUsados[], tiempoMs, modelo }
     */
    @PostMapping("/{id}/preguntas")
    public ResponseEntity<?> enviarPregunta(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
            /* , Principal principal */) {
        // String texto = body.get("texto");
        // TODO ALUMNO:
        //   PreguntaRequestDTO request = new PreguntaRequestDTO(texto);
        //   PreguntaResponseDTO response = mensajeService.preguntar(id, request);
        //   return ResponseEntity.ok(response);
        throw new UnsupportedOperationException(
            "POST /api/v1/conversaciones/{id}/preguntas — No implementado (Equipo 3)");
    }

    /**
     * Mensajes paginados de una conversación (ordenados cronológicamente).
     * Cada mensaje incluye: tipo (PREGUNTA/RESPUESTA), texto, fecha, chunks usados.
     */
    @GetMapping("/{id}/mensajes")
    public ResponseEntity<?> getMensajes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // TODO ALUMNO: mensajeService.findByConversacionId(id, page, size)
        throw new UnsupportedOperationException(
            "GET /api/v1/conversaciones/{id}/mensajes — No implementado (Equipo 3)");
    }
}
