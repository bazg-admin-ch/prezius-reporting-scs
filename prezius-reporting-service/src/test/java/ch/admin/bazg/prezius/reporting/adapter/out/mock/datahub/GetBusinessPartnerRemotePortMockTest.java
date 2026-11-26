package ch.admin.bazg.prezius.reporting.adapter.out.mock.datahub;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GetBusinessPartnerRemotePortMockTest {

    @Test
    void getById_whenCalledWIthExistingId_thenReturnBusinessPartner() {
        GetBusinessPartnerRemotePortMock adapterUnderTest = new GetBusinessPartnerRemotePortMock();
        final Optional<BusinessPartner> result = adapterUnderTest.getById("1000131412");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("1000131412");
    }
}
