package org.simarro.rag_daw.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.model.dto.FiltroBusqueda;
import org.simarro.rag_daw.model.enums.TipoOperacionBusqueda;
@Component
public class FiltroBusquedaFactory {

    private final static CharSequence separador = ":";

    /**
     * Convierte un array de cadenas en una lista de FiltroBusqueda.
     * 
     * @param filtros Array de filtros en formato "atributo:Operador:valor"
     * @return Lista de FiltroBusqueda
          * @throws FiltroException 
          */
         public List<FiltroBusqueda> crearListaFiltrosBusqueda(String[] filtros) throws FiltroException {
        if (filtros == null || filtros.length == 0) {
            return Collections.emptyList();
        }
        try {
         
        return Arrays.stream(filtros)
        .map(FiltroBusquedaFactory::createFiltro)
        .collect(Collectors.toList());   
        } catch (IllegalArgumentException e) {
                 throw new FiltroException("BAD_FILTER",
                    "Error: Filtro incorrecto",e.getMessage());
        }
    }

    private static FiltroBusqueda createFiltro(String filtro) {
        if (filtro == null || !filtro.contains(separador)) {
            throw new IllegalArgumentException("El filtro proporcionado no tiene el formato esperado" +
                    " (ATRIBUTO" + separador.toString() + "OPERACION" + separador.toString() + "VALOR).");
        }

        String[] partes = filtro.split(separador.toString(), 3);
        if (partes.length < 3) {
            throw new IllegalArgumentException("El filtro proporcionado no tiene el formato esperado" +
                    " (ATRIBUTO" + separador.toString() + "OPERACION" + separador.toString() + "VALOR).");
        }

        String atributo = partes[0].trim();
        String operacionTexto = partes[1].trim();
        // Concatenar las partes restantes para el valor si hay más de 3 partes
        String valor = Arrays.stream(partes, 2, partes.length)
                .reduce((a, b) -> a + separador.toString() + b)
                .orElse("").trim();

        TipoOperacionBusqueda operacion;
        try {
            operacion = TipoOperacionBusqueda.valueOf(operacionTexto.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Operación no válida: " + operacionTexto);
        }

        return new FiltroBusqueda(atributo, operacion, valor);
    }
}