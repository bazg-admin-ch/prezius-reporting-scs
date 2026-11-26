package ch.admin.bazg.prezius.reporting.adapter.out.jpa;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.reporting.application.ports.out.SearchAnalysisReportItemsPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.StoreAnalysisReportItemPort;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import ch.admin.bazg.prezius.reporting.domain.ReportItem_;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ch.admin.bit.jeap.db.tx.TransactionalReadReplica;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
class AnalysisReportItemJpaAdapter implements StoreAnalysisReportItemPort, SearchAnalysisReportItemsPort {

    private final AnalysisReportItemRepository analysisReportItemRepository;

    @Override
    public void storeOne(final AnalysisReportItem reportItem) {
        analysisReportItemRepository.save(reportItem);
    }

    @Override
    public void storeAll(final List<AnalysisReportItem> reportItems) {
        analysisReportItemRepository.saveAll(reportItems);
    }

    @Override
    @TransactionalReadReplica
    public List<AnalysisReportItem> search(final AnalysisFilterCriteria filterCriteria) {
        return analysisReportItemRepository.findAll(filterSpecification(filterCriteria));
    }

    private Specification<AnalysisReportItem> filterSpecification(final AnalysisFilterCriteria filterCriteria) {
        final List<Specification<AnalysisReportItem>> specifications = new ArrayList<>();
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
        return Specification.allOf(specifications);
    }

    private Specification<AnalysisReportItem> businessPartnerFilterSpecification(final List<String> businessPartners) {
        if (businessPartners == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.get(ReportItem_.businessPartner).in(businessPartners);
    }

    private Specification<AnalysisReportItem> dateBeforeFilterSpecification(final LocalDateTime endDateTime) {
        if (endDateTime == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(ReportItem_.date), endDateTime);
    }

    private Specification<AnalysisReportItem> dateAfterFilterSpecification(final LocalDateTime startDateTime) {
        if (startDateTime == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(ReportItem_.date), startDateTime);
    }

    private Specification<AnalysisReportItem> officeFilterSpecification(final List<ControlOffice> controlOffices) {
        if (controlOffices == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.get(ReportItem_.office).in(controlOffices);
    }
}
