package org.simarro.rag_daw.reportes.repository;

import org.simarro.rag_daw.documentos.model.db.DocumentoDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardDocumentoRepository extends JpaRepository<DocumentoDb, Long> {

    @Query("SELECT COUNT(d) FROM DocumentoDb d WHERE d.estado != 'ELIMINADO'")
    Long countTotal();

    @Query("SELECT s.nombre, COUNT(d) FROM DocumentoDb d JOIN d.seccionTematica s GROUP BY s.nombre")
    List<Object[]> countPorSeccion();

    @Query("SELECT d.estado, COUNT(d) FROM DocumentoDb d GROUP BY d.estado")
    List<Object[]> countPorEstado();

    @Query("SELECT CAST(d.fechaSubida AS date), COUNT(d) FROM DocumentoDb d " +
           "WHERE d.fechaSubida BETWEEN :desde AND :hasta " +
           "GROUP BY CAST(d.fechaSubida AS date) ORDER BY 1")
    List<Object[]> evolucionPorFecha(@Param("desde") LocalDateTime desde,
                                     @Param("hasta") LocalDateTime hasta);
}