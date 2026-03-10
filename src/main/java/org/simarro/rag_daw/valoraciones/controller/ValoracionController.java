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

@RestController
@RequestMapping("/api/v1/valoraciones")
@RequiredArgsConstructor
@Tag(name = "Valoraciones", description = "Endpoints para gestionar valoraciones de respuestas (👍/👎)")
public class ValoracionController {

    private final ValoracionService valoracionService;

    @PostMapping
    @Operation(
        summary = "Crear o actualizar una valoración",
        description = "Crea una nueva valoración o actualiza la existente si el usuario ya había valorado ese mensaje"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Valoración creada/actualizada correctamente",
                content = @Content(schema = @Schema(implementation = ValoracionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos incorrectos o valoración no válida",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> crearValoracion(
            @Valid
            @RequestBody
            @Parameter(description = "Datos de la valoración (mensajeId, valoracion: POSITIVA/NEGATIVA, comentario opcional)")
            ValoracionCreateDTO dto,
            Principal principal) {
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

    @GetMapping("/mensaje/{msgId}")
    @Operation(
        summary = "Obtener valoraciones de un mensaje",
        description = "Devuelve todas las valoraciones de un mensaje específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de valoraciones",
                content = @Content(schema = @Schema(implementation = ValoracionDTO.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    public ResponseEntity<List<ValoracionDTO>> getValoracionesMensaje(
            @PathVariable
            @Parameter(description = "ID del mensaje", example = "2")
            Long msgId) {
        return ResponseEntity.ok(valoracionService.getValoracionesMensaje(msgId));
    }

    @GetMapping("/conversacion/{convId}")
    @Operation(
        summary = "Obtener resumen de valoraciones de una conversación",
        description = "Devuelve un resumen con totales positivas, negativas y ratio de una conversación"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen de valoraciones",
                content = @Content(schema = @Schema(implementation = ValoracionResumenDTO.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Conversación no encontrada")
    })
    public ResponseEntity<ValoracionResumenDTO> getResumenConversacion(
            @PathVariable
            @Parameter(description = "ID de la conversación", example = "1")
            Long convId) {
        return ResponseEntity.ok(valoracionService.getResumenConversacion(convId));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar una valoración",
        description = "Elimina la valoración del usuario autenticado (solo puede eliminar las suyas)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Valoración eliminada correctamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "No puedes eliminar valoraciones de otros usuarios"),
        @ApiResponse(responseCode = "404", description = "Valoración no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> eliminarValoracion(
            @PathVariable
            @Parameter(description = "ID de la valoración", example = "1")
            Long id,
            Principal principal) {
        try {
            valoracionService.eliminarValoracion(id, principal.getName());
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Mensaje(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al eliminar la valoración: " + e.getMessage()));
        }
    }
}