package ch.admin.bazg.prezius.reporting.domain;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.common.domain.order.analysis.AnalysisType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DiscriminatorValue("ANALYSIS")
@NoArgsConstructor
@Getter
public class AnalysisReportItem extends ReportItem {

    @Enumerated(EnumType.STRING)
    private AnalysisType analysisType;

    @Builder(toBuilder = true)
    public AnalysisReportItem(final UUID orderId, final int ag, final int au, final int pd,
                              final int pt, final BigDecimal cost, final ControlOffice office, final LocalDateTime date,
                              final String businessPartner, final AnalysisType analysisType) {

        super(OrderType.ANALYSIS, orderId, ag, au, pd, pt, cost, office, date, businessPartner);
        this.analysisType = analysisType;
    }

    public static AnalysisReportItem createNew(final UUID orderId, final int ag, final int au, final int pd, final int pt,
                                               final BigDecimal cost, final ControlOffice office, final LocalDateTime date,
                                               final String businessPartner, final AnalysisType analysisType) {

        final AnalysisReportItem newItem = new AnalysisReportItem();
        newItem.orderId = orderId;
        newItem.ag = ag;
        newItem.au = au;
        newItem.pd = pd;
        newItem.pt = pt;
        newItem.cost = cost;
        newItem.office = office;
        newItem.date = date;
        newItem.businessPartner = businessPartner;
        newItem.analysisType = analysisType;

        return newItem;
    }

}
