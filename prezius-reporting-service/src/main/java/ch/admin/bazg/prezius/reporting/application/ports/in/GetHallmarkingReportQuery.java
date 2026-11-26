package ch.admin.bazg.prezius.reporting.application.ports.in;

import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;

import java.util.List;

public interface GetHallmarkingReportQuery {

    List<HallmarkingReportItem> getHallmarkingReportItems(HallmarkingFilterCriteria filterCriteria);

}
