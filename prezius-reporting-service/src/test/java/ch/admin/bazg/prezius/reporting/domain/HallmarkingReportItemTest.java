package ch.admin.bazg.prezius.reporting.domain;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.HallmarkingReportItemGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HallmarkingReportItemTest {

    private final HallmarkingReportItemGenerator hallmarkingReportItemGenerator = new HallmarkingReportItemGenerator();

    @Test
    void createNew_whenCalled_thenReturnNewItem() {
        final UUID orderId = UUID.randomUUID();
        final int ag = 1;
        final int au = 0;
        final int pd = 0;
        final int pt = 1;
        final BigDecimal cost = BigDecimal.TEN;
        final ControlOffice office = ControlOffice.NOIC;
        final LocalDateTime date = LocalDateTime.now();
        final String businessPartner = "0123456789";
        final GoodsType goodsType = GoodsType.JEWELRY;
        final HallmarkingType hallmarkingType = HallmarkingType.SINGLE_SWISS;
        final HallmarkingMethod hallmarkingMethod = HallmarkingMethod.MECHANICAL;
        final HallmarkingLocation hallmarkingLocation = HallmarkingLocation.OFFICE;
        final boolean certifiedMaterial = false;

        final var newPosition = HallmarkingReportItem.createNew(orderId, ag, au, pd, pt, cost, office, date, businessPartner,
                goodsType, hallmarkingType, hallmarkingMethod, hallmarkingLocation, certifiedMaterial);

        assertEquals(orderId, newPosition.getOrderId());
        assertEquals(ag, newPosition.getAg());
        assertEquals(au, newPosition.getAu());
        assertEquals(pd, newPosition.getPd());
        assertEquals(pt, newPosition.getPt());
        assertEquals(cost, newPosition.getCost());
        assertEquals(office, newPosition.getOffice());
        assertEquals(date, newPosition.getDate());
        assertEquals(businessPartner, newPosition.getBusinessPartner());
        assertEquals(goodsType, newPosition.getGoodsType());
        assertEquals(hallmarkingType, newPosition.getHallmarkingType());
        assertEquals(hallmarkingMethod, newPosition.getHallmarkingMethod());
        assertEquals(hallmarkingLocation, newPosition.getHallmarkingLocation());
        assertEquals(certifiedMaterial, newPosition.isCertifiedMaterial());
    }

    @Test
    void whenHallmarkCountCalled_thenReturnTheCorrectCount() {
        HallmarkingReportItem swissItem = HallmarkingReportItem.builder()
                .hallmarkingType(HallmarkingType.SINGLE_SWISS)
                .build();
        HallmarkingReportItem internationalItem = HallmarkingReportItem.builder()
                .hallmarkingType(HallmarkingType.SINGLE_INTERNATIONAL)
                .build();
        HallmarkingReportItem doubleItem = HallmarkingReportItem.builder()
                .hallmarkingType(HallmarkingType.DOUBLE)
                .build();

        assertEquals(1, swissItem.hallmarkCount());
        assertEquals(1, internationalItem.hallmarkCount());
        assertEquals(2, doubleItem.hallmarkCount());
    }

    @ParameterizedTest(name = "Should throw exception when order has status {0}")
    @CsvSource({
            "1, 2, 3, 4, GOLD, 1",
            "1, 2, 3, 4, SILVER, 2",
            "1, 2, 3, 4, PALLADIUM, 3",
            "1, 2, 3, 4, PLATINUM, 4"})
    void getCountByMetal(final int countAu, final int countAg, final int countPd, final int countPt, final Metal metal, final int expectedCount) {
        final var reportItem = hallmarkingReportItemGenerator.generate(r -> r
                .ag(countAg)
                .au(countAu)
                .pd(countPd)
                .pt(countPt)
        );

        assertThat(reportItem.getCountByMetal(metal)).isEqualTo(expectedCount);
    }
}
