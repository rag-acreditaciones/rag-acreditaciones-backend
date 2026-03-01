package org.simarro.rag_daw.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.simarro.rag_daw.exception.CustomErrorResponse;
import org.simarro.rag_daw.exception.FiltroException;
import org.springframework.security.core.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {// añadimos un mensaje por cada error
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
    }

    @ExceptionHandler(FiltroException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorResponse> handleFiltroException(FiltroException ex) {
        CustomErrorResponse response = new CustomErrorResponse(ex.getErrorCode(), ex.getMessage(),
                ex.getDetailedMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // UnsupportedOperationException
    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public ResponseEntity<CustomErrorResponse> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        CustomErrorResponse response = new CustomErrorResponse("UNSUPPORTED_OPERATION_EXCEPTION", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_IMPLEMENTED);
    }

    // Excepción de autentificación
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        // Esto capturará BadCredentialsException, UsernameNotFoundException y otras de
        // seguridad
        CustomErrorResponse response = new CustomErrorResponse(
                "BAD_CREDENTIALS",
                "El usuario no existe o la contraseña es incorrecta");

        // Forzamos el 401 (UNAUTHORIZED)
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGeneralException(Exception ex) {
        // Obtener la causa del error
        String cause = (ex.getCause() != null) ? ex.getCause().toString() : "No cause available";

        // Obtener información de la traza de la pila (si está disponible)
        String stackTraceElement = ex.getStackTrace().length > 0
                ? ex.getStackTrace()[0].toString()
                : "No stack trace available";

        // Crear el mensaje de error personalizado
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                ex.getClass().getSimpleName().toUpperCase(), // Código del error
                cause, // Mensaje más detallado del error o causa
                "Error en: " + stackTraceElement + " | Mensaje: " + ex.getMessage());

        // Registro para depuración (reemplazar con logger si es necesario)
        ex.printStackTrace();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}