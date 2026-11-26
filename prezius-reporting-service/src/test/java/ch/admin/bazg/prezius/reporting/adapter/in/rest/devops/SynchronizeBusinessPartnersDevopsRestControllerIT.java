package ch.admin.bazg.prezius.reporting.adapter.in.rest.devops;

import ch.admin.bazg.prezius.reporting.application.ports.in.SynchronizeBusinessPartnersUseCase;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import ch.admin.bit.jeap.security.resource.semanticAuthentication.SemanticApplicationRole;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationToken;
import ch.admin.bit.jeap.security.test.resource.JeapAuthenticationTestTokenBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class SynchronizeBusinessPartnersDevopsRestControllerIT extends ITBase {

    private static final String URL = "/api/devops/synchronize-business-partners";
    private static final SemanticApplicationRole PREZIUS_ERRORHANDLING_RETRY = SemanticApplicationRole.builder()
                                                                                                      .system("prezius")
                                                                                                      .resource("errorhandling")
                                                                                                      .operation("retry")
                                                                                                      .build();

    private final MockMvc mockMvc;

    @MockitoBean
    private SynchronizeBusinessPartnersUseCase synchronizeBusinessPartnersUseCase;

    @Test
    void synchronizeBusinessPartners_whenReceiveRestCall_thenCallUseCase() {
        final SynchronizeBusinessPartnersDevopsRestController controllerUnderTest = new SynchronizeBusinessPartnersDevopsRestController(synchronizeBusinessPartnersUseCase);
        controllerUnderTest.synchronizeBusinessPartners();

        verify(synchronizeBusinessPartnersUseCase).synchronizeAll();
    }

    @Test
    void synchronizeBusinessPartners_whenRoleIsErrorHandling_thenReturnOk() {
        final JeapAuthenticationToken authentication = JeapAuthenticationTestTokenBuilder.create().withUserRoles(PREZIUS_ERRORHANDLING_RETRY).build();

        assertDoesNotThrow(() -> {
            this.mockMvc.perform(put(URL).with(authentication(authentication)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        });
    }

    @Test
    void synchronizeBusinessPartners_whenRoleIsNotErrorHandling_thenReturnForbidden() {
        final JeapAuthenticationToken authentication = JeapAuthenticationTestTokenBuilder.create().build();

        assertDoesNotThrow(() -> {
            this.mockMvc.perform(put(URL).with(authentication(authentication)))
                        .andExpect(status().isForbidden())
                        .andReturn().getResponse();
        });
    }
}
