import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import {
  ActivatedRoute,
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { SnackbarService } from './snackbar.service';
import jwtDecode from 'jwt-decode';
import { GlobalConstants } from '../shared/global.constants';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RouteGuardService {
  // service to check if Admin or User so as to accessible to a particular path/url

  constructor(
    public authSvc: AuthService,
    public router: Router,
    private snakebarSvc: SnackbarService
  ) {}

  canActivate(routeSnapshot: ActivatedRouteSnapshot): boolean {
    let expectedRoleArray = routeSnapshot.data;
    // console.log(routeSnapshot);

    expectedRoleArray = expectedRoleArray['expectedRole'];

    const token: any = localStorage.getItem('token');

    var tokenPayload: any;

    try {
      tokenPayload = jwtDecode(token);
      // console.log(tokenPayload);
    } catch (error) {
      localStorage.clear();
      this.router.navigate(['/']);
    }

    let expectedRole = '';

    for (let i = 0; i < Object.values(expectedRoleArray).length; i++) {
      if (expectedRoleArray[i] == tokenPayload.role) {
        expectedRole = tokenPayload.role;
      }
    }

    // console.log(expectedRole);
    if (tokenPayload.role == 'user' || tokenPayload.role == 'admin') {
      if (this.authSvc.isAuthenticated() && tokenPayload.role == expectedRole) {
        return true;
      }

      this.snakebarSvc.openSnckBar(
        GlobalConstants.unathorizedMessage,
        GlobalConstants.error
      );
      this.router.navigate(['/cafe/dashboard']);
      return false;
    } else {
      this.router.navigate(['/']);
      localStorage.clear();
      return false;
    }
  }
}
