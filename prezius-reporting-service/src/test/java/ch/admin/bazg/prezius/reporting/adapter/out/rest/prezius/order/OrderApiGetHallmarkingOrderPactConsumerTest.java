package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.order;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.Category;
import ch.admin.bazg.prezius.reporting.cdc.PactConsumerBaseTest;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingOrder;
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

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@PactTestFor(pactVersion = PactSpecVersion.V4, providerName = "bazg-prezius-order-scs")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class OrderApiGetHallmarkingOrderPactConsumerTest extends PactConsumerBaseTest {

    private final OrderServicePublicRestApiAdapter orderServicePublicRestApiAdapter;
    private final MockJeapOAuth2RestClientBuilderFactory restClientBuilderFactory;

    private static final String PROVIDER = "bazg-prezius-order-scs";
    private static final String API_PATH = "/prezius-order/api/public/hallmarking-orders/%s";

    private String orderReadToken;
    //Values
    private final String ORDER_BUSINESS_ID = "240802-PMHM-08-327834";
    private final UUID ORDER_ID = UUID.fromString("c24b0d48-b159-4c8a-8b18-ede81cee3752");
    private final UUID POSITION_ID = UUID.fromString("b2301129-8651-4a82-b3d1-41991e24b0ad");
    private final String BUSINESS_PARTNER_ID = "1000131412";
    private final String INTERMEDIARY_PARTNER_ID = "1000131412";

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
                .given("An order with orderId %s present".formatted(ORDER_ID))
                .expectsToReceiveHttpInteraction("A GET request to " + path, httpInteractionBuilder -> httpInteractionBuilder
                        .withRequest(httpRequestBuilder -> httpRequestBuilder
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + orderReadToken)
                                .method(HttpMethod.GET.name())
                                .path(path))
                        .willRespondWith(httpResponseBuilder -> httpResponseBuilder
                                .status(200)
                                .header("content-type", "application/json")
                                .body(
                                        Objects.requireNonNull(Objects.requireNonNull(new PactDslJsonBody()
                                                        .uuid("id", ORDER_ID)
                                                        .stringValue("orderBusinessId", ORDER_BUSINESS_ID)
                                                        .stringValue("businessPartnerId", BUSINESS_PARTNER_ID)
                                                        .stringValue("intermediaryBusinessPartnerId", INTERMEDIARY_PARTNER_ID)
                                                        .stringValue("riskProfile", "MEDIUM")
                                                        .stringValue("status", "IN_PROGRESS")
                                                        .stringValue("hallmarkingLocation", "OFFICE")
                                                        .stringValue("invoicing", "CLIENT_ACCOUNT")
                                                        .datetime("validationDate")
                                                        .booleanValue("certifiedMaterial", false)
                                                        .booleanType("swissPunching")
                                                        .booleanType("internationalPunching")
                                                        .booleanType("isValidated")
                                                        .minArrayLike("positions", 1)
                                                        .uuid("id", POSITION_ID)
                                                        .numberValue("batchCount", 10)
                                                        .numberValue("notContestedBatchCount", 10)
                                                        .numberValue("number", 10)
                                                        .stringValue("goodsType", "WATCH_CASE")
                                                        .booleanValue("ag", false)
                                                        .booleanValue("au", true)
                                                        .booleanValue("pt", false)
                                                        .booleanValue("pd", false)
                                                        .stringType("fineness")
                                                        .stringType("category", Category.NORMAL.name())
                                                        .stringType("responsibilityMark")
                                                        .integerType("pieceCount")
                                                        .integerType("notContestedPieceCount")
                                                        .booleanType("formalExamDone")
                                                        .booleanType("materialExamDone")
                                                        .booleanType("validationDone")
                                                        .booleanType("complaintResolved")
                                                        .booleanType("hallmarkingDone")
                                                        .booleanType("certificateRequiredBySystem")
                                                        .booleanType("certificateUploaded")
                                                        .closeObject())
                                                .closeArray())
                                )
                        )
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getOrderById")
    void getOrderById_whenCallWithOrderBusinessId_thenReceiveOrderDto() {
        restClientBuilderFactory.getAuthTokenProvider().setAuthToken(orderReadToken);

        final HallmarkingOrder resultOrder = orderServicePublicRestApiAdapter.getHallmarkingOrderById(ORDER_ID);

        assertThat(resultOrder.businessId()).isEqualTo(ORDER_BUSINESS_ID);
        assertThat(resultOrder.businessPartnerId()).isEqualTo(BUSINESS_PARTNER_ID);
        assertThat(resultOrder.positions()).allSatisfy(position -> {
            assertThat(position.id()).isNotNull();
        });
        assertThat(resultOrder.positions()).hasSizeGreaterThan(0);
    }
}
