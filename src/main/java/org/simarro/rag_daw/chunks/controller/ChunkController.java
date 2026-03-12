package org.simarro.rag_daw.chunks.controller;

import java.util.Map;
import java.util.Set;

import org.simarro.rag_daw.chunks.model.DTO.ChunkDetailDTO;
import org.simarro.rag_daw.chunks.service.ChunkService;
import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

// ── Imports que necesitaréis cuando implementéis ──
// import org.simarro.rag_daw.chunks.srv.ChunkService;
// import org.simarro.rag_daw.chunks.model.dto.ChunkDTO;
// import org.simarro.rag_daw.chunks.model.dto.ChunkStatsDTO;
// import org.simarro.rag_daw.chunks.model.dto.BusquedaSemanticaRequestDTO;
// import org.simarro.rag_daw.chunks.model.dto.BusquedaSemanticaResponseDTO;
// import org.simarro.rag_daw.exception.FiltroException;
// import org.simarro.rag_daw.model.dto.PaginaResponse;

/**
 * Controlador REST para gestión de chunks — EQUIPO 2
 *
 * Endpoints:
 * GET /api/v1/chunks/documento/{docId} → Chunks de un documento (paginado)
 * GET /api/v1/chunks/{id} → Detalle de un chunk
 * GET /api/v1/chunks/buscar → Búsqueda textual en chunks
 * POST /api/v1/chunks/busqueda-semantica → Búsqueda por similitud vectorial
 * GET /api/v1/chunks/documento/{docId}/stats → Estadísticas de chunks del
 * documento
 * PATCH /api/v1/chunks/{id}/estado → Cambiar estado (REVISADO/DESCARTADO)
 *
 * Los chunks son los fragmentos de texto generados al procesar un PDF.
 * Cada chunk tiene un embedding vectorial almacenado en la tabla PgVector.
 * La tabla documento_chunks relaciona chunks con documentos.
 *
 * Para la búsqueda semántica, se invoca el VectorStore (caja negra del
 * profesor)
 * que busca los chunks más similares usando distancia coseno.
 */
@RestController
@RequestMapping("/api/v1/chunks")
@Validated
public class ChunkController {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("PENDIENTE", "REVISADO", "DESCARTADO");

    private final ChunkService chunkService;

    public ChunkController(ChunkService chunkService) {
        this.chunkService = chunkService;
    }

    /**
     * Chunks de un documento concreto (paginado con filtro opcional por estado).
     * Cada chunk incluye: id, texto, ordenChunk, estado, metadata.
     */
    @GetMapping("/documento/{docId}")
    public ResponseEntity<?> getChunksByDocumento(
            @PathVariable @Positive Long docId,
            @RequestParam(required = false) @Pattern(regexp = "(?i)PENDIENTE|REVISADO|DESCARTADO", message = "estado inválido") String estado,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "ordenChunk,asc") String[] sort) throws FiltroException {

        return ResponseEntity.ok(chunkService.findByDocumentoId(docId, estado, page, size, sort));

    }

    /**
     * Detalle de un chunk: texto completo, metadata, embedding info.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getChunk(@PathVariable @Positive Long id) {
        ChunkDetailDTO response = chunkService.findById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Búsqueda textual (LIKE/ILIKE) en el contenido de los chunks.
     * Útil para encontrar chunks que contengan un término exacto.
     * Diferente de la búsqueda semántica (que busca por significado).
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarTexto(
            @RequestParam @NotBlank String texto,
            @RequestParam(required = false) @Positive Long seccionId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) throws FiltroException {

        return ResponseEntity.ok(chunkService.buscarPorTexto(texto, seccionId, page, size));
    }

    /**
     * Búsqueda semántica: busca chunks similares por significado usando embeddings.
     * Internamente usa VectorStore.similaritySearch() (caja negra del profesor).
     *
     * Body: { "consulta": "texto libre en lenguaje natural", "topK": 5 }
     * Response: lista de chunks ordenados por similitud descendente,
     * cada uno con su score de similitud (0.0 a 1.0).
     */
    @PostMapping("/busqueda-semantica")
    public ResponseEntity<?> busquedaSemantica(@RequestBody Map<String, Object> body)
            throws ResourceNotFoundException, FiltroException {

        if (body == null || body.get("consulta") == null || body.get("consulta").toString().isBlank()) {
            throw new FiltroException("CONSULTA_REQUERIDA", "La consulta es obligatoria", null);
        }

        if (body.containsKey("topK") && body.get("topK") != null) {
            Object topKObj = body.get("topK");
            if (!(topKObj instanceof Number)) {
                throw new FiltroException("TOPK_TIPO_INVALIDO", "topK debe ser numérico", null);
            }
            int topK = ((Number) topKObj).intValue();
            if (topK < 1 || topK > 10) {
                throw new FiltroException("TOPK_RANGO_INVALIDO",
                        "topK debe estar entre 1 y 10",
                        "Valor recibido: " + topK);
            }
        }

        return ResponseEntity.ok(chunkService.busquedaSemantica(body));
    }

    /**
     * Estadísticas de chunks de un documento:
     * - totalChunks, chunksPorEstado, longitudMedia, longitudMax, longitudMin
     */
    @GetMapping("/documento/{docId}/stats")
    public ResponseEntity<?> getChunkStats(@PathVariable @Positive Long docId) {
        return ResponseEntity.ok(chunkService.getChunkStats(docId));
    }

    /**
     * Cambiar estado de un chunk: ACTIVO → REVISADO | DESCARTADO
     * Body: { "estado": "REVISADO" }
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable @Positive Long id,
            @RequestBody Map<String, String> body) throws FiltroException {
        if (body == null || body.get("estado") == null || body.get("estado").isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El estado es obligatorio"));
        }

        String nuevoEstado = body.get("estado").trim().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(nuevoEstado)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "estado debe ser PENDIENTE, REVISADO o DESCARTADO"));
        }

        return ResponseEntity.ok(chunkService.cambiarEstado(id, nuevoEstado));
    }
}
