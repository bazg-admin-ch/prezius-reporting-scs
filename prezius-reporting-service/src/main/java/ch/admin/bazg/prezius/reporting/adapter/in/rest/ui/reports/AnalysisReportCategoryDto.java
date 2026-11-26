package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
record AnalysisReportCategoryDto(
        int au,
        int ag,
        int pd,
        int pt,
        int total,
        BigDecimal totalCostsWithVat
) {
}
