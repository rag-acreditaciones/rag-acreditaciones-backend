package org.simarro.rag_daw.documentos.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// ── Imports que necesitaréis cuando implementéis ──
// import org.simarro.rag_daw.documentos.srv.DocumentoService;
// import org.simarro.rag_daw.documentos.model.dto.DocumentoDTO;
// import org.simarro.rag_daw.documentos.model.dto.DocumentoUploadDTO;
// import org.simarro.rag_daw.documentos.model.dto.DocumentoPreviewDTO;
// import org.simarro.rag_daw.exception.FiltroException;
// import org.simarro.rag_daw.model.dto.PaginaResponse;
// import java.security.Principal;

/**
 * Controlador REST para gestión de documentos — EQUIPO 1
 *
 * Endpoints:
 *   POST   /api/v1/documentos/upload        → Subida multipart (file + metadata JSON)
 *   GET    /api/v1/documentos               → Listado paginado + filtros + ordenación
 *   GET    /api/v1/documentos/{id}          → Detalle del documento (metadatos)
 *   GET    /api/v1/documentos/{id}/download → Descarga del fichero original (byte[])
 *   GET    /api/v1/documentos/{id}/preview  → Base64 del PDF para visualización inline
 *   DELETE /api/v1/documentos/{id}          → Borrado lógico (cambia estado a ELIMINADO)
 *
 * NOTA: Los ficheros se suben en formato multipart con dos partes:
 *   - "file": el PDF (application/pdf)
 *   - "metadata": JSON con { seccionTematicaId, descripcion } (application/json)
 */
@RestController
@RequestMapping("/api/v1/documentos")
public class DocumentoController {

    // TODO ALUMNO: Descomentar cuando creéis DocumentoService
    // private final DocumentoService documentoService;
    //
    // public DocumentoController(DocumentoService documentoService) {
    //     this.documentoService = documentoService;
    // }

    /**
     * Subida de documento PDF con metadatos.
     * El fichero se envía como multipart/form-data con dos partes:
     *   - file: el PDF
     *   - metadata: JSON string con { seccionTematicaId, descripcion }
     * 
     * El servicio internamente llama a IngestaService (caja negra del profesor)
     * para generar los chunks y embeddings en la tabla vectorial.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocumento(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") String metadataJson
            /* Principal principal */) {
        // TODO ALUMNO: 
        //   1. Parsear metadataJson a DocumentoUploadDTO (usar ObjectMapper)
        //   2. Llamar a documentoService.subirDocumento(file, uploadDTO, principal.getName())
        //   3. Devolver ResponseEntity.status(201).body(dto)
        throw new UnsupportedOperationException("POST /api/v1/documentos/upload — No implementado (Equipo 1)");
    }

    /**
     * Listado paginado de documentos con filtros y ordenación por query params.
     * 
     * Parámetros de filtrado admitidos:
     *   - nombre: filtro parcial por nombre del fichero (contiene)
     *   - seccionId: ID de la sección temática (igual)
     *   - subidoPor: email del usuario que subió el documento (igual)
     *   - fechaDesde / fechaHasta: rango de fechas de subida
     *   - estado: PENDIENTE | PROCESANDO | PROCESADO | ERROR | ELIMINADO
     * 
     * Seguir el mismo patrón que JugadorRestController.getAllJugadores()
     */
    @GetMapping
    public ResponseEntity<?> listarDocumentos(
            @RequestParam(required = false) String[] filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaSubida,desc") String[] sort) {
        // TODO ALUMNO: documentoService.findAll(filter, page, size, sort)
        throw new UnsupportedOperationException("GET /api/v1/documentos — No implementado (Equipo 1)");
    }

    /**
     * Detalle de un documento (solo metadatos, sin el contenido del PDF).
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumento(@PathVariable Long id) {
        // TODO ALUMNO: documentoService.findById(id)
        throw new UnsupportedOperationException("GET /api/v1/documentos/{id} — No implementado (Equipo 1)");
    }

    /**
     * Descarga del fichero PDF original.
     * Devuelve el byte[] con Content-Type application/pdf y Content-Disposition attachment.
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadDocumento(@PathVariable Long id) {
        // TODO ALUMNO:
        //   byte[] contenido = documentoService.descargar(id);
        //   return ResponseEntity.ok()
        //       .contentType(MediaType.APPLICATION_PDF)
        //       .header("Content-Disposition", "attachment; filename=\"documento.pdf\"")
        //       .body(contenido);
        throw new UnsupportedOperationException("GET /api/v1/documentos/{id}/download — No implementado (Equipo 1)");
    }

    /**
     * Preview del PDF en Base64 para visualización inline en Angular.
     * Retorna un JSON con { id, nombreFichero, base64, contentType }.
     * En Angular se usa con: <iframe [src]="'data:application/pdf;base64,' + base64">
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<?> previewDocumento(@PathVariable Long id) {
        // TODO ALUMNO: documentoService.preview(id)
        throw new UnsupportedOperationException("GET /api/v1/documentos/{id}/preview — No implementado (Equipo 1)");
    }

    /**
     * Borrado lógico: cambia el estado del documento a ELIMINADO.
     * No se borran ni los chunks ni los embeddings de la tabla vectorial.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDocumento(@PathVariable Long id) {
        // TODO ALUMNO: documentoService.eliminar(id); return ResponseEntity.noContent().build();
        throw new UnsupportedOperationException("DELETE /api/v1/documentos/{id} — No implementado (Equipo 1)");
    }
}
