package ch.admin.bazg.prezius.reporting.domain.valueobject;

import ch.admin.bazg.prezius.common.domain.order.Tenant;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

@Getter
public enum ControlOfficeGroup {
    BOTH(Tenant.BAZG, Tenant.NE),
    BAZG(Tenant.BAZG),
    NE(Tenant.NE);

    final List<Tenant> tenants;

    ControlOfficeGroup(Tenant... tenants) {
        this.tenants = Stream.of(tenants).toList();
    }
}