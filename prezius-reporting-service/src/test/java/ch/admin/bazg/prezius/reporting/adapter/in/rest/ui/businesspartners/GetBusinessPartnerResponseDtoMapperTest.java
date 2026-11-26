package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.businesspartners;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.domain.valueobject.ControlOfficeGroup;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {GetBusinessPartnerResponseDtoMapperImpl.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GetBusinessPartnerResponseDtoMapperTest {

    private final GetBusinessPartnerResponseDtoMapper mapperUnderTest;

    @Test
    void whenDtoPassed_thenMapToInput() {
        final var businessPartner1 = new BusinessPartner(
                "1001", "PARTNER1", ControlOfficeGroup.BOTH);
        final var businessPartner2 = new BusinessPartner(
                "1002", "PARTNER2", ControlOfficeGroup.NE);

        final var mapped = mapperUnderTest.toDtoList(List.of(businessPartner1, businessPartner2));

        assertThat(mapped).hasSize(2);

        assertThat(mapped.getFirst().id()).isEqualTo(businessPartner1.getId());
        assertThat(mapped.getFirst().name()).isEqualTo(businessPartner1.getName());

        assertThat(mapped.get(1).id()).isEqualTo(businessPartner2.getId());
        assertThat(mapped.get(1).name()).isEqualTo(businessPartner2.getName());
    }

}