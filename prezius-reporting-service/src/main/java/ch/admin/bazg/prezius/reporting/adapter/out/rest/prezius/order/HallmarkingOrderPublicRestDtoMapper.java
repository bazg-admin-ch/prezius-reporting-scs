package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.order;

import ch.admin.bazg.prezius.common.dto.HallmarkingOrderPublicRestDto;
import ch.admin.bazg.prezius.reporting.domain.valueobject.HallmarkingOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {HallmarkingPositionPublicRestDtoMapper.class})
interface HallmarkingOrderPublicRestDtoMapper {

    @Mapping(target = "businessId", source = "orderBusinessId")
    HallmarkingOrder toModel(HallmarkingOrderPublicRestDto hallmarkingOrderPublicRestDto);
}
