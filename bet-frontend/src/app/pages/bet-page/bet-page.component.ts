import {Component, OnInit} from '@angular/core';
import {Bet, BetResult} from "../../model/bet";
import {ActivatedRoute} from "@angular/router";
import {BetsService} from "../../services/bets.service";
import {first} from "rxjs";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatchesService} from "../../services/matches.service";
import {Match, MatchResult} from "../../model/match";
import {UsersService} from "../../services/users.service";
import {User} from "../../model/user";
import {MatchTimeHelper} from "../../util/match-time-helper";

@Component({
  selector: 'app-bet-page',
  templateUrl: './bet-page.component.html',
  styleUrls: ['./bet-page.component.scss']
})
export class BetPageComponent implements OnInit {

  bet: Bet | null = null;

  betPredictionForm: FormGroup = this.formBuilder.group({
    homeTeamGoalsPrediction: [0, [Validators.required, Validators.min(0)]],
    awayTeamGoalsPrediction: [0, [Validators.required, Validators.min(0)]]
  });

  constructor(private route: ActivatedRoute,
              private betsService: BetsService,
              private matchesService: MatchesService,
              private usersService: UsersService,
              private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.route.params.pipe(first()).subscribe(({betId}) => {
      if (betId != undefined) {
        this.getBetAndUpdateForm(betId);
      }
    });

    this.route.queryParams.pipe(first()).subscribe(({matchId}) => {
      if (matchId != undefined) {
        this.createBetAndUpdateForm(matchId);
      }
    });
  }

  private getBetAndUpdateForm(id: string): void {
    this.betsService.getBet(id).subscribe(bet => {
      this.bet = bet;
      this.updateBetPredictionForm();
    });
  }

  onSubmit(): void {
    if (this.bet == null) {
      return;
    }
    this.bet.matchPredictedResult.homeTeamGoals = this.betPredictionForm.get('homeTeamGoalsPrediction')?.value;
    this.bet.matchPredictedResult.awayTeamGoals = this.betPredictionForm.get('awayTeamGoalsPrediction')?.value;
    this.betsService.postBet(this.bet).subscribe();
  }

  updateBetPredictionForm(): void {
    this.betPredictionForm.setValue({
      homeTeamGoalsPrediction: this.bet?.matchPredictedResult?.homeTeamGoals,
      awayTeamGoalsPrediction: this.bet?.matchPredictedResult?.awayTeamGoals
    });
  }

  isMatchStarted(): boolean {
    if (this.bet == null) {
      return true;
    }
    return MatchTimeHelper.isMatchStarted(this.bet.match);
  }

  private createBetAndUpdateForm(matchId: number): void {
    this.matchesService.getMatch(matchId).subscribe(match => {
      this.usersService.getCurrentUser().subscribe(user => {
        this.bet = this.createInitialBet(match, user);
      });
    });
  }

  private createInitialBet(match: Match, user: User): Bet {
    let predictedResult: MatchResult = new MatchResult(0, 0);
    let betResult: BetResult = new BetResult('UNRESOLVED', 0);
    return new Bet('', predictedResult, match, user, betResult);
  }
}
