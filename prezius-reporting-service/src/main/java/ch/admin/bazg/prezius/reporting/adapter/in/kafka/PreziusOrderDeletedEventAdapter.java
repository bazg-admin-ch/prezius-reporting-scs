package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.PreziusOrderDeletedEvent;
import ch.admin.bazg.prezius.reporting.application.ports.in.DeleteReportItemsUseCase;
import ch.admin.bit.jeap.messaging.idempotence.messagehandler.IdempotentMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
class PreziusOrderDeletedEventAdapter {

    private final DeleteReportItemsUseCase deleteReportItemsUseCase;

    @Transactional
    @IdempotentMessageHandler
    public void process(final PreziusOrderDeletedEvent event) {
        log.info("Processing PreziusOrderDeletedEvent: {}.", event.getIdentity().getId());
        deleteReportItemsUseCase.process(event.getReferences().getOrderReference().getOrderId());
    }
}
