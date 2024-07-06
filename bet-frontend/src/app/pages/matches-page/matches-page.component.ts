import {Component, OnInit} from '@angular/core';
import {Match} from "../../model/match";
import {MatchesService} from "../../services/matches.service";

@Component({
  selector: 'app-matches-page',
  templateUrl: './matches-page.component.html',
  styleUrls: ['./matches-page.component.css']
})
export class MatchesPageComponent implements OnInit {

  matches: Match[] = [];
  totalMatches: number = 0;

  constructor(private matchesService: MatchesService) {
  }

  ngOnInit(): void {
    this.getPagedMatches();
    this.getTotalMatches();
  }

  private getTotalMatches(): void {
    this.matchesService.getNumberOfMatches().subscribe(matchesNumber => {
      this.totalMatches = matchesNumber;
    });
  }

  private getPagedMatches(page: number = 0): void {
    this.matchesService.getMatches(page).subscribe(matches => {
      this.matches = matches;
    });
  }

  paginate(event: any): void {
    this.getPagedMatches(event.page);
  }

  protected readonly MatchesService = MatchesService;
}
