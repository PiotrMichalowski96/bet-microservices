import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ACCESS_TOKEN, TOKEN_HEADER_PREFIX} from "../util/token-properties";
import {Cookie} from "ng2-cookies";
import {User, UserResult} from "../model/user";
import {Observable} from "rxjs";
import {AppConfig} from "../config/app.config";

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private static readonly userResultEndpoint: string = '/users-results';
  private static readonly currentUserEndpoint: string = '/users/current';

  private readonly baseUrl: string = '';

  constructor(private http: HttpClient, private appConfig: AppConfig) {
    let config = this.appConfig.getConfig();
    if (config !== null) {
      this.baseUrl = config.backendBaseUrl;
    }
  }

  getUsersResults(): Observable<UserResult[]> {
    return this.http.get<UserResult[]>(this.baseUrl + UsersService.userResultEndpoint);
  }

  getCurrentUser(): Observable<User> {
    let options = {
      headers: this.createAuthHeader()
    };
    return this.http.get<User>(this.baseUrl + UsersService.currentUserEndpoint, options);
  }

  private createAuthHeader(): HttpHeaders {
    return new HttpHeaders().set('Authorization', TOKEN_HEADER_PREFIX + Cookie.get(ACCESS_TOKEN));
  }
}
