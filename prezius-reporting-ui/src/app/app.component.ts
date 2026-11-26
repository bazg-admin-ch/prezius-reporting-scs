import { Component, OnInit, ViewEncapsulation, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { QdAuthConfigFull, QdAuthenticationService, QdClaims, QdConfigService } from '@quadrel-enterprise-ui/auth';
import {
  QdDialogAuthSessionEndService,
  QdShellConfig,
  QdShellNavigationConfig,
  QdShellServiceNavigationEnvironment
} from '@quadrel-enterprise-ui/framework';
import { combineLatestWith, map, Observable, startWith } from 'rxjs';
import { serviceBaseURLs } from '../environments/environment';
import { FeatureFlags } from './models/enums/FeatureFlags';
import { ServiceURLService, ServiceUrlsResponseDto } from './modules/openapi';
import { AppState } from './shared/store/reducers/app.reducer';
import { featureFlagsSelector } from './shared/store/selectors/feature-flags.selector';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  encapsulation: ViewEncapsulation.None,
  standalone: false
})
export class AppComponent implements OnInit {
  private readonly sessionEndService = inject(QdDialogAuthSessionEndService);
  private readonly authenticationService = inject(QdAuthenticationService);
  private readonly configService = inject(QdConfigService);
  private readonly router = inject(Router);
  private readonly serviceUrlsService = inject(ServiceURLService);
  private readonly store = inject<Store<AppState>>(Store);

  shellConfig$!: Observable<QdShellConfig>;

  ngOnInit(): void {
    this.authenticationService.registerBeforeSessionLogoutHandler(this.sessionEndService.getLogoutHandler());

    this.shellConfig$ = this.authenticationService.getClaims().pipe(
      startWith({} as QdClaims),
      combineLatestWith(this.configService.config$, this.store.select(featureFlagsSelector)),
      map(([claims, config, featureFlags]: [QdClaims | null, QdAuthConfigFull, FeatureFlags]): QdShellConfig =>
        this.getShellConfig(config.pamsEnvironment, featureFlags, claims)
      )
    );
  }

  private getShellConfig(
    pamsEnvironment: QdShellServiceNavigationEnvironment | '',
    featureFlags: { PREZIUS_3_0?: boolean } | null,
    claims: QdClaims | null
  ): QdShellConfig {
    const isPrezius3_0Enabled: boolean = featureFlags?.PREZIUS_3_0 ?? true;
    return {
      title: { i18n: 'i18n.application.title' },
      headerWidget: { isDisabled: true },
      serviceNavigation: {
        languageList: ['de', 'fr', 'it', 'en'],
        showLanguages: true,
        showNotifications: true,
        showEportalServices: true,
        showProfile: true,
        pamsEnvironment,
        pamsAppId: 'APP05778'
      },
      navigation: isPrezius3_0Enabled
        ? this.createNavigation(claims?.bproles ?? {}, claims?.userroles ?? [])
        : [
            {
              i18n: 'i18n.navigation.orders',
              handler: (): string => (window.location.href = serviceBaseURLs.order),
              icon: 'cube'
            },
            {
              i18n: 'i18n.navigation.protocols',
              handler: (): string => (window.location.href = serviceBaseURLs.analysis),
              icon: 'task'
            },
            {
              i18n: 'i18n.navigation.invoices',
              handler: (): string => (window.location.href = serviceBaseURLs.billing),
              icon: 'fileText'
            },
            {
              i18n: 'i18n.navigation.reporting',
              handler: () => {
                this.router.navigate(['']);
              },
              icon: 'analytics',
              isCurrent: true
            }
          ],
      copyrightInfo: { i18n: 'i18n.application.organization.name', showYear: true },
      isInternal: true,
      isFooterSticky: true
    };
  }

  private createNavigation(bpRoles: { [bpId: string]: string[] }, userroles: string[]): QdShellNavigationConfig[] {
    const isGP: boolean = Object.keys(bpRoles).length > 0;
    const isEMK: boolean = userroles.length > 0;
    const allRoles: string[] = [...userroles, ...Object.values(bpRoles).flat()];
    const hasRole = (role: string): boolean => allRoles.some(r => r.includes(role));

    const entries: Array<[boolean, QdShellNavigationConfig]> = [
      [
        true,
        {
          i18n: 'i18n.navigation.preziusInfo',
          icon: 'circleInfo',
          handler: this.serviceUrlHandler('preziusRegisterServiceBaseUrl')
        }
      ],
      [
        hasRole('marklicence-application') || hasRole('agreement-application') || hasRole('application'),
        {
          i18n: 'i18n.navigation.application',
          icon: 'licenseDraft',
          handler: this.serviceUrlHandler('preziusApplicationServiceBaseUrl')
        }
      ],
      [
        isEMK && hasRole('bpmatching'),
        {
          i18n: 'i18n.navigation.matchmaking',
          icon: 'circleArrow',
          handler: this.serviceUrlHandler('preziusMatchmakingServiceBaseUrl')
        }
      ],
      [
        (isGP || isEMK) && hasRole('billing'),
        {
          i18n: 'i18n.navigation.invoices',
          icon: 'fileText',
          handler: this.serviceUrlHandler('preziusBillingServiceBaseUrl')
        }
      ],
      [
        isEMK && hasRole('reporting'),
        {
          i18n: 'i18n.navigation.reporting',
          icon: 'analytics',
          handler: () => {
            this.router.navigate(['']);
          },
          isCurrent: true
        }
      ],
      [
        (isGP || isEMK) && hasRole('order'),
        { i18n: 'i18n.navigation.orders', icon: 'cube', handler: this.serviceUrlHandler('preziusOrderServiceBaseUrl') }
      ],
      [
        isEMK && hasRole('analysis'),
        {
          i18n: 'i18n.navigation.protocols',
          icon: 'task',
          handler: this.serviceUrlHandler('preziusAnalysisServiceBaseUrl')
        }
      ],
      [
        hasRole('auditcase'),
        {
          i18n: 'i18n.navigation.audit',
          icon: 'eye',
          handler: this.serviceUrlHandler('preziusAuditServiceBaseUrl')
        }
      ]
    ];

    return entries
      .filter(([visible]: [boolean, QdShellNavigationConfig]): boolean => visible)
      .map(([, item]: [boolean, QdShellNavigationConfig]): QdShellNavigationConfig => item);
  }

  private serviceUrlHandler(urlKey: keyof ServiceUrlsResponseDto): () => void {
    return (): void => {
      this.serviceUrlsService.getServiceUrls().subscribe((urls: ServiceUrlsResponseDto): void => {
        window.location.href = urls[urlKey];
      });
    };
  }
}
