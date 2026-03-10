package org.simarro.rag_daw.valoraciones.controller;

import java.util.List;

import org.simarro.rag_daw.model.dto.Mensaje;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadEvolucionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadResumenDTO;
import org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO;
import org.simarro.rag_daw.valoraciones.srv.CalidadService;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/calidad")
@RequiredArgsConstructor
@Tag(name = "Calidad", description = "Endpoints para estadísticas y métricas de calidad del RAG")
public class CalidadController {

    private final CalidadService calidadService;

    @GetMapping("/resumen")
    @Operation(
        summary = "Resumen global de calidad",
        description = "Devuelve estadísticas globales de valoraciones y reportes del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen global obtenido correctamente",
                content = @Content(schema = @Schema(implementation = CalidadResumenDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = Mensaje.class)))
    })
    public ResponseEntity<?> getResumen() {
        try {
            CalidadResumenDTO resumen = calidadService.getResumen();
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al obtener el resumen de calidad: " + e.getMessage()));
        }
    }

    @GetMapping("/por-seccion")
    @Operation(
        summary = "Calidad por sección temática",
        description = "Devuelve métricas de calidad agrupadas por sección temática"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calidad por sección obtenida correctamente",
                content = @Content(schema = @Schema(implementation = CalidadPorSeccionDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = Mensaje.class)))
    })
    public ResponseEntity<?> getCalidadPorSeccion() {
        try {
            List<CalidadPorSeccionDTO> result = calidadService.getCalidadPorSeccion();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al obtener la calidad por sección: " + e.getMessage()));
        }
    }

    @GetMapping("/top-respuestas")
    @Operation(
        summary = "Top respuestas mejor o peor valoradas",
        description = "Devuelve las respuestas mejor o peor valoradas según el parámetro tipo=MEJOR|PEOR"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top respuestas obtenido correctamente",
                content = @Content(schema = @Schema(implementation = TopRespuestaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = Mensaje.class)))
    })
    public ResponseEntity<?> getTopRespuestas(
            @RequestParam(defaultValue = "MEJOR")
            @Parameter(description = "Tipo de ranking: MEJOR o PEOR", example = "MEJOR")
            String tipo,
            @RequestParam(defaultValue = "10")
            @Parameter(description = "Número máximo de resultados", example = "10")
            int limit) {
        try {
            List<TopRespuestaDTO> result = calidadService.getTopRespuestas(tipo, limit);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al obtener el top de respuestas: " + e.getMessage()));
        }
    }

    @GetMapping("/evolucion")
    @Operation(
        summary = "Evolución temporal de calidad",
        description = "Devuelve la evolución temporal de la calidad para gráficos, filtrando por rango de fechas y agrupación"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evolución obtenida correctamente",
                content = @Content(schema = @Schema(implementation = CalidadEvolucionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                content = @Content(schema = @Schema(implementation = Mensaje.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = Mensaje.class)))
    })
    public ResponseEntity<?> getEvolucion(
            @RequestParam
            @Parameter(description = "Fecha inicial", example = "2025-01-01")
            String fechaDesde,
            @RequestParam
            @Parameter(description = "Fecha final", example = "2025-12-31")
            String fechaHasta,
            @RequestParam(defaultValue = "MES")
            @Parameter(description = "Agrupación temporal: DIA, SEMANA o MES", example = "MES")
            String agrupacion) {
        try {
            List<CalidadEvolucionDTO> result =
                    calidadService.getEvolucion(fechaDesde, fechaHasta, agrupacion);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al obtener la evolución de calidad: " + e.getMessage()));
        }
    }
}