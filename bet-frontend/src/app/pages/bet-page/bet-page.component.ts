import { Component, OnInit } from '@angular/core';
import {Bet} from "../../model/bet";
import {ActivatedRoute} from "@angular/router";
import {BetsService} from "../../services/bets.service";
import {first} from "rxjs";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-bet-page',
  templateUrl: './bet-page.component.html',
  styleUrls: ['./bet-page.component.scss']
})
export class BetPageComponent implements OnInit {

  bet: Bet | null = null;

  betPredictionForm : FormGroup = this.formBuilder.group({
    homeTeamGoalsPrediction: 0,
    awayTeamGoalsPrediction: 0
  });

  constructor(private route: ActivatedRoute,
              private betsService: BetsService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.route.params.pipe(first()).subscribe(({id}) => {
      this.getBetAndUpdateForm(id);
    });
  }

  private getBetAndUpdateForm(id: string) {
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

  updateBetPredictionForm() {
    this.betPredictionForm.setValue({
      homeTeamGoalsPrediction: this.bet?.matchPredictedResult?.homeTeamGoals,
      awayTeamGoalsPrediction: this.bet?.matchPredictedResult?.awayTeamGoals
    });
  }
}
