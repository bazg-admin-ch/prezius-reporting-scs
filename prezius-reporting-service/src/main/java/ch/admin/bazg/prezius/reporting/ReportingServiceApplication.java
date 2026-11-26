package ch.admin.bazg.prezius.reporting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.resilience.annotation.EnableResilientMethods;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
@EnableResilientMethods
@Slf4j
public class ReportingServiceApplication {
    public static void main(final String[] args) {

        final Environment env = SpringApplication.run(ReportingServiceApplication.class, args).getEnvironment();

        log.info("""
                        
                        ----------------------------------------------------------
                        \t{} is running!
                        \tSwaggerUI:\thttp://localhost:{}{}/swagger-ui.html
                        \tProfile(s):\t{}
                        ----------------------------------------------------------
                        """,
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path"),
                env.getActiveProfiles());
    }
}
