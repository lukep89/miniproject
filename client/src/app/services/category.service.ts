import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  url = environment.apiUrl;

  constructor(private http: HttpClient) {}

  addCategory(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post(this.url + '/category/add', data, {
      headers: headers,
    });
  }

  updateCategory(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put(this.url + '/category/update', data, {
      headers: headers,
    });
  }

  getCategory() {
    return this.http.get(this.url + '/category/get');
  }
}
