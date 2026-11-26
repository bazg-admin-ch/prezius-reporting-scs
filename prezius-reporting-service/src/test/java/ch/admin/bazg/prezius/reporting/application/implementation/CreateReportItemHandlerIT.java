package ch.admin.bazg.prezius.reporting.application.implementation;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import ch.admin.bazg.prezius.reporting.application.ports.in.CreateReportItemUseCase;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetBillingPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetComplaintsPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetHallmarkingsPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.GetOrderPort;
import ch.admin.bazg.prezius.reporting.domain.HallmarkingReportItem;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Billing;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Complaint;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Hallmarking;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingOrder;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Position;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.AnalysisOrderGenerator;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@AllArgsConstructor(onConstructor_ = @Autowired)
class CreateReportItemHandlerIT extends ITBase {

    private static final String TEST_ORDER_BUSINESS_ID = "12345";
    private static final UUID TEST_ORDER_ID = UUID.randomUUID();
    private static final UUID TEST_POSITION_UUID = UUID.randomUUID();

    @MockitoBean
    private GetOrderPort getOrderPort;
    @MockitoBean
    private GetHallmarkingsPort getHallmarkingsPort;
    @MockitoBean
    private GetComplaintsPort getComplaintsPort;
    @MockitoBean
    private GetBillingPort getBillingPort;

    private final AnalysisOrderGenerator analysisOrderGenerator = new AnalysisOrderGenerator();

    private CreateReportItemHandler handlerUnderTest;

    @Test
    void whenTwoHallmarkingPositionsExist_thenCorrectReportingItemsSaved() {
        hallmarkingReportItemRepository.deleteAll();
        final UUID firstPositionId = UUID.randomUUID();
        final UUID secondPositionId = UUID.randomUUID();
        final Position firstPosition = createPositionDto().withId(firstPositionId).withOrderId(TEST_ORDER_BUSINESS_ID).withPieceCount(210).withGold(true);
        final Position secondPosition = createPositionDto().withId(secondPositionId).withOrderId(TEST_ORDER_BUSINESS_ID).withPieceCount(300).withSilver(true);

        final HallmarkingOrder order = createHallmarkingOrderDto().withPositions(List.of(firstPosition, secondPosition));
        when(getOrderPort.getHallmarkingOrderById(TEST_ORDER_ID)).thenReturn(order);

        when(getBillingPort.getByOrderId(TEST_ORDER_ID, OrderType.HALLMARKING)).thenReturn(new Billing(BigDecimal.valueOf(1600), BigDecimal.valueOf(1780)));
        when(getHallmarkingsPort.getHallmarkingsPerPosition(order.businessId())).thenReturn(Map.of(
                firstPositionId, List.of(Hallmarking.builder().hallmarkingType(HallmarkingType.SINGLE_SWISS).hallmarkingMethod(HallmarkingMethod.MECHANICAL).build()),
                secondPositionId, List.of(Hallmarking.builder().hallmarkingType(HallmarkingType.DOUBLE).hallmarkingMethod(HallmarkingMethod.LASER).build())
        ));
        when(getComplaintsPort.getComplaintsPerPosition(TEST_ORDER_ID)).thenReturn(Map.of(
                firstPositionId, Complaint.builder().contestedPieceCount(10).immediateCompliance(false).build())
        );

        handlerUnderTest.process(TEST_ORDER_ID, TEST_ORDER_BUSINESS_ID, CreateReportItemUseCase.BillingType.HALLMARKING_ORDER, true);

        final var savedReportItems = hallmarkingReportItemRepository.findAll();
        assertThat(savedReportItems).satisfiesExactlyInAnyOrder(
                item1 -> this.assertReportItem(order, firstPosition, item1, new BigDecimal("1780.00"),
                                               200, 0,
                                               HallmarkingMethod.MECHANICAL, HallmarkingType.SINGLE_SWISS),
                item2 -> this.assertReportItem(order, secondPosition, item2, new BigDecimal("0.00"),
                                               0, 300,
                                               HallmarkingMethod.LASER, HallmarkingType.DOUBLE)
            );

    }

