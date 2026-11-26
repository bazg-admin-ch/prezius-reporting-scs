package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.event.PreziusInvoicingNotRequiredEvent;
import ch.admin.bazg.prezius.reporting.application.ports.in.CreateReportItemUseCase;
import ch.admin.bit.jeap.messaging.idempotence.messagehandler.IdempotentMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PreziusInvoicingNotRequiredEventAdapter {

    private final CreateReportItemUseCase createReportItemUseCase;

    @Transactional
    @IdempotentMessageHandler
    public void process(final PreziusInvoicingNotRequiredEvent event) {
        log.info("Processing PreziusInvoicingNotRequiredEvent: {}.", event.getIdentity().getId());
        final UUID orderId = UUID.fromString(event.getReferences().getOrderReference().getOrderId());
        final String orderBusinessId = event.getReferences().getOrderReference().getOrderBusinessId();
        final OrderType orderType = OrderType.valueOf(event.getReferences().getOrderReference().getType());
        final var billingType = switch (orderType) {
            case HALLMARKING -> CreateReportItemUseCase.BillingType.HALLMARKING_ORDER;
            case ANALYSIS -> CreateReportItemUseCase.BillingType.ANALYSIS_ORDER;
        };
        createReportItemUseCase.process(orderId, orderBusinessId, billingType, false);
    }
}
