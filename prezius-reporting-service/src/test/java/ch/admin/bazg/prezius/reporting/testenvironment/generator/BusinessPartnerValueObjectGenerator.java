package ch.admin.bazg.prezius.reporting.testenvironment.generator;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.domain.valueobject.ControlOfficeGroup;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BusinessPartnerValueObjectGenerator {
    public BusinessPartner generate() {
        return generate(null);
    }

    public BusinessPartner generate(Function<BusinessPartner.BusinessPartnerBuilder, BusinessPartner.BusinessPartnerBuilder> decorator) {
        final var businessPartner = new BusinessPartner("1000123456",
                                                        "My Company",
                                                        ControlOfficeGroup.BAZG);
        if (decorator != null) {
            return decorator.apply(businessPartner.toBuilder()).build();
        }
        return businessPartner;
    }
}
