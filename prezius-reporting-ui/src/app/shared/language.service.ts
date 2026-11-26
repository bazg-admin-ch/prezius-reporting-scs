import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { takeUntil } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LanguageService implements OnDestroy {
  private readonly defaultLanguage = 'de';
  private readonly languageLocalStorageKey = 'language';
  private readonly _language = new BehaviorSubject<string>(
    localStorage.getItem(this.languageLocalStorageKey) || this.defaultLanguage
  );
  private readonly unsubscribe = new Subject<void>();

  public readonly language$ = this._language.asObservable();

  constructor(private readonly translateService: TranslateService) {
    this._language.pipe(takeUntil(this.unsubscribe)).subscribe(language => this.useLanguage(language));
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
    this._language.complete();
  }

  onLanguageChange = ($event: any): void => this.useLanguage($event.detail);

  private readonly useLanguage = (language: string): void => {
    this.translateService.use(language);
    localStorage.setItem(this.languageLocalStorageKey, language);
  };
}
