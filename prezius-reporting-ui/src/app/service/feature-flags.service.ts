import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { FeatureFlags } from '../models/enums/FeatureFlags';
import { appEnvironment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FeatureFlagsService {
  private readonly _http = inject(HttpClient);

  private featureFlags = new BehaviorSubject<FeatureFlags>(null);

  getFeatureFlags(): Observable<FeatureFlags> {
    return this.featureFlags.asObservable();
  }

  fetchFeatureFlags(): Observable<FeatureFlags> {
    return this._http.get<FeatureFlags>(`${appEnvironment.BACKEND_SERVICE_API}api/ui/features`).pipe(
      tap(featureFlags => {
        this.featureFlags.next(featureFlags);
      })
    );
  }
}
