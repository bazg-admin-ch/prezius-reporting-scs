package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {HallmarkingReportDtoMapper.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class HallmarkingReportDtoMapperTest {

    private final HallmarkingReportDtoMapper mapperUnderTest;

    private static Stream<Arguments> provideParameters() {
        var hallmarkingReportItemOne = HallmarkingReportItem.builder()
                                                            .goodsType(GoodsType.WATCH_CASE)
                                                            .hallmarkingMethod(HallmarkingMethod.MECHANICAL)
                                                            .certifiedMaterial(false)
                                                            .ag(100)
                                                            .au(0)
                                                            .pd(0)
                                                            .pt(0)
                                                            .cost(BigDecimal.ONE)
                                                            .hallmarkingType(HallmarkingType.SINGLE_SWISS)
                                                            .build();
        var hallmarkingReportItemTwo = HallmarkingReportItem.builder()
                                                            .goodsType(GoodsType.WATCH_CASE)
                                                            .hallmarkingMethod(HallmarkingMethod.MECHANICAL)
                                                            .certifiedMaterial(false)
                                                            .ag(120)
                                                            .au(0)
                                                            .pd(0)
                                                            .pt(0)
                                                            .cost(BigDecimal.TWO)
                                                            .hallmarkingType(HallmarkingType.DOUBLE)
                                                            .build();
        var hallmarkingReportItemThree = HallmarkingReportItem.builder()
                                                              .goodsType(GoodsType.JEWELRY)
                                                              .hallmarkingMethod(HallmarkingMethod.LASER)
                                                              .certifiedMaterial(true)
                                                              .ag(0)
                                                              .au(0)
                                                              .pd(0)
                                                              .pt(100)
                                                              .cost(BigDecimal.valueOf(3))
                                                              .hallmarkingType(HallmarkingType.SINGLE_SWISS)
                                                              .build();

        var hallmarkingReportItemGoodsTypeNoControl = HallmarkingReportItem.builder()
                                                              .goodsType(GoodsType.NO_CONTROL)
                                                              .hallmarkingMethod(HallmarkingMethod.LASER)
                                                              .certifiedMaterial(true)
                                                              .ag(0)
                                                              .au(0)
                                                              .pd(0)
                                                              .pt(100)
                                                              .cost(BigDecimal.valueOf(3))
                                                              .hallmarkingType(HallmarkingType.SINGLE_SWISS)
                                                              .build();

        var hallmarkingReportItemHallmarkingMethodNull = HallmarkingReportItem.builder()
                                                              .goodsType(GoodsType.JEWELRY)
                                                              .hallmarkingMethod(null)
                                                              .certifiedMaterial(true)
                                                              .ag(0)
                                                              .au(0)
                                                              .pd(0)
                                                              .pt(100)
                                                              .cost(BigDecimal.valueOf(3))
                                                              .hallmarkingType(null)
                                                              .build();
        return Stream.of(
                Arguments.of(List.of(hallmarkingReportItemOne, hallmarkingReportItemTwo), 340, 0, 340, BigDecimal.valueOf(3)),
                Arguments.of(List.of(hallmarkingReportItemTwo, hallmarkingReportItemThree), 240, 100, 340, BigDecimal.valueOf(5)),
                Arguments.of(List.of(hallmarkingReportItemOne, hallmarkingReportItemTwo, hallmarkingReportItemThree), 340, 100, 440, BigDecimal.valueOf(6)),
                Arguments.of(List.of(hallmarkingReportItemThree), 0, 100, 100, BigDecimal.valueOf(3)),
                Arguments.of(List.of(hallmarkingReportItemTwo), 240, 0, 240, BigDecimal.TWO),
                Arguments.of(List.of(hallmarkingReportItemGoodsTypeNoControl), 0, 100, 100, BigDecimal.valueOf(3)),
                Arguments.of(List.of(hallmarkingReportItemHallmarkingMethodNull), 0, 100, 100, BigDecimal.valueOf(3))
                        );
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    void toReportDto_whenReportItemsPresent_thenMapToReportDto(final List<HallmarkingReportItem> items, final int expectedTotalAg, final int expectedTotalPt,
                                                               final int expectedTotal, final BigDecimal expectedTotalCostWithVat) {
        final var reportDto = mapperUnderTest.toReportDto(items);

        assertThat(reportDto.getTotal().pt()).isEqualTo(expectedTotalPt);
        assertThat(reportDto.getTotal().ag()).isEqualTo(expectedTotalAg);
        assertThat(reportDto.getTotal().getTotal()).isEqualTo(expectedTotal);
        assertThat(reportDto.getTotal().totalCostsWithVat()).isEqualTo(expectedTotalCostWithVat);
    }
}
