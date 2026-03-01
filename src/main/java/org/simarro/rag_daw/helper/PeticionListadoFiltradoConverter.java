package org.simarro.rag_daw.helper;


import java.util.List;

import org.springframework.stereotype.Component;

import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.model.dto.FiltroBusqueda;
import org.simarro.rag_daw.model.dto.PeticionListadoFiltrado;

@Component
public class PeticionListadoFiltradoConverter {
    private final FiltroBusquedaFactory filtroBusquedaFactory;

    public PeticionListadoFiltradoConverter(FiltroBusquedaFactory filtroBusquedaFactory) {
        this.filtroBusquedaFactory = filtroBusquedaFactory;
    }

    
    public PeticionListadoFiltrado convertFromParams(
            String[] filter, 
            int page, 
            int size, 
            String[] sort) throws FiltroException {
        List<FiltroBusqueda> filtros = filtroBusquedaFactory.crearListaFiltrosBusqueda(filter);
        return new PeticionListadoFiltrado(filtros, page, size, sort);
    }
}
