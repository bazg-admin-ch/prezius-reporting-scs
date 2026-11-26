package ch.admin.bazg.prezius.reporting.adapter.in.kafka;

import ch.admin.bazg.prezius.reporting.application.ports.in.SynchronizeBusinessPartnersUseCase;
import ch.admin.bazg.prezius.reporting.common.generators.event.DatahubBusinessPartnerCreatedEventGenerator;
import ch.admin.bit.jeap.messaging.avro.security.AvroClassSecurity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DatahubBusinesspartnerCreatedEventAdapterTest {

    private final DatahubBusinessPartnerCreatedEventGenerator generator = new DatahubBusinessPartnerCreatedEventGenerator();
    private final String BUSINESS_PARTNER_ID = UUID.randomUUID().toString();

    @Mock
    private SynchronizeBusinessPartnersUseCase synchronizeBusinessPartnersUseCase;

    @BeforeAll
    static void installAvroClassWhitelist() {
        AvroClassSecurity.installDefaultIfMissing();
    }

    @Test
    void whenReceiveEvent_thenCallUseCase() {
        final DatahubBusinesspartnerCreatedEventAdapter adapterUnderTest = new DatahubBusinesspartnerCreatedEventAdapter(synchronizeBusinessPartnersUseCase);
        adapterUnderTest.process(this.generator.generate(BUSINESS_PARTNER_ID));

        verify(synchronizeBusinessPartnersUseCase).synchronizeOne(BUSINESS_PARTNER_ID);
    }
}
