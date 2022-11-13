import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from "@angular/common/http";
import {Cookie} from "ng2-cookies";
import {ACCESS_TOKEN, TOKEN_HEADER_PREFIX} from "../util/token-properties";
import {catchError, Observable, of, switchMap, throwError} from "rxjs";
import {Bet} from "../model/bet";
import {AppConfig} from "../config/app.config";

@Injectable({
  providedIn: 'root'
})
export class BetsService {

  private static readonly betEndpoint: string = '/bets';
  private static readonly itemsOnPage: number = 20;

  private readonly betUri: string;

  constructor(private http: HttpClient, private appConfig: AppConfig) {
    this.betUri = this.appConfig.getConfig()?.backendBaseUrl + BetsService.betEndpoint;
  }

  getBets(page: number = 0): Observable<Bet[]> {
    let options = {
      headers: this.createAuthHeader()
    };
    return this.http.get<Bet[]>(this.betUri, options)
    .pipe(switchMap(res => {
      let countMin: number = page * BetsService.itemsOnPage;
      let countMax: number = (page + 1) * BetsService.itemsOnPage;
      return of(res.slice(countMin, countMax))
    }));
  }

  getBetsByMatchId(matchId: number): Observable<Bet[]> {
    let options = {
      params: new HttpParams().set('matchId', matchId),
      headers: this.createAuthHeader()
    };
    return this.http.get<Bet[]>(this.betUri, options);
  }

  getMyOwnBets(): Observable<Bet[]> {
    let options = {
      headers: this.createAuthHeader()
    };
    return this.http.get<Bet[]>(`${this.betUri}/my-own`, options);
  }

  getBet(id: string): Observable<Bet> {
    let options = {
      headers: this.createAuthHeader()
    };
    return this.http.get<Bet>(`${this.betUri}/${id}`, options);
  }

  postBet(bet: Bet): Observable<Bet> {
    let options = {
      headers: this.createAuthHeader()
    };
    return this.http.post<Bet>(this.betUri, bet, options).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    if (error.status === 0) {
      console.error('An error occurred:', error.error);
    } else {
      console.error(`Backend returned code ${error.status}, body was: `, error.error);
    }
    return throwError(() => new Error('Something bad happened; please try again later.'));
  }

  private createAuthHeader(): HttpHeaders {
    return new HttpHeaders().set('Authorization', TOKEN_HEADER_PREFIX + Cookie.get(ACCESS_TOKEN));
  }
}
