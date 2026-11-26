package ch.admin.bazg.prezius.reporting.application.implementation;

import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.reporting.application.ports.in.CreateReportItemUseCase;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetBillingPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetComplaintsPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetHallmarkingsPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetOrderPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.StoreAnalysisReportItemPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.StoreHallmarkingReportItemPort;
import ch.admin.bazg.prezius.reporting.domain.AnalysisReportItem;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisOrder;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Billing;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Complaint;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Hallmarking;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingOrder;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
class CreateReportItemHandler implements CreateReportItemUseCase {

    private final GetBillingPort getBillingPort;
    private final GetComplaintsPort getComplaintsPort;
    private final GetHallmarkingsPort getHallmarkingsPort;
    private final GetOrderPort getOrderPort;
    private final StoreHallmarkingReportItemPort storeHallmarkingReportItemPort;
    private final StoreAnalysisReportItemPort storeAnalysisReportItemPort;

    @Override
    public void process(final UUID billableId, final String billableBusinessId, final BillingType billingType, final boolean billingExists) {

        if (billingType == BillingType.APPLICATION || billingType == BillingType.MANUAL || billingType == BillingType.REGISTRATION) {
            log.warn("No report item to create for billing type {} for {}", billingType, billableId);
            return;
        }
        final OrderType orderType = billingType == BillingType.HALLMARKING_ORDER ? OrderType.HALLMARKING : OrderType.ANALYSIS;
        final Billing billing = billingExists ? getBillingPort.getByOrderId(billableId, orderType) : null;

        if (orderType == OrderType.HALLMARKING) {
            createHallmarkingReportItems(billableId, billableBusinessId, billing);
        } else {
            createAnalysisReportItem(billableId, billing);
        }

    }

    private void createHallmarkingReportItems(final UUID orderId, final String orderBusinessId, final Billing billing) {
        final HallmarkingOrder order = getOrderPort.getHallmarkingOrderById(orderId);
        final var complaintsPerPosition = getComplaintsPort.getComplaintsPerPosition(orderId);
        final var hallmarkingsPerPosition = getHallmarkingsPort.getHallmarkingsPerPosition(orderBusinessId);

        final var reportItems = IntStream.range(0, order.positions().size())
                .mapToObj(i -> createHallmarkingReportItemsForPosition(orderId, billing, order, order.positions().get(i),
                                                                           hallmarkingsPerPosition, complaintsPerPosition, i == 0))
                .flatMap(List::stream)
                .toList();

        storeHallmarkingReportItemPort.storeAll(reportItems);
        log.info("Created {} analysis report items for order {}", reportItems.size(), orderId);
    }

    private List<HallmarkingReportItem> createHallmarkingReportItemsForPosition(final UUID orderId, final Billing billing, final HallmarkingOrder order,
                                                                          final Position position, final Map<UUID, List<Hallmarking>> hallmarkingsPerPosition,
                                                                          final Map<UUID, Complaint> complaintsPerPosition, final boolean firstPosition) {

        final List<Hallmarking> hallmarkings = hallmarkingsPerPosition.getOrDefault(position.id(), List.of());
        final int pieceCount = reportPieceCount(complaintsPerPosition, position);

        // if there is no hallmarking, we still have to create one item to have the total cost reported in the CSV file
        if (hallmarkings.isEmpty()) {
            return List.of(createHallmarkingReportItem(orderId, order, position, null, pieceCount, itemCost(firstPosition, billing)));
        }

        return IntStream.range(0, hallmarkings.size())
                .mapToObj(i -> createHallmarkingReportItem(orderId, order, position, hallmarkings.get(i), pieceCount,
                                                               itemCost(firstPosition && (i == 0), billing)))
                .toList();
    }

    private HallmarkingReportItem createHallmarkingReportItem(final UUID orderId, final HallmarkingOrder order, final Position position,
                                                              final Hallmarking hallmarking, final int pieceCount, final BigDecimal cost) {

        return HallmarkingReportItem.createNew(orderId, metalCount(position.silver(), pieceCount),
                metalCount(position.gold(), pieceCount), metalCount(position.palladium(), pieceCount), metalCount(position.platinum(),
                pieceCount), cost, order.office(), order.validationDate(), order.businessPartnerId(), position.goodsType(),
                hallmarking == null ? null : hallmarking.hallmarkingType(), hallmarking == null ? null : hallmarking.hallmarkingMethod(),
                order.hallmarkingLocation(), order.certifiedMaterial());
    }

    private void createAnalysisReportItem(final UUID orderId, final Billing billing) {
        final AnalysisOrder order = getOrderPort.getAnalysisOrderById(orderId);
        final var reportItem = AnalysisReportItem.createNew(
                orderId, metalCount(order.silver(), order.positions().size()),
                metalCount(order.gold(), order.positions().size()), metalCount(order.palladium(), order.positions().size()),
                metalCount(order.platinum(), order.positions().size()), billing == null ? BigDecimal.ZERO : billing.totalCostWithVat(),
                order.office(), order.markedAsDoneAt(), order.businessPartnerId(), order.analysisType());

        storeAnalysisReportItemPort.storeOne(reportItem);
        log.info("Created analysis report item for order {}", orderId);
    }

    private int reportPieceCount(final Map<UUID, Complaint> complaintsPerPosition, final Position position) {
        return position.pieceCount() - contestedPieceCount(complaintsPerPosition, position);
    }

    private int contestedPieceCount(final Map<UUID, Complaint> complaintsPerPosition, final Position position) {
        return Optional.ofNullable(complaintsPerPosition.get(position.id()))
                    .filter(complaint -> Boolean.FALSE.equals(complaint.immediateCompliance()))
                    .map(Complaint::contestedPieceCount)
                    .orElse(0);
    }

    private BigDecimal itemCost(final boolean firstItem, final Billing billing) {
        // the billing only gives us the total cost for all hallmarkings of all positions,
        // thus we only set the amount to the first item we build for the billing
        if (firstItem) {
            if (billing == null) {
                return BigDecimal.ZERO;
            }
            return billing.totalCostWithVat();
        } else {
            return BigDecimal.ZERO;
        }
    }

    private int metalCount(final boolean hasMetal, final int pieceCount) {
        return hasMetal ? pieceCount : 0;
    }
}
