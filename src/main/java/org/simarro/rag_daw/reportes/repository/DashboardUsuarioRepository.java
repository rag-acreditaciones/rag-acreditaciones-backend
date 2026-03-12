package org.simarro.rag_daw.reportes.repository;

import java.util.List;

import org.simarro.rag_daw.model.db.UsuarioDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardUsuarioRepository extends JpaRepository<UsuarioDb, Long> {

    @Query("SELECT COUNT(u) FROM UsuarioDb u")
    Long countTotal();

    @Query("SELECT r.nombre, COUNT(u) FROM UsuarioDb u JOIN u.roles r GROUP BY r.nombre")
    List<Object[]> countPorRol();

    @Query(value = """
            SELECT u.email, u.nombre,
                COUNT(DISTINCT d.id) as docs_subidos,
                COUNT(DISTINCT c.id) as conversaciones,
                COUNT(DISTINCT d.id) + COUNT(DISTINCT c.id) as total
            FROM usuarios u
            LEFT JOIN documentos d ON d.subido_por = u.email
            LEFT JOIN conversaciones c ON c.usuario_id = u.id
            GROUP BY u.id, u.email, u.nombre
            ORDER BY
                CASE WHEN :criterio = 'DOCS'  THEN COUNT(DISTINCT d.id)
                    WHEN :criterio = 'CHATS' THEN COUNT(DISTINCT c.id)
                    ELSE COUNT(DISTINCT d.id) + COUNT(DISTINCT c.id)
                END DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> getRankingUsuarios(@Param("limit") int limit, @Param("criterio") String criterio);
}