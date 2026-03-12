package org.simarro.rag_daw.chat.controller;

import java.util.Map;

import org.simarro.rag_daw.model.dto.ConversacionCreateDTO;
import org.simarro.rag_daw.model.dto.ConversacionDetailDTO;
import org.simarro.rag_daw.model.dto.ConversacionResponseDTO;
import org.simarro.rag_daw.model.dto.MensajeDTO;
import org.simarro.rag_daw.model.dto.PreguntaDTO;
import org.simarro.rag_daw.model.enums.EstadoConversacion;
import org.simarro.rag_daw.srv.ConversacionService;
import org.simarro.rag_daw.srv.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private ConversacionService conversacionService;
    @Autowired
    private MensajeService mensajeService;

    @PostMapping
    public ResponseEntity<ConversacionDetailDTO> crearConversacion(
            @RequestBody Map<String, String> body
            /* , Principal principal */) {

        String seccion = body.get("seccionTematica");

        ConversacionCreateDTO dto = new ConversacionCreateDTO();
        dto.setUsuarioId(1L); // TODO: reemplazar con principal.getName() → usuarioId
        dto.setSeccionTematica(seccion);

        ConversacionDetailDTO creada = conversacionService.crearConversacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * Listar conversaciones del usuario autenticado (paginado con filtros).
     * Solo devuelve las conversaciones del usuario logueado.
     */
    @GetMapping
    public ResponseEntity<Page<ConversacionResponseDTO>> listarConversaciones(
            @RequestParam(required = false) String seccion,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion,desc") String[] sort) {

        EstadoConversacion estadoEnum = null;
        if (estado != null) {
            try { estadoEnum = EstadoConversacion.valueOf(estado.toUpperCase()); }
            catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<ConversacionResponseDTO> resultado =
                conversacionService.listarConversaciones(1L, seccion, estadoEnum, pageable); // TODO: principal

        return ResponseEntity.ok(resultado);
    }

    /**
     * Detalle de una conversación incluyendo todos sus mensajes.
     * Verifica que la conversación pertenece al usuario autenticado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConversacionDetailDTO> getConversacion(@PathVariable Long id) {
        return conversacionService.obtenerConversacion(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar una conversación (borrado lógico o físico, decidid vosotros).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConversacion(@PathVariable Long id) {
        boolean eliminada = conversacionService.eliminarConversacion(id, 1L); // TODO: principal
        return eliminada
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Archivar una conversación (cambia estado a ARCHIVADA).
     * Una conversación archivada no admite nuevas preguntas.
     */
    @PatchMapping("/{id}/archivar")
    public ResponseEntity<ConversacionDetailDTO> archivarConversacion(@PathVariable Long id) {
        return conversacionService.archivarConversacion(id, 1L) // TODO: principal
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<MensajeDTO> enviarPregunta(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        PreguntaDTO preguntaDTO = new PreguntaDTO();
        preguntaDTO.setConversacionId(id);
        preguntaDTO.setUsuarioId(1L); // TODO: principal
        preguntaDTO.setPregunta(body.get("texto"));

        MensajeDTO respuesta = mensajeService.enviarPregunta(preguntaDTO);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Mensajes paginados de una conversación (ordenados cronológicamente).
     * Cada mensaje incluye: tipo (PREGUNTA/RESPUESTA), texto, fecha, chunks usados.
     */
    @GetMapping("/{id}/mensajes")
    public ResponseEntity<Page<MensajeDTO>> getMensajes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").ascending());
        Page<MensajeDTO> mensajes = mensajeService.findByConversacionId(id, pageable);
        return ResponseEntity.ok(mensajes);
    }

}
