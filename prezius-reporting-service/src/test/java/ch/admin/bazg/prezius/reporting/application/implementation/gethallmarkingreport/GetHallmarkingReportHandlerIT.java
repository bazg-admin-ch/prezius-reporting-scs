package ch.admin.bazg.prezius.reporting.application.implementation.gethallmarkingreport;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.domain.ReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GetHallmarkingReportHandlerIT extends ITBase {

    private final GetHallmarkingReportHandler handlerUnderTest;

    @Test
    void whenOfficeFilterIsSet_thenFilterByOffice() {
        final var zemkItem = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.office(ControlOffice.ZEMK)));
        final var biecItem = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.office(ControlOffice.BIEC)));
        final var bascItem = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.office(ControlOffice.BASC)));
        final var filter = HallmarkingFilterCriteria.builder()
                .officeFilter(List.of(ControlOffice.ZEMK, ControlOffice.BIEC))
                .build();

        final var result = handlerUnderTest.getHallmarkingReportItems(filter);

        assertThat(result)
                .hasSize(2)
                .map(ReportItem::getId)
                .contains(zemkItem.getId(), biecItem.getId())
                .doesNotContain(bascItem.getId());

    }

    @Test
    void whenBpFilterIsSet_thenFilterByOffice() {
        final var bpId1 = "123";
        final var bpId2 = "456";
        final var itemWithBp1 = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.businessPartner(bpId1)));
        final var itemWithBp2 = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.businessPartner(bpId2)));
        final var itemWithBp3 = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.businessPartner("other")));
        final var filter = HallmarkingFilterCriteria.builder()
                                                    .businessPartnerFilter(List.of(bpId1, bpId2))
                                                    .build();

        final var result = handlerUnderTest.getHallmarkingReportItems(filter);

        assertThat(result)
                .hasSize(2)
                .map(ReportItem::getId)
                .contains(itemWithBp1.getId(), itemWithBp2.getId())
                .doesNotContain(itemWithBp3.getId());

    }

    @Test
    void whenDateFilterIsSet_thenFilterByOffice() {
        final var itemMarch24 = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2024, 3, 9, 10, 0))));
        final var itemDecember24 = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2024, 12, 31, 0, 0))));
        final var itemJanuary25 = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2025, 1, 1, 20, 0))));
        final var filter = HallmarkingFilterCriteria.builder()
                                                    .dateAfterFilter(LocalDateTime.of(2024, 1, 1, 22, 0))
                                                    .dateBeforeFilter(LocalDateTime.of(2024, 12, 31, 10, 0))
                                                    .build();

        final var result = handlerUnderTest.getHallmarkingReportItems(filter);

        assertThat(result)
                .hasSize(2)
                .map(ReportItem::getId)
                .contains(itemMarch24.getId(), itemDecember24.getId())
                .doesNotContain(itemJanuary25.getId());
    }

    @Test
    void getHallmarkingReport_Items_typeSingleSwissFiltered_thenOnlyTypeSingleSwissDataReturned() {
        final var itemSingleSwiss = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.hallmarkingType(HallmarkingType.SINGLE_SWISS)));
        final var itemSingleInternational = storageHelper.store(hallmarkingReportItemGenerator.generate(r -> r.hallmarkingType(HallmarkingType.SINGLE_INTERNATIONAL)));
        final var filter = HallmarkingFilterCriteria.builder()
                                                    .hallmarkingTypes(List.of(HallmarkingType.SINGLE_SWISS))
                                                    .build();

        final var result = handlerUnderTest.getHallmarkingReportItems(filter);

        assertThat(result)
                .hasSize(1)
                .map(ReportItem::getId)
                .contains(itemSingleSwiss.getId())
                .doesNotContain(itemSingleInternational.getId());
    }
}
