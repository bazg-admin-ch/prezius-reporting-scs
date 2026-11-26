package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.billing;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.reporting.cdc.PactConsumerBaseTest;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Billing;
import ch.admin.bit.jeap.security.resource.semanticAuthentication.SemanticApplicationRole;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationContext;
import ch.admin.bit.jeap.security.test.client.MockJeapOAuth2RestClientBuilderFactory;
import ch.admin.bit.jeap.security.test.jws.JwsBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@PactTestFor(pactVersion = PactSpecVersion.V4, providerName = "bazg-prezius-billing-scs")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GetHallmarkingBillingApiPactConsumerTest extends PactConsumerBaseTest {

    private final BillingRestApiAdapter billingRestApiAdapter;
    private final MockJeapOAuth2RestClientBuilderFactory restClientBuilderFactory;

    private static final String PROVIDER = "bazg-prezius-billing-scs";
    private static final String API_PATH = "/prezius-billing/api/public/hallmarking-orders/%s/billings";

    private String billingReadToken;
    private final UUID ORDER_ID = UUID.fromString("d4a3ecb9-695c-4822-8e21-157a620ead0e");

    @BeforeEach
    void init(@Autowired final JwsBuilderFactory jwsBuilderFactory) {
        final var semanticApplicationRole = SemanticApplicationRole.builder()
                .system("prezius")
                .resource("billing")
                .operation("read")
                .build();

        billingReadToken = jwsBuilderFactory.createValidForFixedLongPeriodBuilder("prezius-validation-scs", JeapAuthenticationContext.SYS)
                .withUserRoles(semanticApplicationRole)
                .build().serialize();
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    @Testable
    V4Pact getBillingById(final PactBuilder builder) {
        final String path = API_PATH.formatted(ORDER_ID);
        return builder
                .given("A billing for hallmarking order %s is present".formatted(ORDER_ID))
                .expectsToReceiveHttpInteraction("A GET request to " + path, httpInteractionBuilder -> httpInteractionBuilder
                        .withRequest(httpRequestBuilder -> httpRequestBuilder
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + billingReadToken)
                                .method(HttpMethod.GET.name())
                                .path(path))
                        .willRespondWith(httpResponseBuilder -> httpResponseBuilder
                                .status(200)
                                .header("content-type", "application/json")
                                .body(
                                        new PactDslJsonBody()
                                                .numberType("totalCostNoVat", 90.00)
                                                .numberType("totalCostWithVat", 98.10)
                                )
                        )
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getBillingById")
    void getBillingById_whenCallWithOrderId_thenReceiveBilling() {
        restClientBuilderFactory.getAuthTokenProvider().setAuthToken(billingReadToken);

        final Billing resultBilling = billingRestApiAdapter.getByOrderId(ORDER_ID, OrderType.HALLMARKING);

        assertThat(resultBilling.totalCostNoVat()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(resultBilling.totalCostWithVat()).isEqualByComparingTo(BigDecimal.valueOf(98.10));
    }
}
