package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
class HallmarkingReportDtoMapper {

    ReportDto toReportDto(final List<HallmarkingReportItem> reportItems) {
        record Key(GoodsType goodsType, HallmarkingMethod hallmarkingMethod, boolean certified) {
        }

        final Map<Key, ReportLineDto> reportLineMap = reportItems.stream()
                                                                 .collect(Collectors.groupingBy(
                                                                                  item -> new Key(item.getGoodsType(), item.getHallmarkingMethod(), item.isCertifiedMaterial()),
                                                                                  Collectors.reducing(ReportLineDto.zeros(),
                                                                                                      reportItem -> ReportLineDto.zeros().add(reportItem),
                                                                                                      ReportLineDto::add
                                                                                                     )
                                                                                               )
                                                                         );
        return ReportDto.builder()
                        .watchLaserCertified(reportLineMap.getOrDefault(new Key(GoodsType.WATCH_CASE, HallmarkingMethod.LASER, true), ReportLineDto.zeros()))
                        .watchLaserNotCertified(reportLineMap.getOrDefault(new Key(GoodsType.WATCH_CASE, HallmarkingMethod.LASER, false), ReportLineDto.zeros()))
                        .watchMechanicalCertified(reportLineMap.getOrDefault(new Key(GoodsType.WATCH_CASE, HallmarkingMethod.MECHANICAL, true), ReportLineDto.zeros()))
                        .watchMechanicalNotCertified(reportLineMap.getOrDefault(new Key(GoodsType.WATCH_CASE, HallmarkingMethod.MECHANICAL, false), ReportLineDto.zeros()))
                        .jewelLaserCertified(reportLineMap.getOrDefault(new Key(GoodsType.JEWELRY, HallmarkingMethod.LASER, true), ReportLineDto.zeros()))
                        .jewelLaserNotCertified(reportLineMap.getOrDefault(new Key(GoodsType.JEWELRY, HallmarkingMethod.LASER, false), ReportLineDto.zeros()))
                        .jewelMechanicalCertified(reportLineMap.getOrDefault(new Key(GoodsType.JEWELRY, HallmarkingMethod.MECHANICAL, true), ReportLineDto.zeros()))
                        .jewelMechanicalNotCertified(reportLineMap.getOrDefault(new Key(GoodsType.JEWELRY, HallmarkingMethod.MECHANICAL, false), ReportLineDto.zeros()))
                        .other(reportLineMap.entrySet().stream()
                                            .filter(entry -> entry.getKey().goodsType == GoodsType.NO_CONTROL || entry.getKey().hallmarkingMethod == null)
                                            .map(Map.Entry::getValue)
                                            .reduce(ReportLineDto.zeros(), ReportLineDto::add))
                        .build();
    }
}
