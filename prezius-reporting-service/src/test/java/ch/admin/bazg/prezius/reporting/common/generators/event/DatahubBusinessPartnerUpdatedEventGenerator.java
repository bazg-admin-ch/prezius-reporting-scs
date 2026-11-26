package ch.admin.bazg.prezius.reporting.common.generators.event;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventIdentity;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventPublisher;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventType;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.BusinesspartnerIdReference;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.BusinesspartnerUpdatedReferences;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.DatahubBusinesspartnerUpdatedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DatahubBusinessPartnerUpdatedEventGenerator {

    public DatahubBusinesspartnerUpdatedEvent generate(final String businessPartnerId) {
        final DatahubBusinesspartnerUpdatedEvent event = DatahubBusinesspartnerUpdatedEvent
                .newBuilder()
                .setIdentity(AvroDomainEventIdentity
                                     .newBuilder()
                                     .setEventId("12345678")
                                     .setIdempotenceId("abcdefgh")
                                     .setCreated(Instant.now())
                                     .build())
                .setType(AvroDomainEventType
                                 .newBuilder()
                                 .setName("DatahubBusinesspartnerUpdatedEvent")
                                 .setVersion("1.0.1")
                                 .build())
                .setPublisher(AvroDomainEventPublisher
                                      .newBuilder()
                                      .setSystem("Dazit")
                                      .setService("Prezius")
                                      .build())
                .setReferences(BusinesspartnerUpdatedReferences
                                       .newBuilder()
                                       .setBusinesspartnerId(BusinesspartnerIdReference.newBuilder().setId(businessPartnerId).build())
                                       .build())
                .setDomainEventVersion("1.0.1")
                .build();

        return event;
    }
}
