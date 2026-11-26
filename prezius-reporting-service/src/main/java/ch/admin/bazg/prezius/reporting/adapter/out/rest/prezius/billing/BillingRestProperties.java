package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.billing;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("adapter.prezius.billing")
record BillingRestProperties(
        @NotBlank String clientId,
        @NotBlank @URL(regexp = ".*[^/]") String apiUrl
) {}