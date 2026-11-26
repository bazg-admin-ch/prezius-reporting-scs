package ch.admin.bazg.prezius.reporting.adapter.in.startup;

import ch.admin.bazg.prezius.reporting.application.ports.in.SynchronizeBusinessPartnersUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationStartupEventAdapterTest {

    @InjectMocks
    private ApplicationStartupEventAdapter adapterUnderTest;
    @Mock
    private SynchronizeBusinessPartnersUseCase synchronizeBusinessPartnersUseCase;
    @Mock
    private ConfigurableApplicationContext ctx;
    @Mock
    private ExitHandler exitHandler;

    @Test
    void onApplicationEvent_whenReceiveEvent_thenCallSynchronizeAll() {
        adapterUnderTest.onApplicationEvent(Mockito.mock());
        verify(synchronizeBusinessPartnersUseCase, times(1)).synchronizeAll();
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void onApplicationEvent_whenErrorIsThrown_thenShutDown(final CapturedOutput capture) {
        final int exitCode = 0;
        doThrow(new RuntimeException("error")).when(synchronizeBusinessPartnersUseCase).synchronizeAll();

        assertDoesNotThrow(() -> adapterUnderTest.onApplicationEvent(Mockito.mock()));

        verify(exitHandler).exit(exitCode);
        assertThat(capture.getOut()).contains("Spring Boot application will shut down");
    }
}
