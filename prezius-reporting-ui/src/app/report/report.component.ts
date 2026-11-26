import { Component, inject, OnInit } from '@angular/core';
import { ReportService } from '../service/report.service';
import { QdFilterConfigData, QdTreeConfig, QdTreeData, QdPageConfig } from '@quadrel-enterprise-ui/framework';
import { combineLatest, map, Observable, of, zip } from 'rxjs';
import { ReportLine } from '../models/ReportLine';
import { BusinessPartnerService } from '../businesspartner.service';
import { ControlOfficeService } from '../control-office.service';
import { HallmarkingReport } from '../models/HallmarkingReport';
import { TranslateService } from '@ngx-translate/core';
import { AnalysisReport } from '../models/analysis-report.model';
import { formatCurrency } from '../shared/format-currency';
import { fixQdFilterDates } from '../shared/filter.service';

type TreeColumnDefinition = 'title' | 'ag' | 'au' | 'pt' | 'pd' | 'total' | 'revenue';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  standalone: false
})
export class ReportComponent implements OnInit {
  private translateBaseKey: string = 'i18n.prezius.reporting.recapTable.title.';
  treeData$: Observable<QdTreeData<any>>;
  treeConfig$: Observable<QdTreeConfig<TreeColumnDefinition>>;

  pageConfig: QdPageConfig = {
    title: { i18n: 'i18n.prezius.reporting.title' },
    pageType: 'overview'
  };

  private treeViewStructConfig = {
    hallmarking: {
      reportKey: 'total',
      nestedKeys: ['jewel', 'watch']
    },
    jewel: {
      reportKey: 'jewelTotal',
      nestedKeys: ['jewelLaser', 'jewelMechanical']
    },
    jewelLaser: {
      reportKey: 'jewelLaserTotal',
      nestedKeys: ['jewelLaserCertified', 'jewelLaserNotCertified']
    },
    jewelMechanical: {
      reportKey: 'jewelMechanicalTotal',
      nestedKeys: ['jewelMechanicalCertified', 'jewelMechanicalNotCertified']
    },
    watch: {
      reportKey: 'watchTotal',
      nestedKeys: ['watchLaser', 'watchMechanical']
    },
    watchLaser: {
      reportKey: 'watchLaserTotal',
      nestedKeys: ['watchLaserCertified', 'watchLaserNotCertified']
    },
    watchMechanical: {
      reportKey: 'watchMechanicalTotal',
      nestedKeys: ['watchMechanicalCertified', 'watchMechanicalNotCertified']
    },
    jewelLaserCertified: {
      reportKey: 'jewelLaserCertified',
      nestedKeys: []
    },
    jewelLaserNotCertified: {
      reportKey: 'jewelLaserNotCertified',
      nestedKeys: []
    },
    jewelMechanicalCertified: {
      reportKey: 'jewelMechanicalCertified',
      nestedKeys: []
    },
    jewelMechanicalNotCertified: {
      reportKey: 'jewelMechanicalNotCertified',
      nestedKeys: []
    },
    watchLaserCertified: {
      reportKey: 'watchLaserCertified',
      nestedKeys: []
    },
    watchLaserNotCertified: {
      reportKey: 'watchLaserNotCertified',
      nestedKeys: []
    },
    watchMechanicalCertified: {
      reportKey: 'watchMechanicalCertified',
      nestedKeys: []
    },
    watchMechanicalNotCertified: {
      reportKey: 'watchMechanicalNotCertified',
      nestedKeys: []
    },
    analysis: {
      reportKey: 'total',
      nestedKeys: [
        'conformityEvaluation',
        'determinationOnSamples',
        'determinationOnMeltingPoint',
        'samplesFromHallmarking',
        'samplesFromOtherProcesses'
      ]
    },
    conformityEvaluation: {
      reportKey: 'CONFORMITY_EVALUATION',
      nestedKeys: []
    },
    determinationOnSamples: {
      reportKey: 'DETERMINATION_ON_SAMPLES',
      nestedKeys: []
    },
    determinationOnMeltingPoint: {
      reportKey: 'DETERMINATION_ON_MELTING_POINT',
      nestedKeys: []
    },
    samplesFromHallmarking: {
      reportKey: 'SAMPLES_FROM_HALLMARKING',
      nestedKeys: []
    },
    samplesFromOtherProcesses: {
      reportKey: 'SAMPLES_FROM_OTHER_PROCESSES',
      nestedKeys: []
    }
  };

  private readonly reportService: ReportService = inject(ReportService);
  private readonly controlOfficeService: ControlOfficeService = inject(ControlOfficeService);
  private readonly businessPartnerService: BusinessPartnerService = inject(BusinessPartnerService);
  private readonly translationService: TranslateService = inject(TranslateService);

  public genTreeData(): Observable<QdTreeData<any>> {
    const keysToTranslate: string[] = Object.keys(this.treeViewStructConfig).map(key => this.translateBaseKey + key);

    return combineLatest([
      this.translationService.stream(keysToTranslate),
      this.reportService.hallmarkingReport$,
      this.reportService.analysisReport$
    ]).pipe(
      map(([translatedKeys, hallmarkingReport, analysisReport]) => {
        const hallmarkingReportData: { nestedRows: any; title: any } = this.getHallmarkingNestedKeys(
          hallmarkingReport,
          translatedKeys
        );
        const analysisReportData: { nestedRows: any; title: any } = this.getAnalysisNestedKeys(
          analysisReport,
          translatedKeys
        );
        return [hallmarkingReportData, analysisReportData] as QdTreeData<any>;
      })
    );
  }

