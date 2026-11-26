package ch.admin.bazg.prezius.reporting.adapter.out.rest.datahub;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.testenvironment.ITMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_JEAPRESTCLIENT;
import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_JOB_SCHEDULERS;
import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_MESSAGESENDER;
import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.TEST;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({TEST, MOCK_MESSAGESENDER, MOCK_JEAPRESTCLIENT, MOCK_JOB_SCHEDULERS})
@SpringBootTest(classes = ITMockConfiguration.class)
@Sql(scripts = {"/cleanup.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Slf4j
class DataHubBusinessPartnerRestApiAdapterIT {

    @Autowired
    private DataHubBusinessPartnerRestApiAdapter adapterUnderTest;

    private static final String JSON_RESPONSE = """
            [
              {
                "id": "bp1",
                "businessPartnerType": "2",
                "company": {
                  "name": "BP_NAME"
                },
                "roles": [
                  {
                    "technicalId": "ZKH16",
                    "businessId": "ZKH16",
                    "emkGroup": "F"
                  }
                ]
              }
            ]
            """;

    private static MockWebServer mockDependencyService;

    @BeforeAll
    static void initialSetup() throws IOException {
        mockDependencyService = new MockWebServer();
        mockDependencyService.start(9999);
    }

    @AfterAll
    static void teardown() {
        try {
            mockDependencyService.shutdown();
        } catch (Exception ex) {
            log.error("Tear down of mock datahub server was not successful.", ex);
        }
    }

    @Test
    void getById_whenEndpointReturnsCorrectJson_thenReturnBusinessPartners() {
        mockDependencyService.enqueue(
                new MockResponse().setBody(JSON_RESPONSE).addHeader("Content-Type", "application/json"));

        final Optional<BusinessPartner> result = this.adapterUnderTest.getById("id");

        assertThat(result).isPresent();
    }

    @Test
    void getById_whenEndpointReturnsWrongRole_thenReturnEmptyOptional() {
        final String modifiedJson = JSON_RESPONSE.replaceAll("ZKH16", "WrongRole");

        mockDependencyService.enqueue(
                new MockResponse().setBody(modifiedJson).addHeader("Content-Type", "application/json"));

        final Optional<BusinessPartner> result = this.adapterUnderTest.getById("id");

        assertThat(result).isEmpty();
    }

    @Test
    void getById_whenEndpointReturnsNoContent_thenReturnEmptyOptional() {
        mockDependencyService.enqueue(
                new MockResponse().setBody("")
                                  .setResponseCode(204));

        final Optional<BusinessPartner> result = this.adapterUnderTest.getById("id");

        assertThat(result).isEmpty();
    }

    @Test
    void getById_whenEndpointReturnsEmptyBody_thenReturnEmptyOptional() {
        mockDependencyService.enqueue(
                new MockResponse().setBody("").addHeader("Content-Type", "application/json")
                                  .setResponseCode(200));

        final Optional<BusinessPartner> result = this.adapterUnderTest.getById("id");

        assertThat(result).isEmpty();
    }

    @Test
    void getById_whenEndpointReturnsEmptyList_thenReturnEmptyOptional() {
        mockDependencyService.enqueue(
                new MockResponse().setBody("").addHeader("Content-Type", "application/json")
                                  .setResponseCode(200));

        final Optional<BusinessPartner> result = this.adapterUnderTest.getById("id");

        assertThat(result).isEmpty();
    }

    @Test
    void getAll_whenEndpointReturnsCorrectJson_thenReturnBusinessPartners() {
        mockDependencyService.enqueue(
                new MockResponse().setBody(JSON_RESPONSE).addHeader("Content-Type", "application/json"));

        final List<BusinessPartner> result = this.adapterUnderTest.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo("bp1");
    }

    @Test
    void getAll_whenEndpointReturnsNoContent_thenReturnEmptyOptional() {
        mockDependencyService.enqueue(
                new MockResponse().setBody("")
                                  .setResponseCode(204));

        final List<BusinessPartner> result = this.adapterUnderTest.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getAll_whenEndpointReturnsEmptyBody_thenReturnEmptyOptional() {
        mockDependencyService.enqueue(
                new MockResponse().setBody("").addHeader("Content-Type", "application/json")
                                  .setResponseCode(200));

        final List<BusinessPartner> result = this.adapterUnderTest.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getAll_whenEndpointReturnsEmptyList_thenReturnEmptyOptional() {
        mockDependencyService.enqueue(
                new MockResponse().setBody("").addHeader("Content-Type", "application/json")
                                  .setResponseCode(200));

        final List<BusinessPartner> result = this.adapterUnderTest.getAll();

        assertThat(result).isEmpty();
    }

}
