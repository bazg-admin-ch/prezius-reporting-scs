package ch.admin.bazg.prezius.reporting.application.implementation;

import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class DeleteInvoiceItemHandlerTest extends ITBase {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private HallmarkingReportItem reportItemRemained;
    private static final int BEFORE_TOTAL_REPORT_ITEM_COUNT = 3;
    private static final int DELETED_TOTAL_REPORT_ITEM_COUNT = 2;

    private final DeleteInvoiceItemHandler deleteInvoiceItemHandler;

    @BeforeEach
    void setup() {
        this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.orderId(ORDER_ID)));
        this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.orderId(ORDER_ID)));
        reportItemRemained = this.storageHelper.store(this.hallmarkingReportItemGenerator.generate(item -> item.orderId(UUID.randomUUID())));
    }

    @Test
    void process_whenReportItemsWithCorrectOrderIdExist_thenDeleteAll() {
        assertThat(hallmarkingReportItemRepository.count()).isEqualTo(BEFORE_TOTAL_REPORT_ITEM_COUNT);

        deleteInvoiceItemHandler.process(ORDER_ID.toString());

        final List<HallmarkingReportItem> allItems = hallmarkingReportItemRepository.findAll();
        assertThat(allItems)
                .hasSize(BEFORE_TOTAL_REPORT_ITEM_COUNT - DELETED_TOTAL_REPORT_ITEM_COUNT)
                .contains(reportItemRemained);
    }

    @Test
    void process_whenNoReportItemsWithCorrectOrderIdExist_thenDontDelete() {
        assertThat(hallmarkingReportItemRepository.count()).isEqualTo(BEFORE_TOTAL_REPORT_ITEM_COUNT);

        deleteInvoiceItemHandler.process(UUID.randomUUID().toString());

        final List<HallmarkingReportItem> allItems = hallmarkingReportItemRepository.findAll();
        assertThat(allItems).hasSize(BEFORE_TOTAL_REPORT_ITEM_COUNT);
    }
}
