import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutComponent } from './layout.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateModule } from '@ngx-translate/core';
import { provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';

describe('LayoutComponent', (): void => {
  let component: LayoutComponent;
  let fixture: ComponentFixture<LayoutComponent>;
  let fakeStore: Store;

  beforeEach((): void => {
    TestBed.configureTestingModule({
      declarations: [LayoutComponent],
      imports: [HttpClientTestingModule, TranslateModule.forRoot()],
      providers: [provideMockStore({})],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(LayoutComponent);
    fakeStore = TestBed.inject(Store);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', (): void => {
    expect(component).toBeTruthy();
  });
});
