package ch.admin.bazg.prezius.reporting.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.togglz.core.context.FeatureContext;

import java.util.Arrays;

@Slf4j
@Component
@Profile("local")
public class FeatureFlagsLocalInitializer {

    @EventListener(ApplicationReadyEvent.class)
    public void enableAllFeatureFlags() {
        log.info("Enabling all feature flags 🚀");
        Arrays.stream(FeatureFlags.values()).forEach(featureFlag -> {
            FeatureContext.getFeatureManager().enable(featureFlag);
        });
    }
}