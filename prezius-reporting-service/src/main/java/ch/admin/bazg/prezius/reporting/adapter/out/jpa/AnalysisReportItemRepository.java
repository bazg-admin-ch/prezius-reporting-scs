package ch.admin.bazg.prezius.reporting.adapter.out.jpa;

import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalysisReportItemRepository extends JpaRepository<AnalysisReportItem, UUID> {

    List<AnalysisReportItem> findAll(Specification<AnalysisReportItem> specification);

}
