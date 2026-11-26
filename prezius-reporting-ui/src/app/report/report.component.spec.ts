import { ReportComponent } from './report.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { fakeFeatureFlagsService, fakeReportService } from '../shared/service-mocks';
import { ReportService } from '../service/report.service';
import { readJsonFileFromFront } from '../test/test-utils';
import { FeatureFlagsService } from '../service/feature-flags.service';
import { BusinessPartnerService } from '../businesspartner.service';
import { ControlOfficeService } from '../control-office.service';
import { of } from 'rxjs';
import { BusinessPartner } from '../models/BusinessPartner';
import { ControlOffice } from '../models/ControlOffice';

const mockControlOffices: ControlOffice[] = [
  { code: 1, name: 'ZEMK' },
  { code: 2, name: 'GDPC' }
];

const mockBusinessPartners: BusinessPartner[] = [
  { id: '1000131419', name: 'Partner A' },
  { id: '1000143862', name: 'Partner B' }
];

const fakeControlOfficeService: Partial<ControlOfficeService> = {
  get controlOffices$() {
    return of(mockControlOffices);
  },
  fetchControlOffices() {
    return of(mockControlOffices);
  }
};

const fakeBusinessPartnerService: Partial<BusinessPartnerService> = {
  get businessPartners$() {
    return of(mockBusinessPartners);
  },
  fetchBusinessPartners() {
    return of(mockBusinessPartners);
  }
};

// Extend the shared fakeReportService with downloadCsv for this spec
const mockReportService = {
  ...fakeReportService,
  downloadCsv: jest.fn()
};

