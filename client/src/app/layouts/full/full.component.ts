import { MediaMatcher } from '@angular/cdk/layout';
import {
  ChangeDetectorRef,
  Component,
  OnDestroy,
  AfterViewInit,
} from '@angular/core';
import { RouteGuardService } from 'src/app/services/route-guard.service';

/* @title Responsive sidenav */
@Component({
  selector: 'app-full-layouts',
  templateUrl: 'full.component.html',
  styleUrls: ['full.component.css'],
})
export class FullComponent implements OnDestroy, AfterViewInit {
  mobileQuery: MediaQueryList;


  private _mobileQueryListener: () => void;

  constructor(changeDetectorRef: ChangeDetectorRef, media: MediaMatcher, routeGuardSvc: RouteGuardService) {
    this.mobileQuery = media.matchMedia('(min-width: 768px)');
    this._mobileQueryListener = () => changeDetectorRef.detectChanges();

    this.mobileQuery.addEventListener('change', this._mobileQueryListener);
    
  }

  ngOnDestroy(): void {
    this.mobileQuery.removeEventListener('change', this._mobileQueryListener);
  }
  ngAfterViewInit() {}
}
