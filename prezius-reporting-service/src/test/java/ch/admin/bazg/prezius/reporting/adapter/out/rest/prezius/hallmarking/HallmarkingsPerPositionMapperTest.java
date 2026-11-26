package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.hallmarking;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Hallmarking;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {HallmarkingsPerPositionMapperImpl.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class HallmarkingsPerPositionMapperTest {

    private final HallmarkingsPerPositionMapper hallmarkingsPerPositionMapper;

    @Test
    void toModelMap_whenValidDto_thenMapToModel() {
        final UUID positionId1 = UUID.randomUUID();
        final UUID positionId2 = UUID.randomUUID();
        final var hallmarkingDto1Position1 = new GetHallmarkingOutputRestDto(
                HallmarkingType.SINGLE_INTERNATIONAL, HallmarkingMethod.MECHANICAL
        );
        final var hallmarkingDto1Position2 = new GetHallmarkingOutputRestDto(
                HallmarkingType.SINGLE_SWISS, HallmarkingMethod.LASER
        );
        final var hallmarkingDto2Position1 = new GetHallmarkingOutputRestDto(
                HallmarkingType.SINGLE_INTERNATIONAL, HallmarkingMethod.MECHANICAL
        );
        final Map<UUID, List<GetHallmarkingOutputRestDto>> mapDto = Map.of(
                positionId1, List.of(hallmarkingDto1Position1, hallmarkingDto1Position2),
                positionId2, List.of(hallmarkingDto2Position1)
                                                                          );

        final var mapped = hallmarkingsPerPositionMapper.toModelMap(mapDto);

        final var expectedResult = Map.of(
                positionId1, List.of(
                        Hallmarking.builder()
                                   .hallmarkingType(HallmarkingType.SINGLE_INTERNATIONAL)
                                   .hallmarkingMethod(HallmarkingMethod.MECHANICAL)
                                   .build(),
                        Hallmarking.builder()
                                   .hallmarkingType(HallmarkingType.SINGLE_SWISS)
                                   .hallmarkingMethod(HallmarkingMethod.LASER)
                                   .build()
                                    ),
                positionId2, List.of(Hallmarking.builder()
                                                .hallmarkingType(HallmarkingType.SINGLE_INTERNATIONAL)
                                                .hallmarkingMethod(HallmarkingMethod.MECHANICAL)
                                                .build())
                                         );
        assertThat(mapped).isEqualTo(expectedResult);
    }

}
