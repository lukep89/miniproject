import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
// import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class BillService {
  constructor(private http: HttpClient) {}

  generateReport(data: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post('/api/bill/generateReport', data, {
      headers: headers,
    });
  }

  getPdf(data: any): Observable<Blob> {
    return this.http.post('/api/bill/getPdf', data, {
      responseType: 'blob',
    });
  }

  getBills() {
    return this.http.get('/api/bill/getBills');
  }

  deleteBill(id: any) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.delete('/api/bill/delete/' + id, {
      headers: headers,
    });
  }
}
