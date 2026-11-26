import '@angular/compiler';
import { AppState } from '../reducers/app.reducer';
import { FF_PREZIUS_3_0, featureFlagsSelector } from './feature-flags.selector';

describe('featureFlagsSelector', (): void => {
  it('given feature flags state when selecting then returns all feature flags', (): void => {
    const featureFlagsState: AppState['featureFlags'] = {
      PREZIUS_3_0: true
    };

    expect(featureFlagsSelector.projector(featureFlagsState)).toStrictEqual({
      PREZIUS_3_0: true
    });
  });

  it('given feature flags state when selecting DFEA 3875 then returns flag value', (): void => {
    const featureFlagsState: AppState['featureFlags'] = {
      PREZIUS_3_0: false
    };

    expect(FF_PREZIUS_3_0.projector(featureFlagsState)).toBe(false);
  });
});
