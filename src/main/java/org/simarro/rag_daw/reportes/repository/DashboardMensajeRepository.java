package org.simarro.rag_daw.reportes.repository;

import java.util.List;

import org.simarro.rag_daw.model.db.MensajeDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardMensajeRepository extends JpaRepository<MensajeDb, Long> {

    @Query("SELECT COUNT(m) FROM MensajeDb m WHERE m.tipo = 'PREGUNTA'")
    Long countPreguntas();

    @Query("SELECT EXTRACT(HOUR FROM m.fecha), COUNT(m) FROM MensajeDb m " +
            "WHERE m.tipo = 'PREGUNTA' GROUP BY EXTRACT(HOUR FROM m.fecha) ORDER BY 1")
    List<Object[]> countPorHora();
}