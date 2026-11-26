package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring")
interface ReportLineOutputMapper {

    @Mapping(target = "section", constant = "ANALYSIS")
    @Mapping(target = "operationOrGoodsType", source = "attributesCombination.analysisType")
    @Mapping(target = "countCh", source = "count")
    GenerateReportUseCase.ReportLineOutput toOutputLineItem(AnalysisLineAttributesCombination attributesCombination, int count, Metal metal);

    @Mapping(target = "section", constant = "HALLMARKING")
    @Mapping(target = "operationOrGoodsType", source = "attributesCombination.goodsType")
    @Mapping(target = "certification", source = "attributesCombination.certified")
    @Mapping(target = "countCh", source = "metalCount.ch")
    @Mapping(target = "countHmc", source = "metalCount.hmc")
    GenerateReportUseCase.ReportLineOutput toOutputLineItem(HallmarkingLineAttributesCombination attributesCombination, GenerateReportHandler.MetalCount metalCount, Metal metal);

    @ValueMapping(source = "JEWELRY", target = "OTHER_GOODS")
    @ValueMapping(source = "WATCH_CASE", target = "WATCH_CASE")
    @ValueMapping(source = "NO_CONTROL", target = MappingConstants.NULL)
    GenerateReportUseCase.OperationOrGoodsType toOperationOrGoodsType(GoodsType goodsType);

    static GenerateReportUseCase.Certification toCertification(final boolean certified) {
        return certified ? GenerateReportUseCase.Certification.YES : GenerateReportUseCase.Certification.NO;
    }

    @ValueMapping(source = "CONFORMITY_EVALUATION", target = "CONFORMITY_EVALUATION")
    @ValueMapping(source = "DETERMINATION_ON_SAMPLES", target = "DETERMINATION_ON_SAMPLES")
    @ValueMapping(source = "DETERMINATION_ON_MELTING_POINT", target = "DETERMINATION_ON_MELTING_POINT")
    @ValueMapping(source = "SAMPLES_FROM_HALLMARKING", target = "SAMPLES_FROM_HALLMARKING")
    @ValueMapping(source = "SAMPLES_FROM_OTHER_PROCESSES", target = "SAMPLES_FROM_OTHER_PROCESSES")
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    GenerateReportUseCase.OperationOrGoodsType toOperationOrGoodsType(AnalysisType analysisType);

}