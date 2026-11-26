import { Routes } from '@angular/router';
import { QdAuthorizationGuard } from '@quadrel-enterprise-ui/auth';
import { ReportComponent } from '../../report/report.component';
import { inject } from '@angular/core';
import { ControlOfficeService } from '../../control-office.service';
import { BusinessPartnerService } from '../../businesspartner.service';
import { ReportService } from '../../service/report.service';
import { FeatureFlagsService } from '../../service/feature-flags.service';
import { LayoutComponent } from '../../layout/layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    canActivate: [QdAuthorizationGuard],
    children: [
      {
        path: '',
        component: ReportComponent,
        resolve: {
          controlOffices: () => inject(ControlOfficeService).fetchControlOffices(),
          businessPartners: () => inject(BusinessPartnerService).fetchBusinessPartners(),
          hallmarkingReport: () => inject(ReportService).fetchHallmarkingReport(),
          analysisReport: () => inject(ReportService).fetchAnalysisReport(),
          featureFlags: () => inject(FeatureFlagsService).fetchFeatureFlags()
        },
        canLoad: [QdAuthorizationGuard],
        data: {
          name: 'Report',
          description: '',
          isInternal: false,
          pageLevel: 'home',
          isHome: true
        }
      },
      {
        path: '**',
        redirectTo: ''
      }
    ]
  }
];
