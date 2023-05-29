import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
// import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  constructor(private http: HttpClient) {}

  addCategory(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post('/api/category/add', data, {
      headers: headers,
    });
  }

  updateCategory(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put('/api/category/update', data, {
      headers: headers,
    });
  }

  getCategoryList() {
    return this.http.get('/api/category/get');
  }

  getFilteredCategoryList() {
    return this.http.get('/api/category/get?filterValue=true');
  }
}
