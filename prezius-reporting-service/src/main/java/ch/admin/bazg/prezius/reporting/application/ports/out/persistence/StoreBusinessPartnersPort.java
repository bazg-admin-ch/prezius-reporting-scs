package ch.admin.bazg.prezius.reporting.application.ports.out.persistence;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;

import java.util.List;

public interface StoreBusinessPartnersPort {

    void storeOne(BusinessPartner businessPartner);

    void storeAll(List<BusinessPartner> businessPartners);
}
