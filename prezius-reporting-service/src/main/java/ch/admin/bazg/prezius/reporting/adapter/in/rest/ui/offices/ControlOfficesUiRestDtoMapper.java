package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.offices;


import ch.admin.bazg.prezius.common.domain.order.ControlOffice;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
interface ControlOfficesUiRestDtoMapper {
    
    List<ControlOfficeUiRestDto> toRestDtos(List<ControlOffice> controlOffices);
}
