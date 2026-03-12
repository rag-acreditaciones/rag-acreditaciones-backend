package org.simarro.rag_daw.reportes.repository;

import org.simarro.rag_daw.valoraciones.model.db.ValoracionDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardValoracionesRepository extends JpaRepository<ValoracionDb, Long> {
    @Query("SELECT " +
            "CASE WHEN COUNT(v) > 0 " +
            "THEN (SUM(CASE WHEN v.valoracion = 'POSITIVA' THEN 1.0 ELSE 0 END) * 100.0 / COUNT(v)) " +
            "ELSE 0 END " +
            "FROM ValoracionDb v")
    Double getRatioCalidad();
}