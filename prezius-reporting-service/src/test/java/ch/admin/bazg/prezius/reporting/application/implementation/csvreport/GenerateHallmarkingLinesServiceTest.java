package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.HallmarkingReportItemGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {ReportLineOutputMapperImpl.class, GenerateHallmarkingLinesService.class})
class GenerateHallmarkingLinesServiceTest {

    @Autowired
    private GenerateHallmarkingLinesService serviceUnderTest;

    private final HallmarkingReportItemGenerator hallmarkingReportItemGenerator = new HallmarkingReportItemGenerator();

    @Test
    void whenCalled_returnGeneratedStream() {
        final var reportItems = this.generateFourHallmarkingItems();
        final var office = ControlOffice.ZEMK;

        final var resultList = serviceUnderTest.generate(ControlOffice.ZEMK, reportItems).toList();

        assertThat(resultList).hasSize(97);

        final var reportLineAsserter = ReportLineAsserter.init();
        reportLineAsserter.assertHallmarkingCountLine(resultList, 4, office, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE, GenerateReportUseCase.Certification.YES,
                                                      HallmarkingLocation.OFFICE, HallmarkingMethod.LASER, Metal.GOLD, 0, 5);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 5, office, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE, GenerateReportUseCase.Certification.YES,
                                                      HallmarkingLocation.OFFICE, HallmarkingMethod.LASER, Metal.SILVER, 0, 3);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 6, office, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE, GenerateReportUseCase.Certification.YES,
                                                      HallmarkingLocation.OFFICE, HallmarkingMethod.LASER, Metal.PALLADIUM, 0, 7);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 7, office, GenerateReportUseCase.OperationOrGoodsType.WATCH_CASE, GenerateReportUseCase.Certification.YES,
                                                      HallmarkingLocation.OFFICE, HallmarkingMethod.LASER, Metal.PLATINUM, 0, 9);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 56, office, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                                                      HallmarkingLocation.CLIENT_AT_OFFICE, HallmarkingMethod.MECHANICAL, Metal.GOLD, 32, 32);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 57, office, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                                                      HallmarkingLocation.CLIENT_AT_OFFICE, HallmarkingMethod.MECHANICAL, Metal.SILVER, 31, 31);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 58, office, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                                                      HallmarkingLocation.CLIENT_AT_OFFICE, HallmarkingMethod.MECHANICAL, Metal.PALLADIUM, 33, 33);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 59, office, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                                                      HallmarkingLocation.CLIENT_AT_OFFICE, HallmarkingMethod.MECHANICAL, Metal.PLATINUM, 34, 34);

        reportLineAsserter.assertHallmarkingCountLine(resultList, 92, office, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                                                      HallmarkingLocation.CLIENT, HallmarkingMethod.LASER, Metal.GOLD, 25, 0);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 93, office, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                                                      HallmarkingLocation.CLIENT, HallmarkingMethod.LASER, Metal.SILVER, 15, 0);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 94, office, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                                                      HallmarkingLocation.CLIENT, HallmarkingMethod.LASER, Metal.PALLADIUM, 35, 0);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 95, office, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                                                      HallmarkingLocation.CLIENT, HallmarkingMethod.LASER, Metal.PLATINUM, 45, 0);
        reportLineAsserter.assertHallmarkingCountLine(resultList, 95, office, GenerateReportUseCase.OperationOrGoodsType.OTHER_GOODS, GenerateReportUseCase.Certification.NO,
                                                      HallmarkingLocation.CLIENT, HallmarkingMethod.LASER, Metal.PLATINUM, 45, 0);
        reportLineAsserter.assertRevenueLine(resultList, 96, office, GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, 132.12);

        reportLineAsserter.assertAllOtherLinesHaveZeroCount(resultList);
    }

    private List<HallmarkingReportItem> generateFourHallmarkingItems() {
        return List.of(
                hallmarkingReportItemGenerator.generate(r -> r
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
                ),
                hallmarkingReportItemGenerator.generate(r -> r
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
                ),
                hallmarkingReportItemGenerator.generate(r -> r
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
                ),
                hallmarkingReportItemGenerator.generate(r -> r
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
                ),
                hallmarkingReportItemGenerator.generate(r -> r
                    .office(ControlOffice.ZEMK)
                    .goodsType(GoodsType.NO_CONTROL)
                    .certifiedMaterial(false)
                    .hallmarkingLocation(HallmarkingLocation.CLIENT_AT_OFFICE)
                    .hallmarkingMethod(HallmarkingMethod.MECHANICAL)
                    .hallmarkingType(HallmarkingType.DOUBLE)
                    .ag(100)
                    .au(101)
                    .pd(102)
                    .pt(103)
                    .cost(BigDecimal.valueOf(100))
                )
                      );
    }

}