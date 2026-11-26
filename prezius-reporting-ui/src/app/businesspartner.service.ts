import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, catchError, Observable, of, tap } from 'rxjs';
import { appEnvironment } from '../environments/environment';
import { BusinessPartner } from './models/BusinessPartner';

@Injectable({
  providedIn: 'root'
})
export class BusinessPartnerService {
  private httpClient = inject(HttpClient);

  private businessPartners = new BehaviorSubject<BusinessPartner[]>([]);

  get businessPartners$() {
    return this.businessPartners.asObservable();
  }

  fetchBusinessPartners() {
    return this.httpClient.get<BusinessPartner[]>(`${appEnvironment.BACKEND_SERVICE_API}api/ui/businesspartners`).pipe(
      tap(businessPartners => {
        this.businessPartners.next(businessPartners);
      }),
      catchError((): Observable<BusinessPartner[]> => {
        return of([]);
      })
    );
  }
}
