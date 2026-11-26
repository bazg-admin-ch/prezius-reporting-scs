package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.billing;

import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetBillingPort;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Billing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@Profile("!mock-restclient")
@RequiredArgsConstructor
@Slf4j
class BillingRestApiAdapter implements GetBillingPort {

    @Qualifier("preziusBillingRestClient")
    private final RestClient restClient;
    private final BillingDtoToModelMapper billingDtoToModelMapper;

    @Override
    @Retryable
    public Billing getByOrderId(final UUID orderId, final OrderType orderType) {
        final String apiPath = orderType == OrderType.HALLMARKING ?
                "/api/public/hallmarking-orders/%s/billings".formatted(orderId) :
                "/api/public/analysis-orders/%s/billings".formatted(orderId);

        final BillingDto responseDto = restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(apiPath).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(BillingDto.class);
        return billingDtoToModelMapper.toModel(responseDto);
    }
}