describe('ReportComponent', (): void => {
  let component: ReportComponent;
  let fixture: ComponentFixture<ReportComponent>;

  beforeEach(async (): Promise<void> => {
    jest.restoreAllMocks();
    await TestBed.configureTestingModule({
      declarations: [ReportComponent],
      imports: [HttpClientTestingModule, TranslateModule.forRoot()],
      providers: [
        {
          provide: ReportService,
          useValue: mockReportService
        },
        {
          provide: FeatureFlagsService,
          useValue: fakeFeatureFlagsService
        },
        {
          provide: ControlOfficeService,
          useValue: fakeControlOfficeService
        },
        {
          provide: BusinessPartnerService,
          useValue: fakeBusinessPartnerService
        }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach((): void => {
    fixture = TestBed.createComponent(ReportComponent);
    component = fixture.componentInstance;
    component.filter('filter=hallmarkingType$SINGLE_SWISS');
    fixture.detectChanges();
  });

  afterEach((): void => {
    jest.restoreAllMocks();
  });

  describe('genTreeData', (): void => {
    const expectedBoth = readJsonFileFromFront('report-component-tree-data-both.json');

    it('when called with feature flag enabled, return report with 2 subtrees', done => {
      component.genTreeData().subscribe(treeData => {
        expect(treeData).toHaveLength(2);
        expect(treeData).toEqual(expectedBoth);
        done();
      });
    });

    it('when filter called with hallmarkingType, return encoded filter data without hallmarking types', done => {
      const fetchAnalysisReport = jest.spyOn(mockReportService, 'fetchAnalysisReport');
      const fetchHallmarkingReport = jest.spyOn(mockReportService, 'fetchHallmarkingReport');
      // Using T10:00:00.000Z (midday UTC) to ensure date is timezone-independent
      component.filter(
        'filter=officeName%24ZEMK%C2%A7GDPC%7Cdate%242025-04-03T10%3A00%3A00.000Z%C2%A7%21%7CbusinessPartner%241000131419%7ChallmarkingType%24SINGLE_INTERNATIONAL'
      );
      expect(fetchAnalysisReport).toHaveBeenCalledTimes(1);
      expect(fetchHallmarkingReport).toHaveBeenCalledTimes(1);
      expect(fetchAnalysisReport).toHaveBeenCalledWith(
        'filter=officeName%24ZEMK%C2%A7GDPC%7Cdate%242025-04-03%C2%A7!%7CbusinessPartner%241000131419'
      );
      expect(fetchHallmarkingReport).toHaveBeenCalledWith(
        'filter=officeName%24ZEMK%C2%A7GDPC%7Cdate%242025-04-03%C2%A7%21%7CbusinessPartner%241000131419%7ChallmarkingType%24SINGLE_INTERNATIONAL'
      );
      done();
    });
  });

  describe('ngOnInit', (): void => {
    it('should populate filterData with categories', (done): void => {
      component.ngOnInit();
      component.filterData.subscribe(filterData => {
        expect(filterData).toBeTruthy();
        expect(filterData.categories).toHaveLength(4);
        expect(filterData.outputLegacyQueryStringFormat).toBe(false);
        done();
      });
    });

    it('should include control office filter category', (done): void => {
      component.ngOnInit();
      component.filterData.subscribe(filterData => {
        const officeCategory = filterData.categories.find(c => c.category === 'officeName');
        expect(officeCategory).toBeTruthy();
        expect(officeCategory.type).toBe('multiSelect');
        expect(officeCategory.items).toHaveLength(mockControlOffices.length);
        done();
      });
    });

    it('should include business partner filter category', (done): void => {
      component.ngOnInit();
      component.filterData.subscribe(filterData => {
        const bpCategory = filterData.categories.find(c => c.category === 'businessPartner');
        expect(bpCategory).toBeTruthy();
        expect(bpCategory.type).toBe('multiSelect');
        expect(bpCategory.items).toHaveLength(mockBusinessPartners.length);
        done();
      });
    });

    it('should include date range filter category', (done): void => {
      component.ngOnInit();
      component.filterData.subscribe(filterData => {
        const dateCategory = filterData.categories.find(c => c.category === 'date');
        expect(dateCategory).toBeTruthy();
        expect(dateCategory.type).toBe('dateRange');
        done();
      });
    });

    it('should include hallmarking type filter category with 2 items', (done): void => {
      component.ngOnInit();
      component.filterData.subscribe(filterData => {
        const hallmarkingCategory = filterData.categories.find(c => c.category === 'hallmarkingType');
        expect(hallmarkingCategory).toBeTruthy();
        expect(hallmarkingCategory.items).toHaveLength(2);
        done();
      });
    });

    it('should initialise treeConfig$ and treeData$', (done): void => {
      component.ngOnInit();
      component.filterData.subscribe(() => {
        expect(component.treeConfig$).toBeTruthy();
        expect(component.treeData$).toBeTruthy();
        done();
      });
    });
  });

  describe('defineTreeConfig', (): void => {
    it('should return a QdTreeConfig with 7 columns', (done): void => {
      component.defineTreeConfig().subscribe(config => {
        expect(config).toBeTruthy();
        expect(config.columns).toHaveLength(7);
        expect(config.mainColumn).toBe('title');
        done();
      });
    });
  });

  describe('exportCsv', (): void => {
    it('should call reportService.downloadCsv', (): void => {
      const downloadCsvSpy = jest.spyOn(mockReportService, 'downloadCsv').mockImplementation(() => {});
      component.exportCsv();
      expect(downloadCsvSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('filter', (): void => {
    it('should call fetchHallmarkingReport and fetchAnalysisReport', (): void => {
      const fetchAnalysisReport = jest.spyOn(mockReportService, 'fetchAnalysisReport');
      const fetchHallmarkingReport = jest.spyOn(mockReportService, 'fetchHallmarkingReport');
      component.filter('filter=test%24value');
      expect(fetchHallmarkingReport).toHaveBeenCalledTimes(1);
      expect(fetchAnalysisReport).toHaveBeenCalledTimes(1);
    });

    it('should pass unchanged filter to fetchAnalysisReport when no hallmarkingType is present', (): void => {
      const fetchAnalysisReport = jest.spyOn(mockReportService, 'fetchAnalysisReport');
      component.filter('filter=officeName%24ZEMK');
      expect(fetchAnalysisReport).toHaveBeenCalledWith('filter=officeName%24ZEMK');
    });

    it('should handle filter string without "=" sign (no query params)', (): void => {
      const fetchAnalysisReport = jest.spyOn(mockReportService, 'fetchAnalysisReport');
      component.filter('');
      expect(fetchAnalysisReport).toHaveBeenCalledWith('');
    });
  });
});

describe('ReportComponent with null service responses', (): void => {
  let component: ReportComponent;
  let fixture: ComponentFixture<ReportComponent>;

  beforeEach(async (): Promise<void> => {
    await TestBed.configureTestingModule({
      declarations: [ReportComponent],
      imports: [HttpClientTestingModule, TranslateModule.forRoot()],
      providers: [
        { provide: ReportService, useValue: mockReportService },
        { provide: FeatureFlagsService, useValue: fakeFeatureFlagsService },
        { provide: ControlOfficeService, useValue: { controlOffices$: of(null), fetchControlOffices: () => of(null) } },
        {
          provide: BusinessPartnerService,
          useValue: { businessPartners$: of(null), fetchBusinessPartners: () => of(null) }
        }
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach((): void => {
    fixture = TestBed.createComponent(ReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should use empty arrays when controlOffices and businessPartners are null', (done): void => {
    component.filterData.subscribe(filterData => {
      const officeCategory = filterData.categories.find(c => c.category === 'officeName');
      expect(officeCategory.items).toHaveLength(0);
      const bpCategory = filterData.categories.find(c => c.category === 'businessPartner');
      expect(bpCategory.items).toHaveLength(0);
      done();
    });
  });
});
