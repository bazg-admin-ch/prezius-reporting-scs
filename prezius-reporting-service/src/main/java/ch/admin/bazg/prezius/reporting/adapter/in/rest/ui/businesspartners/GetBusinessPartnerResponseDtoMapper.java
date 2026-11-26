package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.businesspartners;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
interface GetBusinessPartnerResponseDtoMapper {

    List<GetBusinessPartnerResponseDto> toDtoList(List<BusinessPartner> businessPartners);

    GetBusinessPartnerResponseDto toDto(BusinessPartner businessPartner);

}
