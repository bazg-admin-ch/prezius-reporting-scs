package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
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
class GenerateAnalysisLinesService {

    private final ReportLineOutputMapper outputMapper;

    public Stream<GenerateReportUseCase.ReportLineOutput> generate(final ControlOffice office, final List<AnalysisReportItem> analysisReportItems) {

        return Stream.of(
                             this.generateAllAnalysisCountLineItems(analysisReportItems, office),
                             this.generateAnalysisRevenueLineItems(analysisReportItems, office))
                     .flatMap(stream -> stream);
    }

    private Stream<GenerateReportUseCase.ReportLineOutput> generateAnalysisRevenueLineItems(final List<AnalysisReportItem> items, final ControlOffice office) {
        final BigDecimal totalCosts = items.stream()
                                           .filter(item -> item.getOffice() == office)
                                           .map(AnalysisReportItem::getCost)
                                           .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Stream.of(
                GenerateReportUseCase.ReportLineOutput.createRevenueLine(office, GenerateReportUseCase.OperationOrGoodsType.ANALYSIS_DEPARTMENT, totalCosts, 0));
    }

    private Stream<GenerateReportUseCase.ReportLineOutput> generateAllAnalysisCountLineItems(final List<AnalysisReportItem> allReportItems, final ControlOffice office) {
        return AnalysisLineAttributesCombination.getAllAttributeCombinations(office)
                                                   .flatMap(attributeCombination -> this.generateLineForAnalysisAttributeCombination(allReportItems, attributeCombination));
    }

    private Stream<GenerateReportUseCase.ReportLineOutput> generateLineForAnalysisAttributeCombination(final List<AnalysisReportItem> allReportItems, final AnalysisLineAttributesCombination attributeCombination) {
        final List<AnalysisReportItem> filteredReportItems = allReportItems
                .stream()
                .filter(item -> item.getOffice() == attributeCombination.controlOffice()
                        && item.getAnalysisType() == attributeCombination.analysisType())
                .toList();

        final Map<Metal, Integer> metalCountsMap = filteredReportItems
                .stream()
                .flatMap(this::getItemCountsStream)
                .collect(Collectors.groupingBy(ItemCount::metal, Collectors.summingInt(ItemCount::analysisCount)));

        return Stream.of(
                outputMapper.toOutputLineItem(attributeCombination, extractCount(metalCountsMap, Metal.GOLD), Metal.GOLD),
                outputMapper.toOutputLineItem(attributeCombination, extractCount(metalCountsMap, Metal.SILVER), Metal.SILVER),
                outputMapper.toOutputLineItem(attributeCombination, extractCount(metalCountsMap, Metal.PALLADIUM), Metal.PALLADIUM),
                outputMapper.toOutputLineItem(attributeCombination, extractCount(metalCountsMap, Metal.PLATINUM), Metal.PLATINUM)
                        );
    }

    private int extractCount(final Map<Metal, Integer> metalCountsMap, final Metal metal) {
        return metalCountsMap.getOrDefault(metal, 0);
    }

    private Stream<ItemCount> getItemCountsStream(final AnalysisReportItem reportItem) {
        return Arrays.stream(Metal.values()).map(metal -> new ItemCount(metal, reportItem.getCountByMetal(metal)));
    }

    private record ItemCount(Metal metal, int analysisCount) {}

}
