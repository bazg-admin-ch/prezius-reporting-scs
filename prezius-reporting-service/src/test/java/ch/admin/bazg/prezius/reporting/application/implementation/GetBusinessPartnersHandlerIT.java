package ch.admin.bazg.prezius.reporting.application.implementation;

import ch.admin.bazg.prezius.common.authorization.AuthorizationInfo;
import ch.admin.bazg.prezius.common.domain.order.Tenant;
import ch.admin.bazg.prezius.reporting.application.ports.out.persistence.GetBusinessPartnersLocalPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.persistence.StoreBusinessPartnersPort;
import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.domain.valueobject.ControlOfficeGroup;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GetBusinessPartnersHandlerIT extends ITBase {

    private static final String BENITA_BUSINESS_PARTNER_WITH_INTERMEDIARY_BP = "1000131422";
    private final GetBusinessPartnersHandler handlerUnderTest;

    @MockitoBean
    private GetBusinessPartnersLocalPort getBusinessPartnersLocalPort;
    @MockitoBean
    private StoreBusinessPartnersPort storeBusinessPartnersPort;

    @Test
    void whenAuthenticatedAsAdminUserRole_thenReturnAllBusinessPartner() {
        when(getBusinessPartnersLocalPort.getAll()).thenReturn(List.of(
                createBusinessPartner("bp1", ControlOfficeGroup.BAZG),
                createBusinessPartner("bp2", ControlOfficeGroup.NE),
                createBusinessPartner("bp3", ControlOfficeGroup.BOTH)));
        final var authorizationInfo = new AuthorizationInfo(true, List.of(), List.of());

        final List<BusinessPartner> result = handlerUnderTest.getBusinessPartners(authorizationInfo);

        assertThat(result).hasSize(3);
    }

    @Test
    void whenAuthenticatedAsBazgUserRole_thenReturnOnlyBazgBusinessPartners() {
        final var bpDtoBoth = createBusinessPartner("bp1", ControlOfficeGroup.BOTH);
        final var bpDtoBazg = createBusinessPartner("bp2", ControlOfficeGroup.BAZG);
        final var bpDtoNe = createBusinessPartner("bp2", ControlOfficeGroup.NE);
        final var authorizationInfo = new AuthorizationInfo(false, List.of(), List.of(Tenant.BAZG));
        when(getBusinessPartnersLocalPort.getAll()).thenReturn(List.of(bpDtoBoth, bpDtoBazg, bpDtoNe));

        final List<BusinessPartner> result = handlerUnderTest.getBusinessPartners(authorizationInfo);

        assertThat(result)
                .hasSize(2)
                .map(BusinessPartner::getId)
                .contains(bpDtoBoth.getId(), bpDtoBazg.getId());
    }

    @Test
    void whenAuthenticatedAsBusinessPartner_thenReturnOnlyOneBusinessPartner() {
        final var bpFromToken = createBusinessPartner(BENITA_BUSINESS_PARTNER_WITH_INTERMEDIARY_BP, ControlOfficeGroup.BOTH);
        final var bpOther1 = createBusinessPartner("bp2", ControlOfficeGroup.BOTH);
        final var bpOther2 = createBusinessPartner("bp3", ControlOfficeGroup.NE);
        when(getBusinessPartnersLocalPort.getAll()).thenReturn(List.of(bpFromToken, bpOther1, bpOther2));
        final var authorizationInfo = new AuthorizationInfo(false, List.of(BENITA_BUSINESS_PARTNER_WITH_INTERMEDIARY_BP), List.of());

        final List<BusinessPartner> result = handlerUnderTest.getBusinessPartners(authorizationInfo);

        assertThat(result)
                .hasSize(1)
                .map(BusinessPartner::getId)
                .containsOnly(bpFromToken.getId());
    }

    private BusinessPartner createBusinessPartner(final String businessPartnerId, final ControlOfficeGroup controlOfficeGroup) {
        return new BusinessPartner(businessPartnerId, "name: " + businessPartnerId, controlOfficeGroup);
    }

}