package ch.admin.bazg.prezius.reporting.domain.valueobject;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record AnalysisFilterCriteria(
        List<ControlOffice> officeFilter,
        LocalDateTime dateAfterFilter,
        LocalDateTime dateBeforeFilter,
        List<String> businessPartnerFilter
) {}