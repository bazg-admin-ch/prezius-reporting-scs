package ch.admin.bazg.prezius.reporting.common.mocks;

import ch.admin.bazg.prezius.reporting.config.FeatureFlags;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.spi.FeatureManagerProvider;
import org.togglz.testing.TestFeatureManager;

public class TestFeatureManagerProvider implements FeatureManagerProvider {

    @Override
    public int priority() {
        return 20;
    }

    @Override
    public FeatureManager getFeatureManager() {
        final TestFeatureManager testFeatureManager = new TestFeatureManager(FeatureFlags.class);
        testFeatureManager.enableAll();
        return testFeatureManager;
    }

}