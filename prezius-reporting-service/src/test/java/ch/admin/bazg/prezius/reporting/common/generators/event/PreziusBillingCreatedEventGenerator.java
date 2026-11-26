package ch.admin.bazg.prezius.reporting.common.generators.event;

import ch.admin.bazg.prezius.BillingCreatedBusinessPartnerReference;
import ch.admin.bazg.prezius.BillingCreatedInvoiceReference;
import ch.admin.bazg.prezius.BillingCreatedOrderReference;
import ch.admin.bazg.prezius.BillingType;
import ch.admin.bazg.prezius.PreziusBillingCreatedEvent;
import ch.admin.bazg.prezius.PreziusBillingCreatedEventReferences;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventIdentity;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventPublisher;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PreziusBillingCreatedEventGenerator {

    public PreziusBillingCreatedEvent generate(final UUID billableId, final String billableBusinessId, final BillingType billingType) {
        return generate(billableId, billableBusinessId, billingType, "54321");
    }

    public PreziusBillingCreatedEvent generate(final UUID billableId, final String billableBusinessId, final BillingType billingType, final String businessPartnerId) {
        return PreziusBillingCreatedEvent
                .newBuilder()
                .setIdentity(AvroDomainEventIdentity
                        .newBuilder()
                        .setEventId("12345678")
                        .setIdempotenceId("abcdefgh")
                        .setCreated(Instant.now())
                        .build())
                .setType(AvroDomainEventType
                        .newBuilder()
                        .setName("PreziusBillingCreatedEvent")
                        .setVersion("1.0.0")
                        .build())
                .setPublisher(AvroDomainEventPublisher
                        .newBuilder()
                        .setSystem("Dazit")
                        .setService("Prezius")
                        .build())
                .setReferences(PreziusBillingCreatedEventReferences
                        .newBuilder()
                        .setOrderReference(BillingCreatedOrderReference
                                .newBuilder()
                                .setBillableId(billableId.toString())
                                .setBillableBusinessId(billableBusinessId)
                                .setType(billingType.name())
                                .setBillingType(billingType)
                                .build())
                        .setBusinessPartnerReference(BillingCreatedBusinessPartnerReference
                                .newBuilder()
                                .setType("BusinessPartnerReference")
                                .setBusinessPartnerId(businessPartnerId)
                                .setControlOffice("GDPC")
                                .build())
                        .setInvoiceReference(BillingCreatedInvoiceReference
                                .newBuilder()
                                .setType("InvoiceReference")
                                .setInvoiceId("98765")
                                .build())
                        .build())
                .setDomainEventVersion("1.0.0")
                .build();
    }
}
