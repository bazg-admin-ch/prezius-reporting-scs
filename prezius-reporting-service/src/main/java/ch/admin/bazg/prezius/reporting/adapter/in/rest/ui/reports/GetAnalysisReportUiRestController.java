package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetAnalysisReportUseCase;
import ch.admin.bazg.quadrel.filter.QdFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Tag(name = "Analysis Reporting API", description = "The API for analysis reporting")
@RestController
@RequestMapping("/api/ui/analysis-reports")
@RequiredArgsConstructor
class GetAnalysisReportUiRestController {

    private final GetAnalysisReportUseCase getAnalysisReportUseCase;
    private final AnalysisReportDtoMapper analysisReportDtoMapper;
    private static final DateTimeFormatter dateFilterFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

    @Operation(summary = "Get analysis report")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRoleForAllPartners('reporting', 'read')")
    public AnalysisReportDto get(@RequestParam(defaultValue = "") final QdFilter filter) {
        final var inputBuilder = GetAnalysisReportUseCase.Input.builder();

        filter.categories().forEach(qdFilterCategory -> {
            switch (qdFilterCategory.category()) {
                case "officeName" -> {
                    final var controlOffices = qdFilterCategory.items()
                                                               .stream()
                                                               .map(ControlOffice::valueOf)
                                                               .toList();
                    inputBuilder.officeFilter(controlOffices);
                }
                case "date" -> {
                    final String startDateString = qdFilterCategory.items().get(0);
                    final String endDateString = qdFilterCategory.items().get(1);
                    if (StringUtils.hasText(startDateString)) {
                        final var startDate = LocalDate.parse(startDateString, dateFilterFormatter);
                        inputBuilder.dateAfterFilter(startDate.atStartOfDay());
                    }
                    if (StringUtils.hasText(endDateString)) {
                        final var endDate = LocalDate.parse(endDateString, dateFilterFormatter);
                        inputBuilder.dateBeforeFilter(endDate.atTime(LocalTime.MAX));
                    }
                }
                case "businessPartner" -> inputBuilder.businessPartnerFilter(qdFilterCategory.items());
                default -> throw new IllegalArgumentException("Unexpected filter category: " + qdFilterCategory.category());
            }
        });

        return analysisReportDtoMapper.toDto(getAnalysisReportUseCase.getAnalysisReport(inputBuilder.build()));
    }
}
