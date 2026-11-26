package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.featureflag;

import ch.admin.bazg.prezius.reporting.config.FeatureFlags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ui/")
@Tag(name = "Endpoints for features", description = "Prezius Feature Resource")
class GetFeatureFlagsUiRestController {

    enum Format {
        DEFAULT,
        ALL_ENABLED,
        ALL_DISABLED
    }

    @Operation(summary = "Returns the feature states.",
               responses = {@ApiResponse(responseCode = "200", description = "Success")}
    )
    @GetMapping(value = "features")
    public ResponseEntity<Map<String, Boolean>> getFeatures(@RequestParam(required = false, defaultValue = "DEFAULT") final Format format) {
      final Map<String, Boolean> features = Arrays.stream(FeatureFlags.values())
                                                  .collect(toMap(FeatureFlags::name, flag -> getIsActive(format, flag)));

      return ResponseEntity.ok(features);
    }

    private boolean getIsActive(final Format format, final FeatureFlags flag) {
        return switch (format) {
            case ALL_ENABLED -> true;
            case ALL_DISABLED -> false;
            case DEFAULT -> flag.isActive();
        };
    }

}
