package ch.admin.bazg.prezius.reporting.adapter.in.rest.test.featureflag;

import ch.admin.bazg.prezius.reporting.config.FeatureFlags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.togglz.core.context.FeatureContext;
import org.togglz.core.repository.FeatureState;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("/api/test/")
@Profile("local")
@Tag(name = "Endpoints for features")
class UpdateFeatureFlagsTestRestController {

    @Operation(summary = "Set the feature flags locally so that no feature server is needed.",
               responses = { @ApiResponse(responseCode = "200", description = "Success") }
    )
    @PutMapping(value = "features")
    public ResponseEntity<Map<String, Boolean>> updateFeature(@RequestBody final Map<FeatureFlags, Boolean> featureSettingsMap) {
        featureSettingsMap.forEach((feature, enabled) -> {
            final FeatureState featureState = new FeatureState(feature, enabled);
            FeatureContext.getFeatureManager().setFeatureState(featureState);
        });

        final Map<String, Boolean> features = Arrays.stream(FeatureFlags.values())
                                                    .collect(toMap(FeatureFlags::name, FeatureFlags::isActive));
        return ResponseEntity.ok(features);
    }
}
