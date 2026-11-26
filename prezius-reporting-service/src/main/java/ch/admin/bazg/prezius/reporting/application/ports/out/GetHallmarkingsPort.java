package ch.admin.bazg.prezius.reporting.application.ports.out;

import ch.admin.bazg.prezius.reporting.domain.valueobject.Hallmarking;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GetHallmarkingsPort {

    Map<UUID, List<Hallmarking>> getHallmarkingsPerPosition(String orderBusinessId);

}
