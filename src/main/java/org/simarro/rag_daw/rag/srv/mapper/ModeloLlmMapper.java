package org.simarro.rag_daw.rag.srv.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.simarro.rag_daw.rag.model.db.ModeloLlmDb;
import org.simarro.rag_daw.rag.model.dto.ModeloLlmDTO;

@Mapper(componentModel = "spring")
public interface ModeloLlmMapper {

    ModeloLlmDTO modeloLlmDbToModeloLlmDTO(ModeloLlmDb modeloLlmDb);

    List<ModeloLlmDTO> modeloLlmDbListToModeloLlmDTOList(
            List<ModeloLlmDb> modeloLlmDbList);
}