package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.businesspartners;

import ch.admin.bazg.prezius.common.authorization.AuthorizationInfo;
import ch.admin.bazg.prezius.common.authorization.PreziusAuthorizationService;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetBusinessPartnersQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Business Partner API", description = "The API for business partners")
@RestController
@RequestMapping("/api/ui/businesspartners")
@RequiredArgsConstructor
class GetBusinessPartnersUiRestController {

    private final GetBusinessPartnersQuery getBusinessPartnersQuery;
    private final PreziusAuthorizationService preziusAuthorizationService;
    private final GetBusinessPartnerResponseDtoMapper responseDtoMapper;

    @Operation(summary = "Get all business partners")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('reporting', 'read')")
    public List<GetBusinessPartnerResponseDto> getAll() {
        final AuthorizationInfo authorizationInfo = preziusAuthorizationService.generateAuthorizationInfo("reporting", "read");
        final var businessPartners = getBusinessPartnersQuery.getBusinessPartners(authorizationInfo);
        return responseDtoMapper.toDtoList(businessPartners);
    }
}
