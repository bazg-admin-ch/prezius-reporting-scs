package ch.admin.bazg.prezius.reporting.adapter.out.jpa;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisFilterCriteria;
import ch.admin.bazg.prezius.reporting.testenvironment.ITPersistenceBase;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.AnalysisReportItemGenerator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AnalysisReportItemJpaAdapterIT extends ITPersistenceBase {

    private final AnalysisReportItemJpaAdapter adapterUnderTest;
    private final AnalysisReportItemGenerator analysisReportItemGenerator;
    private final AnalysisReportItemRepository analysisReportItemRepository;

    @Test
    void storeOne_whenCalled_shouldStoreReportItem() {
        final AnalysisReportItem reportItem = analysisReportItemGenerator.generate();

        adapterUnderTest.storeOne(reportItem);

        final var storedReportItems = analysisReportItemRepository.findAll();
        assertThat(storedReportItems).hasSize(1);
        assertThat(storedReportItems.getFirst().getOrderId()).isEqualTo(reportItem.getOrderId());
    }

    @Test
    void storeAll_whenCalled_shouldStoreAllReportItems() {
        final AnalysisReportItem reportItem1 = analysisReportItemGenerator.generate();
        final AnalysisReportItem reportItem2 = analysisReportItemGenerator.generate();

        adapterUnderTest.storeAll(List.of(reportItem1, reportItem2));

        final var storedReportItems = analysisReportItemRepository.findAll();
        assertThat(storedReportItems).hasSize(2)
                                     .extracting("orderId")
                                     .containsExactlyInAnyOrder(reportItem1.getOrderId(), reportItem2.getOrderId());
    }

    @Test
    void search_whenOfficeFilterSet_thenReturnMatchingReportItems() {
        final AnalysisReportItem reportItem1 = storageHelper.store(analysisReportItemGenerator.generate(r -> r.office(ControlOffice.NOIC)));
        storageHelper.store(analysisReportItemGenerator.generate(r -> r.office(ControlOffice.CDFC)));

        final var searchInput = AnalysisFilterCriteria.builder()
                                                      .officeFilter(List.of(ControlOffice.NOIC))
                                                      .build();
        final var foundReportItems = adapterUnderTest.search(searchInput);
        assertThat(foundReportItems).satisfiesExactly(reportItem -> assertThat(reportItem.getId()).isEqualTo(reportItem1.getId()));
    }


    @Test
    void search_whenBusinessPartnerFilterSet_thenReturnMatchingReportItems() {
        final AnalysisReportItem reportItem1 = storageHelper.store(analysisReportItemGenerator.generate(r -> r.businessPartner("bp1")));
        storageHelper.store(analysisReportItemGenerator.generate(r -> r.businessPartner("bp2")));

        final var searchInput = AnalysisFilterCriteria.builder()
                                                      .businessPartnerFilter(List.of("bp1"))
                                                      .build();
        final var foundReportItems = adapterUnderTest.search(searchInput);
        assertThat(foundReportItems).satisfiesExactly(reportItem -> assertThat(reportItem.getId()).isEqualTo(reportItem1.getId()));
    }

    @Test
    void search_whenDateAfterFilterSet_thenReturnMatchingReportItems() {
        final AnalysisReportItem reportItem1 = storageHelper.store(analysisReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2025, 2, 26, 10, 20))));
        storageHelper.store(analysisReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2023, 2, 26, 10, 20))));

        final var searchInput = AnalysisFilterCriteria.builder()
                                                      .dateAfterFilter(LocalDateTime.of(2024, 2, 26, 10, 20))
                                                      .build();
        final var foundReportItems = adapterUnderTest.search(searchInput);
        assertThat(foundReportItems).satisfiesExactly(reportItem -> assertThat(reportItem.getId()).isEqualTo(reportItem1.getId()));
    }

    @Test
    void search_whenDateBeforeFilterSet_thenReturnMatchingReportItems() {
        final AnalysisReportItem reportItem1 = storageHelper.store(analysisReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2023, 2, 26, 10, 20))));
        storageHelper.store(analysisReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2025, 2, 26, 10, 20))));

        final var searchInput = AnalysisFilterCriteria.builder()
                                                      .dateBeforeFilter(LocalDateTime.of(2024, 2, 26, 10, 20))
                                                      .build();
        final var foundReportItems = adapterUnderTest.search(searchInput);
        assertThat(foundReportItems).satisfiesExactly(reportItem -> assertThat(reportItem.getId()).isEqualTo(reportItem1.getId()));
    }

    @Test
    void search_whenAllFiltersSet_thenReturnMatchingReportItems() {
        final AnalysisReportItem reportItem1 = storageHelper.store(analysisReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2025, 2, 23, 10, 20))
                                                                                                                .businessPartner("bp1")
                                                                                                                .office(ControlOffice.NOIC)
                                                                                                       ));
        storageHelper.store(analysisReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2025, 2, 26, 10, 21))));
        storageHelper.store(analysisReportItemGenerator.generate(r -> r.date(LocalDateTime.of(2023, 2, 26, 10, 21))));
        storageHelper.store(analysisReportItemGenerator.generate(r -> r.businessPartner("bp33")));
        storageHelper.store(analysisReportItemGenerator.generate(r -> r.office(ControlOffice.ZEMK)));

        final var searchInput = AnalysisFilterCriteria.builder()
                                                      .dateBeforeFilter(LocalDateTime.of(2025, 2, 26, 10, 20))
                                                      .dateAfterFilter(LocalDateTime.of(2024, 2, 26, 10, 20))
                                                      .businessPartnerFilter(List.of("bp1"))
                                                      .officeFilter(List.of(ControlOffice.NOIC))
                                                      .build();
        final var foundReportItems = adapterUnderTest.search(searchInput);
        assertThat(foundReportItems).satisfiesExactly(reportItem -> assertThat(reportItem.getId()).isEqualTo(reportItem1.getId()));
    }
}
