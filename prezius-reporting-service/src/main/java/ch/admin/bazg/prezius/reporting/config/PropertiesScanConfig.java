package ch.admin.bazg.prezius.reporting.config;

import ch.admin.bazg.prezius.reporting.ReportingServiceApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = ReportingServiceApplication.class)
public class PropertiesScanConfig {
}
