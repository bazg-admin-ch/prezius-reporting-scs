package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.order;

import ch.admin.bazg.prezius.common.dto.AnalysisOrderPublicRestDto;
import ch.admin.bazg.prezius.reporting.domain.valueobject.AnalysisOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface AnalysisOrderPublicRestDtoMapper {

    @Mapping(target = "businessId", source = "orderBusinessId")
    @Mapping(target = "gold", source = "au")
    @Mapping(target = "silver", source = "ag")
    @Mapping(target = "platinum", source = "pt")
    @Mapping(target = "palladium", source = "pd")
    @Mapping(target = "markedAsDoneAt", source = "setToDoneAt")
    AnalysisOrder toModel(AnalysisOrderPublicRestDto analysisOrderPublicRestDto);
}
