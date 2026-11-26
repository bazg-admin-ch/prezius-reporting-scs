import '@angular/compiler';
import { FeatureFlags } from '../../../models/enums/FeatureFlags';
import { featureFlagsReceived, loadFeatureFlagsFailed } from '../actions/feature-flag.action';
import { featureFlagsReducer, initialFeatureFlagsState } from './feature-flags.reducer';

describe('featureFlagsReducer', (): void => {
  it('given unknown action when reducing then returns initial state', (): void => {
    const action = { PREZIUS_3_0: false } as any;

    const state: FeatureFlags = featureFlagsReducer(initialFeatureFlagsState, action);

    expect(state).toBe(initialFeatureFlagsState);
  });

  it('given received feature flags when reducing then merges feature flags into state', (): void => {
    const action = featureFlagsReceived({
      featureFlags: {
        PREZIUS_3_0: true
      }
    });

    const state: FeatureFlags = featureFlagsReducer(initialFeatureFlagsState, action);

    expect(state).toStrictEqual({
      PREZIUS_3_0: true
    });
  });

  it('given feature flag load failure when reducing then keeps current state', (): void => {
    const currentState: FeatureFlags = {
      PREZIUS_3_0: false
    };

    const state: FeatureFlags = featureFlagsReducer(currentState, loadFeatureFlagsFailed({ error: new Error('boom') }));

    expect(state).toStrictEqual(currentState);
  });
});
