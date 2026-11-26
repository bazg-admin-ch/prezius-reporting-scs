package ch.admin.bazg.prezius.reporting.adapter.out.rest.prezius.billing;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {BillingDtoToModelMapperImpl.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BillingDtoToModelMapperTest {

    private final BillingDtoToModelMapper mapperUnderTest;

    @Test
    void toModelMap_whenValidDto_thenMapToModel() {
        final var billingDto = new BillingDto(BigDecimal.valueOf(3.00), BigDecimal.valueOf(5.00));

        final var mappedResult = mapperUnderTest.toModel(billingDto);

        assertThat(mappedResult.totalCostNoVat()).isEqualByComparingTo(BigDecimal.valueOf(3.00));
        assertThat(mappedResult.totalCostWithVat()).isEqualByComparingTo(BigDecimal.valueOf(5.00));
    }

}