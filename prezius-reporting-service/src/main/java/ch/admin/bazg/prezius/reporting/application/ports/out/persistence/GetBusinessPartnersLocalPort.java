package ch.admin.bazg.prezius.reporting.application.ports.out.persistence;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GetBusinessPartnersLocalPort {

    Map<String, BusinessPartner> findAllById(List<String> ids);
    List<BusinessPartner> getAll();

    Optional<BusinessPartner> getById(String businessPartnerId);
}
