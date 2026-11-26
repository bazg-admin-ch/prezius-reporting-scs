package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.AnalysisReportItemGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {ReportLineOutputMapperImpl.class, GenerateAnalysisLinesService.class})
class GenerateAnalysisLinesServiceTest {

    @Autowired
    private GenerateAnalysisLinesService serviceUnderTest;

    private final AnalysisReportItemGenerator analysisReportItemGenerator = new AnalysisReportItemGenerator();

    @Test
    void whenCalled_returnGeneratedStream() {
        final var reportItems = this.generateFourAnalysisItems();
        final var office = ControlOffice.BASC;

        final var resultList = serviceUnderTest.generate(office, reportItems).toList();

        assertThat(resultList).hasSize(21);

        final var reportLineAsserter = ReportLineAsserter.init();
        reportLineAsserter.assertAnalysisCountLine(resultList, 0, office, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.GOLD, 5);
        reportLineAsserter.assertAnalysisCountLine(resultList, 1, office, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.SILVER, 7);
        reportLineAsserter.assertAnalysisCountLine(resultList, 2, office, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PALLADIUM, 9);
        reportLineAsserter.assertAnalysisCountLine(resultList, 3, office, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PLATINUM, 11);
        reportLineAsserter.assertAnalysisCountLine(resultList, 4, office, GenerateReportUseCase.OperationOrGoodsType.DETERMINATION_ON_SAMPLES, Metal.GOLD, 12);
        reportLineAsserter.assertAnalysisCountLine(resultList, 5, office, GenerateReportUseCase.OperationOrGoodsType.DETERMINATION_ON_SAMPLES, Metal.SILVER, 14);
        reportLineAsserter.assertAnalysisCountLine(resultList, 6, office, GenerateReportUseCase.OperationOrGoodsType.DETERMINATION_ON_SAMPLES, Metal.PALLADIUM, 16);
        reportLineAsserter.assertAnalysisCountLine(resultList, 7, office, GenerateReportUseCase.OperationOrGoodsType.DETERMINATION_ON_SAMPLES, Metal.PLATINUM, 18);
        reportLineAsserter.assertAnalysisCountLine(resultList, 12, office, GenerateReportUseCase.OperationOrGoodsType.SAMPLES_FROM_HALLMARKING, Metal.GOLD, 54);
        reportLineAsserter.assertAnalysisCountLine(resultList, 13, office, GenerateReportUseCase.OperationOrGoodsType.SAMPLES_FROM_HALLMARKING, Metal.SILVER, 56);
        reportLineAsserter.assertAnalysisCountLine(resultList, 14, office, GenerateReportUseCase.OperationOrGoodsType.SAMPLES_FROM_HALLMARKING, Metal.PALLADIUM, 58);
        reportLineAsserter.assertAnalysisCountLine(resultList, 15, office, GenerateReportUseCase.OperationOrGoodsType.SAMPLES_FROM_HALLMARKING, Metal.PLATINUM, 60);
        reportLineAsserter.assertRevenueLine(resultList, 20, office, GenerateReportUseCase.OperationOrGoodsType.ANALYSIS_DEPARTMENT, 116.75);

        reportLineAsserter.assertAllOtherLinesHaveZeroCount(resultList);
    }

    private List<AnalysisReportItem> generateFourAnalysisItems() {
        return List.of(
                analysisReportItemGenerator.generate(r -> r
                        .office(ControlOffice.BASC)
                        .analysisType(AnalysisType.CONFORMITY_EVALUATION)
                        .au(4)
                        .ag(5)
                        .pd(6)
                        .pt(7)
                        .cost(BigDecimal.valueOf(2.93))
                ),
                analysisReportItemGenerator.generate(
                        r -> r
                                .office(ControlOffice.BASC)
                                .analysisType(AnalysisType.CONFORMITY_EVALUATION)
                                .au(1)
                                .ag(2)
                                .pd(3)
                                .pt(4)
                                .cost(BigDecimal.valueOf(2.22))),
                analysisReportItemGenerator.generate(
                        r -> r
                                .office(ControlOffice.BASC)
                                .analysisType(AnalysisType.DETERMINATION_ON_SAMPLES)
                                .au(12)
                                .ag(14)
                                .pd(16)
                                .pt(18)
                                .cost(BigDecimal.valueOf(0.4))),
                analysisReportItemGenerator.generate(
                        r -> r
                                .office(ControlOffice.BASC)
                                .analysisType(AnalysisType.SAMPLES_FROM_HALLMARKING)
                                .au(54)
                                .ag(56)
                                .pd(58)
                                .pt(60)
                                .cost(BigDecimal.valueOf(11.2))),
                analysisReportItemGenerator.generate(
                        r -> r
                                .office(ControlOffice.BASC)
                                .analysisType(AnalysisType.EXPERTISE_ON_FINISHED_GOODS) // should be ignored
                                .au(101)
                                .ag(102)
                                .pd(103)
                                .pt(104)
                                .cost(BigDecimal.valueOf(100)))
                      );
    }

}