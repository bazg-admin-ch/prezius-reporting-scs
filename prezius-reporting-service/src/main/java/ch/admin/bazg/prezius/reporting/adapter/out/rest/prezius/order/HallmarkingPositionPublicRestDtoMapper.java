package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.order;

import ch.admin.bazg.prezius.common.dto.HallmarkingPositionPublicRestDto;
import ch.admin.bazg.prezius.reporting.domain.valueobject.Position;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
interface HallmarkingPositionPublicRestDtoMapper {

    @Mapping(target = "orderId", source = "orderBusinessId")
    @Mapping(target = "gold", source = "au")
    @Mapping(target = "silver", source = "ag")
    @Mapping(target = "platinum", source = "pt")
    @Mapping(target = "palladium", source = "pd")
    @Mapping(target = "tool", ignore = true)
    Position toModel(HallmarkingPositionPublicRestDto hallmarkingPositionPublicRestDto);
}
