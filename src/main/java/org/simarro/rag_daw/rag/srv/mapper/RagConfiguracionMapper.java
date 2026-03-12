package org.simarro.rag_daw.rag.srv.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.simarro.rag_daw.model.dto.FiltroBusqueda;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.rag.model.db.RagConfiguracionDb;
import org.simarro.rag_daw.rag.model.dto.RagConfiguracionDTO;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface RagConfiguracionMapper {

        RagConfiguracionDTO ragConfiguracionDbToRagConfiguracionDTO(
                        RagConfiguracionDb ragConfiguracionDb);

        List<RagConfiguracionDTO> ragConfiguracionDbListToRagConfiguracionDTOList(
                        List<RagConfiguracionDb> ragConfiguracionDbList);

        /**
         * Convierte una Page de JPA en PaginaResponse.
         * Es 'default' (no static) para poder llamar a métodos de instancia del mapper.
         * Solo se incluye en mappers cuyo service devuelve listados paginados.
         */
        default PaginaResponse<RagConfiguracionDTO> pageToPaginaResponse(
                        Page<RagConfiguracionDb> page,
                        List<FiltroBusqueda> filtros,
                        List<String> ordenaciones) {
                return new PaginaResponse<>(
                                page.getNumber(),
                                page.getSize(),
                                page.getTotalElements(),
                                page.getTotalPages(),
                                ragConfiguracionDbListToRagConfiguracionDTOList(page.getContent()),
                                filtros,
                                ordenaciones);
        }
}