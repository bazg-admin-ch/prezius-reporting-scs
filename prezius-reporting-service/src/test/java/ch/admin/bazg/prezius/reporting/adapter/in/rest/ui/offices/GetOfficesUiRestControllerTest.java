package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.offices;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetControlOfficesQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOfficesUiRestControllerTest {

    @Mock
    private GetControlOfficesQuery getControlOfficesQuery;

    @Mock
    private ControlOfficesUiRestDtoMapper controlOfficesUiRestDtoMapper;

    @InjectMocks
    private GetOfficesUiRestController getOfficesUiRestController;


    @Test
    void getAll_whenTwoOfficesExist_thenOfficesMappedAndReturned() {
        final List<ControlOffice> mockedControlOffices = List.of(ControlOffice.ZEMK, ControlOffice.CDFC);
        final List<ControlOfficeUiRestDto> mockedControlOfficeUiRestDtos = List.of(
                ControlOfficeUiRestDto.builder().name("ZEMK").code(1).build(),
                ControlOfficeUiRestDto.builder().name("CDFC").code(9).build()
                                                                                  );
        when(getControlOfficesQuery.getControlOffices()).thenReturn(mockedControlOffices);
        when(controlOfficesUiRestDtoMapper.toRestDtos(mockedControlOffices)).thenReturn(mockedControlOfficeUiRestDtos);

        final List<ControlOfficeUiRestDto> result = getOfficesUiRestController.getAll();

        assertThat(result).isEqualTo(mockedControlOfficeUiRestDtos);
    }
}
