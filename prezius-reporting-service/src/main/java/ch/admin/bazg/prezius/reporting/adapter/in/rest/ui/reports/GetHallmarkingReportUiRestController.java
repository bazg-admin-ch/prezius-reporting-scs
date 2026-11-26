package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports.mapper.HallmarkingFilterCriteriaMapper;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetHallmarkingReportQuery;
import ch.admin.bazg.quadrel.filter.QdFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reporting API", description = "The API for reporting")
@RestController
@RequestMapping("/api/ui/hallmarking-reports")
@RequiredArgsConstructor
class GetHallmarkingReportUiRestController {

    private final GetHallmarkingReportQuery getHallmarkingReportQuery;
    private final HallmarkingReportDtoMapper hallmarkingReportDtoMapper;
    private final HallmarkingFilterCriteriaMapper hallmarkingFilterCriteriaMapper;

    @Operation(summary = "Get one reporting")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRoleForAllPartners('reporting', 'read')")
    public ReportDto get(@RequestParam(defaultValue = "") final QdFilter filter) {
        final var result = getHallmarkingReportQuery.getHallmarkingReportItems(hallmarkingFilterCriteriaMapper.toFilterCriteria(filter));
        return hallmarkingReportDtoMapper.toReportDto(result);
    }
}
