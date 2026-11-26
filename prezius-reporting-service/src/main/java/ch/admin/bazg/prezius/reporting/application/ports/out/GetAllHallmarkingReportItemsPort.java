package ch.admin.bazg.prezius.reporting.application.ports.out;

import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;

import java.util.List;
import java.util.UUID;

public interface GetAllHallmarkingReportItemsPort {
    List<HallmarkingReportItem> getAllByOrderId(final UUID orderId);
}
