package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports.csvreport;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ReportLineResponseMapperTest {

    private final ReportLineResponseMapper mapper;

    ReportLineResponseMapperTest() {
        final var messageSource = new org.springframework.context.support.ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        mapper = new ReportLineResponseMapper(messageSource);
    }

    @Test
    void whenHallmarkingLine_thenMapToReportLineString() {
        final var lineItem = new GenerateReportUseCase.ReportLineOutput(
                ControlOffice.ZEMK, GenerateReportUseCase.Section.HALLMARKING,
                GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                HallmarkingLocation.CLIENT, HallmarkingMethod.LASER,
                Metal.PLATINUM, BigDecimal.valueOf(45), 0
        );

        final var resultLine = mapper.toResponseLineString(lineItem);

        assertThat(resultLine).isEqualTo("ZEMK;Punzierung;Andere Waren;Nicht Zert. Mat;Kunde offsite;LAS;pt;45;0");

    }

    @Test
    void whenAnalysisLine_thenMapToReportLineString() {
        final var lineItem = GenerateReportUseCase.ReportLineOutput.createAnalysisCountLine(
                ControlOffice.GDPC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION,
                Metal.SILVER, BigDecimal.valueOf(693)
        );

        final var resultLine = mapper.toResponseLineString(lineItem);

        assertThat(resultLine).isEqualTo("GDPC;Analysen;Konformitätsbewertung;;;;ag;693;");

    }

    @Test
    void whenSectionRevenueAndCostHasNoDecimal_thenMapToReportLineStringAndSetCostToTwoDecimals() {
        final var lineItem = new GenerateReportUseCase.ReportLineOutput(
                ControlOffice.CDFC, GenerateReportUseCase.Section.REVENUE,
                GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, null,
                null, null,
                null, BigDecimal.valueOf(7), 0
        );

        final var resultLine = mapper.toResponseLineString(lineItem);

        assertThat(resultLine).isEqualTo("CDFC;Einnahmen;Bereich Punzierung;;;;;7.00;0");

    }

    @ParameterizedTest(name = "When countCh = {0}, then end of line should be {1}")
    @CsvSource({
            "7, 7.00",
            "7.8, 7.80",
            "7.82, 7.82",
            "0.0, 0"
    })
    void whenGivenCostThenFormatCorrectly(final double countCh, final String expectedFormattedCount) {
        final var lineItem = new GenerateReportUseCase.ReportLineOutput(
                ControlOffice.CDFC, GenerateReportUseCase.Section.REVENUE,
                GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, null,
                null, null,
                null, BigDecimal.valueOf(countCh), 0
        );

        final var resultLine = mapper.toResponseLineString(lineItem);

        final var expectedLine = "CDFC;Einnahmen;Bereich Punzierung;;;;;%s;0".formatted(expectedFormattedCount);
        assertThat(resultLine).isEqualTo(expectedLine);

    }

    @Test
    void whenSectionRevenueForAnalysis_thenMapToReportLineString() {
        final var lineItem = GenerateReportUseCase.ReportLineOutput.createRevenueLine(
                ControlOffice.NOIC, GenerateReportUseCase.OperationOrGoodsType.ANALYSIS_DEPARTMENT, BigDecimal.valueOf(0.0), 0
                                                                                           );

        final var resultLine = mapper.toResponseLineString(lineItem);

        assertThat(resultLine).isEqualTo("NOIC;Einnahmen;Bereich Analysen;;;;;0;0");

    }

}