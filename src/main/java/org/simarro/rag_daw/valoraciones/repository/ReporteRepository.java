package org.simarro.rag_daw.valoraciones.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.simarro.rag_daw.valoraciones.model.db.ReporteRespuestaDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReporteRepository
        extends JpaRepository<ReporteRespuestaDb, Long>, JpaSpecificationExecutor<ReporteRespuestaDb> {

    long countByEstado(ReporteRespuestaDb.EstadoReporte estado);

    @Query("SELECT COUNT(r) FROM ReporteRespuestaDb r WHERE r.estado = :estado")
    long countByEstado(@Param("estado") String estado);

    @Query("SELECT r.motivo, COUNT(r) FROM ReporteRespuestaDb r GROUP BY r.motivo")
    List<Object[]> countByMotivo();

    @Query("SELECT FUNCTION('DATE', r.fechaCreacion), COUNT(r) FROM ReporteRespuestaDb r " +
           "WHERE r.fechaCreacion BETWEEN :fechaDesde AND :fechaHasta " +
           "GROUP BY FUNCTION('DATE', r.fechaCreacion) ORDER BY FUNCTION('DATE', r.fechaCreacion)")
    List<Object[]> countByFechaBetween(@Param("fechaDesde") LocalDateTime fechaDesde, 
                                        @Param("fechaHasta") LocalDateTime fechaHasta);
}