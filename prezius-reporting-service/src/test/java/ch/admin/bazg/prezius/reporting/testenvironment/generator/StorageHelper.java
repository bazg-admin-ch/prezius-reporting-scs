package ch.admin.bazg.prezius.reporting.testenvironment.generator;

import ch.admin.bazg.prezius.reporting.adapter.out.jpa.AnalysisReportItemRepository;
import ch.admin.bazg.prezius.reporting.adapter.out.jpa.BusinessPartnerRepository;
import ch.admin.bazg.prezius.reporting.adapter.out.jpa.HallmarkingReportItemRepository;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class StorageHelper {

    private final HallmarkingReportItemRepository hallmarkingReportItemRepository;
    private final AnalysisReportItemRepository analysisReportItemRepository;
    private final BusinessPartnerRepository businessPartnerRepository;

    @Transactional
    public HallmarkingReportItem store(final HallmarkingReportItem reportItem) {
        if (reportItem == null) {
            return null;
        }

        return hallmarkingReportItemRepository.save(reportItem);
    }

    @Transactional
    public AnalysisReportItem store(final AnalysisReportItem reportItem) {
        if (reportItem == null) {
            return null;
        }

        return analysisReportItemRepository.save(reportItem);
    }

    public BusinessPartner store(final BusinessPartner businessPartner) {
        return businessPartnerRepository.save(businessPartner);
    }
}

