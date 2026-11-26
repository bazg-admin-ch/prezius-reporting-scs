package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.offices;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {ControlOfficesUiRestDtoMapperImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ControlOfficesUiRestDtoMapperTest {

    private final ControlOfficesUiRestDtoMapper mapperUnderTest;

    @Test
    void toRestDtos_whenOfficesPassed_thenReturnOfficeDtoList() {
        final List<ControlOffice> inputOffices = List.of(ControlOffice.ZEMK, ControlOffice.CDFC);
        final List<ControlOfficeUiRestDto> outputDtos = mapperUnderTest.toRestDtos(inputOffices);
        assertThat(outputDtos).satisfiesExactly(outputDto -> {
            assertThat(outputDto.name()).isEqualTo(ControlOffice.ZEMK.getName());
            assertThat(outputDto.code()).isEqualTo(1);
        }, outputDto -> {
            assertThat(outputDto.name()).isEqualTo(ControlOffice.CDFC.getName());
            assertThat(outputDto.code()).isEqualTo(9);
        });
    }
}
