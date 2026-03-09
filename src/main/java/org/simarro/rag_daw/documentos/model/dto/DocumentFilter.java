package org.simarro.rag_daw.documentos.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Este documento segun lo que he entendido de la explicacion de Joserra, se va a utilizar como hemos utilizado en proyectos/actividades anteriores el FiltroBusqueda

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFilter {
    @Size(min = 1, message = "Debe especificar un atributo")
    private String atributo;

    @NotNull(message = "Debe especificar una operación")
    private String operacion;

    @NotNull(message = "El valor no puede estar vacío")
    private String valor;
}
