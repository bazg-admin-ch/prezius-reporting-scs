import '@angular/compiler';
import { HttpClientModule } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MemoizedSelector, Store } from '@ngrx/store';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { QdAuthenticationService, QdClaims, QdConfigService } from '@quadrel-enterprise-ui/auth';
import {
  QdDialogAuthSessionEndService,
  QdShellConfig,
  QdShellNavigationConfig
} from '@quadrel-enterprise-ui/framework';
import { of } from 'rxjs';
import { FeatureFlags } from './models/enums/FeatureFlags';
import { ServiceURLService } from './modules/openapi';
import {
  mockQdAuthenticationService,
  mockQdConfigService,
  mockRouter,
  mockServiceUrlsService,
  mockSessionEndService
} from './shared/service-mock';
import { AppState } from './shared/store/reducers/app.reducer';
import { featureFlagsSelector } from './shared/store/selectors/feature-flags.selector';
import { AppComponent } from './app.component';
import { FakeTranslateLoader } from './shared/service-mock';

const buildProviders = () => [
  { provide: QdDialogAuthSessionEndService, useValue: mockSessionEndService },
  { provide: QdAuthenticationService, useValue: mockQdAuthenticationService },
  { provide: QdConfigService, useValue: mockQdConfigService },
  { provide: Router, useValue: mockRouter },
  { provide: ServiceURLService, useValue: mockServiceUrlsService },
  provideMockStore<AppState>({
    initialState: {
      featureFlags: {} as FeatureFlags
    }
  })
];

