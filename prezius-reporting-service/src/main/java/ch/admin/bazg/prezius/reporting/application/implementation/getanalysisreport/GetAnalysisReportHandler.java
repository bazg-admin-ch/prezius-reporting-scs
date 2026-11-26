package ch.admin.bazg.prezius.reporting.application.implementation.getanalysisreport;

import ch.admin.bazg.prezius.reporting.application.ports.in.GetAnalysisReportUseCase;
import ch.admin.bazg.prezius.reporting.application.ports.out.SearchAnalysisReportItemsPort;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisFilterCriteria;
import ch.admin.bit.jeap.db.tx.TransactionalReadReplica;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@TransactionalReadReplica
class GetAnalysisReportHandler implements GetAnalysisReportUseCase {

    private final SearchAnalysisReportItemsPort searchAnalysisReportItemsPort;

    @Override
    public Output getAnalysisReport(final Input input) {
        final var analysisTypeReports = searchAnalysisReportItemsPort.search(AnalysisFilterCriteria.builder()
                                                                                                   .officeFilter(input.officeFilter())
                                                                                                   .dateAfterFilter(input.dateAfterFilter())
                                                                                                   .dateBeforeFilter(input.dateBeforeFilter())
                                                                                                   .businessPartnerFilter(input.businessPartnerFilter())
                                                                                                   .build())
                                                                     .stream()
                                                                     .collect(Collectors.groupingBy(AnalysisReportItem::getAnalysisType, Collector.of(
                                                                             ReportCategoryContainer::empty,
                                                                             ReportCategoryContainer::add,
                                                                             ReportCategoryContainer::add)));

        return Output.builder()
                     .analysisTypeReports(analysisTypeReports.entrySet()
                                                             .stream()
                                                             .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toReportCategoryOutput()))
                                         )
                     .totalReport(analysisTypeReports.values()
                                                     .stream()
                                                     .reduce(ReportCategoryContainer.empty(), ReportCategoryContainer::add)
                                                     .toReportCategoryOutput()
                                 )
                     .build();
    }

    @Builder(access = AccessLevel.PACKAGE)
    @Data
    private static final class ReportCategoryContainer {
        private int au;
        private int ag;
        private int pd;
        private int pt;
        private int total;
        private BigDecimal totalCostsWithVat;

        public static ReportCategoryContainer empty() {
            return ReportCategoryContainer.builder()
                                          .totalCostsWithVat(BigDecimal.ZERO)
                                          .build();
        }

        public void add(final AnalysisReportItem reportItem) {
            this.au += reportItem.getAu();
            this.ag += reportItem.getAg();
            this.pd += reportItem.getPd();
            this.pt += reportItem.getPt();
            this.total += reportItem.getAg() + reportItem.getAu() + reportItem.getPd() + reportItem.getPt();
            this.totalCostsWithVat = this.totalCostsWithVat.add(reportItem.getCost());
        }

        public ReportCategoryContainer add(final ReportCategoryContainer other) {
            this.au += other.au;
            this.ag += other.ag;
            this.pd += other.pd;
            this.pt += other.pt;
            this.total += other.total;
            this.totalCostsWithVat = this.totalCostsWithVat.add(other.totalCostsWithVat);
            return this;
        }

        public Output.ReportCategoryOutput toReportCategoryOutput() {
            return Output.ReportCategoryOutput.builder()
                                              .au(au)
                                              .ag(ag)
                                              .pd(pd)
                                              .pt(pt)
                                              .total(total)
                                              .totalCostsWithVat(totalCostsWithVat)
                                              .build();
        }

    }
}
