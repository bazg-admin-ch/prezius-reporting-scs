package ch.admin.bazg.prezius.reporting.adapter.out.jpa;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, String> {
}
