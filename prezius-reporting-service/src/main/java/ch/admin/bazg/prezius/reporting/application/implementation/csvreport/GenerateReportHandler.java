package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import ch.admin.bazg.prezius.reporting.application.ports.out.SearchAnalysisReportItemsPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.persistence.SearchHallmarkingReportItemsPort;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisFilterCriteria;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ch.admin.bit.jeap.db.tx.TransactionalReadReplica;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
@TransactionalReadReplica
class GenerateReportHandler implements GenerateReportUseCase {

    private final SearchHallmarkingReportItemsPort searchHallmarkingReportItemsPort;
    private final SearchAnalysisReportItemsPort searchAnalysisReportItemsPort;
    private final GenerateHallmarkingLinesService generateHallmarkingLinesService;
    private final GenerateAnalysisLinesService generateAnalysisLinesService;

    @Override
    public Flux<ReportLineOutput> generate(final HallmarkingFilterCriteria filterCriteria) {

        final List<HallmarkingReportItem> allHallmarkingReportItems = searchHallmarkingReportItemsPort.search(filterCriteria);
        final List<AnalysisReportItem> allAnalysisReportItems = searchAnalysisReportItemsPort.search(AnalysisFilterCriteria
                                                                                                             .builder()
                                                                                                             .officeFilter(filterCriteria.officeFilter())
                                                                                                             .dateAfterFilter(filterCriteria.dateAfterFilter())
                                                                                                             .dateBeforeFilter(filterCriteria.dateBeforeFilter())
                                                                                                             .businessPartnerFilter(filterCriteria.businessPartnerFilter())
                                                                                                             .build());

        final List<ReportLineOutput> reportLines = filterCriteria.officeFilter().stream()
                                                                 .filter(controlOffice -> controlOffice.isCanDoAnalysisProcess() || controlOffice.isCanDoHallmarkProcess())
                                                                 .flatMap(office -> Stream.of(
                                                                          generateHallmarkingLinesService.generate(office, allHallmarkingReportItems),
                                                                          generateAnalysisLinesService.generate(office, allAnalysisReportItems)))
                                                                 .flatMap(stream -> stream)
                                                                 .toList();
        return Flux.fromIterable(reportLines);
    }

    record ItemCounts(Metal metal, int ch, int hmc) {}

    record MetalCount(int ch, int hmc) {
        public static MetalCount empty() {
            return new MetalCount(0, 0);
        }
    }
}
