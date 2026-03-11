package org.simarro.rag_daw.valoraciones.repository;

import org.simarro.rag_daw.valoraciones.model.db.ReporteRespuestaDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReporteRepository
        extends JpaRepository<ReporteRespuestaDb, Long>,
        JpaSpecificationExecutor<ReporteRespuestaDb> {

    long countByEstado(ReporteRespuestaDb.EstadoReporte estado);
}