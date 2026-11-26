package ch.admin.bazg.prezius.reporting.domain.valueobject;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record AnalysisOrder(
        String businessId,
        ControlOffice office,
        String businessPartnerId,
        AnalysisType analysisType,
        boolean silver,
        boolean gold,
        boolean platinum,
        boolean palladium,
        LocalDateTime markedAsDoneAt,
        List<Position> positions
) {}
