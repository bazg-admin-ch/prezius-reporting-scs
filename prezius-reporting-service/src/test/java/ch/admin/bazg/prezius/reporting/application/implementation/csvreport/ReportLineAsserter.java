package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class ReportLineAsserter {

    private final List<Integer> linesWithNoZeroCount = new ArrayList<>();

    static ReportLineAsserter init() {
        return new ReportLineAsserter();
    }

    void asssertHallmarkingCountLinesForEachMetal(final List<GenerateReportUseCase.ReportLineOutput> reportLines,
                                                  final int reportLineStartIndex, final ControlOffice expectedOffice,
                                                   final GenerateReportUseCase.OperationOrGoodsType expectedGoodsType, final GenerateReportUseCase.Certification expectedCertification,
                                                   final HallmarkingLocation expectedHallmarkingLocation, final HallmarkingMethod expectedHallmarkingMethod,
                                                   final int expectedGoldChCount, final int expectedGoldHmcCount,
                                                  final int expectedSilverChCount, final int expectedSilverHmcCount,
                                                  final int expectedPalladiumChCount, final int expectedPalladiumHmcCount,
                                                  final int expectedPlatinumChCount, final int expectedPlatinumHmcCount
                                                  ) {
        assertHallmarkingCountLine(reportLines, reportLineStartIndex, expectedOffice, expectedGoodsType, expectedCertification,
                                   expectedHallmarkingLocation, expectedHallmarkingMethod, Metal.GOLD, expectedGoldChCount, expectedGoldHmcCount);
        assertHallmarkingCountLine(reportLines, reportLineStartIndex + 1, expectedOffice, expectedGoodsType, expectedCertification,
                                   expectedHallmarkingLocation, expectedHallmarkingMethod, Metal.SILVER, expectedSilverChCount, expectedSilverHmcCount);
        assertHallmarkingCountLine(reportLines, reportLineStartIndex + 2, expectedOffice, expectedGoodsType, expectedCertification,
                                   expectedHallmarkingLocation, expectedHallmarkingMethod, Metal.PALLADIUM, expectedPalladiumChCount, expectedPalladiumHmcCount);
        assertHallmarkingCountLine(reportLines, reportLineStartIndex + 3, expectedOffice, expectedGoodsType, expectedCertification,
                                   expectedHallmarkingLocation, expectedHallmarkingMethod, Metal.PLATINUM, expectedPlatinumChCount, expectedPlatinumHmcCount);
    }

    void assertHallmarkingCountLine(
            final List<GenerateReportUseCase.ReportLineOutput> reportLines,
            final int reportLineIndex, final ControlOffice expectedOffice,
            final GenerateReportUseCase.OperationOrGoodsType expectedGoodsType, final GenerateReportUseCase.Certification expectedCertification,
            final HallmarkingLocation expectedHallmarkingLocation, final HallmarkingMethod expectedHallmarkingMethod,
            final Metal expectedMetal, final int expectedChCount, final int expectedHmcCount
                                   ) {
        this.linesWithNoZeroCount.add(reportLineIndex);
        final var reportLine = reportLines.get(reportLineIndex);
        assertThat(reportLine.controlOffice()).isEqualTo(expectedOffice);
        assertThat(reportLine.section()).isEqualTo(GenerateReportUseCase.Section.HALLMARKING);
        assertThat(reportLine.operationOrGoodsType()).isEqualTo(expectedGoodsType);
        assertThat(reportLine.certification()).isEqualTo(expectedCertification);
        assertThat(reportLine.hallmarkingLocation()).isEqualTo(expectedHallmarkingLocation);
        assertThat(reportLine.hallmarkingMethod()).isEqualTo(expectedHallmarkingMethod);
        assertThat(reportLine.metal()).isEqualTo(expectedMetal);
        assertThat(reportLine.countCh()).isEqualTo(BigDecimal.valueOf(expectedChCount));
        assertThat(reportLine.countHmc()).isEqualTo(expectedHmcCount);
    }

    void assertAnalysisCountLine(
            final List<GenerateReportUseCase.ReportLineOutput> reportLines,
            final int reportLineIndex, final ControlOffice expectedOffice,
            final GenerateReportUseCase.OperationOrGoodsType expectedOperationType,
            final Metal expectedMetal, final int expectedChCount
                                   ) {
        this.linesWithNoZeroCount.add(reportLineIndex);
        final var reportLine = reportLines.get(reportLineIndex);
        assertThat(reportLine.controlOffice()).isEqualTo(expectedOffice);
        assertThat(reportLine.section()).isEqualTo(GenerateReportUseCase.Section.ANALYSIS);
        assertThat(reportLine.operationOrGoodsType()).isEqualTo(expectedOperationType);
        assertThat(reportLine.certification()).isNull();
        assertThat(reportLine.hallmarkingLocation()).isNull();
        assertThat(reportLine.hallmarkingMethod()).isNull();
        assertThat(reportLine.metal()).isEqualTo(expectedMetal);
        assertThat(reportLine.countCh()).isEqualTo(BigDecimal.valueOf(expectedChCount));
        assertThat(reportLine.countHmc()).isNull();
    }

    void assertRevenueLine(
            final List<GenerateReportUseCase.ReportLineOutput> reportLines,
            final int reportLineIndex, final ControlOffice expectedOffice,
            final GenerateReportUseCase.OperationOrGoodsType expectedDepartment,
            final double expectedCosts) {
        this.linesWithNoZeroCount.add(reportLineIndex);
        final var reportLine = reportLines.get(reportLineIndex);
        assertThat(reportLine.controlOffice()).isEqualTo(expectedOffice);
        assertThat(reportLine.section()).isEqualTo(GenerateReportUseCase.Section.REVENUE);
        assertThat(reportLine.operationOrGoodsType()).isEqualTo(expectedDepartment);
        assertThat(reportLine.certification()).isNull();
        assertThat(reportLine.hallmarkingLocation()).isNull();
        assertThat(reportLine.hallmarkingMethod()).isNull();
        assertThat(reportLine.metal()).isNull();
        assertThat(reportLine.countCh()).isEqualByComparingTo(BigDecimal.valueOf(expectedCosts));
        assertThat(reportLine.countHmc()).isZero();
    }

    void assertAllOtherLinesHaveZeroCount(final List<GenerateReportUseCase.ReportLineOutput> reportLines) {
        IntStream.range(0, reportLines.size())
                 .filter(currentLineIndex -> !linesWithNoZeroCount.contains(currentLineIndex))
                 .mapToObj(reportLines::get)
                 .forEach(reportLine -> {
                     assertThat(reportLine.countCh()).isZero();
                     final boolean hmcIsZeroOrNull = reportLine.countHmc() == null || reportLine.countHmc() == 0;
                     assertThat(hmcIsZeroOrNull).isTrue();
                 });
    }
}
