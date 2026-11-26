import { inject, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { Observable } from 'rxjs';
import { FeatureFlagsService } from './feature-flags.service';
import { appEnvironment } from '../../environments/environment';
import { FeatureFlags } from '../models/enums/FeatureFlags';

describe('FeatureFlagsService', (): void => {
  let serviceUnderTest: FeatureFlagsService;
  let httpTestingController: HttpTestingController;

  beforeEach((): void => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FeatureFlagsService]
    });
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(inject([FeatureFlagsService], (featureFlagsService: FeatureFlagsService): void => {
    serviceUnderTest = featureFlagsService;
  }));

  describe('fetchFeatureFlags()', () => {
    it('should call correct endpoint', (done): void => {
      const expectedResult = { EXAMPLE_FEATURE_FLAG: true };
      serviceUnderTest.fetchFeatureFlags().subscribe((featureFlags: TestFeatureFlags) => {
        expect(featureFlags.EXAMPLE_FEATURE_FLAG).toEqual(true);
        done();
      });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/features`
      );
      expect(request.request.method).toBe('GET');
      request.flush(expectedResult);
      httpTestingController.verify();
    });
  });

  describe('getFeatureFlags', () => {
    it('should return correct value', (done): void => {
      const featureFlags = serviceUnderTest.getFeatureFlags();
      expect(featureFlags).toBeTruthy();
      expect(featureFlags).toBeInstanceOf(Observable);
      done();
    });
  });
});

export interface TestFeatureFlags extends FeatureFlags {
  EXAMPLE_FEATURE_FLAG: boolean;
}

describe('fakeFeatureFlagsService (service-mocks)', (): void => {
  it('getFeatureFlags should return an observable of FeatureFlags', (done): void => {
    const { fakeFeatureFlagsService } = require('../shared/service-mocks');
    fakeFeatureFlagsService.getFeatureFlags().subscribe((flags: FeatureFlags) => {
      expect(flags).toBeDefined();
      done();
    });
  });

  it('fetchFeatureFlags should return an observable of FeatureFlags', (done): void => {
    const { fakeFeatureFlagsService } = require('../shared/service-mocks');
    fakeFeatureFlagsService.fetchFeatureFlags().subscribe((flags: FeatureFlags) => {
      expect(flags).toBeDefined();
      done();
    });
  });
});

describe('fakeReportService (service-mocks)', (): void => {
  it('fetchHallmarkingReport without args should return an observable', (done): void => {
    const { fakeReportService } = require('../shared/service-mocks');
    fakeReportService.fetchHallmarkingReport().subscribe((report: any) => {
      expect(report).toBeDefined();
      done();
    });
  });

  it('fetchAnalysisReport without args should return an observable', (done): void => {
    const { fakeReportService } = require('../shared/service-mocks');
    fakeReportService.fetchAnalysisReport().subscribe((report: any) => {
      expect(report).toBeDefined();
      done();
    });
  });
});
