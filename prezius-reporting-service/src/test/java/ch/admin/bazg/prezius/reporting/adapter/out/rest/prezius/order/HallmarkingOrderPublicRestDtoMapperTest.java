package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.order;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Invoicing;
import ch.admin.bazg.prezius.common.domain.order.RiskProfile;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.Category;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.common.dto.HallmarkingOrderPublicRestDto;
import ch.admin.bazg.prezius.common.dto.HallmarkingPositionPublicRestDto;
import ch.admin.bazg.prezius.common.publicEnum.OrderStatus;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {HallmarkingOrderPublicRestDtoMapperImpl.class, HallmarkingPositionPublicRestDtoMapperImpl.class})
class HallmarkingOrderPublicRestDtoMapperTest {

    @Autowired
    private HallmarkingOrderPublicRestDtoMapper mapperUnderTest;

    @Test
    void toModel_whenInputPresent_thenMapToModel() {
        final var hallmarkingPositionPublicRestDto = new HallmarkingPositionPublicRestDto(
                UUID.randomUUID(), UUID.randomUUID(),
                "orderBusinessId", 1,
                1, 1,
                GoodsType.JEWELRY, true, true,
                true, true,
                "respMark", "tool234", 2,
                4, true,
                true, true,
                true, true, true, true,
                new HallmarkingPositionPublicRestDto.AnalysisOrderReferenceDto(UUID.randomUUID(), "analysisOrderId"),
                BigDecimal.TEN, Category.NORMAL, "fin", "clientRef"
        );

        final var hallmarkingOrderPublicRestDto = new HallmarkingOrderPublicRestDto(
                UUID.randomUUID(), "order_123", ControlOffice.NOIC,
                "businessPartnerId", "intermediary", true,
                true, true, HallmarkingMethod.MECHANICAL,
                HallmarkingLocation.OFFICE, Invoicing.CLIENT_ACCOUNT, OrderStatus.SENT,
                LocalDateTime.now(), List.of(hallmarkingPositionPublicRestDto), RiskProfile.LOW,
                true
        );

        final HallmarkingOrder mapped = mapperUnderTest.toModel(hallmarkingOrderPublicRestDto);

        assertThat(mapped.businessId()).isEqualTo(hallmarkingOrderPublicRestDto.orderBusinessId());
        assertThat(mapped.office().getName()).isEqualTo(hallmarkingOrderPublicRestDto.office().getName());
        assertThat(mapped.office().getCode()).isEqualTo(hallmarkingOrderPublicRestDto.office().getCode());
        assertThat(mapped.office().getTenant()).isEqualTo(hallmarkingOrderPublicRestDto.office().getTenant());
        assertThat(mapped.businessPartnerId()).isEqualTo(hallmarkingOrderPublicRestDto.businessPartnerId());
        assertThat(mapped.certifiedMaterial()).isEqualTo(hallmarkingOrderPublicRestDto.certifiedMaterial());
        assertThat(mapped.hallmarkingLocation().name()).isEqualTo(hallmarkingOrderPublicRestDto.hallmarkingLocation().name());
        assertThat(mapped.validationDate()).isEqualTo(hallmarkingOrderPublicRestDto.validationDate());
        assertThat(mapped.positions()).hasSize(1);
        assertThat(mapped.positions().getFirst().id()).isEqualTo(hallmarkingPositionPublicRestDto.id());
    }
}
