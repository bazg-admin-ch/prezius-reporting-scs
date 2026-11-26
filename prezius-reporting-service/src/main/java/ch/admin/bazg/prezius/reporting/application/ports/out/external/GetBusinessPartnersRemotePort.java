package ch.admin.bazg.prezius.reporting.application.ports.out.external;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;

import java.util.List;
import java.util.Optional;

public interface GetBusinessPartnersRemotePort {

    List<BusinessPartner> getAll();

    Optional<BusinessPartner> getById(String businessPartnerId);
}