package org.simarro.rag_daw.documentos.repository;

import org.simarro.rag_daw.documentos.model.db.DocumentoDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentoRepository
        extends JpaRepository<DocumentoDb, Long>, JpaSpecificationExecutor<DocumentoDb> {
}
