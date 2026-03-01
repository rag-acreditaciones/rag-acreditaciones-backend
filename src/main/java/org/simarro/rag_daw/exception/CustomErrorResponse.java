package org.simarro.rag_daw.exception;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
public class CustomErrorResponse {
    @Schema(example = "ERRROR_CODE_STRING", description = "Código del error (String)")
    private String errorCode;
    @Schema(example = "Mensaje", description = "Mensaje informativo del error")
    private String message;
    @Schema(example = "Mensaje detallado del error", description = "Mensaje más detallado del error que puede estar nulo.")
    private String detailedMessage;
    @Schema(example = "2025-01-31T12:59:10.123456789", description = "Timestamp de cuando sucedio el error")
    private String timestamp;


    public CustomErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public CustomErrorResponse(String errorCode, String message,String detailedMessage) {
        this.errorCode = errorCode;
        this.message = message;
        this.detailedMessage=detailedMessage;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public void setDetailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }
}
