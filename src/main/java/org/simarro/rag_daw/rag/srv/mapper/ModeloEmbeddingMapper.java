package org.simarro.rag_daw.rag.srv.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.simarro.rag_daw.rag.model.db.ModeloEmbeddingDb;
import org.simarro.rag_daw.rag.model.dto.ModeloEmbeddingDTO;

@Mapper(componentModel = "spring")
public interface ModeloEmbeddingMapper {

        ModeloEmbeddingDTO modeloEmbeddingDbToModeloEmbeddingDTO(
                        ModeloEmbeddingDb modeloEmbeddingDb);

        List<ModeloEmbeddingDTO> modeloEmbeddingDbListToModeloEmbeddingDTOList(
                        List<ModeloEmbeddingDb> modeloEmbeddingDbList);
}