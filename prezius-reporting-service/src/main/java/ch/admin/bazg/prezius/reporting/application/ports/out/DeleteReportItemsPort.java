package ch.admin.bazg.prezius.reporting.application.ports.out;

import java.util.UUID;

public interface DeleteReportItemsPort {
    void deleteAllByOrderId(final UUID orderId);
}
