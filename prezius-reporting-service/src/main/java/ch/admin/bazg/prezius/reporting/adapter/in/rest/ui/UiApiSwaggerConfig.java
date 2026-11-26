package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UiApiSwaggerConfig {

    @Bean
    GroupedOpenApi uiApi() {
        return GroupedOpenApi.builder()
                             .group("UI API")
                             .pathsToMatch(
                                     "/api/ui/**",
                                     "/ui-api/**"
                                          )
                             .packagesToScan(this.getClass().getPackageName())
                             .build();
    }

}
