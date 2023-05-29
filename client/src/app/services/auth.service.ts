import { Injectable } from '@angular/core';
import { Router } from '@angular/router';


@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // serivce to check for valid token

  constructor(private router: Router) {}

  public isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    // console.log(token);

    if (!token) {
      this.router.navigate(['/']);
      return false;
    } else {
      return true;
    }
  }

}
