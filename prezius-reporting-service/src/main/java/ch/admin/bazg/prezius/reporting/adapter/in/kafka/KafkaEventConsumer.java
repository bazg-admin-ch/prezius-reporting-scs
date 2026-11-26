package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.PreziusBillingCreatedEvent;
import ch.admin.bazg.prezius.PreziusOrderDeletedEvent;
import ch.admin.bazg.prezius.common.kafka.consumer.PreziusMessageConsumer;
import ch.admin.bazg.prezius.event.PreziusInvoicingNotRequiredEvent;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.DatahubBusinesspartnerCreatedEvent;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.DatahubBusinesspartnerUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class KafkaEventConsumer {
    private final PreziusBillingCreatedEventAdapter preziusBillingCreatedEventAdapter;
    private final PreziusOrderDeletedEventAdapter preziusOrderDeletedEventAdapter;
    private final DatahubBusinesspartnerCreatedEventAdapter datahubBusinesspartnerCreatedEventAdapter;
    private final DatahubBusinesspartnerUpdatedEventAdapter datahubBusinesspartnerUpdatedEventAdapter;
    private final PreziusInvoicingNotRequiredEventAdapter preziusInvoicingNotRequiredEventAdapter;
    private final PreziusMessageConsumer messageConsumer;

    @KafkaListener(topics = {PreziusBillingCreatedEvent.TypeRef.DEFAULT_TOPIC})
    public void consume(final PreziusBillingCreatedEvent event, final Acknowledgment ack) {
        messageConsumer.consumeWithErrorHandling(e -> preziusBillingCreatedEventAdapter.process(event), event, ack);
    }

    @KafkaListener(topics = {PreziusOrderDeletedEvent.TypeRef.DEFAULT_TOPIC})
    public void consume(final PreziusOrderDeletedEvent event, final Acknowledgment ack) {
        messageConsumer.consumeWithErrorHandling(e -> preziusOrderDeletedEventAdapter.process(event), event, ack);
    }

    @KafkaListener(topics = {DatahubBusinesspartnerCreatedEvent.TypeRef.DEFAULT_TOPIC})
    public void consume(final DatahubBusinesspartnerCreatedEvent event, final Acknowledgment ack) {
        messageConsumer.consumeWithErrorHandling(e -> datahubBusinesspartnerCreatedEventAdapter.process(event), event, ack);
    }

    @KafkaListener(topics = {DatahubBusinesspartnerUpdatedEvent.TypeRef.DEFAULT_TOPIC})
    public void consume(final DatahubBusinesspartnerUpdatedEvent event, final Acknowledgment ack) {
        messageConsumer.consumeWithErrorHandling(e -> datahubBusinesspartnerUpdatedEventAdapter.process(event), event, ack);
    }

    @KafkaListener(topics = {PreziusInvoicingNotRequiredEvent.TypeRef.DEFAULT_TOPIC})
    public void consume(final PreziusInvoicingNotRequiredEvent event, final Acknowledgment ack) {
        messageConsumer.consumeWithErrorHandling(e -> preziusInvoicingNotRequiredEventAdapter.process(event), event, ack);
    }
}
