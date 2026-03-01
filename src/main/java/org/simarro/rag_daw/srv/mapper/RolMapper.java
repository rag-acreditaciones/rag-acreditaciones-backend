package org.simarro.rag_daw.srv.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.simarro.rag_daw.model.db.RolDb;
import org.simarro.rag_daw.model.dto.RolInfo;

@Mapper
public interface RolMapper {
    RolMapper INSTANCE = Mappers.getMapper(RolMapper.class);

    // Método para mapear un RolDb a un RolInfo
    RolInfo rolDbToRolInfo(RolDb rolDb); 
    // Método para mapear una lista de RolDb a RolInfo
    Set<RolInfo> rolesDbToRolInfo(Set<RolDb> rolesDb); 
}

