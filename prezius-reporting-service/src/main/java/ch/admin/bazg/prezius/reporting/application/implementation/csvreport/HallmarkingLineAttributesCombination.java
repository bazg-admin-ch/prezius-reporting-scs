package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;

import java.util.Arrays;
import java.util.stream.Stream;

record HallmarkingLineAttributesCombination(
            ControlOffice controlOffice,
            HallmarkingLocation hallmarkingLocation,
            boolean certified,
            GoodsType goodsType,
            HallmarkingMethod hallmarkingMethod
    ) {

    public static Stream<HallmarkingLineAttributesCombination> getAllAttributeCombinations(final ControlOffice office) {
        return Arrays.stream(HallmarkingLocation.values())
                     .flatMap(hallmarkingLocation -> Stream.of(true, false)
                                                           .flatMap(certified -> Stream.of(GoodsType.WATCH_CASE, GoodsType.JEWELRY)
                                                                                       .flatMap(goodsType -> Arrays.stream(HallmarkingMethod.values())
                                                                                                                   .flatMap(method ->
                                                                                                                           Stream.of(new HallmarkingLineAttributesCombination(office, hallmarkingLocation, certified, goodsType, method))))));
    }
}