package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetHallmarkingReportQuery;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import ch.admin.bit.jeap.security.resource.semanticAuthentication.SemanticApplicationRole;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationToken;
import ch.admin.bit.jeap.security.test.resource.JeapAuthenticationTestTokenBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
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
class GetHallmarkingReportUiRestControllerIT extends ITBase {

    private static final SemanticApplicationRole REPORTING_READ_ROLE = SemanticApplicationRole.builder()
                                                                                              .system("prezius")
                                                                                              .resource("reporting")
                                                                                              .operation("read")
                                                                                              .build();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetHallmarkingReportQuery getHallmarkingReportQuery;

    @Test
    void get_whenNoFilter_returnCorrectReport() {
        final var hallmarkingReportItem = this.hallmarkingReportItemGenerator.generate();
        when(getHallmarkingReportQuery.getHallmarkingReportItems(any())).thenReturn(List.of(hallmarkingReportItem));

        final JeapAuthenticationToken authentication = createAuthenticationWithReportingReadRole();
        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get("/api/ui/hallmarking-reports").with(authentication(authentication)))
                        .andExpect(status().isOk());
        });
    }

    @Test
    void get_whenFiltersSet_thenUseCaseCalledWithParsedFilters() {
        final JeapAuthenticationToken authentication = createAuthenticationWithReportingReadRole();
        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get("/api/ui/hallmarking-reports").with(authentication(authentication))
                                                                .param("filter", "officeName$NOIC§CFEC|date$!§2025-02-21|businessPartner$1000143862|hallmarkingType$SINGLE_INTERNATIONAL"))
                        .andExpect(status().isOk());
        });
        final var expectedUseCaseInput = HallmarkingFilterCriteria.builder()
                                                                  .businessPartnerFilter(List.of("1000143862"))
                                                                  .dateAfterFilter(null)
                                                                  .dateBeforeFilter(LocalDate.of(2025, 2, 21).atTime(LocalTime.MAX))
                                                                  .officeFilter(List.of(ControlOffice.NOIC, ControlOffice.CFEC))
                                                                  .hallmarkingTypes(List.of(HallmarkingType.SINGLE_INTERNATIONAL))
                                                                  .build();
        verify(getHallmarkingReportQuery).getHallmarkingReportItems(expectedUseCaseInput);
    }

    private static JeapAuthenticationToken createAuthenticationWithReportingReadRole() {
        return JeapAuthenticationTestTokenBuilder.create().withUserRoles(REPORTING_READ_ROLE).build();
    }
}
