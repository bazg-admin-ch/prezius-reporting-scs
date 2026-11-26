package ch.admin.bazg.prezius.reporting.application.implementation.getoffices;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GetControlOfficesHandlerTest {

    private final GetControlOfficesHandler getControlOfficesHandler = new GetControlOfficesHandler();

    @Test
    void getOffices() {
        assertThat(getControlOfficesHandler.getControlOffices()).isEqualTo(List.of(ControlOffice.values()));
    }
}
