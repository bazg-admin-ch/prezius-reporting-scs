package ch.admin.bazg.prezius.reporting.application.ports.out;

import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;

import java.util.List;

public interface StoreHallmarkingReportItemPort {

    void storeAll(final List<HallmarkingReportItem> reportItems);
}
