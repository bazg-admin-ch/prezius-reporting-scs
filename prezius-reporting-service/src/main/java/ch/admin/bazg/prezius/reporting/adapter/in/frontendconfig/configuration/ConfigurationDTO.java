package ch.admin.bazg.prezius.reporting.adapter.in.frontendconfig.configuration;

import lombok.Builder;

import java.util.List;

@Builder
record ConfigurationDTO(
        String authority,
        String applicationUrl,
        String clientId,
        String redirectUrl,
        boolean mockPams,
        String pamsEnvironment,
        List<String> tokenAwarePatterns
) {
}
