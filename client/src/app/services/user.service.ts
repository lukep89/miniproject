import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs';
// import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  // using observable
  signup(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post('/api/user/signup', data, {
      headers: headers,
    });
  }

  forgotPassword(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post('/api/user/forgotPassword', data, {
      headers: headers,
    });
  }

  login(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post('/api/user/login', data, {
      headers: headers,
    });
  }

  checkToken() {
    return this.http.get('/api/user/checkToken');
  }

  changePassword(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put('/api/user/changePassword', data, {
      headers: headers,
    });
  }

  getUsers() {
    return this.http.get('/api/user/getAllUsers');
  }

  updateUser(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put('/api/user/updateStatus', data, {
      headers: headers,
    });
  }

  resetPassword(data: any, paramValue: String) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put('/api/user/resetPassword?token=' + paramValue, data, {
      headers: headers,
    });
  }
}
