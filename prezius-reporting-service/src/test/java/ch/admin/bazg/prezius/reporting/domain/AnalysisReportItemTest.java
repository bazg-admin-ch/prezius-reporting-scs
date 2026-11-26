package ch.admin.bazg.prezius.reporting.domain;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.AnalysisReportItemGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalysisReportItemTest {

    private final AnalysisReportItemGenerator analysisReportItemGenerator = new AnalysisReportItemGenerator();

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
        final AnalysisType analysisType = AnalysisType.SAMPLES_FROM_HALLMARKING;

        final var newPosition = AnalysisReportItem.createNew(orderId, ag, au, pd, pt, cost, office, date, businessPartner, analysisType);

        assertEquals(orderId, newPosition.getOrderId());
        assertEquals(ag, newPosition.getAg());
        assertEquals(au, newPosition.getAu());
        assertEquals(pd, newPosition.getPd());
        assertEquals(pt, newPosition.getPt());
        assertEquals(cost, newPosition.getCost());
        assertEquals(office, newPosition.getOffice());
        assertEquals(date, newPosition.getDate());
        assertEquals(businessPartner, newPosition.getBusinessPartner());
        assertEquals(analysisType, newPosition.getAnalysisType());
    }

    @ParameterizedTest(name = "Should throw exception when order has status {0}")
    @CsvSource({
            "1, 2, 3, 4, GOLD, 1",
            "1, 2, 3, 4, SILVER, 2",
            "1, 2, 3, 4, PALLADIUM, 3",
            "1, 2, 3, 4, PLATINUM, 4"})
    void getCountByMetal(final int countAu, final int countAg, final int countPd, final int countPt, final Metal metal, final int expectedCount) {
        final var reportItem = analysisReportItemGenerator.generate(r -> r
                .ag(countAg)
                .au(countAu)
                .pd(countPd)
                .pt(countPt)
        );

        assertThat(reportItem.getCountByMetal(metal)).isEqualTo(expectedCount);
    }
}
