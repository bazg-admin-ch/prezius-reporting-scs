package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.event.PreziusInvoicingNotRequiredEvent;
import ch.admin.bazg.prezius.reporting.application.ports.in.CreateReportItemUseCase;
import ch.admin.bazg.prezius.reporting.common.generators.event.PreziusInvoicingNotRequiredEventGenerator;
import ch.admin.bit.jeap.messaging.avro.security.AvroClassSecurity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PreziusInvoicingNotRequiredEventAdapterTest {

    private final PreziusInvoicingNotRequiredEventGenerator generator = new PreziusInvoicingNotRequiredEventGenerator();

    @InjectMocks
    private PreziusInvoicingNotRequiredEventAdapter adapterUnderTest;

    @Mock
    private CreateReportItemUseCase createReportItemUseCase;

    @BeforeAll
    static void installAvroClassWhitelist() {
        AvroClassSecurity.installDefaultIfMissing();
    }

    @Test
    void whenReceiveEvent_thenCallUseCase() {
        final UUID orderId = UUID.randomUUID();
        final String orderBusinessId = "12345";
        final PreziusInvoicingNotRequiredEvent event = generator.generate(orderId, orderBusinessId);

        adapterUnderTest.process(event);

        verify(createReportItemUseCase).process(orderId, orderBusinessId, CreateReportItemUseCase.BillingType.ANALYSIS_ORDER, false);
    }
}
