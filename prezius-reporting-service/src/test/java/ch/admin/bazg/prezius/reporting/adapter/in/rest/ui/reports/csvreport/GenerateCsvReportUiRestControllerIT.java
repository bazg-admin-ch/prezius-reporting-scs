package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports.csvreport;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import ch.admin.bit.jeap.security.resource.semanticAuthentication.SemanticApplicationRole;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationContext;
import ch.admin.bit.jeap.security.test.jws.JwsBuilderFactory;
import ch.admin.bit.jeap.security.test.resource.configuration.JeapOAuth2IntegrationTestResourceConfiguration;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bit.jeap.security.test.jws.JwsBuilder.DEFAULT_ISSUER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
                properties = {
                        "server.port=8888",
                        "jeap.security.oauth2.resourceserver.authorization-server.issuer=" + DEFAULT_ISSUER,
                        "jeap.security.oauth2.resourceserver.authorization-server.jwk-set-uri=http://localhost:${server.port}/prezius-reporting/.well-known/jwks.json"
                })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(JeapOAuth2IntegrationTestResourceConfiguration.class)
class GenerateCsvReportUiRestControllerIT extends ITBase {

    private OkHttpClient httpClient;

    private final JwsBuilderFactory jwsBuilderFactory;
    private static final String API_PATH = "/prezius-reporting/api/ui/reports/export";

    @MockitoBean
    private GenerateReportUseCase generateReportUseCase;

    @BeforeEach
    void setUp() {
        httpClient = new OkHttpClient();
    }

    @Test
    void whenRightRoleAndWithFilter_thenReturnStatusOk() throws IOException {
        when(generateReportUseCase.generate(any())).thenReturn(Flux.just(
                new GenerateReportUseCase.ReportLineOutput(
                        ControlOffice.ZEMK, GenerateReportUseCase.Section.HALLMARKING,
                        GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                        HallmarkingLocation.CLIENT, HallmarkingMethod.LASER,
                        Metal.PLATINUM, BigDecimal.valueOf(45), 0)
                                                                        ));

        final HttpUrl url = HttpUrl.parse("http://localhost:8888" + API_PATH).newBuilder()
                .addQueryParameter("filter", "officeName$ZEMK§BIEC|date$2025-04-01§2025-04-20|businessPartner$1000139473|hallmarkingType$SINGLE_SWISS")
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + createAuthToken("read"))
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body()).isNotNull();

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
            final List<String> lines = reader.lines().collect(Collectors.toList());

            assertThat(lines)
                    .hasSize(2)
                    .contains(
                            headerLineWithLeadingUtf8Bytes(),
                            "ZEMK;Punzierung;Andere Waren;Nicht Zert. Mat;Kunde offsite;LAS;pt;45;0");
        }

        final var expectedFilter = HallmarkingFilterCriteria.builder()
                                                            .officeFilter(List.of(ControlOffice.ZEMK, ControlOffice.BIEC))
                                                            .dateAfterFilter(LocalDate.of(2025, 4, 1).atStartOfDay())
                                                            .dateBeforeFilter(LocalDate.of(2025, 4, 20).atTime(LocalTime.MAX))
                                                            .hallmarkingTypes(List.of(HallmarkingType.SINGLE_SWISS))
                                                            .businessPartnerFilter(List.of("1000139473"))
                                                            .build();
        verify(generateReportUseCase).generate(expectedFilter);
    }

    @Test
    void whenRightRoleAndWithoutFilterAndSearchString_thenReturnStatusOk() throws IOException {
        when(generateReportUseCase.generate(any())).thenReturn(Flux.just(
                new GenerateReportUseCase.ReportLineOutput(
                        ControlOffice.ZEMK, GenerateReportUseCase.Section.HALLMARKING,
                        GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                        HallmarkingLocation.CLIENT, HallmarkingMethod.LASER,
                        Metal.PLATINUM, BigDecimal.valueOf(45), 0)
                                                                        ));

        final Request request = new Request.Builder()
                .url("http://localhost:8888" + API_PATH)
                .addHeader("Authorization", "Bearer " + createAuthToken("read"))
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.body()).isNotNull();

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
            final List<String> lines = reader.lines().collect(Collectors.toList());

            assertThat(lines)
                    .hasSize(2)
                    .contains(
                            headerLineWithLeadingUtf8Bytes(),
                            "ZEMK;Punzierung;Andere Waren;Nicht Zert. Mat;Kunde offsite;LAS;pt;45;0");
        }

        final var expectedFilter = HallmarkingFilterCriteria.builder().officeFilter(Arrays.asList(ControlOffice.values())).build();
        verify(generateReportUseCase).generate(expectedFilter);
    }

    @Test
    void whenCalledWithoutRole_thenReturnStatusForbidden() throws IOException {
        final HttpUrl url = HttpUrl.parse("http://localhost:8888" + API_PATH).newBuilder()
                .addQueryParameter("filter", "officeName$ZEMK§BIEC")
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + createAuthToken("whatever"))
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            assertThat(response.code()).isEqualTo(403);
        }


        verify(generateReportUseCase, never()).generate(any());
    }

    private String createAuthToken(final String operation) {
        return jwsBuilderFactory.createValidForFixedLongPeriodBuilder("user", JeapAuthenticationContext.USER)
                                .withUserRoles(SemanticApplicationRole.builder()
                                                                      .system("prezius")
                                                                      .resource("reporting")
                                                                      .operation(operation)
                                                                      .build())
                                .build().serialize();
    }

    private String headerLineWithLeadingUtf8Bytes() {
        final String headerLine = "Office;Rubrique;Operation / Warenkategorie;Zertifizierung;Punzierung durch / Standort;Methode;Metall;Anzahl CH;Anzahl HMC";
        final byte[] utf8BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        return new String(utf8BOM, StandardCharsets.UTF_8).concat(headerLine);
    }
}
