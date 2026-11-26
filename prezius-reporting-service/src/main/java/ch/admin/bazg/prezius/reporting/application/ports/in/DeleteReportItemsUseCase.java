package ch.admin.bazg.prezius.reporting.application.ports.in;

public interface DeleteReportItemsUseCase {
    void process(final String orderId);
}
