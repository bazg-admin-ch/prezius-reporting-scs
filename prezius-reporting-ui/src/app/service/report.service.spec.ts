import { inject, TestBed } from '@angular/core/testing';
import { ReportService } from './report.service';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { appEnvironment } from '../../environments/environment';
import { ReportLine } from '../models/ReportLine';
import { readJsonFileFromBack } from '../test/test-utils';
import { Observable } from 'rxjs';
import { HallmarkingReport } from '../models/HallmarkingReport';
import { AnalysisReport } from '../models/analysis-report.model';

describe('ReportService', (): void => {
  let serviceUnderTest: ReportService;
  let httpTestingController: HttpTestingController;

  beforeEach((): void => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReportService]
    });
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(inject([ReportService], (reportService: ReportService): void => {
    serviceUnderTest = reportService;
  }));

  describe('downloadCsv()', (): void => {
    it('when called without filter, should call the correct endpoint', (done): void => {
      serviceUnderTest.downloadCsv();

      const request = httpTestingController.expectOne(`${appEnvironment.BACKEND_SERVICE_API}api/ui/reports/export?`);
      expect(request.request.method).toBe('GET');
      request.flush(new ArrayBuffer(2));
      httpTestingController.verify();
      done();
    });

    it('when called with filter, should call the correct endpoint with filter', (done): void => {
      serviceUnderTest.downloadCsv('filter=test%24value');

      const request = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/reports/export?filter=test%24value`
      );
      expect(request.request.method).toBe('GET');
      request.flush(new ArrayBuffer(2));
      httpTestingController.verify();
      done();
    });
  });

  describe('downloadFile()', (): void => {
    it('should create an anchor element and trigger a download', (): void => {
      const mockUrl = 'blob:http://localhost/mock-url';
      const mockAnchor = {
        style: { display: '' },
        href: '',
        download: '',
        click: jest.fn(),
        remove: jest.fn()
      } as any as HTMLAnchorElement;

      const createElementSpy = jest.spyOn(document, 'createElement').mockReturnValue(mockAnchor);
      const appendChildSpy = jest.spyOn(document.body, 'appendChild').mockImplementation(() => mockAnchor);

      const originalCreateObjectURL = window.URL.createObjectURL;
      const originalRevokeObjectURL = window.URL.revokeObjectURL;
      const createObjectURLMock = jest.fn().mockReturnValue(mockUrl);
      const revokeObjectURLMock = jest.fn();
      Object.defineProperty(window.URL, 'createObjectURL', {
        value: createObjectURLMock,
        writable: true,
        configurable: true
      });
      Object.defineProperty(window.URL, 'revokeObjectURL', {
        value: revokeObjectURLMock,
        writable: true,
        configurable: true
      });

      serviceUnderTest.downloadFile(new ArrayBuffer(2), 'application/csv');

      expect(createElementSpy).toHaveBeenCalledWith('a');
      expect(appendChildSpy).toHaveBeenCalledWith(mockAnchor);
      expect(mockAnchor.style.display).toBe('none');
      expect(createObjectURLMock).toHaveBeenCalled();
      expect(mockAnchor.href).toBe(mockUrl);
      expect(mockAnchor.download).toBe('report.csv');
      expect(mockAnchor.click).toHaveBeenCalled();
      expect(revokeObjectURLMock).toHaveBeenCalledWith(mockUrl);
      expect(mockAnchor.remove).toHaveBeenCalled();

      Object.defineProperty(window.URL, 'createObjectURL', {
        value: originalCreateObjectURL,
        writable: true,
        configurable: true
      });
      Object.defineProperty(window.URL, 'revokeObjectURL', {
        value: originalRevokeObjectURL,
        writable: true,
        configurable: true
      });
      createElementSpy.mockRestore();
      appendChildSpy.mockRestore();
    });
  });

  describe('fetchHallmarkingReport()', () => {
    it('should call correct endpoint without filter', (done): void => {
      const expectedRequestBody = readJsonFileFromBack('response/get-hallmarking-report.json');
      serviceUnderTest.fetchHallmarkingReport().subscribe(report => {
        testHallmarkingReport(report);
        done();
      });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/hallmarking-reports?`
      );
      expect(request.request.method).toBe('GET');
      request.flush(expectedRequestBody);
      httpTestingController.verify();
    });

    it('should call correct endpoint with filter', (done): void => {
      const expectedRequestBody = readJsonFileFromBack('response/get-hallmarking-report.json');
      serviceUnderTest
        .fetchHallmarkingReport('date$2025-02-28T23:00:00.000Z§2025-03-06T23:00:00.000Z%7CbusinessPartner$1000143862')
        .subscribe(report => {
          testHallmarkingReport(report);
          done();
        });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/hallmarking-reports?date$2025-02-28T23:00:00.000Z§2025-03-06T23:00:00.000Z%7CbusinessPartner$1000143862`
      );
      expect(request.request.method).toBe('GET');
      request.flush(expectedRequestBody);
      httpTestingController.verify();
    });
  });

  describe('fetchAnalysisReport()', () => {
    it('should call correct endpoint without filter', (done): void => {
      const expectedRequestBody = readJsonFileFromBack('response/get-analysis-report.json');
      serviceUnderTest.fetchAnalysisReport().subscribe(report => {
        testAnalysisReport(report);
        done();
      });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/analysis-reports?`
      );
      expect(request.request.method).toBe('GET');
      request.flush(expectedRequestBody);
      httpTestingController.verify();
    });

    it('should call correct endpoint with filter', (done): void => {
      const expectedRequestBody = readJsonFileFromBack('response/get-analysis-report.json');
      serviceUnderTest
        .fetchAnalysisReport('date$2025-02-28T23:00:00.000Z§2025-03-06T23:00:00.000Z%7CbusinessPartner$1000143862')
        .subscribe(report => {
          testAnalysisReport(report);
          done();
        });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/analysis-reports?date$2025-02-28T23:00:00.000Z§2025-03-06T23:00:00.000Z%7CbusinessPartner$1000143862`
      );
      expect(request.request.method).toBe('GET');
      request.flush(expectedRequestBody);
      httpTestingController.verify();
    });
  });

  describe('hallmarkingReport$', () => {
    it('should return correct value', (done): void => {
      const report = serviceUnderTest.hallmarkingReport$;
      expect(report).toBeTruthy();
      expect(report).toBeInstanceOf(Observable);
      done();
    });
  });

  describe('analysisReport$', () => {
    it('should return correct value', (done): void => {
      const report = serviceUnderTest.analysisReport$;
      expect(report).toBeTruthy();
      expect(report).toBeInstanceOf(Observable);
      done();
    });
  });
});

function testHallmarkingReport(report: HallmarkingReport): void {
  testReportLine(report['watchLaserCertified'], 264, 735, 99, 88, 1186);
  testReportLine(report['watchLaserNotCertified'], 676, 2125, 1306, 329, 4436);
  testReportLine(report['watchMechanicalCertified'], 247, 1160, 499, 66, 1972);
  testReportLine(report['watchMechanicalNotCertified'], 2848, 8009, 1883, 836, 13576);
  testReportLine(report['jewelLaserCertified'], 111, 122, 68, 96, 397);
  testReportLine(report['jewelLaserNotCertified'], 2291, 478, 40, 1780, 4589);
  testReportLine(report['jewelMechanicalCertified'], 155, 118, 24, 90, 387);
  testReportLine(report['jewelMechanicalNotCertified'], 7261, 3077, 6, 3628, 13972);
  testReportLine(report['watchLaserTotal'], 940, 2860, 1405, 417, 5622);
  testReportLine(report['watchMechanicalTotal'], 3095, 9169, 2382, 902, 15548);
  testReportLine(report['jewelLaserTotal'], 2402, 600, 108, 1876, 4986);
  testReportLine(report['jewelMechanicalTotal'], 7416, 3195, 30, 3718, 14359);
  testReportLine(report['watchTotal'], 4035, 12029, 3787, 1319, 21170);
  testReportLine(report['jewelTotal'], 9818, 3795, 138, 5594, 19345);
  testReportLine(report['total'], 13853, 15824, 3925, 6913, 40515);
}

function testAnalysisReport(report: AnalysisReport): void {
  testReportLine(report['total'], 100, 200, 400, 300, 0);
  testReportLine(report['CONFORMITY_EVALUATION'], 1100, 1200, 120, 130, 2550);
  testReportLine(report['DETERMINATION_ON_SAMPLES'], 100, 200, 20, 30, 350);
  testReportLine(report['DETERMINATION_ON_MELTING_POINT'], 200, 300, 120, 130, 750);
  expect(report['SAMPLES_FROM_HALLMARKING']).toBeNull();
  expect(report['SAMPLES_FROM_OTHER_PROCESSES']).toBeNull();
}

function testReportLine(line: ReportLine, ag: number, au: number, pd: number, pt: number, total: number): void {
  expect(line.ag).toBe(ag);
  expect(line.au).toBe(au);
  expect(line.pd).toBe(pd);
  expect(line.pt).toBe(pt);
  expect(line.total).toBe(total);
}
