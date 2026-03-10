package org.simarro.rag_daw.valoraciones.srv;

import java.util.List;

import org.simarro.rag_daw.valoraciones.model.dto.ValoracionCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionResumenDTO;

public interface ValoracionService {

    ValoracionDTO crearValoracion(ValoracionCreateDTO dto, String emailUsuario);

    List<ValoracionDTO> getValoracionesMensaje(Long mensajeId);

    ValoracionResumenDTO getResumenConversacion(Long conversacionId);

    void eliminarValoracion(Long id, String emailUsuario);
}