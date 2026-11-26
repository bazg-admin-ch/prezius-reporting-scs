package ch.admin.bazg.prezius.reporting.application.implementation.synchronizebusinesspartners;

import ch.admin.bazg.prezius.reporting.application.ports.out.external.GetBusinessPartnersRemotePort;
import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.domain.valueobject.ControlOfficeGroup;
import ch.admin.bazg.prezius.reporting.testenvironment.ITBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class SynchronizeBusinessPartnersHandlerIT extends ITBase {

    private final static String NEW_ID = "newId";
    private final static String NEW_NAME = "newName";
    private final static ControlOfficeGroup NEW_GROUP = ControlOfficeGroup.BOTH;

    private final SynchronizeBusinessPartnersHandler handlerUnderTest;

    @MockitoBean
    private final GetBusinessPartnersRemotePort getBusinessPartnersRemotePort;

    @Test
    void synchronizeOne_whenBusinessPartnerExists_thenUpdateData() {
        final BusinessPartner businessPartnerBefore = storeBusinessPartner();
        createDatahubBusinessPartner(businessPartnerBefore.getId());

        handlerUnderTest.synchronizeOne(businessPartnerBefore.getId());

        final Optional<BusinessPartner> businessPartnerAfter = this.businessPartnerRepository.findById(businessPartnerBefore.getId());
        assertThat(businessPartnerAfter).isPresent();
        assertThat(businessPartnerAfter.get().getId()).isEqualTo(businessPartnerBefore.getId());
        assertThat(businessPartnerAfter.get().getName()).isEqualTo(NEW_NAME);
        assertThat(businessPartnerAfter.get().getControlOfficeGroup()).isEqualTo(NEW_GROUP);
    }

    @Test
    void synchronizeOne_whenBusinessPartnerNotExists_thenCreateData() {
        createDatahubBusinessPartner(NEW_ID);

        handlerUnderTest.synchronizeOne(NEW_ID);

        final Optional<BusinessPartner> businessPartners = this.businessPartnerRepository.findById(NEW_ID);
        assertThat(businessPartners).isPresent();
        assertThat(businessPartners.get().getId()).isEqualTo(NEW_ID);
        assertThat(businessPartners.get().getName()).isEqualTo(NEW_NAME);
        assertThat(businessPartners.get().getControlOfficeGroup()).isEqualTo(NEW_GROUP);
    }

    @Test
    void synchronizeAll_whenBusinessPartnerExists_thenUpdateData() {
        final BusinessPartner businessPartnerBefore = storeBusinessPartner();
        createDatahubBusinessPartner(businessPartnerBefore.getId());

        handlerUnderTest.synchronizeAll();

        final Optional<BusinessPartner> businessPartner = this.businessPartnerRepository.findById(businessPartnerBefore.getId());
        assertThat(businessPartner).isPresent();
        assertThat(businessPartner.get().getId()).isEqualTo(businessPartnerBefore.getId());
        assertThat(businessPartner.get().getName()).isEqualTo(NEW_NAME);
        assertThat(businessPartner.get().getControlOfficeGroup()).isEqualTo(NEW_GROUP);
    }

    @Test
    void synchronizeAll_whenBusinessPartnerNotExists_thenCreateData() {
        createDatahubBusinessPartner(NEW_ID);

        handlerUnderTest.synchronizeAll();

        final Optional<BusinessPartner> businessPartner = this.businessPartnerRepository.findById(NEW_ID);
        assertThat(businessPartner).isPresent();
        assertThat(businessPartner.get().getId()).isEqualTo(NEW_ID);
        assertThat(businessPartner.get().getName()).isEqualTo(NEW_NAME);
        assertThat(businessPartner.get().getControlOfficeGroup()).isEqualTo(NEW_GROUP);
    }

    private BusinessPartner storeBusinessPartner() {
        return this.storageHelper.store(this.businessPartnerGenerator.generate());
    }

    private void createDatahubBusinessPartner(final String id) {
        final var businessPartner = BusinessPartner.builder()
                .id(id).name(NEW_NAME).controlOfficeGroup(NEW_GROUP).build();
        when(this.getBusinessPartnersRemotePort.getById(id)).thenReturn(Optional.of(businessPartner));
        when(this.getBusinessPartnersRemotePort.getAll()).thenReturn(List.of(businessPartner));
    }
}
