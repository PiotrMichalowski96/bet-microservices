import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Match} from "../model/match";
import {map, Observable, of, switchMap} from "rxjs";
import {AppConfig} from "../config/app.config";

@Injectable({
  providedIn: 'root'
})
export class MatchesService {

  private static readonly matchEndpoint: string = '/matches';
  static readonly itemsOnPage: number = 10;

  private readonly matchUri: string;

  constructor(private http: HttpClient, private appConfig: AppConfig) {
    this.matchUri = this.appConfig.getConfig()?.backendBaseUrl + MatchesService.matchEndpoint;
  }

  getNumberOfMatches(): Observable<number> {
    let params = new HttpParams().set('order', 'match-time');
    return this.http.get<Match[]>(this.matchUri, {params})
    .pipe(
      map(matches => matches.length)
    );
  }

  getMatches(page: number = 0): Observable<Match[]> {
    let params = new HttpParams().set('order', 'match-time');
    return this.http.get<Match[]>(this.matchUri, {params})
    .pipe(switchMap(
      this.mapResponseToPage(page)
    ));
  }

  getOngoingMatches(page: number = 0): Observable<Match[]> {
    let params = new HttpParams().set('order', 'match-time');
    return this.http.get<Match[]>(this.matchUri, {params})
    .pipe(
      map(matches => matches.filter(match => this.isOngoingMatch(match))),
      switchMap(this.mapResponseToPage(page))
    );
  }

  private isOngoingMatch(match: Match): boolean {
    return new Date(match.startTime) < new Date() && match.result === null;
  }

  getUpcomingMatches(page: number = 0): Observable<Match[]> {
    return this.http.get<Match[]>(`${this.matchUri}/upcoming`)
    .pipe(switchMap(
      this.mapResponseToPage(page)
    ));
  }

  getMatch(id: number): Observable<Match> {
    return this.http.get<Match>(`${this.matchUri}/${id}`);
  }

  private mapResponseToPage(page: number) {
    return (res: Match[]) => {
      let countMin: number = page * MatchesService.itemsOnPage;
      let countMax: number = (page + 1) * MatchesService.itemsOnPage;
      return of(res.slice(countMin, countMax))
    };
  }
}
