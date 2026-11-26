package ch.admin.bazg.prezius.reporting.application.implementation;

import ch.admin.bazg.prezius.common.authorization.AuthorizationInfo;
import ch.admin.bazg.prezius.common.domain.order.Tenant;
import ch.admin.bazg.prezius.reporting.application.ports.in.GetBusinessPartnersQuery;
import ch.admin.bazg.prezius.reporting.application.ports.out.persistence.GetBusinessPartnersLocalPort;
import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bit.jeap.db.tx.TransactionalReadReplica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@TransactionalReadReplica
class GetBusinessPartnersHandler implements GetBusinessPartnersQuery {

    private final GetBusinessPartnersLocalPort getBusinessPartnersLocalPort;

    @Override
    public List<BusinessPartner> getBusinessPartners(final AuthorizationInfo authorizationInfo) {
        return getBusinessPartnersLocalPort.getAll().stream()
                                                   .filter(bp -> canAccess(authorizationInfo, bp))
                                                   .toList();
    }

    private boolean canAccess(final AuthorizationInfo authorizationInfo, final BusinessPartner businessPartner) {
        return authorizationInfo.canAccessAll()
                || hasAllowedTenant(businessPartner, authorizationInfo.allowedTenants())
                || isAllowedBusinessPartner(businessPartner, authorizationInfo.allowedBusinessPartners());

    }

    private boolean isAllowedBusinessPartner(final BusinessPartner businessPartner, final List<String> allowedBusinessPartners) {
        return allowedBusinessPartners.contains(businessPartner.getId());
    }

    private boolean hasAllowedTenant(final BusinessPartner businessPartner, final List<Tenant> allowedTenants) {
        return businessPartner.getControlOfficeGroup().getTenants().stream()
                              .anyMatch(allowedTenants::contains);
    }
}
