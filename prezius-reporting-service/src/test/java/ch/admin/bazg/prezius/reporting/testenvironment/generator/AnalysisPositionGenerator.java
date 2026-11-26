package ch.admin.bazg.prezius.reporting.testenvironment.generator;

import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Position;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class AnalysisPositionGenerator {

    public Position generate() {
        return generate(null);
    }

    public Position generate(final Function<Position.PositionBuilder, Position.PositionBuilder> decorator) {
        final var position = new Position(
                UUID.randomUUID(), UUID.randomUUID().toString(),
                5, 1, GoodsType.JEWELRY,
                true, true, true, true,
                1, "tool"
                );
        if (decorator != null) {
            return decorator.apply(position.toBuilder()).build();
        }
        return position;
    }
}
