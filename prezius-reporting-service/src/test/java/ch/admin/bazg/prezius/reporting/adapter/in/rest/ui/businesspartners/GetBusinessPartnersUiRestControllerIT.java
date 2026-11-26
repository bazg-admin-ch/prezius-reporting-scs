package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.businesspartners;

import ch.admin.bazg.prezius.common.authorization.AuthorizationInfo;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetBusinessPartnersQuery;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.BusinessPartnerValueObjectGenerator;
import ch.admin.bit.jeap.security.resource.semanticAuthentication.SemanticApplicationRole;
import ch.admin.bit.jeap.security.test.resource.JeapAuthenticationTestTokenBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class GetBusinessPartnersUiRestControllerIT extends ITBase {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetBusinessPartnersQuery getBusinessPartnersQuery;

    private final BusinessPartnerValueObjectGenerator businessPartnerValueObjectGenerator = new BusinessPartnerValueObjectGenerator();

    private static final String API_URL = "/api/ui/businesspartners";

    private static final SemanticApplicationRole REPORTING_READ_ROLE = SemanticApplicationRole.builder()
                                                                                              .system("prezius")
                                                                                              .resource("reporting")
                                                                                              .operation("read")
                                                                                              .build();

    @Test
    void whenValidRequest_thenReturnOk() {
        final var authentication = JeapAuthenticationTestTokenBuilder.create().withUserRoles(REPORTING_READ_ROLE).build();
        final var businessPartner = businessPartnerValueObjectGenerator.generate();
        when(this.getBusinessPartnersQuery.getBusinessPartners(any())).thenReturn(List.of(businessPartner));

        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get(API_URL).with(authentication(authentication)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();
        });

        verify(this.getBusinessPartnersQuery).getBusinessPartners(AuthorizationInfo.createCanAccessAll());
    }

    @Test
    void whenWrongRole_thenReturnForbidden() {
        final var authentication = JeapAuthenticationTestTokenBuilder.create()
                                                                     .withUserRoles(SemanticApplicationRole
                                                                                            .builder()
                                                                                            .system("prezius")
                                                                                            .resource("reporting")
                                                                                            .operation("test")
                                                                                            .build())
                                                                     .build();
        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get(API_URL).with(authentication(authentication)))
                        .andExpect(status().isForbidden());
        });
    }
}
