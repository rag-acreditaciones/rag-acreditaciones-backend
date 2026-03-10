package org.simarro.rag_daw.documentos.model.db;

import java.time.LocalDateTime;

import org.simarro.rag_daw.documentos.model.enums.EstadoDocumento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documentos")
public class DocumentoDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "nombre_fichero", nullable = false)
    private String nombreFichero;

    @Size(max = 100)
    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Size(max = 500)
    private String descripcion;

    @Column(name = "base64_contenido", columnDefinition = "TEXT")
    private String base64Contenido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seccion_tematica_id")
    private SeccionTematicaDb seccionTematica;

    @NotBlank
    @Column(name = "subido_por", nullable = false)
    private String subidoPor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDocumento estado = EstadoDocumento.PENDIENTE;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida = LocalDateTime.now();
}
