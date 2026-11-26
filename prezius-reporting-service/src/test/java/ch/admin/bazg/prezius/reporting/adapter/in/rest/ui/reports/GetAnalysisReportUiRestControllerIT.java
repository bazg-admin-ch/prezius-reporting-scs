package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.reporting.adapter.in.rest.TestFileUtils;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetAnalysisReportUseCase;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import ch.admin.bit.jeap.security.resource.semanticAuthentication.SemanticApplicationRole;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationToken;
import ch.admin.bit.jeap.security.test.resource.JeapAuthenticationTestTokenBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.CONFORMITY_EVALUATION;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.DETERMINATION_ON_MELTING_POINT;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.DETERMINATION_ON_SAMPLES;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class GetAnalysisReportUiRestControllerIT extends ITBase {

    private static final SemanticApplicationRole REPORTING_READ_ROLE = SemanticApplicationRole.builder()
                                                                                              .system("prezius")
                                                                                              .resource("reporting")
                                                                                              .operation("read")
                                                                                              .build();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAnalysisReportUseCase getAnalysisReportUseCase;

    @Test
    void get_whenNoFilter_returnCorrectReport() {
        final var analysisTypeReports = new HashMap<AnalysisType, GetAnalysisReportUseCase.Output.ReportCategoryOutput>();
        analysisTypeReports.put(CONFORMITY_EVALUATION, GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                                           .ag(1100)
                                                                                                           .au(1200)
                                                                                                           .pd(120)
                                                                                                           .pt(130)
                                                                                                           .total(2550)
                                                                                                           .totalCostsWithVat(new BigDecimal("5000"))
                                                                                                           .build());
        analysisTypeReports.put(DETERMINATION_ON_SAMPLES, GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                                              .ag(100)
                                                                                                              .au(200)
                                                                                                              .pd(20)
                                                                                                              .pt(30)
                                                                                                              .total(350)
                                                                                                              .totalCostsWithVat(new BigDecimal("5000"))
                                                                                                              .build());
        analysisTypeReports.put(DETERMINATION_ON_MELTING_POINT, GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                                                    .ag(200)
                                                                                                                    .au(300)
                                                                                                                    .pd(120)
                                                                                                                    .pt(130)
                                                                                                                    .total(750)
                                                                                                                    .totalCostsWithVat(new BigDecimal("5000"))
                                                                                                                    .build());
        final var analysisReport = GetAnalysisReportUseCase.Output.builder()
                                                                  .analysisTypeReports(analysisTypeReports)
                                                                  .totalReport(GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                                                                   .ag(100)
                                                                                                                                   .au(200)
                                                                                                                                   .pt(300)
                                                                                                                                   .pd(400)
                                                                                                                                   .totalCostsWithVat(new BigDecimal("15000"))
                                                                                                                                   .build())
                                                                  .build();
        when(getAnalysisReportUseCase.getAnalysisReport(any())).thenReturn(analysisReport);
        final JeapAuthenticationToken authentication = createAuthenticationWithReportingReadRole();
        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get("/api/ui/analysis-reports").with(authentication(authentication)))
                        .andExpect(status().isOk())
                        .andExpect(content().json(TestFileUtils.readResponseBody("get-analysis-report.json")));
        });
    }

    @Test
    void get_whenHallmarkingTypeFilterIsSet_thenReturnBadRequest() {
        final JeapAuthenticationToken authentication = createAuthenticationWithReportingReadRole();
        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get("/api/ui/analysis-reports").with(authentication(authentication))
                            .param("filter", "hallmarkingType$SINGLE_SWISS§SINGLE_INTERNATIONAL"))
                    .andExpect(status().isBadRequest());
        });

    }

    @Test
    void get_whenFiltersSet_thenUseCaseCalledWithParsedFilters() {
        final JeapAuthenticationToken authentication = createAuthenticationWithReportingReadRole();
        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get("/api/ui/analysis-reports").with(authentication(authentication))
                                                                .param("filter", "officeName$NOIC§CFEC|date$!§2025-02-21|businessPartner$1000143862"))
                        .andExpect(status().isOk());
        });
        final var expectedUseCaseInput = GetAnalysisReportUseCase.Input.builder()
                .businessPartnerFilter(List.of("1000143862"))
                .dateAfterFilter(null)
                .dateBeforeFilter(LocalDate.of(2025, 2, 21).atTime(LocalTime.MAX))
                .officeFilter(List.of(ControlOffice.NOIC, ControlOffice.CFEC))
                .build();
        verify(getAnalysisReportUseCase).getAnalysisReport(expectedUseCaseInput);
    }

    private static JeapAuthenticationToken createAuthenticationWithReportingReadRole() {
        return JeapAuthenticationTestTokenBuilder.create().withUserRoles(GetAnalysisReportUiRestControllerIT.REPORTING_READ_ROLE).build();
    }
}
