package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.urls;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@ConfigurationProperties(prefix = "prezius-urls")
@Validated
record PreziusServicesUrlProperties(
        @NotNull URI analysisServiceRootUrl,
        @NotNull URI auditServiceRootUrl,
        @NotNull URI billingServiceRootUrl,
        @NotNull URI applicationServiceRootUrl,
        @NotNull URI matchmakingServiceRootUrl,
        @NotNull URI registerServiceRootUrl,
        @NotNull URI orderServiceRootUrl
) {
}
