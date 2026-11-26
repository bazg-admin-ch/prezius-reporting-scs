package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.reporting.application.ports.in.GetHallmarkingReportQuery;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import ch.admin.bit.jeap.security.resource.semanticAuthentication.SemanticApplicationRole;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationToken;
import ch.admin.bit.jeap.security.test.resource.JeapAuthenticationTestTokenBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class HallmarkingReportControllerAuthorizationIT extends ITBase {

    private static final SemanticApplicationRole REPORTING_READ_ROLE = SemanticApplicationRole.builder()
                                                                                              .system("prezius")
                                                                                              .resource("reporting")
                                                                                              .operation("read")
                                                                                              .build();
    private static final SemanticApplicationRole REPORTING_UPDATE_ROLE = SemanticApplicationRole.builder()
                                                                                              .system("prezius")
                                                                                              .resource("reporting")
                                                                                              .operation("update")
                                                                                              .build();

    private static final SemanticApplicationRole ORDER_UPDATE_ROLE = SemanticApplicationRole.builder()
                                                                                              .system("prezius")
                                                                                              .resource("order")
                                                                                              .operation("update")
                                                                                              .build();

    private static final String BASE_URL = "/api/ui/hallmarking-reports";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GetHallmarkingReportQuery getHallmarkingReportQuery;

    @ParameterizedTest(name = "{2}")
    @MethodSource("provideGetReportArguments")
    void getReport_whenRequestIsOk_thenReturnOk(final JeapAuthenticationToken token, final boolean expectIsAllowed, final String description) {
        when(this.getHallmarkingReportQuery.getHallmarkingReportItems(any())).thenReturn(mock());

        if (expectIsAllowed) {
            assertDoesNotThrow(() -> {
                this.mockMvc.perform(get(BASE_URL)
                                             .with(authentication(token)))
                            .andExpect(status().isOk())
                            .andReturn().getResponse();
            });
            verify(this.getHallmarkingReportQuery).getHallmarkingReportItems(HallmarkingFilterCriteria.builder().build());
        } else {
            assertDoesNotThrow(() -> {
                this.mockMvc.perform(get(BASE_URL).with(authentication(token)))
                            .andExpect(status().isForbidden())
                            .andReturn().getResponse();
            });
        }
    }

    private static Stream<Arguments> provideGetReportArguments() {
        return Stream.of(
                Arguments.of(createAuthenticationForUserRoles(REPORTING_READ_ROLE), true, "reporting_read is allowed"),
                Arguments.of(createAuthenticationForBpRoles(REPORTING_READ_ROLE), false, "BP is forbidden"),
                Arguments.of(createAuthenticationForUserRoles(REPORTING_UPDATE_ROLE), false, "reporting_update is forbidden"),
                Arguments.of(createAuthenticationForUserRoles(ORDER_UPDATE_ROLE), false, "order_update is forbidden"));
    }

    private static JeapAuthenticationToken createAuthenticationForUserRoles(final SemanticApplicationRole... userroles) {
        return JeapAuthenticationTestTokenBuilder.create().withUserRoles(userroles).build();
    }

    private static JeapAuthenticationToken createAuthenticationForBpRoles(final SemanticApplicationRole... userroles) {
        return JeapAuthenticationTestTokenBuilder.create().withBusinessPartnerRoles("123", userroles).build();
    }

}
