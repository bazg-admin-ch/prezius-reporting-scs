package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.hallmarking;

import ch.admin.bazg.prezius.reporting.domain.valueobject.Hallmarking;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
interface HallmarkingsPerPositionMapper {

    Map<UUID, List<Hallmarking>> toModelMap(final Map<UUID, List<GetHallmarkingOutputRestDto>> mapDto);

    List<Hallmarking> toModelList(final List<GetHallmarkingOutputRestDto> dtoList);

    Hallmarking toModel(final GetHallmarkingOutputRestDto dto);

}
