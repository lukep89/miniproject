import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  url = environment.apiUrl;

  constructor(private http: HttpClient) {}

  addProduct(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post(this.url + '/product/add', data, {
      headers: headers,
    });
  }

  getProducts() {
    return this.http.get(this.url + '/product/get');
  }

  updateProduct(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put(this.url + '/product/update', data, {
      headers: headers,
    });
  }

  updateProductStatus(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put(this.url + '/product/updateStatus', data, {
      headers: headers,
    });
  }

  deleteProduct(id: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.delete(this.url + '/product/delete/' + id, {
      headers: headers,
    });
  }

  getProductListByCategory(catId: any) {
    return this.http.get(this.url + '/product/getByCategory/' + catId);
  }

  getProductById(id: any) {
    return this.http.get(this.url + '/product/getById/' + id);
  }
}
