package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
class GenerateHallmarkingLinesService {

    private final ReportLineOutputMapper outputMapper;

    public Stream<GenerateReportUseCase.ReportLineOutput> generate(final ControlOffice office, final List<HallmarkingReportItem> hallmarkingReportItems) {

        return Stream.of(
                             this.generateAllHallmarkingCountLineItems(hallmarkingReportItems, office), // generated hallmarking lines
                             this.generateHallmarkingRevenueLineItems(hallmarkingReportItems, office))
                     .flatMap(stream -> stream);
    }

    private Stream<GenerateReportUseCase.ReportLineOutput> generateHallmarkingRevenueLineItems(final List<HallmarkingReportItem> items, final ControlOffice office) {
        final BigDecimal totalCosts = items.stream()
                                           .filter(item -> item.getOffice() == office)
                                           .map(HallmarkingReportItem::getCost)
                                           .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Stream.of(
                GenerateReportUseCase.ReportLineOutput.createRevenueLine(office, GenerateReportUseCase.OperationOrGoodsType.HALLMARKING_DEPARTMENT, totalCosts, 0));
    }

    private Stream<GenerateReportUseCase.ReportLineOutput> generateAllHallmarkingCountLineItems(final List<HallmarkingReportItem> allReportItems, final ControlOffice office) {
        return HallmarkingLineAttributesCombination.getAllAttributeCombinations(office)
                                                   .flatMap(attributeCombination -> this.generateLineForHallmarkingAttributeCombination(allReportItems, attributeCombination));
    }

    private Stream<GenerateReportUseCase.ReportLineOutput> generateLineForHallmarkingAttributeCombination(final List<HallmarkingReportItem> allReportItems, final HallmarkingLineAttributesCombination attributeCombination) {
        final List<HallmarkingReportItem> filteredReportItems = allReportItems
                .stream()
                .filter(item -> item.getOffice() == attributeCombination.controlOffice()
                        && item.isCertifiedMaterial() == attributeCombination.certified()
                        && item.getHallmarkingLocation() == attributeCombination.hallmarkingLocation()
                        && item.getGoodsType() == attributeCombination.goodsType()
                        && item.getHallmarkingMethod() == attributeCombination.hallmarkingMethod())
                .toList();

        final Map<Metal, GenerateReportHandler.MetalCount> metalCountsMap = filteredReportItems
                .stream()
                .flatMap(this::getItemCountsStream)
                .collect(Collectors.groupingBy(GenerateReportHandler.ItemCounts::metal, Collectors.reducing(
                                                       GenerateReportHandler.MetalCount.empty(),
                                                       itemCounts -> new GenerateReportHandler.MetalCount(itemCounts.ch(), itemCounts.hmc()),
                                                       (c1, c2) -> new GenerateReportHandler.MetalCount(c1.ch() + c2.ch(), c1.hmc() + c2.hmc()))
                                              ));

        return Stream.of(
                outputMapper.toOutputLineItem(attributeCombination, extractCount(metalCountsMap, Metal.GOLD), Metal.GOLD),
                outputMapper.toOutputLineItem(attributeCombination, extractCount(metalCountsMap, Metal.SILVER), Metal.SILVER),
                outputMapper.toOutputLineItem(attributeCombination, extractCount(metalCountsMap, Metal.PALLADIUM), Metal.PALLADIUM),
                outputMapper.toOutputLineItem(attributeCombination, extractCount(metalCountsMap, Metal.PLATINUM), Metal.PLATINUM)
                        );
    }

    private GenerateReportHandler.MetalCount extractCount(final Map<Metal, GenerateReportHandler.MetalCount> metalCountsMap, final Metal metal) {
        return metalCountsMap.getOrDefault(metal, GenerateReportHandler.MetalCount.empty());
    }

    private Stream<GenerateReportHandler.ItemCounts> getItemCountsStream(final HallmarkingReportItem item) {
        final boolean isCh = item.getHallmarkingType() != HallmarkingType.SINGLE_INTERNATIONAL;
        final boolean isHmc = item.getHallmarkingType() != HallmarkingType.SINGLE_SWISS;

        return Arrays.stream(Metal.values()).map(metal -> {
            final int metalCount = item.getCountByMetal(metal);
            final int ch = isCh ? metalCount : 0;
            final int hmc = isHmc ? metalCount : 0;
            return new GenerateReportHandler.ItemCounts(metal, ch, hmc);
        });
    }


}
