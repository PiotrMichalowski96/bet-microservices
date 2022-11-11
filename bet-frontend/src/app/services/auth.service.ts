import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Cookie} from "ng2-cookies";
import {ID_TOKEN, ACCESS_TOKEN, REFRESH_TOKEN} from '../util/token-properties';
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly clientId: string = 'bet-frontend-app';
  private readonly redirectUri: string = 'http://localhost:4201';

  public readonly authorizationUri: string = `http://host.docker.internal:8080/realms/BetSpringBootKeycloak/protocol/openid-connect/auth?response_type=code&scope=openid%20roles&client_id=${this.clientId}&redirect_uri=${this.redirectUri}`;

  private readonly authorizationCodeGrant: string = 'authorization_code';
  private readonly refreshTokenGrant: string = 'refresh_token';
  private readonly tokenUri: string = 'http://host.docker.internal:8080/realms/BetSpringBootKeycloak/protocol/openid-connect/token';

  private readonly options = {
    headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')
  };

  constructor(private http: HttpClient) {
  }

  retrieveToken(code: string): void {
    let body = new HttpParams()
    .set('grant_type', this.authorizationCodeGrant)
    .set('client_id', this.clientId)
    .set('redirect_uri', this.redirectUri)
    .set('code', code);

    this.http.post(this.tokenUri, body.toString(), this.options)
    .subscribe(
      data => {
        this.saveToken(data);
        this.saveRefreshToken(data);
        window.location.href = 'http://localhost:4201';
      },
      err => alert('Invalid Credentials')
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
    .set('grant_type', this.refreshTokenGrant)
    .set('client_id', this.clientId)
    .set('refresh_token', token);

    return this.http.post(this.tokenUri, body.toString(), this.options);
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
