package ch.admin.bazg.prezius.reporting.common.generators.event;

import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventIdentity;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventPublisher;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventType;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.BusinesspartnerCreatedReferences;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.BusinesspartnerIdReference;
import ch.admin.ezv.datahub.businesspartner.infrastructure.kafka.event.DatahubBusinesspartnerCreatedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DatahubBusinessPartnerCreatedEventGenerator {

    public DatahubBusinesspartnerCreatedEvent generate(final String businessPartnerId) {
        final DatahubBusinesspartnerCreatedEvent event = DatahubBusinesspartnerCreatedEvent
                .newBuilder()
                .setIdentity(AvroDomainEventIdentity
                                     .newBuilder()
                                     .setEventId("12345678")
                                     .setIdempotenceId("abcdefgh")
                                     .setCreated(Instant.now())
                                     .build())
                .setType(AvroDomainEventType
                                 .newBuilder()
                                 .setName("DatahubBusinesspartnerCreatedEvent")
                                 .setVersion("1.0.1")
                                 .build())
                .setPublisher(AvroDomainEventPublisher
                                      .newBuilder()
                                      .setSystem("Dazit")
                                      .setService("Prezius")
                                      .build())
                .setReferences(BusinesspartnerCreatedReferences
                                       .newBuilder()
                                       .setBusinesspartnerId(BusinesspartnerIdReference.newBuilder().setId(businessPartnerId).build())
                                       .build())
                .setDomainEventVersion("1.0.1")
                .build();

        return event;
    }
}
