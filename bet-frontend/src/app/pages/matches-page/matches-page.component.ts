import { Component, OnInit } from '@angular/core';
import {Match} from "../../model/match";
import {MatchesService} from "../../services/matches.service";

@Component({
  selector: 'app-matches-page',
  templateUrl: './matches-page.component.html',
  styleUrls: ['./matches-page.component.css']
})
export class MatchesPageComponent implements OnInit {

  matches: Match[] = [];

  constructor(private matchesService: MatchesService) { }

  ngOnInit(): void {
    this.getPagedMatches();
  }

  private getPagedMatches(page: number = 0) {
    this.matchesService.getMatches(page).subscribe(matches => {
      this.matches = matches;
    });
  }

  paginate(event: any) {
    this.getPagedMatches(event.page);
  }
}
