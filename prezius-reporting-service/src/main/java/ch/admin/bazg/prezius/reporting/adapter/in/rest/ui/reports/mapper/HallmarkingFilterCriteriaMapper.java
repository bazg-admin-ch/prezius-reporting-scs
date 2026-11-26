package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports.mapper;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import ch.admin.bazg.quadrel.filter.QdFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class HallmarkingFilterCriteriaMapper {
    private static final DateTimeFormatter dateFilterFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

    public HallmarkingFilterCriteria toFilterCriteria(final QdFilter filter) {
        final var hallmarkingFilterCriteriaBuilder = HallmarkingFilterCriteria.builder();

        filter.categories().forEach(qdFilterCategory -> {
            switch (qdFilterCategory.category()) {
                case "officeName" -> {
                    final var controlOffices = qdFilterCategory.items()
                                                               .stream()
                                                               .map(ControlOffice::valueOf)
                                                               .toList();
                    hallmarkingFilterCriteriaBuilder.officeFilter(controlOffices);
                }
                case "hallmarkingType" -> {
                    final var hallmarkingTypes = qdFilterCategory.items()
                                                                 .stream()
                                                                 .map(HallmarkingType::valueOf)
                                                                 .toList();
                    hallmarkingFilterCriteriaBuilder.hallmarkingTypes(hallmarkingTypes);
                }
                case "date" -> {
                    final String startDateString = qdFilterCategory.items().getFirst();
                    final String endDateString = qdFilterCategory.items().get(1);
                    if (StringUtils.hasText(startDateString)) {
                        final var startDate = LocalDate.parse(startDateString, dateFilterFormatter);
                        hallmarkingFilterCriteriaBuilder.dateAfterFilter(startDate.atStartOfDay());
                    }
                    if (StringUtils.hasText(endDateString)) {
                        final var endDate = LocalDate.parse(endDateString, dateFilterFormatter);
                        hallmarkingFilterCriteriaBuilder.dateBeforeFilter(endDate.atTime(LocalTime.MAX));
                    }
                }
                case "businessPartner" -> hallmarkingFilterCriteriaBuilder.businessPartnerFilter(qdFilterCategory.items());
                default -> throw new IllegalArgumentException("Unexpected filter category: " + qdFilterCategory.category());
            }
        });

        return hallmarkingFilterCriteriaBuilder.build();
    }

}
