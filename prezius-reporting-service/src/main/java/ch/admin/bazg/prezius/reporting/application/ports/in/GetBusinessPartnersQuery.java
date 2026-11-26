package ch.admin.bazg.prezius.reporting.application.ports.in;

import ch.admin.bazg.prezius.common.authorization.AuthorizationInfo;
import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;

import java.util.List;

public interface GetBusinessPartnersQuery {

    List<BusinessPartner> getBusinessPartners(AuthorizationInfo authorizationInfo);

}