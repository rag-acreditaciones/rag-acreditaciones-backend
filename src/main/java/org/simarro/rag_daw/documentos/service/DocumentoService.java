package org.simarro.rag_daw.documentos.service;

import org.simarro.rag_daw.documentos.model.dto.DocumentoDetailDTO;
import org.simarro.rag_daw.documentos.model.dto.DocumentoResponseDTO;
import org.simarro.rag_daw.documentos.model.dto.DocumentoUploadDTO;
import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentoService {

    DocumentoResponseDTO subirDocumento(MultipartFile file, DocumentoUploadDTO uploadDTO, String subidoPor);

    PaginaResponse<DocumentoResponseDTO> findAll(String[] filter, int page, int size, String[] sort)
            throws FiltroException;

    DocumentoResponseDTO findById(Long id);

    byte[] descargar(Long id);

    DocumentoDetailDTO preview(Long id);

    void eliminar(Long id);
}
