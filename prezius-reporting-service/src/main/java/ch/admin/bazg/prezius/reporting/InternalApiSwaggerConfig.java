package ch.admin.bazg.prezius.reporting;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalApiSwaggerConfig {
    @Bean
    GroupedOpenApi internalApi() {
        return GroupedOpenApi.builder()
                .group("- Prezius Reporting API")
                .pathsToMatch("/api/**")
                .packagesToScan(this.getClass().getPackageName())
                .build();
    }
}
