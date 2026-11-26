package ch.admin.bazg.prezius.reporting.adapter.out.jpa;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import ch.admin.bazg.prezius.reporting.testenvironment.ITPersistenceBase;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.HallmarkingReportItemGenerator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class HallmarkingReportItemJpaAdapterIT extends ITPersistenceBase {

    private final static UUID ORDER_ID = UUID.randomUUID();

    private final HallmarkingReportItemGenerator hallmarkingReportItemGenerator;
    private final HallmarkingReportItemRepository hallmarkingReportItemRepository;
    private final HallmarkingReportItemJpaAdapter adapterUnderTest;

    private HallmarkingReportItem reportItemRemained;
    private HallmarkingReportItem reportItem1;
    private HallmarkingReportItem reportItem2;

    @Test
    void search_whenFilteredByOffice_thenReturnFiltered() {
        final var reportItemZEMK1 = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.office(ControlOffice.ZEMK)));
        final var reportItemZEMK2 = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.office(ControlOffice.ZEMK)));
        final var reportItemCDFC = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.office(ControlOffice.CDFC)));
        final var filter = HallmarkingFilterCriteria.builder().officeFilter(List.of(ControlOffice.ZEMK)).build();

        final List<HallmarkingReportItem> allByOrderId = adapterUnderTest.search(filter);
        assertThat(allByOrderId)
                .hasSize(2)
                .contains(reportItemZEMK1, reportItemZEMK2)
                .doesNotContain(reportItemCDFC);
    }

    @Test
    void search_whenFilteredByBusinessPartner_thenReturnFiltered() {
        final var reportItem1 = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.businessPartner("bp2")));
        final var reportItem2 = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.businessPartner("bp2")));
        final var ignoredReportItem = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.businessPartner("bp1")));

        final var filter = HallmarkingFilterCriteria.builder().businessPartnerFilter(List.of("bp2")).build();

        final List<HallmarkingReportItem> allByOrderId = adapterUnderTest.search(filter);

        assertThat(allByOrderId)
                .hasSize(2)
                .contains(reportItem1, reportItem2)
                .doesNotContain(ignoredReportItem);
    }

    @Test
    void search_whenFilteredByDateRange_thenReturnFiltered() {
        final var reportItem1 = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.date(LocalDateTime.of(2023, 2, 28, 12, 44))));
        final var ignoredReportItem = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.date(LocalDateTime.of(2024, 2, 28, 12, 44))));

        final var filter = HallmarkingFilterCriteria.builder()
                .dateAfterFilter(LocalDateTime.of(2023, 1, 1, 22, 0))
                .dateBeforeFilter(LocalDateTime.of(2023, 12, 31, 22, 0))
                .build();

        final List<HallmarkingReportItem> allByOrderId = adapterUnderTest.search(filter);
        assertThat(allByOrderId)
                .hasSize(1)
                .contains(reportItem1)
                .doesNotContain(ignoredReportItem);
    }

    @Test
    void search_whenFilteredByHallmarkingTypes_thenReturnFiltered() {
        final var reportItem1 = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.hallmarkingType(HallmarkingType.SINGLE_SWISS)));
        final var reportItem2 = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.hallmarkingType(HallmarkingType.SINGLE_SWISS)));
        final var ignoredReportItem = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.hallmarkingType(HallmarkingType.SINGLE_INTERNATIONAL)));

        final var filter = HallmarkingFilterCriteria.builder().hallmarkingTypes(List.of(HallmarkingType.SINGLE_SWISS)).build();

        final List<HallmarkingReportItem> allByOrderId = adapterUnderTest.search(filter);
        assertThat(allByOrderId)
                .hasSize(2)
                .contains(reportItem1, reportItem2)
                .doesNotContain(ignoredReportItem);
    }

    @Test
    void getAllByFilter_whenBothHallmarkingTypesArePassed_thenReturnOnlyDoubleAndNoSingle() {
        final var reportItemSingleSwiss = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.hallmarkingType(HallmarkingType.SINGLE_SWISS)));
        final var reportItemSingleInternational = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.hallmarkingType(HallmarkingType.SINGLE_INTERNATIONAL)));
        final var reportItemDouble = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.hallmarkingType(HallmarkingType.DOUBLE)));

        final var filter = HallmarkingFilterCriteria.builder().hallmarkingTypes(List.of(HallmarkingType.SINGLE_SWISS, HallmarkingType.SINGLE_INTERNATIONAL)).build();

        final List<HallmarkingReportItem> allByOrderId = adapterUnderTest.search(filter);

        assertThat(allByOrderId)
                .hasSize(1)
                .contains(reportItemDouble)
                .doesNotContain(reportItemSingleSwiss, reportItemSingleInternational);
    }

    @Test
    void deleteAllByOrderId_whenReportItemsWithCorrectOrderIdExist_thenDeleteAll() {
        reportItem1 = this.hallmarkingReportItemGenerator.generate(item -> item.orderId(ORDER_ID));
        reportItem2 = this.hallmarkingReportItemGenerator.generate(item -> item.orderId(ORDER_ID));
        reportItemRemained = this.hallmarkingReportItemGenerator.generate(item -> item.orderId(UUID.randomUUID()));
        adapterUnderTest.storeAll(List.of(reportItem1, reportItem2, reportItemRemained));

        assertThat(hallmarkingReportItemRepository.count()).isEqualTo(3);

        adapterUnderTest.deleteAllByOrderId(ORDER_ID);

        final List<HallmarkingReportItem> allItems = hallmarkingReportItemRepository.findAll();
        assertThat(allItems).hasSize(1)
                .extracting("orderId")
                .containsExactlyInAnyOrder(reportItemRemained.getOrderId());
    }

    @Test
    void getAllByOrderId_whenReportItemsWithCorrectOrderIdExist_thenReturnAll() {
        reportItem1 = this.hallmarkingReportItemGenerator.generate(item -> item.orderId(ORDER_ID));
        reportItem2 = this.hallmarkingReportItemGenerator.generate(item -> item.orderId(ORDER_ID));
        reportItemRemained = this.hallmarkingReportItemGenerator.generate(item -> item.orderId(UUID.randomUUID()));
        adapterUnderTest.storeAll(List.of(reportItem1, reportItem2, reportItemRemained));

        assertThat(hallmarkingReportItemRepository.count()).isEqualTo(3);

        final List<HallmarkingReportItem> allByOrderId = adapterUnderTest.getAllByOrderId(ORDER_ID);
        assertThat(allByOrderId).hasSize(2)
                .extracting("orderId")
                .containsExactlyInAnyOrder(reportItem1.getOrderId(), reportItem1.getOrderId());
    }
}
