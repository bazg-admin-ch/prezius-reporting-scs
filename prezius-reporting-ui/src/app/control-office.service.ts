import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, tap } from 'rxjs';
import { ControlOffice } from './models/ControlOffice';
import { HttpClient } from '@angular/common/http';
import { appEnvironment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ControlOfficeService {
  private httpClient = inject(HttpClient);

  private controlOffices = new BehaviorSubject(new Array<ControlOffice>());

  get controlOffices$() {
    return this.controlOffices.asObservable();
  }

  fetchControlOffices() {
    return this.httpClient.get<ControlOffice[]>(`${appEnvironment.BACKEND_SERVICE_API}api/ui/offices`).pipe(
      tap(controlOffices => {
        this.controlOffices.next(controlOffices);
      })
    );
  }
}
