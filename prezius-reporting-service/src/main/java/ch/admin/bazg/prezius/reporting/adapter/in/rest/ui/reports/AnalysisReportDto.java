package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
record AnalysisReportDto(
        @JsonProperty("CONFORMITY_EVALUATION") AnalysisReportCategoryDto conformityEvaluation,
        @JsonProperty("DETERMINATION_ON_SAMPLES") AnalysisReportCategoryDto determinationOnSamples,
        @JsonProperty("DETERMINATION_ON_MELTING_POINT") AnalysisReportCategoryDto determinationOnMeltingPoint,
        @JsonProperty("SAMPLES_FROM_HALLMARKING") AnalysisReportCategoryDto samplesFromHallmarking,
        @JsonProperty("SAMPLES_FROM_OTHER_PROCESSES") AnalysisReportCategoryDto samplesFromOtherProcesses,
        AnalysisReportCategoryDto total
) {
}
