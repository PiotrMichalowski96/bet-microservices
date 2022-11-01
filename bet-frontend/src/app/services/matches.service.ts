import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Match} from "../model/match";
import {Observable, of, switchMap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MatchesService {

  private static readonly baseUrl: string = 'http://localhost:8082/matches';
  private static readonly itemsOnPage: number = 20;

  constructor(private http: HttpClient) {
  }

  getMatches(page: number = 0): Observable<Match[]> {
    let params = new HttpParams().set('order', 'match-time');
    return this.http.get<Match[]>(`${MatchesService.baseUrl}`, {params})
    .pipe(switchMap(
      this.mapResponseToPage(page)
    ));
  }

  getUpcomingMatches(page: number = 0): Observable<Match[]> {
    return this.http.get<Match[]>(`${MatchesService.baseUrl}/upcoming`)
    .pipe(switchMap(
      this.mapResponseToPage(page)
    ));
  }

  getMatch(id: number): Observable<Match> {
    return this.http.get<Match>(`${MatchesService.baseUrl}/${id}`);
  }

  private mapResponseToPage(page: number) {
    return (res: Match[]) => {
      let countMin: number = page * MatchesService.itemsOnPage;
      let countMax: number = (page + 1) * MatchesService.itemsOnPage;
      return of(res.slice(countMin, countMax))
    };
  }
}
