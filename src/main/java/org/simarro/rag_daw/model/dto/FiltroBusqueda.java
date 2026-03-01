package org.simarro.rag_daw.model.dto;

import org.simarro.rag_daw.model.enums.TipoOperacionBusqueda;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FiltroBusqueda {
    @Size(min=1,message="Debe especificar un atributo")
    private String atributo;
    @Size(min=1,message="Debe especificar una operación")
    private TipoOperacionBusqueda operacion;
    @NotNull(message="El valor no puede estar vacio")
    private Object valor;
}