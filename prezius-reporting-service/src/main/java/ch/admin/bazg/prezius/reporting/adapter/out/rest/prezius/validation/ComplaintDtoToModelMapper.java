package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.validation;

import ch.admin.bazg.prezius.reporting.domain.valueobject.Complaint;
import org.mapstruct.Mapper;

import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
interface ComplaintDtoToModelMapper {

    Map<UUID, Complaint> toModelMap(final Map<UUID, ComplaintDto> complaintMap);

    Complaint toModel(final ComplaintDto complaintDto);

}
