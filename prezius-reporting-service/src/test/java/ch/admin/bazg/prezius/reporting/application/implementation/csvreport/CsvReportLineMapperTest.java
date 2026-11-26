package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {ReportLineOutputMapperImpl.class})
class CsvReportLineMapperTest {

    @Autowired
    private ReportLineOutputMapper mapperUnderTest;

    @Test
    void whenCalled_thenReturnMapped() {
        final var metalCount = new GenerateReportHandler.MetalCount(3, 5);
        final var metal = Metal.GOLD;
        final var hallmarkingLineAttributesCombination = new HallmarkingLineAttributesCombination(
                ControlOffice.GDPC, HallmarkingLocation.OFFICE,
                true, GoodsType.WATCH_CASE, HallmarkingMethod.LASER
        );

        final var mapped = mapperUnderTest.toOutputLineItem(hallmarkingLineAttributesCombination, metalCount, metal);

        assertThat(mapped.controlOffice()).isEqualTo(ControlOffice.GDPC);
        assertThat(mapped.section()).isEqualTo(GenerateReportUseCase.Section.HALLMARKING);
        assertThat(mapped.operationOrGoodsType()).isEqualTo(GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE);
        assertThat(mapped.certification()).isEqualTo(GenerateReportUseCase.Certification.YES);
        assertThat(mapped.hallmarkingMethod()).isEqualTo(hallmarkingLineAttributesCombination.hallmarkingMethod());
        assertThat(mapped.hallmarkingLocation()).isEqualTo(hallmarkingLineAttributesCombination.hallmarkingLocation());
        assertThat(mapped.metal()).isEqualTo(metal);
        assertThat(mapped.countCh().intValue()).isEqualTo(metalCount.ch());
        assertThat(mapped.countHmc()).isEqualTo(metalCount.hmc());

    }

    @Test
    void whenAnalysisAttributesCombination_thenReturnMapped() {
        final var metal = Metal.GOLD;
        final var analysisAttributesCombination = new AnalysisLineAttributesCombination(
                ControlOffice.GDPC, AnalysisType.SAMPLES_FROM_HALLMARKING
        );

        final var mapped = mapperUnderTest.toOutputLineItem(analysisAttributesCombination, 6, metal);

        assertThat(mapped.controlOffice()).isEqualTo(ControlOffice.GDPC);
        assertThat(mapped.section()).isEqualTo(GenerateReportUseCase.Section.ANALYSIS);
        assertThat(mapped.operationOrGoodsType()).isEqualTo(GenerateReportUseCase.OperationOrGoodsType.SAMPLES_FROM_HALLMARKING);
        assertThat(mapped.certification()).isNull();
        assertThat(mapped.hallmarkingMethod()).isNull();
        assertThat(mapped.hallmarkingLocation()).isNull();
        assertThat(mapped.metal()).isEqualTo(metal);
        assertThat(mapped.countCh().intValue()).isEqualTo(6);
        assertThat(mapped.countHmc()).isNull();

    }

    @Test
    void  whenAnalysisAttributesCombinationWithNotMappedAnalysisType_thenReturnMappedWithAnalysisTypeNull() {
        final var metal = Metal.GOLD;
        final var analysisAttributesCombination = new AnalysisLineAttributesCombination(
                ControlOffice.GDPC, AnalysisType.CONFORMITY_EVALUATION_COATING
        );

        final var mapped = mapperUnderTest.toOutputLineItem(analysisAttributesCombination, 6, metal);

        assertThat(mapped.operationOrGoodsType()).isNull();
        assertThat(mapped.controlOffice()).isEqualTo(ControlOffice.GDPC);
        assertThat(mapped.section()).isEqualTo(GenerateReportUseCase.Section.ANALYSIS);
        assertThat(mapped.certification()).isNull();
        assertThat(mapped.hallmarkingMethod()).isNull();
        assertThat(mapped.hallmarkingLocation()).isNull();
        assertThat(mapped.metal()).isEqualTo(metal);
        assertThat(mapped.countCh().intValue()).isEqualTo(6);
        assertThat(mapped.countHmc()).isNull();

    }

}