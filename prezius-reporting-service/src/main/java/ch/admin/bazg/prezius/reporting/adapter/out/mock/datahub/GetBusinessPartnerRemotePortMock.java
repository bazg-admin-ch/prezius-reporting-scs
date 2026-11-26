package ch.admin.bazg.prezius.reporting.adapter.out.mock.datahub;

import ch.admin.bazg.prezius.reporting.application.ports.out.external.GetBusinessPartnersRemotePort;
import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.domain.valueobject.ControlOfficeGroup;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Profile("datahub-mock")
@Service
@Slf4j
class GetBusinessPartnerRemotePortMock implements GetBusinessPartnersRemotePort {

    private final List<BusinessPartner> sampleBps = Arrays.asList(
            new BusinessPartner("1000131412", "Altersheim Mels", ControlOfficeGroup.NE),
            new BusinessPartner("1000131417", "BAUMET AG", ControlOfficeGroup.BOTH),
            new BusinessPartner("1000131418", "MET Asset Management Holding AG", ControlOfficeGroup.BAZG),
            new BusinessPartner("1000131419", "Bricona met GmbH", ControlOfficeGroup.BOTH),
            new BusinessPartner("1000131423", "Verein Schlau", ControlOfficeGroup.BOTH),
            new BusinessPartner("1000131422", "Benita Edel", ControlOfficeGroup.BOTH),
            new BusinessPartner("1000096366", "Pinguin Test 1 CS", ControlOfficeGroup.BOTH),
            new BusinessPartner("1000096367", "Pinguin Test 2 CS", ControlOfficeGroup.BOTH),
            new BusinessPartner("1000096368", "Pinguin Test 3 CS", ControlOfficeGroup.BOTH));

    @PostConstruct
    void postConstruct() {
        log.info("Using {} implementation for DataHubBusinessPartnerRestApiAdapter", this.getClass().getSimpleName());
    }

    @Override
    public List<BusinessPartner> getAll() {
        return sampleBps;
    }

    @Override
    public Optional<BusinessPartner> getById(final String id) {
        return sampleBps.stream()
                        .filter(bp -> bp.getId().equals(id))
                        .findFirst();
    }
}
