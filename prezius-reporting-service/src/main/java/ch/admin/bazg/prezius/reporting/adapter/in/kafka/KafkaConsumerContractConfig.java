package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.PreziusBillingCreatedEvent;
import ch.admin.bazg.prezius.PreziusOrderDeletedEvent;
import ch.admin.bazg.prezius.event.PreziusInvoicingNotRequiredEvent;
import ch.admin.bit.jeap.messaging.annotations.JeapMessageConsumerContracts;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.DatahubBusinesspartnerCreatedEvent;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.DatahubBusinesspartnerUpdatedEvent;
import org.springframework.context.annotation.Configuration;

@JeapMessageConsumerContracts({
        PreziusBillingCreatedEvent.TypeRef.class,
        PreziusOrderDeletedEvent.TypeRef.class,
        DatahubBusinesspartnerCreatedEvent.TypeRef.class,
        DatahubBusinesspartnerUpdatedEvent.TypeRef.class,
        PreziusInvoicingNotRequiredEvent.TypeRef.class
})
@Configuration
class KafkaConsumerContractConfig {
}
