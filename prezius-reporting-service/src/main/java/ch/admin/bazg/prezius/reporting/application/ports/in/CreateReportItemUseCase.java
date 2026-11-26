package ch.admin.bazg.prezius.reporting.application.ports.in;

import java.util.UUID;

public interface CreateReportItemUseCase {
    void process(UUID billableId, String billableBusinessId, BillingType billingType, boolean billingExists);

    enum BillingType {
        HALLMARKING_ORDER,
        ANALYSIS_ORDER,
        APPLICATION,
        MANUAL,
        REGISTRATION
    }
}
