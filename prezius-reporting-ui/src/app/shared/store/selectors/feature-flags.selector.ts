import { createSelector, DefaultProjectorFn, MemoizedSelector } from '@ngrx/store';
import { FeatureFlags } from '../../../models/enums/FeatureFlags';
import { AppState } from '../reducers/app.reducer';

export const featureFlags = (state: AppState): FeatureFlags => state.featureFlags;

export const featureFlagsSelector: MemoizedSelector<
  AppState,
  FeatureFlags,
  DefaultProjectorFn<FeatureFlags>
> = createSelector(featureFlags, (featureFlagsState: FeatureFlags): FeatureFlags => featureFlagsState);

export const FF_PREZIUS_3_0 = createSelector(
  featureFlags,
  (featureFlagsState: FeatureFlags): boolean => featureFlagsState?.PREZIUS_3_0
);
