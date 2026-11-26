package ch.admin.bazg.prezius.reporting.common.generators.event;

import ch.admin.bazg.prezius.PreziusOrderDeletedEvent;
import ch.admin.bazg.prezius.PreziusOrderDeletedReferences;
import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.v2.OrderReference;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventIdentity;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventPublisher;
import ch.admin.bit.jeap.domainevent.avro.AvroDomainEventType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PreziusOrderDeletedEventGenerator {

    public PreziusOrderDeletedEvent generate(final UUID orderId, final String orderBusinessId, final OrderType orderType) {
        return PreziusOrderDeletedEvent
                .newBuilder()
                .setIdentity(AvroDomainEventIdentity
                        .newBuilder()
                        .setEventId("12345678")
                        .setIdempotenceId("abcdefgh")
                        .setCreated(Instant.now())
                        .build())
                .setType(AvroDomainEventType
                        .newBuilder()
                        .setName("PreziusOrderDeletedEvent")
                        .setVersion("1.0.0")
                        .build())
                .setPublisher(AvroDomainEventPublisher
                        .newBuilder()
                        .setSystem("Dazit")
                        .setService("Prezius")
                        .build())
                .setReferences(PreziusOrderDeletedReferences
                        .newBuilder()
                        .setOrderReference(OrderReference
                                .newBuilder()
                                .setOrderId(orderId.toString())
                                .setOrderBusinessId(orderBusinessId)
                                .setType(orderType.name())
                                .build())
                        .build())
                .setDomainEventVersion("1.0.0")
                .build();
    }
}
