package ch.admin.bazg.prezius.reporting.adapter.out.rest.datahub;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.domain.valueobject.ControlOfficeGroup;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {DatahubBusinessPartnerRestDtoMapperImpl.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class DatahubBusinessPartnerRestDtoMapperTest {

    private final DatahubBusinessPartnerRestDtoMapper mapperUnderTest;

    private static final String EMK_CODE = "ZKH16";

    @Test
    void whenEmkGroupIsF_thenControlOfficeGroupIsBAZG() {
        final DatahubBusinessPartnerRestDto businessPartnerRestDto = new DatahubBusinessPartnerRestDto(
                "testId",
                "2",
                null,
                new DatahubBusinessPartnerRestDto.CompanyRestDto("TestCompany", null),
                List.of(new DatahubBusinessPartnerRestDto.RoleRestDto(EMK_CODE, EMK_CODE, "F"))
        );
        final BusinessPartner businessPartner = mapperUnderTest.toModel(new DatahubBusinessPartnerRestDtoMapper.Input(businessPartnerRestDto, EMK_CODE));
        assertThat(businessPartner.getControlOfficeGroup()).isEqualTo(ControlOfficeGroup.BAZG);
        assertThat(businessPartner.getId()).isEqualTo("testId");
        assertThat(businessPartner.getName()).isEqualTo("TestCompany");
    }

    @Test
    void whenEmkGroupIsN_thenControlOfficeGroupIsNE() {
        final DatahubBusinessPartnerRestDto businessPartnerRestDto = new DatahubBusinessPartnerRestDto(
                "testId",
                "2",
                null,
                new DatahubBusinessPartnerRestDto.CompanyRestDto("TestCompany", null),
                List.of(new DatahubBusinessPartnerRestDto.RoleRestDto(EMK_CODE, EMK_CODE, "N"))
        );
        final BusinessPartner businessPartner = mapperUnderTest.toModel(new DatahubBusinessPartnerRestDtoMapper.Input(businessPartnerRestDto, EMK_CODE));
        assertThat(businessPartner.getControlOfficeGroup()).isEqualTo(ControlOfficeGroup.NE);
        assertThat(businessPartner.getId()).isEqualTo("testId");
        assertThat(businessPartner.getName()).isEqualTo("TestCompany");
    }

    @Test
    void whenEmkGroupIsNotFNorN_thenControlOfficeGroupIsBOTH() {
        final DatahubBusinessPartnerRestDto businessPartnerRestDto = new DatahubBusinessPartnerRestDto(
                "testId",
                "2",
                null,
                new DatahubBusinessPartnerRestDto.CompanyRestDto("TestCompany", null),
                List.of(new DatahubBusinessPartnerRestDto.RoleRestDto(EMK_CODE, EMK_CODE, "B"))
        );
        final BusinessPartner businessPartner = mapperUnderTest.toModel(new DatahubBusinessPartnerRestDtoMapper.Input(businessPartnerRestDto, EMK_CODE));
        assertThat(businessPartner.getControlOfficeGroup()).isEqualTo(ControlOfficeGroup.BOTH);
        assertThat(businessPartner.getId()).isEqualTo("testId");
        assertThat(businessPartner.getName()).isEqualTo("TestCompany");
    }

    @Test
    void whenBusinessPartnerTypeIs1_thenGetNameFromPerson() {
        final DatahubBusinessPartnerRestDto businessPartnerRestDto = new DatahubBusinessPartnerRestDto(
                "testId",
                "1",
                new DatahubBusinessPartnerRestDto.PersonRestDto("first", "last"),
                null,
                List.of(new DatahubBusinessPartnerRestDto.RoleRestDto(EMK_CODE, EMK_CODE, "B"))
        );
        final BusinessPartner businessPartner = mapperUnderTest.toModel(new DatahubBusinessPartnerRestDtoMapper.Input(businessPartnerRestDto, EMK_CODE));
        assertThat(businessPartner.getControlOfficeGroup()).isEqualTo(ControlOfficeGroup.BOTH);
        assertThat(businessPartner.getId()).isEqualTo("testId");
        assertThat(businessPartner.getName()).isEqualTo("first last");
    }

    @ParameterizedTest(name = "When businessPartnerType = {0}, then set name to empty string")
    @MethodSource("provideParameters")
    void whenBusinessPartnerTypeIsInvalid(final String businessPartnerType) {
        final DatahubBusinessPartnerRestDto businessPartnerRestDto = new DatahubBusinessPartnerRestDto(
                "testId",
                businessPartnerType,
                null,
                null,
                List.of(new DatahubBusinessPartnerRestDto.RoleRestDto(EMK_CODE, EMK_CODE, "B"))
        );
        final BusinessPartner businessPartner = mapperUnderTest.toModel(new DatahubBusinessPartnerRestDtoMapper.Input(businessPartnerRestDto, EMK_CODE));
        assertThat(businessPartner.getControlOfficeGroup()).isEqualTo(ControlOfficeGroup.BOTH);
        assertThat(businessPartner.getName()).isEmpty();
    }

    private static Stream<Arguments> provideParameters() {
        final String nullString = null;
        return Stream.of(
                Arguments.of("1"),
                Arguments.of("2"),
                Arguments.of(nullString)
                        );
    }

}