    @Test
    void whenTwoHallmarkingPositionsOneWithoutHallmarking_thenCorrectReportingItemsSaved() {
        hallmarkingReportItemRepository.deleteAll();
        final UUID firstPositionId = UUID.randomUUID();
        final UUID secondPositionId = UUID.randomUUID();
        final Position firstPosition = createPositionDto().withId(firstPositionId).withOrderId(TEST_ORDER_BUSINESS_ID).withPieceCount(210).withGold(true);
        final Position secondPosition = createPositionDto().withId(secondPositionId).withOrderId(TEST_ORDER_BUSINESS_ID).withPieceCount(300).withSilver(true);

        final HallmarkingOrder order = createHallmarkingOrderDto().withPositions(List.of(firstPosition, secondPosition));
        when(getOrderPort.getHallmarkingOrderById(TEST_ORDER_ID)).thenReturn(order);
        when(getBillingPort.getByOrderId(TEST_ORDER_ID, OrderType.HALLMARKING)).thenReturn(new Billing(BigDecimal.valueOf(1600), BigDecimal.valueOf(1780)));
        when(getHallmarkingsPort.getHallmarkingsPerPosition(order.businessId())).thenReturn(Map.of(
                firstPositionId, List.of(),
                secondPositionId, List.of(Hallmarking.builder().hallmarkingType(HallmarkingType.DOUBLE).hallmarkingMethod(HallmarkingMethod.LASER).build())
        ));
        when(getComplaintsPort.getComplaintsPerPosition(TEST_ORDER_ID)).thenReturn(Map.of(
                firstPositionId, Complaint.builder().contestedPieceCount(10).immediateCompliance(false).build())
        );

        handlerUnderTest.process(TEST_ORDER_ID, TEST_ORDER_BUSINESS_ID, CreateReportItemUseCase.BillingType.HALLMARKING_ORDER, true);

        final var savedReportItems = hallmarkingReportItemRepository.findAll();

        assertThat(savedReportItems).satisfiesExactlyInAnyOrder(
                item1 -> this.assertReportItem(order, firstPosition, item1, new BigDecimal("1780.00"),
                                               200, 0,
                                               null, null)
                , item2 -> this.assertReportItem(order, firstPosition, item2, new BigDecimal("0.00"),
                                                 0, 300,
                                                 HallmarkingMethod.LASER, HallmarkingType.DOUBLE)
                );

    }

    @Test
    void whenTwoAnalysisPositionsExist_thenCorrectReportingItemsSaved() {
        analysisReportItemRepository.deleteAll();
        final UUID firstPositionId = UUID.randomUUID();
        final UUID secondPositionId = UUID.randomUUID();
        final Position firstPosition = createPositionDto().withId(firstPositionId).withOrderId(TEST_ORDER_BUSINESS_ID).withPieceCount(150).withGold(true);
        final Position secondPosition = createPositionDto().withId(secondPositionId).withOrderId(TEST_ORDER_BUSINESS_ID).withPieceCount(300).withSilver(true);

        final var order = analysisOrderGenerator.generate(
                ao -> ao
                        .businessId(TEST_ORDER_BUSINESS_ID)
                        .gold(false)
                        .silver(true)
                        .palladium(true)
                        .platinum(false)
                        .markedAsDoneAt( LocalDateTime.parse("2024-05-04T16:13:55.000"))
                        .positions(List.of(firstPosition, secondPosition))
                                                         );

        when(getOrderPort.getAnalysisOrderById(TEST_ORDER_ID)).thenReturn(order);

        when(getBillingPort.getByOrderId(TEST_ORDER_ID, OrderType.ANALYSIS)).thenReturn(new Billing(BigDecimal.valueOf(1600), BigDecimal.valueOf(1780)));

        when(getComplaintsPort.getComplaintsPerPosition(TEST_ORDER_ID)).thenReturn(Map.of(
                firstPositionId, Complaint.builder().contestedPieceCount(10).immediateCompliance(false).build())
        );

        handlerUnderTest.process(TEST_ORDER_ID, TEST_ORDER_BUSINESS_ID, CreateReportItemUseCase.BillingType.ANALYSIS_ORDER, true);

        final var savedReportItems = analysisReportItemRepository.findAll();
        assertThat(savedReportItems).hasSize(1);
        assertThat(savedReportItems.getFirst()).satisfies(item -> {
                    assertThat(item.getOrderId()).isEqualTo(TEST_ORDER_ID);
                    assertThat(item.getAg()).isEqualTo(2);
                    assertThat(item.getAu()).isZero();
                    assertThat(item.getPd()).isEqualTo(2);
                    assertThat(item.getPt()).isZero();
                    assertThat(item.getCost()).isEqualTo(new BigDecimal("1780.00"));
                    assertThat(item.getOffice()).isEqualTo(order.office());
                    assertThat(item.getDate()).isEqualTo(order.markedAsDoneAt());
                    assertThat(item.getBusinessPartner()).isEqualTo(order.businessPartnerId());
                    assertThat(item.getAnalysisType()).isEqualTo(order.analysisType());
                });
    }

