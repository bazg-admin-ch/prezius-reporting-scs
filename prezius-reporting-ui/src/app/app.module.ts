import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { TranslateModule } from '@ngx-translate/core';
import { QdAuthModule } from '@quadrel-enterprise-ui/auth';
import { APP_ENVIRONMENT, QdUiModule } from '@quadrel-enterprise-ui/framework';
import {
  LANGUAGE_ENVIRONMENT,
  qdLanguageLoader,
  WEBLATE_RESOURCES,
  WeblateTranslationResource
} from '@quadrel-enterprise-ui/language';
import {
  appEnvironment,
  appSetup,
  authConfig,
  backendServiceEndpointWithoutTrailingSlash
} from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ReportComponent } from './report/report.component';
import { FeatureFlagEffect } from './shared/store/effects/feature-flag.effect';
import { featureFlagsReducer } from './shared/store/reducers/feature-flags.reducer';
import { LayoutComponent } from './layout/layout.component';
import { BASE_PATH } from './modules/openapi';

const appTranslationResource: WeblateTranslationResource = {
  project: 'quadrel',
  component: 'sandbox'
};

@NgModule({
  declarations: [AppComponent, LayoutComponent, ReportComponent],
  bootstrap: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NoopAnimationsModule,
    ReactiveFormsModule,
    StoreModule.forRoot({
      featureFlags: featureFlagsReducer
    }),
    EffectsModule.forRoot([FeatureFlagEffect]),
    StoreDevtoolsModule.instrument({
      name: 'QdAuthSandbox',
      logOnly: appSetup.production,
      connectInZone: true
    }),
    QdUiModule.forRoot(appEnvironment),
    QdAuthModule.forRoot(appSetup, authConfig),
    TranslateModule.forRoot(qdLanguageLoader())
  ],
  providers: [
    {
      provide: WEBLATE_RESOURCES,
      useValue: [appTranslationResource]
    },
    {
      provide: APP_ENVIRONMENT,
      useValue: appEnvironment
    },
    {
      provide: LANGUAGE_ENVIRONMENT,
      useValue: appEnvironment
    },
    {
      provide: BASE_PATH,
      useValue: backendServiceEndpointWithoutTrailingSlash
    },
    provideHttpClient(withInterceptorsFromDi())
  ]
})
export class AppModule {}
