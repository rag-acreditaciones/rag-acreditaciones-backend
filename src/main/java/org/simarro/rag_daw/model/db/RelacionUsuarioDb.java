package org.simarro.rag_daw.model.db;

import java.io.Serializable;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "relaciones_usuarios")
public class RelacionUsuarioDb implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private UsuarioDb usuario;

    @ManyToOne
    @JoinColumn(name = "usuario_relacionado_id", nullable = false)
    @NotNull(message = "El usuario relacionado es obligatorio")
    private UsuarioDb usuarioRelacionado;

    @Column(name = "tipo_relacion", nullable = false)
    @Enumerated(EnumType.STRING) // Si no por defecto seria numérico
    @NotNull(message = "El tipo de relación es obligatorio")
    private TipoRelacion tipoRelacion;

    @Column(name = "fecha_inicio", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate fechaInicio;

    public enum TipoRelacion {
        @JsonProperty("supervisor-asesor")
        SUPERVISOR_ASESOR,
        @JsonProperty("asesor-candidato")
        ASESOR_CANDIDATO
    }
}
