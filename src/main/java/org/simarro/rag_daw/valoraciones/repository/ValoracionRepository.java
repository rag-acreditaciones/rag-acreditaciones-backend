package org.simarro.rag_daw.valoraciones.repository;

import java.util.List;
import java.util.Optional;

import org.simarro.rag_daw.valoraciones.model.db.ValoracionDb;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadEvolucionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ValoracionRepository extends JpaRepository<ValoracionDb, Long> {

    Optional<ValoracionDb> findByMensajeIdAndUsuarioId(Long mensajeId, Long usuarioId);

    List<ValoracionDb> findByMensajeId(Long mensajeId);

    List<ValoracionDb> findByConversacionId(Long conversacionId);

    long countByValoracion(ValoracionDb.TipoValoracion valoracion);

    // ---- CALIDAD ----

    @Query("""
        SELECT new org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO(
            'General',
            SUM(CASE WHEN v.valoracion='POSITIVA' THEN 1 ELSE 0 END),
            SUM(CASE WHEN v.valoracion='NEGATIVA' THEN 1 ELSE 0 END),
            (SUM(CASE WHEN v.valoracion='POSITIVA' THEN 1 ELSE 0 END) * 1.0) / COUNT(v),
            COUNT(v)
        )
        FROM ValoracionDb v
        """)
    List<CalidadPorSeccionDTO> getCalidadPorSeccion();

    @Query("""
        SELECT new org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO(
            v.mensajeId,
            'Respuesta',
            SUM(CASE WHEN v.valoracion='POSITIVA' THEN 1 ELSE 0 END),
            SUM(CASE WHEN v.valoracion='NEGATIVA' THEN 1 ELSE 0 END),
            0
        )
        FROM ValoracionDb v
        GROUP BY v.mensajeId
        ORDER BY SUM(CASE WHEN v.valoracion='POSITIVA' THEN 1 ELSE 0 END) DESC
        """)
    List<TopRespuestaDTO> getTopMejoresRespuestas(int limit);

    @Query("""
        SELECT new org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO(
            v.mensajeId,
            'Respuesta',
            SUM(CASE WHEN v.valoracion='POSITIVA' THEN 1 ELSE 0 END),
            SUM(CASE WHEN v.valoracion='NEGATIVA' THEN 1 ELSE 0 END),
            0
        )
        FROM ValoracionDb v
        GROUP BY v.mensajeId
        ORDER BY SUM(CASE WHEN v.valoracion='NEGATIVA' THEN 1 ELSE 0 END) DESC
        """)
    List<TopRespuestaDTO> getTopPeoresRespuestas(int limit);

    @Query("""
        SELECT new org.simarro.rag_daw.valoraciones.model.dto.CalidadEvolucionDTO(
            DATE(v.fechaCreacion),
            SUM(CASE WHEN v.valoracion='POSITIVA' THEN 1 ELSE 0 END),
            SUM(CASE WHEN v.valoracion='NEGATIVA' THEN 1 ELSE 0 END),
            (SUM(CASE WHEN v.valoracion='POSITIVA' THEN 1 ELSE 0 END) * 1.0) / COUNT(v),
            0
        )
        FROM ValoracionDb v
        GROUP BY DATE(v.fechaCreacion)
        ORDER BY DATE(v.fechaCreacion)
        """)
    List<CalidadEvolucionDTO> getEvolucionCalidad(
            String fechaDesde,
            String fechaHasta,
            String agrupacion);
}