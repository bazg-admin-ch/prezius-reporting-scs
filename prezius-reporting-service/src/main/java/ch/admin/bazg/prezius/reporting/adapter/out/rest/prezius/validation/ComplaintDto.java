package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.validation;

import lombok.Builder;

@Builder(toBuilder = true)
record ComplaintDto(
        Boolean immediateCompliance,
        int contestedPieceCount) {
}
