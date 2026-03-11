package org.simarro.rag_daw.valoraciones.controller;

import java.security.Principal;

import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.simarro.rag_daw.model.dto.Mensaje;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteEstadoDTO;
import org.simarro.rag_daw.valoraciones.srv.ReporteService;
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
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Endpoints para reportar respuestas del RAG y gestionar su estado")
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping
    @Operation(
        summary = "Crear un reporte",
        description = "Permite al usuario reportar una respuesta del RAG como incorrecta, incompleta u otro motivo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reporte creado correctamente",
                content = @Content(schema = @Schema(implementation = ReporteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos incorrectos",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> crearReporte(
            @Valid
            @RequestBody
            @Parameter(description = "Datos del reporte: mensajeId, motivo, descripcion")
            ReporteCreateDTO dto,
            Principal principal) {
        try {
            ReporteDTO result = reporteService.crearReporte(dto, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al crear el reporte: " + e.getMessage()));
        }
    }

    @GetMapping
    @Operation(
        summary = "Listar reportes",
        description = "Devuelve reportes paginados, con filtros opcionales por estado y motivo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de reportes paginado",
                content = @Content(schema = @Schema(implementation = PaginaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Filtro inválido",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> listarReportes(
            @RequestParam(required = false)
            @Parameter(description = "Estado del reporte", example = "PENDIENTE")
            String estado,
            @RequestParam(required = false)
            @Parameter(description = "Motivo del reporte", example = "INCORRECTA")
            String motivo,
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Número de página", example = "0")
            int page,
            @RequestParam(defaultValue = "10")
            @Parameter(description = "Tamaño de página", example = "10")
            int size,
            @RequestParam(defaultValue = "fechaCreacion,desc")
            @Parameter(description = "Ordenación", example = "fechaCreacion,desc")
            String[] sort) {
        try {
            PaginaResponse<ReporteDTO> result = reporteService.listarReportes(estado, motivo, page, size, sort);
            return ResponseEntity.ok(result);
        } catch (FiltroException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al listar reportes: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    @Operation(
        summary = "Cambiar estado de un reporte",
        description = "Permite cambiar el estado de un reporte a REVISADO o DESCARTADO"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente",
                content = @Content(schema = @Schema(implementation = ReporteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> cambiarEstado(
            @PathVariable
            @Parameter(description = "ID del reporte", example = "1")
            Long id,
            @Valid
            @RequestBody
            @Parameter(description = "Nuevo estado del reporte")
            ReporteEstadoDTO dto) {
        try {
            ReporteDTO result = reporteService.cambiarEstado(id, dto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al cambiar el estado del reporte: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar un reporte",
        description = "Elimina un reporte del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reporte eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> eliminarReporte(
            @PathVariable
            @Parameter(description = "ID del reporte", example = "1")
            Long id) {

        try {

            reporteService.eliminarReporte(id);

            return ResponseEntity.noContent().build();

        } catch (ResourceNotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje(e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al eliminar el reporte: " + e.getMessage()));
        }
    }
}