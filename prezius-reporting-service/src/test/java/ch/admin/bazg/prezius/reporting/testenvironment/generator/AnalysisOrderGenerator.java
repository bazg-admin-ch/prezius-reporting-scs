package ch.admin.bazg.prezius.reporting.testenvironment.generator;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisOrder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
public class AnalysisOrderGenerator {

    public final AnalysisPositionGenerator analysisPositionGenerator;

    public AnalysisOrderGenerator() {
        this.analysisPositionGenerator = new AnalysisPositionGenerator();
    }

    public AnalysisOrder generate() {
        return generate(null);
    }

    public AnalysisOrder generate(final Function<AnalysisOrder.AnalysisOrderBuilder, AnalysisOrder.AnalysisOrderBuilder> decorator) {
        final var analysisOrder = new AnalysisOrder(
                UUID.randomUUID().toString(), ControlOffice.BASC, "123456", AnalysisType.CONFORMITY_EVALUATION,
                true, true, true, true,
                LocalDateTime.now(), List.of(analysisPositionGenerator.generate())
                );
        if (decorator != null) {
            return decorator.apply(analysisOrder.toBuilder()).build();
        }
        return analysisOrder;
    }
}
