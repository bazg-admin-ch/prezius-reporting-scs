import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { FeatureFlagsService } from '../../../service/feature-flags.service';
import { FeatureFlags } from '../../../models/enums/FeatureFlags';
import { catchError, map, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { featureFlagsReceived, loadFeatureFlags, loadFeatureFlagsFailed } from '../actions/feature-flag.action';

@Injectable()
export class FeatureFlagEffect {
  private readonly actions$ = inject(Actions);
  private readonly featureFlagsService = inject(FeatureFlagsService);

  readonly loadFeatureFlags$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadFeatureFlags),
      switchMap(() =>
        this.featureFlagsService.fetchFeatureFlags().pipe(
          map((featureFlags: FeatureFlags) => featureFlagsReceived({ featureFlags })),
          catchError((error: unknown) => {
            console.error('Could not fetch featureFlags', error);
            return of(loadFeatureFlagsFailed({ error }));
          })
        )
      )
    )
  );
}
