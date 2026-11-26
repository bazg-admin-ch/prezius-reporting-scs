package ch.admin.bazg.prezius.reporting.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Prezius Reporting Service",
                description = "REST API for Prezius Reporting Service",
                contact = @Contact(
                        email = "jEAP-Community@bit.admin.ch",
                        name = "jEAP",
                        url = "https://confluence.example.com/display/BLUE/"
                )
        ),
        externalDocs = @ExternalDocumentation(
                url = "https://confluence.example.com/display/JEAP/Blueprint+Microservices",
                description = "Blueprint Microservices in Confluence"),
        security = {@SecurityRequirement(name = "OIDC")}
)
@Configuration
public class SwaggerConfig {
}
