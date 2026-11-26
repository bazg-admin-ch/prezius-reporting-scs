package ch.admin.bazg.prezius.reporting.adapter.out.jpa;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.application.ports.out.DeleteReportItemsPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetAllHallmarkingReportItemsPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.StoreHallmarkingReportItemPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.persistence.SearchHallmarkingReportItemsPort;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem_;
import ch.admin.bazg.prezius.reporting.domain.ReportItem_;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ch.admin.bit.jeap.db.tx.TransactionalReadReplica;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
class HallmarkingReportItemJpaAdapter implements DeleteReportItemsPort, GetAllHallmarkingReportItemsPort, StoreHallmarkingReportItemPort, SearchHallmarkingReportItemsPort {

    private final HallmarkingReportItemRepository hallmarkingReportItemRepository;

    @Override
    public void deleteAllByOrderId(final UUID orderId) {
        this.hallmarkingReportItemRepository.deleteAllByOrderId(orderId);
    }

    @Override
    @TransactionalReadReplica
    public List<HallmarkingReportItem> getAllByOrderId(final UUID orderId) {
        return this.hallmarkingReportItemRepository.findAllByOrderId(orderId);
    }

    @Override
    public void storeAll(final List<HallmarkingReportItem> reportItems) {
        this.hallmarkingReportItemRepository.saveAll(reportItems);
    }

    @Override
    @TransactionalReadReplica
    public List<HallmarkingReportItem> search(final HallmarkingFilterCriteria filterCriteria) {
        return hallmarkingReportItemRepository.findAll(createFilterSpecification(filterCriteria));
    }

    private Specification<HallmarkingReportItem> createFilterSpecification(final HallmarkingFilterCriteria filterCriteria) {
        final List<Specification<HallmarkingReportItem>> specifications = new ArrayList<>();
        if (filterCriteria.officeFilter() != null) {
            specifications.add(officeFilterSpecification(filterCriteria.officeFilter()));
        }
        if (filterCriteria.dateAfterFilter() != null) {
            specifications.add(dateAfterFilterSpecification(filterCriteria.dateAfterFilter()));
        }
        if (filterCriteria.dateBeforeFilter() != null) {
            specifications.add(dateBeforeFilterSpecification(filterCriteria.dateBeforeFilter()));
        }
        if (filterCriteria.businessPartnerFilter() != null) {
            specifications.add(businessPartnerFilterSpecification(filterCriteria.businessPartnerFilter()));
        }
        if (filterCriteria.hallmarkingTypes() != null) {
            specifications.add(hallmarkingTypeFilterSpecification(filterCriteria.hallmarkingTypes()));
        }
        return Specification.allOf(specifications);
    }

    private Specification<HallmarkingReportItem> businessPartnerFilterSpecification(final List<String> businessPartners) {
        if (businessPartners == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.get(ReportItem_.businessPartner).in(businessPartners);
    }

    private Specification<HallmarkingReportItem> dateBeforeFilterSpecification(final LocalDateTime endDateTime) {
        if (endDateTime == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(ReportItem_.date), endDateTime);
    }

    private Specification<HallmarkingReportItem> dateAfterFilterSpecification(final LocalDateTime startDateTime) {
        if (startDateTime == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(ReportItem_.date), startDateTime);
    }

    private Specification<HallmarkingReportItem> officeFilterSpecification(final List<ControlOffice> controlOffices) {
        if (controlOffices == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.get(ReportItem_.office).in(controlOffices);
    }

    private Specification<HallmarkingReportItem> hallmarkingTypeFilterSpecification(final List<HallmarkingType> hallmarkingTypes) {
        if (hallmarkingTypes == null) {
            return null;
        }

        final var hallmarkingTypeFilter = hallmarkingTypes.size() == 2 ? List.of(HallmarkingType.DOUBLE) : hallmarkingTypes;
        return (root, query, criteriaBuilder) -> root.get(HallmarkingReportItem_.hallmarkingType).in(hallmarkingTypeFilter);
    }
}
