package ch.admin.bazg.prezius.reporting.domain.valueobject;

import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import lombok.Builder;
import lombok.With;

import java.util.UUID;

@With
@Builder(toBuilder = true)
public record Position(
        UUID id,
        String orderId,
        int batchCount,
        int number,
        GoodsType goodsType,
        boolean silver,
        boolean gold,
        boolean platinum,
        boolean palladium,
        int pieceCount,
        String tool
) {

}
