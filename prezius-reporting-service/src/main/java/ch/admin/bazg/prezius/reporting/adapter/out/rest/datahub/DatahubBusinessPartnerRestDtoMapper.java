package ch.admin.bazg.prezius.reporting.adapter.out.rest.datahub;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.domain.valueobject.ControlOfficeGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
abstract class DatahubBusinessPartnerRestDtoMapper {

    private static final String BP_PERSON_TYPE = "1";
    private static final String BP_COMPANY_TYPE = "2";

    @Mapping(source = ".", target = "controlOfficeGroup")
    @Mapping(source = "dto", target = "name", qualifiedByName = "toBusinessPartnerName")
    @Mapping(source = "dto.id", target = "id")
    abstract BusinessPartner toModel(final Input input);

    protected ControlOfficeGroup toControlOfficeGroup(final Input input) {
        return Optional.ofNullable(input.dto.roles()).orElse(List.of())
                       .stream()
                       .filter(role -> role.businessId().equals(input.emkCode))
                       .map(DatahubBusinessPartnerRestDto.RoleRestDto::emkGroup)
                       .map(this::emkGroupCodeToControlOfficeGroup)
                       .findFirst()
                       .orElse(ControlOfficeGroup.BOTH);
    }

    protected ControlOfficeGroup emkGroupCodeToControlOfficeGroup(final String emkGroup) {
        return switch (emkGroup) {
            case "F" -> ControlOfficeGroup.BAZG;
            case "N" -> ControlOfficeGroup.NE;
            default -> ControlOfficeGroup.BOTH;
        };
    }

    @Named("toBusinessPartnerName")
    protected String toBusinessPartnerName(final DatahubBusinessPartnerRestDto dto) {
        if (dto.businessPartnerType() == null) {
            return "";
        }
        if (dto.businessPartnerType().equals(BP_PERSON_TYPE)) {
            return toName(dto.person());
        } else if (dto.businessPartnerType().equals(BP_COMPANY_TYPE)) {
            return toName(dto.company());
        } else {
            return "";
        }
    }

    protected String toName(final DatahubBusinessPartnerRestDto.CompanyRestDto companyDto) {
        return Optional.ofNullable(companyDto)
                       .map(DatahubBusinessPartnerRestDto.CompanyRestDto::name)
                       .orElse("");
    }

    protected String toName(final DatahubBusinessPartnerRestDto.PersonRestDto personDto) {
        return Optional.ofNullable(personDto)
                       .map(dto -> "%s %s".formatted(personDto.firstName(), personDto.lastName()))
                       .orElse("");
    }

    record Input(DatahubBusinessPartnerRestDto dto, String emkCode) {
    }

}
