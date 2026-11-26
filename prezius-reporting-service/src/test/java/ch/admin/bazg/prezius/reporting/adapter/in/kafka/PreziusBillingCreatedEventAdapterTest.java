package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.BillingType;
import ch.admin.bazg.prezius.PreziusBillingCreatedEvent;
import ch.admin.bazg.prezius.reporting.application.ports.in.CreateReportItemUseCase;
import ch.admin.bazg.prezius.reporting.common.generators.event.PreziusBillingCreatedEventGenerator;
import ch.admin.bit.jeap.messaging.avro.security.AvroClassSecurity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PreziusBillingCreatedEventAdapterTest {

    private final PreziusBillingCreatedEventGenerator generator = new PreziusBillingCreatedEventGenerator();

    @InjectMocks
    private PreziusBillingCreatedEventAdapter adapterUnderTest;

    @Mock
    private CreateReportItemUseCase createReportItemUseCase;

    @BeforeAll
    static void installAvroClassWhitelist() {
        AvroClassSecurity.installDefaultIfMissing();
    }

    @Test
    void whenReceiveEvent_thenCallUseCase() {
        final UUID billableId = UUID.randomUUID();
        final String billableBusinessId = "12345";
        final BillingType billingType = BillingType.HALLMARKING_ORDER;
        final PreziusBillingCreatedEvent event = generator.generate(billableId, billableBusinessId, billingType);

        adapterUnderTest.process(event);

        verify(createReportItemUseCase).process(billableId, billableBusinessId, CreateReportItemUseCase.BillingType.HALLMARKING_ORDER, true);
    }

    @Test
    void whenReceiveManualEventWithoutBusinessPartner_thenCallUseCase() {
        final UUID billableId = UUID.randomUUID();
        final String billableBusinessId = "12345";
        final PreziusBillingCreatedEvent event = generator.generate(billableId, billableBusinessId, BillingType.MANUAL, null);

        adapterUnderTest.process(event);

        verify(createReportItemUseCase).process(billableId, billableBusinessId, CreateReportItemUseCase.BillingType.MANUAL, true);
    }

    @ParameterizedTest
    @EnumSource(BillingType.class)
    void whenReceiveEvent_thenAllBillingTypesAreConsumedCorrectly(final BillingType billingType) {
        final UUID billableId = UUID.randomUUID();
        final String billableBusinessId = "12345";
        final PreziusBillingCreatedEvent event = generator.generate(billableId, billableBusinessId, billingType);

        adapterUnderTest.process(event);

        verify(createReportItemUseCase).process(billableId, billableBusinessId, CreateReportItemUseCase.BillingType.valueOf(billingType.name()), true);
    }
}
