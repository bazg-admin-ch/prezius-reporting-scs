package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.BillingType;
import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.common.kafka.consumer.PreziusMessageConsumer;
import ch.admin.bazg.prezius.event.PreziusInvoicingNotRequiredEvent;
import ch.admin.bazg.prezius.reporting.common.generators.event.DatahubBusinessPartnerCreatedEventGenerator;
import ch.admin.bazg.prezius.reporting.common.generators.event.DatahubBusinessPartnerUpdatedEventGenerator;
import ch.admin.bazg.prezius.reporting.common.generators.event.PreziusBillingCreatedEventGenerator;
import ch.admin.bazg.prezius.reporting.common.generators.event.PreziusInvoicingNotRequiredEventGenerator;
import ch.admin.bazg.prezius.reporting.common.generators.event.PreziusOrderDeletedEventGenerator;
import ch.admin.bit.jeap.messaging.avro.AvroMessage;
import ch.admin.bit.jeap.messaging.avro.security.AvroClassSecurity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class KafkaEventConsumerTest {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final String ORDER_BUSINESS_ID = "12345";
    private static final OrderType ORDER_TYPE = OrderType.HALLMARKING;

    @InjectMocks
    private KafkaEventConsumer consumerUnderTest;
    @Mock
    private PreziusBillingCreatedEventAdapter preziusBillingCreatedEventAdapter;
    @Mock
    private PreziusOrderDeletedEventAdapter preziusOrderDeletedEventAdapter;
    @Mock
    private DatahubBusinesspartnerCreatedEventAdapter datahubBusinesspartnerCreatedEventAdapter;
    @Mock
    private DatahubBusinesspartnerUpdatedEventAdapter datahubBusinesspartnerUpdatedEventAdapter;
    @Mock
    private PreziusInvoicingNotRequiredEventAdapter preziusInvoicingNotRequiredEventAdapter;
    @Mock
    private Acknowledgment acknowledgment;
    @Mock
    private PreziusMessageConsumer messageConsumer;

    @BeforeAll
    static void installAvroClassWhitelist() {
        AvroClassSecurity.installDefaultIfMissing();
    }

    @Test
    void whenConsumeBillingCreatedEvent_thenCallAdapter() {
        final var generator = new PreziusBillingCreatedEventGenerator();
        final var event = generator.generate(ORDER_ID, ORDER_BUSINESS_ID, BillingType.HALLMARKING_ORDER);

        consumerUnderTest.consume(event, acknowledgment);

        final ArgumentCaptor<Consumer<AvroMessage>> argumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(messageConsumer).consumeWithErrorHandling(argumentCaptor.capture(), any(), any());
        argumentCaptor.getValue().accept(event);
        verify(preziusBillingCreatedEventAdapter).process(event);
    }

    @Test
    void whenConsumeOrderDeletedEvent_thenCallAdapter() {
        final var generator = new PreziusOrderDeletedEventGenerator();
        final var event = generator.generate(ORDER_ID, ORDER_BUSINESS_ID, ORDER_TYPE);

        consumerUnderTest.consume(event, acknowledgment);

        final ArgumentCaptor<Consumer<AvroMessage>> argumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(messageConsumer).consumeWithErrorHandling(argumentCaptor.capture(), any(), any());
        argumentCaptor.getValue().accept(event);
        verify(preziusOrderDeletedEventAdapter).process(event);
    }

    @Test
    void whenConsumeDatahubBusinesspartnerCreatedEvent_thenCallAdapter() {
        final var generator = new DatahubBusinessPartnerCreatedEventGenerator();
        final var event = generator.generate("12345");

        consumerUnderTest.consume(event, acknowledgment);

        final ArgumentCaptor<Consumer<AvroMessage>> argumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(messageConsumer).consumeWithErrorHandling(argumentCaptor.capture(), any(), any());
        argumentCaptor.getValue().accept(event);
        verify(datahubBusinesspartnerCreatedEventAdapter).process(event);
    }

    @Test
    void whenConsumeDatahubBusinesspartnerUpdatedEvent_thenCallAdapter() {
        final var generator = new DatahubBusinessPartnerUpdatedEventGenerator();
        final var event = generator.generate("12345");

        consumerUnderTest.consume(event, acknowledgment);

        final ArgumentCaptor<Consumer<AvroMessage>> argumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(messageConsumer).consumeWithErrorHandling(argumentCaptor.capture(), any(), any());
        argumentCaptor.getValue().accept(event);
        verify(datahubBusinesspartnerUpdatedEventAdapter).process(event);
    }

    @Test
    void whenConsumePreziusInvoicingNotRequiredEvent_thenCallAdapter() {
        final PreziusInvoicingNotRequiredEventGenerator generator = new PreziusInvoicingNotRequiredEventGenerator();
        final PreziusInvoicingNotRequiredEvent event = generator.generate(UUID.randomUUID(), "orderBusinessId");

        consumerUnderTest.consume(event, acknowledgment);

        final ArgumentCaptor<Consumer<AvroMessage>> argumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(messageConsumer).consumeWithErrorHandling(argumentCaptor.capture(), any(), any());
        argumentCaptor.getValue().accept(event);
        verify(preziusInvoicingNotRequiredEventAdapter).process(event);

    }
}