    @Test
    void whenTwoAnalysisPositionsExist_thenCorrectReportingItemsSavedWithCostZero() {
        analysisReportItemRepository.deleteAll();
        final UUID firstPositionId = UUID.randomUUID();
        final UUID secondPositionId = UUID.randomUUID();
        final Position firstPosition = createPositionDto().withId(firstPositionId).withOrderId(TEST_ORDER_BUSINESS_ID).withPieceCount(150).withGold(true);
        final Position secondPosition = createPositionDto().withId(secondPositionId).withOrderId(TEST_ORDER_BUSINESS_ID).withPieceCount(300).withSilver(true);
        final var order = analysisOrderGenerator.generate(
                ao -> ao
                        .businessId(TEST_ORDER_BUSINESS_ID)
                        .gold(false)
                        .silver(true)
                        .palladium(true)
                        .platinum(false)
                        .markedAsDoneAt(LocalDateTime.parse("2024-05-04T16:13:55.000"))
                        .positions(List.of(firstPosition, secondPosition)));

        when(getOrderPort.getAnalysisOrderById(TEST_ORDER_ID)).thenReturn(order);

        when(getComplaintsPort.getComplaintsPerPosition(TEST_ORDER_ID)).thenReturn(Map.of(
                firstPositionId, Complaint.builder().contestedPieceCount(10).immediateCompliance(false).build())
        );

        handlerUnderTest.process(TEST_ORDER_ID, TEST_ORDER_BUSINESS_ID, CreateReportItemUseCase.BillingType.ANALYSIS_ORDER, false);

        final var savedReportItems = analysisReportItemRepository.findAll();
        assertThat(savedReportItems).hasSize(1);
        assertThat(savedReportItems.getFirst()).satisfies(item -> {
            assertThat(item.getOrderId()).isEqualTo(TEST_ORDER_ID);
            assertThat(item.getAg()).isEqualTo(2);
            assertThat(item.getAu()).isZero();
            assertThat(item.getPd()).isEqualTo(2);
            assertThat(item.getPt()).isZero();
            assertThat(item.getCost()).isEqualTo(new BigDecimal("0.00"));
            assertThat(item.getOffice()).isEqualTo(order.office());
            assertThat(item.getDate()).isEqualTo(order.markedAsDoneAt());
            assertThat(item.getBusinessPartner()).isEqualTo(order.businessPartnerId());
            assertThat(item.getAnalysisType()).isEqualTo(order.analysisType());
        });
    }

    @Test
    void whenBillingTypeIsApplication_thenDoNothing() {
        handlerUnderTest.process(TEST_ORDER_ID, TEST_ORDER_BUSINESS_ID, CreateReportItemUseCase.BillingType.APPLICATION, false);

        assertThat(analysisReportItemRepository.count()).isZero();
        assertThat(hallmarkingReportItemRepository.count()).isZero();
    }

    @Test
    void givenManualBilling_whenProcessed_thenDoNotCreateReportItems() {
        handlerUnderTest.process(TEST_ORDER_ID, TEST_ORDER_BUSINESS_ID, CreateReportItemUseCase.BillingType.MANUAL, true);

        assertThat(analysisReportItemRepository.count()).isZero();
        assertThat(hallmarkingReportItemRepository.count()).isZero();
        verifyNoInteractions(getBillingPort, getOrderPort, getHallmarkingsPort, getComplaintsPort);
    }

    @Test
    void whenBillingTypeIsRegistration_thenDoNothing() {
        handlerUnderTest.process(TEST_ORDER_ID, TEST_ORDER_BUSINESS_ID, CreateReportItemUseCase.BillingType.REGISTRATION, false);

        assertThat(analysisReportItemRepository.count()).isZero();
        assertThat(hallmarkingReportItemRepository.count()).isZero();
    }

    private static HallmarkingOrder createHallmarkingOrderDto() {
        return new HallmarkingOrder(TEST_ORDER_BUSINESS_ID,
                                    ControlOffice.GDPC, "123", false,
                                    HallmarkingLocation.OFFICE, LocalDateTime.parse("2024-05-02T11:42:00.933"),
                                    List.of());
    }

    private static Position createPositionDto() {
        return Position.builder()
                .id(TEST_POSITION_UUID)
                .orderId(TEST_ORDER_BUSINESS_ID)
                .batchCount(1)
                .pieceCount(1)
                .number(1)
                .goodsType(GoodsType.WATCH_CASE)
                .silver(false)
                .gold(false)
                .platinum(false)
                .palladium(false)
                .tool("whatever")
                .build();
    }

    private void assertReportItem(final HallmarkingOrder order, final Position position, final HallmarkingReportItem reportItem,
                                  final BigDecimal expectedCost, final int expectedAuCount, final int expectedAgCount,
                                  final HallmarkingMethod expectedHallmarkingMethod, final HallmarkingType expectedHallmarkingType) {
        assertThat(reportItem.getOrderId()).isEqualTo(TEST_ORDER_ID);
        assertThat(reportItem.getCost()).isEqualTo(expectedCost);
        assertThat(reportItem.getAu()).isEqualTo(expectedAuCount);
        assertThat(reportItem.getAg()).isEqualTo(expectedAgCount);
        assertThat(reportItem.getPt()).isZero();
        assertThat(reportItem.getPd()).isZero();
        assertThat(reportItem.getGoodsType()).isEqualTo(position.goodsType());
        assertThat(reportItem.getHallmarkingMethod()).isEqualTo(expectedHallmarkingMethod);
        assertThat(reportItem.getHallmarkingType()).isEqualTo(expectedHallmarkingType);
        assertThat(reportItem.getDate()).isEqualTo(order.validationDate());
        assertThat(reportItem.isCertifiedMaterial()).isFalse();
        assertThat(reportItem.getHallmarkingLocation()).isEqualTo(order.hallmarkingLocation());
        assertThat(reportItem.getOffice()).isEqualTo(order.office());
        assertThat(reportItem.getBusinessPartner()).isEqualTo(order.businessPartnerId());
    }
}
