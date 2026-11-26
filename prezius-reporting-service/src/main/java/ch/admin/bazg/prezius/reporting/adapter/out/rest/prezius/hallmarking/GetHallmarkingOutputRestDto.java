package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.hallmarking;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import lombok.Builder;

@Builder(toBuilder = true)
record GetHallmarkingOutputRestDto(
        HallmarkingType hallmarkingType,
        HallmarkingMethod hallmarkingMethod) {
}
