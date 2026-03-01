package org.simarro.rag_daw.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PeticionListadoFiltrado {
    private List<FiltroBusqueda> listaFiltros;
    private Integer page;
    private Integer size;
    private List<String> sort;

    public PeticionListadoFiltrado(List<FiltroBusqueda> listaFiltros, Integer page, Integer size, String[] sort) {
        this.listaFiltros = listaFiltros;
        this.page = page;
        this.size = size;
        // Verificar si el tamaño de sort es 2 y el segundo elemento es "asc" o "desc"
        if (sort != null && sort.length == 2 && ("asc".equalsIgnoreCase(sort[1]) || "desc".equalsIgnoreCase(sort[1]))) {
            // Crear una lista con un solo elemento compuesto por "campo,orden"
            this.sort = List.of(sort[0] + "," + sort[1]);
        } else {
            // Asignar sort directamente como lista
            this.sort = List.of(sort);
        }
    }
}
