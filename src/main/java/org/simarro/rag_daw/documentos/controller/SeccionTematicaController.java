package org.simarro.rag_daw.documentos.controller;

import java.util.List;

import org.simarro.rag_daw.documentos.model.dto.SeccionTematicaDTO;
import org.simarro.rag_daw.documentos.service.SeccionTematicaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/secciones-tematicas")
public class SeccionTematicaController {

    private final SeccionTematicaService seccionService;

    public SeccionTematicaController(SeccionTematicaService seccionService) {
        this.seccionService = seccionService;
    }

    @GetMapping
    public ResponseEntity<List<SeccionTematicaDTO>> listarSecciones() {
        return ResponseEntity.ok(seccionService.findAll());
    }
}
