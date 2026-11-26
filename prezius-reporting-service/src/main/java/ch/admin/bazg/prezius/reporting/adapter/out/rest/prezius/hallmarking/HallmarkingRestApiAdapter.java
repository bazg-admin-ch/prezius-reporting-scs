package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.hallmarking;

import ch.admin.bazg.prezius.reporting.application.ports.out.GetHallmarkingsPort;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Hallmarking;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Profile("!mock-restclient")
@RequiredArgsConstructor
class HallmarkingRestApiAdapter implements GetHallmarkingsPort {

    @HallmarkingClientQualifier
    private final RestClient restClient;
    private final HallmarkingsPerPositionMapper hallmarkingsPerPositionMapper;

    @Override
    @Retryable
    public Map<UUID, List<Hallmarking>> getHallmarkingsPerPosition(final String orderBusinessId) {
        final Map<UUID, List<GetHallmarkingOutputRestDto>> hallmarkingsPerPositionMap = restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/public/orders/{orderBusinessId}/hallmarkings")
                        .build(orderBusinessId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<UUID, List<GetHallmarkingOutputRestDto>>>() {});
        return hallmarkingsPerPositionMapper.toModelMap(hallmarkingsPerPositionMap);
    }
}
