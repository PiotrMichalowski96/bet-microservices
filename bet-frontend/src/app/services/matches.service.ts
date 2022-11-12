import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Match} from "../model/match";
import {Observable, of, switchMap} from "rxjs";
import {AppConfig} from "../config/app.config";

@Injectable({
  providedIn: 'root'
})
export class MatchesService {

  private static readonly matchEndpoint: string = '/matches';
  private static readonly itemsOnPage: number = 20;

  private readonly matchUri: string;

  constructor(private http: HttpClient, private appConfig: AppConfig) {
    this.matchUri = this.appConfig.getConfig()?.backendBaseUrl + MatchesService.matchEndpoint;
  }

  getMatches(page: number = 0): Observable<Match[]> {
    let params = new HttpParams().set('order', 'match-time');
    return this.http.get<Match[]>(this.matchUri, {params})
    .pipe(switchMap(
      this.mapResponseToPage(page)
    ));
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
