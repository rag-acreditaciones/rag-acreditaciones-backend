package org.simarro.rag_daw.valoraciones.controller;

import java.security.Principal;
import java.util.List;

import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.simarro.rag_daw.model.dto.Mensaje;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionResumenDTO;
import org.simarro.rag_daw.valoraciones.srv.ValoracionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/valoraciones")
@RequiredArgsConstructor
@Tag(name = "Valoraciones", description = "Endpoints para gestionar valoraciones de respuestas")
public class ValoracionController {

    private final ValoracionService valoracionService;

    @Operation(
        summary = "Crear o actualizar una valoración",
        description = "Crea una nueva valoración o actualiza la existente si el usuario ya había valorado ese mensaje"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Valoración creada/actualizada correctamente",
                content = @Content(schema = @Schema(implementation = ValoracionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos incorrectos",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = Mensaje.class)))
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> crearValoracion(
            @Valid @RequestBody
            @Parameter(description = "Datos de la valoración")
            ValoracionCreateDTO dto,
            Principal principal) {

        log.debug("Creando valoración para mensaje ID: {}", dto.getMensajeId());

        try {
            ValoracionDTO result = valoracionService.crearValoracion(dto, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al crear la valoración: " + e.getMessage()));
        }
    }

    @GetMapping("/mensaje/{mensajeId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Obtener valoraciones de un mensaje",
        description = "Devuelve todas las valoraciones asociadas a un mensaje concreto"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de valoraciones obtenida correctamente",
                content = @Content(schema = @Schema(implementation = ValoracionDTO.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = Mensaje.class)))
    })
    public ResponseEntity<?> getValoracionesMensaje(
            @PathVariable
            @Parameter(description = "ID del mensaje", example = "2")
            Long mensajeId,
            Principal principal) {

        log.debug("Obteniendo valoraciones para mensaje ID: {}", mensajeId);

        try {
            List<ValoracionDTO> result = valoracionService.getValoracionesMensaje(mensajeId);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al obtener las valoraciones del mensaje: " + e.getMessage()));
        }
    }

    @GetMapping("/conversacion/{conversacionId}")
    @Operation(
        summary = "Obtener resumen de valoraciones de una conversación",
        description = "Devuelve el resumen de valoraciones asociadas a una conversación"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen obtenido correctamente",
                content = @Content(schema = @Schema(implementation = ValoracionResumenDTO.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = Mensaje.class)))
    })
    public ResponseEntity<?> getResumenConversacion(
            @PathVariable
            @Parameter(description = "ID de la conversación", example = "1")
            Long conversacionId,
            Principal principal) {

        log.debug("Obteniendo resumen para conversación ID: {}", conversacionId);

        try {
            ValoracionResumenDTO result = valoracionService.getResumenConversacion(conversacionId);
            return ResponseEntity.ok(result);

        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new Mensaje(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al obtener el resumen de la conversación: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar mi valoración",
        description = "Elimina una valoración del usuario autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Valoración eliminada correctamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Valoración no encontrada",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = Mensaje.class)))
    })
    public ResponseEntity<?> eliminarValoracion(
            @PathVariable
            @Parameter(description = "ID de la valoración", example = "1")
            Long id,
            Principal principal) {

        log.debug("Eliminando valoración ID: {}", id);

        try {
            valoracionService.eliminarValoracion(id, principal.getName());
            return ResponseEntity.noContent().build();

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al eliminar la valoración: " + e.getMessage()));
        }
    }
}