package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.order;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Invoicing;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisOption;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.common.dto.AnalysisOrderPublicRestDto;
import ch.admin.bazg.prezius.common.dto.AnalysisPositionPublicRestDto;
import ch.admin.bazg.prezius.common.publicEnum.OrderStatus;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisOrder;
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
@SpringBootTest(classes = {AnalysisOrderPublicRestDtoMapperImpl.class})
class AnalysisOrderPublicRestDtoMapperTest {

    @Autowired
    private AnalysisOrderPublicRestDtoMapper mapperUnderTest;

    @Test
    void toModel_whenInputPresent_thenMapToModel() {
        final var analysisPositionPublicRestDto = new AnalysisPositionPublicRestDto(
                UUID.randomUUID(), UUID.randomUUID(), "orderBusinessId", 1,
                "description", true, BigDecimal.TEN
        );
        final var setToDoneAt = LocalDateTime.now();
        final var materialReceivedDate = LocalDateTime.now().minusDays(1);

        final var analysisOrderPublicRestDto = new AnalysisOrderPublicRestDto(
                UUID.randomUUID(), "order_123", ControlOffice.NOIC,
                "businessPartnerId", "intermediary", Invoicing.CLIENT_ACCOUNT,
                OrderStatus.IN_PROGRESS, List.of(analysisPositionPublicRestDto), AnalysisType.DETERMINATION_ON_SAMPLES,
                AnalysisOption.OTHER_METHOD, true, true, false, false, false,
                "ref", materialReceivedDate, setToDoneAt
        );

        final AnalysisOrder mapped = mapperUnderTest.toModel(analysisOrderPublicRestDto);

        assertThat(mapped.businessId()).isEqualTo(analysisOrderPublicRestDto.orderBusinessId());
        assertThat(mapped.office().getName()).isEqualTo(analysisOrderPublicRestDto.office().getName());
        assertThat(mapped.office().getCode()).isEqualTo(analysisOrderPublicRestDto.office().getCode());
        assertThat(mapped.office().getTenant()).isEqualTo(analysisOrderPublicRestDto.office().getTenant());
        assertThat(mapped.businessPartnerId()).isEqualTo(analysisOrderPublicRestDto.businessPartnerId());
        assertThat(mapped.analysisType().name()).isEqualTo(analysisOrderPublicRestDto.analysisType().name());
        assertThat(mapped.gold()).isEqualTo(analysisOrderPublicRestDto.au());
        assertThat(mapped.silver()).isEqualTo(analysisOrderPublicRestDto.ag());
        assertThat(mapped.palladium()).isEqualTo(analysisOrderPublicRestDto.pd());
        assertThat(mapped.platinum()).isEqualTo(analysisOrderPublicRestDto.pt());
        assertThat(mapped.positions()).hasSize(1);
        assertThat(mapped.positions().getFirst().id()).isEqualTo(analysisPositionPublicRestDto.id());
        assertThat(mapped.markedAsDoneAt()).isEqualTo(analysisOrderPublicRestDto.setToDoneAt());
    }
}
