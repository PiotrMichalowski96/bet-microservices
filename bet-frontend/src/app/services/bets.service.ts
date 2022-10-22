import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Cookie} from "ng2-cookies";
import {TOKEN, TOKEN_HEADER_PREFIX} from "../constants/token-properties";
import {of, switchMap} from "rxjs";
import {Bet} from "../model/bet";

@Injectable({
  providedIn: 'root'
})
export class BetsService {

  private static readonly baseUrl: string = 'http://localhost:8083/bets';
  private static readonly itemsOnPage : number = 20;

  constructor(private http: HttpClient) { }

  getBets(page: number = 0) {
    let options = {
      // params: new HttpParams().set('matchId', 1), //TODO
      headers: this.createAuthHeader()
    };
    return this.http.get<Bet[]>(`${BetsService.baseUrl}`, options)
      .pipe(switchMap(res => {
        let countMin : number = page * BetsService.itemsOnPage;
        let countMax : number = (page + 1) * BetsService.itemsOnPage;
        return of(res.slice(countMin, countMax))
      }));
  }

  getBetsByMatchId(matchId: number) {
    let options = {
      params: new HttpParams().set('matchId', matchId),
      headers: this.createAuthHeader()
    };
    return this.http.get<Bet[]>(`${BetsService.baseUrl}`, options);
  }

  getBet(id: number) {
    let options = {
      headers: this.createAuthHeader()
    };
    return this.http.get<Bet>(`${BetsService.baseUrl}/${id}`, options);
  }

  private createAuthHeader(): HttpHeaders {
    return new HttpHeaders().set('Authorization', TOKEN_HEADER_PREFIX + Cookie.get(TOKEN));
  }
}
