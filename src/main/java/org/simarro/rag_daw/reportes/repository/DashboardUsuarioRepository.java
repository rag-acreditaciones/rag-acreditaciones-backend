package org.simarro.rag_daw.reportes.repository;

import org.simarro.rag_daw.model.db.UsuarioDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardUsuarioRepository extends JpaRepository<UsuarioDb, Long> {

    @Query("SELECT COUNT(u) FROM UsuarioDb u")
    Long countTotal();

    @Query("SELECT r.nombre, COUNT(u) FROM UsuarioDb u JOIN u.roles r GROUP BY r.nombre")
    List<Object[]> countPorRol();
}