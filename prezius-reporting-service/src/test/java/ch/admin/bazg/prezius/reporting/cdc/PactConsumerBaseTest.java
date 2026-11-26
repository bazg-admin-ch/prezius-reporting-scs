package ch.admin.bazg.prezius.reporting.cdc;

import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import ch.admin.bit.jeap.security.test.client.configuration.JeapOAuth2IntegrationTestClientConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_JOB_SCHEDULERS;
import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.MOCK_MESSAGESENDER;
import static ch.admin.bazg.prezius.reporting.testenvironment.mocks.MockProfiles.TEST;


@ActiveProfiles({TEST, MOCK_MESSAGESENDER, MOCK_JOB_SCHEDULERS})
@SpringBootTest(
        properties = {
                "logging.level.au.com.dius.pact=DEBUG"
        }
)
@Import({JeapOAuth2IntegrationTestClientConfiguration.class})
@PactConsumerTest
@MockServerConfig(hostInterface = "localhost", port = "8888")
public abstract class PactConsumerBaseTest {

        public static final String CONSUMER = "bazg-prezius-reporting-scs";

}
