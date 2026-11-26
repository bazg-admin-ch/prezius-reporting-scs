package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GenerateReportHandlerIT extends ITBase {

    private final GenerateReportHandler handlerUnderTest;

    @Test
    void whenOfficeListisComplete_thenReturnAll1062HallmarkingAndAnalysisLines() {
        this.storeFiveHallmarkingItemsForOfficeZemkAndCdfc();
        this.storeOneAnalysisItem(ControlOffice.BASC, 91);
        final var zemkOffice = ControlOffice.ZEMK;

        final var filter = HallmarkingFilterCriteria.builder().officeFilter(Arrays.asList(ControlOffice.values())).build();
        final var fluxResult = handlerUnderTest.generate(filter);

        final var blockedResultList = fluxResult.collect(Collectors.toList()).block();

        assertThat(blockedResultList).hasSize(1062);
        final var reportLineAsserter = ReportLineAsserter.init();
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 4, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE,
                GenerateReportUseCase.Certification.YES, HallmarkingLocation.OFFICE, HallmarkingMethod.LASER,
                0, 5, 0, 3,
                0, 7, 0, 9);
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 56, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS,
                GenerateReportUseCase.Certification.NO, HallmarkingLocation.CLIENT_AT_OFFICE, HallmarkingMethod.MECHANICAL,
                32, 32, 31, 31,
                33, 33, 34, 34);
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 92, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS,
                GenerateReportUseCase.Certification.NO, HallmarkingLocation.CLIENT, HallmarkingMethod.LASER,
                25, 0, 15, 0,
                35, 0, 45, 0);
        reportLineAsserter.assertRevenueLine(blockedResultList, 96, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, 32.12);
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 1000, ControlOffice.CDFC, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS,
                GenerateReportUseCase.Certification.NO, HallmarkingLocation.CLIENT_AT_OFFICE, HallmarkingMethod.MECHANICAL,
                21, 0, 11, 0,
                31, 0, 41, 0);
        reportLineAsserter.assertRevenueLine(blockedResultList, 1040, ControlOffice.CDFC, GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, 3.00);

        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 923, ControlOffice.BASC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.GOLD, 91);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 924, ControlOffice.BASC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.SILVER, 93);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 925, ControlOffice.BASC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PALLADIUM,
                                                   95);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 926, ControlOffice.BASC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PLATINUM,
                                                   97);
        reportLineAsserter.assertRevenueLine(blockedResultList, 943, ControlOffice.BASC, GenerateReportUseCase.OperationOrGoodsType.ANALYSIS_DEPARTMENT, 77.88);

        reportLineAsserter.assertAllOtherLinesHaveZeroCount(blockedResultList);
    }

    @Test
    void whenFilterContainsOneOffice_thenReturnAll873Items() {
        this.storeFiveHallmarkingItemsForOfficeZemkAndCdfc();
        final var zemkOffice = ControlOffice.ZEMK;
        this.storeOneAnalysisItem(zemkOffice, 55);
        this.storeOneAnalysisItem(ControlOffice.GDPC, 91);

        final var filter = HallmarkingFilterCriteria.builder().officeFilter(List.of(ControlOffice.ZEMK)).build();
        final var fluxResult = handlerUnderTest.generate(filter);
        final var blockedResultList = fluxResult.collect(Collectors.toList()).block();

        assertThat(blockedResultList).hasSize(118);

        final var reportLineAsserter = ReportLineAsserter.init();
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 4, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE,
                GenerateReportUseCase.Certification.YES, HallmarkingLocation.OFFICE, HallmarkingMethod.LASER,
                0, 5, 0, 3,
                0, 7, 0, 9);
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 56, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS,
                GenerateReportUseCase.Certification.NO, HallmarkingLocation.CLIENT_AT_OFFICE, HallmarkingMethod.MECHANICAL,
                32, 32, 31, 31,
                33, 33, 34, 34);
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 92, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS,
                GenerateReportUseCase.Certification.NO, HallmarkingLocation.CLIENT, HallmarkingMethod.LASER,
                25, 0, 15, 0,
                35, 0, 45, 0);
        reportLineAsserter.assertRevenueLine(blockedResultList, 96, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, 32.12);

        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 97, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.GOLD, 55);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 98, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.SILVER, 93);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 99, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PALLADIUM, 95);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 100, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PLATINUM, 97);
        reportLineAsserter.assertRevenueLine(blockedResultList, 117, zemkOffice, GenerateReportUseCase.OperationOrGoodsType.ANALYSIS_DEPARTMENT, 77.88);

        reportLineAsserter.assertAllOtherLinesHaveZeroCount(blockedResultList);
    }

    @Test
    void whenFilterContainsOneBusinessPartner_thenReturnMatchingItems() {
        this.storeOneAnalysisItem(ControlOffice.BIEC, 69, LocalDateTime.now(), "1");
        this.storeOneAnalysisItem(ControlOffice.BIEC, 420, LocalDateTime.now(), "2");
        this.storeOneHallmarkingItem(ControlOffice.BIEC, 69, LocalDateTime.now(), "1", HallmarkingType.SINGLE_SWISS);
        this.storeOneHallmarkingItem(ControlOffice.BIEC, 420, LocalDateTime.now(), "2", HallmarkingType.SINGLE_SWISS);

        final var filter = HallmarkingFilterCriteria.builder()
                                                    .officeFilter(List.of(ControlOffice.BIEC))
                                                    .businessPartnerFilter(List.of("1"))
                                                    .build();
        final var fluxResult = handlerUnderTest.generate(filter);
        final var blockedResultList = fluxResult.collect(Collectors.toList()).block();

        assertThat(blockedResultList).hasSize(118);

        final var reportLineAsserter = ReportLineAsserter.init();
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 16, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE,
                GenerateReportUseCase.Certification.NO, HallmarkingLocation.OFFICE, HallmarkingMethod.MECHANICAL,
                69, 0, 0, 0,
                0, 0, 0, 0);

        reportLineAsserter.assertRevenueLine(blockedResultList, 96, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, 3);

        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 97, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.GOLD, 69);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 98, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.SILVER, 93);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 99, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PALLADIUM,
                                                   95);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 100, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PLATINUM,
                                                   97);
        reportLineAsserter.assertRevenueLine(blockedResultList, 117, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.ANALYSIS_DEPARTMENT, 77.88);

        reportLineAsserter.assertAllOtherLinesHaveZeroCount(blockedResultList);
    }

    @Test
    void whenFilterContainsDateAfter_thenReturnMatchingItems() {
        this.storeOneAnalysisItem(ControlOffice.BIEC, 69, LocalDateTime.of(2025, 4, 10, 0, 0, 0), "1");
        this.storeOneAnalysisItem(ControlOffice.BIEC, 420, LocalDateTime.of(2025, 2, 10, 0, 0, 0), "2");
        this.storeOneHallmarkingItem(ControlOffice.BIEC, 69, LocalDateTime.of(2025, 4, 10, 0, 0, 0), "1", HallmarkingType.SINGLE_SWISS);
        this.storeOneHallmarkingItem(ControlOffice.BIEC, 420, LocalDateTime.of(2025, 2, 10, 0, 0, 0), "2", HallmarkingType.SINGLE_SWISS);

        final var filter = HallmarkingFilterCriteria.builder()
                                                    .officeFilter(List.of(ControlOffice.BIEC))
                                                    .dateAfterFilter(LocalDateTime.of(2025, 3, 10, 0, 0, 0))
                                                    .build();
        final var fluxResult = handlerUnderTest.generate(filter);
        final var blockedResultList = fluxResult.collect(Collectors.toList()).block();

        assertThat(blockedResultList).hasSize(118);

        final var reportLineAsserter = ReportLineAsserter.init();
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 16, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE,
                GenerateReportUseCase.Certification.NO, HallmarkingLocation.OFFICE, HallmarkingMethod.MECHANICAL,
                69, 0, 0, 0,
                0, 0, 0, 0);

        reportLineAsserter.assertRevenueLine(blockedResultList, 96, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, 3);

        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 97, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.GOLD, 69);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 98, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.SILVER, 93);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 99, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PALLADIUM,
                                                   95);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 100, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PLATINUM,
                                                   97);
        reportLineAsserter.assertRevenueLine(blockedResultList, 117, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.ANALYSIS_DEPARTMENT, 77.88);

        reportLineAsserter.assertAllOtherLinesHaveZeroCount(blockedResultList);
    }

    @Test
    void whenFilterContainsDateBefore_thenReturnMatchingItems() {
        this.storeOneAnalysisItem(ControlOffice.BIEC, 69, LocalDateTime.of(2025, 2, 10, 0, 0, 0), "1");
        this.storeOneAnalysisItem(ControlOffice.BIEC, 420, LocalDateTime.of(2025, 4, 10, 0, 0, 0), "2");
        this.storeOneHallmarkingItem(ControlOffice.BIEC, 69, LocalDateTime.of(2025, 2, 10, 0, 0, 0), "1", HallmarkingType.SINGLE_SWISS);
        this.storeOneHallmarkingItem(ControlOffice.BIEC, 420, LocalDateTime.of(2025, 4, 10, 0, 0, 0), "2", HallmarkingType.SINGLE_SWISS);

        final var filter = HallmarkingFilterCriteria.builder()
                                                    .officeFilter(List.of(ControlOffice.BIEC))
                                                    .dateBeforeFilter(LocalDateTime.of(2025, 3, 10, 0, 0, 0))
                                                    .build();
        final var fluxResult = handlerUnderTest.generate(filter);
        final var blockedResultList = fluxResult.collect(Collectors.toList()).block();

        assertThat(blockedResultList).hasSize(118);

        final var reportLineAsserter = ReportLineAsserter.init();
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 16, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE,
                GenerateReportUseCase.Certification.NO, HallmarkingLocation.OFFICE, HallmarkingMethod.MECHANICAL,
                69, 0, 0, 0,
                0, 0, 0, 0);

        reportLineAsserter.assertRevenueLine(blockedResultList, 96, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, 3);

        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 97, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.GOLD, 69);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 98, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.SILVER, 93);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 99, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PALLADIUM,
                                                   95);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 100, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PLATINUM,
                                                   97);
        reportLineAsserter.assertRevenueLine(blockedResultList, 117, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.ANALYSIS_DEPARTMENT, 77.88);

        reportLineAsserter.assertAllOtherLinesHaveZeroCount(blockedResultList);
    }

    @Test
    void whenFilterContainsHallmarkingType_thenReturnMatchingItems() {
        this.storeOneAnalysisItem(ControlOffice.BIEC, 69, LocalDateTime.now(), "1");
        this.storeOneAnalysisItem(ControlOffice.BIEC, 420, LocalDateTime.now(), "2");
        this.storeOneHallmarkingItem(ControlOffice.BIEC, 69, LocalDateTime.now(), "1", HallmarkingType.SINGLE_SWISS);
        this.storeOneHallmarkingItem(ControlOffice.BIEC, 420, LocalDateTime.now(), "2", HallmarkingType.SINGLE_INTERNATIONAL);

        final var filter = HallmarkingFilterCriteria.builder()
                                                    .officeFilter(List.of(ControlOffice.BIEC))
                                                    .hallmarkingTypes(List.of(HallmarkingType.SINGLE_SWISS))
                                                    .build();
        final var fluxResult = handlerUnderTest.generate(filter);
        final var blockedResultList = fluxResult.collect(Collectors.toList()).block();

        assertThat(blockedResultList).hasSize(118);

        final var reportLineAsserter = ReportLineAsserter.init();
        reportLineAsserter.asssertHallmarkingCountLinesForEachMetal(
                blockedResultList, 16, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE,
                GenerateReportUseCase.Certification.NO, HallmarkingLocation.OFFICE, HallmarkingMethod.MECHANICAL,
                69, 0, 0, 0,
                0, 0, 0, 0);

        reportLineAsserter.assertRevenueLine(blockedResultList, 96, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, 3);

        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 97, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.GOLD,
                                                   420 + 69);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 98, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.SILVER, 186);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 99, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PALLADIUM,
                                                   190);
        reportLineAsserter.assertAnalysisCountLine(blockedResultList, 100, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.CONFORMITY_EVALUATION, Metal.PLATINUM,
                                                   194);
        reportLineAsserter.assertRevenueLine(blockedResultList, 117, ControlOffice.BIEC, GenerateReportUseCase.OperationOrGoodsType.ANALYSIS_DEPARTMENT, 155.76);

        reportLineAsserter.assertAllOtherLinesHaveZeroCount(blockedResultList);
    }


    @Test
    void whenFilterIsNull_thenThrowConstrainViolationException() {
        assertThrows(ConstraintViolationException.class, () -> handlerUnderTest.generate(null));
    }

    private void storeFiveHallmarkingItemsForOfficeZemkAndCdfc() {
        storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r
            .office(ControlOffice.ZEMK)
            .goodsType(GoodsType.WATCH_CASE)
            .certifiedMaterial(true)
            .hallmarkingLocation(HallmarkingLocation.OFFICE)
            .hallmarkingMethod(HallmarkingMethod.LASER)
            .hallmarkingType(HallmarkingType.SINGLE_INTERNATIONAL)
            .ag(1)
            .au(2)
            .pd(3)
            .pt(4)
            .cost(BigDecimal.valueOf(1.11))
        ));
        storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r
            .office(ControlOffice.ZEMK)
            .goodsType(GoodsType.WATCH_CASE)
            .certifiedMaterial(true)
            .hallmarkingLocation(HallmarkingLocation.OFFICE)
            .hallmarkingMethod(HallmarkingMethod.LASER)
            .hallmarkingType(HallmarkingType.SINGLE_INTERNATIONAL)
            .ag(2)
            .au(3)
            .pd(4)
            .pt(5)
            .cost(BigDecimal.valueOf(3.61))
        ));
        storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r
            .office(ControlOffice.ZEMK)
            .goodsType(GoodsType.JEWELRY)
            .certifiedMaterial(false)
            .hallmarkingLocation(HallmarkingLocation.CLIENT)
            .hallmarkingMethod(HallmarkingMethod.LASER)
            .hallmarkingType(HallmarkingType.SINGLE_SWISS)
            .ag(15)
            .au(25)
            .pd(35)
            .pt(45)
            .cost(BigDecimal.valueOf(12.4))
        ));
        storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r
            .office(ControlOffice.ZEMK)
            .goodsType(GoodsType.JEWELRY)
            .certifiedMaterial(false)
            .hallmarkingLocation(HallmarkingLocation.CLIENT_AT_OFFICE)
            .hallmarkingMethod(HallmarkingMethod.MECHANICAL)
            .hallmarkingType(HallmarkingType.DOUBLE)
            .ag(31)
            .au(32)
            .pd(33)
            .pt(34)
            .cost(BigDecimal.valueOf(15))
        ));
        storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r
            .office(ControlOffice.CDFC)
            .goodsType(GoodsType.JEWELRY)
            .certifiedMaterial(false)
            .hallmarkingLocation(HallmarkingLocation.CLIENT_AT_OFFICE)
            .hallmarkingMethod(HallmarkingMethod.MECHANICAL)
            .hallmarkingType(HallmarkingType.SINGLE_SWISS)
            .ag(11)
            .au(21)
            .pd(31)
            .pt(41)
            .cost(BigDecimal.valueOf(3))
        ));
    }

    private void storeOneHallmarkingItem(final ControlOffice controlOffice, final int goldCount, final LocalDateTime date, final String bp, HallmarkingType hallmarkingType) {
        storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r
            .office(controlOffice)
            .goodsType(GoodsType.WATCH_CASE)
            .certifiedMaterial(false)
            .hallmarkingLocation(HallmarkingLocation.OFFICE)
            .hallmarkingMethod(HallmarkingMethod.MECHANICAL)
            .hallmarkingType(hallmarkingType)
            .ag(0)
            .au(goldCount)
            .pd(0)
            .pt(0)
            .cost(BigDecimal.valueOf(3))
            .businessPartner(bp)
            .date(date)
        ));
    }


    private void storeOneAnalysisItem(final ControlOffice controlOffice, final int goldCount) {
        storeOneAnalysisItem(controlOffice, goldCount, LocalDateTime.of(2025, 1, 2, 3, 4, 5), "100992111");
    }

    private void storeOneAnalysisItem(final ControlOffice controlOffice, final int goldCount, final LocalDateTime date, final String bp) {
        storageHelper.store(analysisReportItemGenerator.generate(
                r -> r
                        .office(controlOffice)
                        .analysisType(AnalysisType.CONFORMITY_EVALUATION)
                        .au(goldCount)
                        .ag(93)
                        .pd(95)
                        .pt(97)
                        .cost(BigDecimal.valueOf(77.88))
                        .date(date)
                        .businessPartner(bp)
                                                                ));
    }

    @Test
    void whenFilterIncludesOnlyBAZG_thenNoLinesAreGeneratedBecauseBAZGCannotDoAnalysisOrHallmarking() {
        // Given: BAZG office cannot do analysis or hallmarking processes
        // Store some data for BAZG office
        this.storeOneAnalysisItem(ControlOffice.BAZG, 100, LocalDateTime.now(), "bp1");
        this.storeOneHallmarkingItem(ControlOffice.BAZG, 50, LocalDateTime.now(), "bp1", HallmarkingType.SINGLE_SWISS);

        // When: Filter includes only BAZG
        final var filter = HallmarkingFilterCriteria.builder()
                .officeFilter(List.of(ControlOffice.BAZG))
                .build();
        final var fluxResult = handlerUnderTest.generate(filter);
        final var blockedResultList = fluxResult.collect(Collectors.toList()).block();

        // Then: No lines should be generated because BAZG is filtered out
        // by the condition: controlOffice.isCanDoAnalysisProcess() || controlOffice.isCanDoHallmarkProcess()
        assertThat(blockedResultList).isEmpty();

        // Verify that BAZG cannot do either process
        assertThat(ControlOffice.BAZG.isCanDoAnalysisProcess()).isFalse();
        assertThat(ControlOffice.BAZG.isCanDoHallmarkProcess()).isFalse();
    }

}
