package ch.admin.bazg.prezius.reporting.application.ports.out;

import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisOrder;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingOrder;

import java.util.UUID;

public interface GetOrderPort {

    HallmarkingOrder getHallmarkingOrderById(UUID orderId);
    AnalysisOrder getAnalysisOrderById(UUID orderId);
}
