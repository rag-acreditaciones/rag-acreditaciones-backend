package org.simarro.rag_daw.documentos.model.db;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Archivo que sirve para guardar los documentos, falta por poner a que tabla va referenciada pero como no tengo la base de datos ni el nombre no lo he puesto

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadDb {
    @NotNull(message = "El archivo es obligatorio")
    private MultipartFile archivo;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 120, message = "El título no puede superar 120 caracteres")
    private String titulo;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;
}
