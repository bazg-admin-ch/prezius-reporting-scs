package ch.admin.bazg.prezius.reporting.testenvironment.generator;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Component
public class HallmarkingReportItemGenerator {

    public HallmarkingReportItem generate() {
        return generate(null);
    }

    public HallmarkingReportItem generate(final Function<HallmarkingReportItem.HallmarkingReportItemBuilder, HallmarkingReportItem.HallmarkingReportItemBuilder> decorator) {

        final var hallmarkingReportItem = new HallmarkingReportItem(
                UUID.randomUUID(), 1, 2,
                0, 0, BigDecimal.valueOf(112),
                ControlOffice.BASC, LocalDateTime.now(), "123456",
                GoodsType.JEWELRY, HallmarkingType.DOUBLE, HallmarkingMethod.LASER,
                HallmarkingLocation.CLIENT, true
        );
        if (decorator != null) {
            return decorator.apply(hallmarkingReportItem.toBuilder()).build();
        }
        return hallmarkingReportItem;
    }

}
