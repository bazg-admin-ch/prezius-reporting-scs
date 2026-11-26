package ch.admin.bazg.prezius.reporting.application.ports.in;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Validated
public interface GetAnalysisReportUseCase {

    Output getAnalysisReport(@Valid @NotNull Input input);

    @Builder(toBuilder = true)
    record Input(
            @Size(min = 1) List<ControlOffice> officeFilter,
            LocalDateTime dateAfterFilter,
            LocalDateTime dateBeforeFilter,
            @Size(min = 1) List<String> businessPartnerFilter
    ) {}

    @Builder
    record Output(
            Map<AnalysisType, ReportCategoryOutput> analysisTypeReports,
            ReportCategoryOutput totalReport
    ) {
        @Builder
        public record ReportCategoryOutput(
                int au,
                int ag,
                int pd,
                int pt,
                int total,
                BigDecimal totalCostsWithVat
        ) {
        }
    }
}
