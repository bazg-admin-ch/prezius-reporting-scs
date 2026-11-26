package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.hallmarking;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("adapter.prezius.hallmarking")
record PreziusHallmarkingRestProperties(
        @NotBlank String clientId,
        @NotBlank @URL(regexp = ".*[^/]") String apiUrl
) {}
