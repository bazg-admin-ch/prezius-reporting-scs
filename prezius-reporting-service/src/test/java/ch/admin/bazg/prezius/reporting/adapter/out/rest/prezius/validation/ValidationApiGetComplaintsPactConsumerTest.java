package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.validation;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import ch.admin.bazg.prezius.reporting.cdc.PactConsumerBaseTest;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Complaint;
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

import java.util.Map;
import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.assertj.core.api.Assertions.assertThat;

@PactTestFor(pactVersion = PactSpecVersion.V4, providerName = "bazg-prezius-validation-scs")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ValidationApiGetComplaintsPactConsumerTest extends PactConsumerBaseTest {

    private final MockJeapOAuth2RestClientBuilderFactory restClientBuilderFactory;
    private final ComplaintRestApiAdapter complaintRestApiAdapter;

    private static final String PROVIDER = "bazg-prezius-validation-scs";
    private static final String API_PATH = "/prezius-validation/api/public/orders/v2/%s/complaints";
    private static final UUID ORDER_ID = UUID.fromString("45e23c27-3b64-4baa-8b5b-00273adf45c4");
    private static final UUID POSITION_UUID = UUID.fromString("f73a4b96-e131-4762-9e4b-1184dd8dfb14");
    private String complaintToken;

    @BeforeEach
    void init(@Autowired final JwsBuilderFactory jwsBuilderFactory) {
        final SemanticApplicationRole semanticComplaintApplicationRole = SemanticApplicationRole.builder()
                                                                                                .system("prezius")
                                                                                                .resource("complaint")
                                                                                                .operation("read")
                                                                                                .build();

        complaintToken = jwsBuilderFactory.createValidForFixedLongPeriodBuilder("prezius-client", JeapAuthenticationContext.SYS)
                                          .withUserRoles(semanticComplaintApplicationRole)
                                          .build().serialize();
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    @SuppressWarnings("unused")
    private V4Pact getComplaintsByOrderId(final PactBuilder builder) {
        final String path = API_PATH.formatted(ORDER_ID);
        return builder
                .given( "A complaint exists for order with id orderBusinessId")
                .expectsToReceiveHttpInteraction("A GET request to " + path, httpInteractionBuilder -> httpInteractionBuilder
                        .withRequest(httpRequestBuilder -> httpRequestBuilder
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + complaintToken)
                                .method(HttpMethod.GET.name())
                                .path(path))
                        .willRespondWith(httpResponseBuilder -> httpResponseBuilder
                                .status(200)
                                .header("content-type", "application/json")
                                .body(newJsonBody(body -> body
                                                          .eachKeyLike(POSITION_UUID.toString(), complaint -> complaint
                                                                  .integerType("contestedPieceCount", 22)
                                                                  .booleanType("immediateCompliance", true))
                                                 ).build()
                                     )))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getComplaintsByOrderId")
    void testGetComplaintsByOrderId() {
        restClientBuilderFactory.getAuthTokenProvider().setAuthToken(complaintToken);
        final Map<UUID, Complaint> complaintMap = complaintRestApiAdapter.getComplaintsPerPosition(ORDER_ID);
        assertThat(complaintMap).hasSize(1);
        assertThat(complaintMap.values()).satisfiesExactly(complaint -> {
            assertThat(complaint.contestedPieceCount()).isPositive();
            assertThat(complaint.immediateCompliance()).isNotNull();
        });
    }
}
