package org.simarro.rag_daw.rag.srv;

import java.util.List;

import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.model.dto.PeticionListadoFiltrado;
import org.simarro.rag_daw.rag.model.dto.RagConfiguracionDTO;

public interface RagConfiguracionService {
    List<RagConfiguracionDTO> listarActivas();
    RagConfiguracionDTO obtenerPorId(Long id);
    RagConfiguracionDTO obtenerPorDefecto();
    RagConfiguracionDTO obtenerPorNombre(String nombre);
    PaginaResponse<RagConfiguracionDTO> findAll(
            String[] filter, int page, int size, String[] sort) throws FiltroException;
    PaginaResponse<RagConfiguracionDTO> findAll(
            PeticionListadoFiltrado peticionListadoFiltrado) throws FiltroException;
}
