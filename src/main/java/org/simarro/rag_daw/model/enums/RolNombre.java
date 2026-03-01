package org.simarro.rag_daw.model.enums;

public enum RolNombre {
        ROLE_ADMIN("Administrador con acceso completo"),
        ROLE_USER("Usuario estándar"),
        ROLE_MODERATOR("Moderador con permisos intermedios"),
        ROLE_ENTRENADOR("Entrenador de un club de futbol"),
        ROLE_JUGADOR("Jugador de un equipo de futbol"),
        ROLE_ARBITRO("Arbitro de un partido"),
        ROLE_CANDIDATO("Candidato a una acreditación"),
        ROLE_ASESOR("Asesor de una acreditación"),
        ROLE_EVALUADOR ("Evaluador de una acreditación"),
        ROLE_PROFESOR("Profesor de una institución educativa"),
        ROLE_JEFEDPTO("Jefe de departamento de una institución educativa"),
        ROLE_JEFEESTUDIOS("Jefe de estudios de una institución educativa"),
        ROLE_ADMINISTRATIVO("Administrativo de una institución educativa"),
        ROLE_ADMINISTRADOR("Administrador de la aplicación"); 
        private final String descripcion;
    
        RolNombre(String descripcion) {
            this.descripcion = descripcion;
        }
    
        public String getDescripcion() {
            return descripcion;
        }
}
