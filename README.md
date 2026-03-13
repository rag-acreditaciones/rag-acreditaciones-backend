# RAG Acreditaciones - Backend (Spring Boot)

Este es el backend del proyecto **RAG Acreditaciones**, desarrollado con **Spring Boot 3.5** y **Java 21**. Proporciona una API RESTful para gestionar documentos, acreditaciones, e integración con Inteligencia Artificial utilizando **Spring AI**.

## 🛠️ Tecnologías Principales

- **Java 21**
- **Spring Boot 3.5.11**
- **Spring AI**: Integración con Ollama (LLM local) y pgvector (Base de datos vectorial).
- **PostgreSQL + pgvector**: Base de datos relacional y vectorial.
- **Docker & Docker Compose**: Para levantar fácilmente la infraestructura externa (BD y Ollama).
- **Lombok & MapStruct**: Reducción de código repetitivo y mapeo de DTOs.
- **Spring Security & JWT**: Autenticación y autorización.
- **OpenAPI/Swagger**: Documentación interactiva de la API.

## 🚀 Requisitos Previos

1. **Java 21** instalado en el sistema.
2. **Maven** (o usar el wrapper incluido `./mvnw`).
3. **Docker y Docker Compose** instalados y ejecutándose.

## 🐳 Infraestructura (Docker Compose)

El proyecto depende de dos servicios clave gestionados por Docker:
- **PostgreSQL (con pgvector)**: Puerto `5432`.
- **Ollama (LLM Local)**: Puerto `11434`.

### Scripts de Gestión

Para facilitar el desarrollo, se incluyen varios scripts Bash en la raíz del backend:

1. **Arrancar (Sin borrar datos previos):**
   ```bash
   ./arrancar.sh
   ```
   Levanta los contenedores. Si es la primera vez y no hay modelos de Ollama, intentará descargar los modelos mínimos necesarios automáticamente.

2. **Arrancar desde Cero (Limpiando BD):**
   ```bash
   ./limpiar_y_arrancar.sh
   ```
   Borra el volumen de PostgreSQL (ideal para aplicar `seed/data.sql` de nuevo) pero **conserva los modelos** pesados de Ollama.

3. **Parar la Infraestructura:**
   ```bash
   ./parar.sh
   ```
   Detiene los contenedores de forma segura sin borrar datos.

4. **Descarga Manual de Modelos (Ollama):**
   - `./init-ollama-local-minimo.sh`: Descarga *all-minilm* y *llama3.2:1b* (~1.4GB en total).
   - `./init-ollama-local.sh`: Para modelos más potentes si dispones de recursos.

## ▶️ Cómo arrancar la aplicación Spring Boot

1. Asegúrate de que los contenedores están arriba (`./arrancar.sh`).
2. Abre un terminal en esta carpeta (`rag-acreditaciones-backend`).
3. Ejecuta la aplicación mediante Maven Wrapper:

   ```bash
   ./mvnw spring-boot:run
   ```

   *(En Windows puedes usar `mvnw.cmd spring-boot:run`)*

La aplicación se levantará por defecto en el puerto `8080`.

## 📚 Documentación de la API (Swagger)

Una vez arrancada la aplicación, puedes explorar e interactuar con los endpoints a través de Swagger UI:

👉 **http://localhost:8080/swagger-ui/index.html**

*(Los datos de configuración de la BD y la API KEY externa de Groq se encuentran en `src/main/resources/application.properties`).*
