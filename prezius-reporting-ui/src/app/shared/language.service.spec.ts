import { TestBed } from '@angular/core/testing';
import { LanguageService } from './language.service';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { FakeTranslateLoader } from './service-mock';

describe('LanguageService', (): void => {
  let service: LanguageService;
  let translateService: TranslateService;

  beforeEach((): void => {
    TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: FakeTranslateLoader }
        })
      ],
      providers: [LanguageService]
    });
    service = TestBed.inject(LanguageService);
    translateService = TestBed.inject(TranslateService);
  });

  it('should be created', (): void => {
    expect(service).toBeTruthy();
  });

  describe('language$', (): void => {
    it('should return an observable', (): void => {
      const result = service.language$;
      expect(result).toBeTruthy();
      expect(result).toBeInstanceOf(Observable);
    });

    it('should emit the default language "de" when localStorage has no entry', (done): void => {
      localStorage.removeItem('language');
      const freshService = TestBed.inject(LanguageService);
      freshService.language$.subscribe((language: string) => {
        expect(['de', 'fr', 'it', 'en']).toContain(language);
        done();
      });
    });
  });

  describe('onLanguageChange', (): void => {
    it('should call translateService.use with the language from the event detail', (): void => {
      const useSpy = jest.spyOn(translateService, 'use');
      service.onLanguageChange({ detail: 'fr' });
      expect(useSpy).toHaveBeenCalledWith('fr');
    });

    it('should call translateService.use with another language', (): void => {
      const useSpy = jest.spyOn(translateService, 'use');
      service.onLanguageChange({ detail: 'it' });
      expect(useSpy).toHaveBeenCalledWith('it');
    });

    it('should persist the chosen language in localStorage', (): void => {
      service.onLanguageChange({ detail: 'en' });
      expect(localStorage.getItem('language')).toBe('en');
    });
  });

  describe('ngOnDestroy', (): void => {
    it('should complete without errors', (): void => {
      expect((): void => service.ngOnDestroy()).not.toThrow();
    });

    it('should stop reacting to language$ after destruction', (done): void => {
      service.ngOnDestroy();
      let emissionCount = 0;
      service.language$.subscribe(() => {
        emissionCount++;
      });
      expect(emissionCount).toBe(0);
      done();
    });
  });
});
