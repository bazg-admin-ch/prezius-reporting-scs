package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.reporting.application.ports.in.SynchronizeBusinessPartnersUseCase;
import ch.admin.bit.jeap.messaging.idempotence.messagehandler.IdempotentMessageHandler;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.DatahubBusinesspartnerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class DatahubBusinesspartnerCreatedEventAdapter {

    private final SynchronizeBusinessPartnersUseCase synchronizeBusinessPartnersUseCase;

    @Transactional
    @IdempotentMessageHandler
    public void process(final DatahubBusinesspartnerCreatedEvent event) {
        synchronizeBusinessPartnersUseCase.synchronizeOne(event.getReferences().getBusinesspartnerId().getId());
    }
}
