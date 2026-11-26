import { inject, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { ControlOfficeService } from './control-office.service';
import { appEnvironment } from '../environments/environment';
import { ControlOffice } from './models/ControlOffice';
import { Observable } from 'rxjs';

describe('ControlOfficeService', (): void => {
  let serviceUnderTest: ControlOfficeService;
  let httpTestingController: HttpTestingController;

  beforeEach((): void => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ControlOfficeService]
    });
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(inject([ControlOfficeService], (service: ControlOfficeService): void => {
    serviceUnderTest = service;
  }));

  afterEach((): void => {
    httpTestingController.verify();
  });

  describe('controlOffices$', (): void => {
    it('should return an observable', (): void => {
      const result = serviceUnderTest.controlOffices$;
      expect(result).toBeTruthy();
      expect(result).toBeInstanceOf(Observable);
    });

    it('should initially emit an empty array', (done): void => {
      serviceUnderTest.controlOffices$.subscribe((offices: ControlOffice[]) => {
        expect(offices).toEqual([]);
        done();
      });
    });
  });

  describe('fetchControlOffices()', (): void => {
    it('should call the correct endpoint and return control offices', (done): void => {
      const mockOffices: ControlOffice[] = [
        { code: 1, name: 'ZEMK' },
        { code: 2, name: 'GDPC' }
      ];

      serviceUnderTest.fetchControlOffices().subscribe((offices: ControlOffice[]) => {
        expect(offices).toEqual(mockOffices);
        done();
      });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/offices`
      );
      expect(request.request.method).toBe('GET');
      request.flush(mockOffices);
    });

    it('should update controlOffices$ after a successful fetch', (done): void => {
      const mockOffices: ControlOffice[] = [{ code: 3, name: 'TEST' }];

      serviceUnderTest.fetchControlOffices().subscribe(() => {
        serviceUnderTest.controlOffices$.subscribe((offices: ControlOffice[]) => {
          expect(offices).toEqual(mockOffices);
          done();
        });
      });

      const request: TestRequest = httpTestingController.expectOne(
        `${appEnvironment.BACKEND_SERVICE_API}api/ui/offices`
      );
      request.flush(mockOffices);
    });
  });
});
