package ch.admin.bazg.prezius.reporting.adapter.out.rest.datahub;

import ch.admin.bazg.prezius.reporting.application.ports.out.external.GetBusinessPartnersRemotePort;
import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Profile("!datahub-mock")
@Component
@RequiredArgsConstructor
class DataHubBusinessPartnerRestApiAdapter implements GetBusinessPartnersRemotePort {

    @Qualifier("datahubBusinessPartnerRestClient")
    private final RestClient restClient;
    private final DatahubBusinessPartnerRestProperties datahubBusinessPartnerRestProperties;
    private final DatahubBusinessPartnerRestDtoMapper mapper;

    @Override
    @Retryable
    public List<BusinessPartner> getAll() {
        final String emkCode = datahubBusinessPartnerRestProperties.businessPartnerRoleEmkCode();
        final DatahubBusinessPartnerRestDto[] businessPartnerDtoArray = restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/with-role/" + emkCode).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DatahubBusinessPartnerRestDto[].class);
        if (businessPartnerDtoArray == null) {
            return List.of();
        }
        return Arrays.stream(businessPartnerDtoArray)
                     .map(dto -> mapper.toModel(new DatahubBusinessPartnerRestDtoMapper.Input(dto, emkCode)))
                     .filter(Objects::nonNull)
                     .toList();
    }

    @Override
    @Retryable
    public Optional<BusinessPartner> getById(final String id) {
        final String emkCode = datahubBusinessPartnerRestProperties.businessPartnerRoleEmkCode();
        final DatahubBusinessPartnerRestDto[] businessPartners = restClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/").queryParam("withRoles", "true").build())
                .body(List.of(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DatahubBusinessPartnerRestDto[].class);
        if (businessPartners == null || businessPartners.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(businessPartners)
                     .filter(bp -> bp.roles() != null && bp.roles().stream().map(DatahubBusinessPartnerRestDto.RoleRestDto::businessId).anyMatch(roles -> roles.contains(emkCode)))
                     .map(dto -> mapper.toModel(new DatahubBusinessPartnerRestDtoMapper.Input(dto, emkCode)))
                     .findFirst();
    }

}
