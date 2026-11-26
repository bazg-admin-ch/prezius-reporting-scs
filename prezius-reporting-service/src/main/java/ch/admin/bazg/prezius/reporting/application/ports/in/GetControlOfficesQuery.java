package ch.admin.bazg.prezius.reporting.application.ports.in;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;

import java.util.List;

public interface GetControlOfficesQuery {

    List<ControlOffice> getControlOffices();
}
