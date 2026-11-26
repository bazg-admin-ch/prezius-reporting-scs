package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetAnalysisReportUseCase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.HashMap;

import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.CONFORMITY_EVALUATION;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.DETERMINATION_ON_MELTING_POINT;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.DETERMINATION_ON_SAMPLES;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.SAMPLES_FROM_HALLMARKING;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.SAMPLES_FROM_OTHER_PROCESSES;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {AnalysisReportDtoMapperImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AnalysisReportDtoMapperTest {

    private final AnalysisReportDtoMapper mapperUnderTest;

    @Test
    void toDto_whenReportPassed_thenMappedCorrectly() {
        final var analysisTypeReports = new HashMap<AnalysisType, GetAnalysisReportUseCase.Output.ReportCategoryOutput>();
        analysisTypeReports.put(CONFORMITY_EVALUATION, GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                                           .ag(1100)
                                                                                                           .au(1200)
                                                                                                           .pd(120)
                                                                                                           .pt(130)
                                                                                                           .total(2550)
                                                                                                           .totalCostsWithVat(new BigDecimal("42079"))
                                                                                                           .build());
        analysisTypeReports.put(DETERMINATION_ON_SAMPLES, GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                                              .ag(100)
                                                                                                              .au(200)
                                                                                                              .pd(20)
                                                                                                              .pt(30)
                                                                                                              .total(350)
                                                                                                              .totalCostsWithVat(new BigDecimal("42079"))
                                                                                                              .build());
        analysisTypeReports.put(DETERMINATION_ON_MELTING_POINT, GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                                                    .ag(200)
                                                                                                                    .au(300)
                                                                                                                    .pd(120)
                                                                                                                    .pt(130)
                                                                                                                    .total(750)
                                                                                                                    .totalCostsWithVat(new BigDecimal("42079"))
                                                                                                                    .build());
        analysisTypeReports.put(SAMPLES_FROM_HALLMARKING, GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                                              .ag(101)
                                                                                                              .au(201)
                                                                                                              .pd(21)
                                                                                                              .pt(31)
                                                                                                              .total(354)
                                                                                                              .totalCostsWithVat(new BigDecimal("42079"))
                                                                                                              .build());
        analysisTypeReports.put(SAMPLES_FROM_OTHER_PROCESSES, GetAnalysisReportUseCase.Output.ReportCategoryOutput.builder()
                                                                                                                  .ag(100)
                                                                                                                  .au(220)
                                                                                                                  .pd(20)
                                                                                                                  .pt(30)
                                                                                                                  .total(370)
                                                                                                                  .totalCostsWithVat(new BigDecimal("42079"))
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

        final AnalysisReportDto mappedDto = mapperUnderTest.toDto(analysisReport);

        final var expectedDto = AnalysisReportDto.builder()
                                                 .conformityEvaluation(AnalysisReportCategoryDto.builder()
                                                                                                .ag(1100)
                                                                                                .au(1200)
                                                                                                .pd(120)
                                                                                                .pt(130)
                                                                                                .total(2550)
                                                                                                .totalCostsWithVat(new BigDecimal("42079"))
                                                                                                .build())
                                                 .determinationOnSamples(AnalysisReportCategoryDto.builder()
                                                                                                  .ag(100)
                                                                                                  .au(200)
                                                                                                  .pd(20)
                                                                                                  .pt(30)
                                                                                                  .total(350)
                                                                                                  .totalCostsWithVat(new BigDecimal("42079"))
                                                                                                  .build())
                                                 .determinationOnMeltingPoint(AnalysisReportCategoryDto.builder()
                                                                                                       .ag(200)
                                                                                                       .au(300)
                                                                                                       .pd(120)
                                                                                                       .pt(130)
                                                                                                       .total(750)
                                                                                                       .totalCostsWithVat(new BigDecimal("42079"))
                                                                                                       .build())
                                                 .samplesFromHallmarking(AnalysisReportCategoryDto.builder()
                                                                                                  .ag(101)
                                                                                                  .au(201)
                                                                                                  .pd(21)
                                                                                                  .pt(31)
                                                                                                  .total(354)
                                                                                                  .totalCostsWithVat(new BigDecimal("42079"))
                                                                                                  .build())
                                                 .samplesFromOtherProcesses(AnalysisReportCategoryDto.builder()
                                                                                                     .ag(100)
                                                                                                     .au(220)
                                                                                                     .pd(20)
                                                                                                     .pt(30)
                                                                                                     .total(370)
                                                                                                     .totalCostsWithVat(new BigDecimal("42079"))
                                                                                                     .build())
                                                 .total(AnalysisReportCategoryDto.builder()
                                                                                 .ag(100)
                                                                                 .au(200)
                                                                                 .pt(300)
                                                                                 .pd(400)
                                                                                 .totalCostsWithVat(new BigDecimal("15000"))
                                                                                 .build())
                                                 .build();
        assertThat(mappedDto).isEqualTo(expectedDto);
    }

}
