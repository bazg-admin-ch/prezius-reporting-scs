package ch.admin.bazg.prezius.reporting.application.implementation;

import ch.admin.bazg.prezius.reporting.application.ports.in.DeleteReportItemsUseCase;
import ch.admin.bazg.prezius.reporting.application.ports.out.DeleteReportItemsPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetAllHallmarkingReportItemsPort;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
class DeleteInvoiceItemHandler implements DeleteReportItemsUseCase {

    private final DeleteReportItemsPort deleteReportItemsPort;
    private final GetAllHallmarkingReportItemsPort getAllHallmarkingReportItemsPort;

    @Override
    public void process(final String orderId) {
        final UUID orderIdAsUuid = UUID.fromString(orderId);
        final List<HallmarkingReportItem> reportItemsToDelete = getAllHallmarkingReportItemsPort.getAllByOrderId(orderIdAsUuid);

        if (!reportItemsToDelete.isEmpty()) {
            deleteReportItemsPort.deleteAllByOrderId(orderIdAsUuid);
            reportItemsToDelete.forEach(item -> log.info("ReportItem {} is deleted relative to orderId {}", item.getId(), item.getOrderId()));
        }
    }
}
