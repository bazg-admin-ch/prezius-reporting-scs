package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.billing;

import ch.admin.bazg.prezius.reporting.domain.valueobject.Billing;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface BillingDtoToModelMapper {

    Billing toModel(BillingDto dto);

}
