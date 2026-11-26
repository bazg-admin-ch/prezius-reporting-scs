package ch.admin.bazg.prezius.reporting.testenvironment.generator;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Component
public class AnalysisReportItemGenerator {

    public AnalysisReportItem generate() {
        return this.generate(null);
    }

    public AnalysisReportItem generate(final Function<AnalysisReportItem.AnalysisReportItemBuilder, AnalysisReportItem.AnalysisReportItemBuilder> decorator) {
        final var reportItem = AnalysisReportItem.createNew(
                UUID.randomUUID(), 1, 2, 3, 4, BigDecimal.TEN,
                ControlOffice.BASC, LocalDateTime.now(), "123456",
                AnalysisType.CONFORMITY_EVALUATION);
        if (decorator != null) {
            return decorator.apply(reportItem.toBuilder()).build();
        }
        return reportItem;
    }

}
