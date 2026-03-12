package org.simarro.rag_daw.model.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "usuarios")
public class UsuarioDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nombre;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    private String password;
    @NotNull
    @Column(nullable = false)
    private String estado = "PENDIENTE";
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    //En la tabla 'usuarios_roles' queremos sacar todos los 'idRol' correspondientes
    //al 'idUsuario' actual para poder generar la lista de 'roles' en 'UsuarioDb'.
    @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "idUsuario"),
    inverseJoinColumns = @JoinColumn(name = "idRol"))
    private Set<RolDb> roles = new HashSet<>();
    //Constructor con todos los campos menos 'id'
    public UsuarioDb(@NotNull String nombre, @NotNull String email, @NotNull String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }
}


