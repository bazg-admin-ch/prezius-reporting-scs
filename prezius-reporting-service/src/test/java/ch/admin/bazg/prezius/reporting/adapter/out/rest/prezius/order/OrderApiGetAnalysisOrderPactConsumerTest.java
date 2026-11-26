package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.order;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.reporting.cdc.PactConsumerBaseTest;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisOrder;
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

import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.assertj.core.api.Assertions.assertThat;

@PactTestFor(pactVersion = PactSpecVersion.V4, providerName = "bazg-prezius-order-scs")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrderApiGetAnalysisOrderPactConsumerTest extends PactConsumerBaseTest {

    private static final String PROVIDER = "bazg-prezius-order-scs";
    private static final String API_PATH = "/prezius-order/api/public/analysis-orders/%s";

    private static final String ORDER_BUSINESS_ID = "240802-PMHM-08-327834";
    private static final UUID ORDER_ID = UUID.fromString("22acc407-67a1-4717-8551-4b9b7ba43400");
    private static final UUID POSITION_ID = UUID.fromString("b2301129-8651-4a82-b3d1-41991e24b0ad");
    private static final String BUSINESS_PARTNER_ID = "1000131412";
    private static final String INTERMEDIARY_PARTNER_ID = "1000131412";

    private final OrderServicePublicRestApiAdapter orderServicePublicRestApiAdapter;
    private final MockJeapOAuth2RestClientBuilderFactory restClientBuilderFactory;

    private String orderReadToken;

    @BeforeEach
    void init(@Autowired final JwsBuilderFactory jwsBuilderFactory) {
        final var semanticApplicationRole = SemanticApplicationRole.builder()
                .system("prezius")
                .resource("order")
                .operation("read")
                .build();

        orderReadToken = jwsBuilderFactory.createValidForFixedLongPeriodBuilder("prezius-validation-scs", JeapAuthenticationContext.SYS)
                .withUserRoles(semanticApplicationRole)
                .build().serialize();
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    @Testable
    V4Pact getOrderById(final PactBuilder builder) {
        final String path = API_PATH.formatted(ORDER_ID);
        return builder
                .given("An analysis order with id %s is present".formatted(ORDER_ID))
                .expectsToReceiveHttpInteraction("A GET request to " + path, httpInteractionBuilder -> httpInteractionBuilder
                        .withRequest(httpRequestBuilder -> httpRequestBuilder
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + orderReadToken)
                                .method(HttpMethod.GET.name())
                                .path(path))
                        .willRespondWith(httpResponseBuilder -> httpResponseBuilder
                                .status(200)
                                .header("content-type", "application/json")
                                .body(newJsonBody(jsonBody ->
                                                jsonBody
                                                        .uuid("id", ORDER_ID)
                                                        .stringType("orderBusinessId", ORDER_BUSINESS_ID)
                                                        .stringType("office", ControlOffice.BASC.toString())
                                                        .stringType("businessPartnerId", BUSINESS_PARTNER_ID)
                                                        .stringType("intermediaryBusinessPartnerId", INTERMEDIARY_PARTNER_ID)
                                                        .stringType("invoicing", "CLIENT_ACCOUNT")
                                                        .stringType("status", "IN_PROGRESS")
                                                        .stringType("analysisType", AnalysisType.CONFORMITY_EVALUATION.name())
                                                        .stringType("clientReference")
                                                        .stringType("materialReceivedDate", "2025-01-31T16:48:19.661Z")
                                                        .booleanType("arbitraryAnalysisNeeded", false)
                                                        .booleanType("ag", true)
                                                        .booleanType("au", true)
                                                        .booleanType("pd", false)
                                                        .booleanType("pt", false)
                                                        .minArrayLike("positions", 1, position -> position
                                                                .uuid("id", POSITION_ID)
                                                                .uuid("orderId")
                                                                .stringType("orderBusinessId")
                                                                .integerType("number")
                                                                .stringType("description")
                                                                .booleanType("analysisDone", false)
                                                        )
                                        ).build()
                                )
                        )
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getOrderById")
    void getOrderById_whenCallWithOrderBusinessId_thenReceiveOrderDto() {
        restClientBuilderFactory.getAuthTokenProvider().setAuthToken(orderReadToken);

        final AnalysisOrder resultOrder = orderServicePublicRestApiAdapter.getAnalysisOrderById(ORDER_ID);

        assertThat(resultOrder.businessId()).isEqualTo(ORDER_BUSINESS_ID);
        assertThat(resultOrder.businessPartnerId()).isEqualTo(BUSINESS_PARTNER_ID);
        assertThat(resultOrder.positions()).allSatisfy(position -> assertThat(position.id()).isNotNull());
        assertThat(resultOrder.positions()).hasSizeGreaterThan(0);
    }
}
