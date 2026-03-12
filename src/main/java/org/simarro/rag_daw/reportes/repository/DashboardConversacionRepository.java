package org.simarro.rag_daw.reportes.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.simarro.rag_daw.model.db.ConversacionDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardConversacionRepository extends JpaRepository<ConversacionDb, Long> {

       @Query("SELECT COUNT(c) FROM ConversacionDb c")
       Long countTotal();

       @Query("SELECT c.seccionTematica, COUNT(c) FROM ConversacionDb c GROUP BY c.seccionTematica")
       List<Object[]> countPorSeccion();

       @Query("SELECT CAST(c.fechaCreacion AS date), COUNT(c) FROM ConversacionDb c " +
                     "WHERE c.fechaCreacion BETWEEN :desde AND :hasta " +
                     "GROUP BY CAST(c.fechaCreacion AS date) ORDER BY 1")
       List<Object[]> actividadPorFecha(@Param("desde") LocalDateTime desde,
                     @Param("hasta") LocalDateTime hasta);
}