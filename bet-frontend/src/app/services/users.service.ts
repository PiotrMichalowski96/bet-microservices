import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {TOKEN, TOKEN_HEADER_PREFIX} from "../util/token-properties";
import {Cookie} from "ng2-cookies";
import {User, UserResult} from "../model/user";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private static readonly baseUrl: string = 'http://localhost:8083';

  constructor(private http: HttpClient) {
  }

  getUsersResults(): Observable<UserResult[]> {
    return this.http.get<UserResult[]>(`${UsersService.baseUrl}/users-results`);
  }

  getCurrentUser(): Observable<User> {
    let options = {
      headers: this.createAuthHeader()
    };
    return this.http.get<User>(`${UsersService.baseUrl}/users/current`, options);
  }

  private createAuthHeader(): HttpHeaders {
    return new HttpHeaders().set('Authorization', TOKEN_HEADER_PREFIX + Cookie.get(TOKEN));
  }
}
