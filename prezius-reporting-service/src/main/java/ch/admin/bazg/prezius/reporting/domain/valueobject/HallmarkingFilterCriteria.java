package ch.admin.bazg.prezius.reporting.domain.valueobject;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record HallmarkingFilterCriteria(
        List<ControlOffice> officeFilter,
        LocalDateTime dateAfterFilter,
        LocalDateTime dateBeforeFilter,
        List<String> businessPartnerFilter,
        List<HallmarkingType> hallmarkingTypes
) {}