package ch.admin.bazg.prezius.reporting.application.implementation.getoffices;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetControlOfficesQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class GetControlOfficesHandler implements GetControlOfficesQuery {

    @Override
    public List<ControlOffice> getControlOffices() {
        return List.of(ControlOffice.values());
    }
}
