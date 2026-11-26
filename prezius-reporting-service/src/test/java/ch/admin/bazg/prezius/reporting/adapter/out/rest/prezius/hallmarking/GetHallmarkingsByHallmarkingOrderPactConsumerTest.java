package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.hallmarking;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.cdc.PactConsumerBaseTest;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Hallmarking;
import ch.admin.bit.jeap.security.resource.semanticAuthentication.SemanticApplicationRole;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationContext;
import ch.admin.bit.jeap.security.test.client.MockJeapOAuth2RestClientBuilderFactory;
import ch.admin.bit.jeap.security.test.jws.JwsBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.assertj.core.api.Assertions.assertThat;

@PactTestFor(pactVersion = PactSpecVersion.V4, providerName = "bazg-prezius-hallmarking-scs")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GetHallmarkingsByHallmarkingOrderPactConsumerTest extends PactConsumerBaseTest {

    private final MockJeapOAuth2RestClientBuilderFactory restClientBuilderFactory;
    private final HallmarkingRestApiAdapter hallmarkingRestApiAdapter;

    private static final String PROVIDER = "bazg-prezius-hallmarking-scs";
    private static final String API_PATH = "/prezius-hallmarking/api";
    private static final String ORDER_BUSINESS_ID = "orderBusinessId";
    private static final UUID POSITION_UUID = UUID.fromString("20c68243-e6b6-41c1-b75f-c5166fbea8da");
    private String hallmarkingToken;

    @BeforeEach
    void init(@Autowired final JwsBuilderFactory jwsBuilderFactory) {
        final SemanticApplicationRole semanticHallmarkingApplicationRole = SemanticApplicationRole.builder()
                                                                                                  .system("prezius")
                                                                                                  .resource("hallmarking")
                                                                                                  .operation("read")
                                                                                                  .build();

        hallmarkingToken = jwsBuilderFactory.createValidForFixedLongPeriodBuilder("prezius-client", JeapAuthenticationContext.SYS)
                                            .withUserRoles(semanticHallmarkingApplicationRole)
                                            .build().serialize();
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    @SuppressWarnings("unused")
    private V4Pact getHallmarkingsByOrderId(final PactBuilder builder) {
        final String path = API_PATH + "/public/orders/%s/hallmarkings".formatted(ORDER_BUSINESS_ID);
        return builder
                .given("Two hallmarkings for position 20c68243-e6b6-41c1-b75f-c5166fbea8da exist")
                .expectsToReceiveHttpInteraction("A GET request to " + path, httpInteractionBuilder -> httpInteractionBuilder
                        .withRequest(httpRequestBuilder -> httpRequestBuilder
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + hallmarkingToken)
                                .method(HttpMethod.GET.name())
                                .path(path))
                        .willRespondWith(httpResponseBuilder -> httpResponseBuilder
                                .status(200)
                                .header("content-type", "application/json")
                                .body(newJsonBody(body -> body
                                                          .array(POSITION_UUID.toString(), hallmarkingsList -> hallmarkingsList
                                                                  .object(hallmarking -> hallmarking
                                                                          .stringValue("hallmarkingType", "SINGLE_SWISS")
                                                                          .stringValue("hallmarkingMethod", "MECHANICAL")
                                                                         )
                                                                  .object(hallmarking -> hallmarking
                                                                          .stringValue("hallmarkingType", "SINGLE_INTERNATIONAL")
                                                                          .stringValue("hallmarkingMethod", "LASER")
                                                                         )
                                                                )
                                                 ).build()
                                     )))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getHallmarkingsByOrderId")
    void testGetHallmarkingsByOrderId() {
        restClientBuilderFactory.getAuthTokenProvider().setAuthToken(hallmarkingToken);
        final var hallmarkingsPerPositionMap = hallmarkingRestApiAdapter.getHallmarkingsPerPosition(ORDER_BUSINESS_ID);
        assertThat(hallmarkingsPerPositionMap).hasSize(1);
        final List<Hallmarking> hallmarkingsOfPosition = hallmarkingsPerPositionMap.get(POSITION_UUID);
        assertThat(hallmarkingsOfPosition).hasSize(2);
        assertThat(hallmarkingsOfPosition.getFirst().hallmarkingType()).isEqualTo(HallmarkingType.SINGLE_SWISS);
        assertThat(hallmarkingsOfPosition.getFirst().hallmarkingMethod()).isEqualTo(HallmarkingMethod.MECHANICAL);
    }
}
