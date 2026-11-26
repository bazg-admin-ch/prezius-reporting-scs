package ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
record ReportDto(
        ReportLineDto watchLaserCertified,
        ReportLineDto watchLaserNotCertified,
        ReportLineDto watchMechanicalCertified,
        ReportLineDto watchMechanicalNotCertified,
        ReportLineDto jewelLaserCertified,
        ReportLineDto jewelLaserNotCertified,
        ReportLineDto jewelMechanicalCertified,
        ReportLineDto jewelMechanicalNotCertified,
        @JsonIgnore ReportLineDto other
) {

    @JsonProperty
    public ReportLineDto getWatchLaserTotal() {
        return this.watchLaserCertified.add(this.watchLaserNotCertified);
    }

    @JsonProperty
    public ReportLineDto getWatchMechanicalTotal() {
        return this.watchMechanicalCertified.add(this.watchMechanicalNotCertified);
    }

    @JsonProperty
    public ReportLineDto getJewelLaserTotal() {
        return this.jewelLaserCertified.add(this.jewelLaserNotCertified);
    }

    @JsonProperty
    public ReportLineDto getJewelMechanicalTotal() {
        return this.jewelMechanicalCertified.add(this.jewelMechanicalNotCertified);
    }

    @JsonProperty
    public ReportLineDto getWatchTotal() {
        return this.getWatchLaserTotal().add(this.getWatchMechanicalTotal());
    }

    @JsonProperty
    public ReportLineDto getJewelTotal() {
        return this.getJewelLaserTotal().add(this.getJewelMechanicalTotal());
    }

    @JsonProperty
    public ReportLineDto getTotal() {
        return this.getWatchTotal().add(this.getJewelTotal()).add(other);
    }
}
