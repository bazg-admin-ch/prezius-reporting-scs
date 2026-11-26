package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.order;

import ch.admin.bazg.prezius.common.domain.order.hallmarking.Category;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.dto.HallmarkingPositionPublicRestDto;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {HallmarkingPositionPublicRestDtoMapperImpl.class})
class HallmarkingPositionPublicRestDtoMapperTest {

    @Autowired
    private HallmarkingPositionPublicRestDtoMapper mapperUnderTest;

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

        final Position mapped = mapperUnderTest.toModel(hallmarkingPositionPublicRestDto);

        assertThat(mapped.id()).isEqualTo(hallmarkingPositionPublicRestDto.id());
        assertThat(mapped.orderId()).isEqualTo(hallmarkingPositionPublicRestDto.orderBusinessId());
        assertThat(mapped.number()).isEqualTo(hallmarkingPositionPublicRestDto.number());
        assertThat(mapped.batchCount()).isEqualTo(hallmarkingPositionPublicRestDto.batchCount());
        assertThat(mapped.pieceCount()).isEqualTo(hallmarkingPositionPublicRestDto.pieceCount());
        assertThat(mapped.gold()).isEqualTo(hallmarkingPositionPublicRestDto.au());
        assertThat(mapped.silver()).isEqualTo(hallmarkingPositionPublicRestDto.ag());
        assertThat(mapped.platinum()).isEqualTo(hallmarkingPositionPublicRestDto.pt());
        assertThat(mapped.palladium()).isEqualTo(hallmarkingPositionPublicRestDto.pd());
        assertThat(mapped.goodsType().name()).isEqualTo(hallmarkingPositionPublicRestDto.goodsType().name());
    }
}
