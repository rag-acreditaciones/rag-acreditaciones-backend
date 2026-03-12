package org.simarro.rag_daw.valoraciones.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.simarro.rag_daw.valoraciones.model.db.ValoracionDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CalidadRepository extends JpaRepository<ValoracionDb, Long> {
    
    @Query(value = "SELECT * FROM valoraciones v WHERE v.fecha_creacion BETWEEN :fechaDesde AND :fechaHasta", 
           nativeQuery = true)
    @NonNull
    List<ValoracionDb> findValoracionesEnRango(
        @Param("fechaDesde") LocalDateTime fechaDesde, 
        @Param("fechaHasta") LocalDateTime fechaHasta
    );
    
    @Query("SELECT COUNT(v) FROM ValoracionDb v")
    long countTotalValoraciones();
    
    @Query("SELECT COUNT(v) FROM ValoracionDb v WHERE v.valoracion = 'POSITIVA'")
    long countPositivas();
    
    @Query("SELECT COUNT(v) FROM ValoracionDb v WHERE v.valoracion = 'NEGATIVA'")
    long countNegativas();
    
    @Query("SELECT COUNT(r) FROM ReporteRespuestaDb r")
    long countTotalReportes();
    
    @Query("SELECT COUNT(r) FROM ReporteRespuestaDb r WHERE r.estado = 'PENDIENTE'")
    long countReportesPendientes();

    @Query("SELECT FUNCTION('DATE', v.fechaCreacion), " +
           "COUNT(CASE WHEN v.valoracion = 'POSITIVA' THEN 1 END), " +
           "COUNT(CASE WHEN v.valoracion = 'NEGATIVA' THEN 1 END) " +
           "FROM ValoracionDb v " +
           "WHERE v.fechaCreacion BETWEEN :fechaDesde AND :fechaHasta " +
           "GROUP BY FUNCTION('DATE', v.fechaCreacion) " +
           "ORDER BY FUNCTION('DATE', v.fechaCreacion)")
    @NonNull
    List<Object[]> getEstadisticasDiarias(
        @Param("fechaDesde") LocalDateTime fechaDesde,
        @Param("fechaHasta") LocalDateTime fechaHasta
    );
}