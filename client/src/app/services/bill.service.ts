import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class BillService {
  url = environment.apiUrl;

  constructor(private http: HttpClient) {}

  generateReport(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post(this.url + '/bill/generateReport', data, {
      headers: headers,
    });
  }

  getPdf(data: any): Observable<Blob> {

    return this.http.post(this.url + '/bill/getPdf', data, {
      responseType: 'blob',
    });
  }

  getBills() {
    return this.http.get(this.url + '/bill/getBills');
  }

  deleteBill(id: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.delete(this.url + '/bill/delete/' + id, {
      headers: headers,
    });
  }
}
