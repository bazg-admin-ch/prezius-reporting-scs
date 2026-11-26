package ch.admin.bazg.prezius.reporting.adapter.in.frontendconfig.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ui/configuration")
@RequiredArgsConstructor
class GetConfigurationUiRestController {
    private final FrontendConfigProperties frontendConfigProperties;

    @GetMapping
    public ConfigurationDTO getConfiguration() {
        return ConfigurationDTO.builder()
                .applicationUrl(frontendConfigProperties.getApplicationUrl().toString())
                .pamsEnvironment(frontendConfigProperties.getPamsEnvironment())
                .authority(frontendConfigProperties.getAuthority().toString())
                .redirectUrl(frontendConfigProperties.getRedirectUrl().toString())
                .clientId(frontendConfigProperties.getClientId())
                .mockPams(frontendConfigProperties.isMockPams())
                .tokenAwarePatterns(frontendConfigProperties.getTokenAwarePatterns())
                .build();
    }
}
