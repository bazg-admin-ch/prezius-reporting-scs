package ch.admin.bazg.prezius.reporting.adapter.in.startup;

import ch.admin.bazg.prezius.reporting.application.ports.in.SynchronizeBusinessPartnersUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile({"local", "dev"})
@Slf4j
@RequiredArgsConstructor
@Component
class ApplicationStartupEventAdapter implements ApplicationListener<ApplicationReadyEvent> {

    private final SynchronizeBusinessPartnersUseCase synchronizeBusinessPartnersUseCase;
    private final ConfigurableApplicationContext ctx;
    private final ExitHandler exitHandler;

    @Transactional
    @Override
    public void onApplicationEvent(@NotNull final ApplicationReadyEvent event) {
        log.info("ApplicationContext started, base data is being synchronized with the configs...");
        try {
            this.synchronizeBusinessPartnersUseCase.synchronizeAll();
        } catch (final Exception e) {
            log.error("Import Business Partner went wrong with the error {}", e);
            log.info("Spring Boot application will shut down");
            exitHandler.exit(SpringApplication.exit(ctx,() -> 0));
        }
    }
}
@Profile({"local", "dev"})
@Component
class ExitHandler {
    public void exit(int status) {
        System.exit(status);
    }
}
