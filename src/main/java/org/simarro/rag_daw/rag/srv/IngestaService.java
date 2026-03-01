package org.simarro.rag_daw.rag.srv;

import org.simarro.rag_daw.rag.model.dto.DocumentoMetadataDTO;
import org.simarro.rag_daw.rag.model.dto.IngestaResultDTO;

public interface IngestaService {
    IngestaResultDTO procesarPdf(byte[] pdfBytes, DocumentoMetadataDTO metadata);
    IngestaResultDTO procesarPdf(byte[] pdfBytes, DocumentoMetadataDTO metadata,
                                  Long ragConfiguracionId);
}