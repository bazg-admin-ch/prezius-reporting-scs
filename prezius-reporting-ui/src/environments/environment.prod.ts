import { QdAppSetup, QdLogLevel, QdAuthConfigServerSide } from '@quadrel-enterprise-ui/auth';
import { QdAppEnvironment } from '@quadrel-enterprise-ui/framework';

export const backendServiceEndpointWithoutTrailingSlash: string = '/prezius-reporting';

export const appSetup: QdAppSetup = {
  production: true,
  serviceEndpoint: backendServiceEndpointWithoutTrailingSlash + '/'
};

export const authConfig: QdAuthConfigServerSide = {
  systemName: 'prezius',
  configPathSegment: 'api/ui/configuration',
  clientId: 'prezius-reporting-ui',
  logLevel: QdLogLevel.None,
  renewUserInfoAfterTokenRenew: true,
  silentRenew: true,
  silentRenewUrl: `${window.location.origin}/prezius-reporting/assets/auth/silent-renew.html`,
  useAutoLogin: true
};

export const appEnvironment: QdAppEnvironment = {
  production: appSetup.production,
  BACKEND_SERVICE_API: appSetup.serviceEndpoint,
  CONFIGURATION_PATH: authConfig.configPathSegment
};

export const serviceBaseURLs = {
  order: `${window.location.origin}/prezius-order/`,
  analysis: `${window.location.origin}/prezius-analysis/`,
  billing: `${window.location.origin}/prezius-billing/`
};
