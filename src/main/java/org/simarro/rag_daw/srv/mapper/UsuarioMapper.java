package org.simarro.rag_daw.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import org.simarro.rag_daw.model.db.UsuarioDb;
import org.simarro.rag_daw.model.dto.UsuarioInfo;

@Mapper(uses = RolMapper.class) // Para obtener List<RolInfo> en UsuarioInfo
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    @Mapping(target = "roles", source = "roles") // Asegura que los roles se conviertan
    UsuarioInfo usuarioDbToUsuarioInfo(UsuarioDb usuarioDb);
}
