import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

// interceptor to modify the token

@Injectable()
export class TokenInterceptorInterceptor implements HttpInterceptor {
  constructor(private router: Router) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    // console.log(request.url)

    if (
      (request.url.indexOf('/api/user/login') !== -1 &&
        request.method.indexOf('GET') !== -1) ||
      (request.url.indexOf('/api/user/checkToken') !== -1 &&
        request.method.indexOf('GET') !== -1) ||
      (request.url.indexOf('/api/user/resetPassword') !== -1 &&
        request.method.indexOf('GET') !== -1) ||
      (request.url.indexOf('/resetPassword') !== -1 &&
        request.method.indexOf('GET') !== -1)
    ) {
      return next.handle(request);
    }

    const token = localStorage.getItem('token');

    if (token) {
      
      request = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` },
      });
    }

    return next.handle(request).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse) {
          // console.log(error.url);

          if (error.status === 401 || error.status === 403) {
            if (this.router.url === '/') {
            } else {
              localStorage.clear();
              this.router.navigate(['/']);
            }
          }
        }
        return throwError(error);
      })
    );
  }
}
