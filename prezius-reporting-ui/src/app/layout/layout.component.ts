import { Component, OnInit, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../shared/store/reducers/app.reducer';
import { loadFeatureFlags } from '../shared/store/actions/feature-flag.action';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss'],
  standalone: false
})
export class LayoutComponent implements OnInit {
  private readonly store = inject<Store<AppState>>(Store);

  ngOnInit(): void {
    this.store.dispatch(loadFeatureFlags());
  }
}
