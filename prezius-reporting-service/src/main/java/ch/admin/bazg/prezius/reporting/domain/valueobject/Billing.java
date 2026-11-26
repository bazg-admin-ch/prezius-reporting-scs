package ch.admin.bazg.prezius.reporting.domain.valueobject;

import java.math.BigDecimal;

public record Billing(BigDecimal totalCostNoVat, BigDecimal totalCostWithVat) {
}