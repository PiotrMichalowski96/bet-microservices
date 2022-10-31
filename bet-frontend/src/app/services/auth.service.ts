import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Cookie} from "ng2-cookies";
import {ID_TOKEN, TOKEN} from '../util/token-properties';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly clientId: string = 'bet-frontend-app';
  private readonly redirectUri: string = 'http://localhost:4201';

  public readonly authorizationUri: string = `http://localhost:8080/realms/BetSpringBootKeycloak/protocol/openid-connect/auth?response_type=code&scope=openid%20roles&client_id=${this.clientId}&redirect_uri=${this.redirectUri}`;

  private readonly authorizationCodeGrant: string = 'authorization_code';
  private readonly tokenUri: string = 'http://localhost:8080/realms/BetSpringBootKeycloak/protocol/openid-connect/token';

  constructor(private http: HttpClient) {
  }

  retrieveToken(code: string) {
    let body = new HttpParams()
    .set('grant_type', this.authorizationCodeGrant)
    .set('client_id', this.clientId)
    .set('redirect_uri', this.redirectUri)
    .set('code', code);

    let options = {
      headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')
    };

    this.http.post(this.tokenUri, body.toString(), options)
    .subscribe(
      data => this.saveToken(data),
      err => alert('Invalid Credentials')
    );
  }

  saveToken(token: any) {
    let expireDate = new Date().getTime() + (1000 * token.expires_in);
    Cookie.set(TOKEN, token.access_token, expireDate);
    console.log('Obtained Access token');
    window.location.href = 'http://localhost:4201';
  }

  checkCredentials() {
    return Cookie.check('access_token');
  }

  logout() {
    Cookie.delete(TOKEN);
    Cookie.delete(ID_TOKEN);
    window.location.reload();
  }
}
