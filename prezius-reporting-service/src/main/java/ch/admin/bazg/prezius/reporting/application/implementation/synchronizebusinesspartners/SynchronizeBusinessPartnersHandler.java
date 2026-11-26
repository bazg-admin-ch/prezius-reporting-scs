package ch.admin.bazg.prezius.reporting.application.implementation.synchronizebusinesspartners;

import ch.admin.bazg.prezius.reporting.application.ports.in.SynchronizeBusinessPartnersUseCase;
import ch.admin.bazg.prezius.reporting.application.ports.out.external.GetBusinessPartnersRemotePort;
import ch.admin.bazg.prezius.reporting.application.ports.out.persistence.StoreBusinessPartnersPort;
import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
class SynchronizeBusinessPartnersHandler implements SynchronizeBusinessPartnersUseCase {

    private final GetBusinessPartnersRemotePort getBusinessPartnersRemotePort;
    private final StoreBusinessPartnersPort storeBusinessPartnersPort;

    @Override
    public void synchronizeOne(final String businessPartnerId) {
        getBusinessPartnersRemotePort.getById(businessPartnerId).ifPresentOrElse(
                storeBusinessPartnersPort::storeOne,
                () -> log.warn("Could not synchronize business partner {} because it is not in datahub", businessPartnerId));
    }

    @Override
    public void synchronizeAll() {
        final List<BusinessPartner> businessPartners = getBusinessPartnersRemotePort.getAll()
                                                                                    .stream()
                                                                                    .toList();

        storeBusinessPartnersPort.storeAll(businessPartners);
    }
}
