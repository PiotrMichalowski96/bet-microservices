import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Match} from "../model/match";

@Injectable({
  providedIn: 'root'
})
export class MatchesService {

  private static readonly baseUrl: string = 'http://localhost:8082/matches';

  constructor(private http: HttpClient) { }

  getMatches() {
    let params = new HttpParams().set('order', 'match-time');
    return this.http.get<Match[]>(`${MatchesService.baseUrl}`, {params});
  }
}
