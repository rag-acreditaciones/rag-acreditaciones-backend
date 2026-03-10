package org.simarro.rag_daw.documentos.controller;

import org.simarro.rag_daw.documentos.model.dto.DocumentoDetailDTO;
import org.simarro.rag_daw.documentos.model.dto.DocumentoResponseDTO;
import org.simarro.rag_daw.documentos.model.dto.DocumentoUploadDTO;
import org.simarro.rag_daw.documentos.service.DocumentoService;
import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;
    private final ObjectMapper objectMapper;

    public DocumentoController(DocumentoService documentoService, ObjectMapper objectMapper) {
        this.documentoService = documentoService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoResponseDTO> uploadDocumento(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") String metadataJson) throws JsonProcessingException {
        DocumentoUploadDTO uploadDTO = objectMapper.readValue(metadataJson, DocumentoUploadDTO.class);
        DocumentoResponseDTO response = documentoService.subirDocumento(file, uploadDTO, "usuario@sistema.es");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PaginaResponse<DocumentoResponseDTO>> listarDocumentos(
            @RequestParam(required = false) String[] filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaSubida,desc") String[] sort) throws FiltroException {
        return ResponseEntity.ok(documentoService.findAll(filter, page, size, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> getDocumento(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.findById(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocumento(@PathVariable Long id) {
        DocumentoResponseDTO doc = documentoService.findById(id);
        byte[] contenido = documentoService.descargar(id);
        String safeFilename = doc.nombreFichero().replaceAll("[^a-zA-Z0-9._\\-()]", "_");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=\"" + safeFilename + "\"")
                .body(contenido);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<DocumentoDetailDTO> previewDocumento(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.preview(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocumento(@PathVariable Long id) {
        documentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
