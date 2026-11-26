package ch.admin.bazg.prezius.reporting.application.implementation.getanalysisreport;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetAnalysisReportUseCase;
import ch.admin.bazg.prezius.reporting.application.ports.out.SearchAnalysisReportItemsPort;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisFilterCriteria;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.AnalysisReportItemGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAnalysisReportHandlerTest {

    @InjectMocks
    private GetAnalysisReportHandler handlerUnderTest;

    @Mock
    private SearchAnalysisReportItemsPort searchAnalysisReportItemsPort;
    private final AnalysisReportItemGenerator analysisReportItemGenerator = new AnalysisReportItemGenerator();


    @Test
    void getAnalysisReport_whenFiltersDefined_thenPersistencePortCalledCorrectly() {
        final var getAnalysisReportInput = GetAnalysisReportUseCase.Input.builder()
                                                                         .officeFilter(List.of(ControlOffice.BASC))
                                                                         .businessPartnerFilter(List.of("b33zn3ssp4rtn3r"))
                                                                         .dateBeforeFilter(LocalDateTime.of(2025, 2, 26, 10, 40))
                                                                         .dateAfterFilter(LocalDateTime.of(2024, 2, 26, 10, 40))
                                                                         .build();

        handlerUnderTest.getAnalysisReport(getAnalysisReportInput);

        final var expectedPersistencePortInput = AnalysisFilterCriteria.builder()
                                                                       .officeFilter(List.of(ControlOffice.BASC))
                                                                       .businessPartnerFilter(List.of("b33zn3ssp4rtn3r"))
                                                                       .dateBeforeFilter(LocalDateTime.of(2025, 2, 26, 10, 40))
                                                                       .dateAfterFilter(LocalDateTime.of(2024, 2, 26, 10, 40))
                                                                       .build();
        verify(searchAnalysisReportItemsPort).search(expectedPersistencePortInput);
    }

    @Test
    void getAnalysisReport_whenReportItemsFound_thenResultAggregatedCorrectly() {
        final var getAnalysisReportInput = GetAnalysisReportUseCase.Input.builder().build();
        final List<AnalysisReportItem> mockedReportItems = new ArrayList<>();
        mockedReportItems.add(
                analysisReportItemGenerator.generate(r -> r.analysisType(AnalysisType.DETERMINATION_ON_MELTING_POINT)
                                                           .au(100)
                                                           .ag(0)
                                                           .pt(0)
                                                           .pd(0)
                                                           .cost(new BigDecimal("420"))
                                                    )
                             );
        mockedReportItems.add(
                analysisReportItemGenerator.generate(r -> r.analysisType(AnalysisType.DETERMINATION_ON_SAMPLES)
                                                           .au(0)
                                                           .ag(220)
                                                           .pt(100)
                                                           .pd(0)
                                                           .cost(new BigDecimal("810"))
                                                    )
                             );
        mockedReportItems.add(
                analysisReportItemGenerator.generate(r -> r.analysisType(AnalysisType.CONFORMITY_EVALUATION)
                                                           .au(100)
                                                           .ag(300)
                                                           .pt(0)
                                                           .pd(0)
                                                           .cost(new BigDecimal("1690"))
                                                    )
                             );
        mockedReportItems.add(
                analysisReportItemGenerator.generate(r -> r.analysisType(AnalysisType.CONFORMITY_EVALUATION)
                                                           .au(300)
                                                           .ag(0)
                                                           .pt(200)
                                                           .pd(0)
                                                           .cost(new BigDecimal("3000"))
                                                    )
                             );
        when(searchAnalysisReportItemsPort.search(any())).thenReturn(mockedReportItems);

        final GetAnalysisReportUseCase.Output analysisReportOutput = handlerUnderTest.getAnalysisReport(getAnalysisReportInput);

        final Map<AnalysisType, GetAnalysisReportUseCase.Output.ReportCategoryOutput> expectedAnalysisTypeReports = new HashMap<>();
        expectedAnalysisTypeReports.put(AnalysisType.DETERMINATION_ON_MELTING_POINT,
                                        GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                            .au(100)
                                                                                            .total(100)
                                                                                            .totalCostsWithVat(new BigDecimal("420"))
                                                                                            .build());
        expectedAnalysisTypeReports.put(AnalysisType.DETERMINATION_ON_SAMPLES,
                                        GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                            .ag(220)
                                                                                            .pt(100)
                                                                                            .total(320)
                                                                                            .totalCostsWithVat(new BigDecimal("810"))
                                                                                            .build());
        expectedAnalysisTypeReports.put(AnalysisType.CONFORMITY_EVALUATION,
                                        GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                            .au(400)
                                                                                            .ag(300)
                                                                                            .pt(200)
                                                                                            .total(900)
                                                                                            .totalCostsWithVat(new BigDecimal("4690"))
                                                                                            .build());
        final var expectedAnalysisReportOutput = GetAnalysisReportUseCase.Output.builder()
                                                                                .analysisTypeReports(expectedAnalysisTypeReports)
                                                                                .totalReport(GetAnalysisReportUseCase.Output.ReportCategoryOutput
                                                                                                     .builder()
                                                                                                     .au(500)
                                                                                                     .ag(520)
                                                                                                     .pd(0)
                                                                                                     .pt(300)
                                                                                                     .total(1320)
                                                                                                     .totalCostsWithVat(new BigDecimal("5920"))
                                                                                                     .build())
                                                                                .build();
        assertThat(analysisReportOutput).isEqualTo(expectedAnalysisReportOutput);

    }


}
