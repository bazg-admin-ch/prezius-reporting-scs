package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.urls;

import ch.admin.bazg.prezius.reporting.adapter.in.rest.TestFileUtils;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationToken;
import ch.admin.bit.jeap.security.test.resource.JeapAuthenticationTestTokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class GetServiceUrlsUiRestControllerIT extends ITBase {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/ui-api/service-urls";

    @Test
    void getServiceUrls_whenRequestReceived_thenReturnStatusOk() {
        final JeapAuthenticationToken authentication = JeapAuthenticationTestTokenBuilder.create().build();

        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get(BASE_URL).with(authentication(authentication)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        });
    }

    @Test
    void getServiceUrls_whenRequestReceived_thenReturnExpectedServiceUrls() {
        final String expectedResponseBody = TestFileUtils.readResponseBody("get-service-urls.json");
        final JeapAuthenticationToken authentication = JeapAuthenticationTestTokenBuilder.create().build();

        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get(BASE_URL).with(authentication(authentication)))
                        .andExpect(status().isOk())
                        .andExpect(content().json(expectedResponseBody))
                        .andReturn().getResponse();
        });
    }
}
