package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.billing;

import java.math.BigDecimal;

record BillingDto(BigDecimal totalCostNoVat, BigDecimal totalCostWithVat) {
}
