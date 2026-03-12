package org.simarro.rag_daw.valoraciones.srv;

import java.util.List;

import org.simarro.rag_daw.valoraciones.model.dto.ValoracionCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionResumenDTO;

public interface ValoracionService {

    /**
     * Crea una nueva valoración o actualiza una existente
     * @param dto Datos de la valoración
     * @param emailUsuario Email del usuario autenticado
     * @return Valoración creada/actualizada
     */
    ValoracionDTO crearValoracion(ValoracionCreateDTO dto, String emailUsuario);

    /**
     * Obtiene todas las valoraciones de un mensaje
     * @param mensajeId ID del mensaje
     * @return Lista de valoraciones
     */
    List<ValoracionDTO> getValoracionesMensaje(Long mensajeId);

    /**
     * Obtiene resumen de valoraciones de una conversación
     * @param conversacionId ID de la conversación
     * @return Resumen de valoraciones
     */
    ValoracionResumenDTO getResumenConversacion(Long conversacionId);

    /**
     * Elimina una valoración (solo si pertenece al usuario)
     * @param id ID de la valoración
     * @param emailUsuario Email del usuario autenticado
     */
    void eliminarValoracion(Long id, String emailUsuario);
}