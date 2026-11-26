package ch.admin.bazg.prezius.reporting.adapter.out.rest.datahub;

import java.util.List;

record DatahubBusinessPartnerRestDto(
        String id,
        String businessPartnerType,
        PersonRestDto person,
        CompanyRestDto company,
        List<RoleRestDto> roles
){
        record RoleRestDto(
                String technicalId,
                String businessId,
                String emkGroup
        ) {}

        record CompanyRestDto(
                String name,
                String legalForm
        ) {}

        record PersonRestDto(
                String firstName,
                String lastName

        ) {}
}


