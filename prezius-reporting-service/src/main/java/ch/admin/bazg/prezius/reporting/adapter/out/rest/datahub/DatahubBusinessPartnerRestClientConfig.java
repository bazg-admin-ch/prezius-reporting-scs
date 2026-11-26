package ch.admin.bazg.prezius.reporting.adapter.out.rest.datahub;

import ch.admin.bit.jeap.security.restclient.JeapOAuth2RestClientBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
class DatahubBusinessPartnerRestClientConfig {

    private final DatahubBusinessPartnerRestProperties datahubBusinessPartnerRestProperties;
    private final JeapOAuth2RestClientBuilderFactory restClientBuilderFactory;

    @Bean
    @Qualifier
    @Profile("!datahub-mock")
    public RestClient datahubBusinessPartnerRestClient() {
        final String clientId = datahubBusinessPartnerRestProperties.clientId();
        final String baseUrl = this.datahubBusinessPartnerRestProperties.apiUrl();
        final var requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        return restClientBuilderFactory
                .createForClientRegistryId(clientId)
                .requestFactory(requestFactory)
                .baseUrl(baseUrl)
                .build();
    }
}
