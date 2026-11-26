import { FeatureFlags } from '../../../models/enums/FeatureFlags';

export interface AppState {
  readonly featureFlags: FeatureFlags;
}