describe('AppComponent', (): void => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let authenticationService: QdAuthenticationService;
  let store: MockStore<AppState>;
  let featureFlagsStoreSelector: MemoizedSelector<AppState, FeatureFlags>;

  beforeEach(async (): Promise<void> => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      imports: [
        HttpClientModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: FakeTranslateLoader }
        })
      ],
      providers: buildProviders(),
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    authenticationService = TestBed.inject(QdAuthenticationService);
    store = TestBed.inject(Store) as MockStore<AppState>;
    featureFlagsStoreSelector = store.overrideSelector(featureFlagsSelector, {
      PREZIUS_3_0: true
    });
  });

  beforeEach((): void => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('given app initialization when ngOnInit then registers logout handler', (): void => {
    const registerLogoutHandler = jest.spyOn(authenticationService, 'registerBeforeSessionLogoutHandler');

    component.ngOnInit();

    expect(registerLogoutHandler).toHaveBeenCalledTimes(1);
  });

  it('given enabled feature flag when building shell config then shows always preziusInfo', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: [], bproles: {} } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      expect(config.navigation).toHaveLength(1);
      expect(config.navigation[0].i18n).toBe('i18n.navigation.preziusInfo');
      expect(config.navigation[0].icon).toBe('circleInfo');
      done();
    });
  });

  it('given enabled feature flag when EMK has reporting role then shows Reporting', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: ['prezius_@reporting_#read'], bproles: {} } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const reportingItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.reporting'
      );
      expect(reportingItem).toBeTruthy();
      expect(reportingItem?.icon).toBe('analytics');
      done();
    });
  });

  it('given enabled feature flag when GP has order role in bproles then shows Order', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: [], bproles: { bp1: ['prezius_@order_#read'] } } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const orderItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.orders'
      );
      expect(orderItem).toBeTruthy();
      expect(orderItem?.icon).toBe('cube');
      done();
    });
  });

  it('given enabled feature flag when EMK has order role then shows Order', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: ['prezius_@order_#read'], bproles: {} } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const orderItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.orders'
      );
      expect(orderItem).toBeTruthy();
      expect(orderItem?.icon).toBe('cube');
      done();
    });
  });

  it('given enabled feature flag when EMK has mark licence application role then shows Application', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(
        of({ userroles: ['prezius_@marklicence-application_#read'], bproles: {} } as unknown as QdClaims)
      );

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const appItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.application'
      );
      expect(appItem).toBeTruthy();
      expect(appItem?.icon).toBe('licenseDraft');
      done();
    });
  });

  it('given enabled feature flag when EMK has agreement application read role then shows Application', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: ['prezius_@agreement-application_#read'], bproles: {} } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      expect(
        config.navigation.some(
          (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.application'
        )
      ).toBeTruthy();
      done();
    });
  });

  it('given enabled feature flag when GP has application role then shows Application', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: [], bproles: { bp1: ['prezius_@application_#read'] } } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const appItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.application'
      );
      expect(appItem).toBeTruthy();
      expect(appItem?.icon).toBe('licenseDraft');
      done();
    });
  });

  it('given enabled feature flag when GP has billing role then shows Billing', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: [], bproles: { bp1: ['prezius_@billing_#read'] } } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const appItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.invoices'
      );
      expect(appItem).toBeTruthy();
      expect(appItem?.icon).toBe('fileText');
      done();
    });
  });

  it('given enabled feature flag when EMK has billing role then shows Billing', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: ['prezius_@billing_#read'], bproles: {} } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const appItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.invoices'
      );
      expect(appItem).toBeTruthy();
      expect(appItem?.icon).toBe('fileText');
      done();
    });
  });

  it('given enabled feature flag when EMK has bpmatching role then shows Matchmaking', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: ['prezius_@bpmatching_#read'], bproles: {} } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const matchItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.matchmaking'
      );
      expect(matchItem).toBeTruthy();
      expect(matchItem?.icon).toBe('circleArrow');
      done();
    });
  });

  it('given enabled feature flag when GP only has bpmatching role then does not show Matchmaking', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: [], bproles: { bp1: ['prezius_@bpmatching_#read'] } } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      expect(
        config.navigation.some(
          (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.matchmaking'
        )
      ).toBeFalsy();
      done();
    });
  });

  it('given enabled feature flag when EMK has analysis role then shows Analysis', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: ['prezius_@analysis_#read'], bproles: {} } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const analysisItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.protocols'
      );
      expect(analysisItem).toBeTruthy();
      expect(analysisItem?.icon).toBe('task');
      done();
    });
  });

  it('given enabled feature flag when GP only has reporting and analysis roles then does not show Reporting and Analysis', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(
        of({
          userroles: [],
          bproles: { bp1: ['prezius_@reporting_#read', 'prezius_@analysis_#read'] }
        } as unknown as QdClaims)
      );

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      expect(
        config.navigation.some(
          (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.reporting'
        )
      ).toBeFalsy();
      expect(
        config.navigation.some(
          (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.protocols'
        )
      ).toBeFalsy();
      done();
    });
  });

  it('given enabled feature flag when user has auditcase role then shows Audit', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: ['prezius_@auditcase_#read'], bproles: {} } as unknown as QdClaims));

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      const auditItem = config.navigation.find(
        (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.audit'
      );
      expect(auditItem).toBeTruthy();
      expect(auditItem?.icon).toBe('eye');
      done();
    });
  });

  it('given enabled feature flag when EMK has all roles then shows all 8 items', (done: jest.DoneCallback): void => {
    jest.spyOn(authenticationService, 'getClaims').mockReturnValue(
      of({
        userroles: [
          'prezius_@marklicence-application_#read',
          'prezius_@bpmatching_#read',
          'prezius_@billing_#read',
          'prezius_@reporting_#read',
          'prezius_@order_#read',
          'prezius_@analysis_#read',
          'prezius_@auditcase_#read'
        ],
        bproles: {}
      } as unknown as QdClaims)
    );

    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      expect(config.navigation).toHaveLength(8);

      let capturedHref = '';
      Object.defineProperty(window, 'location', {
        configurable: true,
        value: {
          set href(url: string) {
            capturedHref = url;
          }
        }
      });

      const expectedNavigation: Array<{ i18n: string; icon: string; url: string | null; isCurrent?: boolean }> = [
        { i18n: 'i18n.navigation.preziusInfo', icon: 'circleInfo', url: 'http://localhost:4290/prezius-register/' },
        {
          i18n: 'i18n.navigation.application',
          icon: 'licenseDraft',
          url: 'http://localhost:4297/prezius-application/'
        },
        { i18n: 'i18n.navigation.matchmaking', icon: 'circleArrow', url: 'http://localhost:4221/prezius-matchmaking/' },
        { i18n: 'i18n.navigation.invoices', icon: 'fileText', url: 'http://localhost:4203/prezius-billing/' },
        { i18n: 'i18n.navigation.reporting', icon: 'analytics', url: null, isCurrent: true },
        { i18n: 'i18n.navigation.orders', icon: 'cube', url: 'http://localhost:4200/prezius-order/' },
        { i18n: 'i18n.navigation.protocols', icon: 'task', url: 'http://localhost:4269/prezius-analysis/' },
        { i18n: 'i18n.navigation.audit', icon: 'eye', url: 'http://localhost:4265/prezius-audit/' }
      ];

      expectedNavigation.forEach(
        (expected: { i18n: string; icon: string; url: string | null; isCurrent?: boolean }, index: number): void => {
          const actual: QdShellNavigationConfig = config.navigation[index];
          expect(actual.i18n).toBe(expected.i18n);
          expect(actual.icon).toBe(expected.icon);
          expect(actual.isCurrent).toBe(expected.isCurrent);
          actual.handler();
          if (expected.url !== null) {
            expect(capturedHref).toBe(expected.url);
          } else {
            expect(mockRouter.navigate).toHaveBeenCalledWith(['']);
          }
        }
      );

      done();
    });
  });

  it('given missing feature flag in store when building shell config then defaults PREZIUS_3_0 to true', (done: jest.DoneCallback): void => {
    jest
      .spyOn(authenticationService, 'getClaims')
      .mockReturnValue(of({ userroles: ['prezius_@reporting_#read'], bproles: {} } as unknown as QdClaims));
    featureFlagsStoreSelector.setResult({} as FeatureFlags);
    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((config: QdShellConfig): void => {
      expect(
        config.navigation.some(
          (navigation: QdShellNavigationConfig): boolean => navigation.i18n === 'i18n.navigation.reporting'
        )
      ).toBeTruthy();
      done();
    });
  });

  it('given PREZIUS_3_0 feature flag disabled when building shell config then returns legacy navigation', (done: jest.DoneCallback): void => {
    const url = 'http://dummy.com/';
    Object.defineProperty(window, 'location', {
      value: new URL(url)
    });
    featureFlagsStoreSelector.setResult({ PREZIUS_3_0: false });
    store.refreshState();

    component.ngOnInit();
    component.shellConfig$.subscribe((shellConfig: QdShellConfig): void => {
      expect(shellConfig).toBeTruthy();
      expect(shellConfig.navigation.length).toBe(4);
      shellConfig.navigation.forEach((navigation: QdShellNavigationConfig): void => {
        navigation.handler();
        expect(navigation).toBeTruthy();
        expect(navigation.i18n).toBeTruthy();
        expect(navigation.icon).toBeTruthy();
      });
      shellConfig.navigation[3].handler();
      expect(mockRouter.navigate).toHaveBeenCalled();
      expect(shellConfig.title['i18n']).toBeTruthy();
      expect(shellConfig.isInternal).toBeTruthy();
      expect(shellConfig.isFooterSticky).toBeTruthy();
      expect(shellConfig.serviceNavigation.languageList.length).toBe(4);
      expect(shellConfig.serviceNavigation.showLanguages).toBe(true);
      expect(shellConfig.serviceNavigation.showNotifications).toBe(true);
      expect(shellConfig.serviceNavigation.showEportalServices).toBe(true);
      expect(shellConfig.serviceNavigation.showProfile).toBe(true);
      expect(shellConfig.serviceNavigation.pamsEnvironment).toBe('DEV');
      done();
    });
  });
});
