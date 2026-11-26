import { QdDialogAuthSessionEndService } from '@quadrel-enterprise-ui/framework';
import { Router } from '@angular/router';
import { QdAuthenticationService, QdConfigService } from '@quadrel-enterprise-ui/auth';
import { Observable, of } from 'rxjs';
import { ServiceURLService } from '../modules/openapi';
import { TranslateLoader } from '@ngx-translate/core';

export class FakeTranslateLoader implements TranslateLoader {
  getTranslation(): Observable<Record<string, string>> {
    return of({});
  }
}
export const mockSessionEndService: Partial<QdDialogAuthSessionEndService> = {
  getLogoutHandler: jest.fn()
};

export const mockRouter: Partial<Router> = {
  navigate: jest.fn()
};

export const mockQdConfigService: Partial<QdConfigService> = {
  get config$(): Observable<any> {
    return of({
      pamsEnvironment: 'DEV'
    });
  }
};

export const mockQdAuthenticationService: Partial<QdAuthenticationService> = {
  registerBeforeSessionLogoutHandler(_: any): void {},
  getClaims(): Observable<any> {
    return of({ userroles: [], bproles: {} });
  }
};

export const mockServiceUrlsService: Partial<ServiceURLService> = {
  getServiceUrls: jest.fn().mockReturnValue(
    of({
      preziusRegisterServiceBaseUrl: 'http://localhost:4290/prezius-register/',
      preziusApplicationServiceBaseUrl: 'http://localhost:4297/prezius-application/',
      preziusMatchmakingServiceBaseUrl: 'http://localhost:4221/prezius-matchmaking/',
      preziusBillingServiceBaseUrl: 'http://localhost:4203/prezius-billing/',
      preziusOrderServiceBaseUrl: 'http://localhost:4200/prezius-order/',
      preziusAnalysisServiceBaseUrl: 'http://localhost:4269/prezius-analysis/',
      preziusAuditServiceBaseUrl: 'http://localhost:4265/prezius-audit/'
    })
  )
};
