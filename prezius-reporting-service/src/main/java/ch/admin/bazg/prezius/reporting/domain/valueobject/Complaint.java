package ch.admin.bazg.prezius.reporting.domain.valueobject;

import lombok.Builder;

@Builder
public record Complaint(Boolean immediateCompliance,
                        Integer contestedPieceCount) {
}
