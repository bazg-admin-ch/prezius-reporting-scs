import { inject, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { BusinessPartnerService } from './businesspartner.service';
import { appEnvironment } from '../environments/environment';
import { BusinessPartner } from './models/BusinessPartner';
import { Observable } from 'rxjs';

describe('BusinessPartnerService', (): void => {
  let serviceUnderTest: BusinessPartnerService;
  let httpTestingController: HttpTestingController;

  beforeEach((): void => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BusinessPartnerService]
    });
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(inject([BusinessPartnerService], (service: BusinessPartnerService): void => {
    serviceUnderTest = service;
  }));

  afterEach((): void => {
    httpTestingController.verify();
  });

  describe('businessPartners$', (): void => {
    it('should return an observable', (): void => {
      const result = serviceUnderTest.businessPartners$;
      expect(result).toBeTruthy();
      expect(result).toBeInstanceOf(Observable);
    });

    it('should initially emit an empty array', (done): void => {
      serviceUnderTest.businessPartners$.subscribe((partners: BusinessPartner[]) => {
        expect(partners).toEqual([]);
        done();
      });
    });
  });

  describe('fetchBusinessPartners()', (): void => {
    it('should call the correct endpoint and return business partners', (done): void => {
      const mockPartners: BusinessPartner[] = [
        { id: '1000', name: 'Partner A' },
        { id: '2000', name: 'Partner B' }
      ];

      serviceUnderTest.fetchBusinessPartners().subscribe((partners: BusinessPartner[]) => {
        expect(partners).toEqual(mockPartners);
        done();
      });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/businesspartners`
      );
      expect(request.request.method).toBe('GET');
      request.flush(mockPartners);
    });

    it('should update businessPartners$ after a successful fetch', (done): void => {
      const mockPartners: BusinessPartner[] = [{ id: '1001', name: 'Partner X' }];

      serviceUnderTest.fetchBusinessPartners().subscribe(() => {
        serviceUnderTest.businessPartners$.subscribe((partners: BusinessPartner[]) => {
          expect(partners).toEqual(mockPartners);
          done();
        });
      });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/businesspartners`
      );
      request.flush(mockPartners);
    });

    it('should return an empty array on HTTP error', (done): void => {
      serviceUnderTest.fetchBusinessPartners().subscribe((partners: BusinessPartner[]) => {
        expect(partners).toEqual([]);
        done();
      });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/businesspartners`
      );
      request.flush('error', { status: 500, statusText: 'Internal Server Error' });
    });
  });
});
