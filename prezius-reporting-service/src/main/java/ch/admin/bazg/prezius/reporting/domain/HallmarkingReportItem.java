package ch.admin.bazg.prezius.reporting.domain;

import ch.admin.bazg.prezius.common.domain.hallmark.HallmarkingMethod;
import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.OrderType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.GoodsType;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingLocation;
import ch.admin.bazg.prezius.common.domain.order.hallmarking.HallmarkingType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@DiscriminatorValue("HALLMARKING")
@NoArgsConstructor
public class HallmarkingReportItem extends ReportItem {

    @Enumerated(EnumType.STRING)
    private GoodsType goodsType;
    @Enumerated(EnumType.STRING)
    protected HallmarkingType hallmarkingType;
    @Enumerated(EnumType.STRING)
    private HallmarkingMethod hallmarkingMethod;
    @Enumerated(EnumType.STRING)
    private HallmarkingLocation hallmarkingLocation;
    private boolean certifiedMaterial;

    @Builder(toBuilder = true)
    @SuppressWarnings("unused")
    /* only used for creating a builder, that includes all child and parent fields. Lombok does not offer an automatic way for this
      Builder used in OrderGenerator because we do not want to expose unnecessary setters.
      Let Intellij generate new constructors when fields change
    * */
    public HallmarkingReportItem(final UUID orderId, final int ag, final int au, final int pd, final int pt, final BigDecimal cost, final ControlOffice office,
                                 final LocalDateTime date, final String businessPartner, final GoodsType goodsType, final HallmarkingType hallmarkingType,
                                 final HallmarkingMethod hallmarkingMethod, final HallmarkingLocation hallmarkingLocation, final boolean certifiedMaterial) {

        super(OrderType.HALLMARKING, orderId, ag, au, pd, pt, cost, office, date, businessPartner);
        this.goodsType = goodsType;
        this.hallmarkingType = hallmarkingType;
        this.hallmarkingMethod = hallmarkingMethod;
        this.hallmarkingLocation = hallmarkingLocation;
        this.certifiedMaterial = certifiedMaterial;
    }

    public static HallmarkingReportItem createNew(final UUID orderId, final int ag, final int au, final int pd, final int pt, final BigDecimal cost, final ControlOffice office,
                                                  final LocalDateTime date, final String businessPartner, final GoodsType goodsType, final HallmarkingType hallmarkingType,
                                                  final HallmarkingMethod hallmarkingMethod, final HallmarkingLocation hallmarkingLocation, final boolean certifiedMaterial) {

        final HallmarkingReportItem newItem = new HallmarkingReportItem();
        newItem.orderId = orderId;
        newItem.ag = ag;
        newItem.au = au;
        newItem.pd = pd;
        newItem.pt = pt;
        newItem.cost = cost;
        newItem.office = office;
        newItem.date = date;
        newItem.businessPartner = businessPartner;
        newItem.goodsType = goodsType;
        newItem.hallmarkingType = hallmarkingType;
        newItem.hallmarkingMethod = hallmarkingMethod;
        newItem.hallmarkingLocation = hallmarkingLocation;
        newItem.certifiedMaterial = certifiedMaterial;

        return newItem;
    }

    public int hallmarkCount() {
        if(hallmarkingType == HallmarkingType.DOUBLE) {
            return 2;
        } else {
            return 1;
        }
    }
}
