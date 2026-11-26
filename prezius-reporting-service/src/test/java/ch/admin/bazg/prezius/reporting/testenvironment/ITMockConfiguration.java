package ch.admin.bazg.prezius.reporting.testenvironment;

import ch.admin.bazg.prezius.reporting.ReportingServiceApplication;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.StorageHelper;
import ch.admin.bazg.prezius.reporting.testenvironment.mocks.JeapOAuth2RestClientBuilderFactoryMock;
import ch.admin.bit.jeap.messaging.kafka.KafkaConfiguration;
import ch.admin.bit.jeap.messaging.kafka.KafkaConsumerConfiguration;
import ch.admin.bit.jeap.messaging.kafka.serde.confluent.config.ConfluentSchemaRegistryAutoConfiguration;
import ch.admin.bit.jeap.messaging.transactionaloutbox.messaging.OutboxMessagingConfig;
import ch.admin.bit.jeap.messaging.transactionaloutbox.outbox.OutboxConfig;
import ch.admin.bit.jeap.messaging.transactionaloutbox.scheduling.OutboxSchedulingConfig;
import ch.admin.bit.jeap.messaging.transactionaloutbox.transaction.OutboxTransactionConfig;
import ch.admin.bit.jeap.security.restclient.JeapOAuth2RestClientBuilderFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_JEAPRESTCLIENT;

@Configuration
@Import(value = {ReportingServiceApplication.class, StorageHelper.class})
@EnableAutoConfiguration(exclude = {
        OutboxSchedulingConfig.class,
        OutboxTransactionConfig.class,
        OutboxMessagingConfig.class,
        OutboxConfig.class,
        ConfluentSchemaRegistryAutoConfiguration.class,
        KafkaAutoConfiguration.class,
        KafkaConfiguration.class,
        KafkaConsumerConfiguration.class
})
public class ITMockConfiguration {

    @Bean
    @Primary
    @Profile(MOCK_JEAPRESTCLIENT)
    public JeapOAuth2RestClientBuilderFactory jeapOAuth2RestClientBuilderFactory() {
        return new JeapOAuth2RestClientBuilderFactoryMock();
    }
}
