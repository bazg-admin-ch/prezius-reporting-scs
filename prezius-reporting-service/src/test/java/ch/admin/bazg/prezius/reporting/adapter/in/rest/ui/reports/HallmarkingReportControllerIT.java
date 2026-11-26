package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.adapter.in.rest.TestFileUtils;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import ch.admin.bit.jeap.security.resource.semanticAuthentication.SemanticApplicationRole;
import ch.admin.bit.jeap.security.resource.token.JeapAuthenticationToken;
import ch.admin.bit.jeap.security.test.resource.JeapAuthenticationTestTokenBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class HallmarkingReportControllerIT extends ITBase {

    private static final SemanticApplicationRole REPORTING_READ_ROLE = SemanticApplicationRole.builder()
            .system("prezius")
            .resource("reporting")
            .operation("read")
            .build();

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        saveHallmarkingReportItem(GoodsType.WATCH_CASE, HallmarkingMethod.LASER, true, 264, 735, 99, 88, 1186);
        saveHallmarkingReportItem(GoodsType.WATCH_CASE, HallmarkingMethod.LASER, false, 676, 2125, 1306, 329, 4436);
        saveHallmarkingReportItem(GoodsType.WATCH_CASE, HallmarkingMethod.MECHANICAL, true,247, 1160, 499, 66, 1972);
        saveHallmarkingReportItem(GoodsType.WATCH_CASE, HallmarkingMethod.MECHANICAL, false, 2848, 8009, 1883, 836, 13576);
        saveHallmarkingReportItem(GoodsType.JEWELRY, HallmarkingMethod.LASER, true, 111, 122, 68, 96, 397);
        saveHallmarkingReportItem(GoodsType.JEWELRY, HallmarkingMethod.LASER, false, 2291, 478, 40, 1780, 4589);
        saveHallmarkingReportItem(GoodsType.JEWELRY, HallmarkingMethod.MECHANICAL, true, 155, 118, 24, 90, 387);
        saveHallmarkingReportItem(GoodsType.JEWELRY, HallmarkingMethod.MECHANICAL, false, 7261, 3077, 6, 3628, 13972);
    }

    @Test
    void get_whenNoFilter_returnCorrectReport() {
        final String expectedResponseBody = TestFileUtils.readResponseBody("get-hallmarking-report.json");

        final JeapAuthenticationToken authentication = createAuthenticationWithReportingReadRole();
        assertDoesNotThrow(() -> {
            this.mockMvc.perform(get("/api/ui/hallmarking-reports").with(authentication(authentication)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedResponseBody));
        });
    }

    private static JeapAuthenticationToken createAuthenticationWithReportingReadRole() {
        return JeapAuthenticationTestTokenBuilder.create().withUserRoles(HallmarkingReportControllerIT.REPORTING_READ_ROLE).build();
    }

    private void saveHallmarkingReportItem(final GoodsType goodsType, final HallmarkingMethod hallmarkingMethod,
                                           final boolean certifiedMaterial,
                                           final int ag, final int au, final int pd, final int pt, final int cost) {
        hallmarkingReportItemRepository.save(HallmarkingReportItem.builder()
                .orderId(UUID.randomUUID())
                .goodsType(goodsType)
                .hallmarkingMethod(hallmarkingMethod)
                .certifiedMaterial(certifiedMaterial)
                .ag(ag)
                .au(au)
                .pd(pd)
                .pt(pt)
                .cost(BigDecimal.valueOf(cost))
                .hallmarkingLocation(HallmarkingLocation.OFFICE)
                .hallmarkingType(HallmarkingType.SINGLE_SWISS)
                .office(ControlOffice.ZEMK)
                .date(LocalDateTime.now())
                .businessPartner("bp1")
                .build());
    }
}
