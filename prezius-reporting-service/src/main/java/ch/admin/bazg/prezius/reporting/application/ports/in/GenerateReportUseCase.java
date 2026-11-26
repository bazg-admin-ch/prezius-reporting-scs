package ch.admin.bazg.prezius.reporting.application.ports.in;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@Validated
public interface GenerateReportUseCase {

    Flux<ReportLineOutput> generate(@NotNull HallmarkingFilterCriteria filterCriteria);

    record ReportLineOutput(
        ControlOffice controlOffice,
        Section section,
        OperationOrGoodsType operationOrGoodsType,
        Certification certification,
        HallmarkingLocation hallmarkingLocation,
        HallmarkingMethod hallmarkingMethod,
        Metal metal,
        BigDecimal countCh,
        Integer countHmc) {

        public static ReportLineOutput createRevenueLine(final ControlOffice controlOffice, final OperationOrGoodsType operationOrGoodsType, final BigDecimal countCh, final Integer countHmc) {
            return new ReportLineOutput(
                    controlOffice, Section.REVENUE, operationOrGoodsType,
                    null, null, null, null,
                    countCh, countHmc
            );
        }

        public static ReportLineOutput createAnalysisCountLine(final ControlOffice controlOffice, final OperationOrGoodsType operationOrGoodsType, final Metal metal, final BigDecimal countCh) {
            return new ReportLineOutput(
                    controlOffice, Section.ANALYSIS, operationOrGoodsType,
                    null, null, null,
                    metal, countCh, null
            );
        }
    }

    enum Section {
        REVENUE,
        HALLMARKING,
        ANALYSIS
    }

    enum OperationOrGoodsType {
        WATCH_CASE,
        OTHER_GOODS,
        CONFORMITY_EVALUATION,
        DETERMINATION_ON_SAMPLES,
        DETERMINATION_ON_MELTING_POINT,
        SAMPLES_FROM_HALLMARKING,
        SAMPLES_FROM_OTHER_PROCESSES,
        HALLMARKING_DEPARTMENT,
        ANALYSIS_DEPARTMENT
    }

    enum Certification {
        YES,
        NO
    }

}
