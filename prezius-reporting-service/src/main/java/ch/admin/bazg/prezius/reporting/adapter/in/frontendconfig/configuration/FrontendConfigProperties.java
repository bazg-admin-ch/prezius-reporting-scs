package ch.admin.bazg.prezius.reporting.adapter.in.frontendconfig.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * Configuration properties that will be forwarded to the UI
 */
@Component
@ConfigurationProperties(prefix = "frontend")
@Data
class FrontendConfigProperties {
    /**
     * Authentication server to be used.
     */
    private URI authority;
    /**
     * URL of the application for the redirect URI after a login.
     */
    private URI applicationUrl;
    /**
     * URL to go to after login.
     */
    private URI redirectUrl;
    /**
     * Client id for frontend
     */
    private String clientId;
    /**
     * Should PAMS mock be used.
     */
    private boolean mockPams;
    /**
     * Pams Environment to be used.
     */
    private String pamsEnvironment;
    /**
     * List of backends where to a token shall be send.
     */
    private List<String> tokenAwarePatterns;
}
