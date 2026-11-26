package ch.admin.bazg.prezius.reporting.adapter.in.rest.devops;

import ch.admin.bazg.prezius.reporting.application.ports.in.SynchronizeBusinessPartnersUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Error Handling API", description = "The API for error handling, should only be used by devops")
@RestController
@RequestMapping("/api/devops")
@RequiredArgsConstructor
class SynchronizeBusinessPartnersDevopsRestController {

    private final SynchronizeBusinessPartnersUseCase synchronizeBusinessPartnersUseCase;

    @Operation(summary = "Synchronize business partners with DataHub")
    @PutMapping(value = "/synchronize-business-partners")
    @PreAuthorize("hasRole('errorhandling', 'retry')")
    public void synchronizeBusinessPartners() {
        synchronizeBusinessPartnersUseCase.synchronizeAll();
    }
}
