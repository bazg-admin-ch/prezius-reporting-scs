package ch.admin.bazg.prezius.reporting.adapter.out.rest.datahub;

import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import ch.admin.bit.jeap.messaging.transactionaloutbox.messaging.OutboxMessagingConfig;
import ch.admin.bit.jeap.messaging.transactionaloutbox.outbox.OutboxConfig;
import ch.admin.bit.jeap.messaging.transactionaloutbox.scheduling.OutboxSchedulingConfig;
import ch.admin.bit.jeap.messaging.transactionaloutbox.transaction.OutboxTransactionConfig;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationContext;
import ch.admin.bit.jeap.security.test.client.MockJeapOAuth2RestClientBuilderFactory;
import ch.admin.bit.jeap.security.test.client.configuration.JeapOAuth2IntegrationTestClientConfiguration;
import ch.admin.bit.jeap.security.test.jws.JwsBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.TEST;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test class specifies this consumer's pact (contracts) for its provider and checks that the consumer can interact successfully with a provider that complies to that pact
 * (consumer tests).
 */

@SpringBootTest
@ActiveProfiles({TEST})
@ExtendWith(PactConsumerTestExt.class)
@MockServerConfig(hostInterface = "localhost", port = "9999")
@EnableAutoConfiguration(exclude = {
        OutboxSchedulingConfig.class,
        OutboxTransactionConfig.class,
        OutboxMessagingConfig.class,
        OutboxConfig.class
})
@PactTestFor(pactVersion = PactSpecVersion.V3)
@Import({JeapOAuth2IntegrationTestClientConfiguration.class})
@SuppressWarnings({"ConstantConditions", "SpringJavaInjectionPointsAutowiringInspection"}) final class DatahubBusinessPartnerPactConsumerTest {

    private static final String CONSUMER = "bazg-prezius-reporting-scs";
    private static final String PROVIDER = "bazg-datahub-businesspartner-service";
    private static final String API_PATH = "/api/v4/businesspartner";
    private final String bpPersonId = "1000037814";
    private final String bpCompanyId = "1000037840";
    
    @Autowired
    private MockJeapOAuth2RestClientBuilderFactory mockRestClientBuilderFactory;
    @Autowired
    private DataHubBusinessPartnerRestApiAdapter dataHubBusinessPartnerRestApiAdapter;
    private String datahubToken;

    @BeforeEach
    void init(@Autowired final JwsBuilderFactory jwsBuilderFactory) {
        // We prepare access tokens with different authorizations to be used in the consumer tests
        datahubToken = jwsBuilderFactory
                .createValidForFixedLongPeriodBuilder("prezius-client", JeapAuthenticationContext.SYS)
                .withUserRoles("DATAHUB_BUSINESSPARTNER_READ")
                .build()
                .serialize();
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    @SuppressWarnings("unused")
    private RequestResponsePact getBpByIdForPersonType(final PactDslWithProvider builder) {
        final String path = API_PATH + "/";
        return builder.
                given("the person businesspartner 'John Doe' with bpId ${businesspartner-id} exists", "businesspartner-id", bpPersonId).
                uponReceiving("A POST request to " + path).
                path(path).
                query("withRoles=true").
                method("POST").
                body(
                        new PactDslJsonArray()
                                .stringValue(bpPersonId)
                                .asArray()
                    ).
                headers(HttpHeaders.AUTHORIZATION, "Bearer " + datahubToken).
                headers("Content-Type", "application/json").
                willRespondWith().
                status(200).
                matchHeader("Content-Type", "application/json", "application/json").
                body(
                        new PactDslJsonArray()
                                .object()
                                .stringType("businessPartnerType", "1")
                                .stringType("id", bpPersonId)
                                .object("person")
                                .stringType("firstName", "John")
                                .stringType("lastName", "Doe")
                                .closeObject()
                                .closeObject()
                                .asArray()
                    ).toPact();
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    @SuppressWarnings("unused")
    private RequestResponsePact getBpByIdForCompanyType(final PactDslWithProvider builder) {
        final String path = API_PATH + "/";
        return builder.
                given("the company businesspartner 'DemoCompany' with bpId ${businesspartner-id} exists", "businesspartner-id", bpCompanyId).
                uponReceiving("A POST request to " + path).
                path(path).
                query("withRoles=true").
                method("POST").
                body(
                        new PactDslJsonArray()
                                .stringType(bpCompanyId)
                                .asArray()
                    ).
                headers(HttpHeaders.AUTHORIZATION, "Bearer " + datahubToken).
                headers("Content-Type", "application/json").
                willRespondWith().
                status(200).
                matchHeader("Content-Type", "application/json", "application/json").
                body(
                        new PactDslJsonArray()
                                .object()
                                .stringType("businessPartnerType", "2")
                                .stringType("id", bpCompanyId)
                                .object("company")
                                .stringType("name", "DemoCompany")
                                .closeObject()
                                .closeObject()
                                .asArray()
                    ).toPact();
    }

    @Pact(provider = PROVIDER, consumer = CONSUMER)
    @SuppressWarnings("unused")
    private RequestResponsePact getAllWithRole(final PactDslWithProvider builder) {
        final String path = API_PATH + "/with-role/ZKH16";
        return builder.
                given("the list of only one company businesspartner with role ZKH16").
                uponReceiving("A GET request to " + path).
                path(path).
                method("GET").
                headers(HttpHeaders.AUTHORIZATION, "Bearer " + datahubToken).
                willRespondWith().
                status(200).
                matchHeader("Content-Type", "application/json", "application/json").
                body(
                        new PactDslJsonArray()
                                .object()
                                .stringType("businessPartnerType", "2")
                                .stringType("id", bpCompanyId)
                                .object("company")
                                .stringType("name", "DemoCompany")
                                .closeObject()
                                .array("roles")
                                .object()
                                .stringType("technicalId", "ZKH16")
                                .stringType("businessId", "ZKH16")
                                .stringType("emkGroup", "B")
                                .closeObject()
                                .closeArray()
                                .closeObject()
                                .asArray()
                    ).toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getBpByIdForPersonType")
    void testBusinessPartnerForPersonType() {
        mockRestClientBuilderFactory.getAuthTokenProvider().setAuthToken(datahubToken);
        assertThat(dataHubBusinessPartnerRestApiAdapter.getById(bpPersonId)).isEmpty();
    }

    @Test
    @PactTestFor(pactMethod = "getBpByIdForCompanyType")
    void testBusinessPartnerForCompanyType() {
        mockRestClientBuilderFactory.getAuthTokenProvider().setAuthToken(datahubToken);
        assertThat(dataHubBusinessPartnerRestApiAdapter.getById(bpCompanyId)).isEmpty();
    }

    @Test
    @PactTestFor(pactMethod = "getAllWithRole")
    void testBusinessPartnerWithRole() {
        mockRestClientBuilderFactory.getAuthTokenProvider().setAuthToken(datahubToken);
        assertThat(dataHubBusinessPartnerRestApiAdapter.getAll()).isNotEmpty();
    }
}
