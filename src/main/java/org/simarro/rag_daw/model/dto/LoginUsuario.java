package org.simarro.rag_daw.model.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginUsuario {
    @Size(min=5,message="El nickname debe de tener un tamaño mínimo de 5 carácteres")
    private String nickname;
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "El password debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
    )
    private String password;
}

