package ch.admin.bazg.prezius.reporting.adapter.out.jpa;

import ch.admin.bazg.prezius.reporting.application.ports.out.persistence.GetBusinessPartnersLocalPort;
import ch.admin.bazg.prezius.reporting.application.ports.out.persistence.StoreBusinessPartnersPort;
import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bit.jeap.db.tx.TransactionalReadReplica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Component
@Transactional
@RequiredArgsConstructor
class BusinessPartnerJpaAdapter implements GetBusinessPartnersLocalPort, StoreBusinessPartnersPort {

    private final BusinessPartnerRepository businessPartnerRepository;

    @Override
    @TransactionalReadReplica
    public List<BusinessPartner> getAll() {
        return businessPartnerRepository.findAll();
    }

    @Override
    @TransactionalReadReplica
    public Optional<BusinessPartner> getById(final String businessPartnerId) {
        return businessPartnerRepository.findById(businessPartnerId);
    }

    @Override
    @TransactionalReadReplica
    public Map<String, BusinessPartner> findAllById(final List<String> ids) {
        return businessPartnerRepository.findAllById(ids).stream()
                                        .collect(toMap(BusinessPartner::getId, bp -> bp));
    }

    @Override
    public void storeOne(final BusinessPartner businessPartner) {
        businessPartnerRepository.save(businessPartner);
    }

    @Override
    public void storeAll(final List<BusinessPartner> businessPartners) {
        businessPartnerRepository.saveAll(businessPartners);
    }
}
