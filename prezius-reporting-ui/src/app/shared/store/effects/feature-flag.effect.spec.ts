import '@angular/compiler';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Action } from '@ngrx/store';
import { Observable, throwError } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { FeatureFlags } from '../../../models/enums/FeatureFlags';
import { FeatureFlagsService } from '../../../service/feature-flags.service';
import { featureFlagsReceived, loadFeatureFlags, loadFeatureFlagsFailed } from '../actions/feature-flag.action';
import { FeatureFlagEffect } from './feature-flag.effect';

describe('FeatureFlagEffect', (): void => {
  let actions$: Observable<Action>;
  let effect: FeatureFlagEffect;
  let featureFlagsService: jest.Mocked<Pick<FeatureFlagsService, 'fetchFeatureFlags'>>;
  let testScheduler: TestScheduler;

  beforeEach((): void => {
    jest.spyOn(console, 'error').mockImplementation((): void => undefined);
    featureFlagsService = {
      fetchFeatureFlags: jest.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        FeatureFlagEffect,
        provideMockActions(() => actions$),
        {
          provide: FeatureFlagsService,
          useValue: featureFlagsService
        }
      ]
    });

    effect = TestBed.inject(FeatureFlagEffect);

    testScheduler = new TestScheduler((actual: unknown, expected: unknown): void => {
      expect(actual).toEqual(expected);
    });
  });

  it('given successful response when loading feature flags then dispatches success action', (): void => {
    testScheduler.run(({ cold, hot, expectObservable }): void => {
      const featureFlags: FeatureFlags = { PREZIUS_3_0: true };
      actions$ = hot('-a', { a: loadFeatureFlags() });
      featureFlagsService.fetchFeatureFlags.mockReturnValue(
        cold('--a|', {
          a: featureFlags
        })
      );

      expectObservable(effect.loadFeatureFlags$).toBe('---b', {
        b: featureFlagsReceived({ featureFlags })
      });
    });
  });

  it('given failing response when loading feature flags then dispatches failed action', (): void => {
    testScheduler.run(({ hot, expectObservable }): void => {
      const error: Error = new Error('boom');
      actions$ = hot('-a', { a: loadFeatureFlags() });
      featureFlagsService.fetchFeatureFlags.mockReturnValue(throwError(() => error));

      expectObservable(effect.loadFeatureFlags$).toBe('-b', {
        b: loadFeatureFlagsFailed({ error })
      });
    });
  });
});
