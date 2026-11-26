import { createReducer, on } from '@ngrx/store';
import { FeatureFlags } from '../../../models/enums/FeatureFlags';
import { featureFlagsReceived, loadFeatureFlagsFailed } from '../actions/feature-flag.action';

export const initialFeatureFlagsState: FeatureFlags = {
  PREZIUS_3_0: false
} as FeatureFlags;

export const featureFlagsReducer = createReducer(
  initialFeatureFlagsState,
  on(featureFlagsReceived, (state: FeatureFlags, { featureFlags }: { featureFlags: FeatureFlags }): FeatureFlags => ({
    ...state,
    ...featureFlags
  })),
  on(loadFeatureFlagsFailed, (state: FeatureFlags): FeatureFlags => ({ ...state }))
);
