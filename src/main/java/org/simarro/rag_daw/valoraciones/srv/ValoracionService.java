package org.simarro.rag_daw.valoraciones.srv;

import java.util.List;

import org.simarro.rag_daw.valoraciones.model.dto.ValoracionCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionResumenDTO;

public interface ValoracionService {
    ValoracionDTO crearOActualizar(ValoracionCreateDTO dto, String emailUsuario);
    List<ValoracionDTO> findByMensajeId(Long mensajeId);
    ValoracionResumenDTO getResumenConversacion(Long conversacionId);
    void eliminar(Long id, String emailUsuario);
    List<ValoracionDTO> findByConversacionId(Long conversacionId);
}