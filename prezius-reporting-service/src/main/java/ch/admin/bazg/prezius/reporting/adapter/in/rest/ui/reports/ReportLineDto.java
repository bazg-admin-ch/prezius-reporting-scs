package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
record ReportLineDto(int ag, int au, int pd, int pt, BigDecimal totalCostsWithVat) {

    public static ReportLineDto zeros() {
        return ReportLineDto.builder().totalCostsWithVat(BigDecimal.ZERO).build();
    }

    public int getTotal() {
        return this.ag + this.au + this.pd + this.pt;
    }

    public ReportLineDto add(final HallmarkingReportItem reportItem) {
        final int ag = this.ag + reportItem.getAg() * reportItem.hallmarkCount();
        final int au = this.au + reportItem.getAu() * reportItem.hallmarkCount();
        final int pd = this.pd + reportItem.getPd() * reportItem.hallmarkCount();
        final int pt = this.pt + reportItem.getPt() * reportItem.hallmarkCount();
        return ReportLineDto.builder()
                            .ag(ag)
                            .au(au)
                            .pd(pd)
                            .pt(pt)
                            .totalCostsWithVat(reportItem.getCost())
                            .build();
    }

    public ReportLineDto add(final ReportLineDto dto) {
        return ReportLineDto.builder()
                            .ag(this.ag + dto.ag)
                            .au(this.au + dto.au)
                            .pd(this.pd + dto.pd)
                            .pt(this.pt + dto.pt)
                            .totalCostsWithVat(this.totalCostsWithVat.add(dto.totalCostsWithVat))
                            .build();
    }
}
