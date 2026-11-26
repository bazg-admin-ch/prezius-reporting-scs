package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports.csvreport;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports.mapper.HallmarkingFilterCriteriaMapper;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import ch.admin.bazg.quadrel.filter.QdFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "Get a csv of all reports")
@RestController
@RequestMapping("/api/ui")
@RequiredArgsConstructor
class GenerateCsvReportUiRestController {

    private final GenerateReportUseCase generateReportUseCase;
    private final ReportLineResponseMapper reportLineResponseMapper;
    private final HallmarkingFilterCriteriaMapper hallmarkingFilterCriteriaMapper;

    private static final String HEADER_LINE = "Office;Rubrique;Operation / Warenkategorie;Zertifizierung;Punzierung durch / Standort;Methode;Metall;Anzahl CH;Anzahl HMC\n";

    @GetMapping("/reports/export")
    @PreAuthorize("hasRole('reporting', 'read')")
    public ResponseEntity<Flux<String>> generateReport(@RequestParam(defaultValue = "") final QdFilter filter) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "report.csv");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(getReportLines(filter));
    }

    private Flux<String> getReportLines(final QdFilter filter) {
        final var requestedFilterCriteria = hallmarkingFilterCriteriaMapper.toFilterCriteria(filter);
        final var hallmarkingFilterCriteriaBuilder = requestedFilterCriteria.toBuilder();
        if (requestedFilterCriteria.officeFilter() == null) {
            hallmarkingFilterCriteriaBuilder.officeFilter(List.of(ControlOffice.values()));
        }

        final Flux<String> headerLineFlux = Flux.just(HEADER_LINE);
        final Flux<String> contentLinesFlux = generateReportUseCase.generate(hallmarkingFilterCriteriaBuilder.build())
                                                                   .map(data -> reportLineResponseMapper.toResponseLineString(data).concat("\n"));

        return Flux.concat(
                Flux.just(utf8BytesAsString()),
                headerLineFlux,
                contentLinesFlux);
    }

    private String utf8BytesAsString() {
        /* those arrays must be at the start of the Flux<String> to mark the whole string as UTF-8
        It is needed for Excel to open the csv file with UTF-8
         */
        final byte[] utf8BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        return new String(utf8BOM, StandardCharsets.UTF_8);
    }

}
