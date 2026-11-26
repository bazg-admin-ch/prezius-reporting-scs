package ch.admin.bazg.prezius.reporting.adapter.out.rest.datahub;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("adapter.datahub")
record DatahubBusinessPartnerRestProperties(
        @NotBlank String clientId,
        @NotBlank @URL(regexp = ".*[^/]") String apiUrl,
        @NotBlank String businessPartnerRoleEmkCode
) {}
