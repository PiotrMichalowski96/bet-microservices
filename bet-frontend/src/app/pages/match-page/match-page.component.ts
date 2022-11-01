import {Component, OnInit} from '@angular/core';
import {Match} from "../../model/match";
import {MatchesService} from "../../services/matches.service";
import {ActivatedRoute} from "@angular/router";
import {first} from "rxjs";
import {Bet} from "../../model/bet";
import {BetsService} from "../../services/bets.service";
import {MatchTimeHelper} from "../../util/match-time-helper";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-match-page',
  templateUrl: './match-page.component.html',
  styleUrls: ['./match-page.component.scss']
})
export class MatchPageComponent implements OnInit {

  isLoggedIn: boolean = false;
  match: Match | null = null;
  bets: Bet[] = [];

  constructor(private route: ActivatedRoute,
              private matchesService: MatchesService,
              private betsService: BetsService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.hasCredentials();
    this.route.params.pipe(first()).subscribe(({id}) => {
      this.getMatch(id);
      this.getBets(id);
    });
  }

  private getMatch(id: number): void {
    this.matchesService.getMatch(id).subscribe(match => {
      this.match = match;
    });
  }

  private getBets(matchId: number): void {
    this.betsService.getBetsByMatchId(matchId).subscribe(bets => {
      this.bets = bets;
    });
  }

  canCreateBet(): boolean {
    return !MatchTimeHelper.isMatchStarted(this.match);
  }
}
