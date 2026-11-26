package ch.admin.bazg.prezius.reporting.domain.valueobject;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import lombok.Builder;

@Builder
public record Hallmarking(
        HallmarkingType hallmarkingType,
        HallmarkingMethod hallmarkingMethod
) {}
