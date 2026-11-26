package ch.admin.bazg.prezius.reporting.application.ports.out;

import ch.admin.bazg.prezius.reporting.domain.valueobject.Complaint;

import java.util.Map;
import java.util.UUID;

public interface GetComplaintsPort {

    Map<UUID, Complaint> getComplaintsPerPosition(UUID orderId);

}
