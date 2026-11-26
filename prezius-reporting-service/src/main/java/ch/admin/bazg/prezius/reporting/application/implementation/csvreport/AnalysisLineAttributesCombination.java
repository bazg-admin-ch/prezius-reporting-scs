package ch.admin.bazg.prezius.reporting.application.implementation.csvreport;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;

import java.util.stream.Stream;

record AnalysisLineAttributesCombination(
            ControlOffice controlOffice,
            AnalysisType analysisType
    ) {

    public static Stream<AnalysisLineAttributesCombination> getAllAttributeCombinations(final ControlOffice office) {
        return Stream.of(
                AnalysisType.CONFORMITY_EVALUATION,
                AnalysisType.DETERMINATION_ON_SAMPLES,
                AnalysisType.DETERMINATION_ON_MELTING_POINT,
                AnalysisType.SAMPLES_FROM_HALLMARKING,
                AnalysisType.SAMPLES_FROM_OTHER_PROCESSES)
                    .flatMap(analysisType ->
                                     Stream.of(new AnalysisLineAttributesCombination(office, analysisType)));
    }
}