  private getHallmarkingNestedKeys(
    report: HallmarkingReport,
    translations: object,
    key = 'hallmarking'
  ): { nestedRows: any; title: any } {
    const nestedRows: any = this.treeViewStructConfig[key].nestedKeys.map(
      (nestedKey: any): { nestedRows: any; title: any } =>
        this.getHallmarkingNestedKeys(report, translations, nestedKey)
    );
    const translationFullKey: string = this.translateBaseKey + key;
    const treeData = {
      title: translations[translationFullKey],
      ...this.reportLineToTableLine(report[this.treeViewStructConfig[key].reportKey], key === 'hallmarking'),
      nestedRows
    };
    if (nestedRows.length == 0) {
      delete treeData.nestedRows;
    }

    return treeData;
  }

  private getAnalysisNestedKeys(
    report: AnalysisReport,
    translations: object,
    key = 'analysis'
  ): { nestedRows: any; title: any } {
    const nestedRows: any = this.treeViewStructConfig[key].nestedKeys.map(
      (nestedKey: any): { nestedRows: any; title: any } => this.getAnalysisNestedKeys(report, translations, nestedKey)
    );
    const translationFullKey: string = this.translateBaseKey + key;
    const treeData = {
      title: translations[translationFullKey],
      ...this.reportLineToTableLine(report[this.treeViewStructConfig[key].reportKey], key === 'analysis'),
      nestedRows
    };
    if (nestedRows.length == 0) {
      delete treeData.nestedRows;
    }

    return treeData;
  }

  private currentFilter = '';

  private reportLineToTableLine(reportLine: ReportLine, includeRevenue: boolean = false): object {
    if (reportLine == null) {
      return {
        au: '0',
        ag: '0',
        pt: '0',
        pd: '0',
        total: '0',
        revenue: ''
      };
    }

    const { au, ag, pt, pd, total, totalCostsWithVat } = reportLine;
    return {
      au: au.toString(),
      ag: ag.toString(),
      pt: pt.toString(),
      pd: pd.toString(),
      total: total.toString(),
      revenue: includeRevenue ? formatCurrency(totalCostsWithVat) : ''
    };
  }

  public defineTreeConfig(): Observable<QdTreeConfig<TreeColumnDefinition>> {
    return of({
      columns: [
        { column: 'title', type: 'tree' },
        { column: 'au', type: 'text' },
        { column: 'ag', type: 'text' },
        { column: 'pt', type: 'text' },
        { column: 'pd', type: 'text' },
        { column: 'total', type: 'text' },
        { column: 'revenue', type: 'text' }
      ],
      i18ns: 'i18n.prezius.reporting.recapTable',
      mainColumn: 'title'
    } as QdTreeConfig<TreeColumnDefinition>);
  }

  public filterData: Observable<QdFilterConfigData>;

  public ngOnInit() {
    this.filterData = zip(
      this.controlOfficeService.controlOffices$,
      this.businessPartnerService.businessPartners$
    ).pipe(
      map(([controlOffices, businessPartners]) => {
        this.treeConfig$ = this.defineTreeConfig();
        this.treeData$ = this.genTreeData();
        return {
          outputLegacyQueryStringFormat: false,
          categories: [
            {
              type: 'multiSelect',
              category: 'officeName',
              i18n: 'i18n.prezius.reporting.filters.controlOffice',
              items: (controlOffices || []).map(office => ({
                item: office.name,
                i18n: `i18n.prezius.reporting.filters.controlOffice.${office.name}`
              })),
              filter: true
            },
            {
              type: 'dateRange',
              category: 'date',
              i18n: 'i18n.prezius.reporting.filters.date',
              items: [
                {
                  item: 'placeholder'
                }
              ]
            },
            {
              type: 'multiSelect',
              category: 'businessPartner',
              i18n: 'i18n.prezius.reporting.filters.client',
              items: (businessPartners || []).map(businessPartner => ({
                item: businessPartner.id,
                i18n: businessPartner.name
              })),
              filter: true
            },
            {
              type: 'multiSelect',
              category: 'hallmarkingType',
              i18n: 'i18n.prezius.reporting.filters.mark',
              items: [
                {
                  item: 'SINGLE_SWISS',
                  i18n: 'i18n.prezius.reporting.filters.swiss'
                },
                {
                  item: 'SINGLE_INTERNATIONAL',
                  i18n: 'i18n.prezius.reporting.filters.international'
                }
              ],
              filter: true
            }
          ]
        };
      })
    );
  }

  public exportCsv() {
    this.reportService.downloadCsv(this.currentFilter);
  }

  public filter(filterData: string): void {
    this.currentFilter = fixQdFilterDates(filterData);
    this.reportService.fetchHallmarkingReport(this.currentFilter).subscribe();
    this.reportService.fetchAnalysisReport(this.extractAnalysisReportFilter(this.currentFilter)).subscribe();
  }

  private extractAnalysisReportFilter(filterData: string): string {
    if (filterData.indexOf('=') == -1) {
      return filterData;
    }
    const filterDataParameterValue: string = filterData.substring(filterData.indexOf('=') + 1);
    const urlDecoded: string = decodeURIComponent(filterDataParameterValue);
    const split: string[] = urlDecoded.split('|');
    const filtered: string[] = split.filter((item: string): boolean => !item.startsWith('hallmarkingType'));
    const joined: string = filtered.join('|');
    const urlEncoded: string = encodeURIComponent(joined);
    return 'filter=' + urlEncoded;
  }
}
