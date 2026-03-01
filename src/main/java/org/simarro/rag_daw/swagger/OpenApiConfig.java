package org.simarro.rag_daw.swagger;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "JoseRa",
                        email = "joseramon.profesor@gmail.com",
                        url = "https://ieslluissimarro.org/DWES/"
                ),
                description = "OpenApi documentation for Spring Security",
                title = "OpenApi specification - JoseRa",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://some-url.com"
                ),
                termsOfService = "https://ieslluissimarro.org/DWES/TermsOfService/"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8090"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://ieslluissimarro.org/DWES/Production_API/"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}