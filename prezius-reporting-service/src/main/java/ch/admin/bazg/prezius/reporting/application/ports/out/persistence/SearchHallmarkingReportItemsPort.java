package ch.admin.bazg.prezius.reporting.application.ports.out.persistence;

import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;

import java.util.List;

public interface SearchHallmarkingReportItemsPort {

    List<HallmarkingReportItem> search(HallmarkingFilterCriteria filterCriteria);
}
