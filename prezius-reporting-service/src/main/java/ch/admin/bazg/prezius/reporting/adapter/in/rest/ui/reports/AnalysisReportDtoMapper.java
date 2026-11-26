package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.reporting.application.ports.in.GetAnalysisReportUseCase;
import org.mapstruct.Mapper;

import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.CONFORMITY_EVALUATION;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.DETERMINATION_ON_MELTING_POINT;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.DETERMINATION_ON_SAMPLES;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.SAMPLES_FROM_HALLMARKING;
import static ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType.SAMPLES_FROM_OTHER_PROCESSES;

@Mapper(componentModel = "spring")
interface AnalysisReportDtoMapper {

    default AnalysisReportDto toDto(GetAnalysisReportUseCase.Output report) {
        if (report == null || report.analysisTypeReports() == null) {
            return null;
        }
        return AnalysisReportDto.builder()
                                .conformityEvaluation(toDto(report.analysisTypeReports().get(CONFORMITY_EVALUATION)))
                                .determinationOnSamples(toDto(report.analysisTypeReports().get(DETERMINATION_ON_SAMPLES)))
                                .determinationOnMeltingPoint(toDto(report.analysisTypeReports().get(DETERMINATION_ON_MELTING_POINT)))
                                .samplesFromHallmarking(toDto(report.analysisTypeReports().get(SAMPLES_FROM_HALLMARKING)))
                                .samplesFromOtherProcesses(toDto(report.analysisTypeReports().get(SAMPLES_FROM_OTHER_PROCESSES)))
                                .total(toDto(report.totalReport()))
                                .build();
    }

    AnalysisReportCategoryDto toDto(GetAnalysisReportUseCase.Output.ReportCategoryOutput reportCategory);


}
