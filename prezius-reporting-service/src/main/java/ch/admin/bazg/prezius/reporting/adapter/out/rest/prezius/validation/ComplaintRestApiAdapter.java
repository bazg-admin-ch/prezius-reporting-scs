package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.validation;

import ch.admin.bazg.prezius.reporting.application.ports.out.GetComplaintsPort;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Complaint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Component
@Profile("!mock-restclient")
@RequiredArgsConstructor
class ComplaintRestApiAdapter implements GetComplaintsPort {

    @ValidationClientQualifier
    private final RestClient restClient;
    private final ComplaintDtoToModelMapper complaintDtoToModelMapper;

    @Override
    @Retryable
    public Map<UUID, Complaint> getComplaintsPerPosition(final UUID orderId) {
        final String apiPath = "/api/public/orders/v2/%s/complaints".formatted(orderId);
        final Map<UUID, ComplaintDto> complaintDtoMap = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(apiPath)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<UUID, ComplaintDto>>() {});
        return complaintDtoToModelMapper.toModelMap(complaintDtoMap);
    }

}
