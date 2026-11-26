package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.offices;

import ch.admin.bazg.prezius.reporting.application.ports.in.GetControlOfficesQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "GetControlOffices API", description = "The API for retrieving control offices")
@RestController
@RequestMapping("/api/ui/offices")
@RequiredArgsConstructor
class GetOfficesUiRestController {

    private final GetControlOfficesQuery getControlOfficesQuery;
    private final ControlOfficesUiRestDtoMapper controlOfficesUiRestDtoMapper;

    @Operation(summary = "Get all control offices")
    @GetMapping
    @PreAuthorize("hasRoleForAllPartners('reporting', 'read')")
    public List<ControlOfficeUiRestDto> getAll() {
        return controlOfficesUiRestDtoMapper.toRestDtos(getControlOfficesQuery.getControlOffices());
    }
}
