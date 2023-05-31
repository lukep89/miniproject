import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
// import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  
  constructor(private http: HttpClient) {}

  addProduct(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post('/api/product/add', data, {
      headers: headers,
    });
  }

  getProducts() {
    return this.http.get('/api/product/get');
  }

  updateProduct(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put('/api/product/update', data, {
      headers: headers,
    });
  }

  updateProductStatus(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put('/api/product/updateStatus', data, {
      headers: headers,
    });
  }

  deleteProduct(id: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.delete('/api/product/delete/' + id, {
      headers: headers,
    });
  }

  getProductListByCategory(catId: any) {
    return this.http.get('/api/product/getByCategory/' + catId);
  }

  getProductById(id: any) {
    return this.http.get('/api/product/getById/' + id);
  }
}
