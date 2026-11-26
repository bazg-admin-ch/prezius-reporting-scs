package ch.admin.bazg.prezius.reporting.domain.valueobject;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

@With
@Builder(toBuilder = true)
public record HallmarkingOrder(
        String businessId,
        ControlOffice office,
        String businessPartnerId,
        boolean certifiedMaterial,
        HallmarkingLocation hallmarkingLocation,
        LocalDateTime validationDate,
        List<Position> positions
) {}
