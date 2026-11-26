package ch.admin.bazg.prezius.reporting.domain;

import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import ch.admin.bazg.prezius.common.domain.order.Metal;
import ch.admin.bazg.prezius.common.domain.order.OrderType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_item")
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE) // For @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class ReportItem {

    @Id
    protected final UUID id = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, insertable = false, updatable = false)
    protected OrderType type;
    protected UUID orderId;
    // report data START
    protected int ag;
    protected int au;
    protected int pd;
    protected int pt;
    // report data END

    // CSV report data START
    protected BigDecimal cost;
    // CSV report data end

    // filter data START
    @Enumerated(EnumType.STRING)
    protected ControlOffice office;
    protected LocalDateTime date;
    protected String businessPartner;
    // filter data END

    public int getCountByMetal(final Metal metal) {
        return switch (metal) {
            case Metal.GOLD -> au;
            case Metal.SILVER -> ag;
            case Metal.PALLADIUM -> pd;
            case Metal.PLATINUM -> pt;
        };
    }

}
