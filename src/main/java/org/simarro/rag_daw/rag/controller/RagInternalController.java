package org.simarro.rag_daw.rag.controller;

import java.io.IOException;
import java.util.Map;

import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.model.dto.PeticionListadoFiltrado;
import org.simarro.rag_daw.rag.model.dto.ChatResponseDTO;
import org.simarro.rag_daw.rag.model.dto.DocumentoMetadataDTO;
import org.simarro.rag_daw.rag.model.dto.IngestaResultDTO;
import org.simarro.rag_daw.rag.model.dto.RagConfiguracionDTO;
import org.simarro.rag_daw.rag.srv.ChatRagService;
import org.simarro.rag_daw.rag.srv.IngestaService;
import org.simarro.rag_daw.rag.srv.RagConfiguracionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/internal/rag")
public class RagInternalController {

    private final IngestaService ingestaService;
    private final ChatRagService chatRagService;
    private final RagConfiguracionService ragConfigService;

    public RagInternalController(IngestaService ingestaService,
                                  ChatRagService chatRagService,
                                  RagConfiguracionService ragConfigService) {
        this.ingestaService = ingestaService;
        this.chatRagService = chatRagService;
        this.ragConfigService = ragConfigService;
    }

    // ── Listado paginado ──

    @GetMapping("/configuraciones")
    public ResponseEntity<PaginaResponse<RagConfiguracionDTO>> getAllConfiguraciones(
            @RequestParam(required = false) String[] filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort)
            throws FiltroException {
        return ResponseEntity.ok(
            ragConfigService.findAll(filter, page, size, sort));
    }

    @PostMapping("/configuraciones")
    public ResponseEntity<PaginaResponse<RagConfiguracionDTO>> getAllConfiguracionesPost(
            @Valid @RequestBody PeticionListadoFiltrado peticionListadoFiltrado)
            throws FiltroException {
        return ResponseEntity.ok(
            ragConfigService.findAll(peticionListadoFiltrado));
    }

    @GetMapping("/configuraciones/defecto")
    public RagConfiguracionDTO obtenerConfigDefecto() {
        return ragConfigService.obtenerPorDefecto();
    }

    // ── Ingesta ──

    @PostMapping("/ingestar")
    public IngestaResultDTO ingestar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentoId") Long documentoId,
            @RequestParam("seccion") String seccion,
            @RequestParam("subidoPor") String subidoPor,
            @RequestParam(value = "ragConfigId", required = false) Long ragConfigId)
            throws IOException {
        DocumentoMetadataDTO metadata = new DocumentoMetadataDTO(
            documentoId, file.getOriginalFilename(), seccion, subidoPor);
        return ingestaService.procesarPdf(file.getBytes(), metadata, ragConfigId);
    }

    // ── Chat ──

    @PostMapping("/preguntar")
    public ChatResponseDTO preguntar(@RequestBody Map<String, Object> body) {
        String pregunta = (String) body.get("pregunta");
        String seccion = (String) body.getOrDefault("seccion", null);
        Long ragConfigId = body.containsKey("ragConfigId")
            ? ((Number) body.get("ragConfigId")).longValue() : null;
        return chatRagService.preguntar(pregunta, seccion, ragConfigId);
    }

    @GetMapping(value = "/preguntar/stream",
                produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> preguntarStream(
            @RequestParam String pregunta,
            @RequestParam(required = false) String seccion,
            @RequestParam(required = false) Long ragConfigId) {
        return chatRagService.preguntarStream(pregunta, seccion, ragConfigId);
    }

    // ── Health ──

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "configuracionesActivas", ragConfigService.listarActivas().size(),
            "configPorDefecto", ragConfigService.obtenerPorDefecto().nombre());
    }
}