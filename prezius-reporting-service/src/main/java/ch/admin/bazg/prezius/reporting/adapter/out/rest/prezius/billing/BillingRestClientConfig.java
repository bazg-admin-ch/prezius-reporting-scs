package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.billing;

import ch.admin.bit.jeap.security.restclient.JeapOAuth2RestClientBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
class BillingRestClientConfig {

    private final BillingRestProperties billingRestProperties;
    private final JeapOAuth2RestClientBuilderFactory restClientBuilderFactory;

    @Bean
    @Profile("!mock-restclient")
    public RestClient preziusBillingRestClient() {
        final String clientId = billingRestProperties.clientId();
        final String baseUrl = billingRestProperties.apiUrl();
        final var requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        return restClientBuilderFactory
                .createForClientRegistryId(clientId)
                .requestFactory(requestFactory)
                .baseUrl(baseUrl)
                .build();
    }
}
