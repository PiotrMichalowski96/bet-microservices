import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Cookie} from "ng2-cookies";
import {ID_TOKEN, ACCESS_TOKEN, REFRESH_TOKEN} from '../util/token-properties';
import {Observable} from "rxjs";
import {AppConfig} from "../config/app.config";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private static readonly authorizationCodeGrant: string = 'authorization_code';
  private static readonly refreshTokenGrant: string = 'refresh_token';
  private static readonly options = {
    headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')
  };

  public readonly authorizationUri: string = '';
  private readonly frontendBaseUrl: string = '';
  private readonly clientId: string = '';
  private readonly tokenUri: string = '';

  constructor(private http: HttpClient, private appConfig: AppConfig) {
    let config = this.appConfig.getConfig();
    if (config != null) {
      this.authorizationUri = config.authorizationUri;
      this.clientId = config.clientId;
      this.frontendBaseUrl = config.frontendBaseUrl;
      this.tokenUri = config.tokenUri;
    }
  }


  retrieveToken(code: string): void {
    let body = new HttpParams()
    .set('grant_type', AuthService.authorizationCodeGrant)
    .set('client_id', this.clientId)
    .set('redirect_uri', this.frontendBaseUrl)
    .set('code', code);

    this.http.post(this.tokenUri, body.toString(), AuthService.options)
    .subscribe(
      data => {
        this.saveToken(data);
        this.saveRefreshToken(data);
        window.location.href = this.frontendBaseUrl;
      },
      err => {
        alert('Invalid Credentials');
      }
    );
  }

  saveToken(token: any): void {
    let expireDate = new Date().getTime() + (1000 * token.expires_in);
    Cookie.set(ACCESS_TOKEN, token.access_token, expireDate);
  }

  saveRefreshToken(token: any): void {
    let expireDate = new Date().getTime() + (1000 * token.refresh_expires_in);
    Cookie.set(REFRESH_TOKEN, token.refresh_token, expireDate);
  }

  refreshToken(token: string): Observable<any> {
    let body = new HttpParams()
    .set('grant_type', AuthService.refreshTokenGrant)
    .set('client_id', this.clientId)
    .set('refresh_token', token);

    return this.http.post(this.tokenUri, body.toString(), AuthService.options);
  }

  hasCredentials(): boolean {
    return Cookie.check('access_token');
  }

  logout(): void {
    Cookie.delete(ACCESS_TOKEN);
    Cookie.delete(REFRESH_TOKEN);
    Cookie.delete(ID_TOKEN);
    window.location.reload();
  }
}
