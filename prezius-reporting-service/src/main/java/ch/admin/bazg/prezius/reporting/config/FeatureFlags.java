package ch.admin.bazg.prezius.reporting.config;


import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum FeatureFlags implements Feature {

    @Label("This flag enables feature PREZIUS_3_0")
    PREZIUS_3_0;

    @Override
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }

}
