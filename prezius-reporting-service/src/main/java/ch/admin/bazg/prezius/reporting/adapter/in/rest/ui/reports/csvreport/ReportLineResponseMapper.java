package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports.csvreport;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.reporting.application.ports.in.GenerateReportUseCase;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
class ReportLineResponseMapper {

    private static final String EMPTY_COLUMN = "";
    private static final Locale REPORT_LOCALE = Locale.GERMAN;

    private final MessageSource messageSource;

    ReportLineResponseMapper(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /* The CSV has a fixed number of predefined column, so here we are mapping a value to each column. Some columns are empty */
    public String toResponseLineString(final GenerateReportUseCase.ReportLineOutput lineItem) {
        return getColumnStrings(lineItem).stream()
                .map(column -> column == null ? EMPTY_COLUMN : column)
                .collect(Collectors.joining(";"));
    }

    private List<String> getColumnStrings(final GenerateReportUseCase.ReportLineOutput lineItem) {
        final List<String> columns = new ArrayList<>(Collections.nCopies(9, EMPTY_COLUMN));

        columns.set(0, lineItem.controlOffice().getName());
        columns.set(1, this.toSectionText(lineItem.section()));
        columns.set(2, this.toOperationOrGoodsTypeText(lineItem.operationOrGoodsType()));
        columns.set(3, this.toCertificationText(lineItem.certification()));
        columns.set(4, this.toHallmarkingLocationText(lineItem.hallmarkingLocation()));
        columns.set(5, this.toHallmarkingMethodText(lineItem.hallmarkingMethod()));
        columns.set(6, this.toMetalText(lineItem.metal()));
        columns.set(7, this.doubleToText(lineItem.countCh(), lineItem.section()));
        columns.set(8, this.integerToText(lineItem.countHmc()));

        return columns;
    }

    private String toSectionText(final GenerateReportUseCase.Section section) {
        if (section == null) {
            return EMPTY_COLUMN;
        }
        return messageSource.getMessage("section." + section.name(), null, REPORT_LOCALE);
    }

    private String toOperationOrGoodsTypeText(final GenerateReportUseCase.OperationOrGoodsType operationOrGoodsType) {
        if (operationOrGoodsType == null) {
            return EMPTY_COLUMN;
        }
        return messageSource.getMessage("operation-or-goods-type." + operationOrGoodsType.name(), null, REPORT_LOCALE);
    }

    private String toCertificationText(final GenerateReportUseCase.Certification certification) {
        if (certification == null) {
            return EMPTY_COLUMN;
        }
        return messageSource.getMessage("certification." + certification.name(), null, REPORT_LOCALE);
    }

    private String toHallmarkingLocationText(final HallmarkingLocation hallmarkingLocation) {
        if (hallmarkingLocation == null) {
            return EMPTY_COLUMN;
        }
        return messageSource.getMessage("hallmarking-location." + hallmarkingLocation.name(), null, REPORT_LOCALE);
    }

    private String toHallmarkingMethodText(final HallmarkingMethod hallmarkingMethod) {
        if (hallmarkingMethod == null) {
            return EMPTY_COLUMN;
        }
        return messageSource.getMessage("hallmarking-method." + hallmarkingMethod.name(), null, REPORT_LOCALE);
    }

    private String toMetalText(final Metal metal) {
        return Optional.ofNullable(metal)
                .map(Metal::getSymbol)
                .orElse(EMPTY_COLUMN);
    }

    private String doubleToText(final BigDecimal value, final GenerateReportUseCase.Section section) {
        final String format = section == GenerateReportUseCase.Section.REVENUE && value.compareTo(BigDecimal.ZERO) != 0
                ? "%.2f" : "%.0f";
        return format.formatted(value.doubleValue());
    }

    private String integerToText(final Integer value) {
        return Optional.ofNullable(value)
                       .map(String::valueOf)
                       .orElse(EMPTY_COLUMN);
    }

}
