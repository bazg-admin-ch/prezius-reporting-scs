package ch.admin.bazg.prezius.reporting.common.generators.event;

import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.event.OrderReference;
import ch.admin.bazg.prezius.event.PreziusInvoicingNotRequiredEvent;
import ch.admin.bazg.prezius.event.PreziusInvoicingNotRequiredEventReferences;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventIdentity;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventPublisher;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;


@Component
public class PreziusInvoicingNotRequiredEventGenerator {

    public PreziusInvoicingNotRequiredEvent generate(final UUID orderId, final String orderBusinessId) {
        return PreziusInvoicingNotRequiredEvent
                .newBuilder()
                .setIdentity(AvroDomainEventIdentity
                        .newBuilder()
                        .setEventId("12345678")
                        .setIdempotenceId("abcdefgh")
                        .setCreated(Instant.now())
                        .build())
                .setType(AvroDomainEventType
                        .newBuilder()
                        .setName("PreziusInvoicingNotRequiredEvent")
                        .setVersion("1.0.0")
                        .build())
                .setPublisher(AvroDomainEventPublisher
                        .newBuilder()
                        .setSystem("Dazit")
                        .setService("Prezius")
                        .build())
                .setReferences(PreziusInvoicingNotRequiredEventReferences
                        .newBuilder()
                        .setOrderReference(OrderReference
                                .newBuilder()
                                .setOrderId(orderId.toString())
                                .setOrderBusinessId(orderBusinessId)
                                .setType(OrderType.ANALYSIS.name())
                                .build())
                        .build())
                .setDomainEventVersion("1.0.1")
                .build();
    }
}
