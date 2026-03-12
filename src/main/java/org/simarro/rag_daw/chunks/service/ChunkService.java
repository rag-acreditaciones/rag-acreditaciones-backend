package org.simarro.rag_daw.chunks.service;

import java.util.List;
import java.util.Map;

import org.simarro.rag_daw.chunks.model.DTO.ChunkBusquedaDTO;
import org.simarro.rag_daw.chunks.model.DTO.ChunkDetailDTO;
import org.simarro.rag_daw.chunks.model.DTO.ChunkResponseDTO;
import org.simarro.rag_daw.chunks.model.DTO.ChunkStatsDTO;
import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.simarro.rag_daw.model.dto.PaginaResponse;

public interface ChunkService {
    
    ChunkDetailDTO findById(Long id);
    PaginaResponse<ChunkDetailDTO> findByDocumentoId (Long docId, String estado, int page, int size, String[] sort)  throws FiltroException; 
    PaginaResponse<ChunkBusquedaDTO> buscarPorTexto (String texto, Long seccionId, int page, int size) throws FiltroException;
    List<ChunkBusquedaDTO> busquedaSemantica (Map<String, Object> body) throws ResourceNotFoundException, FiltroException;
    ChunkStatsDTO getChunkStats(Long docId);
    ChunkResponseDTO cambiarEstado(Long id, String nuevoEstado) throws FiltroException;

}
