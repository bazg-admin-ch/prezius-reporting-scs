package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.order;

import ch.admin.bazg.prezius.common.dto.AnalysisOrderPublicRestDto;
import ch.admin.bazg.prezius.common.dto.HallmarkingOrderPublicRestDto;
import ch.admin.bazg.prezius.common.orderPublicApiClient.PreziusOrderPublicApiClient;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetOrderPort;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisOrder;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class OrderServicePublicRestApiAdapter implements GetOrderPort {

    private final PreziusOrderPublicApiClient preziusOrderPublicAPIClient;
    private final HallmarkingOrderPublicRestDtoMapper hallmarkingOrderPublicRestDtoMapper;
    private final AnalysisOrderPublicRestDtoMapper analysisOrderPublicRestDtoMapper;

    public HallmarkingOrder getHallmarkingOrderById(final UUID orderId) {
        final HallmarkingOrderPublicRestDto responseDto = this.preziusOrderPublicAPIClient.fetchHallmarkingOrderById(orderId);
        return hallmarkingOrderPublicRestDtoMapper.toModel(responseDto);
    }

    public AnalysisOrder getAnalysisOrderById(final UUID orderId) {
        final AnalysisOrderPublicRestDto responseDto = this.preziusOrderPublicAPIClient.fetchAnalysisOrderById(orderId);
        return analysisOrderPublicRestDtoMapper.toModel(responseDto);
    }
}
