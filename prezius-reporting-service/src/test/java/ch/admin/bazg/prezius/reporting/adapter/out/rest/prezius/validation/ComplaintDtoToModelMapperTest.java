package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.validation;

import ch.admin.bazg.prezius.reporting.domain.valueobject.Complaint;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {ComplaintDtoToModelMapperImpl.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ComplaintDtoToModelMapperTest {

    private final ComplaintDtoToModelMapper complaintDtoToModelMapper;

    @Test
    void toModelMap_whenValidDto_thenMapToModel() {
        final var positionId = UUID.randomUUID();
        final var complaintDto = new ComplaintDto(
                true, 22);
        final var dtoMap = Map.of(positionId, complaintDto);

        final Map<UUID, Complaint> mappedMap = complaintDtoToModelMapper.toModelMap(dtoMap);

        final var expectedResult = Map.of(positionId, Complaint.builder().immediateCompliance(true).contestedPieceCount(22).build());
        assertThat(mappedMap).isEqualTo(expectedResult);
    }

    @Test
    void toModelMap_whenImmediateComplianceIsNull_thenMapToNull() {
        final var positionId = UUID.randomUUID();
        final var complaintDto = new ComplaintDto(
                null, 22);
        final var dtoMap = Map.of(positionId, complaintDto);

        final Map<UUID, Complaint> mappedMap = complaintDtoToModelMapper.toModelMap(dtoMap);

        final var expectedResult = Map.of(positionId, Complaint.builder().immediateCompliance(null).contestedPieceCount(22).build());
        assertThat(mappedMap).isEqualTo(expectedResult);
    }
}

