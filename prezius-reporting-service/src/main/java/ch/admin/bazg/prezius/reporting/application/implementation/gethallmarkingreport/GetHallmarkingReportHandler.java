package ch.admin.bazg.prezius.reporting.application.implementation.gethallmarkingreport;

import ch.admin.bazg.prezius.reporting.application.ports.in.GetHallmarkingReportQuery;
import ch.admin.bazg.prezius.reporting.application.ports.out.persistence.SearchHallmarkingReportItemsPort;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingFilterCriteria;
import ch.admin.bit.jeap.db.tx.TransactionalReadReplica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@TransactionalReadReplica
class GetHallmarkingReportHandler implements GetHallmarkingReportQuery {

    private final SearchHallmarkingReportItemsPort searchHallmarkingReportItemsPort;

    @Override
    public List<HallmarkingReportItem> getHallmarkingReportItems(final HallmarkingFilterCriteria filterCriteria) {
        return searchHallmarkingReportItemsPort.search(filterCriteria);
    }
}
