package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.PreziusBillingCreatedEvent;
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
class PreziusBillingCreatedEventAdapter {

    private final CreateReportItemUseCase createReportItemUseCase;

    @Transactional
    @IdempotentMessageHandler
    public void process(final PreziusBillingCreatedEvent event) {
        log.info("Processing PreziusBillingCreatedEvent: {}.", event.getIdentity().getId());
        final UUID billableId = UUID.fromString(event.getReferences().getOrderReference().getBillableId());
        final String billableBusinessId = event.getReferences().getOrderReference().getBillableBusinessId();
        final var billingType = CreateReportItemUseCase.BillingType.valueOf(event.getReferences().getOrderReference().getBillingType().name());
        createReportItemUseCase.process(billableId, billableBusinessId, billingType, true);
    }
}
