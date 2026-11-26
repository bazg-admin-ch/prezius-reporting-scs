package ch.admin.bazg.prezius.reporting.application.ports.out;

import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Billing;

import java.util.UUID;

public interface GetBillingPort {

    Billing getByOrderId(UUID orderId, OrderType orderType);
}
