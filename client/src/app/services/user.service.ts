import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  url = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // using observable
  signup(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post(this.url + '/user/signup', data, {
      headers: headers,
    });
  }

  // // using promise
  // signup(data: any): Promise<any> {
  //   const headers = new HttpHeaders().set('Content-Type', 'application/json');

  //   return lastValueFrom(
  //     this.http.post(this.url + '/api/user/signup', data, { headers: headers })
  //   );
  // }

  forgotPassword(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post(this.url + '/user/forgotPassword', data, {
      headers: headers,
    });
  }

  login(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post(this.url + '/user/login', data, {
      headers: headers,
    });
  }

  checkToken() {
    return this.http.get(this.url + '/user/checkToken');
  }

  changePassword(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put(this.url + "/user/changePassword", data, {
      headers: headers,
    }); 
  }
}
