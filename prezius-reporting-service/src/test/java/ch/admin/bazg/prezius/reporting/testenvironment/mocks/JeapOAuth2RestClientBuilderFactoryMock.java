package ch.admin.bazg.prezius.reporting.testenvironment.mocks;

import ch.admin.bit.jeap.security.restclient.JeapOAuth2RestClientBuilderFactory;
import org.springframework.web.client.RestClient;

public class JeapOAuth2RestClientBuilderFactoryMock implements JeapOAuth2RestClientBuilderFactory {

    @Override
    public RestClient.Builder createForClientRegistryId(final String clientId) {
        return RestClient.builder();
    }

    @Override
    public RestClient.Builder createForClientRegistryIdPreferringTokenFromIncomingRequest(final String clientId) {
        return RestClient.builder();
    }

    @Override
    public RestClient.Builder createForTokenFromIncomingRequest() {
        return RestClient.builder();
    }
}
