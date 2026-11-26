import { createAction, props } from '@ngrx/store';
import { FeatureFlags } from '../../../models/enums/FeatureFlags';

export const loadFeatureFlags = createAction('[FeatureFlags] Load FeatureFlags');

export const featureFlagsReceived = createAction(
  '[FeatureFlags] FeatureFlags received',
  props<{ featureFlags: FeatureFlags }>()
);

export const loadFeatureFlagsFailed = createAction(
  '[FeatureFlags] Loading of feature flags failed',
  props<{ error: unknown }>()
);
