package ch.admin.bazg.prezius.reporting.adapter.out.jpa;

import ch.admin.bazg.prezius.reporting.domain.BusinessPartner;
import ch.admin.bazg.prezius.reporting.testenvironment.ITPersistenceBase;
import ch.admin.bazg.prezius.reporting.testenvironment.generator.BusinessPartnerGenerator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BusinessPartnerJpaAdapterIT extends ITPersistenceBase {

    private final BusinessPartnerJpaAdapter adapterUnderTest;
    private final BusinessPartnerGenerator businessPartnerGenerator;
    private final BusinessPartnerRepository businessPartnerRepository;

    @Test
    void getAll_whenCalled_shouldReturnAllBPs() {
        final List<BusinessPartner> bps = generateBPs();
        businessPartnerRepository.saveAll(bps);

        final List<BusinessPartner> storedBPs = adapterUnderTest.getAll();

        verifyBPs(storedBPs, bps);
    }

    @Test
    void getById_whenCalled_shouldReturnBPIfExists() {
        final BusinessPartner bp = businessPartnerGenerator.generate();
        businessPartnerRepository.save(bp);

        final Optional<BusinessPartner> storedBP = adapterUnderTest.getById(bp.getId());

        assertThat(storedBP).isPresent();
        assertThat(storedBP.get().getId()).isEqualTo(bp.getId());
    }

    @Test
    void getById_whenCalled_shouldReturnEmptyIfNotExists() {
        final BusinessPartner bp = businessPartnerGenerator.generate();
        businessPartnerRepository.save(bp);

        final Optional<BusinessPartner> unknownBP = adapterUnderTest.getById("unknown");

        assertThat(unknownBP).isEmpty();
    }

    @Test
    void findAllById_whenCalled_shouldReturnAllBPs() {
        final List<BusinessPartner> bps = generateBPs();
        businessPartnerRepository.saveAll(bps);
        final List<BusinessPartner> partialBps = bps.subList(0, 2);

        final Map<String, BusinessPartner> storedBPs = adapterUnderTest.findAllById(partialBps.stream().map(BusinessPartner::getId).toList());

        verifyBPs(storedBPs.values(), partialBps);
        assertThat(storedBPs).allSatisfy((key, value) -> assertThat(value.getId()).isEqualTo(key));
    }

    @Test
    void storeOne_whenCalled_shouldStoreBP() {
        final BusinessPartner bp = businessPartnerGenerator.generate();

        adapterUnderTest.storeOne(bp);

        verifyBPs(businessPartnerRepository.findAll(), List.of(bp));
    }

    @Test
    void storeAll_whenCalled_shouldStoreAllBPs() {
        final List<BusinessPartner> bps = generateBPs();

        adapterUnderTest.storeAll(bps);

        verifyBPs(businessPartnerRepository.findAll(), bps);
    }

    private List<BusinessPartner> generateBPs() {
        final BusinessPartner bp1 = businessPartnerGenerator.generate(bp -> bp.id("10000012345"));
        final BusinessPartner bp2 = businessPartnerGenerator.generate(bp -> bp.id("10000067890"));
        final BusinessPartner bp3 = businessPartnerGenerator.generate(bp -> bp.id("100000556656"));

        return List.of(bp1, bp2, bp3);
    }

    private void verifyBPs(final Collection<BusinessPartner> bps, final Collection<BusinessPartner> expectedBps) {
        assertThat(bps)
                .hasSize(expectedBps.size())
                .extracting("id")
                .containsExactlyInAnyOrder(expectedBps.stream().map(BusinessPartner::getId).toArray());
    }
}
