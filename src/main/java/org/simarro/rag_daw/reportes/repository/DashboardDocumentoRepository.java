package org.simarro.rag_daw.reportes.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.simarro.rag_daw.documentos.model.db.DocumentoDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Query(value = """
            SELECT u.nombre, 'SUBIDA_DOCUMENTO', d.nombre_fichero, CAST(d.fecha_subida AS VARCHAR)
            FROM documentos d JOIN usuarios u ON u.email = d.subido_por
            UNION ALL
            SELECT u.nombre, 'PREGUNTA_RAG', LEFT(m.contenido, 50), CAST(m.fecha AS VARCHAR)
            FROM mensajes m JOIN conversaciones c ON m.conversacion_id = c.id
                          JOIN usuarios u ON u.id = c.usuario_id
            WHERE m.tipo = 'USUARIO'
            UNION ALL
            SELECT u.nombre, 'VALORACION', v.valoracion::VARCHAR, CAST(v.fecha_creacion AS VARCHAR)
            FROM valoraciones v JOIN usuarios u ON u.id = v.usuario_id
            ORDER BY 4 DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> getActividadReciente();
}