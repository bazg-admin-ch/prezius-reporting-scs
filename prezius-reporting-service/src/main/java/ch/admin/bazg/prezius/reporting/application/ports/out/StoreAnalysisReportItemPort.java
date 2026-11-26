package ch.admin.bazg.prezius.reporting.application.ports.out;

import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;

import java.util.List;

public interface StoreAnalysisReportItemPort {

    void storeOne(AnalysisReportItem reportItem);
    void storeAll(List<AnalysisReportItem> reportItems);
}
