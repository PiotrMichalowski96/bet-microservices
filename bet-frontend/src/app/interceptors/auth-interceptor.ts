import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import {BehaviorSubject, catchError, filter, Observable, switchMap, take, throwError} from 'rxjs';
import {AuthService} from "../services/auth.service";
import {Cookie} from "ng2-cookies";
import {REFRESH_TOKEN, TOKEN_HEADER_PREFIX} from "../util/token-properties";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private authService: AuthService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<Object>> {
    return next.handle(req).pipe(catchError(error => {
      if (this.isUnauthorizedError(error)) {
        return this.handle401Error(req, next);
      }
      return throwError(error);
    }));
  }

  private isUnauthorizedError(error: any): boolean {
    if (!(error instanceof HttpErrorResponse)) {
      return false;
    }
    return error.status === 401 || error.status === 0;
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<Object>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const token = Cookie.get(REFRESH_TOKEN);

      if (token) {
        return this.authService.refreshToken(token).pipe(
          switchMap((token: any) => {
            this.isRefreshing = false;

            this.authService.saveToken(token.access_token);
            this.refreshTokenSubject.next(token.access_token);

            return next.handle(this.addTokenHeader(request, token.access_token));
          }),
          catchError((err) => {
            this.isRefreshing = false;
            this.authService.logout()
            return throwError(err);
          })
        );
      }
    }

    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap((token) => next.handle(this.addTokenHeader(request, token)))
    );
  }

  private addTokenHeader(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({headers: request.headers.set('Authorization', TOKEN_HEADER_PREFIX + token)});
  }
}
