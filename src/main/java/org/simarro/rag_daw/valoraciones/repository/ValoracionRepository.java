package org.simarro.rag_daw.valoraciones.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.simarro.rag_daw.valoraciones.model.db.ValoracionDb;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ValoracionRepository extends JpaRepository<ValoracionDb, Long> {

    Optional<ValoracionDb> findByMensajeIdAndUsuarioId(Long mensajeId, Long usuarioId);

    List<ValoracionDb> findByMensajeId(Long mensajeId);

    long countByValoracion(ValoracionDb.TipoValoracion valoracion);

    List<ValoracionDb> findByFechaCreacionBetween(LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    @Query("""
        SELECT new org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO(
            'General',
            SUM(CASE WHEN v.valoracion = org.simarro.rag_daw.valoraciones.model.db.ValoracionDb$TipoValoracion.POSITIVA THEN 1L ELSE 0L END),
            SUM(CASE WHEN v.valoracion = org.simarro.rag_daw.valoraciones.model.db.ValoracionDb$TipoValoracion.NEGATIVA THEN 1L ELSE 0L END),
            CASE
                WHEN COUNT(v) = 0 THEN 0.0
                ELSE 1.0 * SUM(CASE WHEN v.valoracion = org.simarro.rag_daw.valoraciones.model.db.ValoracionDb$TipoValoracion.POSITIVA THEN 1L ELSE 0L END) / COUNT(v)
            END,
            COUNT(v)
        )
        FROM ValoracionDb v
        """)
    List<CalidadPorSeccionDTO> getCalidadPorSeccion();

    @Query("""
        SELECT new org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO(
            v.mensajeId,
            'Respuesta',
            SUM(CASE WHEN v.valoracion = org.simarro.rag_daw.valoraciones.model.db.ValoracionDb$TipoValoracion.POSITIVA THEN 1L ELSE 0L END),
            SUM(CASE WHEN v.valoracion = org.simarro.rag_daw.valoraciones.model.db.ValoracionDb$TipoValoracion.NEGATIVA THEN 1L ELSE 0L END),
            0L
        )
        FROM ValoracionDb v
        GROUP BY v.mensajeId
        ORDER BY SUM(CASE WHEN v.valoracion = org.simarro.rag_daw.valoraciones.model.db.ValoracionDb$TipoValoracion.POSITIVA THEN 1L ELSE 0L END) DESC
        """)
    List<TopRespuestaDTO> getTopMejoresRespuestas();

    @Query("""
        SELECT new org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO(
            v.mensajeId,
            'Respuesta',
            SUM(CASE WHEN v.valoracion = org.simarro.rag_daw.valoraciones.model.db.ValoracionDb$TipoValoracion.POSITIVA THEN 1L ELSE 0L END),
            SUM(CASE WHEN v.valoracion = org.simarro.rag_daw.valoraciones.model.db.ValoracionDb$TipoValoracion.NEGATIVA THEN 1L ELSE 0L END),
            0L
        )
        FROM ValoracionDb v
        GROUP BY v.mensajeId
        ORDER BY SUM(CASE WHEN v.valoracion = org.simarro.rag_daw.valoraciones.model.db.ValoracionDb$TipoValoracion.NEGATIVA THEN 1L ELSE 0L END) DESC
        """)
    List<TopRespuestaDTO> getTopPeoresRespuestas();

    @Query("SELECT v FROM ValoracionDb v WHERE v.fechaCreacion BETWEEN :fechaDesde AND :fechaHasta")
    List<ValoracionDb> findEnRangoFechas(
    @Param("fechaDesde") LocalDateTime fechaDesde, 
    @Param("fechaHasta") LocalDateTime fechaHasta
    );
}