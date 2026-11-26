package ch.admin.bazg.prezius.reporting.application.ports.out;

import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisFilterCriteria;

import java.util.List;

public interface SearchAnalysisReportItemsPort {

    List<AnalysisReportItem> search(AnalysisFilterCriteria filterCriteria);
}
