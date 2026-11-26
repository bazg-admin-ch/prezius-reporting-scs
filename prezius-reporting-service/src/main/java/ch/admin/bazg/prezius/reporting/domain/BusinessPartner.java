package ch.admin.bazg.prezius.reporting.domain;

import ch.admin.bazg.prezius.reporting.domain.valueobject.ControlOfficeGroup;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
@Getter
@Builder(toBuilder = true)
public class BusinessPartner {
    @Id
    private String id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ControlOfficeGroup controlOfficeGroup;
}
